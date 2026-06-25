package de.fintracker.util;

import javafx.stage.FileChooser;
import java.io.File;

public class FileChooserUtil {

    public static File saveCSV(String defaultName) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("CSV speichern");
        chooser.setInitialFileName(defaultName);
        chooser.getExtensionFilters().setAll(
                new FileChooser.ExtensionFilter("CSV Dateien (*.csv)", "*.csv")
        );
        return chooser.showSaveDialog(null);
    }

    public static File saveXLS(String defaultName) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Excel speichern");
        chooser.setInitialFileName(defaultName);
        chooser.getExtensionFilters().setAll(
                new FileChooser.ExtensionFilter("Excel Dateien (*.xlsx)", "*.xlsx")
        );
        return chooser.showSaveDialog(null);
    }

    public static File openCSV() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("CSV auswählen");
        chooser.getExtensionFilters().setAll(
                new FileChooser.ExtensionFilter("CSV Dateien (*.csv)", "*.csv")
        );
        return chooser.showOpenDialog(null);
    }

    public static File openXLS() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Excel auswählen");
        chooser.getExtensionFilters().setAll(
                new FileChooser.ExtensionFilter("Excel Dateien (*.xlsx)", "*.xlsx")
        );
        return chooser.showOpenDialog(null);
    }
}
