package com.movieplatform;

import java.sql.Connection;
import java.util.Scanner;

public class DatabaseApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        DatabaseConnection dbConnection = new DatabaseConnection();
        Connection connection = dbConnection.getConnection();

        while (true) {
            System.out.println("\n--- Database Menu ---");
            System.out.println("1. Create Tables");
            System.out.println("2. Populate Tables");
            System.out.println("3. Query Tables");
            System.out.println("4. Drop Tables");
            System.out.println("5. Exit");
            System.out.print("Select an option: ");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    CreateTables.createBaseTables(connection);
                    CreateTables.createNormalizedTables(connection);
                    CreateTables.createViews(connection);
                    break;
                case 2:
                    PopulateTables.populateBaseTables(connection);
                    PopulateTables.populateNormalizedTables(connection);
                    break;
                case 3:
                    QueryTables.executeQueries(connection);
                    break;
                case 4:
                    DropTables.dropTables(connection);
                    break;
                case 5:
                    System.out.println("Exiting...");
                    dbConnection.closeConnection(connection);
                    System.exit(0);
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
}
