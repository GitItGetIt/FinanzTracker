package de.fintracker.controller;

import de.fintracker.util.UiZoomAndPanUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public abstract class BaseController {

    protected Stage stage;



    public void setStage(Stage stage) {
        this.stage = stage;
    }

    protected void switchScene(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());

            stage.setScene(scene);

            BaseController controller = loader.getController();
            controller.setStage(stage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void initialize() {
//        UiZoomAndPanUtil.enableZoom(root);
//        UiZoomAndPanUtil.enablePanning(scrollPane);
    }
}
