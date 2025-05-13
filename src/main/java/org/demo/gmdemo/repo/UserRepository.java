package org.demo.gmdemo.repo;

import org.demo.gmdemo.dto.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {}

