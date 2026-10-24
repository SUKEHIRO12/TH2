package com.example.studentmanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studentmanager.MainActivity;
import com.example.studentmanager.R;
import com.example.studentmanager.dal.DBHelper;
import com.example.studentmanager.dal.EmployeeDAO;
import com.example.studentmanager.models.Account;
import com.example.studentmanager.models.Employee;
import com.example.studentmanager.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {
    private EditText edtUsername, edtPassword;
    private Button btnLogin;
    private DBHelper db;
    private EmployeeDAO employeeDAO;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new SessionManager(this);
        if (session.isLoggedIn()) {
            goToMain();
            return;
        }

        setContentView(R.layout.activity_login);
        db = new DBHelper(this);
        employeeDAO = new EmployeeDAO(this);

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> doLogin());
    }

    private void doLogin() {
        String u = edtUsername.getText().toString().trim();
        String p = edtPassword.getText().toString().trim();

        if (u.isEmpty() || p.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên đăng nhập và mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        Account acc = db.login(u, p);
        if (acc == null) {
            Toast.makeText(this, "Sai tài khoản hoặc mật khẩu!", Toast.LENGTH_SHORT).show();
            return;
        }

        int employeeId = -1;
        if (SessionManager.ROLE_EMPLOYEE.equalsIgnoreCase(acc.getRole())) {
            Employee emp = employeeDAO.getEmployeeByAccountId(acc.getId());
            if (emp != null) {
                employeeId = emp.getId();
            }
        }

        session.saveLogin(acc, employeeId);
        Toast.makeText(this, "Đăng nhập thành công với vai trò: " + acc.getRole(), Toast.LENGTH_SHORT).show();
        goToMain();
    }

    private void goToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
