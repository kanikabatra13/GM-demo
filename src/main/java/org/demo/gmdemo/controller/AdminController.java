package org.demo.gmdemo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.demo.gmdemo.dto.Organization;
import org.demo.gmdemo.dto.ProductDefinition;
import org.demo.gmdemo.dto.Vehicle;
import org.demo.gmdemo.repo.OrganizationRepository;
import org.demo.gmdemo.repo.ProductDefinitionRepository;
import org.demo.gmdemo.repo.VehicleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "Admin APIs", description = "Endpoints to add organizations, vehicles, and products")
public class AdminController {

    private final OrganizationRepository organizationRepository;
    private final VehicleRepository vehicleRepository;
    private final ProductDefinitionRepository productRepository;

    @PostMapping("/organization")
    @Operation(summary = "Add a new organization")
    @ApiResponse(responseCode = "200", description = "Organization created")
    public ResponseEntity<Organization> addOrganization(@RequestBody Organization organization) {
        return ResponseEntity.ok(organizationRepository.save(organization));
    }

    @PostMapping("/vehicle")
    @Operation(summary = "Add a new vehicle")
    public ResponseEntity<Vehicle> addVehicle(@RequestBody Vehicle vehicle) {
        return ResponseEntity.ok(vehicleRepository.save(vehicle));
    }

    @PostMapping("/product")
    @Operation(summary = "Add a new product")
    public ResponseEntity<ProductDefinition> addProduct(@RequestBody ProductDefinition product) {
        return ResponseEntity.ok(productRepository.save(product));
    }
}
