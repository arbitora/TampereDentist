package com.example.alienegg.tamperedentist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class DentistDetailFragment extends android.support.v4.app.Fragment {

    private final String LOG_TAG = DentistDetailFragment.class.getSimpleName();

    // Text fields to populate with data.
    private TextView dentistTitleTextView;
    private TextView phoneTextView;
    private TextView linkURLTextView;
    private TextView streetAddressTextView;
    private TextView ZIP_CityTextView;

    // For loading appropriate icons into them.
    private ImageView Maps;
    private ImageView Phone;
    private ImageView Browser;

    public DentistDetailFragment(){}


    // TODO save data for recreation.

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

        dentistTitleTextView =(TextView)rootView.findViewById(R.id.dentistTextView);
        phoneTextView =(TextView)rootView.findViewById(R.id.phoneTXTview);
        linkURLTextView =(TextView)rootView.findViewById(R.id.linkTXTview);
        streetAddressTextView=(TextView)rootView.findViewById(R.id.streetAddressTXTview);
        ZIP_CityTextView=(TextView)rootView.findViewById(R.id.ZIPcityTXTview);

        Maps = (ImageView)rootView.findViewById(R.id.GoogleMapsImageView);
        Phone = (ImageView)rootView.findViewById(R.id.phoneImageView);
        Browser = (ImageView)rootView.findViewById(R.id.browserImageView);

        // Populate fields via intent.
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("NIMI")
                && intent.hasExtra("OSOITE")
                && intent.hasExtra("POSTINUMERO")
                && intent.hasExtra("POSTITOIMIPAIKKA")
                && intent.hasExtra("PUHELIN")
                && intent.hasExtra("URL"))
        {
            dentistTitleTextView.setText(intent.getStringExtra("NIMI"));
            phoneTextView.setText(intent.getStringExtra("PUHELIN"));
            linkURLTextView.setText(intent.getStringExtra("URL"));
            streetAddressTextView.setText(intent.getStringExtra("OSOITE"));
            ZIP_CityTextView.setText(intent.getStringExtra("POSTINUMERO")
                    + " " + intent.getStringExtra("POSTITOIMIPAIKKA"));
        }

        // Populate fields via savedInstanceState.
        if (savedInstanceState != null)
        {
            dentistTitleTextView.setText(savedInstanceState.getString("NIMI"));
            phoneTextView.setText(savedInstanceState.getString("PUHELIN"));
            linkURLTextView.setText(savedInstanceState.getString("URL"));
            streetAddressTextView.setText(savedInstanceState.getString("OSOITE"));
            ZIP_CityTextView.setText(savedInstanceState.getString("POSTINUMERO")
                    + " " + savedInstanceState.getString("POSTITOIMIPAIKKA"));
        }

        return rootView;
    }

    // TODO call appropriate intents for different fields (Maps, Phone, Browser).

    // TODO get appropriate default application icons for the intents, if possible.
}
