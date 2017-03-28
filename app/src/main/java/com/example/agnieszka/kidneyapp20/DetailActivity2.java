package com.example.agnieszka.kidneyapp20;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.database.Cursor;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.example.agnieszka.kidneyapp20.data.KidneyContract;
import com.example.agnieszka.kidneyapp20.data.KidneyContract.JournalEntry;
import com.example.agnieszka.kidneyapp20.data.KidneyDbHelper;

import static com.example.agnieszka.kidneyapp20.ChooseTheMeal.context;

public class DetailActivity2 extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail2);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment2())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DetailFragment2 extends Fragment implements LoaderCallbacks<Cursor> {

        private static final String LOG_TAG = DetailFragment2.class.getSimpleName();

        private String mForecast;
        private String mForecast2;
        private String mAmount;
        private String mDate;
        private String mUriStr;
        TextView uriTextView;
        TextView uriDate;
        TextView detailAmount;
        TextView detailDate;
        TextView detailTextView;
        TextView detailTextView2;
        double amount;
        double kcal;
        double carbon;
        double fat;
        double protein;
        double phosphorus;
        double sodium;
        double potassium;
        double fluid;

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
                JournalEntry.COLUMN_FLUID
        };

        // these constants correspond to the projection defined above, and must change if the
        // projection changes
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
        public static final int COL_JOURNAL_FLUID =  11;
        Button deleteFood;


        public DetailFragment2() {
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Intent intent = getActivity().getIntent();
            View rootView = inflater.inflate(R.layout.fragment_detail2, container, false);
            if (intent != null) {
                mUriStr = intent.getDataString();
                            }

            final Uri uri = Uri.parse(mUriStr);
            deleteFood = (Button) rootView.findViewById(R.id.delete_button);
            View.OnClickListener clickingToDelete = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context = getActivity().getApplicationContext();
                    KidneyDbHelper dbHelper = new KidneyDbHelper(context);
                    long date = Long.parseLong(uri.getPathSegments().get(1));
                    long id = Long.parseLong(uri.getPathSegments().get(2));
                    dbHelper.deletingValueFromJournal(id);
                    dbHelper.deletingValueFromValues(kcal, KidneyContract.ValuesEntry.COLUMN_KCAL, date);
                    dbHelper.deletingValueFromValues(carbon, KidneyContract.ValuesEntry.COLUMN_CARBON, date);
                    dbHelper.deletingValueFromValues(fat, KidneyContract.ValuesEntry.COLUMN_FAT, date);
                    dbHelper.deletingValueFromValues(protein, KidneyContract.ValuesEntry.COLUMN_PROTEIN, date);
                    dbHelper.deletingValueFromValues(phosphorus, KidneyContract.ValuesEntry.COLUMN_PHOSPHORUS, date);
                    dbHelper.deletingValueFromValues(sodium, KidneyContract.ValuesEntry.COLUMN_SODIUM, date);
                    dbHelper.deletingValueFromValues(potassium, KidneyContract.ValuesEntry.COLUMN_POTASSIUM, date);
                    dbHelper.deletingValueFromValues(fluid, KidneyContract.ValuesEntry.COLUMN_FLUID, date);

                    Utility.setNegativeFluidIntake(context, fluid);

                    Intent newIntent = new Intent (context, MainActivity.class);
                    startActivity(newIntent);
                }

            };
            deleteFood.setOnClickListener(clickingToDelete);


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
            Log.v(LOG_TAG, "In onCreateLoader");
            Intent intent = getActivity().getIntent();
            if (intent == null) {
                return null;
            }
            String showMeData = intent.getDataString();
            Log.v(LOG_TAG, "intent not equal 0");
            Log.v(LOG_TAG, "intent looks like this: " + showMeData);
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    intent.getData(),
                    DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            Log.v(LOG_TAG, "In onLoadFinished");
            detailDate = (TextView)getView().findViewById(R.id.detail_date);
            detailTextView = (TextView)getView().findViewById(R.id.detail_text);
            detailTextView2 = (TextView)getView().findViewById(R.id.detail_text2);
            detailAmount = (TextView)getView().findViewById(R.id.detail_amount);
            ImageView iconView = (ImageView)getView().findViewById(R.id.list_item_icon);
            iconView.setImageResource(R.drawable.sztucce50);
            ImageView iconDateView = (ImageView)getView().findViewById(R.id.list_item_date_icon);
            iconDateView.setImageResource(R.drawable.calendar50);

            if (!data.moveToFirst()) { return; }

            String dateString = Utility.formatDate(
                    data.getLong(COL_JOURNAL_DATE));

            String foodName =
                    data.getString(COL_JOURNAL_FOOD_NAME);

            amount = data.getDouble(COL_JOURNAL_AMOUNT);
            kcal = data.getDouble(COL_JOURNAL_KCAL);
            carbon = data.getDouble(COL_JOURNAL_CARBON);
            fat = data.getDouble(COL_JOURNAL_FAT);
            protein = data.getDouble(COL_JOURNAL_PROTEIN);
            phosphorus = data.getDouble(COL_JOURNAL_PHOSPHORUS);
            sodium = data.getDouble(COL_JOURNAL_SODIUM);
            potassium = data.getDouble(COL_JOURNAL_POTASSIUM);
            fluid = data.getDouble(COL_JOURNAL_FLUID);

            mForecast = String.format("Kcal: %s kcal\n Carbon: %s g\n Fat: %s g\n Protein: %s g\n" +
                    " Phosphorus: %s mg\n " + "Sodium: %s mg\n Potassium: %s mg\n Fluid: %s mg",
                    kcal, carbon, fat, protein, phosphorus, sodium, potassium, fluid);

            mForecast2 = String.format("%s",
                    foodName);

            mAmount = String.format("Amount: %s g", amount);

            mDate = String.format("Date: %s", dateString);

            detailDate.setText(mDate);
            detailTextView.setText(mForecast);
            detailTextView2.setText(mForecast2);
            detailAmount.setText(mAmount);


        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) { }
    }
}