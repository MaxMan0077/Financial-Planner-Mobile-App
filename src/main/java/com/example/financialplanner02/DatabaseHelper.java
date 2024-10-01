package com.example.financialplanner02;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String TAG = "DatabaseHelper";

    private static final String DATABASE_NAME = "financial_planner.db";
    private static final int DATABASE_VERSION = 4;
    private static final String COLUMN_USER_ID = "id";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT UNIQUE," +
                "password TEXT)";
        db.execSQL(CREATE_USERS_TABLE);

        String CREATE_INCOME_SOURCES_TABLE = "CREATE TABLE income_sources (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER," +
                "name TEXT," +
                "amount REAL," +
                "monthly INTEGER," +
                "FOREIGN KEY(user_id) REFERENCES users(id))";
        db.execSQL(CREATE_INCOME_SOURCES_TABLE);

        String CREATE_EXPENSES_TABLE = "CREATE TABLE expenses (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER," +
                "name TEXT," +
                "amount REAL," +
                "monthly INTEGER," +
                "FOREIGN KEY(user_id) REFERENCES users(id))";
        db.execSQL(CREATE_EXPENSES_TABLE);

        String CREATE_SAVINGS_GOALS_TABLE = "CREATE TABLE savings_goals (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER," +
                "name TEXT," +
                "amount REAL," +
                "purchaceDate INTEGER," +
                "FOREIGN KEY(user_id) REFERENCES users(id))";
        db.execSQL(CREATE_SAVINGS_GOALS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS income_sources");
        db.execSQL("DROP TABLE IF EXISTS expenses");
        db.execSQL("DROP TABLE IF EXISTS savings_goals");
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }

    public long addUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("password", password);

        long id = db.insert("users", null, contentValues);
        db.close();

        return id;
    }

    public boolean checkUserExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username = ?", new String[]{username});

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();

        return exists;
    }

    public int authenticateUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username = ? AND password = ?", new String[]{username, password});

        int userId = -1;
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(COLUMN_USER_ID);
            if (columnIndex >= 0) {
                userId = cursor.getInt(columnIndex);
            }
        }
        cursor.close();
        db.close();

        return userId;
    }

    public boolean usernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username = ?", new String[]{username});

        boolean exists = cursor.getCount() > 0;

        cursor.close();
        db.close();

        return exists;
    }

    public void clearDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        onUpgrade(db, DATABASE_VERSION, DATABASE_VERSION);
    }

    public long addIncomeSource(IncomeSource incomeSource, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", incomeSource.getName());
        contentValues.put("amount", incomeSource.getAmount());
        contentValues.put("monthly", incomeSource.isMonthly() ? 1 : 0);
        contentValues.put("user_id", userId);
        db.execSQL("PRAGMA foreign_keys = ON;");

        long result = -1;
        try {
            result = db.insertOrThrow("income_sources", null, contentValues);
        } catch (Exception e) {
            Log.e(TAG, "Error inserting income source", e);
        }
        return result;
    }

    public List<IncomeSource> getIncomeSources(int userId) {
        List<IncomeSource> incomeSources = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM income_sources WHERE user_id = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                int nameColumnIndex = cursor.getColumnIndex("name");
                int amountColumnIndex = cursor.getColumnIndex("amount");
                int monthlyColumnIndex = cursor.getColumnIndex("monthly");

                if (nameColumnIndex != -1 && amountColumnIndex != -1 && monthlyColumnIndex != -1) {
                    String name = cursor.getString(nameColumnIndex);
                    double amount = cursor.getDouble(amountColumnIndex);
                    boolean monthly = cursor.getInt(monthlyColumnIndex) == 1;

                    incomeSources.add(new IncomeSource(name, amount, monthly));
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return incomeSources;
    }

    public double getTotalMonthlyIncome(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(amount) as total FROM income_sources WHERE user_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        double total = 0;
        if (cursor.moveToFirst()) {
            int totalColumnIndex = cursor.getColumnIndex("total");
            if (totalColumnIndex != -1) {
                total = cursor.getDouble(totalColumnIndex);
            }
        }
        cursor.close();
        return total;
    }

    public double getTotalMonthlyExpenditure(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(amount) as total FROM expenses WHERE user_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        double total = 0;
        if (cursor.moveToFirst()) {
            int totalColumnIndex = cursor.getColumnIndex("total");
            if (totalColumnIndex != -1) {
                total = cursor.getDouble(totalColumnIndex);
            }
        }
        cursor.close();
        return total;
    }

    public void deleteIncomeSource(String incomeSourceName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("income_sources", "name = ?", new String[]{incomeSourceName});
    }

    public long addExpense(Expense expense, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", expense.getName());
        contentValues.put("amount", expense.getAmount());
        contentValues.put("monthly", expense.isMonthly() ? 1 : 0);
        contentValues.put("user_id", userId);
        db.execSQL("PRAGMA foreign_keys = ON;");

        long result = -1;
        try {
            result = db.insertOrThrow("expenses", null, contentValues);
        } catch (Exception e) {
            Log.e(TAG, "Error inserting expense", e);
        }
        return result;
    }

    public List<Expense> getExpenses(int userId) {
        List<Expense> expenses = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("expenses", new String[]{"id", "name", "amount", "monthly", "user_id"},
                "user_id=?", new String[]{String.valueOf(userId)}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int nameColumnIndex = cursor.getColumnIndex("name");
                int amountColumnIndex = cursor.getColumnIndex("amount");
                int monthlyColumnIndex = cursor.getColumnIndex("monthly");

                if (nameColumnIndex != -1 && amountColumnIndex != -1 && monthlyColumnIndex != -1) {
                    String name = cursor.getString(nameColumnIndex);
                    double amount = cursor.getDouble(amountColumnIndex);
                    boolean monthly = cursor.getInt(monthlyColumnIndex) == 1;

                    Expense expense = new Expense(name, amount, monthly);
                    expenses.add(expense);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        return expenses;
    }

    public boolean deleteExpense(String expenseName) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete("expenses", "name = ?", new String[]{expenseName});
        return result > 0;
    }

    public void resetIncomeAndExpenses(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Delete non-monthly income sources
        String deleteNonMonthlyIncomeQuery = "DELETE FROM income_sources WHERE user_id = ? AND is_monthly = 0";
        db.execSQL(deleteNonMonthlyIncomeQuery, new String[]{String.valueOf(userId)});

        // Delete non-monthly expenses
        String deleteNonMonthlyExpensesQuery = "DELETE FROM expenses WHERE user_id = ? AND is_monthly = 0";
        db.execSQL(deleteNonMonthlyExpensesQuery, new String[]{String.valueOf(userId)});

        db.close();
    }

    public List<SavingsGoal> getSavingsGoals(int userId) {
        List<SavingsGoal> savingsGoals = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("savings_goals",
                new String[]{"id", "name", "amount", "purchaceDate", "user_id"},
                "user_id=?", new String[]{String.valueOf(userId)}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int nameIndex = cursor.getColumnIndex("name");
                int amountIndex = cursor.getColumnIndex("amount");
                int purchaceDateIndex = cursor.getColumnIndex("purchaceDate");

                if (nameIndex >= 0 && amountIndex >= 0 && purchaceDateIndex >= 0) {
                    String name = cursor.getString(nameIndex);
                    double amount = cursor.getDouble(amountIndex);
                    String purchaceDate = cursor.getString(purchaceDateIndex);

                    SavingsGoal savingsGoal = new SavingsGoal(name, amount, purchaceDate);
                    savingsGoals.add(savingsGoal);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        return savingsGoals;
    }

    public long addSavingsGoal(SavingsGoal savingsGoal, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", savingsGoal.getName());
        values.put("amount", savingsGoal.getTargetAmount());
        values.put("purchaceDate", savingsGoal.getTargetDate());
        values.put("user_id", userId);

        long id = db.insert("savings_goals", null, values);
        db.close();

        return id;
    }

    public SavingsGoal getSavingsGoal(int savingsGoalId, int userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query("savings_goals",
                new String[]{"id", "user_id", "name", "target_amount", "target_date"},
                "id=? AND user_id=?",
                new String[]{String.valueOf(savingsGoalId), String.valueOf(userId)},
                null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex("name");
            int targetAmountIndex = cursor.getColumnIndex("target_amount");
            int targetDateIndex = cursor.getColumnIndex("target_date");

            if (nameIndex >= 0 && targetAmountIndex >= 0 && targetDateIndex >= 0) {
                String name = cursor.getString(nameIndex);
                double targetAmount = cursor.getDouble(targetAmountIndex);
                String targetDate = cursor.getString(targetDateIndex);

                SavingsGoal savingsGoal = new SavingsGoal(name, targetAmount, targetDate);
                cursor.close();
                return savingsGoal;
            }
        }
        return null;
    }

    public void deleteSavingsGoal(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = "name=?";
        String[] selectionArgs = {name};

        db.delete("savings_goals", selection, selectionArgs);
    }
}
