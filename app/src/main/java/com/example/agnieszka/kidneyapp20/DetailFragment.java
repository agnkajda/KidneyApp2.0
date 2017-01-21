package com.example.agnieszka.kidneyapp20;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.agnieszka.kidneyapp20.data.KidneyContract.JournalEntry;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    static final String DETAIL_URI = "URI";

    private String mJournal;
    private Uri mUri;

    private static final int DETAIL_LOADER = 0;

    private static final String[] DETAIL_COLUMNS = {
            JournalEntry.TABLE_NAME + "." + JournalEntry._ID,
            JournalEntry.COLUMN_DATE,
            JournalEntry.COLUMN_FOOD_NAME,
            JournalEntry.COLUMN_AMOUNT,
            JournalEntry.COLUMN_KCAL,
            JournalEntry.COLUMN_CARBON,
            JournalEntry.COLUMN_FAT,
            JournalEntry.COLUMN_PROTEIN,
            JournalEntry.COLUMN_PHOSPHORUS,
            JournalEntry.COLUMN_SODIUM,
            JournalEntry.COLUMN_POTASSIUM,
            JournalEntry.COLUMN_FLUID,
    };

    public static final int COL_JOURNAL_ID = 0;
    public static final int COL_JOURNAL_DATE = 1;
    public static final int COL_JOURNAL_FOOD_NAME = 2;
    public static final int COL_JOURNAL_AMOUNT = 3;
    public static final int COL_JOURNAL_KCAL = 4;
    public static final int COL_JOURNAL_CARBON = 5;
    public static final int COL_JOURNAL_FAT = 6;
    public static final int COL_JOURNAL_PROTEIN = 7;
    public static final int COL_JOURNAL_PHOSPHORUS = 8;
    public static final int COL_JOURNAL_SODIUM = 9;
    public static final int COL_JOURNAL_POTASSIUM = 10;
    public static final int COL_JOURNAL_FLUID = 11;

    private ImageView mIconView;
    private TextView mFriendlyDateView;
    private TextView mDateView;
    private TextView mDescriptionView;
    private TextView mHighTempView;
    private TextView mLowTempView;
    private TextView mHumidityView;
    private TextView mWindView;
    private TextView mPressureView;
    Button deleteFood;
    Context context;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
        }

        //TO DO: tu chyba trzeba coś pozmieniać

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mIconView = (ImageView) rootView.findViewById(R.id.detail_icon);
        mDateView = (TextView) rootView.findViewById(R.id.detail_date_textview);
        mFriendlyDateView = (TextView) rootView.findViewById(R.id.detail_day_textview);
        mDescriptionView = (TextView) rootView.findViewById(R.id.detail_forecast_textview);
        mHighTempView = (TextView) rootView.findViewById(R.id.detail_high_textview);
        mLowTempView = (TextView) rootView.findViewById(R.id.detail_low_textview);
        mHumidityView = (TextView) rootView.findViewById(R.id.detail_humidity_textview);
        mWindView = (TextView) rootView.findViewById(R.id.detail_wind_textview);
        mPressureView = (TextView) rootView.findViewById(R.id.detail_pressure_textview);
        deleteFood = (Button) rootView.findViewById(R.id.delete_button);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detailfragment, menu);

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( null != mUri ) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            // Read weather condition ID from cursor
            int weatherId = data.getInt(COL_JOURNAL_ID);

            // Use weather art image
            mIconView.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));

            // Read date from cursor and update views for day of week and date
            long date = data.getLong(COL_JOURNAL_DATE);
            String friendlyDateText = Utility.getDayName(getActivity(), date);
            String dateText = Utility.getFormattedMonthDay(getActivity(), date);
            mFriendlyDateView.setText(friendlyDateText);
            mDateView.setText(dateText);

            // Read description from cursor and update view
            String description = data.getString(COL_JOURNAL_FOOD_NAME);
            mDescriptionView.setText(description);

            // For accessibility, add a content description to the icon field
            mIconView.setContentDescription(description);

            double amount = data.getDouble(COL_JOURNAL_AMOUNT);
            String amountString = Double.toString(amount);
            mHighTempView.setText(amountString);

            double kcal = data.getDouble(COL_JOURNAL_KCAL);
            String kcalString = Double.toString(kcal);
            mHighTempView.setText(kcalString);

            // Read low temperature from cursor and update view
            double carbon = data.getDouble(COL_JOURNAL_CARBON);
            String carbonString = Double.toString(carbon);
            mLowTempView.setText(carbonString);

            // Read humidity from cursor and update view
            double fat = data.getDouble(COL_JOURNAL_FAT);
            String fatString = Double.toString(fat);
            mHumidityView.setText(fatString);

            // Read wind speed and direction from cursor and update view
            double protein = data.getDouble(COL_JOURNAL_PROTEIN);
            String proteinString = Double.toString(protein);
            mWindView.setText(proteinString);

            // Read pressure from cursor and update view
            double phosphorus = data.getDouble(COL_JOURNAL_PHOSPHORUS);
            String phosphorusString = Double.toString(phosphorus);
            mPressureView.setText(phosphorusString);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }
}
