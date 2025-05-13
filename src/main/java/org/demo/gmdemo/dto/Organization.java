package org.demo.gmdemo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "organizations")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Organization {

    @Id
    private String id;

    private String name;

    // Constructors, Getters, Setters
}

