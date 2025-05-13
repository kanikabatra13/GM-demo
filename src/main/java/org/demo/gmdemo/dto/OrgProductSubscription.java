package org.demo.gmdemo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "org_product_subscriptions")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrgProductSubscription {

    @Id
    private String id;

    private String organizationId;
    private String productId;

    private ProductType type;
    private Instant subscribedOn;
    private Instant expiresOn;

    private ProductStatus status; // ACTIVE, CANCELLED, EXPIRED

    private Double recurringCharge; // captured from product at time of subscription
    private BillingCycle billingCycle;

    private List<ChargeRecord> charges;
}

