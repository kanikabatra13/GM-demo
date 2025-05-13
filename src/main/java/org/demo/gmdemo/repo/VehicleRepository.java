package org.demo.gmdemo.repo;

import org.demo.gmdemo.dto.Vehicle;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface VehicleRepository extends MongoRepository<Vehicle, String> {
    List<Vehicle> findByOrganizationId(String organizationId);
    Vehicle findByVin(String vin);
}

