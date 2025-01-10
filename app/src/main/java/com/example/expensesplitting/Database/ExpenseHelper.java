package com.example.expensesplitting.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.expensesplitting.Group.Expense;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

public class ExpenseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ExpenseSplitting.db";
    private static final int DATABASE_VERSION = 3; // Incremented database version

    private static final String TABLE_EXPENSES = "Expenses";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_GROUP_ID = "group_id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_AMOUNT = "amount";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_PAID_BY = "paid_by";
    private static final String COLUMN_SPLIT_BY = "split_by";
    private static final String COLUMN_SPLIT_DETAILS = "split_details"; // New column
    private static final String COLUMN_NOTES = "notes";

    public ExpenseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_EXPENSES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_GROUP_ID + " INTEGER, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_AMOUNT + " REAL, " +
                COLUMN_CATEGORY + " TEXT, " +
                COLUMN_PAID_BY + " TEXT, " +
                COLUMN_SPLIT_BY + " TEXT, " +
                COLUMN_SPLIT_DETAILS + " TEXT, " + // New column
                COLUMN_NOTES + " TEXT)";
        db.execSQL(createTable);
        Log.d("ExpenseHelper", "Database table created: " + TABLE_EXPENSES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) { // Add the `split_details` column in version 3
            db.execSQL("ALTER TABLE " + TABLE_EXPENSES + " ADD COLUMN " + COLUMN_SPLIT_DETAILS + " TEXT");
            Log.d("ExpenseHelper", "Added column split_details to table " + TABLE_EXPENSES);
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("ExpenseHelper", "Downgrading database from version " + oldVersion + " to " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);
        onCreate(db);
    }

    /**
     * Adds a new expense to the database.
     *
     * @param expense The expense to add.
     * @return true if the expense was added successfully, false otherwise.
     */
    public boolean addExpense(Expense expense) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_GROUP_ID, expense.getGroupId());
        values.put(COLUMN_TITLE, expense.getTitle());
        values.put(COLUMN_AMOUNT, expense.getAmount());
        values.put(COLUMN_CATEGORY, expense.getCategory());
        values.put(COLUMN_PAID_BY, expense.getPaidBy());
        values.put(COLUMN_SPLIT_BY, expense.getSplitBy());
        values.put(COLUMN_SPLIT_DETAILS, expense.getSplitDetails()); // Add split details
        values.put(COLUMN_NOTES, expense.getNotes());

        long result = db.insert(TABLE_EXPENSES, null, values);
        db.close();

        if (result == -1) {
            Log.e("ExpenseHelper", "Failed to insert expense: " + expense);
            return false;
        } else {
            Log.d("ExpenseHelper", "Expense inserted successfully: " + expense);
            return true;
        }
    }

    /**
     * Retrieves all expenses for a specific group as a Cursor.
     *
     * @param groupId The ID of the group.
     * @return A Cursor pointing to the result set.
     */
    public Cursor getExpensesCursorForGroup(long groupId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_EXPENSES, // Table name
                null, // All columns
                COLUMN_GROUP_ID + "=?", // WHERE clause
                new String[]{String.valueOf(groupId)}, // WHERE clause arguments
                null, // GROUP BY clause
                null, // HAVING clause
                null // ORDER BY clause
        );
        Log.d("ExpenseHelper", "Fetched expenses Cursor for groupId: " + groupId);
        return cursor;
    }

    /**
     * Retrieves all expenses for a specific group as a list of Expense objects.
     *
     * @param groupId The ID of the group.
     * @return A list of Expense objects.
     */
    public ArrayList<Expense> getExpensesForGroup(long groupId) {
        ArrayList<Expense> expenses = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Gson gson = new Gson(); // Gson instance to deserialize split_details
        Type type = new TypeToken<Map<String, Double>>() {}.getType();

        try (Cursor cursor = db.query(TABLE_EXPENSES, null, COLUMN_GROUP_ID + "=?",
                new String[]{String.valueOf(groupId)}, null, null, null)) {

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String splitDetailsJson = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SPLIT_DETAILS));
                    Map<String, Double> splitDetailsMap = null;

                    if (splitDetailsJson != null) {
                        splitDetailsMap = gson.fromJson(splitDetailsJson, type); // Convert JSON String to Map
                        Log.d("ExpenseHelper", "Split Details Map: " + splitDetailsMap);
                    }

                    Expense expense = new Expense(
                            cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_GROUP_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                            cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PAID_BY)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SPLIT_BY)),
                            splitDetailsJson, // Keep the JSON string in Expense
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTES))
                    );
                    expenses.add(expense);
                }
                Log.d("ExpenseHelper", "Fetched " + expenses.size() + " expenses for groupId: " + groupId);
            }
        } catch (Exception e) {
            Log.e("ExpenseHelper", "Error fetching expenses for groupId: " + groupId, e);
        } finally {
            db.close();
        }
        return expenses;
    }
}