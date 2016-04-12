package com.example.alienegg.tamperedentist.data;

/**
 * Created by AlienNest on 12.4.2016.
 */

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the weather database.
 */
public class DentistContract{

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.example.alienegg.tamperedentist";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_DENTIST = "dentist";


    public static final class DentistEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_DENTIST).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY;

        // Table name
        public static final String TABLE_NAME = "dentist";

        // Stores the ID of the dentists.
        public static final String COLUMN_D_id = "D_id";
        // Stores the name of the dentist.
        public static final String COLUMN_name = "name";
        // Stores the streetaddress for the dentist.
        public static final String COLUMN_address = "address";
        // Stores the zip code of the dentist.
        public static final String COLUMN_zip = "zip";
        // Stores the city of the dentist.
        public static final String COLUMN_city = "city";
        // Stores the dentists's phone number.
        public static final String COLUMN_phone = "phone";
        // Stores the URL link of the dentist.
        public static final String COLUMN_urlLink = "urlLink";


        public static Uri buildDentistUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildDentistIdUri(String searchID){
            return CONTENT_URI.buildUpon().appendQueryParameter(COLUMN_D_id, searchID).build();
        }

    }
}