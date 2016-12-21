package com.example.agnieszka.kidneyapp20.data;

import android.net.Uri;
import android.test.AndroidTestCase;

public class TestKidneyContract extends AndroidTestCase {
    private static final long TEST_KIDNEY_DATE = 1419033600L;  // December 20th, 2014

    /*
        Students: Uncomment this out to test your weather location function.
     */
    public void testBuildValuesWithDate() {
        Uri locationUri = (KidneyContract.ValuesEntry.buildValuesWithDate(TEST_KIDNEY_DATE));
        assertNotNull("Error: Null Uri returned.  You must fill-in buildWeatherLocation in " +
                        "WeatherContract.",
                locationUri);
        assertEquals("Error: Weather location not properly appended to the end of the Uri",
                TEST_KIDNEY_DATE, Long.parseLong(locationUri.getLastPathSegment()));
        assertEquals("Error: Weather location Uri doesn't match our expected result",
                locationUri.toString(),
                "content://com.example.agnieszka.kidneyapp20/nutritional/1419033600");
    }
}
