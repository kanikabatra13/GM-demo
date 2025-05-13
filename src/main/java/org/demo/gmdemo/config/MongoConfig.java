package org.demo.gmdemo.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

public class MongoConfig extends AbstractMongoClientConfiguration {

    @Override
    protected String getDatabaseName() {
        return "testdb"; // default DB
    }

    @Bean
    @Override
    public MongoClient mongoClient() {
        String connectionString = "mongodb://localhost:27017/testdb";
        return MongoClients.create(connectionString);
    }

    // Optional: Enable MongoDB auto-index creation
    @Override
    protected boolean autoIndexCreation() {
        return true;
    }


}
