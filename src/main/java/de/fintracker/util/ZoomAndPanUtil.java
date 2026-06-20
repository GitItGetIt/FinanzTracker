package de.fintracker.util;

import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

/**
 * A reusable service that adds zoom and pan functionality to any ScrollPane
 * containing a zoomable content node.
 *
 * Safe against GPU crashes (max zoom limit).
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

    private static final double MAX_SCALE = 10.0;   // verhindert GPU crash
    private static final double MIN_SCALE = 0.1;

    private double lastMouseX;
    private double lastMouseY;

    /**
     * Enables zoom and pan functionality on the given ScrollPane.
     *
     * @param scrollPane the ScrollPane that should support zoom and pan
     * @param content the Node inside the ScrollPane that should be zoomable
     */
    public void enableZoomAndPan(ScrollPane scrollPane, Node content) {

        // setzt das Cashing außer Kraft um D3DTexture crash zu verhindert
        content.setCache(false);
        content.setCacheHint(CacheHint.DEFAULT);

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

            double newScale = content.getScaleX() * zoomFactor;

            // die min/max limits anwenden:
            if (newScale < MIN_SCALE) newScale = MIN_SCALE;
            if (newScale > MAX_SCALE) newScale = MAX_SCALE;

            content.setScaleX(newScale);
            content.setScaleY(newScale);

            event.consume();
        });
    }

    /**
     * Adds panning via middle mouse button drag.
     */
    private void enablePanning(ScrollPane scrollPane) {

        scrollPane.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.isMiddleButtonDown()) {
                lastMouseX = event.getSceneX();
                lastMouseY = event.getSceneY();
            }
        });

        scrollPane.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
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
