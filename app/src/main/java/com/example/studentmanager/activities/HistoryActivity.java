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
import com.example.studentmanager.dal.WorkLogDAO;
import com.example.studentmanager.models.WorkLog;
import com.example.studentmanager.utils.SessionManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity {
    private EditText edtMonth, edtYear;
    private Button btnLoadHistory;
    private TextView tvSummary;
    private ListView lvHistory;

    private WorkLogDAO workLogDAO;
    private SessionManager session;
    private int employeeId;
    private List<WorkLog> logList = new ArrayList<>();
    private HistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        session = new SessionManager(this);
        employeeId = session.getEmployeeId();
        workLogDAO = new WorkLogDAO(this);

        edtMonth = findViewById(R.id.edtMonth);
        edtYear = findViewById(R.id.edtYear);
        btnLoadHistory = findViewById(R.id.btnLoadHistory);
        tvSummary = findViewById(R.id.tvSummary);
        lvHistory = findViewById(R.id.lvHistory);

        Calendar cal = Calendar.getInstance();
        edtMonth.setText(String.valueOf(cal.get(Calendar.MONTH) + 1));
        edtYear.setText(String.valueOf(cal.get(Calendar.YEAR)));

        adapter = new HistoryAdapter();
        lvHistory.setAdapter(adapter);

        btnLoadHistory.setOnClickListener(v -> loadHistory());

        loadHistory();
    }

    private void loadHistory() {
        String mStr = edtMonth.getText().toString().trim();
        String yStr = edtYear.getText().toString().trim();

        if (mStr.isEmpty() || yStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tháng và năm!", Toast.LENGTH_SHORT).show();
            return;
        }

        int month = Integer.parseInt(mStr);
        int year = Integer.parseInt(yStr);

        logList = workLogDAO.getLogsByEmployeeAndMonth(employeeId, month, year);
        adapter.notifyDataSetChanged();

        double totalHours = 0.0;
        int completedDays = 0;
        for (WorkLog log : logList) {
            if ("COMPLETED".equals(log.getStatus())) {
                completedDays++;
                totalHours += log.getTotalHours();
            }
        }

        tvSummary.setText(String.format(Locale.getDefault(),
                "Tháng %02d/%d: %d ngày công | Tổng: %.1f giờ",
                month, year, completedDays, totalHours));
    }

    private class HistoryAdapter extends BaseAdapter {

        @Override
        public int getCount() { return logList.size(); }

        @Override
        public Object getItem(int position) { return logList.get(position); }

        @Override
        public long getItemId(int position) { return logList.get(position).getId(); }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(HistoryActivity.this).inflate(R.layout.item_worklog, parent, false);
            }

            WorkLog log = logList.get(position);

            TextView tvDate = convertView.findViewById(R.id.tvDate);
            TextView tvTimes = convertView.findViewById(R.id.tvTimes);
            TextView tvHoursAndStatus = convertView.findViewById(R.id.tvHoursAndStatus);

            tvDate.setText("Ngày: " + log.getWorkDate());
            String checkOut = log.getCheckOutTime() != null ? log.getCheckOutTime() : "--:--";
            tvTimes.setText("Vào: " + log.getCheckInTime() + " - Ra: " + checkOut);

            if ("COMPLETED".equals(log.getStatus())) {
                tvHoursAndStatus.setText("Tổng: " + String.format(Locale.getDefault(), "%.1f", log.getTotalHours()) + " giờ | Đã hoàn thành");
            } else {
                tvHoursAndStatus.setText("Chưa check-out (Chưa hoàn thành)");
            }

            return convertView;
        }
    }
}
