package org.demo.gmdemo.model;

import lombok.Data;

@Data
public class OrgProductSubscriptionRequest {
    private String organizationId;
    private String productId;
}
