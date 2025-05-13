package org.demo.gmdemo.model;

import lombok.Data;

@Data
public class VehicleProductAssignmentRequest {
    private String organizationId;
    private String vehicleId;
    private String orgProductSubscriptionId;
}

