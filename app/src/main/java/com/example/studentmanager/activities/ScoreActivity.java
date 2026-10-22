package com.example.studentmanager.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studentmanager.R;
import com.example.studentmanager.dal.DBHelper;
import com.example.studentmanager.models.Score;
import com.example.studentmanager.models.Student;
import com.example.studentmanager.models.Teacher;
import com.example.studentmanager.utils.SessionManager;

import java.util.List;

/**
 * Teacher: xem tất cả điểm, thêm điểm, nhấn giữ 1 dòng để xoá.
 * Student: chỉ xem điểm của mình.
 */
public class ScoreActivity extends AppCompatActivity {
    private DBHelper db;
    private SessionManager session;
    private ListView lvScore;
    private LinearLayout layoutForm;
    private EditText edtStudentCode, edtSubject, edtScore;
    private Button btnAdd;

    private List<Score> data;
    private ArrayAdapter<Score> adapter;
    private String studentCode = ""; // dùng khi role là student
    private String teacherCode = ""; // dùng khi role là teacher

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        db = new DBHelper(this);
        session = new SessionManager(this);

        lvScore = findViewById(R.id.lvScore);
        layoutForm = findViewById(R.id.layoutForm);
        edtStudentCode = findViewById(R.id.edtStudentCode);
        edtSubject = findViewById(R.id.edtSubject);
        edtScore = findViewById(R.id.edtScore);
        btnAdd = findViewById(R.id.btnAdd);

        if (session.isTeacher()) {
            Teacher t = db.getTeacherByAccountId(session.getAccountId());
            if (t != null) teacherCode = String.valueOf(t.getId());
            layoutForm.setVisibility(View.VISIBLE);

            btnAdd.setOnClickListener(v -> addScore());
            lvScore.setOnItemLongClickListener((parent, view, position, id) -> {
                db.deleteScore(data.get(position).getId());
                loadData();
                return true;
            });
        } else {
            Student s = db.getStudentByAccountId(session.getAccountId());
            if (s != null) studentCode = s.getCode();
            layoutForm.setVisibility(View.GONE);
        }

        loadData();
    }

    private void loadData() {
        data = session.isTeacher() ? db.getAllScores() : db.getScoresByStudentCode(studentCode);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, data);
        lvScore.setAdapter(adapter);
    }

    private void addScore() {
        String code = edtStudentCode.getText().toString().trim();
        String subject = edtSubject.getText().toString().trim();
        String scoreStr = edtScore.getText().toString().trim();

        if (code.isEmpty() || subject.isEmpty() || scoreStr.isEmpty()) {
            Toast.makeText(this, "Nhập đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        double score;
        try {
            score = Double.parseDouble(scoreStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Điểm không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        db.insertScore(new Score(0, code, teacherCode, subject, score));
        edtStudentCode.setText("");
        edtSubject.setText("");
        edtScore.setText("");
        loadData();
    }
}
