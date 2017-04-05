package com.meowroll.movies.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.meowroll.movies.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

/**
 * Created by Meow on 2017/1/3.
 */

public final class MovieDBJsonUtils {

    private static final String JSON_RESULT_HEADING =
            "results";
    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String JSON_POSTER_PATH ="poster_path";

    private static final String JSON_RELEASE_DATE ="release_date";
    private static final String JSON_OVERVIEW ="overview";

    private static final String JSON_ID ="id";
    private static final String JSON_ORIGINAL_TITLE ="original_title";

    private static final String JSON_TITLE ="title";
    private static final String JSON_VOTEAVG ="vote_average";


    /**
     * This method parses JSON from a web response and returns an array of Strings
     * describing the weather over various days from the forecast.
     * <p/>
     * Later on, we'll be parsing the JSON into structured data within the
     * getFullWeatherDataFromJson function, leveraging the data we have stored in the JSON. For
     * now, we just convert the JSON into human-readable strings.
     *
     * @param movieJsonStr JSON response from server
     *
     * @return Array of Strings describing weather data
     *
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static String[] getSimpleMovieStringsFromJson(Context context, String movieJsonStr)
            throws JSONException {



        /* String array to hold each day's weather String */
        String[] parsedMovieData = null;

        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieJson.getJSONArray(JSON_RESULT_HEADING);
         parsedMovieData = new String[movieArray.length()];

        for (int i = 0; i < movieArray.length(); i++) {
            String poster_path;
            String overview;
            String release_date;
            String id;
            String original_title;
            String original_language;
            String title;
            String backdrop_path;
            double vote_average;
            JSONObject eachMovie = movieArray.getJSONObject(i);

            poster_path = eachMovie.getString(JSON_POSTER_PATH);
            release_date = eachMovie.getString(JSON_RELEASE_DATE);
            overview = eachMovie.getString(JSON_OVERVIEW);
            id = eachMovie.getString(JSON_ID);
            original_title = eachMovie.getString(JSON_ORIGINAL_TITLE);

            title = eachMovie.getString(JSON_TITLE);
            //Log.v(TAG, "title= " + title);
            vote_average = eachMovie.getDouble(JSON_VOTEAVG);



            /*
             * Temperatures are sent by Open Weather Map in a child object called "temp".
             *
             * Editor's Note: Try not to name variables "temp" when working with temperature.
             * It confuses everybody. Temp could easily mean any number of things, including
             * temperature, temporary and is just a bad variable name.
             */

            parsedMovieData[i] = title + "#" + original_title + "#" + poster_path + "#" + vote_average + "#" + release_date+ "#" + overview+ "#" + id;
        }

        return parsedMovieData;
    }
    public static JSONArray getJSONArray(Context context, String JsonStr)
            throws JSONException {



        String[] parsedData = null;
        JSONObject trailersJson = new JSONObject(JsonStr);

        JSONArray mArray = trailersJson.getJSONArray(JSON_RESULT_HEADING);

        return mArray;


    }

    public static String getFirstTrailerUrl(JSONArray jsonArray)
            throws JSONException {


           if (jsonArray.length() > 0) {
               JSONObject eachMovie = jsonArray.getJSONObject(0);
               return eachMovie.getString("key");
           } else {
               return null;
           }


    }
    public static String[] getFavoriteMovies(Cursor cursor)
    {

        String[] parsedMovieData =new String[cursor.getCount()];
        int i =0;
        try {


            while (cursor.moveToNext()) {
                String id= cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ID));
                String poster_path= cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER));
                String overview= cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_PLOT));
                String release_date= cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASEDATE));

                String original_title= cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE));
                String title= cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE));
                double vote_average= cursor.getDouble(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_AVGVOTE));

                parsedMovieData[i] = title + "#" + original_title + "#" + poster_path + "#" + vote_average + "#" + release_date+ "#" + overview+ "#" + id;
                //Log.d("pref",  "getFavoriteMovies:" + String.valueOf(parsedMovieData[i].toString()));
                i++;
            }
        } finally {
            cursor.close();
        }
        return parsedMovieData;
    }
}