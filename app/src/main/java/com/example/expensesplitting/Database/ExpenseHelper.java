package com.example.expensesplitting.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.expensesplitting.Group.Expense;

import java.util.ArrayList;

public class ExpenseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ExpenseSplitting.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_EXPENSES = "Expenses";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_GROUP_NAME = "group_name";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_AMOUNT = "amount";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_PAID_BY = "paid_by";
    private static final String COLUMN_SPLIT_BY = "split_by";
    private static final String COLUMN_NOTES = "notes";

    public ExpenseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_EXPENSES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_GROUP_NAME + " TEXT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_AMOUNT + " REAL, " +
                COLUMN_CATEGORY + " TEXT, " +
                COLUMN_PAID_BY + " TEXT, " +
                COLUMN_SPLIT_BY + " TEXT, " +
                COLUMN_NOTES + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);
        onCreate(db);
    }

    public boolean addExpense(Expense expense) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_GROUP_NAME, expense.getGroupName());
        values.put(COLUMN_TITLE, expense.getTitle());
        values.put(COLUMN_AMOUNT, expense.getAmount());
        values.put(COLUMN_CATEGORY, expense.getCategory());
        values.put(COLUMN_PAID_BY, expense.getPaidBy());
        values.put(COLUMN_SPLIT_BY, expense.getSplitBy());
        values.put(COLUMN_NOTES, expense.getNotes());

        long result = db.insert(TABLE_EXPENSES, null, values);
        return result != -1;
    }

    public ArrayList<Expense> getExpensesForGroup(String groupName) {
        ArrayList<Expense> expenses = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_EXPENSES, null, COLUMN_GROUP_NAME + "=?",
                new String[]{groupName}, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Expense expense = new Expense(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GROUP_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PAID_BY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SPLIT_BY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTES))
                );
                expenses.add(expense);
            }
            cursor.close();
        }
        return expenses;
    }
}
