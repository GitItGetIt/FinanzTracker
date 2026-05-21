package de.fintracker.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class OverviewController implements Navigatable {

    private Stage stage;

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void goToIncome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/income.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);

            IncomeController controller = loader.getController();
            controller.setStage(stage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToExpense() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/expense.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);

            ExpenseController controller = loader.getController();
            controller.setStage(stage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToMain() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/main.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);

            MainController controller = loader.getController();
            controller.setStage(stage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
