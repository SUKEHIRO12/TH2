package com.example.studentmanager.dal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.studentmanager.models.WorkLog;
import com.example.studentmanager.utils.SalaryCalculator;

import java.util.ArrayList;
import java.util.List;

public class WorkLogDAO {
    private final DBHelper dbHelper;

    public WorkLogDAO(Context context) {
        this.dbHelper = new DBHelper(context);
    }

    public WorkLog getTodayLog(int employeeId, String workDate) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT id, employeeId, workDate, checkInTime, checkOutTime, totalHours, status FROM WorkLog WHERE employeeId=? AND workDate=?",
                new String[]{String.valueOf(employeeId), workDate});
        WorkLog log = null;
        if (c.moveToFirst()) {
            log = cursorToWorkLog(c);
        }
        c.close();
        return log;
    }

    public String checkIn(int employeeId, String workDate, String checkInTime) {
        WorkLog existing = getTodayLog(employeeId, workDate);
        if (existing != null) {
            return "Bạn đã check-in trong ngày hôm nay rồi!";
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("employeeId", employeeId);
        values.put("workDate", workDate);
        values.put("checkInTime", checkInTime);
        values.put("checkOutTime", (String) null);
        values.put("totalHours", 0.0);
        values.put("status", "INCOMPLETE");

        long id = db.insert("WorkLog", null, values);
        return id != -1 ? "OK" : "Lỗi khi lưu check-in";
    }

    public String checkOut(int employeeId, String workDate, String checkOutTime) {
        WorkLog existing = getTodayLog(employeeId, workDate);
        if (existing == null) {
            return "Chưa có bản ghi check-in trong ngày hôm nay!";
        }
        if ("COMPLETED".equals(existing.getStatus())) {
            return "Bạn đã check-out trong ngày hôm nay rồi!";
        }

        double hours = SalaryCalculator.calculateHours(existing.getCheckInTime(), checkOutTime);
        if (hours < 0) {
            return "Thời gian check-out phải sau thời gian check-in (" + existing.getCheckInTime() + ")!";
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("checkOutTime", checkOutTime);
        values.put("totalHours", hours);
        values.put("status", "COMPLETED");

        int rows = db.update("WorkLog", values, "id=?", new String[]{String.valueOf(existing.getId())});
        return rows > 0 ? "OK" : "Lỗi khi cập nhật check-out";
    }

    public List<WorkLog> getLogsByEmployeeAndMonth(int employeeId, int month, int year) {
        List<WorkLog> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String monthStr = String.format("%02d", month);
        String prefix = year + "-" + monthStr + "-%";

        Cursor c = db.rawQuery(
                "SELECT id, employeeId, workDate, checkInTime, checkOutTime, totalHours, status FROM WorkLog WHERE employeeId=? AND workDate LIKE ? ORDER BY workDate DESC",
                new String[]{String.valueOf(employeeId), prefix});
        while (c.moveToNext()) {
            list.add(cursorToWorkLog(c));
        }
        c.close();
        return list;
    }

    public List<WorkLog> getCompletedLogsForEmployeeAndMonth(int employeeId, int month, int year) {
        List<WorkLog> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String monthStr = String.format("%02d", month);
        String prefix = year + "-" + monthStr + "-%";

        Cursor c = db.rawQuery(
                "SELECT id, employeeId, workDate, checkInTime, checkOutTime, totalHours, status FROM WorkLog WHERE employeeId=? AND workDate LIKE ? AND status='COMPLETED'",
                new String[]{String.valueOf(employeeId), prefix});
        while (c.moveToNext()) {
            list.add(cursorToWorkLog(c));
        }
        c.close();
        return list;
    }

    private WorkLog cursorToWorkLog(Cursor c) {
        return new WorkLog(
                c.getInt(0),
                c.getInt(1),
                c.getString(2),
                c.getString(3),
                c.getString(4),
                c.getDouble(5),
                c.getString(6)
        );
    }
}