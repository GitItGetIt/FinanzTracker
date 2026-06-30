package de.fintracker.database;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initialize() {
        try (Connection conn = DBConnector.getConnection();
             Statement stmt = conn.createStatement()) {

            String incomeTable = """
                CREATE TABLE IF NOT EXISTS income (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    amount REAL NOT NULL,
                    category TEXT NOT NULL,
                    date TEXT NOT NULL,
                    note TEXT
                );
            """;

            String expenseTable = """
                CREATE TABLE IF NOT EXISTS expense (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    amount REAL NOT NULL,
                    category TEXT NOT NULL,
                    date TEXT NOT NULL,
                    note TEXT
                );
            """;

            stmt.execute(incomeTable);
            stmt.execute(expenseTable);

            System.out.println("Tabellen erfolgreich erstellt.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

