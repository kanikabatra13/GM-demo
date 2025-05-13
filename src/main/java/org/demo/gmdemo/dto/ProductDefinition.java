package org.demo.gmdemo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "product_definitions")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDefinition {

    @Id
    private String id;

    private String name;

    private String description;

    private ProductType type;

    private Integer termInDays; // nullable for ONE_TIME

    private Double price; // e.g., 49.99

    private BillingCycle billingCycle;// enum: MONTHLY, YEARLY, NONE

    private Boolean autoChargeEnabled;


    // Constructors, Getters, Setters
}
