package org.demo.gmdemo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.demo.gmdemo.dto.OrgProductSubscription;
import org.demo.gmdemo.dto.OrgProductSummaryDTO;
import org.demo.gmdemo.dto.ProductStatus;
import org.demo.gmdemo.dto.VehicleProductAssignment;
import org.demo.gmdemo.model.*;
import org.demo.gmdemo.service.OrgProductSubscriptionService;
import org.demo.gmdemo.service.VehicleProductAssignmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
@Tag(name = "Subscription APIs", description = "Manage product subscriptions, vehicle assignments, and renewals")
public class ProductSubscriptionController {

    private final OrgProductSubscriptionService orgProductSubscriptionService;
    private final VehicleProductAssignmentService vehicleProductAssignmentService;

    @Operation(summary = "Assign a product subscription to a organization")
    @PostMapping("/org")
    public ResponseEntity<OrgProductSubscription> subscribeToProduct(@RequestBody OrgProductSubscriptionRequest request) {
        return ResponseEntity.ok(orgProductSubscriptionService.subscribe(request.getOrganizationId(), request.getProductId()));
    }

    @Operation(summary = "Assign a product subscription to a vehicle")
    @ApiResponse(responseCode = "200", description = "Vehicle-product assignment created")
    @PostMapping("/vehicle")
    public ResponseEntity<VehicleProductAssignment> assignProductToVehicle(@RequestBody VehicleProductAssignmentRequest request) {
        return ResponseEntity.ok(vehicleProductAssignmentService.assign(request.getOrganizationId(), request.getVehicleId(), request.getOrgProductSubscriptionId()));
    }

    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<List<VehicleProductAssignment>> getActiveForVehicle(@PathVariable String vehicleId) {
        return ResponseEntity.ok(vehicleProductAssignmentService.getActiveSubscriptions(vehicleId));
    }


    @Operation(summary = "Get subscription summary by product for an organization")
    @GetMapping("/organization/summary")
    public ResponseEntity<List<OrgProductSummaryDTO>> getOrgSummary(
            @RequestParam String orgId,
            @RequestParam(defaultValue = "ACTIVE") ProductStatus subscriptionStatus,
            @RequestParam(required = false) List<ProductStatus> assignmentStatuses // comma-separated list
    ) {
        return ResponseEntity.ok(
                orgProductSubscriptionService.getProductUsageSummary(orgId, subscriptionStatus, assignmentStatuses)
        );
    }


    @Operation(summary = "Initiate product subscription purchase")
    @ApiResponse(responseCode = "200", description = "Subscription created in PENDING state")
    @PostMapping("/org/purchase")
    public ResponseEntity<OrgProductSubscription> initiateProductPurchase(
            @RequestBody SubscriptionPurchaseRequest request
    ) {
        OrgProductSubscription subscription = orgProductSubscriptionService.initiateSubscriptionPurchase(request);
        return ResponseEntity.ok(subscription);
    }


    @Operation(summary = "Handle callback from external purchase service")
    @ApiResponse(responseCode = "200", description = "Subscription activated or cancelled")
    @PostMapping("/callback")
    public ResponseEntity<String> confirmPurchasePost(@RequestBody Map<String, Object> payload) {
        String subscriptionId = (String) payload.get("subscriptionId");
        boolean success = Boolean.TRUE.equals(payload.get("success"));

        if (success) {
            orgProductSubscriptionService.activateSubscription(subscriptionId);
            return ResponseEntity.ok("Activated");
        } else {
            orgProductSubscriptionService.cancelSubscription(subscriptionId);
            return ResponseEntity.ok("Cancelled");
        }
    }


    @GetMapping("/product/{productId}/vehicles")
    public ResponseEntity<List<String>> getVehiclesWithProduct(
            @PathVariable String productId,
            @RequestParam String orgId
    ) {
        return ResponseEntity.ok(vehicleProductAssignmentService.getVehiclesWithProduct(orgId, productId));
    }

    @GetMapping("/org/{orgId}")
    @Operation(summary = "List subscriptions for an organization")
    public ResponseEntity<List<OrgProductSubscription>> getOrgSubscriptions(@PathVariable String orgId) {
        return ResponseEntity.ok(orgProductSubscriptionService.getOrgSubscriptions(orgId));
    }


    @Operation(summary = "Unsubscribe a vehicle from a product")
    @PostMapping("/vehicle/unsubscribe")
    public ResponseEntity<String> unsubscribeVehicle(@RequestBody VehicleUnsubscribeRequest request) {
        vehicleProductAssignmentService.unsubscribeVehicleFromProduct(
                request.getVehicleId(),
                request.getOrgProductSubscriptionId()
        );
        return ResponseEntity.ok("Vehicle unsubscribed successfully.");
    }


    @Operation(summary = "Get Product subscription vehicle breakdown by organization")
    @GetMapping("/{orgId}/overview")
    public ResponseEntity<OrgOverviewResponse> getOrgOverview(@PathVariable String orgId) {
        return ResponseEntity.ok(orgProductSubscriptionService.getOrganizationOverview(orgId));
    }

}
