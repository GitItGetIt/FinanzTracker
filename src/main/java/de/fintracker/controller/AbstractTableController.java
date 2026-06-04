package de.fintracker.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableView;
import java.util.List;

public abstract class AbstractTableController<T> extends BaseController{

    protected Pagination pagination;
    protected TableView<T> table;
    protected int ROWS_PER_PAGE = 5;

    @FXML
    protected void initialize(){
        //später sout wieder rausnehmen: will nur kurz überprüfen:
        System.out.println("INIT OK: " + this.getClass().getSimpleName());

        super.initialize();
        setupTable();
        setupPagination();
        setupSelectionListener();
        setupFilter();
    }

    private void setupPagination() {
        pagination.setPageFactory(this::createPage);
        updatePageCount();
    }

    protected void updatePageCount() {
        int total = getTotalItemCount();
        int pageCount = (int) Math.ceil((double) total / ROWS_PER_PAGE);
        pagination.setPageCount(pageCount);
    }

    protected void refreshCurrentPage() {
        int current = pagination.getCurrentPageIndex();
        pagination.setCurrentPageIndex(current);
    }

    protected Node createPage(int pageIndex) {
        int offset = pageIndex * ROWS_PER_PAGE;
        List<T> pageData = loadPageData(offset, ROWS_PER_PAGE);
        table.setItems(FXCollections.observableArrayList(pageData));
        return new Label(""); // Dummy Node
    }

    // f Overrides in Income/ExpensCtrlrn
    protected abstract void setupTable();
    protected abstract List<T> loadPageData(int offset, int limit);
    protected abstract int getTotalItemCount();
    protected abstract void setupSelectionListener();
    protected abstract void setupFilter();
}
