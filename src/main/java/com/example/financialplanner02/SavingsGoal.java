package com.example.financialplanner02;

import java.io.Serializable;
import java.time.temporal.ChronoUnit;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SavingsGoal implements Serializable {
    private String name;
    private double targetAmount;
    private String targetDate;

    public SavingsGoal(String name, double targetAmount, String targetDate) {
        this.name = name;
        this.targetAmount = targetAmount;
        this.targetDate = targetDate;
    }

    public String getName() {
        return name;
    }

    public double getTargetAmount() {
        return targetAmount;
    }

    public String getTargetDate() {
        return targetDate;
    }

    public double calculateMonthlySavings(LocalDate currentDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate targetDate = LocalDate.parse(this.targetDate, formatter);

        long monthsBetween = ChronoUnit.MONTHS.between(currentDate, targetDate);
        return monthsBetween > 0 ? targetAmount / monthsBetween : 0;
    }

    public boolean isGoalAchieved() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate currentDate = LocalDate.now();
        LocalDate purchaseDate = LocalDate.parse(this.targetDate, formatter);

        // If current date is the same or after the purchase date
        return !currentDate.isBefore(purchaseDate);
    }
}
