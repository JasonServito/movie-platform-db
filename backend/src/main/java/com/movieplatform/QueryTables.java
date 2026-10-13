package com.movieplatform;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class QueryTables {
    public static void executeQueries(Connection connection) {
        try (Statement stmt = connection.createStatement()) {
            // Query 1: View subscribed users
            System.out.println("\nQuery 1: Subscribed Users");
            String query1 = "SELECT DISTINCT name, email FROM Users WHERE subStatus = 'SUBSCRIBED'";
            executeAndPrintQuery(stmt, query1);

            // Query 2: View all subscription transactions
            System.out.println("\nQuery 2: Subscription Transactions");
            String query2 = "SELECT transId, userId, TO_CHAR(transDate, 'YYYY-MM-DD') AS transDate, paymentAmount " +
                           "FROM Transactions WHERE transType = 'Subscription Payment'";
            executeAndPrintQuery(stmt, query2);

            // Query 3: View subscribed users and their plan details
            System.out.println("\nQuery 3: Users and Their Plan Details");
            String query3 = "SELECT U.name, U.email, B.planName, B.price " +
                           "FROM Subscribes S " +
                           "JOIN Users U ON S.userId = U.userId " +
                           "JOIN Billing B ON S.planId = B.planId";
            executeAndPrintQuery(stmt, query3);

            // Query 4: View all movies with rating > 7
            System.out.println("\nQuery 4: High Rated Movies");
            String query4 = "SELECT title, rating FROM Movies WHERE rating > 7 ORDER BY rating DESC";
            executeAndPrintQuery(stmt, query4);

            // Query 5: View billing plans with duration > 6 months
            System.out.println("\nQuery 5: Long Duration Plans");
            String query5 = "SELECT planName, price FROM Billing WHERE duration > 6";
            executeAndPrintQuery(stmt, query5);

            // Query 6: View transactions by Alice Johnson
            System.out.println("\nQuery 6: Alice Johnson's Transactions");
            String query6 = "SELECT t.transType, t.paymentAmount, TO_CHAR(t.transDate, 'YYYY-MM-DD') as transDate " +
                           "FROM Transactions t " +
                           "JOIN Users u ON t.userId = u.userId " +
                           "WHERE LOWER(u.name) = LOWER('Alice Johnson') " +
                           "ORDER BY t.transDate DESC";
            executeAndPrintQuery(stmt, query6);

            // Query 7: View movies directed by James Cameron
            System.out.println("\nQuery 7: James Cameron Movies");
            String query7 = "SELECT m.title " +
                           "FROM Movies m " +
                           "JOIN DirectedBy db ON m.movieId = db.movieId " +
                           "JOIN Director d ON db.dirId = d.dirId " +
                           "WHERE LOWER(d.name) = LOWER('James Cameron')";
            executeAndPrintQuery(stmt, query7);

            // Query 8: View Action movies
            System.out.println("\nQuery 8: Action Movies");
            String query8 = "SELECT M.title " +
                           "FROM Movies M " +
                           "JOIN CategorizedAs C ON M.movieId = C.movieId " +
                           "JOIN Genre G ON C.genreId = G.genreId " +
                           "WHERE G.genreName = 'Action'";
            executeAndPrintQuery(stmt, query8);

            // Query 9: View Inception reviews
            System.out.println("\nQuery 9: Inception Reviews");
            String query9 = "SELECT r.reviewId, u.name AS reviewer_name, r.rating, r.comments, " +
                           "TO_CHAR(r.reviewDate, 'YYYY-MM-DD') as review_date " +
                           "FROM Review r " +
                           "JOIN Users u ON r.userId = u.userId " +
                           "JOIN Movies m ON r.movieId = m.movieId " +
                           "WHERE m.title = 'Inception'";
            executeAndPrintQuery(stmt, query9);

            // Query 10: Directors and their movie count
            System.out.println("\nQuery 10: Directors Movie Count");
            String query10 = "SELECT d.name AS director_name, COUNT(m.movieId) AS number_of_movies " +
                            "FROM Director d " +
                            "JOIN DirectedBy db ON d.dirId = db.dirId " +
                            "JOIN Movies m ON db.movieId = m.movieId " +
                            "GROUP BY d.name " +
                            "ORDER BY number_of_movies DESC";
            executeAndPrintQuery(stmt, query10);

            // Query 11: Plan revenue from subscriptions
            System.out.println("\nQuery 11: Plan Revenue");
            String query11 = "SELECT b.planName, SUM(t.paymentAmount) AS total_revenue " +
                            "FROM Billing b " +
                            "JOIN Subscribes s ON b.planId = s.planId " +
                            "JOIN Transactions t ON s.userId = t.userId " +
                            "WHERE t.transType = 'Subscription Payment' " +
                            "GROUP BY b.planName " +
                            "ORDER BY total_revenue DESC";
            executeAndPrintQuery(stmt, query11);

            // Query 12: Average user ratings
            System.out.println("\nQuery 12: User Average Ratings");
            String query12 = "SELECT u.name AS reviewer_name, u.email, AVG(r.rating) AS average_rating " +
                            "FROM Users u " +
                            "JOIN Review r ON u.userId = r.userId " +
                            "GROUP BY u.name, u.email " +
                            "HAVING COUNT(r.reviewId) > 0 " +
                            "ORDER BY average_rating DESC";
            executeAndPrintQuery(stmt, query12);

            // Query 13: Movies per genre
            System.out.println("\nQuery 13: Movies per Genre");
            String query13 = "SELECT g.genreName, COUNT(c.movieId) AS number_of_movies " +
                            "FROM Genre g " +
                            "JOIN CategorizedAs c ON g.genreId = c.genreId " +
                            "GROUP BY g.genreName " +
                            "ORDER BY number_of_movies DESC";
            executeAndPrintQuery(stmt, query13);

            // Query 14: Average rating by genre
            System.out.println("\nQuery 14: Genre Average Ratings");
            String query14 = "SELECT g.genreName, AVG(m.rating) AS average_rating " +
                            "FROM Genre g " +
                            "JOIN CategorizedAs c ON g.genreId = c.genreId " +
                            "JOIN Movies m ON c.movieId = m.movieId " +
                            "GROUP BY g.genreName " +
                            "ORDER BY average_rating DESC";
            executeAndPrintQuery(stmt, query14);

            // Query 15: Directors of highly rated movies
            System.out.println("\nQuery 15: Directors of High-Rated Movies");
            String query15 = "SELECT DISTINCT d.name AS director_name " +
                            "FROM Director d " +
                            "JOIN DirectedBy db ON d.dirId = db.dirId " +
                            "JOIN Movies m ON db.movieId = m.movieId " +
                            "WHERE m.rating > 8.0 " +
                            "ORDER BY d.name";
            executeAndPrintQuery(stmt, query15);

            // Query 16: Subscribers per plan
            System.out.println("\nQuery 16: Subscribers per Plan");
            String query16 = "SELECT b.planName, COUNT(s.userId) AS number_of_subscribers " +
                            "FROM Billing b " +
                            "LEFT JOIN Subscribes s ON b.planId = s.planId " +
                            "JOIN Users u ON s.userId = u.userId " +
                            "WHERE u.subStatus = 'SUBSCRIBED' " +
                            "GROUP BY b.planName " +
                            "ORDER BY number_of_subscribers DESC";
            executeAndPrintQuery(stmt, query16);

            // Query 17: Inception reviewers
            System.out.println("\nQuery 17: Inception Reviewers");
            String query17 = "SELECT u.name, u.email " +
                            "FROM Users u " +
                            "WHERE EXISTS (" +
                            "    SELECT 1 " +
                            "    FROM Review r " +
                            "    JOIN Movies m ON r.movieId = m.movieId " +
                            "    WHERE r.userId = u.userId AND m.title = 'Inception'" +
                            ")";
            executeAndPrintQuery(stmt, query17);

            // Query 18: Nolan or Action movies
            System.out.println("\nQuery 18: Nolan or Action Movies");
            String query18 = "SELECT m.title " +
                            "FROM Movies m " +
                            "JOIN DirectedBy db ON m.movieId = db.movieId " +
                            "JOIN Director d ON db.dirId = d.dirId " +
                            "WHERE LOWER(d.name) = LOWER('Christopher Nolan') " +
                            "UNION " +
                            "SELECT m.title " +
                            "FROM Movies m " +
                            "JOIN CategorizedAs c ON m.movieId = c.movieId " +
                            "JOIN Genre g ON c.genreId = g.genreId " +
                            "WHERE LOWER(g.genreName) = LOWER('Action')";
            executeAndPrintQuery(stmt, query18);

            // Query 19: Action movies not by Nolan
            System.out.println("\nQuery 19: Action Movies Not by Nolan");
            String query19 = "SELECT m.title " +
                            "FROM Movies m " +
                            "JOIN CategorizedAs c ON m.movieId = c.movieId " +
                            "JOIN Genre g ON c.genreId = g.genreId " +
                            "WHERE LOWER(g.genreName) = LOWER('Action') " +
                            "MINUS " +
                            "SELECT m.title " +
                            "FROM Movies m " +
                            "JOIN DirectedBy db ON m.movieId = db.movieId " +
                            "JOIN Director d ON db.dirId = d.dirId " +
                            "WHERE LOWER(d.name) = LOWER('Christopher Nolan')";
            executeAndPrintQuery(stmt, query19);

            // Query 20: Directors with multiple movies
            System.out.println("\nQuery 20: Directors with Multiple Movies");
            String query20 = "SELECT d.name AS director_name, " +
                            "COUNT(m.movieId) AS number_of_movies, " +
                            "AVG(m.rating) AS average_rating " +
                            "FROM Director d " +
                            "JOIN DirectedBy db ON d.dirId = db.dirId " +
                            "JOIN Movies m ON db.movieId = m.movieId " +
                            "GROUP BY d.name " +
                            "HAVING COUNT(m.movieId) > 1 " +
                            "ORDER BY average_rating DESC";
            executeAndPrintQuery(stmt, query20);

            // Query 21: Genres with high-rated movies
            System.out.println("\nQuery 21: Genres with High-Rated Movies");
            String query21 = "SELECT g.genreName " +
                            "FROM Genre g " +
                            "JOIN CategorizedAs c ON g.genreId = c.genreId " +
                            "JOIN Movies m ON c.movieId = m.movieId " +
                            "WHERE m.rating > 8 " +
                            "GROUP BY g.genreName " +
                            "HAVING COUNT(m.movieId) > 2";
            executeAndPrintQuery(stmt, query21);

        } catch (SQLException e) {
            System.err.println("Error executing queries: " + e.getMessage());
        }
    }

    private static void executeAndPrintQuery(Statement stmt, String query) throws SQLException {
        try (ResultSet rs = stmt.executeQuery(query)) {
            int columnCount = rs.getMetaData().getColumnCount();
            
            for (int i = 1; i <= columnCount; i++) {
                System.out.printf("%-20s", rs.getMetaData().getColumnName(i));
            }
            System.out.println("\n" + "-".repeat(20 * columnCount));

            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    System.out.printf("%-20s", rs.getString(i));
                }
                System.out.println();
            }
            System.out.println();
        }
    }
}
