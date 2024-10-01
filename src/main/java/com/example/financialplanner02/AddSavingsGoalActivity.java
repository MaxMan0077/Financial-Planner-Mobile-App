package com.example.financialplanner02;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.app.DatePickerDialog;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.DatePicker;


import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;

public class AddSavingsGoalActivity extends AppCompatActivity {
    private EditText savingsGoalNameEditText;
    private EditText savingsGoalAmountEditText;
    private Button savingsGoalDateButton;
    private Button addSavingsGoalButton;
    private DatabaseHelper dbHelper;
    private int userId;
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addsavingsgoal);

        userId = getIntent().getIntExtra("userId", -1);
        savingsGoalNameEditText = findViewById(R.id.savingsGoalNameEditText);
        savingsGoalAmountEditText = findViewById(R.id.savingsGoalAmountEditText);
        savingsGoalDateButton = findViewById(R.id.savingsGoalDateButton);
        addSavingsGoalButton = findViewById(R.id.addSavingsGoalButton);

        dbHelper = new DatabaseHelper(this);

        savingsGoalDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(AddSavingsGoalActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                monthOfYear = monthOfYear + 1;  // Month is counted from 0, so add 1
                                selectedDate = year + "-" + (monthOfYear < 10 ? ("0" + monthOfYear) : monthOfYear) + "-" + (dayOfMonth < 10 ? ("0" + dayOfMonth) : dayOfMonth);
                                savingsGoalDateButton.setText(selectedDate);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        addSavingsGoalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = savingsGoalNameEditText.getText().toString();
                double amount = Double.parseDouble(savingsGoalAmountEditText.getText().toString());

                if (selectedDate != null) {
                    SavingsGoal savingsGoal = new SavingsGoal(name, amount, selectedDate);
                    dbHelper.addSavingsGoal(savingsGoal, userId);
                    finish();
                } else {

                }
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.editSavingsGoals);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        // navigate to HomePageActivity
                        Intent intentHome = new Intent(AddSavingsGoalActivity.this, HomePageActivity.class);
                        intentHome.putExtra("userId", userId);
                        startActivity(intentHome);
                        return true;
                    case R.id.seeAllExpenses:
                        // navigate to ExpensesActivity
                        Intent intentExpenses = new Intent(AddSavingsGoalActivity.this, expensesActivity.class);
                        intentExpenses.putExtra("userId", userId);
                        startActivity(intentExpenses);
                        return true;
                    case R.id.editSavingsGoals:
                        // navigate to SavingsGoalsActivity
                        Intent intentSavings = new Intent(AddSavingsGoalActivity.this, SavingsGoalsOverviewActivity.class);
                        intentSavings.putExtra("userId", userId);
                        startActivity(intentSavings);
                        return true;
                    case R.id.editIncome:
                        Intent intentIncome = new Intent(AddSavingsGoalActivity.this, IncomeSourcesActivity.class);
                        intentIncome.putExtra("userId", userId);
                        startActivity(intentIncome);
                        return true;
                }
                return false;
            }
        });
    }
}

