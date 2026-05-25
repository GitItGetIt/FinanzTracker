package de.fintracker.controller;

import de.fintracker.model.Income;
import de.fintracker.service.IncomeService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class IncomeController implements Navigatable {


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
    private TextField searchField;

    @FXML
    private TableView<Income> incomeTable;

    @FXML
    private TableColumn<Income, Number> idColumn;

    @FXML
    private TableColumn<Income, Double> amountColumn;

    @FXML
    private TableColumn<Income, String> categoryColumn;

    @FXML
    private TableColumn<Income, String> dateColumn;

    @FXML
    private TableColumn<Income, String> noteColumn;

    @FXML
    private Pagination pagination;

    private static final int ROWS_PER_PAGE = 5;


    private ObservableList<Income> incomeList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTable();
        loadCategories();

        // Sortierung aktivieren
        idColumn.setSortable(true);
        amountColumn.setSortable(true);
        categoryColumn.setSortable(true);
        dateColumn.setSortable(true);
        noteColumn.setSortable(true);

        // Optional: Standard-Sortierung (z. B. nach Datum)
        // incomeTable.getSortOrder().add(dateColumn);

        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilter(newVal));

        int total = IncomeService.countIncome();
        int pageCount = (int) Math.ceil((double) total / ROWS_PER_PAGE);
        pagination.setPageCount(pageCount);

        pagination.setPageFactory(this::createPage);

        incomeTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                amountField.setText(String.valueOf(newVal.getAmount()));
                categoryBox.setValue(newVal.getCategory());
                datePicker.setValue(newVal.getDate());
                noteArea.setText(newVal.getNote());
            }
        });
    }

    private Node createPage(int pageIndex) {
        int offset = pageIndex * ROWS_PER_PAGE;

        List<Income> pageData = IncomeService.getIncomePage(offset, ROWS_PER_PAGE);

        ObservableList<Income> data = FXCollections.observableArrayList(pageData);
        incomeTable.setItems(data);

        return new VBox(incomeTable);
    }

    private void setupTable() {
        idColumn.setCellValueFactory( cell -> cell.getValue().idProperty());
        amountColumn.setCellValueFactory(cell -> cell.getValue().amountProperty().asObject());
        categoryColumn.setCellValueFactory(cell -> cell.getValue().categoryProperty());
        dateColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getDate().toString()));
        noteColumn.setCellValueFactory(cell -> cell.getValue().noteProperty());
    }

    private void loadCategories() {
        categoryBox.getItems().addAll("Bonus", "Gehalt", "Geschenk", "Sonstiges");
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

            // Pagintn aktualisiern
            int total = IncomeService.countIncome();
            int pageCount = (int) Math.ceil((double) total / ROWS_PER_PAGE);
            pagination.setPageCount(pageCount);

            int currentPage = pagination.getCurrentPageIndex();
            pagination.setPageFactory(this::createPage);
            pagination.setCurrentPageIndex(currentPage);

            clearFields();

        } catch (Exception e) {
            showError("Bitte überprüfe deine Eingaben im Imcome.");
        }
    }

    private void loadIncomeList() {
        incomeList.setAll(IncomeService.getAllIncome());
    }

    @FXML
    private void deleteIncome() {
        Income selected = incomeTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Bitte einen Eintrag aus von Incometbl.");
            return;
        }

        IncomeService.deleteIncome(selected.getId());

        int total = IncomeService.countIncome();
        int pageCount = (int) Math.ceil((double) total / ROWS_PER_PAGE);
        pagination.setPageCount(pageCount);

        int currentPage = pagination.getCurrentPageIndex();
        pagination.setPageFactory(this::createPage);
        pagination.setCurrentPageIndex(currentPage);
    }

    @FXML
    private void updateIncome() {
        Income selected = incomeTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Bitte wähle einen Eintrag aus Income.....");
            return;
        }

        selected.setAmount(Double.parseDouble(amountField.getText()));
        selected.setCategory(categoryBox.getValue());
        selected.setDate(datePicker.getValue());
        selected.setNote(noteArea.getText());

        IncomeService.updateIncome(selected, selected.getId());

        int currentPage = pagination.getCurrentPageIndex();
        pagination.setPageFactory(this::createPage);
        pagination.setCurrentPageIndex(currentPage);
    }

    private void applyFilter(String filter) {
        if (filter == null || filter.isEmpty()) {
            pagination.setPageFactory(this::createPage);
            return;
        }

        String f = filter.toLowerCase();

        List<Income> all = IncomeService.getAllIncome();
        List<Income> filtered = all.stream()
                .filter(i ->
                        String.valueOf(i.getId()).contains(f) ||
                                String.valueOf(i.getAmount()).contains(f) ||
                                i.getCategory().toLowerCase().contains(f) ||
                                i.getDate().toString().contains(f) ||
                                i.getNote().toLowerCase().contains(f)
                )
                .toList();

        incomeTable.setItems(FXCollections.observableArrayList(filtered));
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
            scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());

            stage.setScene(scene);

            MainController controller = loader.getController();
            controller.setStage(stage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
