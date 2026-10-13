package com.movieplatform;

import java.sql.Connection;
import java.sql.Statement;

public class DropTables {
    public static void dropTables(Connection connection) {
        try (Statement stmt = connection.createStatement()) {
            // Drop relationship tables first to avoid foreign key constraints
            String[] relationshipTables = {
                "CategorizedAs",
                "DirectedBy",
                "Involves",
                "Makes",
                "Subscribes"
            };

            // Drop normalized tables
            String[] normalizedTables = {
                "UserLocation",
                "Discount",
                "BillingCore"
            };

            // Drop base tables
            String[] baseTables = {
                "Review",
                "Transactions",
                "Genre",
                "Director",
                "Movies",
                "Billing",
                "Users"
            };

            String[] views = {
                "SubscribedUsersPlans",
                "HighRatedMovies",
                "MovieReviews",
                "UserTransactions"
            };

            // Drop relationship tables
            for (String table : relationshipTables) {
                try {
                    stmt.execute("DROP TABLE " + table + " CASCADE CONSTRAINTS");
                    System.out.println("Table " + table + " dropped successfully.");
                } catch (Exception e) {
                    System.out.println("Note: " + table + " might not exist.");
                }
            }

            // Drop normalized tables
            for (String table : normalizedTables) {
                try {
                    stmt.execute("DROP TABLE " + table + " CASCADE CONSTRAINTS");
                    System.out.println("Table " + table + " dropped successfully.");
                } catch (Exception e) {
                    System.out.println("Note: " + table + " might not exist.");
                }
            }

            // Drop base tables
            for (String table : baseTables) {
                try {
                    stmt.execute("DROP TABLE " + table + " CASCADE CONSTRAINTS");
                    System.out.println("Table " + table + " dropped successfully.");
                } catch (Exception e) {
                    System.out.println("Note: " + table + " might not exist.");
                }
            }

            // Drop views
            for (String view : views) {
                try {
                    stmt.execute("DROP VIEW " + view + " CASCADE CONSTRAINTS");
                    System.out.println("View " + view + " dropped successfully.");
                } catch (Exception e) {
                    System.out.println("Note: " + view + " might not exist.");
                }
            }

            System.out.println("All tables dropped successfully.");
        } catch (Exception e) {
            System.err.println("Error dropping tables: " + e.getMessage());
        }
    }
}
