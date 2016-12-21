package com.example.agnieszka.kidneyapp20.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.agnieszka.kidneyapp20.data.KidneyContract.JournalEntry;
import com.example.agnieszka.kidneyapp20.data.KidneyContract.ValuesEntry;

public class KidneyDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;

    static final String DATABASE_NAME = "kidney.db";

    public KidneyDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_JOURNAL_TABLE = "CREATE TABLE " + JournalEntry.TABLE_NAME + " (" +
                JournalEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                JournalEntry.COLUMN_DATE + " INTEGER NOT NULL, " +
                JournalEntry.COLUMN_FOOD_NAME + " TEXT NOT NULL, " +
                JournalEntry.COLUMN_AMOUNT + " REAL NOT NULL " + //REAL CZY INTEGER?
                " );";
/*
        final String SQL_CREATE_VALUES_TABLE = "CREATE TABLE " + ValuesEntry.TABLE_NAME + " (" +
                ValuesEntry._ID + " INTEGER PRIMARY KEY," +
                ValuesEntry.COLUMN_DATE + " INTEGER NOT NULL, " +
                ValuesEntry.COLUMN_KCAL + " REAL NOT NULL, " +
                ValuesEntry.COLUMN_CARBON + " REAL NOT NULL, " +
                ValuesEntry.COLUMN_FAT + " REAL NOT NULL, " +
                ValuesEntry.COLUMN_PROTEIN + " REAL NOT NULL, " +
                ValuesEntry.COLUMN_PHOSPHORUS+ " REAL NOT NULL, " +
                ValuesEntry.COLUMN_SODIUM + " REAL NOT NULL, " +
                ValuesEntry.COLUMN_POTASSIUM + " REAL NOT NULL, " +
                ValuesEntry.COLUMN_FLUID + " REAL NOT NULL, " +
                ValuesEntry.COLUMN_IF_DIALYZED + " INTEGER NOT NULL" +
                " );";
                */

        final String SQL_CREATE_VALUES_TABLE = "CREATE TABLE " + ValuesEntry.TABLE_NAME + " (" +
                ValuesEntry._ID + " INTEGER PRIMARY KEY," +
                ValuesEntry.COLUMN_DATE + " INTEGER NOT NULL, " +
                ValuesEntry.COLUMN_KCAL + " REAL, " +
                ValuesEntry.COLUMN_CARBON + " REAL, " +
                ValuesEntry.COLUMN_FAT + " REAL, " +
                ValuesEntry.COLUMN_PROTEIN + " REAL, " +
                ValuesEntry.COLUMN_PHOSPHORUS+ " REAL, " +
                ValuesEntry.COLUMN_SODIUM + " REAL, " +
                ValuesEntry.COLUMN_POTASSIUM + " REAL, " +
                ValuesEntry.COLUMN_FLUID + " REAL, " +
                ValuesEntry.COLUMN_DIALYZED + " INTEGER" +
                ");";

        sqLiteDatabase.execSQL(SQL_CREATE_JOURNAL_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_VALUES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + JournalEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ValuesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);

    }
}
