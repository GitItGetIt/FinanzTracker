package de.fintracker.util;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class ZoomAndPanUtil {
    private static double lastX;
    private static double lastY;

    public static void enableZoom(VBox root) {
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

    public static void enablePanning(ScrollPane scrollPane) {
        scrollPane.setOnMousePressed(event -> {
            if (event.isMiddleButtonDown()) {
                lastX = event.getSceneX();
                lastY = event.getSceneY();
            }
        });

        scrollPane.setOnMouseDragged(event -> {
            if (event.isMiddleButtonDown()) {

                double deltaX = lastX - event.getSceneX();
                double deltaY = lastY - event.getSceneY();

                double width = scrollPane.getContent().getBoundsInLocal().getWidth();
                double height = scrollPane.getContent().getBoundsInLocal().getHeight();

                scrollPane.setHvalue(scrollPane.getHvalue() + deltaX / width);
                scrollPane.setVvalue(scrollPane.getVvalue() + deltaY / height);

                lastX = event.getSceneX();
                lastY = event.getSceneY();
            }
        });
    }
}
