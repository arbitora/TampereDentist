package com.example.alienegg.tamperedentist.data;

/**
 * Created by AlienNest on 12.4.2016.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// Import the contract
import com.example.alienegg.tamperedentist.data.DentistContract.DentistEntry;

public class DentistDbHelper extends SQLiteOpenHelper {

    /*
        VERSION MUST ALWAYS CHANGE IF CHANGES HAVE BEEN MADE INTO DATABASE CODE.

        VERSION = 1, first build
        VERSION = 2, added Latitude and Longitude columns.
    */
    private static final int DATABASE_VERSION = 2;

    // Declare database name.
    static final String DATABASE_NAME = "dentists.db";

    // Constructor
    public DentistDbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Creates a table to hold dentist data.
        final String SQL_CREATE_DENTIST_TABLE = "CREATE TABLE " + DentistEntry.TABLE_NAME + " (" +
                // Unique ID, which will have Autoincrement
                DentistEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // Create the rest of the columns to store Dentist's data.
                DentistEntry.COLUMN_D_id + " TEXT NOT NULL, " +
                DentistEntry.COLUMN_name + " TEXT NOT NULL, " +
                DentistEntry.COLUMN_address + " TEXT NOT NULL, " +
                DentistEntry.COLUMN_zip + " INTEGER NOT NULL," +
                DentistEntry.COLUMN_city + " TEXT NOT NULL, " +
                DentistEntry.COLUMN_phone + " TEXT NOT NULL, " +
                DentistEntry.COLUMN_urlLink + " TEXT NOT NULL, " +
                DentistEntry.COLUMN_latitude + " DOUBLE NOT NULL, " +
                DentistEntry.COLUMN_longitude + " DOUBLE NOT NULL, " +
                // To assure the application will have only one dentist which is found from the dentist ID.
                " UNIQUE (" + DentistEntry.COLUMN_D_id + ") ON CONFLICT REPLACE);";

        // Create the table command.
        sqLiteDatabase.execSQL(SQL_CREATE_DENTIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        /*
            This database stores the online data in a cache, so if changes are made,
            we can discard all data and load the data into it's new proper locations.

            This function will only run if DATABASE_VERSION has been changed.
        */
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DentistEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}