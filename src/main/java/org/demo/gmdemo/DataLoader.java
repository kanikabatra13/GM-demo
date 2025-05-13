package org.demo.gmdemo;

import lombok.RequiredArgsConstructor;
import org.demo.gmdemo.dto.*;
import org.demo.gmdemo.repo.*;
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
    private final OrgProductSubscriptionRepository orgSubscriptionRepository;
    private final VehicleProductAssignmentRepository vehicleAssignmentRepository;

    @Override
    public void run(String... args) throws Exception {
        // Cleanup
        vehicleAssignmentRepository.deleteAll();
        orgSubscriptionRepository.deleteAll();
        productDefinitionRepository.deleteAll();
        vehicleRepository.deleteAll();
        organizationRepository.deleteAll();

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
        ProductDefinition p1 = ProductDefinition.builder()
                .id("prod001")
                .name("OnStar Safety")
                .description("Connected safety plan")
                .type(ProductType.RENEWABLE)
                .termInDays(365)
                .price(99.99)
                .billingCycle(BillingCycle.YEARLY)
                .build();

        ProductDefinition p2 = ProductDefinition.builder()
                .id("prod002")
                .name("Fleet Maintenance")
                .description("1-year maintenance plan")
                .type(ProductType.TERMED)
                .termInDays(365)
                .price(49.99)
                .billingCycle(BillingCycle.YEARLY)
                .build();

        ProductDefinition p3 = ProductDefinition.builder()
                .id("prod003")
                .name("Setup Fee")
                .description("One-time activation")
                .type(ProductType.ONE_TIME)
                .price(149.0)
                .billingCycle(BillingCycle.NONE)
                .build();

        productDefinitionRepository.saveAll(List.of(p1, p2, p3));

        // Create OrgProductSubscription (org acquires OnStar)
        Instant now = Instant.now();
        OrgProductSubscription orgSub = OrgProductSubscription.builder()
                .id("sub001")
                .organizationId("org001")
                .productId("prod001")
                .type(ProductType.RENEWABLE)
                .subscribedOn(now)
                .expiresOn(now.plusSeconds(365 * 86400L))
                .status(ProductStatus.ACTIVE)
                .build();
        orgSubscriptionRepository.save(orgSub);

        // Create VehicleProductAssignments
        VehicleProductAssignment a1 = VehicleProductAssignment.builder()
                .id("assign001")
                .organizationId("org001")
                .vehicleId("veh001")
                .orgProductSubscriptionId("sub001")
                .activatedOn(now)
                .expiresOn(orgSub.getExpiresOn())
                .status(ProductStatus.ACTIVE)
                .build();

        VehicleProductAssignment a2 = VehicleProductAssignment.builder()
                .id("assign002")
                .organizationId("org001")
                .vehicleId("veh002")
                .orgProductSubscriptionId("sub001")
                .activatedOn(now)
                .expiresOn(orgSub.getExpiresOn())
                .status(ProductStatus.ACTIVE)
                .build();

        vehicleAssignmentRepository.saveAll(List.of(a1, a2));

        System.out.println("✅ Sample SaaS-style data loaded successfully.");

        /*
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

        productAssignmentRepository.saveAll(List.of(pa1, pa2, pa3)); */

        System.out.println("✅ Sample data loaded successfully.");
    }
}
