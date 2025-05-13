package org.demo.gmdemo.model;

import lombok.Data;

@Data
public class SubscriptionPurchaseRequest {
    private String organizationId;
    private String productId;
    private String callbackUrl; // optional: purchase service calls back when done
}

