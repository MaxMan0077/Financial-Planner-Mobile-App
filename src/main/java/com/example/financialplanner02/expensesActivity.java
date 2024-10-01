package com.example.financialplanner02;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;
import java.time.LocalDate;



public class expensesActivity extends AppCompatActivity {
    private Button addExpenseButton;
    private RecyclerView expensesRecyclerView;

    private DatabaseHelper databaseHelper;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses);

        addExpenseButton = findViewById(R.id.addExpenseButton);
        expensesRecyclerView = findViewById(R.id.expensesRecyclerView);

        databaseHelper = new DatabaseHelper(this);
        userId = getIntent().getIntExtra("userId", -1);
        setUpSwipeToDelete();

        addExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(expensesActivity.this, AddExpensesActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
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
                        Intent intentHome = new Intent(expensesActivity.this, HomePageActivity.class);
                        intentHome.putExtra("userId", userId);
                        startActivity(intentHome);
                        break;
                    case R.id.editIncome:
                        // navigate to EditIncomeActivity
                        Intent intentIncome = new Intent(expensesActivity.this, IncomeSourcesActivity.class);
                        intentIncome.putExtra("userId", userId);
                        startActivity(intentIncome);
                        break;
                    case R.id.editSavingsGoals:
                        Intent intentSavings = new Intent(expensesActivity.this, SavingsGoalsOverviewActivity.class);
                        intentSavings.putExtra("userId", userId);
                        startActivity(intentSavings);
                        break;
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadExpenses();
    }

    private List<Expense> getExpensesWithSavings() {
        List<SavingsGoal> savingsGoals = databaseHelper.getSavingsGoals(userId);
        List<Expense> expenses = databaseHelper.getExpenses(userId);

        double totalSavings = 0;
        LocalDate currentDate = LocalDate.now();
        for (SavingsGoal savingsGoal : savingsGoals) {
            totalSavings += savingsGoal.calculateMonthlySavings(currentDate);
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


    private void loadExpenses() {
        List<Expense> expenses = getExpensesWithSavings();
        ExpenseAdapter expenseAdapter = new ExpenseAdapter(this, expenses);
        expensesRecyclerView.setAdapter(expenseAdapter);
        expensesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setUpSwipeToDelete() {
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Expense expenseToDelete = getExpensesWithSavings().get(position);

                if (databaseHelper.deleteExpense(expenseToDelete.getName())) {
                    Toast.makeText(expensesActivity.this, "Expense deleted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(expensesActivity.this, "Error deleting expense", Toast.LENGTH_SHORT).show();
                }
                loadExpenses();
            }
        };

        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(expensesRecyclerView);
    }
}
