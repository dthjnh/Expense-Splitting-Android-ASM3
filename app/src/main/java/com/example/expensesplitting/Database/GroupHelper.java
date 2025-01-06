package com.example.expensesplitting.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class GroupHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "GroupManager.db";
    private static final int DATABASE_VERSION = 2;

    // Table names
    private static final String TABLE_GROUPS = "groups";
    private static final String TABLE_PARTICIPANTS = "group_participants";

    // Common columns
    private static final String COLUMN_ID = "id";

    // Groups table columns
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_CURRENCY = "currency";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_IMAGE = "image";

    // Participants table columns
    private static final String COLUMN_GROUP_ID = "group_id";
    private static final String COLUMN_PARTICIPANT_NAME = "participant_name";

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
        if (oldVersion < DATABASE_VERSION) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PARTICIPANTS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUPS);
            onCreate(db);
        }
    }

    /**
     * Inserts a new group into the database.
     *
     * @param name        The name of the group.
     * @param description The description of the group.
     * @param currency    The currency of the group.
     * @param category    The category of the group.
     * @param image       The image associated with the group.
     * @return The ID of the newly created group.
     */
    public long insertGroup(String name, String description, String currency, String category, String image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_DESCRIPTION, description);
        values.put(COLUMN_CURRENCY, currency);
        values.put(COLUMN_CATEGORY, category);
        values.put(COLUMN_IMAGE, image);

        long id = db.insert(TABLE_GROUPS, null, values);
        db.close();
        return id;
    }

    /**
     * Adds a participant to a specific group.
     *
     * @param groupId         The ID of the group.
     * @param participantName The name of the participant.
     */
    public void addParticipantToGroup(long groupId, String participantName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_GROUP_ID, groupId);
        values.put(COLUMN_PARTICIPANT_NAME, participantName);

        db.insert(TABLE_PARTICIPANTS, null, values);
        db.close();
    }

    /**
     * Fetches all participants for a specific group as a Cursor.
     *
     * @param groupId The ID of the group.
     * @return A Cursor containing the participants of the group.
     */
    public Cursor getParticipantsForGroup(long groupId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_PARTICIPANTS,
                new String[]{COLUMN_PARTICIPANT_NAME}, // Select only participant names
                COLUMN_GROUP_ID + " = ?",              // Where clause
                new String[]{String.valueOf(groupId)}, // Where arguments
                null, null, null);
    }

    /**
     * Fetches all groups from the database.
     *
     * @return A Cursor containing all groups.
     */
    public Cursor getAllGroups() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_GROUPS, null, null, null, null, null, COLUMN_NAME + " ASC");
    }

    /**
     * Deletes a specific group and its participants.
     *
     * @param groupId The ID of the group to delete.
     * @return True if the group was deleted successfully, false otherwise.
     */
    public boolean deleteGroup(long groupId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int deletedRows = db.delete(TABLE_GROUPS, COLUMN_ID + " = ?", new String[]{String.valueOf(groupId)});
        db.close();
        return deletedRows > 0;
    }

    /**
     * Deletes a specific participant from a group.
     *
     * @param groupId         The ID of the group.
     * @param participantName The name of the participant to delete.
     * @return True if the participant was deleted successfully, false otherwise.
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
