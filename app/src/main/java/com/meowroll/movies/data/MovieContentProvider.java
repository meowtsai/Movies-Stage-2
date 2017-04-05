package com.meowroll.movies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;

import static com.meowroll.movies.data.MovieContract.MovieEntry;

/**
 * Created by Sophie on 2017/4/1.
 */

public class MovieContentProvider extends ContentProvider {

    public static final int MOVIES = 100;
    public static final int MOVIE_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_MOVIES , MOVIES);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_MOVIES + "/#", MOVIE_WITH_ID);

        return uriMatcher;
    }


    private MovieDbHelper mMovieDbHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mMovieDbHelper = new MovieDbHelper(context);
        return true;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        int UriMatchId= sUriMatcher.match(uri);
        final  SQLiteDatabase db = mMovieDbHelper.getReadableDatabase();
        Cursor retCursor ;
        switch (UriMatchId)
        {
            case MOVIES:
                retCursor= db.query(MovieEntry.TABLE_NAME,projection, selection,selectionArgs,null,null, sortOrder);

                break;
            case MOVIE_WITH_ID:
                String sIdToQuery = uri.getPathSegments().get(1);
                retCursor= db.query(MovieEntry.TABLE_NAME,projection, MovieEntry.COLUMN_ID + "=?" , new String[]{sIdToQuery},null,null, sortOrder);
                break;
            default:
                    throw new UnsupportedOperationException("unknown uri" + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }


    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        int matchId = sUriMatcher.match(uri);
        Uri retUri ;
        switch (matchId)
        {
            case MOVIES:
                long id =db.insert(MovieEntry.TABLE_NAME,null,contentValues);
                if (id >0)
                {
                    retUri = ContentUris.withAppendedId(MovieEntry.CONTENT_URI, id);
                }
                else
                {
                    throw new android.database.SQLException("insert db error" + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("unknown uri:" + uri );
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return retUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        int matchId = sUriMatcher.match(uri);
        int affetedRows=0;
        switch (matchId)
        {
            case MOVIE_WITH_ID:
                String sIdToDelete = uri.getPathSegments().get(1);
                affetedRows =db.delete(MovieEntry.TABLE_NAME, MovieEntry.COLUMN_ID + "=?" , new String[]{sIdToDelete});
                break;
            default:
                throw new UnsupportedOperationException("unknown uri:" + uri );
        }

        if (affetedRows>0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return affetedRows;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        return 0;
    }
}
