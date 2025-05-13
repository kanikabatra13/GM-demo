package org.demo.gmdemo.repo;

import org.demo.gmdemo.dto.ProductStatus;
import org.demo.gmdemo.dto.Vehicle;
import org.demo.gmdemo.dto.VehicleProductAssignment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends MongoRepository<Vehicle, String> {
    List<Vehicle> findByOrganizationId(String organizationId);
    Vehicle findByVin(String vin);

}

