package org.demo.gmdemo.service;

import lombok.RequiredArgsConstructor;
import org.demo.gmdemo.dto.*;
import org.demo.gmdemo.repo.OrgProductSubscriptionRepository;
import org.demo.gmdemo.repo.ProductDefinitionRepository;
import org.demo.gmdemo.repo.VehicleProductAssignmentRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrgProductSubscriptionService {

    private final OrgProductSubscriptionRepository repo;
    private final ProductDefinitionRepository productRepo;
    private final VehicleProductAssignmentRepository vehicleAssignmentRepository;

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



}

