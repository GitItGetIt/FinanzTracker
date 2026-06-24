package de.fintracker.service;

import de.fintracker.model.Expense;
import de.fintracker.model.Income;
import de.fintracker.model.RowConvertible;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class XLSService {

    public <T extends RowConvertible> List<T> importXls (
            File file,
            Supplier<T> factory
    ) {
        List<T> list = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            int rowIndex = 1;

            while (rowIndex <= sheet.getLastRowNum()) {
               Row row = sheet.getRow(rowIndex++);

               if (row == null)
                   continue;

               String[] data = new String[0];

               for (int i = 0; i < 5; i++) {
                   data[i] = row.getCell(i).getStringCellValue();
               }

               T item = factory.get();
               item.fromRow(data);
               list.add(item);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public <T extends RowConvertible> void exportXls (
            File file,
            List<T> items
    ) {
        try (Workbook workbook = new XSSFWorkbook()){

            Sheet sheet = workbook.createSheet("Data");

            // Header setzen
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("Amount");
            header.createCell(2).setCellValue("Category");
            header.createCell(3).setCellValue("Date");
            header.createCell(4).setCellValue("Note");

            int rowIndex = 1;

            for (T item : items ) {
                Row row = sheet.createRow(rowIndex++);
                String[] data = item.toRow();

                for (int i = 0; i < data.length; i++) {
                    row.createCell(i).setCellValue(data[i]);
                }
            }

            for (int col = 0; col < 5; col++) {
                FileOutputStream fos = new FileOutputStream(file);
                workbook.write(fos);
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
