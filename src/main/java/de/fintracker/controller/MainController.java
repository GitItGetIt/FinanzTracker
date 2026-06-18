package de.fintracker.controller;

import de.fintracker.model.Expense;
import de.fintracker.model.Income;
import de.fintracker.service.ExpenseService;
import de.fintracker.service.IncomeService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;

import java.io.IOException;

public class MainController extends BaseController {

    private final IncomeService incomeService = new IncomeService();
    private final ExpenseService expenseService = new ExpenseService();


//    @FXML private ScrollPane scrollPane;

    @FXML
    private Label totalIncomeLabel, totalExpenseLabel, balanceLabel, entryCountLabel;

    @Override
    protected void initialize() {
        super.initialize();
        updateDashboard();
    }

    private void updateDashboard() {
        // Einnahmen berechnen
        double totalIncome = incomeService.getAllIncome().stream()
                .mapToDouble(Income::getAmount)
                .sum();

        // Ausgaben berechnen
        double totalExpense = expenseService.getAllExpense().stream()
                .mapToDouble(Expense::getAmount)
                .sum();

        // Anzahl Einträge
        int entryCount = incomeService.getAllIncome().size()
                + expenseService.getAllExpense().size();

        // Saldo
        double balance = totalIncome - totalExpense;

        // Labels setzen
        totalIncomeLabel.setText(String.format("%.2f €", totalIncome));
        totalExpenseLabel.setText(String.format("%.2f €", totalExpense));
        balanceLabel.setText(String.format("%.2f €", balance));
        entryCountLabel.setText(String.valueOf(entryCount));

        // Saldo farblich hervorheben
        if (balance < 0) {
            balanceLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        } else {
            balanceLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
        }
    }

    @FXML
    private void openIncome() {
        switchScene("/views/income.fxml");
    }

    @FXML
    private void openExpense() {
        switchScene("/views/expense.fxml");
    }

    public void openCharts() {
        try {
            switchScene("/views/charts.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
