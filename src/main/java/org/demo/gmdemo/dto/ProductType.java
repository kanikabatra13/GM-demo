package org.demo.gmdemo.dto;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

public enum ProductType {
    ONE_TIME,
    TERMED,
    RENEWABLE
}

