package de.fintracker.service;

import de.fintracker.model.Income;
import de.fintracker.model.Expense;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CSVService {

    public void exportIncomeCSV(String filePath, List<Income> list) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {

            pw.println("id;amount;category;date;note");

            for (Income i : list) {
                pw.println(i.getId() + ";" +
                        i.getAmount() + ";" +
                        i.getCategory() + ";" +
                        i.getDate() + ";" +
                        i.getNote());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Income> importIncomeCSV(String filePath) {
        List<Income> list = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            // Header überspringen
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");

                int id = Integer.parseInt(parts[0]);
                double amount = Double.parseDouble(parts[1]);
                String category = parts[2];
                LocalDate date = LocalDate.parse(parts[3]);
                String note = parts[4];

                Income income = new Income(id, amount, category, date, note);
                list.add(income);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }


    public void exportExpenseCSV(String filePath, List<Expense> list) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {

            pw.println("id;amount;category;date;note");

            for (Expense i : list) {
                pw.println(i.getId() + ";" +
                        i.getAmount() + ";" +
                        i.getCategory() + ";" +
                        i.getDate() + ";" +
                        i.getNote());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Expense> importExpenseCSV(String filePath) {
        List<Expense> list = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            br.readLine(); // Header überspringen

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");

                int id = Integer.parseInt(parts[0]);
                double amount = Double.parseDouble(parts[1]);
                String category = parts[2];
                LocalDate date = LocalDate.parse(parts[3]);
                String note = parts[4];

                Expense expense = new Expense(id, amount, category, date, note);
                list.add(expense);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
