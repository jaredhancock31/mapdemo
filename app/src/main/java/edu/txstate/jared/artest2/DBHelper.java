package edu.txstate.jared.artest2;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by jared on 2/8/16.
 */
public class DBHelper extends SQLiteOpenHelper {

    /* constants */
    public static final String TAG =                    "DBHELPER";
    public static final String DB_NAME =                "geodrop.db";
    public static final String USER_TABLE =             "user_table";
    public static final String USER_ID =                "user_id";
    public static final String NAME =                   "name";
    public static final String SURNAME =                "surname";
    public static final String USERNAME =               "username";
    public static final String EMAIL =                  "email";

    /* named 'ddrop' because of similarity to 'drop table' command in sql. potential fuckups, should consider better names */
    public static final String DDROP_TABLE =             "ddrop_table";
    public static final String DROP_ID =                 "drop_id";
    public static final String SUBMITTER =               "submitter_id";
    public static final String FILENAME =                "filename";
    public static final String DATE_DROPPED =            "date_dropped";

    /* constructor */
    public DBHelper (Context context) {
        super(context, DB_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + USER_TABLE + " (user_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "surname TEXT, " +
                "username TEXT, " +
                "email TEXT)");
        db.execSQL("CREATE TABLE " + DDROP_TABLE + " (drop_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "submitter_id TEXT, " +
                "filename TEXT, " +
                "date_dropped INTEGER)"); // TODO sqlite uses integer for unix time, change if using postgres

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DDROP_TABLE);
        onCreate(db);
    }

    /**
     * Prepares an entry and attempts to insert entry into the SQLite database
     */
    public boolean insertUser(String name, String surname, String username, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(NAME, name);
        cv.put(SURNAME, surname);
        cv.put(USERNAME, username);
        cv.put(EMAIL, email);
        long result = db.insert(USER_TABLE, null, cv);

        if (result == -1)
            return false;
        else
            return true;
    }

    /**
     * Prepares a drop entry and attempts to insert it into the database
     */
    public boolean insertDrop(String submitter_id, String filename, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(SUBMITTER, submitter_id);
        cv.put(FILENAME, filename);
        cv.put(DATE_DROPPED, date);
        long result = db.insert(DDROP_TABLE, null, cv);

        if (result == -1)
            return false;
        else
            return true;
    }


    /* quick entry of some fake data */
    public void spoofUserData() {
        Log.i(TAG, "inside spoofUserData()");
        insertUser("Jared", "Hancock", "jjh84", "jjh84@txstate.edu");
        insertUser("bob", "bobson", "wert4", "sdfs@txstate.edu");
        insertUser("wer", "ttrr", "dfgsq2", "jjhssdd84@txstate.edu");
        insertUser("Jaasdfred", "cvb", "fghjhj4", "jjasdfh84@txstate.edu");
    }

}
