package com.example.studentmanager.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studentmanager.R;
import com.example.studentmanager.dal.EmployeeDAO;
import com.example.studentmanager.models.Employee;

import java.util.ArrayList;
import java.util.List;

public class EmployeeActivity extends AppCompatActivity {
    private EditText edtSearch;
    private Button btnSearch, btnAddEmployee;
    private ListView lvEmployees;

    private EmployeeDAO employeeDAO;
    private List<Employee> employeeList = new ArrayList<>();
    private EmployeeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);

        employeeDAO = new EmployeeDAO(this);

        edtSearch = findViewById(R.id.edtSearch);
        btnSearch = findViewById(R.id.btnSearch);
        btnAddEmployee = findViewById(R.id.btnAddEmployee);
        lvEmployees = findViewById(R.id.lvEmployees);

        adapter = new EmployeeAdapter();
        lvEmployees.setAdapter(adapter);

        loadEmployees("");

        btnSearch.setOnClickListener(v -> loadEmployees(edtSearch.getText().toString().trim()));

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadEmployees(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnAddEmployee.setOnClickListener(v -> showAddDialog());
    }

    private void loadEmployees(String keyword) {
        if (keyword.isEmpty()) {
            employeeList = employeeDAO.getAllEmployees();
        } else {
            employeeList = employeeDAO.searchEmployees(keyword);
        }
        adapter.notifyDataSetChanged();
    }

    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thêm Nhân Viên Mới");

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_employee, null);
        builder.setView(dialogView);

        EditText edtCode = dialogView.findViewById(R.id.edtCode);
        EditText edtFullname = dialogView.findViewById(R.id.edtFullname);
        EditText edtEmail = dialogView.findViewById(R.id.edtEmail);
        EditText edtPhone = dialogView.findViewById(R.id.edtPhone);
        EditText edtHourlyRate = dialogView.findViewById(R.id.edtHourlyRate);
        EditText edtUsername = dialogView.findViewById(R.id.edtUsername);
        EditText edtPassword = dialogView.findViewById(R.id.edtPassword);

        builder.setPositiveButton("Thêm", null);
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String code = edtCode.getText().toString().trim();
            String name = edtFullname.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();
            String rateStr = edtHourlyRate.getText().toString().trim();
            String username = edtUsername.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (code.isEmpty() || name.isEmpty() || rateStr.isEmpty() || username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin bắt buộc!", Toast.LENGTH_SHORT).show();
                return;
            }

            double rate = Double.parseDouble(rateStr);
            Employee emp = new Employee(code, name, email, phone, rate, 0);

            boolean success = employeeDAO.insertEmployeeWithAccount(emp, username, password);
            if (success) {
                Toast.makeText(this, "Thêm nhân viên và tài khoản thành công!", Toast.LENGTH_SHORT).show();
                loadEmployees("");
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Lỗi: Mã NV hoặc Tên đăng nhập đã tồn tại!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEditDialog(Employee emp) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sửa Thông Tin Nhân Viên");

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_employee, null);
        builder.setView(dialogView);

        EditText edtCode = dialogView.findViewById(R.id.edtCode);
        EditText edtFullname = dialogView.findViewById(R.id.edtFullname);
        EditText edtEmail = dialogView.findViewById(R.id.edtEmail);
        EditText edtPhone = dialogView.findViewById(R.id.edtPhone);
        EditText edtHourlyRate = dialogView.findViewById(R.id.edtHourlyRate);
        View layoutAccount = dialogView.findViewById(R.id.layoutAccount);

        layoutAccount.setVisibility(View.GONE); // Hide account fields during edit

        edtCode.setText(emp.getCode());
        edtFullname.setText(emp.getFullname());
        edtEmail.setText(emp.getEmail());
        edtPhone.setText(emp.getPhone());
        edtHourlyRate.setText(String.valueOf(emp.getHourlyRate()));

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String code = edtCode.getText().toString().trim();
            String name = edtFullname.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();
            String rateStr = edtHourlyRate.getText().toString().trim();

            if (code.isEmpty() || name.isEmpty() || rateStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng không để trống thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            emp.setCode(code);
            emp.setFullname(name);
            emp.setEmail(email);
            emp.setPhone(phone);
            emp.setHourlyRate(Double.parseDouble(rateStr));

            if (employeeDAO.updateEmployee(emp)) {
                Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                loadEmployees("");
            } else {
                Toast.makeText(this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void confirmDelete(Employee emp) {
        new AlertDialog.Builder(this)
                .setTitle("Xác Nhận Xóa")
                .setMessage("Bạn có chắc chắn muốn xóa nhân viên " + emp.getFullname() + " (" + emp.getCode() + ") cùng toàn bộ dữ liệu tài khoản?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    if (employeeDAO.deleteEmployee(emp.getId())) {
                        Toast.makeText(this, "Đã xóa nhân viên!", Toast.LENGTH_SHORT).show();
                        loadEmployees("");
                    } else {
                        Toast.makeText(this, "Lỗi khi xóa nhân viên!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private class EmployeeAdapter extends BaseAdapter {

        @Override
        public int getCount() { return employeeList.size(); }

        @Override
        public Object getItem(int position) { return employeeList.get(position); }

        @Override
        public long getItemId(int position) { return employeeList.get(position).getId(); }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(EmployeeActivity.this).inflate(R.layout.item_employee, parent, false);
            }

            Employee emp = employeeList.get(position);

            TextView tvNameCode = convertView.findViewById(R.id.tvNameCode);
            TextView tvContact = convertView.findViewById(R.id.tvContact);
            TextView tvHourlyRate = convertView.findViewById(R.id.tvHourlyRate);
            Button btnEdit = convertView.findViewById(R.id.btnEdit);
            Button btnDelete = convertView.findViewById(R.id.btnDelete);

            tvNameCode.setText(emp.getFullname() + " (" + emp.getCode() + ")");
            tvContact.setText("Email: " + emp.getEmail() + " | SĐT: " + emp.getPhone());
            tvHourlyRate.setText("Lương theo giờ: " + String.format("%,.0f", emp.getHourlyRate()) + " VNĐ/h");

            btnEdit.setOnClickListener(v -> showEditDialog(emp));
            btnDelete.setOnClickListener(v -> confirmDelete(emp));

            return convertView;
        }
    }
}
