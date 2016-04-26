package com.example.alienegg.tamperedentist;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.util.Log;

import com.example.alienegg.tamperedentist.data.DentistContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

/**
 * Created by AlienNest on 12.4.2016.
 */
public class FetchDentistTask extends AsyncTask<Void, Void, List<DentistObj>> {

    private final String LOG_TAG = FetchDentistTask.class.getSimpleName();
    private final Context mContext;
    private int dentistCount = 0; // getCount of dentist in cached database.

    // Get the columns we need.
    private static final String[] DENTIST_COLUMNS = {
            DentistContract.DentistEntry.TABLE_NAME + "." + DentistContract.DentistEntry._ID,
            DentistContract.DentistEntry.COLUMN_D_id,
            DentistContract.DentistEntry.COLUMN_name
			/*
            DentistContract.DentistEntry.COLUMN_address,
            DentistContract.DentistEntry.COLUMN_zip,
            DentistContract.DentistEntry.COLUMN_city,
            DentistContract.DentistEntry.COLUMN_phone,
            DentistContract.DentistEntry.COLUMN_urlLink,
			DentistContract.DentistEntry.COLUMN_latitude,
            DentistContract.DentistEntry.COLUMN_longitude
			*/
    };

    // These are used to find columns stated above.
    // Change these to appropriate values, if above DENTIST_COLUMNS is changed.
    static final int COL_D_id = 1;
    static final int COL_name = 2;
    /* not used in this fragment.
    static final int COL_address = 3;
    static final int COL_zip = 4;
    static final int COL_city = 5;
    static final int COL_phone = 6;
    static final int COL_urlLink = 7;
	static final int COL_latitude = 8;
    static final int COL_longitude = 9;
    */

    // Count is number of dentists in the cached Database.
    public FetchDentistTask(Context context, int count){
        Log.v(LOG_TAG, "in constructor");
        mContext = context;
        dentistCount = count;
    }


    // Parses JSON data from web request.
    private List<DentistObj> getDentistDataFromJSON(String dentistJSON)
    {
        List<DentistObj> mTempDentistList = new ArrayList<>();
        Log.v(LOG_TAG, "in getDentistDataFromJSON");
        try{
            JSONObject dentistCollection = new JSONObject(dentistJSON);

            // If there are same amount of dentists in the database as in the collection,
            // there is no need to parse and add these to database.
            int totalFeatures = dentistCollection.getInt("totalFeatures");
            if (totalFeatures != dentistCount)
            {
                JSONArray dentistFeatures = dentistCollection.getJSONArray("features");
                // Temporary DentistObj which is populated and added into list.
                DentistObj temp;


                // Loop through JSONArray and add all content into temporary DentistObj
                for (int i = 0; i < dentistFeatures.length(); i++)
                {
                    // For each feature, get dentist.
                    JSONObject dentist = dentistFeatures.getJSONObject(i);
                    // From dentist we can parse ID.
                    String _id = dentist.getString("id");
                    // From dentist we need to parse Geomtry Array which holds latitude and longitude.
                    JSONObject dentistGeometry = dentist.getJSONObject("geometry");
                    JSONArray dentistCoordinates = dentistGeometry.getJSONArray("coordinates");
                    // From dentist we parse the dentist properties.
                    JSONObject dentistProperties = dentist.getJSONObject("properties");

                    // Parse dentist data into DentistObj.
                    String _nimi = dentistProperties.getString("NIMI");

                    // Hardcoded string fix.
                    if (_nimi.equals("Kaukajärjven hammashoitola"))
                    {
                        _nimi = null;
                        if (_nimi == null)
                            _nimi = "Kaukajärven hammashoitola";
                    }
                    String _osoite = dentistProperties.getString("OSOITE");
                    String _postinumero = dentistProperties.getString("POSTINUMERO");
                    String _postitoimipaikka = dentistProperties.getString("POSTITOIMIPAIKKA");
                    String _linkURL = dentistProperties.getString("URL");
                    String _puhelin = dentistProperties.getString("PUHELIN");
                    Double _longitude = dentistCoordinates.getDouble(0);
                    Double _latitude = dentistCoordinates.getDouble(1);

                    // See DentistObj parameter constructor for correct order.
                    temp = new DentistObj(_id, _nimi,
                            _osoite, _postinumero, _postitoimipaikka,
                            _linkURL, _puhelin,
                            _longitude, _latitude);
                    mTempDentistList.add(temp);
                }

            }
        }
        catch (JSONException e){
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        // Return list of DentistObjs after sorting them.
        Collections.sort(mTempDentistList);
        return mTempDentistList;
    }

    @Override
    protected List<DentistObj> doInBackground(Void... params) {
        Log.v(LOG_TAG, "in doInBackground");


        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String dentistJsonStr = null;

        try {
            final String OPENDATA_BASE_URL = "http://opendata.navici.com/tampere/opendata/ows?service=WFS&version=2.0.0";
            final String REQUEST_OPEN_TYPE = "&request=GetFeature&typeName=opendata:HAMMASHOITOLAT"; // We are getting dentist data.
            final String OUTPUT_FORMAT = "&outputFormat=json"; // Get data as JSON format.
            final String LOCATION_TYPE = "&srsName=EPSG:4326"; // Get location as Latitude and Longitude.

            // Build URL based on the strings.
            URL url = new URL(OPENDATA_BASE_URL + REQUEST_OPEN_TYPE + OUTPUT_FORMAT + LOCATION_TYPE);

            // Create the request to get HAMMASHOITOLAT data from opendata.navici.com/tampere
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            dentistJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        if (dentistJsonStr != null)
            return getDentistDataFromJSON(dentistJsonStr);
        else
            return null;
    }

    @Override
    protected void onPostExecute(List<DentistObj> result){
        Log.v(LOG_TAG, "in onPostExecute");
        // If there are results, create ContentValue vector out of the DentistObj list.
        if (result != null)
        {
            Vector<ContentValues> cVVector = new Vector<ContentValues>(result.size());

            for(DentistObj insertDentist : result){
                ContentValues dentistValues = new ContentValues();
                // Link the Dentist's data with the columns in the database found in DentistContract
                dentistValues.put(DentistContract.DentistEntry.COLUMN_D_id, insertDentist.getId());
                dentistValues.put(DentistContract.DentistEntry.COLUMN_name, insertDentist.getNimi());
                dentistValues.put(DentistContract.DentistEntry.COLUMN_address, insertDentist.getOsoite());
                dentistValues.put(DentistContract.DentistEntry.COLUMN_zip, insertDentist.getPostinumero());
                dentistValues.put(DentistContract.DentistEntry.COLUMN_city, insertDentist.getPostitoimipaikka());
                dentistValues.put(DentistContract.DentistEntry.COLUMN_phone, insertDentist.getPuhelin());
                dentistValues.put(DentistContract.DentistEntry.COLUMN_urlLink, insertDentist.getLinkURL());
                dentistValues.put(DentistContract.DentistEntry.COLUMN_latitude, insertDentist.getLatitude());
                dentistValues.put(DentistContract.DentistEntry.COLUMN_longitude, insertDentist.getLongitude());
                cVVector.add(dentistValues);
            }

            int inserted = 0; // For log message, counts how many rows were inserted.
            if (cVVector.size() > 0) {
                // Convert vector into array so it can be inserted into database.
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(DentistContract.DentistEntry.CONTENT_URI, cvArray);
            }

            Log.d(LOG_TAG, "Fetching completed. " + inserted + " Inserted");
        }
    }



}

