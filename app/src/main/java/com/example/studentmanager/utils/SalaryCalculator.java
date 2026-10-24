package com.example.studentmanager.utils;

import com.example.studentmanager.models.WorkLog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SalaryCalculator {
    public static final double STANDARD_DAILY_HOURS = 8.0;
    public static final double OVERTIME_RATE_MULTIPLIER = 1.5;

    public static class SalaryResult {
        public double totalHours;
        public double overtimeHours;
        public double totalSalary;

        public SalaryResult(double totalHours, double overtimeHours, double totalSalary) {
            this.totalHours = totalHours;
            this.overtimeHours = overtimeHours;
            this.totalSalary = totalSalary;
        }
    }

    public static double calculateHours(String checkIn, String checkOut) {
        if (checkIn == null || checkOut == null) return 0.0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date in = sdf.parse(checkIn);
            Date out = sdf.parse(checkOut);
            if (in == null || out == null) return 0.0;

            long diffInMillis = out.getTime() - in.getTime();
            if (diffInMillis < 0) {
                return -1.0; // Invalid checkout time
            }
            return (double) diffInMillis / (1000.0 * 60.0 * 60.0);
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    public static SalaryResult calculateSalary(List<WorkLog> completedLogs, double hourlyRate) {
        double totalHours = 0.0;
        double overtimeHours = 0.0;
        double totalSalary = 0.0;

        if (completedLogs != null) {
            for (WorkLog log : completedLogs) {
                double dayHours = log.getTotalHours();
                if (dayHours <= 0) continue;

                double normal = Math.min(dayHours, STANDARD_DAILY_HOURS);
                double ot = Math.max(0.0, dayHours - STANDARD_DAILY_HOURS);

                totalHours += dayHours;
                overtimeHours += ot;
                totalSalary += (normal * hourlyRate) + (ot * hourlyRate * OVERTIME_RATE_MULTIPLIER);
            }
        }

        return new SalaryResult(totalHours, overtimeHours, totalSalary);
    }
}
