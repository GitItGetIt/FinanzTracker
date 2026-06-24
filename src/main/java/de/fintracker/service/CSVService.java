package de.fintracker.service;

import de.fintracker.model.Income;
import de.fintracker.model.Expense;
import de.fintracker.model.RowConvertible;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class CSVService {

    public <T extends RowConvertible> List<T> importCsv(
            File file,
            Supplier<T> factory
    ) {
        List<T> list = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            br.readLine(); // Header überspringen
            String line;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;

                String[] parts = line.split(";");

                T item = factory.get();
                item.fromRow(parts);
                list.add(item);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public <T extends RowConvertible> void exportCsv (
            File file,
            List<T> items
    ) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {

            pw.println("id;amount;category;date;note");     // Header generisch

            for(T item : items) {
                pw.println(String.join(";",item.toRow()));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



//    public void exportIncomeCSV(String filePath, List<Income> list) {
//        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
//
//            pw.println("id;amount;category;date;note");
//
//            for (Income i : list) {
//                pw.println(i.getId() + ";" +
//                        i.getAmount() + ";" +
//                        i.getCategory() + ";" +
//                        i.getDate() + ";" +
//                        i.getNote());
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public List<Income> importIncomeCSV(String filePath) {
//        List<Income> list = new ArrayList<>();
//
//        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
//            String line;
//
//            // Header überspringen
//            br.readLine();
//
//            while ((line = br.readLine()) != null) {
//
//                if (line.trim().isEmpty())
//                    continue; //leere Zeilen überspring
//
//                String[] parts = line.split(";");
//
//                if (parts.length < 5)
//                    continue; //unvollständige Zeilen überspring
//
//                int id = Integer.parseInt(parts[0]);
//                double amount = Double.parseDouble(parts[1]);
//                String category = parts[2];
//                LocalDate date = LocalDate.parse(parts[3]);
//                String note = parts[4];
//
//                Income income = new Income(amount, category, date, note);
//                list.add(income);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return list;
//    }
//
//    public void exportExpenseCSV(String filePath, List<Expense> list) {
//        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
//
//            pw.println("id;amount;category;date;note");
//
//            for (Expense i : list) {
//                pw.println(i.getId() + ";" +
//                        i.getAmount() + ";" +
//                        i.getCategory() + ";" +
//                        i.getDate() + ";" +
//                        i.getNote());
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public List<Expense> importExpenseCSV(String filePath) {
//        List<Expense> list = new ArrayList<>();
//
//        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
//            String line;
//
//            br.readLine(); // Header überspringen
//
//            while ((line = br.readLine()) != null) {
//
//                if (line.trim().isEmpty())
//                    continue; //leere Zeilen überspring
//
//                String[] parts = line.split(";");
//
//                if (parts.length < 5)
//                    continue; //unvollständige Zeilen überspring
//
//                int id = Integer.parseInt(parts[0]);
//                double amount = Double.parseDouble(parts[1]);
//                String category = parts[2];
//                LocalDate date = LocalDate.parse(parts[3]);
//                String note = parts[4];
//
//                Expense expense = new Expense(amount, category, date, note);
//                list.add(expense);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return list;
//    }
}
