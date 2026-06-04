package de.fintracker.controller;

import de.fintracker.model.Expense;
import de.fintracker.service.ExpenseService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.List;

public class ExpenseController extends AbstractTableController<Expense> {

    @FXML private TableView<Expense> expenseTable;
    @FXML private Pagination pagination;

    @FXML private TableColumn<Expense, Number> idColumn;
    @FXML private TableColumn<Expense, Double> amountColumn;
    @FXML private TableColumn<Expense, String> categoryColumn;
    @FXML private TableColumn<Expense, String> dateColumn;
    @FXML private TableColumn<Expense, String> noteColumn;

    @FXML private TextField amountField;
    @FXML private ComboBox<String> categoryBox;
    @FXML private DatePicker datePicker;
    @FXML private TextArea noteArea;
    @FXML private TextField searchField;

    @FXML
    protected void initialize(){
        //später sout wieder rausnehmen: will nur kurz überprüfen:
        System.out.println("INIT OK: ExpenseController");

        super.initialize();

        setupTable();
        setupPagination(pagination, expenseTable);
        setupSelectionListener();
        setupFilter();
    }

    @Override
    protected void setupTable() {

        idColumn.setCellValueFactory(cell -> cell.getValue().idProperty());
        amountColumn.setCellValueFactory(cell -> cell.getValue().amountProperty().asObject());
        categoryColumn.setCellValueFactory(cell -> cell.getValue().categoryProperty());
        dateColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getDate().toString()));
        noteColumn.setCellValueFactory(cell -> cell.getValue().noteProperty());

        categoryBox.getItems().addAll("Essen", "Freizeit", "Miete", "Transport", "Sonstiges");
    }

    @Override
    protected List<Expense> loadPageData(int offset, int limit) {
        return ExpenseService.getExpensePage(offset, limit);
    }

    @Override
    protected int getTotalItemCount() {
        return ExpenseService.countExpense();
    }

    @Override
    protected void setupSelectionListener() {
        expenseTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                amountField.setText(String.valueOf(newVal.getAmount()));
                categoryBox.setValue(newVal.getCategory());
                datePicker.setValue(newVal.getDate());
                noteArea.setText(newVal.getNote());
            }
        });
    }

    @Override
    protected void setupFilter() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.isEmpty()) {
                pagination.setPageFactory(pageIndex -> createPage(pageIndex, expenseTable));
                return;
            }

            String f = newVal.toLowerCase();

            List<Expense> filtered = ExpenseService.getAllExpense().stream()
                    .filter(i ->
                            String.valueOf(i.getId()).contains(f) ||
                                    String.valueOf(i.getAmount()).contains(f) ||
                                    i.getCategory().toLowerCase().contains(f) ||
                                    i.getDate().toString().contains(f) ||
                                    i.getNote().toLowerCase().contains(f)
                    )
                    .toList();

            expenseTable.setItems(FXCollections.observableArrayList(filtered));
        });
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

            updatePageCount(pagination);
            refreshCurrentPage(pagination);
            clearFields();

        } catch (Exception e) {
            showError("Bitte überprüfe deine Eingabedaten bzgl. Ausgaben.");
        }
    }

    @FXML
    private void deleteExpense() {
        Expense selected = expenseTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Bitte wähle einen Eintrag aus.");
            return;
        }

        ExpenseService.deleteExpense(selected.getId());

        updatePageCount(pagination);
        refreshCurrentPage(pagination);
    }

    @FXML
    private void updateExpense() {
        Expense selected = expenseTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Bitte wähle einen Eintrag aus.");
            return;
        }

        selected.setAmount(Double.parseDouble(amountField.getText()));
        selected.setCategory(categoryBox.getValue());
        selected.setDate(datePicker.getValue());
        selected.setNote(noteArea.getText());

        ExpenseService.updateExpense(selected, selected.getId());

        refreshCurrentPage(pagination);
    }

    @FXML
    private void goToMain() {
        switchScene("/views/main.fxml");
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
}
