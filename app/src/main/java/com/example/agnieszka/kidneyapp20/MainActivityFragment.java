package com.example.agnieszka.kidneyapp20;

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

import static com.example.agnieszka.kidneyapp20.data.KidneyContract.normalizeDate;

public class MainActivityFragment extends Fragment {

    Button addFood;
    Button deleteAll;
    Context context;
    private ListView listViewJournal;
    private int mPosition = listViewJournal.INVALID_POSITION;
    private KidneyAdapter mKidneyAdapter;
    private KidneyJournalAdapter mKidneyJournalAdapter;

    public MainActivityFragment() {
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

    static final int COL_JOURNAL_ID = 0;
    static final int COL_JOURNAL_DATE = 1;
    static final int COL_JOURNAL_FOOD_NAME = 2;
    static final int COL_JOURNAL_AMOUNT = 3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        addFood = (Button) rootView.findViewById(R.id.add_food_button);
        View.OnClickListener clicking = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context = getActivity().getApplicationContext();
                Intent intent = new Intent (context, AddingFood.class);
                startActivity(intent);
            }

        };
        addFood.setOnClickListener(clicking);

        deleteAll = (Button) rootView.findViewById(R.id.delete_all_button);
        View.OnClickListener clickingToDelete = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context = getActivity().getApplicationContext();
                Uri uri = KidneyContract.ValuesEntry.CONTENT_URI;
                int rowsDeleted;
                rowsDeleted = context.getContentResolver().delete(uri, null, null);
                uri = KidneyContract.JournalEntry.CONTENT_URI;
                rowsDeleted = context.getContentResolver().delete(uri, null, null);
                Intent intent = new Intent (context, MainActivity.class);
                startActivity(intent);
            }

        };
        deleteAll.setOnClickListener(clickingToDelete);



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
                null, null, null, sortOrderJournal);

        //curJournal = getValuesByDate(valuesForToday, null, null);
        // sprobowac z funkcja getValuesByDate

        mKidneyJournalAdapter = new KidneyJournalAdapter(getActivity(), curJournal, 0);

        listViewJournal = (ListView) rootView.findViewById(R.id.listview_journal);
        listViewJournal.setAdapter(mKidneyJournalAdapter);

        ListUtils.setDynamicHeight(listView);
        ListUtils.setDynamicHeight(listViewJournal);

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
            }
        });

        return rootView;
    }

    public static class ListUtils {
        // funkcja, dzieki ktorej wysokosc ListView jest dynamicznie obliczana
        public static void setDynamicHeight(ListView mListView) {
            ListAdapter mListAdapter = mListView.getAdapter();
            if (mListAdapter == null) {
                // when adapter is null
                return;
            }
            int height = 0;
            int desiredWidth = MeasureSpec.makeMeasureSpec(mListView.getWidth(), MeasureSpec.UNSPECIFIED);
            for (int i = 0; i < mListAdapter.getCount(); i++) {
                View listItem = mListAdapter.getView(i, null, mListView);
                listItem.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
                height += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = mListView.getLayoutParams();
            params.height = height + (mListView.getDividerHeight() * (mListAdapter.getCount() - 1));
            mListView.setLayoutParams(params);
            mListView.requestLayout();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri dateUri);
    }
}