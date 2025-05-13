package org.demo.gmdemo.repo;

import org.demo.gmdemo.dto.ProductDefinition;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductDefinitionRepository extends MongoRepository<ProductDefinition, String> {
    ProductDefinition findByName(String name);
}

