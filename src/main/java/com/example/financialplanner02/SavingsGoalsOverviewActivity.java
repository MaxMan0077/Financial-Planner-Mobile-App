package com.example.financialplanner02;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback;
import androidx.annotation.NonNull;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class SavingsGoalsOverviewActivity extends AppCompatActivity {

    private FloatingActionButton addSavingsGoalButton;
    private RecyclerView recyclerView;
    private DatabaseHelper dbHelper;
    private SavingsGoalsAdapter adapter;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savingsgoalsoverview);

        userId = getIntent().getIntExtra("userId", -1);

        dbHelper = new DatabaseHelper(this);
        addSavingsGoalButton = findViewById(R.id.fab);
        recyclerView = findViewById(R.id.recyclerView);

        setupSavingsGoalsRecyclerView();

        addSavingsGoalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SavingsGoalsOverviewActivity.this, AddSavingsGoalActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
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
                        Intent intentHome = new Intent(SavingsGoalsOverviewActivity.this, HomePageActivity.class);
                        intentHome.putExtra("userId", userId);
                        startActivity(intentHome);
                        return true;
                    case R.id.seeAllExpenses:
                        // navigate to ExpensesActivity
                        Intent intentExpenses = new Intent(SavingsGoalsOverviewActivity.this, expensesActivity.class);
                        intentExpenses.putExtra("userId", userId);
                        startActivity(intentExpenses);
                        return true;
                    case R.id.editIncome:
                        Intent intentIncome = new Intent(SavingsGoalsOverviewActivity.this, IncomeSourcesActivity.class);
                        intentIncome.putExtra("userId", userId);
                        startActivity(intentIncome);
                        break;
                }
                return false;
            }
        });

        Button linkButton = findViewById(R.id.linkButton);
        linkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUrl("https://www.moneysavingexpert.com/savings/which-saving-account/");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupSavingsGoalsRecyclerView();
    }

    private void setupSavingsGoalsRecyclerView() {
        // Get the list of savings goals from the database
        List<SavingsGoal> savingsGoals = dbHelper.getSavingsGoals(userId);

        // Set up the adapter and layout manager for the RecyclerView
        SavingsGoalsAdapter.OnItemClickListener listener = new SavingsGoalsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                SavingsGoal savingsGoal = savingsGoals.get(position);
                Intent intent = new Intent(SavingsGoalsOverviewActivity.this, SavingsGoalDetailActivity.class);
                intent.putExtra("savingsGoal", savingsGoal);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (position >= 0 && position < savingsGoals.size()) {
                    SavingsGoal savingsGoal = savingsGoals.get(position);

                    dbHelper.deleteSavingsGoal(savingsGoal.getName());

                    savingsGoals.remove(position);
                    adapter.notifyItemRemoved(position);
                }
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);

        adapter = new SavingsGoalsAdapter(savingsGoals, listener);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
    }

    public void openUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
}