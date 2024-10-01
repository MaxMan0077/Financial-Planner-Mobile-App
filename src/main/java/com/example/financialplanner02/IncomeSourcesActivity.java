package com.example.financialplanner02;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class IncomeSourcesActivity extends AppCompatActivity {

    private Button addIncomeSourceButton;
    private RecyclerView incomeSourcesRecyclerView;
    private DatabaseHelper dbHelper;
    private int userId;
    private IncomeSourcesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incomesources);

        addIncomeSourceButton = findViewById(R.id.addIncomeSourceButton);
        incomeSourcesRecyclerView = findViewById(R.id.incomeSourcesRecyclerView);

        dbHelper = new DatabaseHelper(this);
        userId = getIntent().getIntExtra("userId", -1);

        setupIncomeSourcesRecyclerView();
        setupRecyclerView();

        addIncomeSourceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IncomeSourcesActivity.this, editIncomeActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
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
                        Intent intentHome = new Intent(IncomeSourcesActivity.this, HomePageActivity.class);
                        intentHome.putExtra("userId", userId);
                        startActivity(intentHome);
                        return true;
                    case R.id.seeAllExpenses:
                        // navigate to ExpensesActivity
                        Intent intentExpenses = new Intent(IncomeSourcesActivity.this, expensesActivity.class);
                        intentExpenses.putExtra("userId", userId);
                        startActivity(intentExpenses);
                        return true;
                    case R.id.editSavingsGoals:
                        // navigate to SavingsGoalsActivity
                        Intent intentSavings = new Intent(IncomeSourcesActivity.this, SavingsGoalsOverviewActivity.class);
                        intentSavings.putExtra("userId", userId);
                        startActivity(intentSavings);
                }
                return false;
            }
        });
    }

    private void setupRecyclerView(){
        // Attach the ItemTouchHelper to the RecyclerView
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(createItemTouchHelperCallback());
        itemTouchHelper.attachToRecyclerView(incomeSourcesRecyclerView);
    }

    private ItemTouchHelper.SimpleCallback createItemTouchHelperCallback() {
        return new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                // Get the income source ID from the adapter
                IncomeSource incomeSource = adapter.getIncomeSource(position);
                String incomeSourceId = incomeSource.getName();

                // Remove the income source from the list and update the adapter
                adapter.removeItem(position);

                // Remove the income source from the database
                dbHelper.deleteIncomeSource(incomeSourceId);
            }
        };
    }

    private void setupIncomeSourcesRecyclerView() {
        // Load income sources for the user from the database
        List<IncomeSource> incomeSources = dbHelper.getIncomeSources(userId);

        // Set up the adapter and layout manager for the RecyclerView
        adapter = new IncomeSourcesAdapter(incomeSources);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        incomeSourcesRecyclerView.setAdapter(adapter);
        incomeSourcesRecyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupIncomeSourcesRecyclerView();
    }
}
