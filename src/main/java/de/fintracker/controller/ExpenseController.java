package de.fintracker.controller;

import de.fintracker.model.Expense;
import de.fintracker.model.Income;
import de.fintracker.service.ExpenseService;
import de.fintracker.service.IncomeService;
import de.fintracker.util.UiZoomAndPanUtil;
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

public class ExpenseController extends BaseController {

    @FXML
    private VBox root;

    @FXML
    private ScrollPane scrollPane;

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
    private TableView<Expense> expenseTable;

    @FXML
    private TableColumn<Expense, Number> idColumn;

    @FXML
    private TableColumn<Expense, Double> amountColumn;

    @FXML
    private TableColumn<Expense, String> categoryColumn;

    @FXML
    private TableColumn<Expense, String> dateColumn;

    @FXML
    private TableColumn<Expense, String> noteColumn;

    @FXML
    private Pagination pagination;

    private static final int ROWS_PER_PAGE = 5;


    private final ObservableList<Expense> expenseList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        super.initialize();

        setupTable();
        loadCategories();

//        UiZoomAndPanUtil.enableZoom(root);
//        UiZoomAndPanUtil.enablePanning(scrollPane);

        // Sortierung aktivieren
        idColumn.setSortable(true);
        amountColumn.setSortable(true);
        categoryColumn.setSortable(true);
        dateColumn.setSortable(true);
        noteColumn.setSortable(true);

        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilter(newVal));

        int total = IncomeService.countIncome();
        int pageCount = (int) Math.ceil((double) total / ROWS_PER_PAGE);
        pagination.setPageCount(pageCount);

        pagination.setPageFactory(this::createPage);

        expenseTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
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

        List<Expense> pageData = ExpenseService.getExpensePage(offset, ROWS_PER_PAGE);

        expenseTable.setItems(FXCollections.observableArrayList(pageData));

        return new Label("");
    }

    private void setupTable() {
        idColumn.setCellValueFactory(cell -> cell.getValue().idProperty());
        amountColumn.setCellValueFactory(cell -> cell.getValue().amountProperty().asObject());
        categoryColumn.setCellValueFactory(cell -> cell.getValue().categoryProperty());
        dateColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getDate().toString()));
        noteColumn.setCellValueFactory(cell -> cell.getValue().noteProperty());
    }

    private void loadCategories() {
        categoryBox.getItems().addAll("Essen", "Freizeit", "Miete", "Sonstiges", "Transport");
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

            //pagi aktualisiern
            int total = IncomeService.countIncome();
            int pageCount = (int) Math.ceil((double) total / ROWS_PER_PAGE);
            pagination.setPageCount(pageCount);

            int currentPage = pagination.getCurrentPageIndex();
            pagination.setPageFactory(this::createPage);
            pagination.setCurrentPageIndex(currentPage);

            clearFields();

        } catch (Exception e) {
            showError("Bitte überprüfe deine Eingaben beim Expense.");
        }
    }

    private void loadExpenseList() {
        expenseList.setAll(ExpenseService.getAllExpense());
    }

    @FXML
    private void deleteExpense() {
        Expense selected = expenseTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Bitte wähle einen Eintrag aus.");
            return;
        }

        ExpenseService.deleteExpense(selected.getId());

        int total = ExpenseService.countExpense();
        int pageCount = (int) Math.ceil((double) total / ROWS_PER_PAGE);
        pagination.setPageCount(pageCount);

        int currentPage = pagination.getCurrentPageIndex();
        pagination.setPageFactory(this::createPage);
        pagination.setCurrentPageIndex(currentPage);
    }

    @FXML
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

        int currentPage = pagination.getCurrentPageIndex();
        pagination.setCurrentPageIndex(currentPage);
    }

    private void applyFilter(String filter) {
        if (filter == null || filter.isEmpty()) {
            pagination.setPageFactory(this::createPage);
            return;
        }

        String f = filter.toLowerCase();

        List<Expense> all = ExpenseService.getAllExpense();
        List<Expense> filtered = all.stream()
                .filter(i ->
                        String.valueOf(i.getId()).contains(f) ||
                                String.valueOf(i.getAmount()).contains(f) ||
                                i.getCategory().toLowerCase().contains(f) ||
                                i.getDate().toString().contains(f) ||
                                i.getNote().toLowerCase().contains(f)
                )
                .toList();

        expenseTable.setItems(FXCollections.observableArrayList(filtered));
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
        switchScene("/views/main.fxml");
    }
}
