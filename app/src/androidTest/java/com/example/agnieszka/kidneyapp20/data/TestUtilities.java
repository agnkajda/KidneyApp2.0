package com.example.agnieszka.kidneyapp20.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import com.example.agnieszka.kidneyapp20.utils.PollingCheck;

import java.util.Map;
import java.util.Set;

public class TestUtilities  extends AndroidTestCase {
    static final String TEST_LOCATION = "99705";
    static final long TEST_DATE = 1419033600L;  // December 20th, 2014

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    /*
        Students: Use this to create some default kidney values for your database tests.
     */
    static ContentValues createSampleValues() {
        ContentValues kidneyValues = new ContentValues();
        kidneyValues.put(KidneyContract.ValuesEntry.COLUMN_DATE, TEST_DATE);
        kidneyValues.put(KidneyContract.ValuesEntry.COLUMN_KCAL, 1.1);
        kidneyValues.put(KidneyContract.ValuesEntry.COLUMN_CARBON, 1.2);
        kidneyValues.put(KidneyContract.ValuesEntry.COLUMN_FAT, 1.3);
        kidneyValues.put(KidneyContract.ValuesEntry.COLUMN_PROTEIN, 75);
        kidneyValues.put(KidneyContract.ValuesEntry.COLUMN_PHOSPHORUS, 65);
        kidneyValues.put(KidneyContract.ValuesEntry.COLUMN_POTASSIUM, 5);
        kidneyValues.put(KidneyContract.ValuesEntry.COLUMN_FLUID, 5.5);
        kidneyValues.put(KidneyContract.ValuesEntry.COLUMN_DIALYZED, 1);

        return kidneyValues;
    }

    /*
        Students: You can uncomment this helper function once you have finished creating the
        JournalEntry part of the KidneyContract.
     */
    static ContentValues createSampleJournal() {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(KidneyContract.JournalEntry.COLUMN_DATE, TEST_DATE);
        testValues.put(KidneyContract.JournalEntry.COLUMN_FOOD_NAME, "juice");
        testValues.put(KidneyContract.JournalEntry.COLUMN_AMOUNT, 200);

        return testValues;
    }

    /*
        Students: You can uncomment this function once you have finished creating the
        JournalEntry part of the KidneyContract as well as the KidneyDbHelper.
     */
    static long insertSampleJournal(Context context) {
        // insert our test records into the database
        KidneyDbHelper dbHelper = new KidneyDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createSampleJournal();

        long locationRowId;
        locationRowId = db.insert(KidneyContract.JournalEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert North Pole Location Values", locationRowId != -1);

        return locationRowId;
    }

    static long insertSampleValues(Context context) {
        // insert our test records into the database
        KidneyDbHelper dbHelper = new KidneyDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createSampleValues();

        long locationRowId;
        locationRowId = db.insert(KidneyContract.JournalEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert North Pole Location Values", locationRowId != -1);

        return locationRowId;
    }

    /*
        Students: The functions we provide inside of TestProvider use this utility class to test
        the ContentObserver callbacks using the PollingCheck class that we grabbed from the Android
        CTS tests.
        Note that this only tests that the onChange function is called; it does not test that the
        correct Uri is returned.
     */
    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}
