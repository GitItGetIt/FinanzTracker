package de.fintracker.service;

import de.fintracker.database.DBConnector;
import de.fintracker.model.Expense;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ExpenseService {

    public static void insertExpense(Expense expense) {
        String sql = "INSERT INTO expense (amount, category, date, note) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, expense.getAmount());
            stmt.setString(2, expense.getCategory());
            stmt.setString(3, expense.getDate().toString());  // LocalDate → String
            stmt.setString(4, expense.getNote());

            stmt.executeUpdate();
            System.out.println("Ausgabe gespeichert!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ObservableList<Expense> getAllExpense() {
        ObservableList<Expense> list = FXCollections.observableArrayList();

        String sql = "SELECT id, amount, category, date, note FROM expense";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                double amount = rs.getDouble("amount");
                String category = rs.getString("category");
                LocalDate date = LocalDate.parse(rs.getString("date"));
                String note = rs.getString("note");

                list.add(new Expense(id, amount, category, date, note));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static void updateExpense(Expense expense, int id) {
        String sql = "UPDATE expense SET amount = ?, category = ?, date = ?, note = ? WHERE id = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, expense.getAmount());
            stmt.setString(2, expense.getCategory());
            stmt.setString(3, expense.getDate().toString());
            stmt.setString(4, expense.getNote());
            stmt.setInt(5, id);

            stmt.executeUpdate();
            System.out.println("Ausgabe aktualisiert!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteExpense(int id) {
        String sql = "DELETE FROM expense WHERE id = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

            System.out.println("Ausgabe gelöscht!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Expense>getExpensePage(int offset, int limit) {
        List<Expense> list = new ArrayList<>();

        String sql = "SELECT * FROM expense ORDER BY date DESC LIMIT ? OFFSET ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);
            stmt.setInt(2, offset);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(new Expense(
                        rs.getInt("id"),
                        rs.getDouble("amount"),
                        rs.getString("category"),
                        LocalDate.parse(rs.getString("date")),
                        rs.getString("note")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static int countExpense() {
        String sql = "SELECT COUNT(*) FROM expense";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            return rs.getInt(1);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

}
