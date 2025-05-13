package org.demo.gmdemo.dto;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "organizations")
@Data
public class Organization {

    @Id
    private String id;

    private String name;

    // Constructors, Getters, Setters
}

