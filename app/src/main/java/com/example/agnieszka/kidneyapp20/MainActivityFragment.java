package com.example.agnieszka.kidneyapp20;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private KidneyAdapter mKidneyAdapter;
    private KidneyJournalAdapter mKidneyJournalAdapter;

    public MainActivityFragment() {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
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
                //uri = KidneyContract.JournalEntry.CONTENT_URI;
                //rowsDeleted = context.getContentResolver().delete(uri, null, null);
                Intent intent = new Intent (context, MainActivity.class);
                startActivity(intent);
            }

        };
        deleteAll.setOnClickListener(clickingToDelete);



        // Sort order:  Ascending, by date.
        String sortOrder = KidneyContract.ValuesEntry.COLUMN_DATE + " ASC";
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

        Cursor curJournal = getActivity().getContentResolver().query(valuesForToday,
                null, null, null, sortOrder);

        //curJournal = getValuesByDate(valuesForToday, null, null);
        // sprobowac z funkcja getValuesByDate

        mKidneyJournalAdapter = new KidneyJournalAdapter(getActivity(), curJournal, 0);

        ListView listViewJournal = (ListView) rootView.findViewById(R.id.listview_journal);
        listViewJournal.setAdapter(mKidneyJournalAdapter);

        ListUtils.setDynamicHeight(listView);
        ListUtils.setDynamicHeight(listViewJournal);

        return rootView;
    }

    public static class ListUtils {
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
}
