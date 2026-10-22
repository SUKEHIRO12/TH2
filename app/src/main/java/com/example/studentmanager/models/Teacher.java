package com.example.studentmanager.models;

public class Teacher {
    private int id;
    private String fullname;
    private String email;
    private int accountId;

    public Teacher() {}

    public Teacher(int id, String fullname, String email, int accountId) {
        this.id = id;
        this.fullname = fullname;
        this.email = email;
        this.accountId = accountId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getFullname() { return fullname; }
    public void setFullname(String fullname) { this.fullname = fullname; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public int getAccountId() { return accountId; }
    public void setAccountId(int accountId) { this.accountId = accountId; }
}
