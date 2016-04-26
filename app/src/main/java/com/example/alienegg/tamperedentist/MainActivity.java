package com.example.alienegg.tamperedentist;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends ActionBarActivity implements DentistFragment.Callback{

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private String version;
    private String installDate;

    // Was the popup window created?
    private boolean infoCreated = false;
    private static PopupWindow popupWindow = null;

    // Is it tablet view?
    private boolean mTabletPane;

    // SavedInstanceState strings
    private static final String INFO_CREATED = "infoCreate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "in onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.detail_dentist_container) != null)
        {
            // Only create tablet view mode if sw600 activity_main.xml is used.
            mTabletPane = true;

            if (savedInstanceState == null)
            {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_dentist_container, new DentistDetailFragment())
                        .commit();
            }
        }
        else
        {
            // No two pane layout used.
            mTabletPane = false;
        }


        if (savedInstanceState != null)
        {
            infoCreated = savedInstanceState.getBoolean(INFO_CREATED);
            if (infoCreated == true)
                createPopUpView();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.info, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.v(LOG_TAG, "in onOptionsItemSelected");
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Check which menu xml ID does the int id match.
        if (id == R.id.action_info) {
            // Popup application version.
            createPopUpView();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Uri contentUri)
    {
        // If it is a tablet view, detail view will be shown in this activity.
        if (mTabletPane)
        {
            Bundle arguments = new Bundle();
            arguments.putParcelable(DentistDetailFragment.DETAIL_URI, contentUri);

            DentistDetailFragment fragment = new DentistDetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_dentist_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        }
        // Else, send intent data to start new activity.
        else
        {
            Intent intent = new Intent (this, DetailActivity.class)
                    .setData(contentUri);
            startActivity(intent);
        }
    }


    // Creates the Popup Window.
    private void createPopUpView()
    {
        Log.v(LOG_TAG, "in createPopUpView");

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
            long instTime = pInfo.firstInstallTime;
            installDate = new SimpleDateFormat("HH:mm dd MMM yyyy").format(new Date(instTime));

            // Create popup view for popup window.
            LayoutInflater layoutInflater = (LayoutInflater)getBaseContext()
                    .getSystemService(LAYOUT_INFLATER_SERVICE);
            View popupView = layoutInflater.inflate(R.layout.version_popup, null);
            popupWindow = new PopupWindow(
                    popupView,
                    ActionBar.LayoutParams.WRAP_CONTENT,
                    ActionBar.LayoutParams.WRAP_CONTENT);

            // Populate the Popup View and modify component values.
            Button BTNOK = (Button)popupView.findViewById(R.id.popup_ok);
            TextView popup_InstallDate = (TextView)popupView.findViewById(R.id.popup_installDateTXT);
            TextView popup_Version = (TextView)popupView.findViewById(R.id.popup_versionTXT);
            popup_InstallDate.append("\n" + installDate);
            popup_Version.append(" " + version);
            BTNOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                    infoCreated = false;
                }
            });

            // Current view, Gravity, x Position, y Position.
            popupWindow.showAtLocation(getCurrentFocus(), 1, 0, 0);

            infoCreated = true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(INFO_CREATED, infoCreated);
    }

    @Override
    protected void onStart() {
        Log.v(LOG_TAG, "in onStart");
        super.onStart();
        // The activity is about to become visible.
    }

    @Override
    protected void onResume() {
        Log.v(LOG_TAG, "in onResume");
        super.onResume();
        // The activity has become visible (it is now "resumed").

    }

    @Override
    protected void onPause() {
        Log.v(LOG_TAG, "in onPause");
        // Destroy popup to avoid memory leak. Destroy = dismiss.
        if (popupWindow != null)
            popupWindow.dismiss();
        super.onPause();
        // Another activity is taking focus (this activity is about to be "paused").
    }

    @Override
    protected void onStop() {
        Log.v(LOG_TAG, "in onStop");
        super.onStop();
        // The activity is no longer visible (it is now "stopped")
    }

    @Override
    protected void onDestroy() {
        Log.v(LOG_TAG, "in onDestroy");
        super.onDestroy();
        // The activity is about to be destroyed.
    }

}
