package de.fintracker.model;

import javafx.beans.property.*;
import java.time.LocalDate;

public class Income implements RowConvertible {

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final DoubleProperty amount = new SimpleDoubleProperty();
    private final StringProperty category = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
    private final StringProperty note = new SimpleStringProperty();

    public Income(int id, double amount, String category, LocalDate date, String note) {
        this.id.set(id);
        this.amount.set(amount);
        this.category.set(category);
        this.date.set(date);
        this.note.set(note);
    }

    // ctor o id f Inserts
    public Income(double amount, String category, LocalDate date, String note) {
        this.amount.set(amount);
        this.category.set(category);
        this.date.set(date);
        this.note.set(note);
    }

    public Income() {
    }

    public int getId() { return id.get(); }
    public double getAmount() { return amount.get(); }
    public String getCategory() { return category.get(); }
    public LocalDate getDate() { return date.get(); }
    public String getNote() { return note.get(); }

    public IntegerProperty idProperty() { return id; }
    public DoubleProperty amountProperty() { return amount; }
    public StringProperty categoryProperty() { return category; }
    public ObjectProperty<LocalDate> dateProperty() { return date; }
    public StringProperty noteProperty() { return note; }

    public void setId(int id) { this.id.set(id); }
    public void setAmount(double amount) { this.amount.set(amount); }
    public void setCategory(String category) { this.category.set(category); }
    public void setDate(LocalDate date) { this.date.set(date); }
    public void setNote(String note) { this.note.set(note); }

    @Override
    public String[] toRow() {
        return new String[] {
                String.valueOf(getId()),
                String.valueOf(getAmount()),
                getCategory(),
                getDate().toString(),
                getNote()
        };
    }

    @Override
    public void fromRow(String[] row) {
                //niemals auf die Property-Felder direkt zugreifen sondern immer mit getter/setter:
                        // falsch:
                    // this.note = row[4]; (=STringProperty)  oder this.date = LocalDate.parse(row[3]); (=ObjectProperty)
                        // denn meine Felder sehen ja so aus: private final StringProperty note = new SimpleStringProperty();
        setId(Integer.parseInt(row[0]));
        setAmount(Double.parseDouble(row[1]));
        setCategory(row[2]);
        setDate(LocalDate.parse(row[3]));
        setNote(row[4]);
    }
}
