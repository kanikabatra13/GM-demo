package org.demo.gmdemo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "vehicles")
@Data
@AllArgsConstructor
public class Vehicle {

    @Id
    private String id;

    private String vin;

    private String organizationId;

    // Constructors, Getters, Setters
}
