package org.demo.gmdemo.controller;

import lombok.RequiredArgsConstructor;
import org.demo.gmdemo.dto.ProductAssignment;
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
    public <ProductAssignmentRequest> ResponseEntity<ProductAssignment> assignProduct(@RequestBody org.demo.gmdemo.model.ProductAssignmentRequest request) {
        ProductAssignment assignment = productAssignmentService.assignProductToVehicles(
                request.getProductId(),
                request.getOrganizationId(),
                request.getVehicleIds()
        );
        return ResponseEntity.ok(assignment);
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
}

