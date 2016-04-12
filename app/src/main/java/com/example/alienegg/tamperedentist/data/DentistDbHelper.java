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

    private static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "dentists.db";

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

                // the ID of the location entry associated with this weather data
                DentistEntry.COLUMN_D_id + " TEXT NOT NULL, " +
                DentistEntry.COLUMN_name + " TEXT NOT NULL, " +
                DentistEntry.COLUMN_address + " TEXT NOT NULL, " +
                DentistEntry.COLUMN_zip + " INTEGER NOT NULL," +
                DentistEntry.COLUMN_city + " TEXT NOT NULL, " +
                DentistEntry.COLUMN_phone + " TEXT NOT NULL, " +
                DentistEntry.COLUMN_urlLink + " TEXT NOT NULL, " +

                // To assure the application will have only one dentist which is found from the dentist ID.
                " UNIQUE (" + DentistEntry.COLUMN_D_id + ") ON CONFLICT REPLACE);";

        // Create the table command.
        sqLiteDatabase.execSQL(SQL_CREATE_DENTIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DentistEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}