package com.example.alienegg.tamperedentist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;


/**
 * Created by AlienNest on 12.4.2016.
 */
public class DetailActivity extends ActionBarActivity {

    private final String LOG_TAG = DetailActivity.class.getSimpleName();
    private boolean mTwoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);

        Intent intent = getIntent();

        if (intent != null)
        {
            Bundle temp = intent.getExtras();
            if (temp != null)
                mTwoPane = temp.getBoolean(MainActivity.TABLET_PANE);
        }


        if (savedInstanceState == null) {

            // Create a bundle to create arguments.
            Bundle arguments = new Bundle();
            arguments.putParcelable(DentistDetailFragment.DETAIL_URI, getIntent().getData());
            arguments.putBoolean(MainActivity.TABLET_PANE, mTwoPane);

            // Set arguments into fragment
            DentistDetailFragment fragment = new DentistDetailFragment();
            fragment.setArguments(arguments);

            // Commit the created fragment with arguments.
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_dentist_container,fragment)
                    .commit();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
