package org.demo.gmdemo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "product_definitions")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDefinition {

    @Id
    private String id;

    private String name;

    private String description;

    private ProductType type;

    private Integer termInDays; // nullable for ONE_TIME

    // Constructors, Getters, Setters
}
