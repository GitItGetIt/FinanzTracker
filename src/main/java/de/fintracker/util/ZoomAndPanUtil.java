package de.fintracker.util;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;

public class ZoomAndPanUtil {

    private static final double SCALE_DELTA = 1.1;

    public static void enableZoomAndPan(ScrollPane scrollPane, Pane wrapper, Node content) {

        // Zoom (mit STRG + Scroll)
        content.setOnScroll(event -> {
            if (event.isControlDown()) {
                double scale = content.getScaleX();

                if (event.getDeltaY() > 0) {
                    scale *= SCALE_DELTA;
                } else {
                    scale /= SCALE_DELTA;
                }

                content.setScaleX(scale);
                content.setScaleY(scale);

                // Wrapper neu berechnen
                wrapper.setPrefWidth(content.getBoundsInParent().getWidth());
                wrapper.setPrefHeight(content.getBoundsInParent().getHeight());

                event.consume();
            }
        });

        // Pan (mittlere Maustaste gedrückt halten)
        final ObjectProperty<Point2D> lastMouse = new SimpleObjectProperty<>();

        content.setOnMousePressed(event -> {
            if (event.isMiddleButtonDown()) {
                lastMouse.set(new Point2D(event.getSceneX(), event.getSceneY()));
            }
        });

        content.setOnMouseDragged(event -> {
            if (event.isMiddleButtonDown()) {
                double deltaX = event.getSceneX() - lastMouse.get().getX();
                double deltaY = event.getSceneY() - lastMouse.get().getY();

                scrollPane.setHvalue(scrollPane.getHvalue() - deltaX / wrapper.getWidth());
                scrollPane.setVvalue(scrollPane.getVvalue() - deltaY / wrapper.getHeight());

                lastMouse.set(new Point2D(event.getSceneX(), event.getSceneY()));
            }
        });
    }
}
