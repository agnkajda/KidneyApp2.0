package com.example.agnieszka.kidneyapp20.data;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import android.util.Log;


import com.example.agnieszka.kidneyapp20.data.KidneyContract.JournalEntry;
import com.example.agnieszka.kidneyapp20.data.KidneyContract.ValuesEntry;

public class KidneyDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 4;

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
                JournalEntry.COLUMN_AMOUNT + " REAL NOT NULL, " +
                JournalEntry.COLUMN_KCAL + " REAL, " +
                JournalEntry.COLUMN_CARBON + " REAL, " +
                JournalEntry.COLUMN_FAT + " REAL, " +
                JournalEntry.COLUMN_PROTEIN + " REAL, " +
                JournalEntry.COLUMN_PHOSPHORUS+ " REAL, " +
                JournalEntry.COLUMN_SODIUM + " REAL, " +
                JournalEntry.COLUMN_POTASSIUM + " REAL, " +
                JournalEntry.COLUMN_FLUID + " REAL " +
                " );";


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

    public boolean CheckIfDateExists (long date) {

        SQLiteDatabase db = this.getReadableDatabase();
        String Query = "Select * from " +  KidneyContract.ValuesEntry.TABLE_NAME + " where " +
                KidneyContract.ValuesEntry.COLUMN_DATE + " = " + date;
        Cursor cursor = db.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public void updatingValue (double value, String columnName, long date){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("UPDATE " +  ValuesEntry.TABLE_NAME + " SET " +
                columnName + " = " + columnName + " + " + value +
                " WHERE " + ValuesEntry.COLUMN_DATE + " = " + date );
    }

    public void deletingValueFromValues (double value, String columnName, long date){
        SQLiteDatabase db = this.getReadableDatabase();
        value = value * (-1);
        db.execSQL("UPDATE " +  ValuesEntry.TABLE_NAME + " SET " +
                columnName + " = " + columnName + " + " + value +
                " WHERE " + ValuesEntry.COLUMN_DATE + " = " + date );
    }

    public void deletingValueFromJournal (long id){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL(" DELETE FROM " + JournalEntry.TABLE_NAME + " \n" +
                "WHERE " + JournalEntry.COLUMN_ID + " = " + id + "; ");
    }

    public ArrayList<Cursor> getData(String Query){
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[] { "mesage" };
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2= new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);


        try{
            String maxQuery = Query ;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);


            //add value to cursor2
            Cursor2.addRow(new Object[] { "Success" });

            alc.set(1,Cursor2);
            if (null != c && c.getCount() > 0) {


                alc.set(0,c);
                c.moveToFirst();

                return alc ;
            }
            return alc;
        } catch(SQLException sqlEx){
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        } catch(Exception ex){

            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+ex.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        }


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + JournalEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ValuesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);

    }
}