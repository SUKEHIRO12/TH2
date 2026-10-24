package com.example.studentmanager.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studentmanager.R;
import com.example.studentmanager.dal.WorkLogDAO;
import com.example.studentmanager.models.WorkLog;
import com.example.studentmanager.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AttendanceActivity extends AppCompatActivity {
    private TextView tvCurrentDateTime, tvTodayStatus;
    private Button btnCheckIn, btnCheckOut;

    private WorkLogDAO workLogDAO;
    private SessionManager session;
    private int employeeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        session = new SessionManager(this);
        employeeId = session.getEmployeeId();
        workLogDAO = new WorkLogDAO(this);

        tvCurrentDateTime = findViewById(R.id.tvCurrentDateTime);
        tvTodayStatus = findViewById(R.id.tvTodayStatus);
        btnCheckIn = findViewById(R.id.btnCheckIn);
        btnCheckOut = findViewById(R.id.btnCheckOut);

        updateDateTimeAndStatus();

        btnCheckIn.setOnClickListener(v -> doCheckIn());
        btnCheckOut.setOnClickListener(v -> doCheckOut());
    }

    private String getTodayDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    private String getCurrentTime() {
        return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
    }

    private void updateDateTimeAndStatus() {
        String today = getTodayDate();
        String nowTime = getCurrentTime();
        tvCurrentDateTime.setText("Hôm nay: " + today + " | " + nowTime);

        WorkLog todayLog = workLogDAO.getTodayLog(employeeId, today);
        if (todayLog == null) {
            tvTodayStatus.setText("Chưa check-in ngày hôm nay.");
            btnCheckIn.setEnabled(true);
            btnCheckOut.setEnabled(false);
        } else if ("INCOMPLETE".equals(todayLog.getStatus())) {
            tvTodayStatus.setText("Đã check-in lúc " + todayLog.getCheckInTime() + ". Đang trong ca làm việc.");
            btnCheckIn.setEnabled(false);
            btnCheckOut.setEnabled(true);
        } else {
            tvTodayStatus.setText("Đã hoàn thành ca làm việc.\nCheck-in: " + todayLog.getCheckInTime() + "\nCheck-out: " + todayLog.getCheckOutTime() + "\nTổng số giờ: " + String.format(Locale.getDefault(), "%.1f", todayLog.getTotalHours()) + "h");
            btnCheckIn.setEnabled(false);
            btnCheckOut.setEnabled(false);
        }
    }

    private void doCheckIn() {
        String today = getTodayDate();
        String time = getCurrentTime();
        String result = workLogDAO.checkIn(employeeId, today, time);

        if ("OK".equals(result)) {
            Toast.makeText(this, "Check-in thành công lúc " + time, Toast.LENGTH_SHORT).show();
            updateDateTimeAndStatus();
        } else {
            Toast.makeText(this, result, Toast.LENGTH_LONG).show();
        }
    }

    private void doCheckOut() {
        String today = getTodayDate();
        String time = getCurrentTime();
        String result = workLogDAO.checkOut(employeeId, today, time);

        if ("OK".equals(result)) {
            Toast.makeText(this, "Check-out thành công lúc " + time, Toast.LENGTH_SHORT).show();
            updateDateTimeAndStatus();
        } else {
            Toast.makeText(this, result, Toast.LENGTH_LONG).show();
        }
    }
}