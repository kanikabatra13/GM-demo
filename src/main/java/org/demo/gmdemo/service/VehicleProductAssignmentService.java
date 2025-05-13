package org.demo.gmdemo.service;

import lombok.RequiredArgsConstructor;
import org.demo.gmdemo.dto.OrgProductSubscription;
import org.demo.gmdemo.dto.ProductStatus;
import org.demo.gmdemo.dto.Vehicle;
import org.demo.gmdemo.dto.VehicleProductAssignment;
import org.demo.gmdemo.repo.OrgProductSubscriptionRepository;
import org.demo.gmdemo.repo.VehicleProductAssignmentRepository;
import org.demo.gmdemo.repo.VehicleRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleProductAssignmentService {

    private final VehicleProductAssignmentRepository vehicleAssignmentRepo;
    private final OrgProductSubscriptionRepository orgSubscriptionRepo;
    private final VehicleRepository vehicleRepository;

    public VehicleProductAssignment assign(String orgId, String vehicleId, String orgSubId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid vehicle"));

        OrgProductSubscription subscription = orgSubscriptionRepo.findById(orgSubId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid org product subscription"));

        if (!subscription.getOrganizationId().equals(orgId)) {
            throw new IllegalStateException("Subscription does not belong to this org");
        }

        if (vehicleAssignmentRepo.existsByVehicleIdAndOrgProductSubscriptionIdAndStatus(vehicleId, orgSubId, ProductStatus.ACTIVE)) {
            throw new IllegalStateException("Vehicle already has this product assigned");
        }

        Instant now = Instant.now();

        VehicleProductAssignment assignment = VehicleProductAssignment.builder()
                .organizationId(orgId)
                .vehicleId(vehicleId)
                .orgProductSubscriptionId(orgSubId)
                .activatedOn(now)
                .expiresOn(subscription.getExpiresOn())
                .status(ProductStatus.ACTIVE)
                .build();

        return vehicleAssignmentRepo.save(assignment);
    }

    public List<VehicleProductAssignment> getActiveSubscriptions(String vehicleId) {
        return vehicleAssignmentRepo.findByVehicleIdAndStatus(vehicleId, ProductStatus.ACTIVE);
    }

    public List<String> getVehiclesWithProduct(String organizationId, String productId) {
        // 1. Get active org subscriptions for the given product
        Optional<OrgProductSubscription> orgSubs = orgSubscriptionRepo
                .findByOrganizationIdAndProductIdAndStatus(organizationId, productId, ProductStatus.ACTIVE);

        Set<String> validSubIds = orgSubs.stream()
                .map(OrgProductSubscription::getId)
                .collect(Collectors.toSet());

        if (validSubIds.isEmpty()) return List.of();

        // 2. Get active vehicle assignments and filter by subscription IDs
        List<VehicleProductAssignment> activeAssignments = vehicleAssignmentRepo
                .findByOrganizationIdAndStatus(organizationId, ProductStatus.ACTIVE);

        return activeAssignments.stream()
                .filter(a -> validSubIds.contains(a.getOrgProductSubscriptionId()))
                .map(VehicleProductAssignment::getVehicleId)
                .distinct()
                .toList();
    }

    public void unsubscribeVehicleFromProduct(String vehicleId, String subscriptionId) {
        VehicleProductAssignment assignment = vehicleAssignmentRepo
                .findByVehicleIdAndOrgProductSubscriptionIdAndStatus(vehicleId, subscriptionId, ProductStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("Active assignment not found"));

        assignment.setStatus(ProductStatus.CANCELLED);
        assignment.setExpiresOn(Instant.now());

        vehicleAssignmentRepo.save(assignment);
    }


}

