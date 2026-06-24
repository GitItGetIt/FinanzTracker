package de.fintracker.model;

public interface RowConvertible {
    String[] toRow();              // Export: Model -> CSV/XLS-Zeile
    void fromRow(String[] row);    // Import: CSV/XLS-Zeile -> Model
}
