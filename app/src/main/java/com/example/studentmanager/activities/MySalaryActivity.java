package com.example.studentmanager.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studentmanager.R;
import com.example.studentmanager.dal.EmployeeDAO;
import com.example.studentmanager.dal.SalaryDAO;
import com.example.studentmanager.models.Employee;
import com.example.studentmanager.models.Salary;
import com.example.studentmanager.utils.SessionManager;

import java.util.Calendar;
import java.util.Locale;

public class MySalaryActivity extends AppCompatActivity {
    private EditText edtMonth, edtYear;
    private Button btnViewSalary;
    private TextView tvEmpName, tvHourlyRate, tvNormalHours, tvOvertimeHours, tvTotalSalary, tvStatus;
    private View cardSalary;

    private SalaryDAO salaryDAO;
    private EmployeeDAO employeeDAO;
    private SessionManager session;
    private int employeeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_salary);

        session = new SessionManager(this);
        employeeId = session.getEmployeeId();
        salaryDAO = new SalaryDAO(this);
        employeeDAO = new EmployeeDAO(this);

        edtMonth = findViewById(R.id.edtMonth);
        edtYear = findViewById(R.id.edtYear);
        btnViewSalary = findViewById(R.id.btnViewSalary);

        tvEmpName = findViewById(R.id.tvEmpName);
        tvHourlyRate = findViewById(R.id.tvHourlyRate);
        tvNormalHours = findViewById(R.id.tvNormalHours);
        tvOvertimeHours = findViewById(R.id.tvOvertimeHours);
        tvTotalSalary = findViewById(R.id.tvTotalSalary);
        tvStatus = findViewById(R.id.tvStatus);
        cardSalary = findViewById(R.id.cardSalary);

        Calendar cal = Calendar.getInstance();
        edtMonth.setText(String.valueOf(cal.get(Calendar.MONTH) + 1));
        edtYear.setText(String.valueOf(cal.get(Calendar.YEAR)));

        btnViewSalary.setOnClickListener(v -> loadMySalary());

        loadMySalary();
    }

    private void loadMySalary() {
        String mStr = edtMonth.getText().toString().trim();
        String yStr = edtYear.getText().toString().trim();

        if (mStr.isEmpty() || yStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tháng và năm!", Toast.LENGTH_SHORT).show();
            return;
        }

        int month = Integer.parseInt(mStr);
        int year = Integer.parseInt(yStr);

        Employee emp = employeeDAO.getEmployeeById(employeeId);
        Salary sal = salaryDAO.calculateAndSaveSalary(employeeId, month, year);

        if (emp != null) {
            tvEmpName.setText(emp.getFullname() + " (" + emp.getCode() + ")");
            tvHourlyRate.setText("Mức lương theo giờ: " + String.format(Locale.getDefault(), "%,.0f", emp.getHourlyRate()) + " VNĐ/h");
        }

        if (sal != null) {
            cardSalary.setVisibility(View.VISIBLE);
            double normalHours = sal.getTotalHours() - sal.getOvertimeHours();

            tvNormalHours.setText("Số giờ làm thường: " + String.format(Locale.getDefault(), "%.1f", normalHours) + "h");
            tvOvertimeHours.setText("Số giờ tăng ca (1.5x): " + String.format(Locale.getDefault(), "%.1f", sal.getOvertimeHours()) + "h");
            tvTotalSalary.setText("Tổng thu nhập: " + String.format(Locale.getDefault(), "%,.0f", sal.getTotalSalary()) + " VNĐ");
            tvStatus.setText("Trạng thái thanh toán: " + sal.getStatus());
        } else {
            tvNormalHours.setText("Không có dữ liệu chấm công cho tháng " + month + "/" + year);
            tvOvertimeHours.setText("");
            tvTotalSalary.setText("Tổng thu nhập: 0 VNĐ");
            tvStatus.setText("");
        }
    }
}
