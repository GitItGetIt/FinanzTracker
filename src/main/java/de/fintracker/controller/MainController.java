package de.fintracker.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainController implements Navigatable {

    private Stage stage;

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

    @FXML
    private void openIncome() {
        switchScene("income.fxml");
    }

    @FXML
    private void openExpense() {
        switchScene("expense.fxml");
    }
}
