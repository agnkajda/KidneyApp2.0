package com.example.agnieszka.kidneyapp20;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.agnieszka.kidneyapp20.data.KidneyContract;


public class KidneyAdapter extends CursorAdapter {

    public KidneyAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    private String convertCursorRowToUXFormat(Cursor cursor) {
        // get row indices for our cursor
        int idx_date = cursor.getColumnIndex(KidneyContract.ValuesEntry.COLUMN_DATE);
        int idx_kcal = cursor.getColumnIndex(KidneyContract.ValuesEntry.COLUMN_KCAL);
        int idx_carbon = cursor.getColumnIndex(KidneyContract.ValuesEntry.COLUMN_CARBON);
        int idx_fat = cursor.getColumnIndex(KidneyContract.ValuesEntry.COLUMN_FAT);
        int idx_protein = cursor.getColumnIndex(KidneyContract.ValuesEntry.COLUMN_PROTEIN);
        int idx_phosphorus = cursor.getColumnIndex(KidneyContract.ValuesEntry.COLUMN_PHOSPHORUS);
        int idx_sodium = cursor.getColumnIndex(KidneyContract.ValuesEntry.COLUMN_SODIUM);
        int idx_potassium = cursor.getColumnIndex(KidneyContract.ValuesEntry.COLUMN_POTASSIUM);
        int idx_fluid = cursor.getColumnIndex(KidneyContract.ValuesEntry.COLUMN_FLUID);


        return "Kcal: " + cursor.getString(idx_kcal) +
                "\nCarbon: " + cursor.getDouble(idx_carbon) +
                "\nFat: " + cursor.getDouble(idx_fat) +
                "\nProtein: " + cursor.getDouble(idx_protein) +
                "\nPhosphorus: " + cursor.getDouble(idx_phosphorus) +
                "\nSodium: " + cursor.getDouble(idx_sodium) +
                "\nPotassium: " + cursor.getDouble(idx_potassium) +
                "\nFluid: " + cursor.getDouble(idx_fluid);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_kidney_adapter, parent, false);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.

        //TextView tv = (TextView)view;
        //tv.setText(convertCursorRowToUXFormat(cursor));

        TextView descriptionView = (TextView) view.findViewById(R.id.list_item_kidney_adapter_textview);
        descriptionView.setText(convertCursorRowToUXFormat(cursor));

        long dateInMillis = cursor.getLong(MainActivityFragment.COL_JOURNAL_DATE);
        TextView dateView = (TextView) view.findViewById(R.id.list_item_kidney_adapter_textview2);
        dateView.setText("Date:\n"  + Utility.formatDate(dateInMillis));

        ImageView iconView = (ImageView) view.findViewById(R.id.list_item_icon);
        iconView.setImageResource(R.drawable.calendar30);
    }
}