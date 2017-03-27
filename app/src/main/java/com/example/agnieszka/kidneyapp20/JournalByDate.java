package com.example.agnieszka.kidneyapp20;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ListAdapter;
import android.view.View.MeasureSpec;

import com.example.agnieszka.kidneyapp20.data.KidneyContract;

import static com.example.agnieszka.kidneyapp20.R.id.container;

/**
 * Created by Agnieszka on 27.03.2017.
 */

public class JournalByDate extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.journalbydate_activity);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.journalbydate_activity, new JournalByDateFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public static class JournalByDateFragment extends Fragment {

        public JournalByDateFragment() {
            setHasOptionsMenu(true);
        }

        private static final String LOG_TAG = JournalByDate.JournalByDateFragment.class.getSimpleName();

        String[] mSelectionArgs = {""};
        private ListView listViewJournal;
        private int mPosition = listViewJournal.INVALID_POSITION;
        private KidneyJournalAdapter mKidneyJournalAdapter;

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

        static final int COL_JOURNAL_ID = 0;
        static final int COL_JOURNAL_DATE = 1;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState){

        View rootView = inflater.inflate(R.layout.journalbydate, container, false);

        String sortOrderJournal = KidneyContract.JournalEntry.COLUMN_DATE + " DESC";


        Uri valuesForToday = KidneyContract.JournalEntry.buildJournalWithStartDate(System.currentTimeMillis());

            long currentTime = KidneyContract.normalizeDate(System.currentTimeMillis());

            Cursor curJournal = getActivity().getContentResolver().query(valuesForToday,
                JOURNAL_COLUMNS, " date = " + currentTime, null, sortOrderJournal);

            Log.v(LOG_TAG, "intent looks like this: " + valuesForToday);

            mKidneyJournalAdapter = new KidneyJournalAdapter(getActivity(), curJournal, 0);

            listViewJournal = (ListView) rootView.findViewById(R.id.listview_products);
            listViewJournal.setAdapter(mKidneyJournalAdapter);

            listViewJournal.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    // CursorAdapter returns a cursor at the correct position for getItem(), or null
                    // if it cannot seek to that position.
                /*Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    ((Callback) getActivity())
                            .onItemSelected(KidneyContract.JournalEntry.buildJournalWithDate(
                                    cursor.getLong(COL_JOURNAL_DATE)
                            ));
                }
                mPosition = position;*/
                    //testujemy powoli
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

        return rootView;
    }
}
}
