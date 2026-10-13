package com.movieplatform;

import java.sql.Connection;
import java.sql.Statement;

public class PopulateTables {
    public static void populateBaseTables(Connection connection) {
        try (Statement stmt = connection.createStatement()) {
            // Populate Users table
            String insertUsers = "INSERT INTO Users (userId, name, email, password, subStatus, paymentInfo) " +
                "VALUES " +
                "(1, 'Alice Johnson', 'alice.johnson@gmail.com', 'securepassword', 'SUBSCRIBED', 'Credit Card'), " +
                "(2, 'John Doe', 'john.doe@example.com', 'password123', 'SUBSCRIBED', 'PayPal'), " +
                "(3, 'Emma Brown', 'emma.brown@example.com', 'mypassword', 'NOT SUBSCRIBED', NULL), " +
                "(4, 'Liam Smith', 'liam.smith@example.com', 'pass123', 'SUBSCRIBED', 'Debit Card')";
            stmt.execute(insertUsers);

            // Populate Movies table
            String insertMovies = "INSERT INTO Movies (movieId, title, releaseDate, duration, synopsis, rating) " +
                "VALUES " +
                "(1, 'Inception', TO_DATE('2010-07-16', 'YYYY-MM-DD'), 148, 'A mind-bending thriller.', 9.0), " +
                "(2, 'Avatar', TO_DATE('2009-12-18', 'YYYY-MM-DD'), 162, 'A Marine protects Pandora.', 8.7), " +
                "(3, 'Interstellar', TO_DATE('2014-11-07', 'YYYY-MM-DD'), 169, 'Explorers travel through a wormhole.', 8.6), " +
                "(4, 'The Dark Knight', TO_DATE('2008-07-18', 'YYYY-MM-DD'), 152, 'A superhero crime thriller.', 9.1), " +
                "(5, 'Mad Max: Fury Road', TO_DATE('2015-05-15', 'YYYY-MM-DD'), 120, 'Post-apocalyptic action.', 8.1)";
            stmt.execute(insertMovies);

            // Populate Billing table
            String insertBilling = "INSERT INTO Billing (planId, planName, duration, price) " +
                "VALUES " +
                "(1, 'Basic Plan', 1, 9.99), " +
                "(2, 'Standard Plan', 6, 49.99), " +
                "(3, 'Premium Plan', 12, 99.99)";
            stmt.execute(insertBilling);

            // Populate Genre table
            String insertGenres = "INSERT INTO Genre (genreId, genreName, description) " +
                "VALUES " +
                "(1, 'Action', 'Movies with a lot of physical stunts and activity.'), " +
                "(2, 'Sci-Fi', 'Explores futuristic concepts and technology.'), " +
                "(3, 'Drama', 'Serious narratives with character focus.')";
            stmt.execute(insertGenres);

            // Populate Director table
            String insertDirectors = "INSERT INTO Director (dirId, name, nationality, bio) " +
                "VALUES " +
                "(1, 'Christopher Nolan', 'British-American', 'Known for mind-bending thrillers.'), " +
                "(2, 'James Cameron', 'Canadian', 'Visionary filmmaker and innovator.'), " +
                "(3, 'George Miller', 'Australian', 'Creator of Mad Max series.')";
            stmt.execute(insertDirectors);

            // Populate Review table
            String insertReviews = "INSERT INTO Review (reviewId, movieId, userId, rating, reviewDate, comments) " +
                "VALUES " +
                "(1, 1, 1, 9.5, TO_DATE('2023-09-01', 'YYYY-MM-DD'), 'Amazing visuals and story.'), " +
                "(2, 2, 2, 8.2, TO_DATE('2023-09-05', 'YYYY-MM-DD'), 'Loved the action.'), " +
                "(3, 3, 1, 9.0, TO_DATE('2023-09-10', 'YYYY-MM-DD'), 'Masterpiece of science fiction.'), " +
                "(4, 4, 4, 8.8, TO_DATE('2023-09-20', 'YYYY-MM-DD'), 'Brilliant crime thriller.')";
            stmt.execute(insertReviews);

            // Add DirectedBy relationships
            String insertDirectedBy = "INSERT INTO DirectedBy (movieId, dirId) VALUES " +
                "(1, 1), " +  // Inception - Nolan
                "(3, 1), " +  // Interstellar - Nolan
                "(4, 1), " +  // The Dark Knight - Nolan
                "(2, 2), " +  // Avatar - Cameron
                "(5, 3)";     // Mad Max - Miller
            stmt.execute(insertDirectedBy);

            // Add CategorizedAs relationships
            String insertCategorizedAs = "INSERT INTO CategorizedAs (movieId, genreId) VALUES " +
                "(1, 2), " +  // Inception - Sci-Fi
                "(2, 1), " +  // Avatar - Action
                "(2, 2), " +  // Avatar - Sci-Fi
                "(3, 2), " +  // Interstellar - Sci-Fi
                "(4, 1), " +  // The Dark Knight - Action
                "(5, 1)";     // Mad Max - Action
            stmt.execute(insertCategorizedAs);

            // Add Subscribes relationships
            String insertSubscribes = "INSERT INTO Subscribes (userId, planId) VALUES " +
                "(1, 3), " +  // Alice - Premium
                "(2, 2), " +  // John - Standard
                "(4, 1)";     // Liam - Basic
            stmt.execute(insertSubscribes);

            // Add Makes relationships (Users making Reviews)
            String insertMakes = "INSERT INTO Makes (userId, reviewId) VALUES " +
                "(1, 1), " +  // Alice reviews Inception
                "(2, 2), " +  // John reviews Avatar
                "(1, 3), " +  // Alice reviews Interstellar
                "(4, 4)";     // Liam reviews Dark Knight
            stmt.execute(insertMakes);

            // Add Transactions
            String insertTransactions = "INSERT INTO Transactions (transId, userId, transType, transDate, paymentAmount) VALUES " +
                "(1, 1, 'Subscription Payment', TO_DATE('2023-01-01', 'YYYY-MM-DD'), 99.99), " +
                "(2, 2, 'Subscription Payment', TO_DATE('2023-02-01', 'YYYY-MM-DD'), 49.99), " +
                "(3, 4, 'Subscription Payment', TO_DATE('2023-03-01', 'YYYY-MM-DD'), 9.99), " +
                "(4, 1, 'Movie Purchase', TO_DATE('2023-04-01', 'YYYY-MM-DD'), 14.99)";
            stmt.execute(insertTransactions);

            System.out.println("Base tables populated successfully.");
        } catch (Exception e) {
            System.err.println("Error populating base tables: " + e.getMessage());
        }
    }

    public static void populateNormalizedTables(Connection connection) {
        try (Statement stmt = connection.createStatement()) {
            // Populate BillingCore table
            String insertBillingCore = "INSERT INTO BillingCore (planId, planName, duration, price) " +
                "VALUES " +
                "(1, 'Basic Plan', 1, 9.99), " +
                "(2, 'Standard Plan', 6, 49.99), " +
                "(3, 'Premium Plan', 12, 99.99), " +
                "(4, 'Family Plan', 12, 89.99), " +
                "(5, 'Student Plan', 6, 29.99)";
            stmt.execute(insertBillingCore);

            // Populate Discount table
            String insertDiscount = "INSERT INTO Discount (duration, discountRate) " +
                "VALUES " +
                "(1, 0.00), " +
                "(6, 10.00), " +
                "(12, 20.00)";
            stmt.execute(insertDiscount);

            // Populate UserLocation table
            String insertUserLocations = "INSERT INTO UserLocation (userId, userLocation) " +
                "VALUES " +
                "(1, 'Toronto'), " +
                "(2, 'Vancouver'), " +
                "(3, 'Montreal'), " +
                "(4, 'Calgary')";
            stmt.execute(insertUserLocations);

            // Add Involves relationships (Movies involving Genres)
            String insertInvolves = "INSERT INTO Involves (movieId, genreId) VALUES " +
                "(1, 1), " +  // Inception - Action
                "(1, 2), " +  // Inception - Sci-Fi
                "(2, 1), " +  // Avatar - Action
                "(2, 2), " +  // Avatar - Sci-Fi
                "(3, 2), " +  // Interstellar - Sci-Fi
                "(3, 3), " +  // Interstellar - Drama
                "(4, 1), " +  // Dark Knight - Action
                "(4, 3), " +  // Dark Knight - Drama
                "(5, 1)";     // Mad Max - Action
            stmt.execute(insertInvolves);

            System.out.println("Normalized tables populated successfully.");
        } catch (Exception e) {
            System.err.println("Error populating normalized tables: " + e.getMessage());
        }
    }
}
