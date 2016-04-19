package com.example.alienegg.tamperedentist;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.alienegg.tamperedentist.data.DentistContract;

public class DentistDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = DentistDetailFragment.class.getSimpleName();

    // Text fields to populate with data.
    private TextView dentistTitleTextView;
    private TextView phoneTextView;
    private TextView linkURLTextView;
    private TextView streetAddressTextView;
    private TextView ZIP_CityTextView;

    // Data used in opening intents.
    private String phoneNumber; // tel: prefix required when calling intent.
    private String streetAddress;
    private String URLlink;
    private String zipCode;
    private String postalCity;

    private Double latitude;
    private Double longitude;

    // For loading appropriate icons into them.
    private ImageButton Maps;
    private ImageButton Phone;
    private ImageButton Browser;

    public DentistDetailFragment(){
        //setHasOptionsMenu(false);
    }


    private static final int DETAIL_LOADER = 0;

    // Get the columns we need.
    private static final String[] DENTIST_COLUMNS = {
            DentistContract.DentistEntry.TABLE_NAME + "." + DentistContract.DentistEntry._ID,
            DentistContract.DentistEntry.COLUMN_D_id,
            DentistContract.DentistEntry.COLUMN_name,
            DentistContract.DentistEntry.COLUMN_address,
            DentistContract.DentistEntry.COLUMN_zip,
            DentistContract.DentistEntry.COLUMN_city,
            DentistContract.DentistEntry.COLUMN_phone,
            DentistContract.DentistEntry.COLUMN_urlLink,
            DentistContract.DentistEntry.COLUMN_latitude,
            DentistContract.DentistEntry.COLUMN_longitude
    };
    // These correspond the same values defined in DentistFragment.java, so we can easily use these to identify the columns in the code.
    static final int COL_D_id = 1;
    static final int COL_name = 2;
    static final int COL_address = 3;
    static final int COL_zip = 4;
    static final int COL_city = 5;
    static final int COL_phone = 6;
    static final int COL_urlLink = 7;
    static final int COL_latitude = 8;
    static final int COL_longitude = 9;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "in onCreate");
        super.onCreate(savedInstanceState);

        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(false);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(LOG_TAG, "in onCreateView");

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        dentistTitleTextView =(TextView)rootView.findViewById(R.id.detailDentistNameTXTview);
        phoneTextView =(TextView)rootView.findViewById(R.id.phoneTXTview);
        linkURLTextView =(TextView)rootView.findViewById(R.id.linkTXTview);
        streetAddressTextView=(TextView)rootView.findViewById(R.id.streetAddressTXTview);
        ZIP_CityTextView=(TextView)rootView.findViewById(R.id.ZIPcityTXTview);

        Maps = (ImageButton)rootView.findViewById(R.id.GoogleMapsImageView);
        Phone = (ImageButton)rootView.findViewById(R.id.phoneImageView);
        Browser = (ImageButton)rootView.findViewById(R.id.browserImageView);

        // Open google maps with the given latitude and longitude.
        Maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOG_TAG, "in onClick Maps Button");
                // Old Uri used streetaddress to find location, new one uses latitude and longitude.
                //Uri mapsLocation = Uri.parse("geo:0,0?q=" + streetAddress);
                //Uri mapsLocation = Uri.parse("geo:" + latitude + "," + longitude);
                Uri mapsLocation = Uri.parse("geo:" + latitude + "," + longitude + "?q=" + streetAddress);
                Intent mapsI = new Intent(Intent.ACTION_VIEW, mapsLocation);
                try{
                    startActivity(mapsI);
                } catch(Exception e){
                    Log.v(LOG_TAG, "in onClick Maps Button; No Maps application?");
                    e.printStackTrace();
                }
            }
        });

        // Open dialer with the given phone number.
        Phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOG_TAG, "in onClick Phone Button");
                Intent call = new Intent(Intent.ACTION_DIAL);
                call.setData(Uri.parse("tel:" + phoneNumber));
                try{
                    startActivity(call);
                }catch(Exception e){
                    Log.v(LOG_TAG, "in onClick Phone Button; No Dialer?");
                    e.printStackTrace();
                }
            }
        });

        // Open the browser with the given web address.
        Browser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOG_TAG, "in onClick Browser Button");
                Intent browser = new Intent(Intent.ACTION_VIEW);
                browser.setData(Uri.parse(URLlink));
                try{
                    startActivity(browser);
                } catch(Exception e){
                    Log.v(LOG_TAG, "in onClick Browser Button; No Browser?");
                    e.printStackTrace();
                }
            }
        });



        // Populate fields via savedInstanceState.
        if (savedInstanceState != null)
        {
            phoneNumber = savedInstanceState.getString("PUHELIN");
            URLlink = savedInstanceState.getString("URL");
            streetAddress = savedInstanceState.getString("OSOITE")
                    + " " + savedInstanceState.getString("POSTINUMERO")
                    + " " + savedInstanceState.getString("POSTITOIMIPAIKKA");
            latitude = savedInstanceState.getDouble("latitude");
            longitude = savedInstanceState.getDouble("longitude");
            dentistTitleTextView.setText(savedInstanceState.getString("NIMI"));
            phoneTextView.setText(savedInstanceState.getString("PUHELIN"));
            linkURLTextView.setText(savedInstanceState.getString("URL"));
            streetAddressTextView.setText(savedInstanceState.getString("OSOITE"));
            ZIP_CityTextView.setText(savedInstanceState.getString("POSTINUMERO")
                    + " " + savedInstanceState.getString("POSTITOIMIPAIKKA"));
        }


        return rootView;
    }


    // Save data for recreation.
    @Override
    public void onSaveInstanceState(Bundle outstate)
    {
        super.onSaveInstanceState(outstate);

        outstate.putString("PUHELIN", phoneNumber);
        outstate.putString("URL", URLlink);
        outstate.putString("OSOITE", streetAddressTextView.getText().toString());
        outstate.putString("POSTITOIMIPAIKKA", postalCity);
        outstate.putString("POSTINUMERO", zipCode);
        outstate.putString("NIMI", dentistTitleTextView.getText().toString());
        outstate.putDouble("latitude", latitude);
        outstate.putDouble("longitude", longitude);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "In onCreateLoader");
        Intent intent = getActivity().getIntent();
        if (intent == null) {
            return null;
        }

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(),
                intent.getData(),
                DENTIST_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "In onLoadFinished");

        // Populate fields and variables with the data in the cursor.
        if (!data.moveToFirst()){return;}

            phoneNumber = data.getString(COL_phone);
            URLlink = data.getString(COL_urlLink);
            zipCode = data.getString(COL_zip);
            postalCity = data.getString(COL_city);
            streetAddress = data.getString(COL_address)
                    + " " + zipCode
                    + " " + postalCity;

            dentistTitleTextView.setText(data.getString(COL_name));
            phoneTextView.setText(phoneNumber);
            linkURLTextView.setText(URLlink);
            streetAddressTextView.setText(data.getString(COL_address));
            ZIP_CityTextView.setText(zipCode
                    + " " + postalCity);
        latitude = data.getDouble(COL_latitude);
        longitude = data.getDouble(COL_longitude);

        Log.d("Latitude ", latitude.toString());
        Log.d("Longitude ", longitude.toString());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}
