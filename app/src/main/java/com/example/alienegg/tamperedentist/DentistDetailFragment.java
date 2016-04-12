package com.example.alienegg.tamperedentist;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

public class DentistDetailFragment extends android.support.v4.app.Fragment {

    private final String LOG_TAG = DentistDetailFragment.class.getSimpleName();

    // Text fields to populate with data.
    private TextView dentistTitleTextView;
    private TextView phoneTextView;
    private TextView linkURLTextView;
    private TextView streetAddressTextView;
    private TextView ZIP_CityTextView;

    // Data used in opening intents.
    private String phoneNumber; // tel: prefix required.
    private String streetAddress;
    private String URLlink;
    private String zipCode;
    private String postalCity;

    // For loading appropriate icons into them.
    private ImageButton Maps;
    private ImageButton Phone;
    private ImageButton Browser;

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

        dentistTitleTextView =(TextView)rootView.findViewById(R.id.detailDentistNameTXTview);
        phoneTextView =(TextView)rootView.findViewById(R.id.phoneTXTview);
        linkURLTextView =(TextView)rootView.findViewById(R.id.linkTXTview);
        streetAddressTextView=(TextView)rootView.findViewById(R.id.streetAddressTXTview);
        ZIP_CityTextView=(TextView)rootView.findViewById(R.id.ZIPcityTXTview);

        Maps = (ImageButton)rootView.findViewById(R.id.GoogleMapsImageView);
        Phone = (ImageButton)rootView.findViewById(R.id.phoneImageView);
        Browser = (ImageButton)rootView.findViewById(R.id.browserImageView);

        Maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOG_TAG, "in onClick Maps Button");
                // TODO start default Maps application.
                Uri mapsLocation = Uri.parse("geo:0,0?q=" + streetAddress);
                Intent mapsI = new Intent(Intent.ACTION_VIEW, mapsLocation);
                try{
                    startActivity(mapsI);
                } catch(Exception e){
                    Log.v(LOG_TAG, "in onClick Browser Button; No Browser?");
                    e.printStackTrace();
                }
            }
        });

        Phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOG_TAG, "in onClick Phone Button");
                // TODO start default call application.
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

        Browser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOG_TAG, "in onClick Browser Button");
                // TODO start default browser application.
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

        // Populate fields via intent.
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("NIMI")
                && intent.hasExtra("OSOITE")
                && intent.hasExtra("POSTINUMERO")
                && intent.hasExtra("POSTITOIMIPAIKKA")
                && intent.hasExtra("PUHELIN")
                && intent.hasExtra("URL"))
        {
            phoneNumber = intent.getStringExtra("PUHELIN");
            URLlink = intent.getStringExtra("URL");
            zipCode = intent.getStringExtra("POSTINUMERO");
            postalCity = intent.getStringExtra("POSTITOIMIPAIKKA");
            streetAddress = intent.getStringExtra("OSOITE")
                    + " " + zipCode
                    + " " + postalCity;

            dentistTitleTextView.setText(intent.getStringExtra("NIMI"));
            phoneTextView.setText(phoneNumber);
            linkURLTextView.setText(URLlink);
            streetAddressTextView.setText(intent.getStringExtra("OSOITE"));
            ZIP_CityTextView.setText(intent.getStringExtra("POSTINUMERO")
                    + " " + intent.getStringExtra("POSTITOIMIPAIKKA"));
        }

        // Populate fields via savedInstanceState.
        if (savedInstanceState != null)
        {
            phoneNumber = savedInstanceState.getString("PUHELIN");
            URLlink = savedInstanceState.getString("URL");
            streetAddress = savedInstanceState.getString("OSOITE")
                    + " " + savedInstanceState.getString("POSTINUMERO")
                    + " " + savedInstanceState.getString("POSTITOIMIPAIKKA");
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
    }

    // TODO get appropriate default application icons for the intents, if possible.
}
