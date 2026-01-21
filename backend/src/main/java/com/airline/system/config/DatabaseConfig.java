
package com.airline.system.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

@Configuration
public class DatabaseConfig {

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Bean
    public boolean createDatabase() {
        try {
            // Extract the base URL (without the database name)
            // Example:
            // jdbc:mysql://localhost:3306/airline_system?createDatabaseIfNotExist=true...
            // Extract jdbc:mysql://localhost:3306/
            String baseUrl = datasourceUrl.split("\\?")[0];
            int lastSlashIndex = baseUrl.lastIndexOf("/");
            String dbServerUrl = baseUrl.substring(0, lastSlashIndex + 1);
            String dbName = baseUrl.substring(lastSlashIndex + 1);

            // Append SSL parameters to the server URL to avoid warnings/errors
            String serverConnectionUrl = dbServerUrl + "?useSSL=false&allowPublicKeyRetrieval=true";

            System.out.println("Connecting to MySQL server to ensure database exists: " + serverConnectionUrl);

            try (Connection connection = DriverManager.getConnection(serverConnectionUrl, username, password);
                    Statement statement = connection.createStatement()) {

                String sql = "CREATE DATABASE IF NOT EXISTS " + dbName;
                statement.executeUpdate(sql);
                System.out.println("Database '" + dbName + "' checked/created successfully.");
            }
        } catch (Exception e) {
            System.err.println(
                    "Note: Automatic database creation check failed (this is normal if DB already exists or server is unreachable): "
                            + e.getMessage());
        }
        return true;
    }
}
