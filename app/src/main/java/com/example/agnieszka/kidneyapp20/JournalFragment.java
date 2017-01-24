package com.example.agnieszka.kidneyapp20;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import com.example.agnieszka.kidneyapp20.data.KidneyContract;

public class JournalFragment extends Fragment {

    private KidneyAdapter mKidneyAdapter;

    public JournalFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Sort order:  Ascending, by date.
        String sortOrder = KidneyContract.ValuesEntry.COLUMN_DATE + " DESC";
        Uri valuesForToday = KidneyContract.ValuesEntry.buildValuesWithStartDate(System.currentTimeMillis());
        //Uri valuesForLocationUri = KidneyContract.ValuesEntry.buildValuesUri(0);
        Toast.makeText(getActivity().getApplicationContext(), "co zostalo inserted: " + valuesForToday + " lolo ", Toast.LENGTH_SHORT).show();

        Cursor cur = getActivity().getContentResolver().query(valuesForToday,
                null, null, null, sortOrder);

        // The CursorAdapter will take data from our cursor and populate the ListView
        // However, we cannot use FLAG_AUTO_REQUERY since it is deprecated, so we will end
        // up with an empty list the first time we run.
        mKidneyAdapter = new KidneyAdapter(getActivity(), cur, 0);

        View rootView = inflater.inflate(R.layout.fragment_activitytocheck, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_journal);
        listView.setAdapter(mKidneyAdapter);



        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}

