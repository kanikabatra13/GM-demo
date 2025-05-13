package org.demo.gmdemo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "vehicle_product_assignments")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VehicleProductAssignment {

    @Id
    private String id;

    private String organizationId;
    private String vehicleId;

    private String orgProductSubscriptionId;

    private Instant activatedOn;
    private Instant expiresOn;

    private ProductStatus status;
}

