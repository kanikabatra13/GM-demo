package org.demo.gmdemo.dto;

import lombok.Data;

@Data
public class ProductSubscriptionSummary {
    private String productId;
    private String productName;
    private long vehicleCount;

    public ProductSubscriptionSummary(String productId, String productName, long vehicleCount) {
        this.productId = productId;
        this.productName = productName;
        this.vehicleCount = vehicleCount;
    }


    // Getters and Setters (or use Lombok @Data)
}

