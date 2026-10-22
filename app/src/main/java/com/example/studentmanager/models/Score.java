package com.example.studentmanager.models;

public class Score {
    private int id;
    private String studentCode;
    private String teacherCode;
    private String subject;
    private double score;

    public Score() {}

    public Score(int id, String studentCode, String teacherCode, String subject, double score) {
        this.id = id;
        this.studentCode = studentCode;
        this.teacherCode = teacherCode;
        this.subject = subject;
        this.score = score;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getStudentCode() { return studentCode; }
    public void setStudentCode(String studentCode) { this.studentCode = studentCode; }
    public String getTeacherCode() { return teacherCode; }
    public void setTeacherCode(String teacherCode) { this.teacherCode = teacherCode; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }

    @Override
    public String toString() {
        return studentCode + " | " + subject + " | " + score + " (GV: " + teacherCode + ")";
    }
}
