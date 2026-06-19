package de.fintracker.controller;

import de.fintracker.util.ZoomAndPanUtil;
import de.fintracker.model.Expense;
import de.fintracker.model.Income;
import de.fintracker.service.ExpenseService;
import de.fintracker.service.IncomeService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class MainController extends BaseController {

    private final IncomeService incomeService = new IncomeService();
    private final ExpenseService expenseService = new ExpenseService();
    private final ZoomAndPanUtil zoomAndPanUtil = new ZoomAndPanUtil();

    @FXML private ScrollPane scrollPane;
    @FXML private Group zoomGroup;
    @FXML private StackPane zoomPane;
    @FXML private VBox rootContent;

    @FXML
    private Label totalIncomeLabel, totalExpenseLabel, balanceLabel, entryCountLabel;

    @Override
    protected void initialize() {
        super.initialize();
        updateDashboard();

        rootContent.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        rootContent.setPrefSize(400, 400);

        zoomAndPanUtil.enableZoomAndPan(scrollPane, zoomPane);
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
