package com.example.alienegg.tamperedentist;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
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
    static final String DETAIL_URI = "URI";
    private Uri mUri;

    // Text fields to populate with data.
    private TextView dentistTitleTextView;
    private TextView phoneTextView;
    private TextView linkURLTextView;
    private TextView streetAddressTextView;
    private TextView ZIP_CityTextView;

    // Data used in opening intents.
    private String dentistTitle;
    private String phoneNumber; // tel: prefix required when calling intent.
    private String streetAddress;
    private String URLlink;
    private String zipCode;
    private String postalCity;

    private Double latitude = 0.0;
    private Double longitude = 0.0;

    // For loading appropriate icons into them.
    private ImageButton Maps;
    private ImageButton Phone;
    private ImageButton Browser;

    public DentistDetailFragment(){
    }

    private static final String MAPS_BUTTON_ENABLED = "MapsButtonEnabled";
    private static final String BROWSER_BUTTON_ENABLED = "BrowserButtonEnabled";
    private static final String PHONE_BUTTON_ENABLED = "PhoneButtonEnabled";

    private static final int DETAIL_LOADER = 0;

    // Get the columns we need, from database.
    // Also used for savedInstanceState bundle, with corresponding strings.
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

        dentistTitle = getResources().getString(R.string.detailDentistNameTXTview);
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
                // Create URI for finding location via map.
                Uri mapsLocation = Uri.parse("geo:" + latitude + "," + longitude + "?q="
                        + streetAddress + " " + zipCode + " " + postalCity);
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


        // Get arguments for loading data for detail view.
        // If null, means that there is no data, should disable the buttons.
        // If coming via intent (mobilephones), get it's URI to load the data for the view.
        Bundle arguments = getArguments();
        if (arguments != null)
        {
            mUri = arguments.getParcelable(DETAIL_URI);
            changeButtonState(true, true, true);
        }

        // Populate fields via savedInstanceState and check buttons enable state.
        if (savedInstanceState != null)
        {
            changeButtonState(savedInstanceState.getBoolean(MAPS_BUTTON_ENABLED),
                    savedInstanceState.getBoolean(BROWSER_BUTTON_ENABLED),
                    savedInstanceState.getBoolean(PHONE_BUTTON_ENABLED));
            // Check for null
            if (savedInstanceState.getString(DENTIST_COLUMNS[COL_phone]) != null &&
                    savedInstanceState.getString(DENTIST_COLUMNS[COL_urlLink]) != null &&
                    savedInstanceState.getString(DENTIST_COLUMNS[COL_address]) != null &&
                    savedInstanceState.getString(DENTIST_COLUMNS[COL_name]) != null &&
                    savedInstanceState.getString(DENTIST_COLUMNS[COL_zip]) != null &&
                    savedInstanceState.getString(DENTIST_COLUMNS[COL_city]) != null)
            {
                phoneNumber = savedInstanceState.getString(DENTIST_COLUMNS[COL_phone]);
                URLlink = savedInstanceState.getString(DENTIST_COLUMNS[COL_urlLink]);
                streetAddress = savedInstanceState.getString(DENTIST_COLUMNS[COL_address]);
                latitude = savedInstanceState.getDouble(DENTIST_COLUMNS[COL_latitude]);
                longitude = savedInstanceState.getDouble(DENTIST_COLUMNS[COL_longitude]);
                dentistTitleTextView.setText(savedInstanceState.getString(DENTIST_COLUMNS[COL_name]));
                zipCode = savedInstanceState.getString(DENTIST_COLUMNS[COL_zip]);
                postalCity = savedInstanceState.getString(DENTIST_COLUMNS[COL_city]);
                ZIP_CityTextView.setText(zipCode + " " + postalCity);
                phoneTextView.setText(phoneNumber);
                linkURLTextView.setText(URLlink);
                streetAddressTextView.setText(streetAddress);

            }

        }
        else
        {
            changeButtonState(false, false, false);
        }

        return rootView;
    }

    /*
        Changes the buttons to be enabled.
        Disabled will look grayed out.
        Pass in TRUE for enabled, pass in FALSE for disabled.
     */
    private void changeButtonState(boolean bool_Maps, boolean bool_Phone, boolean bool_Browser)
    {

        if (Maps != null){
            Maps.setEnabled(bool_Maps);
            Drawable orig_maps = getResources().getDrawable(R.drawable.big_launch_maps);
            if (bool_Maps)
                Maps.setImageDrawable(orig_maps);
            else
               Maps.setImageDrawable(convertIconToGrayScale(orig_maps));
        }

        if (Browser != null){
            Browser.setEnabled(bool_Browser);
            Drawable orig_browser = getResources().getDrawable(R.drawable.big_launch_browser);
            if (bool_Maps)
                Browser.setImageDrawable(orig_browser);
            else
                Browser.setImageDrawable(convertIconToGrayScale(orig_browser));
        }

        if (Phone != null){
            Phone.setEnabled(bool_Phone);
            Drawable orig_phone = getResources().getDrawable(R.drawable.big_launch_phone);
            if (bool_Maps)
                Phone.setImageDrawable(orig_phone);
            else
                Phone.setImageDrawable(convertIconToGrayScale(orig_phone));
        }


    }

    private static Drawable convertIconToGrayScale(Drawable icon)
    {
        if (icon != null)
        {
            Drawable temp = icon.mutate();
            temp.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
            return temp;
        }
        else
            return null;
    }

    // Save data for recreation.
    @Override
    public void onSaveInstanceState(Bundle outstate)
    {
        super.onSaveInstanceState(outstate);
        outstate.putBoolean(MAPS_BUTTON_ENABLED, Maps.isEnabled());
        outstate.putBoolean(BROWSER_BUTTON_ENABLED, Browser.isEnabled());
        outstate.putBoolean(PHONE_BUTTON_ENABLED, Phone.isEnabled());

        outstate.putString(DENTIST_COLUMNS[COL_phone], phoneNumber);
        outstate.putString(DENTIST_COLUMNS[COL_urlLink], URLlink);
        outstate.putString(DENTIST_COLUMNS[COL_address], streetAddress);
        outstate.putString(DENTIST_COLUMNS[COL_city], postalCity);
        outstate.putString(DENTIST_COLUMNS[COL_zip], zipCode);
        outstate.putString(DENTIST_COLUMNS[COL_name], dentistTitle);
        outstate.putDouble(DENTIST_COLUMNS[COL_latitude], latitude);
        outstate.putDouble(DENTIST_COLUMNS[COL_longitude], longitude);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "In onCreateLoader");

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        if (mUri != null){
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    DENTIST_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        else
            return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "In onLoadFinished");

        // Populate fields and variables with the data in the cursor.
        if (!data.moveToFirst()){return;}
            // Bind data to variables.
            phoneNumber = data.getString(COL_phone);
            URLlink = data.getString(COL_urlLink);
            zipCode = data.getString(COL_zip);
            postalCity = data.getString(COL_city);
            streetAddress = data.getString(COL_address);
            dentistTitle = data.getString(COL_name);
            latitude = data.getDouble(COL_latitude);
            longitude = data.getDouble(COL_longitude);

            // Set data to TextViews
            dentistTitleTextView.setText(dentistTitle);
            phoneTextView.setText(phoneNumber);
            linkURLTextView.setText(URLlink);
            streetAddressTextView.setText(streetAddress);
            ZIP_CityTextView.setText(zipCode + " " + postalCity);
            changeButtonState(true, true, true);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

}
