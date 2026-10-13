package com.movieplatform.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.movieplatform.*;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.sql.PreparedStatement;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class DatabaseController {
    
    @Autowired
    private DatabaseConnection dbConnection;

    @PostMapping("/createTables")
    public String createTables() {
        try (Connection connection = dbConnection.getConnection()) {
            CreateTables.createBaseTables(connection);
            CreateTables.createNormalizedTables(connection);
            CreateTables.createViews(connection);
            return "Tables created successfully";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @PostMapping("/populateTables")
    public String populateTables() {
        try (Connection connection = dbConnection.getConnection()) {
            PopulateTables.populateBaseTables(connection);
            PopulateTables.populateNormalizedTables(connection);
            return "Tables populated successfully";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @GetMapping("/queryTables")
    public Map<String, List<Map<String, Object>>> queryTables() {
        Map<String, List<Map<String, Object>>> allResults = new HashMap<>();
        
        try (Connection connection = dbConnection.getConnection()) {
            String[] queries = {
                "SELECT DISTINCT name, email FROM Users WHERE subStatus = 'SUBSCRIBED'",
                "SELECT transId, userId, TO_CHAR(transDate, 'YYYY-MM-DD') AS transDate, paymentAmount FROM Transactions WHERE transType = 'Subscription Payment'",
                "SELECT U.name, U.email, B.planName, B.price FROM Subscribes S JOIN Users U ON S.userId = U.userId JOIN Billing B ON S.planId = B.planId",
                "SELECT title, rating FROM Movies WHERE rating > 7 ORDER BY rating DESC",
                "SELECT planName, price FROM Billing WHERE duration > 6",
                "SELECT t.transType, t.paymentAmount, TO_CHAR(t.transDate, 'YYYY-MM-DD') as transDate FROM Transactions t JOIN Users u ON t.userId = u.userId WHERE LOWER(u.name) = LOWER('Alice Johnson') ORDER BY t.transDate DESC",
                "SELECT m.title FROM Movies m JOIN DirectedBy db ON m.movieId = db.movieId JOIN Director d ON db.dirId = d.dirId WHERE LOWER(d.name) = LOWER('James Cameron')",
                "SELECT M.title FROM Movies M JOIN CategorizedAs C ON M.movieId = C.movieId JOIN Genre G ON C.genreId = G.genreId WHERE G.genreName = 'Action'",
                "SELECT r.reviewId, u.name AS reviewer_name, r.rating, r.comments, TO_CHAR(r.reviewDate, 'YYYY-MM-DD') as review_date FROM Review r JOIN Users u ON r.userId = u.userId JOIN Movies m ON r.movieId = m.movieId WHERE m.title = 'Inception'",
                "SELECT d.name AS director_name, COUNT(m.movieId) AS number_of_movies FROM Director d JOIN DirectedBy db ON d.dirId = db.dirId JOIN Movies m ON db.movieId = m.movieId GROUP BY d.name ORDER BY number_of_movies DESC",
                "SELECT b.planName, SUM(t.paymentAmount) AS total_revenue FROM Billing b JOIN Subscribes s ON b.planId = s.planId JOIN Transactions t ON s.userId = t.userId WHERE t.transType = 'Subscription Payment' GROUP BY b.planName ORDER BY total_revenue DESC",
                "SELECT u.name AS reviewer_name, u.email, AVG(r.rating) AS average_rating FROM Users u JOIN Review r ON u.userId = r.userId GROUP BY u.name, u.email HAVING COUNT(r.reviewId) > 0 ORDER BY average_rating DESC",
                "SELECT g.genreName, COUNT(c.movieId) AS number_of_movies FROM Genre g JOIN CategorizedAs c ON g.genreId = c.genreId GROUP BY g.genreName ORDER BY number_of_movies DESC",
                "SELECT g.genreName, AVG(m.rating) AS average_rating FROM Genre g JOIN CategorizedAs c ON g.genreId = c.genreId JOIN Movies m ON c.movieId = m.movieId GROUP BY g.genreName ORDER BY average_rating DESC",
                "SELECT DISTINCT d.name AS director_name FROM Director d JOIN DirectedBy db ON d.dirId = db.dirId JOIN Movies m ON db.movieId = m.movieId WHERE m.rating > 8.0 ORDER BY d.name",
                "SELECT b.planName, COUNT(s.userId) AS number_of_subscribers FROM Billing b LEFT JOIN Subscribes s ON b.planId = s.planId JOIN Users u ON s.userId = u.userId WHERE u.subStatus = 'SUBSCRIBED' GROUP BY b.planName ORDER BY number_of_subscribers DESC",
                "SELECT u.name, u.email FROM Users u WHERE EXISTS (SELECT 1 FROM Review r JOIN Movies m ON r.movieId = m.movieId WHERE r.userId = u.userId AND m.title = 'Inception')",
                "SELECT m.title FROM Movies m JOIN DirectedBy db ON m.movieId = db.movieId JOIN Director d ON db.dirId = d.dirId WHERE LOWER(d.name) = LOWER('Christopher Nolan') UNION SELECT m.title FROM Movies m JOIN CategorizedAs c ON m.movieId = c.movieId JOIN Genre g ON c.genreId = g.genreId WHERE LOWER(g.genreName) = LOWER('Action')",
                "SELECT m.title FROM Movies m JOIN CategorizedAs c ON m.movieId = c.movieId JOIN Genre g ON c.genreId = g.genreId WHERE LOWER(g.genreName) = LOWER('Action') MINUS SELECT m.title FROM Movies m JOIN DirectedBy db ON m.movieId = db.movieId JOIN Director d ON db.dirId = d.dirId WHERE LOWER(d.name) = LOWER('Christopher Nolan')",
                "SELECT d.name AS director_name, COUNT(m.movieId) AS number_of_movies, AVG(m.rating) AS average_rating FROM Director d JOIN DirectedBy db ON d.dirId = db.dirId JOIN Movies m ON db.movieId = m.movieId GROUP BY d.name HAVING COUNT(m.movieId) > 1 ORDER BY average_rating DESC",
                "SELECT g.genreName FROM Genre g JOIN CategorizedAs c ON g.genreId = c.genreId JOIN Movies m ON c.movieId = m.movieId WHERE m.rating > 8 GROUP BY g.genreName HAVING COUNT(m.movieId) > 2"
            };

            String[] queryNames = {
                "subscribedUsers",
                "subscriptionTransactions",
                "userPlans",
                "highRatedMovies",
                "longDurationPlans",
                "aliceTransactions",
                "jamesCameronMovies",
                "actionMovies",
                "inceptionReviews",
                "directorMovieCounts",
                "planRevenue",
                "userAverageRatings",
                "moviesPerGenre",
                "genreAverageRatings",
                "topDirectors",
                "subscribersPerPlan",
                "inceptionReviewers",
                "nolanOrActionMovies",
                "actionMoviesNotByNolan",
                "directorsWithMultipleMovies",
                "genresWithHighRatedMovies"
            };

            for (int i = 0; i < queries.length; i++) {
                allResults.put(queryNames[i], executeQuery(connection, queries[i]));
            }

            return allResults;
        } catch (Exception e) {
            throw new RuntimeException("Error executing queries: " + e.getMessage());
        }
    }

    private List<Map<String, Object>> executeQuery(Connection connection, String query) throws SQLException {
        List<Map<String, Object>> results = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            ResultSetMetaData md = rs.getMetaData();
            int columns = md.getColumnCount();
            
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columns; i++) {
                    row.put(md.getColumnName(i), rs.getObject(i));
                }
                results.add(row);
            }
        }
        return results;
    }

    @DeleteMapping("/dropTables")
    public String dropTables() {
        try (Connection connection = dbConnection.getConnection()) {
            DropTables.dropTables(connection);
            return "Tables dropped successfully";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @GetMapping("/tableStatus")
    public Map<String, Object> getTableStatus() {
        Map<String, Object> status = new HashMap<>();
        List<Map<String, Object>> tables = new ArrayList<>();
        
        try (Connection connection = dbConnection.getConnection()) {
            String[] tableNames = {
                "Users", "Movies", "Director", "Genre", "Review", 
                "Transactions", "Billing", "UserLocation", "Discount",
                "BillingCore", "CategorizedAs", "DirectedBy", "Involves",
                "Makes", "Subscribes"
            };
            
            for (String tableName : tableNames) {
                Map<String, Object> tableInfo = new HashMap<>();
                tableInfo.put("name", tableName);
                
                try {
                    String checkQuery = "SELECT table_name FROM user_tables WHERE table_name = '" + tableName.toUpperCase() + "'";
                    List<Map<String, Object>> tableExists = executeQuery(connection, checkQuery);
                    boolean exists = !tableExists.isEmpty();
                    tableInfo.put("exists", exists);
                    
                    if (exists) {
                        String countQuery = "SELECT COUNT(*) as count FROM " + tableName;
                        List<Map<String, Object>> countResult = executeQuery(connection, countQuery);
                        tableInfo.put("rowCount", countResult.get(0).get("COUNT"));
                        
                        String dataQuery = "SELECT * FROM " + tableName;
                        List<Map<String, Object>> data = executeQuery(connection, dataQuery);
                        tableInfo.put("data", data);
                    } else {
                        tableInfo.put("rowCount", 0);
                    }
                } catch (SQLException e) {
                    tableInfo.put("exists", false);
                    tableInfo.put("rowCount", 0);
                }
                
                tables.add(tableInfo);
            }
            
            status.put("tables", tables);
            status.put("hasTables", tables.stream().anyMatch(t -> (Boolean)t.get("exists")));
            
            return status;
        } catch (Exception e) {
            throw new RuntimeException("Error checking table status: " + e.getMessage());
        }
    }

    @PutMapping("/updateRecord/{tableName}")
    public ResponseEntity<?> updateRecord(@PathVariable String tableName, @RequestBody Map<String, Object> record) {
        try (Connection connection = dbConnection.getConnection()) {
            String idColumn;
            switch (tableName.toLowerCase()) {
                case "users":
                    idColumn = "userId";
                    break;
                case "director":
                    idColumn = "dirId";
                    break;
                case "genre":
                    idColumn = "genreId";
                    break;
                case "movies":
                    idColumn = "movieId";
                    break;
                case "billing":
                    idColumn = "planId";
                    break;
                case "transactions":
                    idColumn = "transId";
                    break;
                default:
                    idColumn = tableName.toLowerCase() + "id";
            }
            
            boolean hasIdColumn = record.keySet().stream()
                .anyMatch(key -> key.toLowerCase().equals(idColumn.toLowerCase()));
                
            if (!hasIdColumn) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Missing ID column: " + idColumn);
                return ResponseEntity.badRequest().body(response);
            }

            String actualIdKey = record.keySet().stream()
                .filter(key -> key.toLowerCase().equals(idColumn.toLowerCase()))
                .findFirst()
                .get();

            StringBuilder sql = new StringBuilder("UPDATE " + tableName.toLowerCase() + " SET ");
            List<Object> paramValues = new ArrayList<>();
            
            for (Map.Entry<String, Object> entry : record.entrySet()) {
                if (!entry.getKey().toLowerCase().equals(idColumn.toLowerCase())) {
                    String key = entry.getKey().toLowerCase();
                    Object value = entry.getValue();
                    
                    if (key.toLowerCase().contains("date")) {
                        sql.append(key).append(" = TO_DATE(?, 'YYYY-MM-DD'), ");
                    } else {
                        sql.append(key).append(" = ?, ");
                    }
                    paramValues.add(value);
                }
            }
            
            sql.setLength(sql.length() - 2);
            sql.append(" WHERE ").append(idColumn).append(" = ?");
            paramValues.add(record.get(actualIdKey));
            
            try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
                for (int i = 0; i < paramValues.size(); i++) {
                    stmt.setObject(i + 1, paramValues.get(i));
                }
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    Map<String, String> response = new HashMap<>();
                    response.put("message", "No rows updated - ID may not exist: " + record.get(actualIdKey));
                }
                
                Map<String, String> response = new HashMap<>();
                response.put("message", "Record updated successfully");
                return ResponseEntity.ok().body(response);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Error updating record: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/addRecord/{tableName}")
    public String addRecord(@PathVariable String tableName, @RequestBody Map<String, Object> record) {
        try (Connection connection = dbConnection.getConnection()) {
            StringBuilder sql = new StringBuilder("INSERT INTO " + tableName.toLowerCase() + " (");
            StringBuilder values = new StringBuilder(") VALUES (");
            List<Object> paramValues = new ArrayList<>();
            
            for (Map.Entry<String, Object> entry : record.entrySet()) {
                sql.append(entry.getKey().toLowerCase()).append(", ");
                values.append("?, ");
                paramValues.add(entry.getValue());
            }
            
            sql.setLength(sql.length() - 2);
            values.setLength(values.length() - 2);
            sql.append(values).append(")");
            
            try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
                for (int i = 0; i < paramValues.size(); i++) {
                    stmt.setObject(i + 1, paramValues.get(i));
                }
                stmt.executeUpdate();
                return "Record added successfully";
            }
        } catch (Exception e) {
            throw new RuntimeException("Error adding record: " + e.getMessage());
        }
    }

    @DeleteMapping("/deleteRecord/{tableName}/{id}")
    public ResponseEntity<?> deleteRecord(@PathVariable String tableName, @PathVariable String id) {
        try (Connection connection = dbConnection.getConnection()) {
            connection.setAutoCommit(false); 
            
            try {
                if (tableName.toLowerCase().equals("users")) {
                    String[] relatedTables = {
                        "Makes",
                        "Subscribes",
                        "Review",
                        "Transactions",
                        "UserLocation"
                    };
                    
                    for (String relatedTable : relatedTables) {
                        String deleteRelated = "DELETE FROM " + relatedTable.toLowerCase() + 
                                             " WHERE userId = ?";
                        try (PreparedStatement stmt = connection.prepareStatement(deleteRelated)) {
                            stmt.setInt(1, Integer.parseInt(id));
                            stmt.executeUpdate();
                        }
                    }
                } else if (tableName.toLowerCase().equals("movies")) {
                    String[] relatedTables = {
                        "DirectedBy",
                        "CategorizedAs",
                        "Involves",
                        "Review"
                    };
                    
                    for (String relatedTable : relatedTables) {
                        String deleteRelated = "DELETE FROM " + relatedTable.toLowerCase() + 
                                             " WHERE movieId = ?";
                        try (PreparedStatement stmt = connection.prepareStatement(deleteRelated)) {
                            stmt.setInt(1, Integer.parseInt(id));
                            stmt.executeUpdate();
                        }
                    }
                }
                
                String idColumn = getIdColumnName(tableName);
                String sql = "DELETE FROM " + tableName.toLowerCase() + 
                            " WHERE " + idColumn + " = ?";
                
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setInt(1, Integer.parseInt(id));
                    int rowsAffected = stmt.executeUpdate();
                    
                    if (rowsAffected == 0) {
                        connection.rollback();
                        Map<String, String> response = new HashMap<>();
                        response.put("message", "No rows deleted - ID may not exist: " + id);
                        return ResponseEntity.badRequest().body(response);
                    }
                    
                    connection.commit();
                    Map<String, String> response = new HashMap<>();
                    response.put("message", "Record and related data deleted successfully");
                    return ResponseEntity.ok().body(response);
                }
            } catch (Exception e) {
                connection.rollback();
                throw e;
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Error deleting record: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    private String getIdColumnName(String tableName) {
        switch (tableName.toLowerCase()) {
            case "users": return "userId";
            case "director": return "dirId";
            case "genre": return "genreId";
            case "movies": return "movieId";
            case "billing": return "planId";
            case "transactions": return "transId";
            default: return tableName.toLowerCase() + "id";
        }
    }

    @GetMapping("/views")
    public Map<String, List<Map<String, Object>>> getViews() {
        Map<String, List<Map<String, Object>>> allViews = new HashMap<>();
        
        try (Connection connection = dbConnection.getConnection()) {
            String[] views = {
                "SELECT * FROM SubscribedUsersPlans",
                "SELECT * FROM HighRatedMovies",
                "SELECT * FROM MovieReviews",
                "SELECT * FROM UserTransactions"
            };
            
            String[] viewNames = {
                "subscribedUsersPlans",
                "highRatedMovies",
                "movieReviews",
                "userTransactions"
            };
            
            for (int i = 0; i < views.length; i++) {
                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery(views[i])) {
                    
                    List<Map<String, Object>> viewData = new ArrayList<>();
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    
                    while (rs.next()) {
                        Map<String, Object> row = new HashMap<>();
                        for (int j = 1; j <= columnCount; j++) {
                            row.put(metaData.getColumnName(j), rs.getObject(j));
                        }
                        viewData.add(row);
                    }
                    
                    allViews.put(viewNames[i], viewData);
                }
            }
            
            return allViews;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    @PostMapping("/createViews")
    public String createViews() {
        try (Connection connection = dbConnection.getConnection()) {
            CreateTables.createViews(connection);
            return "Views created successfully";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}

