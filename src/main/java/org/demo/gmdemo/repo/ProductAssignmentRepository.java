package org.demo.gmdemo.repo;

import org.demo.gmdemo.dto.ProductAssignment;
import org.demo.gmdemo.dto.ProductStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProductAssignmentRepository extends MongoRepository<ProductAssignment, String> {

    // Get all assignments for an organization
    List<ProductAssignment> findByOrganizationId(String organizationId);

    // Get active assignments for a vehicle
    List<ProductAssignment> findByVehicleIdAndStatus(String vehicleId, ProductStatus status);

    // Get all assignments for a vehicle (any status)
    List<ProductAssignment> findByVehicleId(String vehicleId);

    List<ProductAssignment> findByProductIdAndOrganizationIdAndStatus(
            String productId, String organizationId, ProductStatus status);

}
