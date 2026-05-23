package de.fintracker.controller;

import de.fintracker.model.Income;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.time.LocalDate;

public class IncomeController implements Navigatable {


    private Stage stage;

    @Override
    public void setStage(Stage stage){
        this.stage = stage;
    }

    @FXML
    public TableColumn<Income, Number> idColumn;

    @FXML
    private TextField amountField;

    @FXML
    private ComboBox<String> categoryBox;

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextArea noteArea;

    @FXML
    private TableView<Income> incomeTable;

    @FXML
    private TableColumn<Income, Double> amountColumn;

    @FXML
    private TableColumn<Income, String> categoryColumn;

    @FXML
    private TableColumn<Income, LocalDate> dateColumn;

    @FXML
    private TableColumn<Income, String> noteColumn;


    private ObservableList<Income> incomeList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTable();
        loadIncomeList();
        loadCategories();
    }

    @FXML
    private void saveIncome() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            String category = categoryBox.getValue();
            LocalDate date = datePicker.getValue();
            String note = noteArea.getText();

            Income income = new Income(amount, category, date, note);
            incomeList.add(income);

            clearFields();

        } catch (Exception e) {
            showError("Bitte überprüfe deine Eingaben.");
        }
    }

    private void setupTable() {
        idColumn.setCellValueFactory( cell -> cell.getValue().idProperty());
        amountColumn.setCellValueFactory(cell -> cell.getValue().amountProperty().asObject());
        categoryColumn.setCellValueFactory(cell -> cell.getValue().categoryProperty());
        dateColumn.setCellValueFactory(cell -> cell.getValue().dateProperty());
        noteColumn.setCellValueFactory(cell -> cell.getValue().noteProperty());

        incomeTable.setItems(incomeList);
    }

    private void loadCategories() {
        categoryBox.getItems().addAll("Gehalt", "Bonus", "Geschenk", "Sonstiges");
    }

    private void loadIncomeList() {
        // später: DB laden
        // jetzt: leer lassen
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
        // später Navigation einbauen
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
