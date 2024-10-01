package com.example.financialplanner02;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class editIncomeActivity extends AppCompatActivity {

    private EditText nameEditText;
    private EditText amountEditText;
    private CheckBox monthlyCheckBox;
    private Button addButton;
    private DatabaseHelper dbHelper;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editincome);

        nameEditText = findViewById(R.id.incomeSourceNameEditText);
        amountEditText = findViewById(R.id.incomeSourceAmountEditText);
        monthlyCheckBox = findViewById(R.id.checkbox_monthly);
        addButton = findViewById(R.id.saveIncomeSourceButton);
        dbHelper = new DatabaseHelper(this);
        userId = getIntent().getIntExtra("userId", -1);

        if (userId == -1) {
            Toast.makeText(editIncomeActivity.this, "Invalid user ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString().trim();
                double amount;

                try {
                    amount = Double.parseDouble(amountEditText.getText().toString());
                } catch (NumberFormatException e) {
                    amount = -1;
                }

                if (!name.isEmpty() && amount >= 0) {
                    boolean isMonthly = monthlyCheckBox.isChecked();
                    IncomeSource incomeSource = new IncomeSource(name, amount, isMonthly);

                    long result = dbHelper.addIncomeSource(incomeSource, userId);
                    if (result != -1) {
                        Toast.makeText(editIncomeActivity.this, "Income source added successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(editIncomeActivity.this, "Error adding income source", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(editIncomeActivity.this, "Please fill in all fields correctly", Toast.LENGTH_SHORT).show();
                }
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.editIncome);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        // navigate to HomePageActivity
                        Intent intentHome = new Intent(editIncomeActivity.this, HomePageActivity.class);
                        intentHome.putExtra("userId", userId);
                        startActivity(intentHome);
                        return true;
                    case R.id.seeAllExpenses:
                        // navigate to ExpensesActivity
                        Intent intentExpenses = new Intent(editIncomeActivity.this, expensesActivity.class);
                        intentExpenses.putExtra("userId", userId);
                        startActivity(intentExpenses);
                        return true;
                    case R.id.editSavingsGoals:
                        // navigate to SavingsGoalsActivity
                        Intent intentSavings = new Intent(editIncomeActivity.this, SavingsGoalsOverviewActivity.class);
                        intentSavings.putExtra("userId", userId);
                        startActivity(intentSavings);
                    case R.id.editIncome:
                        Intent intentIncome = new Intent(editIncomeActivity.this, IncomeSourcesActivity.class);
                        intentIncome.putExtra("userId", userId);
                        startActivity(intentIncome);
                        break;
                }
                return false;
            }
        });
    }
}
