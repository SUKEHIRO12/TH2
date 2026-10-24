package com.example.studentmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studentmanager.activities.AttendanceActivity;
import com.example.studentmanager.activities.EmployeeActivity;
import com.example.studentmanager.activities.HistoryActivity;
import com.example.studentmanager.activities.LoginActivity;
import com.example.studentmanager.activities.MySalaryActivity;
import com.example.studentmanager.activities.SalaryAdminActivity;
import com.example.studentmanager.utils.SessionManager;

public class MainActivity extends AppCompatActivity {
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new SessionManager(this);

        TextView tvWelcome = findViewById(R.id.tvWelcome);
        TextView tvRole = findViewById(R.id.tvRole);
        LinearLayout layoutAdminMenu = findViewById(R.id.layoutAdminMenu);
        LinearLayout layoutEmployeeMenu = findViewById(R.id.layoutEmployeeMenu);

        Button btnManageEmployee = findViewById(R.id.btnManageEmployee);
        Button btnManageSalary = findViewById(R.id.btnManageSalary);

        Button btnAttendance = findViewById(R.id.btnAttendance);
        Button btnHistory = findViewById(R.id.btnHistory);
        Button btnMySalary = findViewById(R.id.btnMySalary);
        Button btnLogout = findViewById(R.id.btnLogout);

        tvWelcome.setText("Xin chào: " + session.getUsername());
        tvRole.setText("Vai trò: " + session.getRole().toUpperCase());

        if (session.isAdmin()) {
            layoutAdminMenu.setVisibility(View.VISIBLE);
            layoutEmployeeMenu.setVisibility(View.GONE);
        } else {
            layoutAdminMenu.setVisibility(View.GONE);
            layoutEmployeeMenu.setVisibility(View.VISIBLE);
        }

        btnManageEmployee.setOnClickListener(v ->
                startActivity(new Intent(this, EmployeeActivity.class)));

        btnManageSalary.setOnClickListener(v ->
                startActivity(new Intent(this, SalaryAdminActivity.class)));

        btnAttendance.setOnClickListener(v ->
                startActivity(new Intent(this, AttendanceActivity.class)));

        btnHistory.setOnClickListener(v ->
                startActivity(new Intent(this, HistoryActivity.class)));

        btnMySalary.setOnClickListener(v ->
                startActivity(new Intent(this, MySalaryActivity.class)));

        btnLogout.setOnClickListener(v -> {
            session.logout();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}
