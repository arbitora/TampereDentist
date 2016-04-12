package com.example.alienegg.tamperedentist;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
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

    public FetchDentistTask(Context context){
        mContext = context;
    }



    private List<DentistObj> getDentistDataFromJSON(String dentistJSON)
    {
        List<DentistObj> mTempDentistList = new ArrayList<>();
        Log.v(LOG_TAG, "in getDentistDataFromJSON");
        try{
            JSONObject dentistCollection = new JSONObject(dentistJSON);
            JSONArray dentistFeatures = dentistCollection.getJSONArray("features");
            // Temporary DentistObj which is populated and added into list.
            DentistObj temp;

            // Loop through JSONArray and add all content into temporary DentistObj
            for (int i = 0; i < dentistFeatures.length(); i++)
            {
                JSONObject dentist = dentistFeatures.getJSONObject(i);
                String _id = dentist.getString("id");
                JSONObject dentistProperties = dentist.getJSONObject("properties");
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
                temp = new DentistObj( _id,  _nimi,  _osoite,
                        _postinumero,  _postitoimipaikka,  _linkURL,  _puhelin);
                mTempDentistList.add(temp);
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
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            final String DENTIST_BASE_URL = "http://opendata.navici.com/tampere/opendata/ows?service=WFS&version=2.0.0&request=GetFeature&typeName=opendata:HAMMASHOITOLAT&outputFormat=json";
            URL url = new URL(DENTIST_BASE_URL);

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
        if (result != null)
        {
            Vector<ContentValues> cVVector = new Vector<ContentValues>(result.size());

            for(DentistObj insertDentist : result){
                ContentValues dentistValues = new ContentValues();
                dentistValues.put(DentistContract.DentistEntry.COLUMN_D_id, insertDentist.dentistID());
                dentistValues.put(DentistContract.DentistEntry.COLUMN_name, insertDentist.dentistName());
                dentistValues.put(DentistContract.DentistEntry.COLUMN_address, insertDentist.dentistOsoite());
                dentistValues.put(DentistContract.DentistEntry.COLUMN_zip, insertDentist.dentistPostinumero());
                dentistValues.put(DentistContract.DentistEntry.COLUMN_city, insertDentist.dentistPostitoimipaikka());
                dentistValues.put(DentistContract.DentistEntry.COLUMN_phone, insertDentist.dentistPuhelin());
                dentistValues.put(DentistContract.DentistEntry.COLUMN_urlLink, insertDentist.dentistLinkURL());
                cVVector.add(dentistValues);
            }

            int inserted = 0;
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(DentistContract.DentistEntry.CONTENT_URI, cvArray);
            }

            Log.d(LOG_TAG, "Fetching completed. " + inserted + " Inserted");
        }
    }



}

