package com.example.alienegg.tamperedentist;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by AlienNest on 12.4.2016.
 */
public class DentistAdapter extends CursorAdapter {
    private static String LOG_TAG = DentistAdapter.class.getSimpleName();

    public DentistAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_dentist, parent, false);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Bind data from cursor into the ListView.
        String cursorDentistName = cursor.getString(DentistFragment.COL_name);
        TextView dentistName = (TextView)view.findViewById(R.id.dentistTextView);
        dentistName.setText(cursorDentistName);
    }
}
