package com.example.agnieszka.kidneyapp20;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.agnieszka.kidneyapp20.data.KidneyContract;


public class KidneyAdapter extends CursorAdapter {
    public KidneyAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }


    /*
        This is ported from FetchWeatherTask --- but now we go straight from the cursor to the
        string.
     */
    private String convertCursorRowToUXFormat(Cursor cursor) {
        // get row indices for our cursor
        int idx_date = cursor.getColumnIndex(KidneyContract.ValuesEntry.COLUMN_DATE);
        int idx_kcal = cursor.getColumnIndex(KidneyContract.ValuesEntry.COLUMN_KCAL);


        return Utility.formatDate(cursor.getLong(idx_date)) +
                " - " + cursor.getString(idx_kcal);
    }

    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_kidney_adapter, parent, false);

        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.

        TextView tv = (TextView)view;
        tv.setText(convertCursorRowToUXFormat(cursor));
    }
}