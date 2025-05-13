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
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductAssignmentService {

    private final ProductDefinitionRepository productDefinitionRepository;
    private final ProductAssignmentRepository productAssignmentRepository;
    private final OrganizationRepository organizationRepository;
    private final VehicleRepository vehicleRepository;

    public ProductAssignment assignProductToVehicles(String productId, String organizationId, List<String> vehicleIds) {
        // Validate product
        ProductDefinition product = productDefinitionRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID"));

        // Validate organization
        if (!organizationRepository.existsById(organizationId)) {
            throw new IllegalArgumentException("Invalid organization ID");
        }

        // Validate vehicles
        List<Vehicle> vehicles = vehicleRepository.findAllById(vehicleIds);
        if (vehicles.size() != vehicleIds.size()) {
            throw new IllegalArgumentException("Some vehicle IDs are invalid");
        }

        // Build assignment
        ProductAssignment assignment = new ProductAssignment();
        assignment.setProductId(productId);
        assignment.setOrganizationId(organizationId);
        assignment.setVehicleIds(vehicleIds);
        assignment.setStatus(ProductStatus.ACTIVE);

        Instant now = Instant.now();
        assignment.setActivationDate(now);

        if (product.getType() == ProductType.ONE_TIME) {
            assignment.setExpiryDate(null);
        } else {
            int termDays = Optional.ofNullable(product.getTermInDays()).orElse(365);
            assignment.setExpiryDate(now.plusSeconds(termDays * 86400L));
        }

        return productAssignmentRepository.save(assignment);
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
        return productAssignmentRepository.findByVehicleIdsContainingAndStatus(vehicleId, ProductStatus.ACTIVE);
    }
}

