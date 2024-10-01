package com.example.financialplanner02;

public class IncomeSource {
    private String name;
    private double amount;
    private boolean monthly;

    public IncomeSource(String name, double amount, boolean monthly) {
        this.name = name;
        this.amount = amount;
        this.monthly = monthly;
    }

    public String getName() {
        return name;
    }

    public double getAmount() {
        return amount;
    }

    public boolean isMonthly() {
        return monthly;
    }
}

