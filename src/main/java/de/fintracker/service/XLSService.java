package de.fintracker.service;

import de.fintracker.model.Expense;
import de.fintracker.model.Income;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class XLSService {

    public void exportIncomeXLS(String filePath, List<Income> list) {
        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("Income");

            // Header
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("Amount");
            header.createCell(2).setCellValue("Category");
            header.createCell(3).setCellValue("Date");
            header.createCell(4).setCellValue("Note");

            // Data rows
            int rowIndex = 1;
            for (Income i : list) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(i.getId());
                row.createCell(1).setCellValue(i.getAmount());
                row.createCell(2).setCellValue(i.getCategory());
                row.createCell(3).setCellValue(i.getDate().toString());
                row.createCell(4).setCellValue(i.getNote());
            }

            // Autosize columns
            for (int col = 0; col < 5; col++) {
                sheet.autoSizeColumn(col);
            }

            FileOutputStream fos = new FileOutputStream(filePath);
            workbook.write(fos);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Income> importIncomeXLS(String filePath) {
        List<Income> list = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            // Header überspringen
            int rowIndex = 1;

            while (rowIndex <= sheet.getLastRowNum()) {
                Row row = sheet.getRow(rowIndex++);

                if (row == null) continue;

                int id = (int) row.getCell(0).getNumericCellValue();
                double amount = row.getCell(1).getNumericCellValue();
                String category = row.getCell(2).getStringCellValue();
                LocalDate date = LocalDate.parse(row.getCell(3).getStringCellValue());
                String note = row.getCell(4).getStringCellValue();

                Income income = new Income(id, amount, category, date, note);
                list.add(income);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public void exportExpenseXLS(String filePath, List<Expense> list) {
        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("Expense");

            // Header
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("Amount");
            header.createCell(2).setCellValue("Category");
            header.createCell(3).setCellValue("Date");
            header.createCell(4).setCellValue("Note");

            // Data rows
            int rowIndex = 1;
            for (Expense i : list) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(i.getId());
                row.createCell(1).setCellValue(i.getAmount());
                row.createCell(2).setCellValue(i.getCategory());
                row.createCell(3).setCellValue(i.getDate().toString());
                row.createCell(4).setCellValue(i.getNote());
            }

            // Autosize columns
            for (int col = 0; col < 5; col++) {
                sheet.autoSizeColumn(col);
            }

            FileOutputStream fos = new FileOutputStream(filePath);
            workbook.write(fos);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Expense> importExpenseXLS(String filePath) {
        List<Expense> list = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            // Header überspringen
            int rowIndex = 1;

            while (rowIndex <= sheet.getLastRowNum()) {
                Row row = sheet.getRow(rowIndex++);

                if (row == null) continue;

                int id = (int) row.getCell(0).getNumericCellValue();
                double amount = row.getCell(1).getNumericCellValue();
                String category = row.getCell(2).getStringCellValue();
                LocalDate date = LocalDate.parse(row.getCell(3).getStringCellValue());
                String note = row.getCell(4).getStringCellValue();

                Expense expense = new Expense(id, amount, category, date, note);
                list.add(expense);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
