package com.movieplatform;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;

public class CreateTables {
    public static void createBaseTables(Connection connection) {
        try (Statement stmt = connection.createStatement()) {
            // Create Users table
            String usersTable = "CREATE TABLE Users (" +
                "userId INT PRIMARY KEY," +
                "name VARCHAR(255) NOT NULL," +
                "email VARCHAR(255) NOT NULL UNIQUE," +
                "password VARCHAR(255) NOT NULL," +
                "subStatus VARCHAR(50) DEFAULT 'NOT SUBSCRIBED'," +
                "paymentInfo VARCHAR(255)" +
            ")";
            stmt.execute(usersTable);

            // Create Movies table
            String moviesTable = "CREATE TABLE Movies (" +
                "movieId INT PRIMARY KEY," +
                "title VARCHAR(255) NOT NULL," +
                "releaseDate DATE NOT NULL," +
                "duration INT NOT NULL," +
                "synopsis VARCHAR(1000)," +
                "rating DECIMAL(3, 1) CHECK (rating BETWEEN 0 AND 10)" +
            ")";
            stmt.execute(moviesTable);

            // Create Review table
            String reviewTable = "CREATE TABLE Review (" +
                "reviewId INT PRIMARY KEY," +
                "movieId INT," +
                "userId INT," +
                "rating DECIMAL(3, 1) CHECK (rating BETWEEN 0 AND 10)," +
                "reviewDate DATE NOT NULL," +
                "comments VARCHAR(1000)," +
                "FOREIGN KEY (userId) REFERENCES Users(userId) ON DELETE CASCADE," +
                "FOREIGN KEY (movieId) REFERENCES Movies(movieId) ON DELETE CASCADE" +
            ")";
            stmt.execute(reviewTable);

            // Create Billing table
            String billingTable = "CREATE TABLE Billing (" +
                "planId INT PRIMARY KEY," +
                "planName VARCHAR(255) NOT NULL," +
                "duration INT NOT NULL," +
                "price DECIMAL(10, 2) NOT NULL" +
            ")";
            stmt.execute(billingTable);

            // Create Transaction table
            String transactionTable = "CREATE TABLE Transactions (" +
                "transId INT PRIMARY KEY," +
                "userId INT," +
                "transType VARCHAR(255) NOT NULL," +
                "transDate DATE NOT NULL," +
                "paymentAmount DECIMAL(10, 2) NOT NULL," +
                "FOREIGN KEY (userId) REFERENCES Users(userId) ON DELETE CASCADE" +
            ")";
            stmt.execute(transactionTable);

            // Create Genre table
            String genreTable = "CREATE TABLE Genre (" +
                "genreId INT PRIMARY KEY," +
                "genreName VARCHAR(255) NOT NULL," +
                "description VARCHAR(1000)" +
            ")";
            stmt.execute(genreTable);

            // Create Director table
            String directorTable = "CREATE TABLE Director (" +
                "dirId INT PRIMARY KEY," +
                "name VARCHAR(255) NOT NULL," +
                "nationality VARCHAR(100)," +
                "bio VARCHAR(1000)" +
            ")";
            stmt.execute(directorTable);

            // Create Relationship Tables
            // Subscribes table
            String subscribesTable = "CREATE TABLE Subscribes (" +
                "userId INT," +
                "planId INT," +
                "PRIMARY KEY (userId, planId)," +
                "FOREIGN KEY (userId) REFERENCES Users(userId) ON DELETE CASCADE," +
                "FOREIGN KEY (planId) REFERENCES Billing(planId) ON DELETE CASCADE" +
            ")";
            stmt.execute(subscribesTable);

            // Makes table
            String makesTable = "CREATE TABLE Makes (" +
                "userId INT," +
                "reviewId INT," +
                "PRIMARY KEY (userId, reviewId)," +
                "FOREIGN KEY (userId) REFERENCES Users(userId) ON DELETE CASCADE," +
                "FOREIGN KEY (reviewId) REFERENCES Review(reviewId) ON DELETE CASCADE" +
            ")";
            stmt.execute(makesTable);

            // Involves table
            String involvesTable = "CREATE TABLE Involves (" +
                "movieId INT," +
                "genreId INT," +
                "PRIMARY KEY (movieId, genreId)," +
                "FOREIGN KEY (movieId) REFERENCES Movies(movieId) ON DELETE CASCADE," +
                "FOREIGN KEY (genreId) REFERENCES Genre(genreId) ON DELETE CASCADE" +
            ")";
            stmt.execute(involvesTable);

            // DirectedBy table
            String directedByTable = "CREATE TABLE DirectedBy (" +
                "movieId INT," +
                "dirId INT," +
                "PRIMARY KEY (movieId, dirId)," +
                "FOREIGN KEY (movieId) REFERENCES Movies(movieId) ON DELETE CASCADE," +
                "FOREIGN KEY (dirId) REFERENCES Director(dirId) ON DELETE CASCADE" +
            ")";
            stmt.execute(directedByTable);

            // CategorizedAs table
            String categorizedAsTable = "CREATE TABLE CategorizedAs (" +
                "movieId INT," +
                "genreId INT," +
                "PRIMARY KEY (movieId, genreId)," +
                "FOREIGN KEY (movieId) REFERENCES Movies(movieId) ON DELETE CASCADE," +
                "FOREIGN KEY (genreId) REFERENCES Genre(genreId) ON DELETE CASCADE" +
            ")";
            stmt.execute(categorizedAsTable);

            System.out.println("Base tables created successfully.");
        } catch (Exception e) {
            System.err.println("Error creating base tables: " + e.getMessage());
        }
    }

    public static void createNormalizedTables(Connection connection) {
        try (Statement stmt = connection.createStatement()) {
            // Create BillingCore table
            String billingCoreTable = "CREATE TABLE BillingCore (" +
                "planId INT PRIMARY KEY," +
                "planName VARCHAR(255) NOT NULL," +
                "duration INT NOT NULL," +
                "price DECIMAL(10, 2) NOT NULL" +
            ")";
            stmt.execute(billingCoreTable);

            // Create Discount table
            String discountTable = "CREATE TABLE Discount (" +
                "duration INT PRIMARY KEY," +
                "discountRate DECIMAL(5, 2)" +
            ")";
            stmt.execute(discountTable);

            // Create UserLocation table
            String userLocationTable = "CREATE TABLE UserLocation (" +
                "userId INT PRIMARY KEY," +
                "userLocation VARCHAR(255)," +
                "FOREIGN KEY (userId) REFERENCES Users(userId) ON DELETE CASCADE" +
            ")";
            stmt.execute(userLocationTable);

            // Drop existing Review table and create updated one
            try {
                stmt.execute("DROP TABLE Review CASCADE CONSTRAINTS");
            } catch (SQLException e) {
            }

            // Create updated Review table
            String updatedReviewTable = "CREATE TABLE Review (" +
                "reviewId INT PRIMARY KEY," +
                "movieId INT," +
                "userId INT," +
                "rating DECIMAL(3, 1) CHECK (rating BETWEEN 0 AND 10)," +
                "reviewDate DATE NOT NULL," +
                "comments VARCHAR(1000)," +
                "FOREIGN KEY (userId) REFERENCES Users(userId) ON DELETE CASCADE," +
                "FOREIGN KEY (movieId) REFERENCES Movies(movieId) ON DELETE CASCADE" +
            ")";
            stmt.execute(updatedReviewTable);

            System.out.println("Normalized tables created successfully.");
        } catch (Exception e) {
            System.err.println("Error creating normalized tables: " + e.getMessage());
        }
    }

    public static void createViews(Connection connection) {
        try (Statement stmt = connection.createStatement()) {
            // Create SubscribedUsersPlans view
            String subscribedUsersPlansView = "CREATE OR REPLACE VIEW SubscribedUsersPlans AS " +
                "SELECT U.name, U.email, B.planName, B.price " +
                "FROM Users U " +
                "JOIN Subscribes S ON U.userId = S.userId " +
                "JOIN Billing B ON S.planId = B.planId " +
                "WHERE U.subStatus = 'SUBSCRIBED'";
            stmt.execute(subscribedUsersPlansView);

            // Create HighRatedMovies view
            String highRatedMoviesView = "CREATE OR REPLACE VIEW HighRatedMovies AS " +
                "SELECT title, rating " +
                "FROM Movies " +
                "WHERE rating > 7";
            stmt.execute(highRatedMoviesView);

            // Create MovieReviews view
            String movieReviewsView = "CREATE OR REPLACE VIEW MovieReviews AS " +
                "SELECT M.title, U.name AS reviewer_name, R.rating, R.comments, " +
                "R.reviewDate " +
                "FROM Review R " +
                "JOIN Users U ON R.userId = U.userId " +
                "JOIN Movies M ON R.movieId = M.movieId";
            stmt.execute(movieReviewsView);

            // Create UserTransactions view
            String userTransactionsView = "CREATE OR REPLACE VIEW UserTransactions AS " +
                "SELECT U.name, U.email, T.transType, T.transDate, T.paymentAmount " +
                "FROM Users U " +
                "JOIN Transactions T ON U.userId = T.userId";
            stmt.execute(userTransactionsView);

            System.out.println("Views created successfully.");
        } catch (Exception e) {
            System.err.println("Error creating views: " + e.getMessage());
        }
    }
}
