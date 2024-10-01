package com.example.financialplanner02;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.graphics.Color;
import android.content.DialogInterface;
import android.view.MenuItem;


import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Calendar;
import java.time.LocalDate;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class HomePageActivity extends AppCompatActivity {
    private Button signOutButton;
    private TextView totalIncomeTextView;
    private TextView totalExpenditureTextView;
    private PieChart pieChart;
    private DatabaseHelper dbHelper;
    private IncomeExpenditureBar incomeExpenditureBar;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        userId = getIntent().getIntExtra("userId", -1);
        signOutButton = findViewById(R.id.signOutButton);
        pieChart = findViewById(R.id.pieChart);
        incomeExpenditureBar = findViewById(R.id.incomeExpenditureBar);

        dbHelper = new DatabaseHelper(this);
        List<Expense> expenses = dbHelper.getExpenses(userId);
        setupPieChart(expenses);
        startNotificationService();

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        if (isFirstDayOfMonth() && !hasUserLoggedInThisMonth(userId)) {
            removeNonMonthlyIncomesAndExpenses(userId);
            updateLastLoginMonth(userId);
            showResetPopup();
        }

        totalIncomeTextView = findViewById(R.id.totalIncomeTextView);
        totalExpenditureTextView = findViewById(R.id.totalExpenditureTextView);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.editIncome:
                        Intent intentIncome = new Intent(HomePageActivity.this, IncomeSourcesActivity.class);
                        intentIncome.putExtra("userId", userId);
                        startActivity(intentIncome);
                        break;
                    case R.id.seeAllExpenses:
                        Intent intentExpenses = new Intent(HomePageActivity.this, expensesActivity.class);
                        intentExpenses.putExtra("userId", userId);
                        startActivity(intentExpenses);
                        break;
                    case R.id.editSavingsGoals:
                        Intent intentSavings = new Intent(HomePageActivity.this, SavingsGoalsOverviewActivity.class);
                        intentSavings.putExtra("userId", userId);
                        startActivity(intentSavings);
                        break;
                }
                return true;
            }
        });

        totalIncomeTextView = findViewById(R.id.totalIncomeTextView);
        dbHelper = new DatabaseHelper(this);
        userId = getIntent().getIntExtra("userId", -1);

        updateTotalMonthlyIncome();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTotalMonthlyIncome();
        updateTotalMonthlyExpenditure();
        List<Expense> expenses = getExpensesWithSavings();
        setupPieChart(expenses);
        double totalIncome = dbHelper.getTotalMonthlyIncome(userId);
        double totalExpenditure = dbHelper.getTotalMonthlyExpenditure(userId);
        incomeExpenditureBar.setValues(totalIncome, totalExpenditure);
    }
    private List<Expense> getExpensesWithSavings() {
        List<SavingsGoal> savingsGoals = dbHelper.getSavingsGoals(userId);
        List<Expense> expenses = dbHelper.getExpenses(userId);

        double totalSavings = 0;
        for (SavingsGoal savingsGoal : savingsGoals) {
            totalSavings += savingsGoal.calculateMonthlySavings(LocalDate.now());
        }

        if (totalSavings > 0) {
            boolean savingsExpenseExists = false;
            for (Expense expense : expenses) {
                if (expense.getName().equalsIgnoreCase("Savings")) {
                    savingsExpenseExists = true;
                    expense.setAmount(totalSavings);
                    break;
                }
            }

            if (!savingsExpenseExists) {
                Expense savingsExpense = new Expense("Savings", totalSavings, true);
                expenses.add(savingsExpense);
            }
        }

        return expenses;
    }

    private void updateTotalMonthlyIncome() {
        double totalMonthlyIncome = dbHelper.getTotalMonthlyIncome(userId);
        totalIncomeTextView.setText(String.format(Locale.getDefault(), "Income £%.2f", totalMonthlyIncome));
    }

    private void updateTotalMonthlyExpenditure() {
        double totalMonthlyExpenditure = dbHelper.getTotalMonthlyExpenditure(userId);
        totalExpenditureTextView.setText(String.format(Locale.getDefault(), "Expenditure £%.2f", totalMonthlyExpenditure));
    }

    private void startNotificationService() {
        Intent serviceIntent = new Intent(this, NotificationService.class);
        serviceIntent.putExtra("userId", userId);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }


    private void setupPieChart(List<Expense> expenses) {
        List<PieEntry> pieEntries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        for (Expense expense : expenses) {
            pieEntries.add(new PieEntry((float) expense.getAmount(), expense.getName()));
            colors.add(getColorFromString(expense.getName()));
        }

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        pieDataSet.setColors(colors);
        pieDataSet.setValueTextSize(0f); // This line is used to hide unwanted pie chart data

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false); // This line removes the small description tag
        pieChart.getLegend().setEnabled(false); // This line removes the legend
        pieChart.invalidate();
    }

    private int getColorFromString(String str) { // function that assigns a color to each expense, lets color stay the same after page reload
        int hash = 0;
        for (char c : str.toCharArray()) { // converts each char in the expense name into a char array
            hash = 31 * hash + c; // applies a hash function to each char in the array to create a unique hash value
        }
        return Color.rgb((hash & 0xFF0000) >> 16, (hash & 0x00FF00) >> 8, hash & 0x0000FF); // return a Color.rgb by applying a bitwise AND to each color component
    }




    private boolean isFirstDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_MONTH) == 1;
    }

    private boolean hasUserLoggedInThisMonth(int userId) {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginHistory", MODE_PRIVATE);
        int lastLoginMonth = sharedPreferences.getInt("lastLoginMonth_" + userId, -1);
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH);

        return lastLoginMonth == currentMonth;
    }

    private void updateLastLoginMonth(int userId) {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginHistory", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
        editor.putInt("lastLoginMonth_" + userId, currentMonth);
        editor.apply();
    }

    private void removeNonMonthlyIncomesAndExpenses(int userId) {
        dbHelper.resetIncomeAndExpenses(userId);
    }

    private void showResetPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Monthly Reset");
        builder.setMessage("Your non-monthly income sources and expenses have been reset for the new month.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}


