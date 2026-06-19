package de.fintracker.util;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;

/**
 * A reusable service that adds zoom and pan functionality to any ScrollPane
 * containing a zoomable content node.
 *
 * <p>This class is designed for aggregation: controllers do not own this
 * service but simply use it. The service is stateless and can be reused
 * across multiple projects.</p>
 *
 * <p>Features:</p>
 * <ul>
 *     <li>CTRL + Scroll = Zoom</li>
 *     <li>Middle Mouse Drag = Pan</li>
 *     <li>Automatic scaling of the content node</li>
 *     <li>No project-specific dependencies</li>
 * </ul>
 */

public class ZoomAndPanUtil {

    private double lastMouseX;
    private double lastMouseY;

    /**
     * Enables zoom and pan functionality on the given ScrollPane.
     *
     * @param scrollPane the ScrollPane that should support zoom and pan
     * @param content the Node inside the ScrollPane that should be zoomable
     */
    public void enableZoomAndPan(ScrollPane scrollPane, Node content) {
        enableZoom(scrollPane, content);
        enablePanning(scrollPane);
    }

    /**
     * Adds zooming via CTRL + mouse wheel.
     */
    private void enableZoom(ScrollPane scrollPane, Node content) {
        scrollPane.addEventFilter(ScrollEvent.SCROLL, event -> {
            if (!event.isControlDown()) {
                return;
            }

            double zoomFactor = 1.05;

            if (event.getDeltaY() < 0) {
                zoomFactor = 1 / zoomFactor;
            }

            content.setScaleX(content.getScaleX() * zoomFactor);
            content.setScaleY(content.getScaleY() * zoomFactor);

            event.consume();
        });
    }

    /**
     * Adds panning via middle mouse button drag.
     */
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

                double width = scrollPane.getContent().getBoundsInLocal().getWidth();
                double height = scrollPane.getContent().getBoundsInLocal().getHeight();

                scrollPane.setHvalue(scrollPane.getHvalue() + deltaX / width);
                scrollPane.setVvalue(scrollPane.getVvalue() + deltaY / height);

                lastMouseX = event.getSceneX();
                lastMouseY = event.getSceneY();
            }
        });
    }

}
