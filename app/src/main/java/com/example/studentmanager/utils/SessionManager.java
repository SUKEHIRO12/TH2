package com.example.studentmanager.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.studentmanager.models.Account;

/** Lưu thông tin tài khoản đăng nhập bằng SharedPreferences. */
public class SessionManager {
    private static final String PREF_NAME = "login_session";
    private static final String KEY_ID = "account_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_ROLE = "role";

    public static final String ROLE_TEACHER = "teacher";
    public static final String ROLE_STUDENT = "student";

    private final SharedPreferences pref;

    public SessionManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveLogin(Account account) {
        pref.edit()
                .putInt(KEY_ID, account.getId())
                .putString(KEY_USERNAME, account.getUsername())
                .putString(KEY_ROLE, account.getRole())
                .apply();
    }

    public boolean isLoggedIn() { return pref.getInt(KEY_ID, -1) != -1; }
    public int getAccountId() { return pref.getInt(KEY_ID, -1); }
    public String getUsername() { return pref.getString(KEY_USERNAME, ""); }
    public String getRole() { return pref.getString(KEY_ROLE, ""); }
    public boolean isTeacher() { return ROLE_TEACHER.equals(getRole()); }

    public void logout() { pref.edit().clear().apply(); }
}
