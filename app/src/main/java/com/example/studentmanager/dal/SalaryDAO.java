package com.example.studentmanager.dal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.studentmanager.models.Employee;
import com.example.studentmanager.models.Salary;
import com.example.studentmanager.models.WorkLog;
import com.example.studentmanager.utils.SalaryCalculator;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for handling Salary calculations and status updates.
 */
public class SalaryDAO {
    private final DBHelper dbHelper;
    private final WorkLogDAO workLogDAO;
    private final EmployeeDAO employeeDAO;

    public SalaryDAO(Context context) {
        this.dbHelper = new DBHelper(context);
        this.workLogDAO = new WorkLogDAO(context);
        this.employeeDAO = new EmployeeDAO(context);
    }

    public Salary calculateAndSaveSalary(int employeeId, int month, int year) {
        Employee emp = employeeDAO.getEmployeeById(employeeId);
        if (emp == null) return null;

        List<WorkLog> completedLogs = workLogDAO.getCompletedLogsForEmployeeAndMonth(employeeId, month, year);
        SalaryCalculator.SalaryResult result = SalaryCalculator.calculateSalary(completedLogs, emp.getHourlyRate());

        Salary existing = getSalaryByEmployeeAndMonth(employeeId, month, year);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        if (existing != null) {
            ContentValues values = new ContentValues();
            values.put("totalHours", result.totalHours);
            values.put("overtimeHours", result.overtimeHours);
            values.put("totalSalary", result.totalSalary);

            db.update("Salary", values, "id=?", new String[]{String.valueOf(existing.getId())});
            existing.setTotalHours(result.totalHours);
            existing.setOvertimeHours(result.overtimeHours);
            existing.setTotalSalary(result.totalSalary);
            return existing;
        } else {
            ContentValues values = new ContentValues();
            values.put("employeeId", employeeId);
            values.put("month", month);
            values.put("year", year);
            values.put("totalHours", result.totalHours);
            values.put("overtimeHours", result.overtimeHours);
            values.put("totalSalary", result.totalSalary);
            values.put("status", "PENDING");

            long id = db.insert("Salary", null, values);
            if (id != -1) {
                return new Salary((int) id, employeeId, month, year, result.totalHours, result.overtimeHours, result.totalSalary, "PENDING");
            }
        }
        return null;
    }

    public void calculateSalaryForAllEmployees(int month, int year) {
        List<Employee> employees = employeeDAO.getAllEmployees();
        for (Employee emp : employees) {
            calculateAndSaveSalary(emp.getId(), month, year);
        }
    }

    public Salary getSalaryByEmployeeAndMonth(int employeeId, int month, int year) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT id, employeeId, month, year, totalHours, overtimeHours, totalSalary, status FROM Salary WHERE employeeId=? AND month=? AND year=?",
                new String[]{String.valueOf(employeeId), String.valueOf(month), String.valueOf(year)});
        Salary sal = null;
        if (c.moveToFirst()) {
            sal = cursorToSalary(c);
        }
        c.close();
        return sal;
    }

    public List<Salary> getAllSalariesByMonthYear(int month, int year) {
        List<Salary> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT id, employeeId, month, year, totalHours, overtimeHours, totalSalary, status FROM Salary WHERE month=? AND year=? ORDER BY id DESC",
                new String[]{String.valueOf(month), String.valueOf(year)});
        while (c.moveToNext()) {
            list.add(cursorToSalary(c));
        }
        c.close();
        return list;
    }

    public boolean updateSalaryStatus(int salaryId, String newStatus) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", newStatus);
        int rows = db.update("Salary", values, "id=?", new String[]{String.valueOf(salaryId)});
        return rows > 0;
    }

    private Salary cursorToSalary(Cursor c) {
        return new Salary(
                c.getInt(0),
                c.getInt(1),
                c.getInt(2),
                c.getInt(3),
                c.getDouble(4),
                c.getDouble(5),
                c.getDouble(6),
                c.getString(7)
        );
    }
}
