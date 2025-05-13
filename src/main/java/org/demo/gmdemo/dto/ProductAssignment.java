package org.demo.gmdemo.dto;

import com.fasterxml.jackson.core.JsonToken;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "product_assignments")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductAssignment {

    @Id
    private String id;

    private String productId;

    private String organizationId;

    private String vehicleId;

    private ProductStatus status;

    private Instant activationDate;

    private Instant expiryDate;

    private Instant lastRenewedDate;


    // Constructors, Getters, Setters
}

