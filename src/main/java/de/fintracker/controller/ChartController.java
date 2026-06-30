package de.fintracker.controller;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.chart.*;
import de.fintracker.service.IncomeService;
import de.fintracker.service.ExpenseService;
import de.fintracker.model.Income;
import de.fintracker.model.Expense;
import de.fintracker.util.ZoomAndPanUtil;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Collectors;

public class ChartController extends BaseController{

    private final IncomeService incomeService = new IncomeService();
    private final ExpenseService expenseService = new ExpenseService();
    private final ZoomAndPanUtil zoomAndPanUtil = new ZoomAndPanUtil();

    @FXML private PieChart expensePieChart;
    @FXML private BarChart<String, Number> monthlyBarChart;

    @FXML private ScrollPane scrollPane;
    @FXML private Group zoomGroup;
    @FXML private StackPane zoomPane;
    @FXML private VBox rootContent;

    @FXML
    public void initialize() {

        loadPieChart();
        loadBarChart();

        rootContent.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        rootContent.setPrefSize(800,800);

        zoomAndPanUtil.enableZoomAndPan(scrollPane, zoomPane);
    }

    private void loadPieChart() {
        Map<String, Double> expenseByCategory = expenseService.getAllExpense().stream()
                .collect(Collectors.groupingBy(
                        Expense::getCategory,
                        Collectors.summingDouble(Expense::getAmount)
                ));

        expensePieChart.getData().clear();
        expenseByCategory.forEach((category, amount) ->
                expensePieChart.getData().add(new PieChart.Data(category, amount))
        );
    }

    private void loadBarChart() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM");

        Map<String, Double> incomeByMonth = incomeService.getAllIncome().stream()
                .collect(Collectors.groupingBy(
                        i -> i.getDate().format(fmt),
                        Collectors.summingDouble(Income::getAmount)
                ));

        Map<String, Double> expenseByMonth = expenseService.getAllExpense().stream()
                .collect(Collectors.groupingBy(
                        e -> e.getDate().format(fmt),
                        Collectors.summingDouble(Expense::getAmount)
                ));

        XYChart.Series<String, Number> incomeSeries = new XYChart.Series<>();
        incomeSeries.setName("Einnahmen");

        XYChart.Series<String, Number> expenseSeries = new XYChart.Series<>();
        expenseSeries.setName("Ausgaben");

        // Alle Monate zusammenführen
        incomeByMonth.keySet().stream()
                        .sorted()
                        .forEach(month -> incomeSeries.getData().add(
                                new XYChart.Data<>(month, incomeByMonth.get(month))
                        ));

        expenseByMonth.keySet().stream()
                .sorted()
                .forEach(month -> expenseSeries.getData().add(
                        new XYChart.Data<>(month, expenseByMonth.get(month))
                ));

        monthlyBarChart.getData().clear();
        monthlyBarChart.getData().addAll(incomeSeries, expenseSeries);
    }

    @FXML
    private void goToMain(){
        switchScene("/views/main.fxml");
    }
}
