package de.fintracker.controller;

import de.fintracker.model.Expense;
import de.fintracker.model.Income;
import de.fintracker.service.CSVService;
import de.fintracker.service.ExpenseService;
import de.fintracker.service.IncomeService;
import de.fintracker.service.XLSService;
import de.fintracker.util.ZoomAndPanUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.apache.commons.math3.analysis.function.Exp;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

public class ExpenseController extends AbstractTableController<Expense> {

    @FXML private ScrollPane scrollPane;
    @FXML private Group zoomGroup;
    @FXML private StackPane zoomPane;
    @FXML private VBox rootContent;

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
    private final ZoomAndPanUtil zoomAndPanUtil = new ZoomAndPanUtil();

    private final CSVService csvService = new CSVService();
    private final XLSService xlsService = new XLSService();

    @FXML
    protected void initialize(){

        setupTable();
        setupPagination(pagination, expenseTable);
        setupSelectionListener();
        setupFilter();

        rootContent.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        rootContent.setPrefSize(800, 800);

        zoomAndPanUtil.enableZoomAndPan(scrollPane, zoomPane);
    }

    private boolean validateExpenseInput() {
        String amountText = amountField.getText();
        String category = categoryBox.getValue();
        LocalDate date = datePicker.getValue();

        if (amountText == null || amountText.isBlank()) {
            showError("Bitte gib einen Betrag ein.");
            return false;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                showError("Der Betrag muss größer als 0 sein.");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Der Betrag muss eine gültige Zahl sein z.B. ohne Komma aber Punkt.");
            return false;
        }

        if (category == null || category.isBlank()) {
            showError("Bitte wähle eine Kategorie aus.");
            return false;
        }

        if (date == null) {
            showError("Bitte wähle ein Datum aus.");
            return false;
        }

        return true;
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
        File file = chooseSaveFile("expense_download.csv");
        if (file == null) return;

        List<Expense> expenses = expenseTable.getItems();
        csvService.exportCsv(file, expenses);
    }

    @FXML
    private void onImportCSV() {
        File file = chooseFile();
        if (file == null) return;

        List<Expense> expenses = csvService.importCsv(file, Expense::new);
        expenseTable.setItems(FXCollections.observableList(expenses));
    }

    @FXML
    private void onExportXLS() {
        File file = chooseSaveFile("expenses_download.xlxs");
        if (file == null) return;

        List<Expense> expenses = expenseTable.getItems();
        xlsService.exportXls(file, expenses);
    }

    @FXML
    private void onImportXLS() {
        File file = chooseFile();
        if (file == null) return;

        List<Expense> expenses = xlsService.importXls(file, Expense::new);
        expenseTable.setItems(FXCollections.observableList(expenses));
    }

    // --- FILE CHOOSER HELPERS ---
    private File chooseFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Datei auswählen");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV Dateien", "*.csv"),
                new FileChooser.ExtensionFilter("Excel Dateien", "*.xlsx")
        );
        return chooser.showOpenDialog(null);
    }

    private File chooseSaveFile(String defaultName) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Datei speichern");
        chooser.setInitialFileName(defaultName);
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV Dateien", "*.csv"),
                new FileChooser.ExtensionFilter("Excel Dateien", "*.xlsx")
        );
        return chooser.showSaveDialog(null);
    }

    @FXML
    private void saveExpense() {

        if (!validateExpenseInput())
            return;

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

        } catch (RuntimeException e) {
            showError("Es ist ein unerwarteter Fehler bei Ausgaben aufgetreten - Die Ausgabe konnte nicht gespeichert werden.");
        }
    }

    @FXML
    private void deleteExpense() {
        Expense selected = expenseTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Bitte wähle einen Eintrag aus.");
            return;
        }
        try {
            expenseService.deleteExpense(selected.getId());

            updatePageCount(pagination);
            refreshCurrentPage(pagination);

        } catch (RuntimeException e) {
            showError("Die Ausgabe konnte nicht gelöscht werden");
        }
    }

    @FXML
    private void updateExpense() {
        Expense selected = expenseTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Bitte wähle einen Eintrag aus.");
            return;
        }
        try {
            selected.setAmount(Double.parseDouble(amountField.getText()));
            selected.setCategory(categoryBox.getValue());
            selected.setDate(datePicker.getValue());
            selected.setNote(noteArea.getText());

            expenseService.updateExpense(selected, selected.getId());

            refreshCurrentPage(pagination);

        } catch (RuntimeException e) {
            showError("Die Ausgabe konnte nicht aktualisiert werden.");
        }
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
        alert.setTitle("Fehlertitel");
        alert.setHeaderText("Fehler");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
