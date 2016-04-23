package com.example.alienegg.tamperedentist;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.alienegg.tamperedentist.data.DentistContract;

/**
 * Created by AlienNest on 11.4.2016.
 */
public class DentistFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private FrameLayout listFragment;
    private Button retry;
    private boolean retryButtonCreated = false;

    private static final int DENTIST_LOADER = 0;

    // Get the columns we need.
    private static final String[] DENTIST_COLUMNS = {
            DentistContract.DentistEntry.TABLE_NAME + "." + DentistContract.DentistEntry._ID,
            DentistContract.DentistEntry.COLUMN_D_id,
            DentistContract.DentistEntry.COLUMN_name,
            DentistContract.DentistEntry.COLUMN_address,
            DentistContract.DentistEntry.COLUMN_zip,
            DentistContract.DentistEntry.COLUMN_city,
            DentistContract.DentistEntry.COLUMN_phone,
            DentistContract.DentistEntry.COLUMN_urlLink
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
    */

    /*
    QUERY_DENTIST = 0
    QUERY_COUNT = 1
    NO_QUERY = 2
    QUERY_DENTIST_NOT_FOUND = 3
     */
    public enum DATABASE_QUERY {
        QUERY_DENTIST, QUERY_COUNT, NO_QUERY, QUERY_DENTIST_NOT_FOUND, ERROR
    }

    ;
    DATABASE_QUERY queryStatus = DATABASE_QUERY.NO_QUERY;
    private DentistAdapter mDentistAdapter;
    private final String LOG_TAG = DentistFragment.class.getSimpleName();

    public DentistFragment(){}

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

        // The custom Adapter will take data from a source and
        // use it to populate the ListView it's attached to.
        mDentistAdapter = new DentistAdapter(getActivity(), null, 0);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        listFragment = (FrameLayout) rootView.findViewById(R.id.listFragment);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listDentistView);
        listView.setAdapter(mDentistAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                // Get DentistObj which is at the corresponding position and start Detail intent for it.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    Intent intent = new Intent(getActivity(), DetailActivity.class);

                    // Query necessary information and start intent.
                    intent.setData(DentistContract.DentistEntry.buildDentistIdUri(cursor.getString(COL_D_id)));

                    startActivity(intent);
                }


            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "in onActivityCreated");
        super.onActivityCreated(savedInstanceState);


        if (savedInstanceState != null) {

            // If retry button was pressed, restart loaders.
            if (savedInstanceState.getBoolean("restart"))
                getLoaderManager().restartLoader(DENTIST_LOADER, null, this);

            // Check if the value is proper value before adding it into queryStatus variable.
            DATABASE_QUERY savedStatus = IntToStatus(savedInstanceState.getInt("queryStatus"));
            if (savedStatus != DATABASE_QUERY.ERROR)
                queryStatus = savedStatus;

            // If retry button was created, recreate it (due to orientation change for exmaple).
            if (savedInstanceState.getBoolean("retryButtonCreated"))
                createRetryButton();
        }

        // If loader is not running, start loader.
        if (!getLoaderManager().hasRunningLoaders())
            getLoaderManager().initLoader(DENTIST_LOADER, null, this);

    }


    private void updateDentists(){
        Log.v(LOG_TAG, "in updateDentists");
        // Check for network before trying to connect to the servers.
        if (isNetworkAvailable(this.getContext())) {
            FetchDentistTask dentistTask = new FetchDentistTask(getActivity());
            dentistTask.execute();
        }
    }

    /*
    Converts DATABASE_QUERY status into integer.
        QUERY_DENTIST = 0
        QUERY_COUNT = 1
        NO_QUERY = 2
        QUERY_DENTIST_NOT_FOUND = 3

        ERROR = -1
     */
    private int StatusToInt(DATABASE_QUERY status) {
        int statusCode;
        switch (status) {
            case QUERY_DENTIST:
                statusCode = 0;
                break;

            case QUERY_COUNT:
                statusCode = 1;
                break;

            case NO_QUERY:
                statusCode = 2;
                break;

            case QUERY_DENTIST_NOT_FOUND:
                statusCode = 3;
                break;

            default:
                statusCode = -1;
                break;
        }

        return statusCode;
    }

    /*
    Converts integer into DATABASE_QUERY status.
        QUERY_DENTIST = 0
        QUERY_COUNT = 1
        NO_QUERY = 2
        QUERY_DENTIST_NOT_FOUND = 3

    -1 will return ERROR
    */
    private DATABASE_QUERY IntToStatus(int statusCode) {
        DATABASE_QUERY status;
        switch (statusCode) {
            case 0:
                status = DATABASE_QUERY.QUERY_DENTIST;
                break;

            case 1:
                status = DATABASE_QUERY.QUERY_COUNT;
                break;

            case 2:
                status = DATABASE_QUERY.NO_QUERY;
                break;

            case 3:
                status = DATABASE_QUERY.QUERY_DENTIST_NOT_FOUND;
                break;

            default:
                status = DATABASE_QUERY.ERROR;
                break;
        }

        return status;
    }

    private void createRetryButton() {
        // If it is already created, do not recreate it.
        if (!retryButtonCreated) {
            retry = new Button(this.getContext());
            retry.setText(R.string.BTNretry);
            retry.setLayoutParams(new ActionBar.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            listFragment.addView(retry);
            retryButtonCreated = true;

            retry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v(LOG_TAG, "in retry Button OnClick");

                    if (isNetworkAvailable(getContext())) {
                        updateDentists();
                        Bundle restartManager = new Bundle();
                        restartManager.putBoolean("restart", true);
                        onActivityCreated(restartManager);
                        listFragment.removeView(retry);
                        retryButtonCreated = false;
                    } else
                        Toast.makeText(getContext(), R.string.networkError, Toast.LENGTH_SHORT).show();

                }
            });
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "in onCreateLoader");
        if (queryStatus == DATABASE_QUERY.NO_QUERY) {
            queryStatus = DATABASE_QUERY.QUERY_COUNT;
            Uri countUri = DentistContract.DentistEntry.buildDentistCountUri();
            return new CursorLoader(getActivity(),
                    countUri,
                    DENTIST_COLUMNS,
                    null,
                    null,
                    null);

        } else if (queryStatus == DATABASE_QUERY.QUERY_COUNT || queryStatus == DATABASE_QUERY.QUERY_DENTIST_NOT_FOUND) {
            queryStatus = DATABASE_QUERY.QUERY_DENTIST;
            String sortOrder = DentistContract.DentistEntry.COLUMN_name + " ASC";
            Uri dentistsUri = DentistContract.DentistEntry.buildDentistBasicUri();

            return new CursorLoader(getActivity(),
                    dentistsUri,
                    DENTIST_COLUMNS,
                    null,
                    null,
                    sortOrder);
        } else
            return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "in onLoadFinished");

        // If query was to get dentists to list, associate with CursorAdapter.
        if (queryStatus == DATABASE_QUERY.QUERY_DENTIST)
        {
            //DatabaseUtils.dumpCursor(data);
            mDentistAdapter.swapCursor(data);
            // If no dentists were found, message the user.
            if (data.getCount() <= 0) {
                Toast.makeText(this.getActivity(), R.string.emptyTableError, Toast.LENGTH_LONG).show();
                // Update query status with a new message.
                queryStatus = DATABASE_QUERY.QUERY_DENTIST_NOT_FOUND;

                createRetryButton();

            }

        }

        // If query was to get count, check if there was data.
        // If no data, start updateDentists.
        if (queryStatus == DATABASE_QUERY.QUERY_COUNT) {
            // Get new data from internet.
            //DatabaseUtils.dumpCursor(data);
            data.moveToFirst();
            if (data.getInt(0) <= 0)
                updateDentists();

            // Reload cursor with dentist data.
            getLoaderManager().restartLoader(DENTIST_LOADER, null, this);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v(LOG_TAG, "in onLoaderReset");

        mDentistAdapter.swapCursor(null);
    }


    // Save necessary data to access it again, for example during orientation change.
    @Override
    public void onSaveInstanceState(Bundle outstate) {
        super.onSaveInstanceState(outstate);
        outstate.putBoolean("retryButtonCreated", retryButtonCreated); // Recreate button if it was created.
        outstate.putInt("queryStatus", StatusToInt(queryStatus)); // saves DATABASE_QUERY enum as integer.

    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isConnected = false;
        if (connectivity != null) {
            // This is for higher API levels
            /*
            Network[] networks = connectivity.getAllNetworks();
            NetworkInfo infoNet;
            for (Network tempNetwork : networks)
            {
                infoNet = connectivity.getNetworkInfo(tempNetwork);
                if (infoNet.getState() == NetworkInfo.State.CONNECTED)
                {
                    isConnected = true;
                }
            }*/
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        isConnected = true;
                    }

        } else {
            isConnected = false;

        }
        return isConnected;
    }

}
