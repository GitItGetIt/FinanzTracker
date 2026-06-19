package de.fintracker.util;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class ZoomAndPanUtil {

    // nxt Aggregation 19.6.
    private static final double SCALE_DELTA = 1.1;

    public static void enableZoomAndPan(ScrollPane scrollPane, Node zoomPane) {

        // Zoom
        zoomPane.setOnScroll(event -> {
            if (event.isControlDown()) {

                double scale = zoomPane.getScaleX();
                scale *= (event.getDeltaY() > 0) ? SCALE_DELTA : 1/SCALE_DELTA;

                zoomPane.setScaleX(scale);
                zoomPane.setScaleY(scale);

                event.consume();
            }
        });

        // Pan
        final ObjectProperty<Point2D> lastMouse = new SimpleObjectProperty<>();
        final ObjectProperty<Point2D> lastTranslate = new SimpleObjectProperty<>();

        zoomPane.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown() && event.isControlDown()) {
                lastMouse.set(new Point2D(event.getSceneX(), event.getSceneY()));
                lastTranslate.set(new Point2D(zoomPane.getTranslateX(),zoomPane.getTranslateY()));
                event.consume();
            }
        });

        zoomPane.setOnMouseDragged(event -> {
            if (event.isPrimaryButtonDown() && event.isControlDown()) {
                double deltaX = event.getSceneX() - lastMouse.get().getX();
                double deltaY = event.getSceneY() - lastMouse.get().getY();

                zoomPane.setTranslateX(lastTranslate.get().getX() + deltaX);
                zoomPane.setTranslateY(lastTranslate.get().getY() + deltaY);

                event.consume();
            }
        });
    }
}
