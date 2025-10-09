package com.company.iesa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main application class for Invoice and Expense Segmentation App (IESA)
 *
 * This is a modular monolith architecture with clear module boundaries:
 * - User Management
 * - Segment Management
 * - Expense Management
 * - Approval Workflow
 * - Budget Management
 * - Reporting & Analytics
 * - Notification Service
 * - Integration Layer
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableCaching
public class IesaApplication {

    public static void main(String[] args) {
        SpringApplication.run(IesaApplication.class, args);
    }

}
