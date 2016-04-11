package com.example.alienegg.tamperedentist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by AlienNest on 11.4.2016.
 */
public class DentistObjAdapter extends ArrayAdapter<DentistObj> {

    // Constructor
    public DentistObjAdapter(Context context, ArrayList<DentistObj> dentistObjs){
        super(context, 0, dentistObjs);
    }

    // Translates the data between DentistObjs and ListView.
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        DentistObj dentistObj = getItem(position);

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_dentist, parent, false);
        }

        TextView dentistName = (TextView) convertView.findViewById(R.id.dentistTextView);
        // Add possible address here or other data to be seen on the ListView.

        dentistName.setText(dentistObj.dentistName());

        return convertView;
    }
}
