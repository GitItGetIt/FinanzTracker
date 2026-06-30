package de.fintracker;

import de.fintracker.controller.MainController;
import de.fintracker.database.DatabaseInitializer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.Objects;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        DatabaseInitializer.initialize();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/main.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setTitle("Finanztracker");
        stage.show();

        MainController controller = loader.getController();
        controller.setStage(stage);

        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/application.css")).toExternalForm());
    }

    public static void main (String[] args) {
        launch();
    }
}
