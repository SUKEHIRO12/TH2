package com.example.studentmanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studentmanager.activities.LoginActivity;
import com.example.studentmanager.activities.ScoreActivity;
import com.example.studentmanager.utils.SessionManager;

/** Màn hình chính: hiển thị chức năng theo role. */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SessionManager session = new SessionManager(this);

        TextView tvWelcome = findViewById(R.id.tvWelcome);
        Button btnScore = findViewById(R.id.btnScore);
        Button btnLogout = findViewById(R.id.btnLogout);

        tvWelcome.setText("Xin chào " + session.getUsername() + " (" + session.getRole() + ")");

        // Chức năng riêng cho từng role
        if (session.isTeacher()) {
            btnScore.setText("Quản lý điểm");
        } else {
            btnScore.setText("Xem điểm của tôi");
        }

        btnScore.setOnClickListener(v ->
                startActivity(new Intent(this, ScoreActivity.class)));

        btnLogout.setOnClickListener(v -> {
            session.logout();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}
