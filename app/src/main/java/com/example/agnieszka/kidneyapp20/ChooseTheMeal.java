package com.example.agnieszka.kidneyapp20;

import android.content.ContentValues;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import com.example.agnieszka.kidneyapp20.data.KidneyContract;
import com.example.agnieszka.kidneyapp20.data.KidneyContract.ValuesEntry;
import com.example.agnieszka.kidneyapp20.data.KidneyContract.JournalEntry;

import static com.example.agnieszka.kidneyapp20.Utility.round;
import static com.example.agnieszka.kidneyapp20.data.KidneyContract.normalizeDate;

public class ChooseTheMeal extends AppCompatActivity {

    static Button addToJournal;
    static Context context;
    static String foodJsonStr;
    static int maxPositions;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_the_meal);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ChooseTheMealFragment())
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class ChooseTheMealFragment extends Fragment {

        private ArrayAdapter<String> mFood;
        static EditText typeAmount;

        public ChooseTheMealFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Add this line in order for this fragment to handle menu events.
            setHasOptionsMenu(true);
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.testfragment, menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.

            int id = item.getItemId();
            if (id == R.id.action_refresh) {
                String numberNDBO;
                Intent intent = getActivity().getIntent();
                /*if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
                    numberNDBO = intent.getStringExtra(Intent.EXTRA_TEXT);
                }
                else {
                    numberNDBO = "01009";
                }*/
                numberNDBO = "01009";

                FetchValuesTask foodTask = new FetchValuesTask();
                foodTask.execute(numberNDBO);
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            String[] data = {
                    "Data is being loaded"
            };

            List<String> foodValues = new ArrayList<String>(Arrays.asList(data));

            mFood =
                    new ArrayAdapter<String>(
                            getActivity(), // The current context (this activity)
                            R.layout.list_item_forecast_3, // The name of the layout ID.
                            R.id.list_item_forecast_textview_3, // The ID of the textview to populate.
                            foodValues);

            View rootView = inflater.inflate(R.layout.activity_choose_the_meal_fragment, container, false);

            typeAmount = (EditText) rootView.findViewById(R.id.type_amount);

            ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast_3);
            listView.setAdapter(mFood);

            addToJournal = (Button) rootView.findViewById(R.id.add_to_journal_button);
            View.OnClickListener clicking = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context = getActivity().getApplicationContext();

                    final String NDB_REPORT = "report";
                    final String NDB_FOOD = "food";
                    final String NDB_NUTRIENTS = "nutrients";
                    final String NDB_VALUE = "value";
                    final String NDB_NAME = "name";
                    final String NDB_ID = "nutrient_id";

                    final int water = 255;
                    final int energy = 208;
                    final int carbohydrate = 205;
                    final int protein = 203;
                    final int fat = 204;
                    final int phosphorus = 305;
                    final int potassium = 306;
                    final int sodium = 307;

                    String amountStr = typeAmount.getText().toString();
                    double amount = Double.parseDouble(amountStr);

                    try {

                        JSONObject foodJson = new JSONObject(foodJsonStr);
                        JSONObject report = foodJson.getJSONObject(NDB_REPORT);
                        JSONObject food = report.getJSONObject(NDB_FOOD);
                        JSONArray nutrientsArray = food.getJSONArray(NDB_NUTRIENTS);

                        Vector<ContentValues> cVVector = new Vector<ContentValues>(nutrientsArray.length());
                        Vector<ContentValues> cVVectorJournal = new Vector<ContentValues>(nutrientsArray.length());

                        Time dayTime = new Time();
                        dayTime.setToNow();

                        // we start at the day returned by local time. Otherwise this is a mess.
                        int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

                        // now we work exclusively in UTC
                        dayTime = new Time();

                        maxPositions = nutrientsArray.length();
                        String[] resultStrs = new String[maxPositions];

                        ContentValues kidneyValues = new ContentValues();
                        ContentValues journalValues = new ContentValues();

                        long dateTime;

                        // Cheating to convert this to UTC time, which is what we want anyhow
                        dateTime = dayTime.setJulianDay(julianStartDay);

                        kidneyValues.put(ValuesEntry.COLUMN_DATE, dateTime);
                        journalValues.put(JournalEntry.COLUMN_DATE, dateTime);

                        for (int i = 0; i < nutrientsArray.length(); i++) {

                            String type;
                            double value;
                            int nutrientId;

                            JSONObject foodValues = nutrientsArray.getJSONObject(i);
                            type = foodValues.getString(NDB_NAME);
                            value = foodValues.getDouble(NDB_VALUE);
                            nutrientId = foodValues.getInt(NDB_ID);
                            value = value * amount * 0.01;
                            value = round(value, 2);

                            journalValues.put(JournalEntry.COLUMN_FOOD_NAME, type);
                            journalValues.put(JournalEntry.COLUMN_AMOUNT, amount);

                            switch (nutrientId) {
                                case water:
                                    kidneyValues.put(ValuesEntry.COLUMN_FLUID, value);
                                    journalValues.put(JournalEntry.COLUMN_FLUID, value);
                                    break;

                                case energy:
                                    kidneyValues.put(ValuesEntry.COLUMN_KCAL, value);
                                    journalValues.put(JournalEntry.COLUMN_KCAL, value);
                                    break;

                                case carbohydrate:
                                    kidneyValues.put(ValuesEntry.COLUMN_CARBON, value);
                                    journalValues.put(JournalEntry.COLUMN_CARBON, value);
                                    break;

                                case fat:
                                    kidneyValues.put(ValuesEntry.COLUMN_FAT, value);
                                    journalValues.put(JournalEntry.COLUMN_FAT, value);
                                    break;

                                case protein:
                                    kidneyValues.put(ValuesEntry.COLUMN_PROTEIN, value);
                                    journalValues.put(JournalEntry.COLUMN_PROTEIN, value);
                                    break;

                                case phosphorus:
                                    kidneyValues.put(ValuesEntry.COLUMN_PHOSPHORUS, value);
                                    journalValues.put(JournalEntry.COLUMN_PHOSPHORUS, value);
                                    break;

                                case sodium:
                                    kidneyValues.put(ValuesEntry.COLUMN_SODIUM, value);
                                    journalValues.put(JournalEntry.COLUMN_SODIUM, value);
                                    break;

                                case potassium:
                                    kidneyValues.put(ValuesEntry.COLUMN_POTASSIUM, value);
                                    journalValues.put(JournalEntry.COLUMN_POTASSIUM, value);
                                    break;
                            }
                        }

                            cVVector.add(kidneyValues);
                            cVVectorJournal.add(journalValues);

                            Context mContext;

                            mContext = getActivity().getApplicationContext();

                            Uri inserted = KidneyContract.ValuesEntry.CONTENT_URI;
                            inserted = mContext.getContentResolver().insert(KidneyContract.ValuesEntry.CONTENT_URI, kidneyValues);

                            Uri insertedToJournal = KidneyContract.JournalEntry.CONTENT_URI;
                            insertedToJournal = mContext.getContentResolver().
                                    insert(KidneyContract.JournalEntry.CONTENT_URI, journalValues);

                            String sortOrder = KidneyContract.ValuesEntry.COLUMN_DATE + " ASC";
                            Uri weatherForLocationUri = KidneyContract.ValuesEntry.buildValuesWithStartDate(
                                    System.currentTimeMillis());

                            String sortOrderJournal = KidneyContract.JournalEntry.COLUMN_DATE + " ASC";
                            Uri JournalUri = KidneyContract.JournalEntry.buildJournalWithStartDate(
                                System.currentTimeMillis());

                    }
                    catch (JSONException e) {
                        Log.e("LOG_TAG", e.getMessage(), e);
                        e.printStackTrace();
                    }

                    Intent intent = new Intent (context, MainActivity.class);
                    startActivity(intent);
                }

            };
            addToJournal.setOnClickListener(clicking);

            // The detail Activity called via intent.  Inspect the intent for forecast data.
            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
                String foodStr = intent.getStringExtra(Intent.EXTRA_TEXT);
                ((TextView) rootView.findViewById(R.id.values_text))
                        .setText(foodStr);
            }
            return rootView;
        }

        private void updateFetchingValuesData() {
            String numberNDBO;
            Intent intent = getActivity().getIntent();
                if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
                    numberNDBO = intent.getStringExtra(Intent.EXTRA_TEXT);
                    StringTokenizer tokens = new StringTokenizer(numberNDBO, "|");
                    String firstPart = tokens.nextToken();
                    numberNDBO = tokens.nextToken();
                    numberNDBO = numberNDBO.trim();
                }
                else {
                    numberNDBO = "01009";
                }
            FetchValuesTask foodTask = new FetchValuesTask();
            foodTask.execute(numberNDBO);
        }

        @Override
        public void onStart() {
            super.onStart();
            updateFetchingValuesData();
        }

        public class FetchValuesTask extends AsyncTask<String, Void, String[]> {

            private final String LOG_TAG = TestActivityFragment.FetchTask.class.getSimpleName();
            //można tu walnąć konstruktor i wtedy przenieść do innego pliku tę całą klasę
            private boolean DEBUG = true;

            private String getReadableDateString(long time){
                // Because the API returns a unix timestamp (measured in seconds),
                // it must be converted to milliseconds in order to be converted to valid date.
                Date date = new Date(time);
                SimpleDateFormat format = new SimpleDateFormat("E, MMM d");
                return format.format(date).toString();
            }

            //jakaś funkcja konwertująca, żeby program działał tymczasowo (bo na razie nie powinien działać po zmianach)

            String[] convertContentValuesToUXFormat(Vector<ContentValues> cvv) {
                // return strings to keep UI functional for now
                String[] resultStrs = new String[cvv.size()];
                for ( int i = 0; i < cvv.size(); i++ ) {
                    ContentValues weatherValues = cvv.elementAt(i);
                    resultStrs[i] = getReadableDateString(
                            weatherValues.getAsLong(KidneyContract.ValuesEntry.COLUMN_DATE)) +
                            " - " + weatherValues.getAsString(KidneyContract.ValuesEntry.COLUMN_KCAL);
                }
                return resultStrs;
            }

            private String[] getValuesFromJson(String foodJsonStr, int maxPositions)
                    throws JSONException {

                final String NDB_REPORT = "report";
                final String NDB_FOOD = "food";
                final String NDB_NUTRIENTS = "nutrients";
                final String NDB_VALUE = "value";
                final String NDB_NAME = "name";
                final String NDB_ID = "nutrient_id";

                try{

                JSONObject foodJson = new JSONObject(foodJsonStr);
                JSONObject report = foodJson.getJSONObject(NDB_REPORT);
                JSONObject food = report.getJSONObject(NDB_FOOD);
                JSONArray nutrientsArray = food.getJSONArray(NDB_NUTRIENTS);

                Vector<ContentValues> cVVector = new Vector<ContentValues>(nutrientsArray.length());

                Time dayTime = new Time();
                dayTime.setToNow();

                // we start at the day returned by local time. Otherwise this is a mess.
                int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

                // now we work exclusively in UTC
                dayTime = new Time();

                maxPositions = nutrientsArray.length();
                String[] resultStrs = new String[maxPositions];

                long dateTime;

                // Cheating to convert this to UTC time, which is what we want anyhow
                dateTime = dayTime.setJulianDay(julianStartDay);

                for (int i = 0; i < nutrientsArray.length(); i++) {

                    String type;
                    double value;
                    int nutrientId;

                    JSONObject foodValues = nutrientsArray.getJSONObject(i);
                    type = foodValues.getString(NDB_NAME);
                    value = foodValues.getDouble(NDB_VALUE);
                    nutrientId = foodValues.getInt(NDB_ID);

                    resultStrs[i] = value + " - " + type;
                }
                return resultStrs;

                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected String[] doInBackground(String... params) {

                // If there's no zip code, there's nothing to look up.  Verify size of params.
                if (params.length == 0) {
                    return null;
                }

                // These two need to be declared outside the try/catch
                // so that they can be closed in the finally block.
                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;

                // Will contain the raw JSON response as a string.
                foodJsonStr = null;

                String format = "json";
                String type = "b";
                maxPositions = 7;

                try {
                    final String FORECAST_BASE_URL = "http://api.nal.usda.gov/ndb/reports/?";
                    final String FORMAT_PARAM = "format";
                    final String TYPE_PARAM = "type";
                    final String NDBO_PARAM = "ndbno";
                    final String APPID_PARAM = "api_key";

                    Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                            .appendQueryParameter(NDBO_PARAM, params[0])
                            .appendQueryParameter(TYPE_PARAM, type)
                            .appendQueryParameter(FORMAT_PARAM, format)
                            .appendQueryParameter(APPID_PARAM, BuildConfig.NDB_API_KEY)
                            .build();

                    URL url = new URL(builtUri.toString());

                    Log.v(LOG_TAG, "Built URI " + builtUri.toString());

                    // Create the request to OpenWeatherMap, and open the connection
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    // Read the input stream into a String
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        // Nothing to do.
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                        // But it does make debugging a *lot* easier if you print out the completed
                        // buffer for debugging.
                        buffer.append(line + "\n");
                    }

                    if (buffer.length() == 0) {
                        // Stream was empty.  No point in parsing.
                        return null;
                    }
                    foodJsonStr = buffer.toString();
                    Log.v(LOG_TAG, "Food string: " + foodJsonStr);
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error ", e);
                    // If the code didn't successfully get the weather data, there's no point in attemping
                    // to parse it.
                    return null;
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                            Log.e(LOG_TAG, "Error closing stream", e);
                        }
                    }
                }

                try {
                    return getValuesFromJson(foodJsonStr, maxPositions);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }

                // This will only happen if there was an error getting or parsing the forecast.
                return null;
            }

            @Override
            protected void onPostExecute(String[] result) {
                if (result != null & mFood != null) {
                    mFood.clear();
                    for (String dayForecastStr : result) {
                        mFood.add(dayForecastStr);
                        Log.d("My array list content: ", dayForecastStr);
                    }
                }
            }
        }
    }
}
