package org.demo.gmdemo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.demo.gmdemo.dto.*;
import org.demo.gmdemo.model.OrgOverviewResponse;
import org.demo.gmdemo.model.OrgSubscriptionSummary;
import org.demo.gmdemo.model.SubscriptionPurchaseRequest;
import org.demo.gmdemo.repo.OrgProductSubscriptionRepository;
import org.demo.gmdemo.repo.OrganizationRepository;
import org.demo.gmdemo.repo.ProductDefinitionRepository;
import org.demo.gmdemo.repo.VehicleProductAssignmentRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrgProductSubscriptionService {

    private final OrgProductSubscriptionRepository repo;
    private final ProductDefinitionRepository productRepo;
    private final VehicleProductAssignmentRepository vehicleAssignmentRepository;
    private final OrganizationRepository organizationRepository;

    public OrgProductSubscription subscribe(String orgId, String productId) {
        ProductDefinition product = productRepo.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        Instant now = Instant.now();
        Instant expiry = (product.getType() == ProductType.ONE_TIME) ? null :
                now.plusSeconds(Optional.ofNullable(product.getTermInDays()).orElse(365) * 86400L);

        OrgProductSubscription subscription = OrgProductSubscription.builder()
                .organizationId(orgId)
                .productId(productId)
                .type(product.getType())
                .subscribedOn(now)
                .expiresOn(expiry)
                .status(ProductStatus.ACTIVE)
                .build();

        log.info("Subscription {} activated successfully");


        return repo.save(subscription);
    }


    public List<OrgProductSummaryDTO> getProductUsageSummary(
            String orgId,
            ProductStatus subscriptionStatus,
            List<ProductStatus> assignmentStatuses
    ) {
        List<OrgProductSubscription> subs = repo
                .findByOrganizationIdAndStatus(orgId, subscriptionStatus);

        List<VehicleProductAssignment> assignments = vehicleAssignmentRepository.findAll();

        Set<ProductStatus> assignmentStatusSet = (assignmentStatuses == null || assignmentStatuses.isEmpty())
                ? Set.of(ProductStatus.ACTIVE)
                : new HashSet<>(assignmentStatuses);

        // Group assignments by orgProductSubscriptionId if matching status
        Map<String, Long> countBySubscriptionId = assignments.stream()
                .filter(a -> a.getOrganizationId().equals(orgId))
                .filter(a -> assignmentStatusSet.contains(a.getStatus()))
                .collect(Collectors.groupingBy(
                        VehicleProductAssignment::getOrgProductSubscriptionId,
                        Collectors.counting()
                ));

        return subs.stream()
                .map(sub -> {
                    String productId = sub.getProductId();
                    String productName = productRepo.findById(productId)
                            .map(ProductDefinition::getName)
                            .orElse("Unknown");

                    long count = countBySubscriptionId.getOrDefault(sub.getId(), 0L);
                    return new OrgProductSummaryDTO(productId, productName, count);
                })
                .collect(Collectors.toList());
    }


    public OrgProductSubscription initiateSubscriptionPurchase(SubscriptionPurchaseRequest request) {
        ProductDefinition product = productRepo.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID"));

        OrgProductSubscription subscription = OrgProductSubscription.builder()
                .organizationId(request.getOrganizationId())
                .productId(request.getProductId())
                .type(product.getType())
                .subscribedOn(Instant.now())
                .status(ProductStatus.PENDING)
                .billingCycle(product.getBillingCycle())
                .recurringCharge(product.getPrice())// status until purchase confirmed
                .build();

        long count = repo
                .findByOrganizationId(request.getOrganizationId())
                .stream()
                .map(OrgProductSubscription::getProductId)
                .distinct()
                .count();

        if (count >= 10) {
            throw new IllegalStateException("Organization has already purchased the maximum number of products (10).");
        }

        OrgProductSubscription saved = repo.save(subscription);

        // Call external purchase service (REST or event queue)
        initiatePurchaseExternally(saved, request.getCallbackUrl());

        return saved;
    }


    public void activateSubscription(String subscriptionId) {
        OrgProductSubscription sub = repo.findById(subscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid subscription ID"));

        if (sub.getStatus() != ProductStatus.PENDING) {
            throw new IllegalStateException("Only pending subscriptions can be activated");
        }

        Instant now = Instant.now();
        Instant expiry = (sub.getType() == ProductType.ONE_TIME) ? null :
                now.plusSeconds(Optional.of(365).get() * 86400L); // default term

        sub.setStatus(ProductStatus.ACTIVE);
        sub.setSubscribedOn(now);
        sub.setExpiresOn(expiry);

        repo.save(sub);
    }

    public void cancelSubscription(String subscriptionId) {
        OrgProductSubscription sub = repo.findById(subscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid subscription ID"));

        sub.setStatus(ProductStatus.CANCELLED);
        repo.save(sub);
    }


    private void initiatePurchaseExternally(OrgProductSubscription sub, String callbackUrl) {
        System.out.println("🛒 [PurchaseService Sim] Starting purchase process for subscription: " + sub.getId());

        // 1. Simulate processing delay (e.g., 1–3 seconds)
        int delayMillis = ThreadLocalRandom.current().nextInt(1000, 3000);
        try {
            Thread.sleep(delayMillis);
        } catch (InterruptedException ignored) {}

        // 2. Simulate purchase result randomly
      //
        //
        //  boolean success = ThreadLocalRandom.current().nextBoolean(); //50% chance success

        boolean success = true;

        // 3. Prepare payload
        Map<String, Object> payload = new HashMap<>();
        payload.put("subscriptionId", sub.getId());
        payload.put("success", success);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<String> response = new RestTemplate().postForEntity(callbackUrl, request, String.class);
            System.out.printf("✅ Callback sent to %s | Success=%s | Status=%s%n",
                    callbackUrl, success, response.getStatusCode());
        } catch (Exception ex) {
            System.err.printf("❌ Failed to send callback to %s: %s%n", callbackUrl, ex.getMessage());
        }
    }


    public List<OrgProductSubscription> getOrgSubscriptions(String orgId) {
        return repo.findByOrganizationId(orgId);
    }



    public OrgOverviewResponse getOrganizationOverview(String orgId) {
        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new IllegalArgumentException("Organization not found"));

        List<OrgProductSubscription> subs = repo.findByOrganizationId(orgId);
        List<VehicleProductAssignment> assignments = vehicleAssignmentRepository.findByOrganizationId(orgId);

        Map<String, ProductDefinition> productMap = productRepo.findAll().stream()
                .collect(Collectors.toMap(ProductDefinition::getId, p -> p));

        Map<String, List<VehicleProductAssignment>> groupedBySub = assignments.stream()
                .collect(Collectors.groupingBy(VehicleProductAssignment::getOrgProductSubscriptionId));

        List<OrgSubscriptionSummary> subscriptionSummaries = subs.stream()
                .map(sub -> {
                    ProductDefinition product = productMap.get(sub.getProductId());

                    Map<ProductStatus, List<String>> vehicleStatusMap = new EnumMap<>(ProductStatus.class);

                    for (ProductStatus status : ProductStatus.values()) {
                        vehicleStatusMap.put(status, new ArrayList<>());
                    }

                    List<VehicleProductAssignment> subAssignments = groupedBySub.getOrDefault(sub.getId(), List.of());

                    for (VehicleProductAssignment a : subAssignments) {
                        vehicleStatusMap.get(a.getStatus()).add(a.getVehicleId());
                    }

                    return OrgSubscriptionSummary.builder()
                            .productId(sub.getProductId())
                            .productName(product.getName())
                            .status(sub.getStatus())
                            .vehicleAssignments(vehicleStatusMap)
                            .build();
                })
                .toList();

        return OrgOverviewResponse.builder()
                .organizationId(org.getId())
                .name(org.getName())
                .subscriptions(subscriptionSummaries)
                .build();
    }


}

