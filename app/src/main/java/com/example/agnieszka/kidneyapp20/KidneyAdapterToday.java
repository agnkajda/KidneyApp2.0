package com.example.agnieszka.kidneyapp20;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.agnieszka.kidneyapp20.data.KidneyContract;

public class KidneyAdapterToday extends CursorAdapter {

    public KidneyAdapterToday(Context context, Cursor c, int flags) {
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
                "\nProtein: " + cursor.getDouble(idx_protein);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.listview_today, parent, false);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView descriptionView = (TextView) view.findViewById(R.id.today);
        descriptionView.setText(convertCursorRowToUXFormat(cursor));

        int idx_phosphorus = cursor.getColumnIndex(KidneyContract.ValuesEntry.COLUMN_PHOSPHORUS);
        int idx_sodium = cursor.getColumnIndex(KidneyContract.ValuesEntry.COLUMN_SODIUM);
        int idx_potassium = cursor.getColumnIndex(KidneyContract.ValuesEntry.COLUMN_POTASSIUM);
        int idx_fluid = cursor.getColumnIndex(KidneyContract.ValuesEntry.COLUMN_FLUID);


        TextView secondView = (TextView) view.findViewById(R.id.today2);
        secondView.setText("Phosphorus: " + cursor.getDouble(idx_phosphorus) +
                    "\nSodium: " + cursor.getDouble(idx_sodium) +
                "\nPotassium: " + cursor.getDouble(idx_potassium) +
                "\nFluid: " + cursor.getDouble(idx_fluid));
    }
}
