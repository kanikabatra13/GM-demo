package org.demo.gmdemo.dto;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "product_assignments")
@Data

public class ProductAssignment {

    @Id
    private String id;

    private String productId;

    private String organizationId;

    private List<String> vehicleIds;

    private ProductStatus status;

    private Instant activationDate;

    private Instant expiryDate;

    private Instant lastRenewedDate;

    // Constructors, Getters, Setters
}

