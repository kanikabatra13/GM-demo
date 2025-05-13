package org.demo.gmdemo.model;

import lombok.Data;

@Data
public class VehicleUnsubscribeRequest {
    private String vehicleId;
    private String orgProductSubscriptionId;
}
