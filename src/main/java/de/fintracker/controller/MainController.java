package de.fintracker.controller;

import de.fintracker.model.Expense;
import de.fintracker.model.Income;
import de.fintracker.service.ExpenseService;
import de.fintracker.service.IncomeService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainController implements Navigatable {

    private Stage stage;

    private double lastMouseX;
    private double lastMouseY;

    @FXML
    private Label totalIncomeLabel, totalExpenseLabel, balanceLabel, entryCountLabel;

    private final IncomeService incomeService = new IncomeService();
    private final ExpenseService expenseService = new ExpenseService();

    @FXML
    private VBox root;

    @FXML
    private ScrollPane scrollPane;


    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void switchScene(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/" + fxmlPath));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());

            stage.setScene(scene);

            Navigatable controller = loader.getController();
            controller.setStage(stage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void enablePanning(ScrollPane scrollPane) {

        scrollPane.setOnMousePressed(event -> {
            if (event.isMiddleButtonDown()) {
                lastMouseX = event.getSceneX();
                lastMouseY = event.getSceneY();
            }
        });

        scrollPane.setOnMouseDragged(event -> {
            if (event.isMiddleButtonDown()) {

                double deltaX = lastMouseX - event.getSceneX();
                double deltaY = lastMouseY - event.getSceneY();

                scrollPane.setHvalue(scrollPane.getHvalue() + deltaX / scrollPane.getContent().getBoundsInLocal().getWidth());
                scrollPane.setVvalue(scrollPane.getVvalue() + deltaY / scrollPane.getContent().getBoundsInLocal().getHeight());

                lastMouseX = event.getSceneX();
                lastMouseY = event.getSceneY();
            }
        });
    }

    @FXML
    private void openIncome() {
        switchScene("income.fxml");
    }

    @FXML
    private void openExpense() {
        switchScene("expense.fxml");
    }

    @FXML
    public void initialize() {
        updateDashboard();
        enableZoom();
        enablePanning(scrollPane);
    }

    private void enableZoom() {
        root.setOnScroll(event -> {
            if (event.isControlDown()) {
                double zoomFactor = 1.05;

                if (event.getDeltaY() < 0) {
                    zoomFactor = 1 / zoomFactor;
                }

                root.setScaleX(root.getScaleX() * zoomFactor);
                root.setScaleY(root.getScaleY() * zoomFactor);

                root.layout();
                event.consume();
            }
        });
    }


    private void updateDashboard() {
        double totalIncome = incomeService.getAllIncome().stream()
                .mapToDouble(Income::getAmount)
                .sum();

        double totalExpense = expenseService.getAllExpense().stream()
                .mapToDouble(Expense::getAmount)
                .sum();

        int entryCount = incomeService.getAllIncome().size()
                + expenseService.getAllExpense().size();

        double balance = totalIncome - totalExpense;

        totalIncomeLabel.setText(String.format("%.2f €", totalIncome));
        totalExpenseLabel.setText(String.format("%.2f €", totalExpense));
        balanceLabel.setText(String.format("%.2f €", balance));
        entryCountLabel.setText(String.valueOf(entryCount));
    }
}
