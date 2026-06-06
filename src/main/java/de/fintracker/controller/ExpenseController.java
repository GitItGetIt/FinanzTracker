package de.fintracker.controller;

import de.fintracker.model.Expense;
import de.fintracker.model.Income;
import de.fintracker.service.CSVService;
import de.fintracker.service.ExpenseService;
import de.fintracker.service.IncomeService;
import de.fintracker.service.XLSService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.apache.commons.math3.analysis.function.Exp;

import java.io.File;
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

    private final ExpenseService expenseService = new ExpenseService();

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
        return expenseService.getExpensePage(offset, limit);
    }

    @Override
    protected int getTotalItemCount() {
        return expenseService.countExpense();
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

            List<Expense> filtered = expenseService.getAllExpense().stream()
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

    //filechooser f export
    @FXML
    private void onExportCSV() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Dateien", "*.csv"));
        chooser.setInitialFileName("expense_export.csv");

        File file = chooser.showSaveDialog(null);
        if (file == null) return;

        List<Expense> allExpense = expenseService.getAllExpense();

        CSVService service = new CSVService();
        service.exportExpenseCSV(file.getAbsolutePath(), allExpense);
    }

    @FXML
    private void onExportXLS() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Dateien", "*.xlsx"));
        chooser.setInitialFileName("expense_export.xlsx");

        File file = chooser.showSaveDialog(null);
        if (file == null) return;

        List<Expense> allExpense = expenseService.getAllExpense();

        XLSService service = new XLSService();
        service.exportExpenseXLS(file.getAbsolutePath(), allExpense);
    }

    @FXML
    private void onImportXLS() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Dateien", "*.xlsx"));

        File file = chooser.showOpenDialog(null);
        if (file == null) return;

        XLSService service = new XLSService();
        List<Expense> imported = service.importExpenseXLS(file.getAbsolutePath());

        // In DB speichern
        for (Expense i : imported) {
            expenseService.insertExpense(i);
        }

        // Tabelle aktualisieren
        expenseTable.setItems(expenseService.getAllExpense());
    }

    @FXML
    private void saveExpense() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            String category = categoryBox.getValue();
            LocalDate date = datePicker.getValue();
            String note = noteArea.getText();

            Expense expense = new Expense(amount, category, date, note);
            expenseService.insertExpense(expense);

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

        expenseService.deleteExpense(selected.getId());

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

        expenseService.updateExpense(selected, selected.getId());

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
