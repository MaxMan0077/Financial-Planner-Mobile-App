package com.example.financialplanner02;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SavingsGoalDetailActivity extends AppCompatActivity {

    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savingsgoaldetail);

        userId = getIntent().getIntExtra("userId", -1);
        SavingsGoal savingsGoal = (SavingsGoal) getIntent().getSerializableExtra("savingsGoal");

        String name = savingsGoal.getName();
        double amount = savingsGoal.getTargetAmount();
        String dateString = savingsGoal.getTargetDate();

        // Correctly parse the date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate targetDate = LocalDate.parse(dateString, formatter);

        // Calculate monthly savings
        LocalDate currentDate = LocalDate.now();
        double monthlySavings = savingsGoal.calculateMonthlySavings(currentDate);

        ((TextView) findViewById(R.id.goalNameTextView)).setText(name);

        TextView goalCostTextView = findViewById(R.id.goalCostTextView);
        String goalCostText = String.format("Cost: £%.2f", amount);
        goalCostTextView.setText(goalCostText);

        TextView goalTargetDateTextView = findViewById(R.id.goalTargetDateTextView);
        String goalTargetDateText = String.format("Target Date: %s", targetDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
        goalTargetDateTextView.setText(goalTargetDateText);

        TextView monthlySavingsTextView = findViewById(R.id.monthlySavingsTextView);
        String monthlySavingsText = String.format("Monthly Savings: £%.2f", monthlySavings);
        monthlySavingsTextView.setText(monthlySavingsText);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.editSavingsGoals);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        // navigate to HomePageActivity
                        Intent intentHome = new Intent(SavingsGoalDetailActivity.this, HomePageActivity.class);
                        intentHome.putExtra("userId", userId);
                        startActivity(intentHome);
                        return true;
                    case R.id.seeAllExpenses:
                        // navigate to ExpensesActivity
                        Intent intentExpenses = new Intent(SavingsGoalDetailActivity.this, expensesActivity.class);
                        intentExpenses.putExtra("userId", userId);
                        startActivity(intentExpenses);
                        return true;
                    case R.id.editSavingsGoals:
                        // navigate to SavingsGoalsActivity
                        Intent intentSavings = new Intent(SavingsGoalDetailActivity.this, SavingsGoalsOverviewActivity.class);
                        intentSavings.putExtra("userId", userId);
                        startActivity(intentSavings);
                        return true; // Add this line
                    case R.id.editIncome:
                        Intent intentIncome = new Intent(SavingsGoalDetailActivity.this, IncomeSourcesActivity.class);
                        intentIncome.putExtra("userId", userId);
                        startActivity(intentIncome);
                        return true;
                }
                return false;
            }
        });
    }
}
