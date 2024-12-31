package com.example.expensesplitting.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.expensesplitting.Contact;

import java.util.ArrayList;
import java.util.List;

public class ContactDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "contacts.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_CONTACTS = "contacts";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_AVATAR = "avatar";
    private static final String COLUMN_IS_FAVORITE = "is_favorite";

    public ContactDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_CONTACTS = "CREATE TABLE " + TABLE_CONTACTS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_EMAIL + " TEXT,"
                + COLUMN_AVATAR + " INTEGER,"
                + COLUMN_IS_FAVORITE + " INTEGER" + ")";
        db.execSQL(CREATE_TABLE_CONTACTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        onCreate(db);
    }

    public boolean addContact(Contact contact) {
        if (isDuplicateContact(contact.getName(), contact.getEmail())) {
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, contact.getName());
        values.put(COLUMN_EMAIL, contact.getEmail());
        values.put(COLUMN_AVATAR, contact.getAvatar());
        values.put(COLUMN_IS_FAVORITE, contact.isFavorite() ? 1 : 0);

        db.insert(TABLE_CONTACTS, null, values);
        db.close();
        return true;
    }

    public List<Contact> getAllContacts() {
        List<Contact> contactList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_CONTACTS;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AVATAR)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_FAVORITE)) == 1
                );
                contactList.add(contact);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return contactList;
    }

    public List<Contact> getFavoriteContacts() {
        List<Contact> favoriteContacts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_CONTACTS + " WHERE " + COLUMN_IS_FAVORITE + " = 1";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AVATAR)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_FAVORITE)) == 1
                );
                favoriteContacts.add(contact);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return favoriteContacts;
    }

    // Method to get a contact by its ID
    public Contact getContactById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_CONTACTS + " WHERE " + COLUMN_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id)});

        if (cursor != null && cursor.moveToFirst()) {
            Contact contact = new Contact(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AVATAR)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_FAVORITE)) == 1
            );
            cursor.close();
            db.close();
            return contact;
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return null;
    }


    public void updateContactFavoriteStatus(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_FAVORITE, contact.isFavorite() ? 1 : 0);

        db.update(TABLE_CONTACTS, values, COLUMN_NAME + " = ?", new String[]{contact.getName()});
        db.close();
    }

    public void editContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, contact.getName());
        values.put(COLUMN_EMAIL, contact.getEmail());
        values.put(COLUMN_AVATAR, contact.getAvatar());
        values.put(COLUMN_IS_FAVORITE, contact.isFavorite() ? 1 : 0);

        db.update(TABLE_CONTACTS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(contact.getId())});
        db.close();
    }

    public void deleteContactById(int contactId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, COLUMN_ID + " = ?", new String[]{String.valueOf(contactId)});
        db.close();
    }

    public void deleteContactByName(String contactName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, COLUMN_NAME + " = ?", new String[]{contactName});
        db.close();
    }

    private boolean isDuplicateContact(String name, String email) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_CONTACTS + " WHERE " + COLUMN_NAME + " = ? AND " + COLUMN_EMAIL + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{name, email});

        boolean isDuplicate = cursor.getCount() > 0;
        cursor.close();
        db.close();

        return isDuplicate;
    }
}
