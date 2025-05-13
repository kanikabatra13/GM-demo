package org.demo.gmdemo.service;

import lombok.RequiredArgsConstructor;
import org.demo.gmdemo.dto.*;
import org.demo.gmdemo.repo.OrganizationRepository;
import org.demo.gmdemo.repo.ProductAssignmentRepository;
import org.demo.gmdemo.repo.ProductDefinitionRepository;
import org.demo.gmdemo.repo.VehicleRepository;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductAssignmentService {

    private final ProductDefinitionRepository productDefinitionRepository;
    private final ProductAssignmentRepository productAssignmentRepository;
    private final OrganizationRepository organizationRepository;
    private final VehicleRepository vehicleRepository;

    public List<ProductAssignment> assignProductToVehicles(String productId, String organizationId, List<String> vehicleIds) {
        ProductDefinition product = productDefinitionRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID"));

        if (!organizationRepository.existsById(organizationId)) {
            throw new IllegalArgumentException("Invalid organization ID");
        }

        List<Vehicle> vehicles = vehicleRepository.findAllById(vehicleIds);
        if (vehicles.size() != vehicleIds.size()) {
            throw new IllegalArgumentException("Some vehicle IDs are invalid");
        }

        // Get existing active assignments for this org + product
        List<ProductAssignment> existingAssignments =
                productAssignmentRepository.findByProductIdAndOrganizationIdAndStatus(
                        productId, organizationId, ProductStatus.ACTIVE
                );

        Set<String> alreadyAssignedVehicleIds = existingAssignments.stream()
                .map(ProductAssignment::getVehicleId)
                .collect(Collectors.toSet());

        // Filter out vehicles that already have active subscriptions
        List<String> newVehicleIds = vehicleIds.stream()
                .filter(id -> !alreadyAssignedVehicleIds.contains(id))
                .toList();

        if (newVehicleIds.isEmpty()) {
            throw new IllegalStateException("All vehicles already have active assignments for this product.");
        }

        Instant now = Instant.now();
        int termDays = Optional.ofNullable(product.getTermInDays()).orElse(365);

        List<ProductAssignment> newAssignments = newVehicleIds.stream()
                .map(vehicleId -> ProductAssignment.builder()
                        .productId(productId)
                        .organizationId(organizationId)
                        .vehicleId(vehicleId)
                        .status(ProductStatus.ACTIVE)
                        .activationDate(now)
                        .expiryDate(product.getType() == ProductType.ONE_TIME ? null : now.plusSeconds(termDays * 86400L))
                        .build())
                .toList();

        return productAssignmentRepository.saveAll(newAssignments);
    }



    public ProductAssignment renewSubscription(String assignmentId) {
        ProductAssignment assignment = productAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));

        ProductDefinition product = productDefinitionRepository.findById(assignment.getProductId())
                .orElseThrow(() -> new IllegalStateException("Product not found"));

        if (product.getType() != ProductType.RENEWABLE) {
            throw new UnsupportedOperationException("Only renewable products can be renewed");
        }

        Instant newExpiry = assignment.getExpiryDate().plusSeconds(product.getTermInDays() * 86400L);
        assignment.setLastRenewedDate(Instant.now());
        assignment.setExpiryDate(newExpiry);
        assignment.setStatus(ProductStatus.ACTIVE);

        return productAssignmentRepository.save(assignment);
    }

    public ProductAssignment cancelSubscription(String assignmentId) {
        ProductAssignment assignment = productAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));

        assignment.setStatus(ProductStatus.CANCELLED);
        return productAssignmentRepository.save(assignment);
    }

    public List<ProductAssignment> getActiveProductsForVehicle(String vehicleId) {
        return productAssignmentRepository.findByVehicleIdAndStatus(vehicleId, ProductStatus.ACTIVE);
    }



    public List<ProductSubscriptionSummary> getProductSummaryForOrganization(String organizationId) {
        List<ProductAssignment> assignments = productAssignmentRepository.findByOrganizationId(organizationId);

        // Group by productId and count
        Map<String, Long> productCountMap = assignments.stream()
                .collect(Collectors.groupingBy(ProductAssignment::getProductId, Collectors.counting()));

        // Join with product names
        return productCountMap.entrySet().stream()
                .map(entry -> {
                    String productId = entry.getKey();
                    long vehicleCount = entry.getValue();

                    ProductDefinition product = productDefinitionRepository.findById(productId)
                            .orElseThrow(() -> new IllegalStateException("Product not found: " + productId));

                    return new ProductSubscriptionSummary(productId, product.getName(), vehicleCount);
                })
                .collect(Collectors.toList());
    }

}

