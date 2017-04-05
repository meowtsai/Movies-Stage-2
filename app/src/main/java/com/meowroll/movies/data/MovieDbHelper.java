package com.meowroll.movies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.meowroll.movies.data.MovieContract.MovieEntry;
/**
 * Created by Sophie on 2017/3/31.
 */

public class MovieDbHelper  extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "movies.db";

    private static final int VERSION = 1;

    MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {


        final String CREATE_TABLE = "CREATE TABLE "  + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID                + " INTEGER , " +
                MovieEntry.COLUMN_ID                + " INTEGER PRIMARY KEY NOT NULL, " +
                MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_RELEASEDATE    + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_POSTER    + " TEXT , " +
                MovieEntry.COLUMN_AVGVOTE    + " DOUBLE NOT NULL, " +
                MovieEntry.COLUMN_PLOT    + " TEXT NOT NULL" +

        ");";

        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);

    }
}
