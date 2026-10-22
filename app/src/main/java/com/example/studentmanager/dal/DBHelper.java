package com.example.studentmanager.dal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.studentmanager.models.Account;
import com.example.studentmanager.models.Score;
import com.example.studentmanager.models.Student;
import com.example.studentmanager.models.Teacher;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "studentmanager.db";
    private static final int DB_VERSION = 1; // đổi schema thì tăng số này (hoặc gỡ app cài lại)

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Account(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL, " +
                "role TEXT NOT NULL)");

        db.execSQL("CREATE TABLE Student(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "code TEXT UNIQUE NOT NULL, " +
                "fullname TEXT, email TEXT, " +
                "accountId INTEGER)");

        db.execSQL("CREATE TABLE Teacher(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "fullname TEXT, email TEXT, " +
                "accountId INTEGER)");

        db.execSQL("CREATE TABLE Score(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "studentCode TEXT, teacherCode TEXT, " +
                "subject TEXT, score REAL)");

        seed(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Score");
        db.execSQL("DROP TABLE IF EXISTS Teacher");
        db.execSQL("DROP TABLE IF EXISTS Student");
        db.execSQL("DROP TABLE IF EXISTS Account");
        onCreate(db);
    }

    /** Dữ liệu mẫu để test: teacher1 / 123456 (giáo viên), sv001 / 123456 (sinh viên). */
    private void seed(SQLiteDatabase db) {
        long tAcc = insertAccount(db, "teacher1", "123456", "teacher");
        long sAcc = insertAccount(db, "sv001", "123456", "student");

        ContentValues t = new ContentValues();
        t.put("fullname", "Nguyen Van A");
        t.put("email", "gv@example.com");
        t.put("accountId", tAcc);
        db.insert("Teacher", null, t);

        ContentValues s = new ContentValues();
        s.put("code", "SV001");
        s.put("fullname", "Tran Thi B");
        s.put("email", "sv001@example.com");
        s.put("accountId", sAcc);
        db.insert("Student", null, s);

        ContentValues sc = new ContentValues();
        sc.put("studentCode", "SV001");
        sc.put("teacherCode", "1");
        sc.put("subject", "Mobile App");
        sc.put("score", 9.0);
        db.insert("Score", null, sc);
    }

    private long insertAccount(SQLiteDatabase db, String u, String p, String r) {
        ContentValues v = new ContentValues();
        v.put("username", u);
        v.put("password", p);
        v.put("role", r);
        return db.insert("Account", null, v);
    }

    // ===================== ACCOUNT =====================
    /** Trả về Account nếu đúng username + password, ngược lại null. */
    public Account login(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT id, username, password, role FROM Account WHERE username=? AND password=?",
                new String[]{username, password});
        Account acc = null;
        if (c.moveToFirst()) {
            acc = new Account(c.getInt(0), c.getString(1), c.getString(2), c.getString(3));
        }
        c.close();
        return acc;
    }

    // ===================== STUDENT / TEACHER =====================
    public Student getStudentByAccountId(int accountId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT id, code, fullname, email, accountId FROM Student WHERE accountId=?",
                new String[]{String.valueOf(accountId)});
        Student s = null;
        if (c.moveToFirst()) {
            s = new Student(c.getInt(0), c.getString(1), c.getString(2), c.getString(3), c.getInt(4));
        }
        c.close();
        return s;
    }

    public Teacher getTeacherByAccountId(int accountId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT id, fullname, email, accountId FROM Teacher WHERE accountId=?",
                new String[]{String.valueOf(accountId)});
        Teacher t = null;
        if (c.moveToFirst()) {
            t = new Teacher(c.getInt(0), c.getString(1), c.getString(2), c.getInt(3));
        }
        c.close();
        return t;
    }

    // ===================== SCORE (mẫu CRUD, copy sửa cho bảng khác) =====================
    public long insertScore(Score s) {
        ContentValues v = new ContentValues();
        v.put("studentCode", s.getStudentCode());
        v.put("teacherCode", s.getTeacherCode());
        v.put("subject", s.getSubject());
        v.put("score", s.getScore());
        return getWritableDatabase().insert("Score", null, v);
    }

    public int updateScore(Score s) {
        ContentValues v = new ContentValues();
        v.put("studentCode", s.getStudentCode());
        v.put("teacherCode", s.getTeacherCode());
        v.put("subject", s.getSubject());
        v.put("score", s.getScore());
        return getWritableDatabase().update("Score", v, "id=?", new String[]{String.valueOf(s.getId())});
    }

    public int deleteScore(int id) {
        return getWritableDatabase().delete("Score", "id=?", new String[]{String.valueOf(id)});
    }

    public List<Score> getAllScores() {
        return queryScores("SELECT * FROM Score ORDER BY id DESC", null);
    }

    public List<Score> getScoresByStudentCode(String studentCode) {
        return queryScores("SELECT * FROM Score WHERE studentCode=? ORDER BY id DESC",
                new String[]{studentCode});
    }

    private List<Score> queryScores(String sql, String[] args) {
        List<Score> list = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery(sql, args);
        while (c.moveToNext()) {
            list.add(new Score(c.getInt(0), c.getString(1), c.getString(2),
                    c.getString(3), c.getDouble(4)));
        }
        c.close();
        return list;
    }
}
