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

    private void switchScene(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/" + fxml));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);

            Object controller = loader.getController();
            if (controller instanceof Navigatable nav) {
                nav.setStage(stage);
            }

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
