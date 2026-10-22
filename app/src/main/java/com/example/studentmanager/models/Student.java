package com.example.studentmanager.models;

public class Student {
    private int id;
    private String code;
    private String fullname;
    private String email;
    private int accountId;

    public Student() {}

    public Student(int id, String code, String fullname, String email, int accountId) {
        this.id = id;
        this.code = code;
        this.fullname = fullname;
        this.email = email;
        this.accountId = accountId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getFullname() { return fullname; }
    public void setFullname(String fullname) { this.fullname = fullname; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public int getAccountId() { return accountId; }
    public void setAccountId(int accountId) { this.accountId = accountId; }
}
