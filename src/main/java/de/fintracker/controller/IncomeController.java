package de.fintracker.controller;

import de.fintracker.model.Income;
import de.fintracker.service.CSVService;
import de.fintracker.service.IncomeService;
import de.fintracker.service.XLSService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

public class IncomeController extends AbstractTableController<Income> {

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

    @FXML
    protected void initialize(){
        //später sout wieder rausnehmen: will nur kurz überprüfen:
        System.out.println("INIT OK: IncomeController");

        super.initialize();

        setupTable();
        setupPagination(pagination, incomeTable);
        setupSelectionListener();
        setupFilter();
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
        return IncomeService.getIncomePage(offset, limit);
    }

    @Override
    protected int getTotalItemCount() {
        return IncomeService.countIncome();
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

            List<Income> filtered = IncomeService.getAllIncome().stream()
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
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Dateien", "*.csv"));
        chooser.setInitialFileName("income_export.csv");

        File file = chooser.showSaveDialog(null);
        if (file == null) return;

        List<Income> allIncome = IncomeService.getAllIncome();

        CSVService service = new CSVService();
        service.exportIncomeCSV(file.getAbsolutePath(), allIncome);
    }

    @FXML
    private void onExportXLS() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Dateien", "*.xlsx"));
        chooser.setInitialFileName("income_export.xlsx");

        File file = chooser.showSaveDialog(null);
        if (file == null) return;

        List<Income> allIncome = IncomeService.getAllIncome();

        XLSService service = new XLSService();
        service.exportIncomeXLS(file.getAbsolutePath(), allIncome);
    }

    @FXML
    private void saveIncome() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            String category = categoryBox.getValue();
            LocalDate date = datePicker.getValue();
            String note = noteArea.getText();

            Income income = new Income(amount, category, date, note);
            IncomeService.insertIncome(income);

            updatePageCount(pagination);
            refreshCurrentPage(pagination);
            clearFields();

        } catch (Exception e) {
            showError("Bitte überprüfe deine Eingaben.");
        }
    }

    @FXML
    private void deleteIncome() {
        Income selected = incomeTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Bitte wähle einen Eintrag aus.");
            return;
        }

        IncomeService.deleteIncome(selected.getId());

        updatePageCount(pagination);
        refreshCurrentPage(pagination);
    }

    @FXML
    private void updateIncome() {
        Income selected = incomeTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Bitte wähle einen Eintrag aus.");
            return;
        }

        selected.setAmount(Double.parseDouble(amountField.getText()));
        selected.setCategory(categoryBox.getValue());
        selected.setDate(datePicker.getValue());
        selected.setNote(noteArea.getText());

        IncomeService.updateIncome(selected, selected.getId());

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
