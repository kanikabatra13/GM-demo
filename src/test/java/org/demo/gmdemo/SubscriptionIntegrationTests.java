package org.demo.gmdemo;

import com.jayway.jsonpath.JsonPath;
import org.demo.gmdemo.dto.*;
import org.demo.gmdemo.repo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SubscriptionIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrgProductSubscriptionRepository subscriptionRepo;

    @Autowired
    private ProductDefinitionRepository productRepo;

    @Autowired private OrganizationRepository organizationRepository;
    @Autowired private VehicleRepository vehicleRepository;
    @Autowired private VehicleProductAssignmentRepository assignmentRepo;

    @BeforeEach
    public void setup() {
        subscriptionRepo.deleteAll();
        productRepo.deleteAll();

        // Create product used in test
        ProductDefinition product = ProductDefinition.builder()
                .id("prod001")
                .name("OnStar Safety")
                .description("Test Product")
                .type(ProductType.RENEWABLE)
                .termInDays(365)
                .build();

        productRepo.save(product);
    }

    @Test
    public void testInitiateSubscriptionPurchase() throws Exception {
        String requestBody = """
        {
            "organizationId": "org001",
            "productId": "prod001",
            "callbackUrl": "http://localhost:8080/subscriptions/callback"
        }
    """;

        mockMvc.perform(post("/subscriptions/org/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.organizationId").value("org001"))
                .andExpect(jsonPath("$.productId").value("prod001"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    public void testConfirmPurchaseSuccess() throws Exception {
        // create pending subscription manually
        OrgProductSubscription sub = subscriptionRepo.save(OrgProductSubscription.builder()
                .id("sub001")
                .organizationId("org001")
                .productId("prod001")
                .type(ProductType.RENEWABLE)
                .status(ProductStatus.PENDING)
                .build());

        String callbackPayload = """
        {
            "subscriptionId": "sub001",
            "success": true
        }
    """;

        mockMvc.perform(post("/subscriptions/callback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(callbackPayload))
                .andExpect(status().isOk())
                .andExpect(content().string("Activated"));

        OrgProductSubscription updated = subscriptionRepo.findById("sub001").orElseThrow();
        assertEquals(ProductStatus.ACTIVE, updated.getStatus());
        assertNotNull(updated.getExpiresOn());
    }


    @Test
    public void testConfirmPurchaseFailure() throws Exception {
        OrgProductSubscription sub = subscriptionRepo.save(OrgProductSubscription.builder()
                .id("sub002")
                .organizationId("org001")
                .productId("prod001")
                .type(ProductType.RENEWABLE)
                .status(ProductStatus.PENDING)
                .build());

        String callbackPayload = """
        {
            "subscriptionId": "sub002",
            "success": false
        }
    """;

        mockMvc.perform(post("/subscriptions/callback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(callbackPayload))
                .andExpect(status().isOk())
                .andExpect(content().string("Cancelled"));

        OrgProductSubscription updated = subscriptionRepo.findById("sub002").orElseThrow();
        assertEquals(ProductStatus.CANCELLED, updated.getStatus());
    }

    @Test
    public void testEndToEndSubscriptionAndAssignmentFlow() throws Exception {
        // Step 1: Create product manually
        ProductDefinition product = ProductDefinition.builder()
                .id("prod003")
                .name("OnStar Safety")
                .description("E2E test product")
                .type(ProductType.RENEWABLE)
                .termInDays(365)
                .build();
        productRepo.save(product);

        // Step 2: Create organization & vehicle manually
        String orgId = "org003";
        String vehicleId = "veh003";
        organizationRepository.save(new Organization(orgId, "GM"));
        vehicleRepository.save(new Vehicle(vehicleId, "VIN1234567892", orgId));

        // Step 3: Initiate subscription purchase
        String purchaseRequest = """
        {
            "organizationId": "org003",
            "productId": "prod003",
            "callbackUrl": "http://localhost:8080/subscriptions/callback"
        }
    """;

        MvcResult result = mockMvc.perform(post("/subscriptions/org/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(purchaseRequest))
                .andExpect(status().isOk())
                .andReturn();

        // Extract subscriptionId from response
        String json = result.getResponse().getContentAsString();
        String subscriptionId = JsonPath.read(json, "$.id");

        OrgProductSubscription sub = subscriptionRepo.findById(subscriptionId).orElseThrow();

        System.out.println("💡 Subscription status before callback: " + sub.getStatus());


        /*
        // Step 4: Simulate purchase callback (success)
        String callbackPayload = String.format("""
        {
            "subscriptionId": "%s",
            "success": true
        }
    """, subscriptionId);

        mockMvc.perform(post("/subscriptions/callback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(callbackPayload))
                .andExpect(status().isOk()); */

        // Step 5: Assign product to vehicle
        String assignRequest = String.format("""
        {
            "organizationId": "org003",
            "vehicleId": "veh003",
            "orgProductSubscriptionId": "%s"
        }
    """, subscriptionId);

        mockMvc.perform(post("/subscriptions/vehicle")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(assignRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vehicleId").value(vehicleId))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        // Step 6: Verify vehicle's active subscriptions
        mockMvc.perform(get("/subscriptions/vehicle/" + vehicleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orgProductSubscriptionId").value(subscriptionId))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));
    }


}
