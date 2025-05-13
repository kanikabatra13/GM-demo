package org.demo.gmdemo.controller;

import lombok.RequiredArgsConstructor;
import org.demo.gmdemo.dto.*;
import org.demo.gmdemo.service.ProductAssignmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.demo.gmdemo.model.ProductAssignmentRequest;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class DemoController {

    private final ProductAssignmentService productAssignmentService;

    @PostMapping("/assign")
    public ResponseEntity<List<ProductAssignment>> assignProduct(
            @RequestBody ProductAssignmentRequest request) {

        List<ProductAssignment> assignments = productAssignmentService.assignProductToVehicles(
                request.getProductId(),
                request.getOrganizationId(),
                request.getVehicleIds()
        );

        return ResponseEntity.ok(assignments);
    }

    @PostMapping("/{assignmentId}/renew")
    public ResponseEntity<ProductAssignment> renew(@PathVariable String assignmentId) {
        return ResponseEntity.ok(productAssignmentService.renewSubscription(assignmentId));
    }

    @PostMapping("/{assignmentId}/cancel")
    public ResponseEntity<ProductAssignment> cancel(@PathVariable String assignmentId) {
        return ResponseEntity.ok(productAssignmentService.cancelSubscription(assignmentId));
    }

    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<List<ProductAssignment>> getActiveProducts(@PathVariable String vehicleId) {
        return ResponseEntity.ok(productAssignmentService.getActiveProductsForVehicle(vehicleId));
    }

    @GetMapping("/organization/summary")
    public ResponseEntity<List<ProductSubscriptionSummary>> getOrgProductSummary(
            @RequestParam("orgId") String orgId) {
        System.out.println("inside org summary");
        return ResponseEntity.ok(productAssignmentService.getProductSummaryForOrganization(orgId));
    }
}

