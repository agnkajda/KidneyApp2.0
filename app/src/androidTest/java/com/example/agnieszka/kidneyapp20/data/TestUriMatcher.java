package com.example.agnieszka.kidneyapp20.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

public class TestUriMatcher  extends AndroidTestCase {
    private static final String LOCATION_QUERY = "London, UK";
    private static final long TEST_DATE = 1419033600L;  // December 20th, 2014
    private static final long TEST_LOCATION_ID = 10L;

    // content://com.example.android.sunshine.app/weather"
    private static final Uri TEST_JOURNAL_DIR = KidneyContract.JournalEntry.CONTENT_URI;
    private static final Uri TEST_JOURNAL_WITH_DATE_DIR = KidneyContract.JournalEntry.buildJournalWithDate(TEST_DATE);
    // content://com.example.android.sunshine.app/location"
    private static final Uri TEST_VALUES_DIR = KidneyContract.ValuesEntry.CONTENT_URI;
    private static final Uri TEST_VALUES_WITH_DATE_DIR = KidneyContract.ValuesEntry.buildValuesWithDate(TEST_DATE);

    /*
        Students: This function tests that your UriMatcher returns the correct integer value
        for each of the Uri types that our ContentProvider can handle.  Uncomment this when you are
        ready to test your UriMatcher.
     */
    public void testUriMatcher() {
        UriMatcher testMatcher = KidneyProvider.buildUriMatcher();

        assertEquals("Error: The JOURNAL URI was matched incorrectly.",
                testMatcher.match(TEST_JOURNAL_DIR), KidneyProvider.JOURNAL);
        assertEquals("Error: The JOURNAL WITH DATE URI was matched incorrectly.",
                testMatcher.match(TEST_JOURNAL_WITH_DATE_DIR), KidneyProvider.JOURNAL_WITH_DATE);
        assertEquals("Error: The VALUES WITH DATE URI was matched incorrectly.",
                testMatcher.match(TEST_VALUES_WITH_DATE_DIR), KidneyProvider.VALUES_WITH_DATE);
        assertEquals("Error: The VALUES URI was matched incorrectly.",
                testMatcher.match(TEST_VALUES_DIR), KidneyProvider.VALUES);
    }
}