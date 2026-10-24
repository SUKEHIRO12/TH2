package com.example.studentmanager.dal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.studentmanager.models.Account;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "employeemanager.db";
    private static final int DB_VERSION = 2;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Account(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL, " +
                "role TEXT NOT NULL)");

        db.execSQL("CREATE TABLE Employee(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "code TEXT UNIQUE NOT NULL, " +
                "fullname TEXT, email TEXT, phone TEXT, " +
                "hourlyRate REAL, " +
                "accountId INTEGER)");

        db.execSQL("CREATE TABLE WorkLog(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "employeeId INTEGER, " +
                "workDate TEXT, " +
                "checkInTime TEXT, " +
                "checkOutTime TEXT, " +
                "totalHours REAL, " +
                "status TEXT)");

        db.execSQL("CREATE TABLE Salary(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "employeeId INTEGER, " +
                "month INTEGER, " +
                "year INTEGER, " +
                "totalHours REAL, " +
                "overtimeHours REAL, " +
                "totalSalary REAL, " +
                "status TEXT)");

        seed(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Salary");
        db.execSQL("DROP TABLE IF EXISTS WorkLog");
        db.execSQL("DROP TABLE IF EXISTS Employee");
        db.execSQL("DROP TABLE IF EXISTS Account");
        onCreate(db);
    }

    private void seed(SQLiteDatabase db) {
        // Seed Admin account
        ContentValues adminAcc = new ContentValues();
        adminAcc.put("username", "admin");
        adminAcc.put("password", "123456");
        adminAcc.put("role", "admin");
        db.insert("Account", null, adminAcc);

        // Seed Employee 1 account & profile
        ContentValues emp1Acc = new ContentValues();
        emp1Acc.put("username", "emp01");
        emp1Acc.put("password", "123456");
        emp1Acc.put("role", "employee");
        long emp1AccId = db.insert("Account", null, emp1Acc);

        ContentValues emp1 = new ContentValues();
        emp1.put("code", "EMP01");
        emp1.put("fullname", "Nguyen Van An");
        emp1.put("email", "an.nguyen@company.com");
        emp1.put("phone", "0901234567");
        emp1.put("hourlyRate", 50000.0);
        emp1.put("accountId", emp1AccId);
        long emp1Id = db.insert("Employee", null, emp1);

        // Seed Employee 2 account & profile
        ContentValues emp2Acc = new ContentValues();
        emp2Acc.put("username", "emp02");
        emp2Acc.put("password", "123456");
        emp2Acc.put("role", "employee");
        long emp2AccId = db.insert("Account", null, emp2Acc);

        ContentValues emp2 = new ContentValues();
        emp2.put("code", "EMP02");
        emp2.put("fullname", "Tran Thi Binh");
        emp2.put("email", "binh.tran@company.com");
        emp2.put("phone", "0987654321");
        emp2.put("hourlyRate", 60000.0);
        emp2.put("accountId", emp2AccId);
        long emp2Id = db.insert("Employee", null, emp2);

        // Seed some sample completed WorkLogs for testing salary calculation
        ContentValues log1 = new ContentValues();
        log1.put("employeeId", emp1Id);
        log1.put("workDate", "2026-10-20");
        log1.put("checkInTime", "08:00");
        log1.put("checkOutTime", "17:00");
        log1.put("totalHours", 9.0);
        log1.put("status", "COMPLETED");
        db.insert("WorkLog", null, log1);

        ContentValues log2 = new ContentValues();
        log2.put("employeeId", emp1Id);
        log2.put("workDate", "2026-10-21");
        log2.put("checkInTime", "08:00");
        log2.put("checkOutTime", "16:00");
        log2.put("totalHours", 8.0);
        log2.put("status", "COMPLETED");
        db.insert("WorkLog", null, log2);
    }

    public Account login(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT id, username, password, role FROM Account WHERE username=? AND password=?",
                new String[]{username, password});
        Account acc = null;
        if (c.moveToFirst()) {
            acc = new Account(c.getInt(0), c.getString(1), c.getString(2), c.getString(3));
        }
        c.close();
        return acc;
    }
}
