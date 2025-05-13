package org.demo.gmdemo;

import lombok.RequiredArgsConstructor;
import org.demo.gmdemo.dto.*;
import org.demo.gmdemo.repo.OrganizationRepository;
import org.demo.gmdemo.repo.ProductAssignmentRepository;
import org.demo.gmdemo.repo.ProductDefinitionRepository;
import org.demo.gmdemo.repo.VehicleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final OrganizationRepository organizationRepository;
    private final VehicleRepository vehicleRepository;
    private final ProductDefinitionRepository productDefinitionRepository;
    private final ProductAssignmentRepository productAssignmentRepository;

    @Override
    public void run(String... args) throws Exception {
        // Clear existing data for fresh run
        organizationRepository.deleteAll();
        vehicleRepository.deleteAll();
        productDefinitionRepository.deleteAll();
        productAssignmentRepository.deleteAll();

        // Create Organizations
        Organization org1 = new Organization();
        org1.setId("org001");
        org1.setName("General Motors");

        Organization org2 = new Organization();
        org2.setId("org002");
        org2.setName("Acme Fleet Solutions");

        organizationRepository.saveAll(List.of(org1, org2));

        // Create Vehicles
        Vehicle v1 = new Vehicle("veh001", "VIN1234567890", "org001");
        Vehicle v2 = new Vehicle("veh002", "VIN0987654321", "org001");
        Vehicle v3 = new Vehicle("veh003", "VIN1122334455", "org002");

        vehicleRepository.saveAll(List.of(v1, v2, v3));

        // Create Products
        ProductDefinition p1 = new ProductDefinition();
        p1.setId("prod001");
        p1.setName("OnStar Safety");
        p1.setDescription("Emergency assistance and crash response");
        p1.setType(ProductType.RENEWABLE);
        p1.setTermInDays(365);

        ProductDefinition p2 = new ProductDefinition();
        p2.setId("prod002");
        p2.setName("Fleet Maintenance");
        p2.setDescription("1-year vehicle maintenance subscription");
        p2.setType(ProductType.TERMED);
        p2.setTermInDays(365);

        ProductDefinition p3 = new ProductDefinition();
        p3.setId("prod003");
        p3.setName("Initial Setup Fee");
        p3.setDescription("One-time vehicle setup");
        p3.setType(ProductType.ONE_TIME);

        productDefinitionRepository.saveAll(List.of(p1, p2, p3));

        // Create product assignments
        Instant now = Instant.now();

        ProductAssignment pa1 = ProductAssignment.builder()
                .id("assign001")
                .productId("prod001")
                .organizationId("org001")
                .vehicleId("veh001")
                .activationDate(now)
                .expiryDate(now.plusSeconds(365L * 86400))
                .status(ProductStatus.ACTIVE)
                .build();

        ProductAssignment pa2 = ProductAssignment.builder()
                .id("assign002")
                .productId("prod002")
                .organizationId("org001")
                .vehicleId("veh002")
                .activationDate(now)
                .expiryDate(now.plusSeconds(365L * 86400))
                .status(ProductStatus.ACTIVE)
                .build();

        ProductAssignment pa3 = ProductAssignment.builder()
                .id("assign003")
                .productId("prod003")
                .organizationId("org001")
                .vehicleId("veh001")
                .activationDate(now)
                .status(ProductStatus.ACTIVE)
                .build();

        ProductAssignment pa4 = ProductAssignment.builder()
                .id("assign003")
                .productId("prod003")
                .organizationId("org001")
                .vehicleId("veh002")
                .activationDate(now)
                .status(ProductStatus.ACTIVE)
                .build();

        productAssignmentRepository.saveAll(List.of(pa1, pa2, pa3));

        System.out.println("✅ Sample data loaded successfully.");
    }
}
