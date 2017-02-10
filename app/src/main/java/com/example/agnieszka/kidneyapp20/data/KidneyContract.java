package com.example.agnieszka.kidneyapp20.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

public class KidneyContract {

    public static final String CONTENT_AUTHORITY = "com.example.agnieszka.kidneyapp20";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_JOURNAL= "journal";
    public static final String PATH_VALUES = "nutritional";

    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }

    //tutaj tworzymy jedną tabelę

    public static final class JournalEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_JOURNAL).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_JOURNAL;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_JOURNAL;

        //nazwa tabeli
        public static final String TABLE_NAME = "journal";

        //ID robi się automatycznie

        //TO DO: co z tym id?
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_FOOD_NAME = "food_name";
        public static final String COLUMN_AMOUNT = "amount";
        public static final String COLUMN_KCAL = "kcal";
        public static final String COLUMN_CARBON = "carbon";
        public static final String COLUMN_FAT = "fat";
        public static final String COLUMN_PROTEIN = "protein";
        public static final String COLUMN_PHOSPHORUS = "phosphorus";
        public static final String COLUMN_SODIUM = "sodium";
        public static final String COLUMN_POTASSIUM = "potassium";
        public static final String COLUMN_FLUID = "fluid";


        public static Uri buildJournalUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
            }


        public static Uri buildJournalWithStartDate(long startDate) {
            long normalizedDate = normalizeDate(startDate);
            return CONTENT_URI.buildUpon()
                    .appendQueryParameter(COLUMN_DATE, Long.toString(normalizedDate)).build();
        }

        public static Uri buildJournalWithDate(long date) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(normalizeDate(date))).build();
        }

        public static Uri buildJournalWithDateAndId(long date, long id) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(normalizeDate(date)))
                    .appendEncodedPath(Long.toString(id)).build();
        }

        public static long getStartDateFromUri(Uri uri) {
            String dateString = uri.getQueryParameter(COLUMN_DATE);
            if (null != dateString && dateString.length() > 0)
                return Long.parseLong(dateString);
            else
                return 0;
        }

        public static long getIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(2));
        }

        public static long getDateFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
            }

    }

    //tutaj tworzymy drugą tabelę

    public static final class ValuesEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_VALUES).build();
        // tu będzie "content://com.example.agnieszka.kidneyapp/nutritional

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VALUES;
        //vnd.android.cursor.dir/com.example.agnieszka.kidneyapp/nutritional
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VALUES;

        public static final String TABLE_NAME = "nutritional";

        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_KCAL = "kcal";
        public static final String COLUMN_CARBON = "carbon";
        public static final String COLUMN_FAT = "fat";
        public static final String COLUMN_PROTEIN = "protein";
        public static final String COLUMN_PHOSPHORUS = "phosphorus";
        public static final String COLUMN_SODIUM = "sodium";
        public static final String COLUMN_POTASSIUM = "potassium";
        public static final String COLUMN_FLUID = "fluid";
        public static final String COLUMN_DIALYZED = "dialyzed";


        public static Uri buildValuesUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
            }

        public static Uri buildValuesWithDate(long date) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(date)).build();
        }

        // get(1)? chyba tak?
        public static long getDateFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static Uri buildValuesWithStartDate(long startDate) {
            long normalizedDate = normalizeDate(startDate);
            return CONTENT_URI.buildUpon()
                    .appendQueryParameter(COLUMN_DATE, Long.toString(normalizedDate)).build();
        }

        public static long getStartDateFromUri(Uri uri) {
            String dateString = uri.getQueryParameter(COLUMN_DATE);
            if (null != dateString && dateString.length() > 0)
                return Long.parseLong(dateString);
            else
                return 0;
        }

    }
}
