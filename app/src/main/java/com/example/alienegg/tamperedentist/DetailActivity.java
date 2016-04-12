package com.example.alienegg.tamperedentist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by AlienNest on 12.4.2016.
 */
public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DentistDetailFragment())
                    .commit();
        }
    }

}
