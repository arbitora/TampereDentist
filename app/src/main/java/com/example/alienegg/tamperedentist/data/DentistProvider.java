package com.example.alienegg.tamperedentist.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * Created by AlienNest on 12.4.2016.
 */
public class DentistProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DentistDbHelper mOpenHelper;

    // URI Matcher codes for different queries.
    static final int DENTIST = 100; // Return all data
    static final int DENTIST_WITH_ID = 101; // Return data with specific Dentist ID (NOTE: Not table's auto increment ID)
    static final int DENTIST_COUNT = 102; // Return only count. Made for checking if data exists in database already.


    // Custom URI matcher which will compare the sent URI with these and return the corresponding UriMatcher.
    // If no match is found or is just the APP package name, returns NO_MATCH
    static UriMatcher buildUriMatcher(){


        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DentistContract.CONTENT_AUTHORITY; // APP Package name, which is used as a base.

        matcher.addURI(authority, DentistContract.PATH_DENTIST, DENTIST);
        matcher.addURI(authority, DentistContract.PATH_DENTIST + "/#", DENTIST_COUNT);
        matcher.addURI(authority, DentistContract.PATH_DENTIST + "/*", DENTIST_WITH_ID);

        return matcher;
    }

    // String for getDentistByID query.
    private static final String sDentistsByID = DentistContract.DentistEntry.TABLE_NAME + "." + DentistContract.DentistEntry.COLUMN_D_id + " = ? ";

    // Query which will return the cursor to corresponding dentist with the given DentistID.
    private Cursor getDentistByID(Uri uri, String[] projection, String sortOrder) {

        String dentistID = DentistContract.DentistEntry.getDentistIDFromUri(uri);
        return  mOpenHelper.getReadableDatabase().query(
                DentistContract.DentistEntry.TABLE_NAME,
                projection,
                sDentistsByID,
                new String[]{dentistID},
                null,
                null,
                sortOrder
        );
    }

    // Create new DentistDbHelper for later use.
    @Override
    public boolean onCreate() {
        mOpenHelper = new DentistDbHelper(getContext());
        return true;
    }

    // Returns what type of URI this is. TYPE = Returns multiple values, ITEM_TYPE = Returns single value.
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case DENTIST_COUNT:
                return DentistContract.DentistEntry.CONTENT_ITEM_TYPE;
            case DENTIST_WITH_ID:
                return DentistContract.DentistEntry.CONTENT_ITEM_TYPE;
            case DENTIST:
                return DentistContract.DentistEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

    }

    /*
        Query URIs.
        Each case will handle a query to database and return a cursor with the data it queries.
        If no case matches, URI was unsupported or incorrect.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor returnCursor;
        Log.d("URI: ", uri.toString());
        switch (sUriMatcher.match(uri)){
            case DENTIST_COUNT: {
                returnCursor = mOpenHelper.getReadableDatabase().query(
                        DentistContract.DentistEntry.TABLE_NAME,
                        new String[]{"COUNT(*) AS count"},
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null
                );
                break;
            }

            case DENTIST_WITH_ID:
            {
                returnCursor = getDentistByID(uri, projection, sortOrder);
                break;
            }
            case DENTIST:
            {
                returnCursor = mOpenHelper.getReadableDatabase().query(
                        DentistContract.DentistEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return returnCursor;
    }

    /*
    Query URIs.
    Each case will insert data into the Database.
    Only one case is handled here, because we only add data with all the columns.
    Exception thrown if incorrect or unsupported URI is used.
    */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case DENTIST: {
                long _id = db.insert(DentistContract.DentistEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = DentistContract.DentistEntry.buildDentistUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    /*
        DELETION URIs.
        Not used much, but is declared here just in case.
    */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        if ( null == selection ) selection = "1";
        switch (match) {
            case DENTIST:
                rowsDeleted = db.delete(
                        DentistContract.DentistEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // If some rows were deleted, send a notification about the change.
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    /*
        UPDATE URIs.
        Updates dentist values based on input values.
        These are not used currently used much either.
    */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case DENTIST:
                rowsUpdated = db.update(DentistContract.DentistEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    /*
        Special insert.
        Instead of looping normal insert multiple times, we can insert bunch of items with a single insert.
        Values will have all the Dentist data within it and insert all those into the table.
    */
    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case DENTIST:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(DentistContract.DentistEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
