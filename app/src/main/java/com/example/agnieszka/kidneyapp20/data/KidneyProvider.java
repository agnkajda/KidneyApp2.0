package com.example.agnieszka.kidneyapp20.data;


import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class KidneyProvider extends ContentProvider {

        // The URI Matcher used by this content provider.
        private static final UriMatcher sUriMatcher = buildUriMatcher();
        private KidneyDbHelper mOpenHelper;

        static final int JOURNAL = 300;
        static final int JOURNAL_WITH_DATE = 301;
        static final int JOURNAL_WITH_DATE_AND_ID = 302;
        static final int VALUES = 100;
        static final int VALUES_WITH_DATE = 101;

        private static final SQLiteQueryBuilder sJournalByDateQueryBuilder;
    private static final SQLiteQueryBuilder sValuesByDateQueryBuilder;

        static{
            sJournalByDateQueryBuilder = new SQLiteQueryBuilder();

            //to jest chyba niepotrzebne, ja nie musze miec inner joina
            //weather INNER JOIN location ON weather.location_id = location._id
            // niby połączyłam te dwa datami
            sJournalByDateQueryBuilder.setTables(
                    KidneyContract.JournalEntry.TABLE_NAME);
                //TODO: tutaj chyba powinno być coś jeszcze, żeby wybierało po dacie
                    /*KidneyContract.JournalEntry.TABLE_NAME + " INNER JOIN " +
                        KidneyContract.ValuesEntry.TABLE_NAME +
                        " ON " + KidneyContract.JournalEntry.TABLE_NAME +
                        "." + KidneyContract.JournalEntry.COLUMN_DATE +
                        " = " + KidneyContract.ValuesEntry.TABLE_NAME +
                        "." + KidneyContract.ValuesEntry.COLUMN_DATE);*/

            sValuesByDateQueryBuilder = new SQLiteQueryBuilder();
            sValuesByDateQueryBuilder.setTables(
                    KidneyContract.ValuesEntry.TABLE_NAME);
    }

        //location.location_setting >= ?
        private static final String sJournalDateSelection =
                KidneyContract.JournalEntry.TABLE_NAME+
                        "." + KidneyContract.JournalEntry.COLUMN_DATE + " = ? ";

    private static final String sJournalWithDateAndIdSelection =
            KidneyContract.JournalEntry.TABLE_NAME+
                    "." + KidneyContract.JournalEntry.COLUMN_DATE + " = ? AND " +
                    KidneyContract.JournalEntry.COLUMN_ID + " = ? ";

        //location.location_setting = ? AND date >= ?
        private static final String sJournalStartDateSelection =
                KidneyContract.JournalEntry.TABLE_NAME+
                        "." + KidneyContract.JournalEntry.COLUMN_DATE + " >= ? ";

        //location.location_setting >= ?
        private static final String sValuesDateSelection =
            KidneyContract.ValuesEntry.TABLE_NAME+
                    "." + KidneyContract.ValuesEntry.COLUMN_DATE + " = ? ";

        //location.location_setting = ? AND date >= ?
        private static final String sValuesStartDateSelection =
            KidneyContract.ValuesEntry.TABLE_NAME+
                    "." + KidneyContract.ValuesEntry.COLUMN_DATE + " >= ? ";


        private Cursor getJournalByDate(Uri uri, String[] projection, String sortOrder) {
            long startDate = KidneyContract.JournalEntry.getStartDateFromUri(uri);

            String[] selectionArgs;
            String selection;

            if (startDate == 0) {
                selection = sJournalDateSelection;
                selectionArgs = null; // Nie wiem, co tutaj ma być??? nic? null?
            } else {
                selectionArgs = new String[]{Long.toString(startDate)};
                selection = sJournalStartDateSelection;
            }

            return sJournalByDateQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
            );
        }

    private Cursor getValuesByDate(Uri uri, String[] projection, String sortOrder) {
        long startDate = KidneyContract.ValuesEntry.getStartDateFromUri(uri);

        String[] selectionArgs;
        String selection;

        if (startDate == 0) {
            selection = sValuesDateSelection;
            selectionArgs = null;
        } else {
            selectionArgs = new String[]{Long.toString(startDate)};
            selection = sValuesStartDateSelection;
        }

        return sValuesByDateQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getJournalByDateAndId(
            Uri uri, String[] projection, String sortOrder) {
        long id = KidneyContract.JournalEntry.getIdFromUri(uri);
        long date = KidneyContract.JournalEntry.getDateFromUri(uri);

        //TO DO: tu uzupełnić
        return sJournalByDateQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sJournalWithDateAndIdSelection,
                new String[]{Long.toString(id), Long.toString(date)},
                null,
                null,
                sortOrder
        );
    }

        static UriMatcher buildUriMatcher() {
            // 1) The code passed into the constructor represents the code to return for the root
            // URI.  It's common to use NO_MATCH as the code for this case. Add the constructor below.
            final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
            final String authority = KidneyContract.CONTENT_AUTHORITY;

            // 2) Use the addURI function to match each of the types.  Use the constants from
            // KidneyContract to help define the types to the UriMatcher.
            matcher.addURI(authority, KidneyContract.PATH_JOURNAL, JOURNAL);
            matcher.addURI(authority, KidneyContract.PATH_VALUES, VALUES);
            matcher.addURI(authority, KidneyContract.PATH_JOURNAL + "/#", JOURNAL_WITH_DATE);
            matcher.addURI(authority, KidneyContract.PATH_VALUES + "/#", VALUES_WITH_DATE);
            matcher.addURI(authority, KidneyContract.PATH_JOURNAL + "/#/#", JOURNAL_WITH_DATE_AND_ID);
            //# - matches only number, * - matches any text
            // 3) Return the new matcher!
            return matcher;
        }

        @Override
        public boolean onCreate() {
            mOpenHelper = new KidneyDbHelper(getContext());
            return true;
        }

        @Override
        public String getType(Uri uri) {

            // Use the Uri Matcher to determine what kind of URI this is.
            final int match = sUriMatcher.match(uri);

            switch (match) {

                //CONTENT_TYPE - zwraca kilka wierszy
                //CONTENT ITEM TYPE - zwraca jeden wiersz
                case JOURNAL_WITH_DATE_AND_ID:
                    return KidneyContract.JournalEntry.CONTENT_ITEM_TYPE;
                case JOURNAL:
                    return KidneyContract.JournalEntry.CONTENT_TYPE;
                case VALUES:
                    return KidneyContract.ValuesEntry.CONTENT_TYPE;
                case JOURNAL_WITH_DATE:
                    return KidneyContract.JournalEntry.CONTENT_TYPE;
                case VALUES_WITH_DATE:
                    return KidneyContract.ValuesEntry.CONTENT_ITEM_TYPE;
                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        @Override
        public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                            String sortOrder) {
            // Here's the switch statement that, given a URI, will determine what kind of request it is,
            // and query the database accordingly.
            //przesyłamy przez content provider parametry (jako argumenty ^), a poniżej wpisujemy je
            //do bazy danych
            Cursor retCursor;
            switch (sUriMatcher.match(uri)) {

                case VALUES_WITH_DATE:
                {
                    retCursor = getValuesByDate(uri, projection, sortOrder);
                    break;
                }

                case JOURNAL_WITH_DATE: {
                    retCursor = getJournalByDate(uri, projection, sortOrder);
                    break;
                }

                //TO DO: tutaj zrobic case

                case JOURNAL_WITH_DATE_AND_ID: {
                    retCursor = getJournalByDateAndId(uri, projection, sortOrder);
                    break;
                }

                case JOURNAL: {
                    retCursor = mOpenHelper.getReadableDatabase().query(
                            KidneyContract.JournalEntry.TABLE_NAME,
                            projection,
                            selection,
                            selectionArgs,
                            null,
                            null,
                            sortOrder
                    );
                    break;
                }

                case VALUES: {
                    retCursor = mOpenHelper.getReadableDatabase().query(
                            KidneyContract.ValuesEntry.TABLE_NAME,
                            projection,
                            selection,
                            selectionArgs,
                            null,
                            null,
                            sortOrder
                    );
                    break;
                }

                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
            retCursor.setNotificationUri(getContext().getContentResolver(), uri);
            return retCursor;
        }

        @Override
        public Uri insert(Uri uri, ContentValues values) {
            final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
            final int match = sUriMatcher.match(uri);
            Uri returnUri;

            switch (match) {
                case JOURNAL: {
                    normalizeDateForJournal(values);
                    long _id = db.insert(KidneyContract.JournalEntry.TABLE_NAME, null, values);
                    if ( _id > 0 )
                        returnUri = KidneyContract.JournalEntry.buildJournalUri(_id);
                    else
                        throw new android.database.SQLException("Failed to insert row into " + uri);
                    break;
                }

                case VALUES: {
                    normalizeDateForValues(values);
                    long _id = db.insert(KidneyContract.ValuesEntry.TABLE_NAME, null, values);
                    if ( _id > 0 )
                        returnUri = KidneyContract.ValuesEntry.buildValuesUri(_id);
                    else
                        throw new android.database.SQLException("Failed to insert row into " + uri);
                    break;
                }
                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
            getContext().getContentResolver().notifyChange(uri, null);
            return returnUri;
        }

        @Override
        public int delete(Uri uri, String selection, String[] selectionArgs) {
            // Student: Start by getting a writable database
            final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
            final int match = sUriMatcher.match(uri);
            int rowsDeleted;
            // this makes delete all rows return the number of rows deleted
            if ( null == selection ) selection = "1";
            switch (match) {
                case JOURNAL:
                    rowsDeleted = db.delete(
                            KidneyContract.JournalEntry.TABLE_NAME, selection, selectionArgs);
                    break;
                case VALUES:
                    rowsDeleted = db.delete(
                            KidneyContract.ValuesEntry.TABLE_NAME, selection, selectionArgs);
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);
            }

            // A null value deletes all rows.  In my implementation of this, I only notified
            // the uri listeners (using the content resolver) if the rowsDeleted != 0 or the selection
            // is null.
            // Oh, and you should notify the listeners here.

            if (rowsDeleted != 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }

            return rowsDeleted;
        }

        private void normalizeDateForJournal(ContentValues values) {
            // normalize the date value
            if (values.containsKey(KidneyContract.JournalEntry.COLUMN_DATE)) {
                long dateValue = values.getAsLong(KidneyContract.JournalEntry.COLUMN_DATE);
                values.put(KidneyContract.JournalEntry.COLUMN_DATE, KidneyContract.normalizeDate(dateValue));
            }
        }

    private void normalizeDateForValues(ContentValues values) {
        // normalize the date value
        if (values.containsKey(KidneyContract.ValuesEntry.COLUMN_DATE)) {
            long dateValue = values.getAsLong(KidneyContract.ValuesEntry.COLUMN_DATE);
            values.put(KidneyContract.ValuesEntry.COLUMN_DATE, KidneyContract.normalizeDate(dateValue));
        }
    }

        @Override
        public int update(
                Uri uri, ContentValues values, String selection, String[] selectionArgs) {
            final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

            final int match = sUriMatcher.match(uri);
            int rowsUpdated;

            switch (match) {
                case JOURNAL:
                    rowsUpdated = db.update(
                            KidneyContract.JournalEntry.TABLE_NAME, values, selection, selectionArgs);
                    break;
                case VALUES:
                    rowsUpdated = db.update(
                            KidneyContract.ValuesEntry.TABLE_NAME, values, selection, selectionArgs);
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);
            }

            if (rowsUpdated != 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }

            return rowsUpdated;
        }

        @Override
        public int bulkInsert(Uri uri, ContentValues[] values) {
            final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
            final int match = sUriMatcher.match(uri);
            switch (match) {
                case JOURNAL:
                    db.beginTransaction();
                    int returnCount = 0;
                    try {
                        for (ContentValues value : values) {
                            normalizeDateForJournal(value);
                            long _id = db.insert(KidneyContract.JournalEntry.TABLE_NAME, null, value);
                            if (_id != -1) {
                                returnCount++;
                            }
                        }
                        db.setTransactionSuccessful();
                    } finally {
                        db.endTransaction();
                    }
                    getContext().getContentResolver().notifyChange(uri, null);
                    return returnCount;

                default:
                    return super.bulkInsert(uri, values); // drugi raz return?!
            }
        }

        // You do not need to call this method. This is a method specifically to assist the testing
        // framework in running smoothly. You can read more at:
        // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
        @Override
        @TargetApi(11)
        public void shutdown() {
            mOpenHelper.close();
            super.shutdown();
        }

}
