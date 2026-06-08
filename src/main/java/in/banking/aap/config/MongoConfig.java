package in.banking.aap.config;

 

import org.springframework.context.annotation.Configuration;
// import org.springframework.data.mongodb.config.EnableMongoAuditing;
// import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * MongoDB configuration.
 * Enables MongoDB repositories and auditing for automatic timestamp management.
 * TEMPORARILY DISABLED FOR TESTING WITHOUT MONGODB
 */
@Configuration
// @EnableMongoRepositories(basePackages = "com.example.chat.repository")
// @EnableMongoAuditing
public class MongoConfig {
    // MongoDB auto-configuration handles connection from application.properties
    // CURRENTLY DISABLED - Re-enable when MongoDB is available
}
