package de.fintracker.model;

import javafx.beans.property.*;
import java.time.LocalDate;

public class Expense {

    private final DoubleProperty amount = new SimpleDoubleProperty();
    private final StringProperty category = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
    private final StringProperty note = new SimpleStringProperty();

    public Expense(double amount, String category, LocalDate date, String note) {
        this.amount.set(amount);
        this.category.set(category);
        this.date.set(date);
        this.note.set(note);
    }

    // Getter
    public double getAmount() { return amount.get(); }
    public String getCategory() { return category.get(); }
    public LocalDate getDate() { return date.get(); }
    public String getNote() { return note.get(); }

    // Properties (für TableView)
    public DoubleProperty amountProperty() { return amount; }
    public StringProperty categoryProperty() { return category; }
    public ObjectProperty<LocalDate> dateProperty() { return date; }
    public StringProperty noteProperty() { return note; }

    // Setter
    public void setAmount(double amount) { this.amount.set(amount); }
    public void setCategory(String category) { this.category.set(category); }
    public void setDate(LocalDate date) { this.date.set(date); }
    public void setNote(String note) { this.note.set(note); }
}
