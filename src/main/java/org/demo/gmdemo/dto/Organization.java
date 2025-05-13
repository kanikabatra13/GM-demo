package org.demo.gmdemo.dto;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "organizations")
public class Organization {

    @Id
    private String id;

    private String name;

    // Constructors, Getters, Setters
}

