package com.example.agnieszka.kidneyapp20;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.agnieszka.kidneyapp20.data.KidneyContract;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;
import static com.example.agnieszka.kidneyapp20.data.KidneyContract.normalizeDate;

/**
 * Created by Agnieszka on 28.03.2017.
 */

public class FoodJournal extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_journal);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new FoodJournalFragment())
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

    public static class FoodJournalFragment extends Fragment {

        private static final String LOG_TAG = DetailActivity2.DetailFragment2.class.getSimpleName();

        public FoodJournalFragment() {
            setHasOptionsMenu(true);
        }

        private static final String[] JOURNAL_COLUMNS = {
                // In this case the id needs to be fully qualified with a table name, since
                // the content provider joins the location & weather tables in the background
                // (both have an _id column)
                // On the one hand, that's annoying.  On the other, you can search the weather table
                // using the location set by the user, which is only in the Location table.
                // So the convenience is worth it.
                KidneyContract.JournalEntry.TABLE_NAME + "." + KidneyContract.JournalEntry._ID,
                KidneyContract.JournalEntry.COLUMN_DATE,
                KidneyContract.JournalEntry.COLUMN_FOOD_NAME,
                KidneyContract.JournalEntry.COLUMN_AMOUNT,
        };

        private static final String[] VALUES_COLUMNS = {
                // In this case the id needs to be fully qualified with a table name, since
                // the content provider joins the location & weather tables in the background
                // (both have an _id column)
                // On the one hand, that's annoying.  On the other, you can search the weather table
                // using the location set by the user, which is only in the Location table.
                // So the convenience is worth it.
                KidneyContract.ValuesEntry.TABLE_NAME + "." + KidneyContract.ValuesEntry._ID,
                KidneyContract.ValuesEntry.COLUMN_DATE,
                KidneyContract.ValuesEntry.COLUMN_KCAL,
                KidneyContract.ValuesEntry.COLUMN_CARBON,
                KidneyContract.ValuesEntry.COLUMN_FAT,
                KidneyContract.ValuesEntry.COLUMN_PROTEIN,
                KidneyContract.ValuesEntry.COLUMN_PHOSPHORUS,
                KidneyContract.ValuesEntry.COLUMN_SODIUM ,
                KidneyContract.ValuesEntry.COLUMN_POTASSIUM,
                KidneyContract.ValuesEntry.COLUMN_FLUID,
                KidneyContract.ValuesEntry.COLUMN_DIALYZED,
        };

        private ListView listViewJournal;
        private int mPosition = listViewJournal.INVALID_POSITION;
        private KidneyAdapter mKidneyAdapter;
        private KidneyJournalAdapter mKidneyJournalAdapter;

        long chosenDate;

        static final int COL_JOURNAL_ID = 0;
        static final int COL_JOURNAL_DATE = 1;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_food_journal, container, false);

            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
                String newString = intent.getStringExtra(Intent.EXTRA_TEXT);
                Log.d(LOG_TAG, "Intent: " + intent.getStringExtra(Intent.EXTRA_TEXT) + "String: " + newString);
                chosenDate = Long.parseLong(newString);
            }
            else{
                chosenDate = normalizeDate(System.currentTimeMillis());
            }

            // Sort order:  Ascending, by date.
            String sortOrder = KidneyContract.ValuesEntry.COLUMN_DATE + " DESC";
            Uri foodJournal = KidneyContract.ValuesEntry.buildValuesWithStartDate(System.currentTimeMillis());
            //Uri valuesForLocationUri = KidneyContract.ValuesEntry.buildValuesUri(0);
            Toast.makeText(getActivity().getApplicationContext(), "co zostalo inserted: " + foodJournal, Toast.LENGTH_SHORT).show();

            Cursor cur = getActivity().getContentResolver().query(foodJournal,
                    null, null, null, sortOrder);

            // The CursorAdapter will take data from our cursor and populate the ListView
            // However, we cannot use FLAG_AUTO_REQUERY since it is deprecated, so we will end
            // up with an empty list the first time we run.
            mKidneyAdapter = new KidneyAdapter(getActivity(), cur, 0);

            ListView listView = (ListView) rootView.findViewById(R.id.listview_values);
            listView.setAdapter(mKidneyAdapter);

            Uri valuesForToday = KidneyContract.JournalEntry.buildJournalWithStartDate(System.currentTimeMillis());
            //Uri valuesForToday = KidneyContract.JournalEntry.buildJournalUri(normalizeDate(System.currentTimeMillis()));

            String []todayDate = new String []{
                    Long.toString(normalizeDate(System.currentTimeMillis()))
            };

            String sortOrderJournal = KidneyContract.JournalEntry.COLUMN_DATE + " DESC";

            Cursor curJournal = getActivity().getContentResolver().query(valuesForToday,
                    JOURNAL_COLUMNS, " date = " + chosenDate, null, sortOrderJournal);

            Log.d(LOG_TAG, "chosen date in query: " + chosenDate);

            //curJournal = getValuesByDate(valuesForToday, null, null);
            // sprobowac z funkcja getValuesByDate

            mKidneyJournalAdapter = new KidneyJournalAdapter(getActivity(), curJournal, 0);

            listViewJournal = (ListView) rootView.findViewById(R.id.listview_journal);
            listViewJournal.setAdapter(mKidneyJournalAdapter);

            listViewJournal.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                    Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                    if (cursor != null) {
                        Intent intent = new Intent(getActivity(), DetailActivity2.class)
                                .setData(KidneyContract.JournalEntry.buildJournalWithDateAndId(
                                        cursor.getLong(COL_JOURNAL_DATE), cursor.getLong(COL_JOURNAL_ID)));
                        startActivity(intent);
                    }
                    mPosition = position;
                }
            });

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                    Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                    if (cursor != null) {
                        //Intent intent = new Intent(getActivity(), DetailActivity2.class)
                                //.setData(KidneyContract.JournalEntry.buildJournalWithDateAndId(
                                        //cursor.getLong(COL_JOURNAL_DATE), cursor.getLong(COL_JOURNAL_ID)));
                        //startActivity(intent);


                        chosenDate = normalizeDate(cursor.getLong(1));
                        String newString = Long.toString(chosenDate);
                        Log.d(LOG_TAG, "chosen date: " + newString);
                        Intent intent = new Intent(getActivity(), FoodJournal.class)
                                .putExtra(Intent.EXTRA_TEXT, newString);
                        startActivity(intent);
                    }
                    mPosition = position;
                }
            });
            return rootView;
        }

        @Override
        public void onStart() {
            super.onStart();
        }


    }

}


