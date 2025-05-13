package org.demo.gmdemo.dto;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "vehicles")
public class Vehicle {

    @Id
    private String id;

    private String vin;

    private String organizationId;

    // Constructors, Getters, Setters
}
