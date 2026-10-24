package com.example.studentmanager.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.studentmanager.models.Account;

public class SessionManager {
    private static final String PREF_NAME = "login_session";
    private static final String KEY_ACCOUNT_ID = "account_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_ROLE = "role";
    private static final String KEY_EMPLOYEE_ID = "employee_id";

    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_EMPLOYEE = "employee";

    private final SharedPreferences pref;

    public SessionManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveLogin(Account account, int employeeId) {
        pref.edit()
                .putInt(KEY_ACCOUNT_ID, account.getId())
                .putString(KEY_USERNAME, account.getUsername())
                .putString(KEY_ROLE, account.getRole())
                .putInt(KEY_EMPLOYEE_ID, employeeId)
                .apply();
    }

    public boolean isLoggedIn() {
        return pref.getInt(KEY_ACCOUNT_ID, -1) != -1;
    }

    public int getAccountId() {
        return pref.getInt(KEY_ACCOUNT_ID, -1);
    }

    public String getUsername() {
        return pref.getString(KEY_USERNAME, "");
    }

    public String getRole() {
        return pref.getString(KEY_ROLE, "");
    }

    public int getEmployeeId() {
        return pref.getInt(KEY_EMPLOYEE_ID, -1);
    }

    public boolean isAdmin() {
        return ROLE_ADMIN.equalsIgnoreCase(getRole());
    }

    public boolean isEmployee() {
        return ROLE_EMPLOYEE.equalsIgnoreCase(getRole());
    }

    public void logout() {
        pref.edit().clear().apply();
    }
}
