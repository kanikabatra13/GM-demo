package org.demo.gmdemo.repo;

import org.demo.gmdemo.dto.Organization;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrganizationRepository extends MongoRepository<Organization, String> {
    Organization findByName(String name);
}

