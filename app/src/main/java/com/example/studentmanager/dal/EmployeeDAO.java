package com.example.studentmanager.dal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.studentmanager.models.Employee;

import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {
    private final DBHelper dbHelper;

    public EmployeeDAO(Context context) {
        this.dbHelper = new DBHelper(context);
    }

    public boolean insertEmployeeWithAccount(Employee emp, String username, String password) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues accValues = new ContentValues();
            accValues.put("username", username);
            accValues.put("password", password);
            accValues.put("role", "employee");

            long accountId = db.insert("Account", null, accValues);
            if (accountId == -1) {
                return false;
            }

            ContentValues empValues = new ContentValues();
            empValues.put("code", emp.getCode());
            empValues.put("fullname", emp.getFullname());
            empValues.put("email", emp.getEmail());
            empValues.put("phone", emp.getPhone());
            empValues.put("hourlyRate", emp.getHourlyRate());
            empValues.put("accountId", accountId);

            long empId = db.insert("Employee", null, empValues);
            if (empId == -1) {
                return false;
            }

            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.endTransaction();
        }
    }

    public boolean updateEmployee(Employee emp) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("code", emp.getCode());
        values.put("fullname", emp.getFullname());
        values.put("email", emp.getEmail());
        values.put("phone", emp.getPhone());
        values.put("hourlyRate", emp.getHourlyRate());

        int rows = db.update("Employee", values, "id=?", new String[]{String.valueOf(emp.getId())});
        return rows > 0;
    }

    public boolean deleteEmployee(int employeeId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Employee emp = getEmployeeById(employeeId);
        if (emp == null) return false;

        db.beginTransaction();
        try {
            db.delete("WorkLog", "employeeId=?", new String[]{String.valueOf(employeeId)});
            db.delete("Salary", "employeeId=?", new String[]{String.valueOf(employeeId)});
            db.delete("Employee", "id=?", new String[]{String.valueOf(employeeId)});
            db.delete("Account", "id=?", new String[]{String.valueOf(emp.getAccountId())});
            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.endTransaction();
        }
    }

    public List<Employee> getAllEmployees() {
        List<Employee> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT id, code, fullname, email, phone, hourlyRate, accountId FROM Employee ORDER BY id DESC", null);
        while (c.moveToNext()) {
            list.add(cursorToEmployee(c));
        }
        c.close();
        return list;
    }

    public List<Employee> searchEmployees(String keyword) {
        List<Employee> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "%" + keyword + "%";
        Cursor c = db.rawQuery("SELECT id, code, fullname, email, phone, hourlyRate, accountId FROM Employee WHERE code LIKE ? OR fullname LIKE ? OR email LIKE ? OR phone LIKE ? ORDER BY id DESC",
                new String[]{query, query, query, query});
        while (c.moveToNext()) {
            list.add(cursorToEmployee(c));
        }
        c.close();
        return list;
    }

    public Employee getEmployeeById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT id, code, fullname, email, phone, hourlyRate, accountId FROM Employee WHERE id=?", new String[]{String.valueOf(id)});
        Employee emp = null;
        if (c.moveToFirst()) {
            emp = cursorToEmployee(c);
        }
        c.close();
        return emp;
    }

    public Employee getEmployeeByAccountId(int accountId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT id, code, fullname, email, phone, hourlyRate, accountId FROM Employee WHERE accountId=?", new String[]{String.valueOf(accountId)});
        Employee emp = null;
        if (c.moveToFirst()) {
            emp = cursorToEmployee(c);
        }
        c.close();
        return emp;
    }

    private Employee cursorToEmployee(Cursor c) {
        return new Employee(
                c.getInt(0),
                c.getString(1),
                c.getString(2),
                c.getString(3),
                c.getString(4),
                c.getDouble(5),
                c.getInt(6)
        );
    }
}
