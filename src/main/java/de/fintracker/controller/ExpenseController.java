package de.fintracker.controller;

import de.fintracker.model.Expense;
import de.fintracker.model.Income;
import de.fintracker.service.ExpenseService;
import de.fintracker.service.IncomeService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;


import java.time.LocalDate;

public class ExpenseController implements Navigatable {

    private Stage stage;

    @Override
    public void setStage(Stage stage){
        this.stage = stage;
    }

    @FXML
    private TextField amountField;

    @FXML
    private ComboBox<String> categoryBox;

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextArea noteArea;

    @FXML
    private TableView<Expense> expenseTable;

    @FXML
    private TableColumn<Expense, Number> idColumn;

    @FXML
    private TableColumn<Expense, Double> amountColumn;

    @FXML
    private TableColumn<Expense, String> categoryColumn;

    @FXML
    private TableColumn<Expense, LocalDate> dateColumn;

    @FXML
    private TableColumn<Expense, String> noteColumn;

    private final ObservableList<Expense> expenseList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTable();
        loadExpenseList();
        loadCategories();
    }

    private void setupTable() {
        idColumn.setCellValueFactory(cell -> cell.getValue().idProperty());
        amountColumn.setCellValueFactory(cell -> cell.getValue().amountProperty().asObject());
        categoryColumn.setCellValueFactory(cell -> cell.getValue().categoryProperty());
        dateColumn.setCellValueFactory(cell -> cell.getValue().dateProperty());
        noteColumn.setCellValueFactory(cell -> cell.getValue().noteProperty());

        expenseTable.setItems(expenseList);
    }

    private void loadCategories() {
        categoryBox.getItems().addAll("Miete", "Essen", "Transport", "Freizeit", "Sonstiges");
    }

    @FXML
    private void saveExpense() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            String category = categoryBox.getValue();
            LocalDate date = datePicker.getValue();
            String note = noteArea.getText();

            Expense expense = new Expense(amount, category, date, note);

            ExpenseService.insertExpense(expense);

            loadExpenseList();

            clearFields();

        } catch (Exception e) {
            showError("Bitte überprüfe deine Eingaben.");
        }
    }

    private void loadExpenseList() {
        expenseList.setAll(ExpenseService.getAllExpense());
    }

    private void deleteExpense() {
        Expense selected = expenseTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showError("Bitte wähle einen Eintrag aus.");
            return;
        }

        ExpenseService.deleteExpense(selected.getId());
        loadExpenseList();
    }

    private void updateExpense() {
        Expense selected = expenseTable.getSelectionModel().getSelectedItem();
        if (selected == null){
            showError("Bitte wähle einen Eintag aus");
            return;
        }

        selected.setAmount(Double.parseDouble(amountField.getText()));
        selected.setCategory(categoryBox.getValue());
        selected.setDate(datePicker.getValue());
        selected.setNote(noteArea.getText());

        ExpenseService.updateExpense(selected, selected.getId());
        loadExpenseList();
    }

    private void clearFields() {
        amountField.clear();
        categoryBox.setValue(null);
        datePicker.setValue(null);
        noteArea.clear();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Fehler");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML
    private void goToMain() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/main.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);

            MainController controller = loader.getController();
            controller.setStage(stage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
