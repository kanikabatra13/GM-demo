package org.demo.gmdemo.dto;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "product_definitions")
@Data
public class ProductDefinition {

    @Id
    private String id;

    private String name;

    private String description;

    private ProductType type;

    private Integer termInDays; // nullable for ONE_TIME

    // Constructors, Getters, Setters
}
