package edu.txstate.jared.artest2;


import android.content.ContentValues;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by jared on 2/8/16.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String TAG =                    "DBHELPER";
    public static final String DB_NAME =                "users.db";
    public static final String TABLE_NAME =             "user_table";
    public static final String USER_ID =                "user_id";
    public static final String NAME =                   "name";
    public static final String SURNAME =                "surname";
    public static final String USERNAME =               "username";
    public static final String EMAIL =                  "email";


    /**
     * use this constructor
     * @param context
     */
    public DBHelper (Context context) {
        super(context, DB_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (user_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "surname TEXT, " +
                "username TEXT, " +
                "email TEXT)");

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /**
     * Prepares an entry and attempts to insert entry into the SQLite database
     */
    public boolean insertData(String name, String surname, String username, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(NAME, name);
        cv.put(SURNAME, surname);
        cv.put(USERNAME, username);
        cv.put(EMAIL, email);
        long result = db.insert(TABLE_NAME, null, cv);

        if (result == -1)
            return false;
        else
            return true;
    }

    /* quick entry of some fake data */
    public void spoofData() {
        Log.i(TAG, "inside spoofData()");
        insertData("Jared", "Hancock", "jjh84", "jjh84@txstate.edu");
        insertData("bob", "bobson", "wert4", "sdfs@txstate.edu");
        insertData("wer", "ttrr", "dfgsq2", "jjhssdd84@txstate.edu");
        insertData("Jaasdfred", "cvb", "fghjhj4", "jjasdfh84@txstate.edu");
    }

}
