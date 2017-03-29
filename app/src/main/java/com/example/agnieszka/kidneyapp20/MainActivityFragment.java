package com.example.agnieszka.kidneyapp20;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ListAdapter;
import android.view.View.MeasureSpec;

import com.example.agnieszka.kidneyapp20.data.KidneyContract;

import static com.example.agnieszka.kidneyapp20.ChooseTheMeal.context;
import static com.example.agnieszka.kidneyapp20.Utility.getFluidIntake;
import static com.example.agnieszka.kidneyapp20.data.KidneyContract.normalizeDate;

public class MainActivityFragment extends Fragment {

    Button addFood;
    Button deleteAll;
    Context context;
    Button button;
    Button test;
    Button dialysis;
    ListView today;
    TextView fluidIntake;
    TextView timeLeftButton;
    private ListView listViewJournal;
    private int mPosition = listViewJournal.INVALID_POSITION;
    private KidneyAdapter mKidneyAdapter;
    private KidneyJournalAdapter mKidneyJournalAdapter;
    private KidneyAdapterToday mKidneyAdapterToday;

    public MainActivityFragment() {
    }

    private static final String[] JOURNAL_COLUMNS = {

            KidneyContract.JournalEntry.TABLE_NAME + "." + KidneyContract.JournalEntry._ID,
            KidneyContract.JournalEntry.COLUMN_DATE,
            KidneyContract.JournalEntry.COLUMN_FOOD_NAME,
            KidneyContract.JournalEntry.COLUMN_AMOUNT,
    };

    private static final String[] VALUES_COLUMNS = {

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

    static final int COL_JOURNAL_ID = 0;
    static final int COL_JOURNAL_DATE = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main_new, container, false);

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

        Button foodJournalButton = (Button) rootView.findViewById(R.id.food_journal_button);
        View.OnClickListener clickingJournal = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context = getActivity().getApplicationContext();
                Intent intent = new Intent (context, FoodJournal.class);
                startActivity(intent);
            }

        };
        foodJournalButton.setOnClickListener(clickingJournal);
/*
        Button button =(Button)rootView.findViewById(R.id.yourbuttonid);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent dbmanager = new Intent(getActivity(),AndroidDatabaseManager.class);
                startActivity(dbmanager);
            }
        });
*/
        dialysis = (Button) rootView.findViewById(R.id.dialysis);
        View.OnClickListener clickingDialysis = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("LOG", "I am clicking dialysis button");
                AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
                builder2.setMessage("Have you been dialyzed today?");
                builder2.setCancelable(true);

                builder2.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                context = getActivity().getApplicationContext();
                                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("fluid", "0");
                                editor.commit();
                                Intent intent = new Intent (context, MainActivity.class);
                                startActivity(intent);
                                dialog.cancel();
                            }
                        });

                builder2.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
                AlertDialog alert11 = builder2.create();
                alert11.show();
            }

        };
        dialysis.setOnClickListener(clickingDialysis);

        double mIntake =Utility.getFluidIntake(getActivity().getApplicationContext());
        double maxIntake = Utility.getFluidLimit(getActivity().getApplicationContext())
                * Utility.getDialysis(getActivity().getApplicationContext());
        double dialysisTime =Utility.getDialysis(getActivity().getApplicationContext());

        double timeLeftD = (maxIntake - mIntake)/Utility.getFluidLimit(getActivity().getApplicationContext());
        int timeLeft = (int)timeLeftD;

        Log.d("LOG", "time left double: " + timeLeftD);
        fluidIntake = (TextView) rootView.findViewById(R.id.fluid_intake);
        fluidIntake.setText("Fluid intake after last dialysis: " + mIntake  + "/" + maxIntake);

        timeLeftButton = (TextView) rootView.findViewById(R.id.time_left);
        timeLeftButton.setText("Days left to dialysis: " + timeLeft);

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

        Uri valuesForToday = KidneyContract.JournalEntry.buildJournalWithStartDate(System.currentTimeMillis());

        String []todayDate = new String []{
                Long.toString(normalizeDate(System.currentTimeMillis()))
        };

        String sortOrderJournal = KidneyContract.JournalEntry.COLUMN_DATE + " DESC";

        Cursor curJournal = getActivity().getContentResolver().query(valuesForToday,
                JOURNAL_COLUMNS, " date = " + normalizeDate(System.currentTimeMillis()), null, sortOrderJournal);


        mKidneyJournalAdapter = new KidneyJournalAdapter(getActivity(), curJournal, 0);

        listViewJournal = (ListView) rootView.findViewById(R.id.listview_journal);
        listViewJournal.setAdapter(mKidneyJournalAdapter);

        //TODAYS VALUES

        long currentTime = KidneyContract.normalizeDate(System.currentTimeMillis());

        Cursor curTodaysValues = getActivity().getContentResolver().query(foodJournal,
                VALUES_COLUMNS, " date = " + currentTime, null, sortOrderJournal);


        mKidneyAdapterToday = new KidneyAdapterToday(getActivity(), curTodaysValues, 0);

        today = (ListView) rootView.findViewById(R.id.today);
        today.setAdapter(mKidneyAdapterToday);

        ListUtils.setDynamicHeight(listViewJournal);
        //getTotalHeightofListView (listView);
        //getTotalHeightofListView(listViewJournal);

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

    public static void getTotalHeightofListView(ListView listView) {

        ListAdapter mAdapter = listView.getAdapter();

        int totalHeight = 0;

        for (int i = 0; i < mAdapter.getCount(); i++) {
            View mView = mAdapter.getView(i, null, listView);

            mView.measure(
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),

                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

            totalHeight += mView.getMeasuredHeight();
            //Log.w("HEIGHT" + i, String.valueOf(totalHeight));

        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (mAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();

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