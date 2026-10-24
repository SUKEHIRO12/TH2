package com.example.studentmanager.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studentmanager.R;
import com.example.studentmanager.dal.EmployeeDAO;
import com.example.studentmanager.dal.SalaryDAO;
import com.example.studentmanager.models.Employee;
import com.example.studentmanager.models.Salary;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SalaryAdminActivity extends AppCompatActivity {
    private EditText edtMonth, edtYear;
    private Button btnCalculateSalary;
    private ListView lvSalary;

    private SalaryDAO salaryDAO;
    private EmployeeDAO employeeDAO;
    private List<Salary> salaryList = new ArrayList<>();
    private SalaryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salary_admin);

        salaryDAO = new SalaryDAO(this);
        employeeDAO = new EmployeeDAO(this);

        edtMonth = findViewById(R.id.edtMonth);
        edtYear = findViewById(R.id.edtYear);
        btnCalculateSalary = findViewById(R.id.btnCalculateSalary);
        lvSalary = findViewById(R.id.lvSalary);

        Calendar cal = Calendar.getInstance();
        edtMonth.setText(String.valueOf(cal.get(Calendar.MONTH) + 1));
        edtYear.setText(String.valueOf(cal.get(Calendar.YEAR)));

        adapter = new SalaryAdapter();
        lvSalary.setAdapter(adapter);

        btnCalculateSalary.setOnClickListener(v -> calculateAndLoadSalary());

        calculateAndLoadSalary();
    }

    private void calculateAndLoadSalary() {
        String mStr = edtMonth.getText().toString().trim();
        String yStr = edtYear.getText().toString().trim();

        if (mStr.isEmpty() || yStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tháng và năm!", Toast.LENGTH_SHORT).show();
            return;
        }

        int month = Integer.parseInt(mStr);
        int year = Integer.parseInt(yStr);

        salaryDAO.calculateSalaryForAllEmployees(month, year);
        salaryList = salaryDAO.getAllSalariesByMonthYear(month, year);
        adapter.notifyDataSetChanged();

        if (salaryList.isEmpty()) {
            Toast.makeText(this, "Không có dữ liệu lương cho tháng " + month + "/" + year, Toast.LENGTH_SHORT).show();
        }
    }

    private class SalaryAdapter extends BaseAdapter {

        @Override
        public int getCount() { return salaryList.size(); }

        @Override
        public Object getItem(int position) { return salaryList.get(position); }

        @Override
        public long getItemId(int position) { return salaryList.get(position).getId(); }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(SalaryAdminActivity.this).inflate(R.layout.item_salary_admin, parent, false);
            }

            Salary sal = salaryList.get(position);
            Employee emp = employeeDAO.getEmployeeById(sal.getEmployeeId());

            TextView tvEmpInfo = convertView.findViewById(R.id.tvEmpInfo);
            TextView tvHoursInfo = convertView.findViewById(R.id.tvHoursInfo);
            TextView tvSalaryAmount = convertView.findViewById(R.id.tvSalaryAmount);
            TextView tvStatus = convertView.findViewById(R.id.tvStatus);
            Button btnChangeStatus = convertView.findViewById(R.id.btnChangeStatus);

            String empName = emp != null ? emp.getFullname() + " (" + emp.getCode() + ")" : "NV #" + sal.getEmployeeId();
            tvEmpInfo.setText(empName);

            tvHoursInfo.setText("Tổng giờ: " + sal.getTotalHours() + "h | Tăng ca: " + sal.getOvertimeHours() + "h");
            tvSalaryAmount.setText("Tổng lương: " + String.format("%,.0f", sal.getTotalSalary()) + " VNĐ");
            tvStatus.setText("Trạng thái: " + sal.getStatus());

            btnChangeStatus.setOnClickListener(v -> {
                String nextStatus;
                if ("PENDING".equals(sal.getStatus())) {
                    nextStatus = "APPROVED";
                } else if ("APPROVED".equals(sal.getStatus())) {
                    nextStatus = "PAID";
                } else {
                    nextStatus = "PENDING";
                }

                if (salaryDAO.updateSalaryStatus(sal.getId(), nextStatus)) {
                    sal.setStatus(nextStatus);
                    notifyDataSetChanged();
                    Toast.makeText(SalaryAdminActivity.this, "Đã đổi trạng thái thành " + nextStatus, Toast.LENGTH_SHORT).show();
                }
            });

            return convertView;
        }
    }
}
