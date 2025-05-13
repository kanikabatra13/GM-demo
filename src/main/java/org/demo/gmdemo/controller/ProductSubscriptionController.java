package org.demo.gmdemo.controller;

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
public class ProductSubscriptionController {

    private final OrgProductSubscriptionService orgProductSubscriptionService;
    private final VehicleProductAssignmentService vehicleProductAssignmentService;

    @PostMapping("/org")
    public ResponseEntity<OrgProductSubscription> subscribeToProduct(@RequestBody OrgProductSubscriptionRequest request) {
        return ResponseEntity.ok(orgProductSubscriptionService.subscribe(request.getOrganizationId(), request.getProductId()));
    }

    @PostMapping("/vehicle")
    public ResponseEntity<VehicleProductAssignment> assignProductToVehicle(@RequestBody VehicleProductAssignmentRequest request) {
        return ResponseEntity.ok(vehicleProductAssignmentService.assign(request.getOrganizationId(), request.getVehicleId(), request.getOrgProductSubscriptionId()));
    }

    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<List<VehicleProductAssignment>> getActiveForVehicle(@PathVariable String vehicleId) {
        return ResponseEntity.ok(vehicleProductAssignmentService.getActiveSubscriptions(vehicleId));
    }


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


    @PostMapping("/org/purchase")
    public ResponseEntity<OrgProductSubscription> initiateProductPurchase(
            @RequestBody SubscriptionPurchaseRequest request
    ) {
        OrgProductSubscription subscription = orgProductSubscriptionService.initiateSubscriptionPurchase(request);
        return ResponseEntity.ok(subscription);
    }


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
    public ResponseEntity<List<OrgProductSubscription>> getOrgSubscriptions(@PathVariable String orgId) {
        return ResponseEntity.ok(orgProductSubscriptionService.getOrgSubscriptions(orgId));
    }


    @PostMapping("/vehicle/unsubscribe")
    public ResponseEntity<String> unsubscribeVehicle(@RequestBody VehicleUnsubscribeRequest request) {
        vehicleProductAssignmentService.unsubscribeVehicleFromProduct(
                request.getVehicleId(),
                request.getOrgProductSubscriptionId()
        );
        return ResponseEntity.ok("Vehicle unsubscribed successfully.");
    }


    @GetMapping("/{orgId}/overview")
    public ResponseEntity<OrgOverviewResponse> getOrgOverview(@PathVariable String orgId) {
        return ResponseEntity.ok(orgProductSubscriptionService.getOrganizationOverview(orgId));
    }

}
