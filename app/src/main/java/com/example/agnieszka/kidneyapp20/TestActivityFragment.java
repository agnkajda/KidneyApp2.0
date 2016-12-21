package com.example.agnieszka.kidneyapp20;

import android.os.Bundle;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
import java.util.List;
import java.util.StringTokenizer;

import android.content.Intent;


public class TestActivityFragment extends Fragment {

    public static String gFoodName;
    private ArrayAdapter<String> mForecastAdapter2;

    public TestActivityFragment() {
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
            String food;
            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
                food = intent.getStringExtra(Intent.EXTRA_TEXT);
            }
            else {
                food = "juice";
            }
            FetchTask weatherTask = new FetchTask();
            weatherTask.execute(food);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Create some dummy data for the ListView.  Here's a sample weekly forecast
        String[] data = {
                "Data is loading"
        };
        List<String> weekForecast = new ArrayList<String>(Arrays.asList(data));

        // Now that we have some dummy forecast data, create an ArrayAdapter.
        // The ArrayAdapter will take data from a source (like our dummy forecast) and
        // use it to populate the ListView it's attached to.
        mForecastAdapter2 =
                new ArrayAdapter<String>(
                        getActivity(), // The current context (this activity)
                        R.layout.list_item_forecast_2, // The name of the layout ID.
                        R.id.list_item_forecast_textview_2, // The ID of the textview to populate.
                        weekForecast);

        View rootView = inflater.inflate(R.layout.activity_test, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast_2);
        listView.setAdapter(mForecastAdapter2);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String forecast = mForecastAdapter2.getItem(position);
                Intent intent = new Intent(getActivity(), ChooseTheMeal.class)
                        .putExtra(Intent.EXTRA_TEXT, forecast);
                startActivity(intent);
            }
        });


        return rootView;
    }

    private void updateSearchingForFood() {
        String food;
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            food = intent.getStringExtra(Intent.EXTRA_TEXT);
        }
        else {
            food = "juice";
        }
        FetchTask weatherTask = new FetchTask();
        weatherTask.execute(food);
        }

    @Override
    public void onStart() {
       super.onStart();
        updateSearchingForFood();
        }

    public class FetchTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchTask.class.getSimpleName();


        private String getReadableDateString(long time){
            // Because the API returns a unix timestamp (measured in seconds),
            // it must be converted to milliseconds in order to be converted to valid date.
            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
            return shortenedDateFormat.format(time);
        }


        private String formatHighLows(double high, double low) {
            // For presentation, assume the user doesn't care about tenths of a degree.
            long roundedHigh = Math.round(high);
            long roundedLow = Math.round(low);

            String highLowStr = roundedHigh + "/" + roundedLow;
            return highLowStr;
        }


        private String[] getWeatherDataFromJson(String forecastJsonStr, int maxPositions)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.

            final String NDB_LIST = "list";
            final String NDB_NDBNO = "ndbno";
            final String NDB_NAME = "name";

            final String NDB_ITEM = "item";

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONObject item = forecastJson.getJSONObject(NDB_LIST);
            JSONArray weatherArray = item.getJSONArray(NDB_ITEM);

            // OWM returns daily forecasts based upon the local time of the city that is being
            // asked for, which means that we need to know the GMT offset to translate this data
            // properly.

            // Since this data is also sent in-order and the first day is always the
            // current day, we're going to take advantage of that to get a nice
            // normalized UTC date for all of our weather.

            Time dayTime = new Time();
            dayTime.setToNow();

            // we start at the day returned by local time. Otherwise this is a mess.
            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

            // now we work exclusively in UTC
            dayTime = new Time();

            String[] resultStrs = new String[maxPositions];
            for(int i = 0; i < weatherArray.length(); i++) {
                // For now, using the format "Day, description, hi/low"
                String day;
                String description;
                String highAndLow;


                JSONObject foodValues = weatherArray.getJSONObject(i);


                int number = foodValues.getInt(NDB_NDBNO);
                String foodName = foodValues.getString(NDB_NAME);

/*
                JSONObject listJson = forecastJson.getJSONObject(NDB_LIST);
                JSONObject itemJson = listJson.getJSONObject(NDB_ITEM);
                String itemName = itemJson.getString(NDB_NAME);
                String itemNdbno = itemJson.getString(NDB_NDBNO);
*/

                // The date/time is returned as a long.  We need to convert that
                // into something human-readable, since most people won't read "1400356800" as
                // "this saturday".
                long dateTime;
                // Cheating to convert this to UTC time, which is what we want anyhow
                dateTime = dayTime.setJulianDay(julianStartDay+i);
                day = getReadableDateString(dateTime);

                // description is in a child array called "weather", which is 1 element long.
                //JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                //description = weatherObject.getString(OWM_DESCRIPTION);

                // Temperatures are in a child object called "temp".  Try not to name variables
                // "temp" when working with temperature.  It confuses everybody.
                //JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                //double high = temperatureObject.getDouble(OWM_MAX);
                //double low = temperatureObject.getDouble(OWM_MIN);

               // highAndLow = formatHighLows(high, low);
                //resultStrs[i] = day + " - " + description + " - " + highAndLow;
                resultStrs[i] = foodName + " | " + number;
            }
            for (String s : resultStrs) {
                Log.v(LOG_TAG, "Forecast entry: " + s);
            }
            return resultStrs;

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
            String forecastJsonStr = null;

            String format = "json";
            String units = "metric";
            String sort = "r";
            int maxPositions = 20;
            int offset = 0;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast

                final String FORECAST_BASE_URL =
                        "http://api.nal.usda.gov/ndb/search/?";
                final String FORMAT_PARAM = "format"; // format to json
                final String QUERY_PARAM = "q";
                final String SORT_PARAM = "sort"; //tylko co to jest ten sort?
                final String MAX_PARAM = "max";
                final String OFFSET_PARAM = "offset";
                final String APPID_PARAM = "api_key";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .appendQueryParameter(QUERY_PARAM, params[0]) //to jest ta nazwa wpisana
                        .appendQueryParameter(SORT_PARAM, sort)
                        .appendQueryParameter(MAX_PARAM, Integer.toString(maxPositions))
                        .appendQueryParameter(OFFSET_PARAM, Integer.toString(offset))
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
                forecastJsonStr = buffer.toString();
                Log.v(LOG_TAG, "Forecast string: " + forecastJsonStr);
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
                return getWeatherDataFromJson(forecastJsonStr, maxPositions);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                mForecastAdapter2.clear();
                for(String dayForecastStr : result) {
                    mForecastAdapter2.add(dayForecastStr);
                }
                // New data is back from the server.  Hooray!
            }
        }
    }
}