package com.meowroll.movies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Sophie on 2017/3/31.
 */

public class MovieContract {

    public static final String AUTHORITY = "com.meowroll.movies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_MOVIES = "movies";

    //The titles and ids of the user's favorite movies are stored in a ContentProvider backed by a SQLite database. This ContentProvider is updated whenever the user favorites or unfavorites a movie.
    // title, release date, movie poster, vote average, and plot synopsis.
   //Extend the favorites ContentProvider to store the movie poster, synopsis, user rating, and release date, and display them even when offline.
    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String TABLE_NAME = "movies";


        public static final String COLUMN_ID = "id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_RELEASEDATE = "release_date";
        public static final String COLUMN_POSTER = "movie_poster";
        public static final String COLUMN_AVGVOTE = "avg_vote";
        public static final String COLUMN_PLOT = "plot";



    }
}
