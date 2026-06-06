package de.fintracker.service;

import de.fintracker.database.DBConnector;
import de.fintracker.model.Income;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class IncomeService {

    public void insertIncome(Income income) {
        String sql = "INSERT INTO income (amount, category, date, note) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, income.getAmount());
            stmt.setString(2, income.getCategory());
            stmt.setString(3, income.getDate().toString());  // LocalDate → String
            stmt.setString(4, income.getNote());

            stmt.executeUpdate();
            System.out.println("Einnahme gespeichert!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ObservableList<Income> getAllIncome() {
        ObservableList<Income> list = FXCollections.observableArrayList();

        String sql = "SELECT id, amount, category, date, note FROM income";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                double amount = rs.getDouble("amount");
                String category = rs.getString("category");
                LocalDate date = LocalDate.parse(rs.getString("date"));
                String note = rs.getString("note");

                list.add(new Income(id,amount, category, date, note));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public void updateIncome(Income income, int id) {
        String sql = "UPDATE income SET amount = ?, category = ?, date = ?, note = ? WHERE id = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, income.getAmount());
            stmt.setString(2, income.getCategory());
            stmt.setString(3, income.getDate().toString());
            stmt.setString(4, income.getNote());
            stmt.setInt(5, id);

            stmt.executeUpdate();
            System.out.println("Einnahme aktualisiert!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteIncome(int id) {
        String sql = "DELETE FROM income WHERE id = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

            System.out.println("Einnahme gelöscht!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Income> getIncomePage(int offset, int limit) {
        List<Income> list = new ArrayList<>();

        String sql = "SELECT * FROM income ORDER BY date DESC LIMIT ? OFFSET ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);
            stmt.setInt(2, offset);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(new Income(
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

    public int countIncome() {
        String sql = "SELECT COUNT(*) FROM income";

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
