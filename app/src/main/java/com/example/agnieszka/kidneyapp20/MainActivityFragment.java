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
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.agnieszka.kidneyapp20.data.KidneyContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    Button addFood;
    Context context;
    private KidneyAdapter mKidneyAdapter;

    public MainActivityFragment() {
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

        // Sort order:  Ascending, by date.
        String sortOrder = KidneyContract.ValuesEntry.COLUMN_DATE + " ASC";
        Uri valuesForToday = KidneyContract.ValuesEntry.buildValuesWithStartDate(System.currentTimeMillis());
        //Uri valuesForLocationUri = KidneyContract.ValuesEntry.buildValuesUri(0);
        Toast.makeText(getActivity().getApplicationContext(), "co zostalo inserted: " + valuesForToday, Toast.LENGTH_SHORT).show();

        Cursor cur = getActivity().getContentResolver().query(valuesForToday,
                null, null, null, sortOrder);

        // The CursorAdapter will take data from our cursor and populate the ListView
        // However, we cannot use FLAG_AUTO_REQUERY since it is deprecated, so we will end
        // up with an empty list the first time we run.
        mKidneyAdapter = new KidneyAdapter(getActivity(), cur, 0);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_journal);
        listView.setAdapter(mKidneyAdapter);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
