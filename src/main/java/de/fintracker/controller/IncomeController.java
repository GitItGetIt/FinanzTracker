package de.fintracker.controller;

import de.fintracker.model.Income;
import de.fintracker.service.CSVService;
import de.fintracker.service.IncomeService;
import de.fintracker.service.XLSService;
import de.fintracker.util.FileChooserUtil;
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

import java.io.File;
import java.time.LocalDate;
import java.util.List;

public class IncomeController extends AbstractTableController<Income> {

    @FXML private ScrollPane scrollPane;
    @FXML private Group zoomGroup;
    @FXML private StackPane zoomPane;
    @FXML private VBox rootContent;

    @FXML private TableView<Income> incomeTable;
    @FXML private Pagination pagination;

    @FXML private TableColumn<Income, Number> idColumn;
    @FXML private TableColumn<Income, Double> amountColumn;
    @FXML private TableColumn<Income, String> categoryColumn;
    @FXML private TableColumn<Income, String> dateColumn;
    @FXML private TableColumn<Income, String> noteColumn;

    @FXML private TextField amountField;
    @FXML private ComboBox<String> categoryBox;
    @FXML private DatePicker datePicker;
    @FXML private TextArea noteArea;
    @FXML private TextField searchField;

    private final IncomeService incomeService = new IncomeService();
    private final ZoomAndPanUtil zoomAndPanUtil = new ZoomAndPanUtil();

    private final CSVService csvService = new CSVService();
    private final XLSService xlsService = new XLSService();

    @FXML
    public void initialize(){

        setupTable();
        setupPagination(pagination, incomeTable);
        setupSelectionListener();
        setupFilter();

        rootContent.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        rootContent.setPrefSize(800, 800);

        zoomAndPanUtil.enableZoomAndPan(scrollPane, zoomPane);
    }

    private boolean validateIncomeInput() {
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
            if (amount <= 0){
                showError("Der Betrag muss größer als 0 sein");
                return false;
            }
        } catch (NumberFormatException d) {
            showError("Der Betrag muss eine gültige Zahl sein, z.B. mit Punkt getrennt statt Komma");
            return false;
        }

        if (category == null || category.isBlank()) {
            showError("Bitte eine Kategorie auswählen");
            return false;
        }

        if (date == null) {
            showError("Bitte Datum auswählen");
            return false;
        }
        return true;
    }

    @Override
    protected void setupTable() {

        idColumn.setCellValueFactory(cell -> cell.getValue().idProperty());
        amountColumn.setCellValueFactory(cell -> cell.getValue().amountProperty().asObject());
        categoryColumn.setCellValueFactory(cell -> cell.getValue().categoryProperty());
        dateColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDate().toString()));
        noteColumn.setCellValueFactory(cell -> cell.getValue().noteProperty());

        categoryBox.getItems().addAll("Gehalt", "Bonus", "Geschenk", "Sonstiges");
    }

    @Override
    protected List<Income> loadPageData(int offset, int limit) {
        return incomeService.getIncomePage(offset, limit);
    }

    @Override
    protected int getTotalItemCount() {
        return incomeService.countIncome();
    }

    @Override
    protected void setupSelectionListener() {
        incomeTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
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
                pagination.setPageFactory(pageIndex -> createPage(pageIndex, incomeTable));
                return;
            }

            String f = newVal.toLowerCase();

            List<Income> filtered = incomeService.getAllIncome().stream()
                    .filter(i ->
                            String.valueOf(i.getId()).contains(f) ||
                            String.valueOf(i.getAmount()).contains(f) ||
                            i.getCategory().toLowerCase().contains(f) ||
                            i.getDate().toString().contains(f) ||
                            i.getNote().toLowerCase().contains(f)
                    )
                    .toList();

            incomeTable.setItems(FXCollections.observableArrayList(filtered));
        });
    }

    //filechooser f export
    @FXML
    private void onExportCSV() {
        File file = FileChooserUtil.saveCSV("incomes_download.csv");
        if (file == null) return;

        List<Income> incomes = incomeTable.getItems();
        csvService.exportCsv(file, incomes);
    }

    @FXML
    private void onImportCSV() {
      File file = FileChooserUtil.openCSV();
      if (file == null) return;

      List<Income> incomes = csvService.importCsv(file, Income::new);
      incomeTable.setItems(FXCollections.observableList(incomes));

    }

    @FXML
    private void onExportXLS() {
        File file = FileChooserUtil.saveXLS("incomes_download.xlxs");
        if (file == null) return;

        List<Income> incomes = incomeTable.getItems();
        xlsService.exportXls(file, incomes);
    }

    @FXML
    private void onImportXLS() {
        File file = FileChooserUtil.openXLS();
        if (file == null) return;

        List<Income> incomes = xlsService.importXls(file, Income::new);
        incomeTable.setItems(FXCollections.observableList(incomes));
    }

    @FXML
    private void saveIncome() {

        if (!validateIncomeInput())
            return;

        try {
            double amount = Double.parseDouble(amountField.getText());
            String category = categoryBox.getValue();
            LocalDate date = datePicker.getValue();
            String note = noteArea.getText();

            Income income = new Income(amount, category, date, note);
            incomeService.insertIncome(income);

            updatePageCount(pagination);
            refreshCurrentPage(pagination);
            clearFields();

        } catch (RuntimeException e) {
            showError("Ein unerwarteter Fehler bei Einnahmen aufgetreten - konnte nicht gespeichert werden");
        }
    }

    @FXML
    private void deleteIncome() {
        Income selected = incomeTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Bitte wähle einen Eintrag aus.");
            return;
        }

        try {
            incomeService.deleteIncome(selected.getId());

            updatePageCount(pagination);
            refreshCurrentPage(pagination);
        } catch (RuntimeException e) {
            showError("Die Ausgabe konnte nicht gelöscht werden");
        }
    }

    @FXML
    private void updateIncome() {
        Income selected = incomeTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Bitte wähle einen Eintrag aus.");
            return;
        }

        try {
            selected.setAmount(Double.parseDouble(amountField.getText()));
            selected.setCategory(categoryBox.getValue());
            selected.setDate(datePicker.getValue());
            selected.setNote(noteArea.getText());

            incomeService.updateIncome(selected, selected.getId());

            refreshCurrentPage(pagination);
        } catch (RuntimeException e) {
            showError("Die Ausgabe konnte nicht aktualisiert werden");
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
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
