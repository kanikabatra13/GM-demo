package org.demo.gmdemo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChargeRecord {
    @Id
    private String id;
    private String orgId;
    private String productId;
    private String subscriptionId;

    private Instant chargedOn;
    private Double amount;
    private BillingCycle billingCycle;
    private ChargeStatus status; // SUCCESS, FAILED, PENDING
    private String referenceId; // from Stripe/PayPal/etc.
}

