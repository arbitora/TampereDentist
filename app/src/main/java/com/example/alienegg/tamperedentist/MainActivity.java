package com.example.alienegg.tamperedentist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    // URL = http://opendata.navici.com/tampere/opendata/ows?service=WFS&version=2.0.0&request=GetFeature&typeName=opendata:HAMMASHOITOLAT&outputFormat=json

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null)
        {
            // TODO Get data back or vice versa.
        }

        // TODO start fragment here.

    }


    @Override
    protected void onResume() {
        super.onResume();
        // TODO On Resume
    }

    @Override
    public void onDestroy ()
    {
        super.onDestroy();
        // TODO On Destroy.
    }

}
