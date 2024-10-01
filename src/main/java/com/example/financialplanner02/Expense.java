package com.example.financialplanner02;

public class Expense {
    private String name;
    private double amount;
    private boolean monthly;

    public Expense(String name, double amount, boolean monthly) {
        this.name = name;
        this.amount = amount;
        this.monthly = monthly;
    }

    public String getName() {
        return name;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }

    public boolean isMonthly() {
        return monthly;
    }
}