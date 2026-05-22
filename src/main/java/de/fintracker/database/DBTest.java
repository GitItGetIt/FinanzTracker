package de.fintracker.database;

public class DBTest {
    public static void main(String[] args) {
        try {
            DBConnector.getConnection();
            System.out.println("SQLit-datei konnte erstellt werden - Super!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
