package de.fintracker.controller;

import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableView;
import java.util.List;

public abstract class AbstractTableController<T> extends BaseController{

    protected int ROWS_PER_PAGE = 5;

    protected void setupPagination(Pagination pagination, TableView<T> table) {
        pagination.setPageFactory(pageIndex -> createPage(pageIndex, table));
        updatePageCount(pagination);
    }

    protected void updatePageCount(Pagination pagination) {
        int total = getTotalItemCount();
        int pageCount = (int) Math.ceil((double) total / ROWS_PER_PAGE);
        pagination.setPageCount(pageCount);
    }

    protected void refreshCurrentPage(Pagination pagination) {
        int current = pagination.getCurrentPageIndex();
        pagination.setCurrentPageIndex(current);
    }

    protected Node createPage(int pageIndex, TableView<T> table) {
        int offset = pageIndex * ROWS_PER_PAGE;
        List<T> pageData = loadPageData(offset, ROWS_PER_PAGE);
        table.setItems(FXCollections.observableArrayList(pageData));
        return new Label(""); // Dummy Node
    }

    protected abstract void setupTable();
    protected abstract List<T> loadPageData(int offset, int limit);
    protected abstract int getTotalItemCount();
    protected abstract void setupSelectionListener();
    protected abstract void setupFilter();
}
