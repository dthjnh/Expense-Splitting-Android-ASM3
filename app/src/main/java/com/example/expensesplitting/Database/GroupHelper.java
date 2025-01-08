package com.example.expensesplitting.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GroupHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "GroupManager.db";
    private static final int DATABASE_VERSION = 2;

    // Table names
    public static final String TABLE_GROUPS = "groups";
    public static final String TABLE_PARTICIPANTS = "group_participants";

    // Common columns
    public static final String COLUMN_ID = "id";

    // Groups table columns
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_CURRENCY = "currency";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_IMAGE = "image";

    // Participants table columns
    public static final String COLUMN_GROUP_ID = "group_id";
    public static final String COLUMN_PARTICIPANT_NAME = "participant_name";

    public GroupHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create groups table
        String CREATE_GROUPS_TABLE = "CREATE TABLE " + TABLE_GROUPS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT NOT NULL, "
                + COLUMN_DESCRIPTION + " TEXT, "
                + COLUMN_CURRENCY + " TEXT, "
                + COLUMN_CATEGORY + " TEXT, "
                + COLUMN_IMAGE + " TEXT)";
        db.execSQL(CREATE_GROUPS_TABLE);

        // Create participants table
        String CREATE_PARTICIPANTS_TABLE = "CREATE TABLE " + TABLE_PARTICIPANTS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_GROUP_ID + " INTEGER NOT NULL, "
                + COLUMN_PARTICIPANT_NAME + " TEXT NOT NULL, "
                + "FOREIGN KEY (" + COLUMN_GROUP_ID + ") REFERENCES " + TABLE_GROUPS + "(" + COLUMN_ID + ") ON DELETE CASCADE)";
        db.execSQL(CREATE_PARTICIPANTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PARTICIPANTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUPS);
        onCreate(db);
    }

    /**
     * Inserts a new group into the database and automatically adds "You" as a participant.
     */
    public long insertGroup(String name, String description, String currency, String category, String image) {
        SQLiteDatabase db = this.getWritableDatabase();
        long groupId = -1;

        try {
            db.beginTransaction();
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME, name);
            values.put(COLUMN_DESCRIPTION, description);
            values.put(COLUMN_CURRENCY, currency);
            values.put(COLUMN_CATEGORY, category);
            values.put(COLUMN_IMAGE, image);

            groupId = db.insert(TABLE_GROUPS, null, values);

            if (groupId != -1) {
                addParticipantToGroupInternal(db, groupId, "You"); // Automatically add "You" as a participant
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }

        return groupId;
    }

    /**
     * Updates an existing group in the database.
     */
    public boolean updateGroup(long groupId, String name, String description, String currency, String category, String imageUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_DESCRIPTION, description);
        values.put(COLUMN_CURRENCY, currency);
        values.put(COLUMN_CATEGORY, category);
        values.put(COLUMN_IMAGE, imageUri);

        int rowsUpdated = db.update(TABLE_GROUPS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(groupId)});
        db.close();
        return rowsUpdated > 0;
    }

    /**
     * Updates the group image for a specific group.
     */
    public boolean updateGroupImage(long groupId, String imageUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IMAGE, imageUri);

        int rowsUpdated = db.update(TABLE_GROUPS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(groupId)});
        db.close();
        return rowsUpdated > 0;
    }

    /**
     * Adds a participant to a specific group.
     */
    public void addParticipantToGroup(long groupId, String participantName) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            addParticipantToGroupInternal(db, groupId, participantName);
        } finally {
            db.close();
        }
    }

    private void addParticipantToGroupInternal(SQLiteDatabase db, long groupId, String participantName) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_GROUP_ID, groupId);
        values.put(COLUMN_PARTICIPANT_NAME, participantName);

        db.insert(TABLE_PARTICIPANTS, null, values);
    }

    /**
     * Fetches all participants for a specific group as a Cursor.
     */
    public Cursor getParticipantsForGroup(long groupId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_PARTICIPANTS,
                new String[]{COLUMN_PARTICIPANT_NAME}, // Select only participant names
                COLUMN_GROUP_ID + " = ?",              // Where clause
                new String[]{String.valueOf(groupId)}, // Where arguments
                null,                                  // Group by
                null,                                  // Having
                null);                                 // Order by
    }

    /**
     * Fetches all groups from the database.
     */
    public Cursor getAllGroups() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_GROUPS, null, null, null, null, null, COLUMN_NAME + " ASC");
    }

    /**
     * Fetches a specific group by ID.
     */
    public Cursor getGroupById(long groupId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_GROUPS,
                null, // Select all columns
                COLUMN_ID + " = ?", // WHERE clause
                new String[]{String.valueOf(groupId)}, // WHERE arguments
                null, null, null); // GroupBy, Having, OrderBy
    }

    /**
     * Deletes a specific group and its participants.
     */
    public boolean deleteGroup(long groupId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int deletedRows = db.delete(TABLE_GROUPS, COLUMN_ID + " = ?", new String[]{String.valueOf(groupId)});
        db.close();
        return deletedRows > 0;
    }

    /**
     * Deletes a specific participant from a group.
     */
    public boolean deleteParticipant(long groupId, String participantName) {
        SQLiteDatabase db = this.getWritableDatabase();
        int deletedRows = db.delete(TABLE_PARTICIPANTS,
                COLUMN_GROUP_ID + " = ? AND " + COLUMN_PARTICIPANT_NAME + " = ?",
                new String[]{String.valueOf(groupId), participantName});
        db.close();
        return deletedRows > 0;
    }
}
