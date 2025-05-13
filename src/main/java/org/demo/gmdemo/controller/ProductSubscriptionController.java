package org.demo.gmdemo.controller;

import lombok.RequiredArgsConstructor;
import org.demo.gmdemo.dto.OrgProductSubscription;
import org.demo.gmdemo.dto.OrgProductSummaryDTO;
import org.demo.gmdemo.dto.ProductStatus;
import org.demo.gmdemo.dto.VehicleProductAssignment;
import org.demo.gmdemo.model.OrgProductSubscriptionRequest;
import org.demo.gmdemo.model.VehicleProductAssignmentRequest;
import org.demo.gmdemo.service.OrgProductSubscriptionService;
import org.demo.gmdemo.service.VehicleProductAssignmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
public class ProductSubscriptionController {

    private final OrgProductSubscriptionService orgService;
    private final VehicleProductAssignmentService vehicleService;

    @PostMapping("/org")
    public ResponseEntity<OrgProductSubscription> subscribeToProduct(@RequestBody OrgProductSubscriptionRequest request) {
        return ResponseEntity.ok(orgService.subscribe(request.getOrganizationId(), request.getProductId()));
    }

    @PostMapping("/vehicle")
    public ResponseEntity<VehicleProductAssignment> assignProductToVehicle(@RequestBody VehicleProductAssignmentRequest request) {
        return ResponseEntity.ok(vehicleService.assign(request.getOrganizationId(), request.getVehicleId(), request.getOrgProductSubscriptionId()));
    }

    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<List<VehicleProductAssignment>> getActiveForVehicle(@PathVariable String vehicleId) {
        return ResponseEntity.ok(vehicleService.getActiveSubscriptions(vehicleId));
    }


    @GetMapping("/organization/summary")
    public ResponseEntity<List<OrgProductSummaryDTO>> getOrgSummary(
            @RequestParam String orgId,
            @RequestParam(defaultValue = "ACTIVE") ProductStatus subscriptionStatus,
            @RequestParam(required = false) List<ProductStatus> assignmentStatuses // comma-separated list
    ) {
        return ResponseEntity.ok(
                orgService.getProductUsageSummary(orgId, subscriptionStatus, assignmentStatuses)
        );
    }


}
