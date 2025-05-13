package org.demo.gmdemo.repo;

import org.demo.gmdemo.dto.ProductStatus;
import org.demo.gmdemo.dto.VehicleProductAssignment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface VehicleProductAssignmentRepository extends MongoRepository<VehicleProductAssignment, String> {
    List<VehicleProductAssignment> findByVehicleIdAndStatus(String vehicleId, ProductStatus status);
    boolean existsByVehicleIdAndOrgProductSubscriptionIdAndStatus(String vehicleId, String orgSubId, ProductStatus status);

    List<VehicleProductAssignment> findByOrgProductSubscriptionIdAndStatus(String subId, ProductStatus status);

}
