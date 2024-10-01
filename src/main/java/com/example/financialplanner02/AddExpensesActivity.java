package com.example.financialplanner02;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AddExpensesActivity extends AppCompatActivity {
    private EditText expenseNameEditText;
    private EditText expenseAmountEditText;
    private CheckBox recurringCheckbox;
    private Button saveExpenseButton;

    private DatabaseHelper databaseHelper;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addexpenses);

        expenseNameEditText = findViewById(R.id.expense_name_edit_text);
        expenseAmountEditText = findViewById(R.id.expense_amount_edit_text);
        recurringCheckbox = findViewById(R.id.recurring_checkbox);
        saveExpenseButton = findViewById(R.id.save_expense_button);

        databaseHelper = new DatabaseHelper(this);
        userId = getIntent().getIntExtra("userId", -1);

        saveExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveExpense();
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.seeAllExpenses);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        // navigate to HomePageActivity
                        Intent intentHome = new Intent(AddExpensesActivity.this, HomePageActivity.class);
                        intentHome.putExtra("userId", userId);
                        startActivity(intentHome);
                        return true;
                    case R.id.seeAllExpenses:
                        // navigate to ExpensesActivity
                        Intent intentExpenses = new Intent(AddExpensesActivity.this, expensesActivity.class);
                        intentExpenses.putExtra("userId", userId);
                        startActivity(intentExpenses);
                        return true;
                    case R.id.editSavingsGoals:
                        // navigate to SavingsGoalsActivity
                        Intent intentSavings = new Intent(AddExpensesActivity.this, SavingsGoalsOverviewActivity.class);
                        intentSavings.putExtra("userId", userId);
                        startActivity(intentSavings);
                        return true;
                    case R.id.editIncome:
                        Intent intentIncome = new Intent(AddExpensesActivity.this, IncomeSourcesActivity.class);
                        intentIncome.putExtra("userId", userId);
                        startActivity(intentIncome);
                        break;
                }
                return false;
            }
        });
    }

    private void saveExpense() {
        String expenseName = expenseNameEditText.getText().toString();
        String expenseAmountString = expenseAmountEditText.getText().toString();
        boolean recurring = recurringCheckbox.isChecked();

        if (expenseName.isEmpty() || expenseAmountString.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double expenseAmount = Double.parseDouble(expenseAmountString);
        Expense expense = new Expense(expenseName, expenseAmount, recurring);

        long result = databaseHelper.addExpense(expense, userId);
        if (result != -1) {
            Toast.makeText(this, "Expense added successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error adding expense", Toast.LENGTH_SHORT).show();
        }
    }
}

