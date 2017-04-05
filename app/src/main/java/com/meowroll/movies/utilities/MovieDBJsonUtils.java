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

    private static final String TAG = NetworkUtils.class.getSimpleName();
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

        //Log.v(TAG, "getSimpleMovieStringsFromJson " + movieJsonStr);
        /* root of the item list */
        final String j_result = "results";



        /* String array to hold each day's weather String */
        String[] parsedMovieData = null;

        JSONObject movieJson = new JSONObject(movieJsonStr);

        /* Is there an error? */



        JSONArray movieArray = movieJson.getJSONArray(j_result);




        parsedMovieData = new String[movieArray.length()];

        //long localDate = System.currentTimeMillis();
        //long utcDate = SunshineDateUtils.getUTCDateFromLocal(localDate);
        //long startDay = SunshineDateUtils.normalizeDate(utcDate);

        for (int i = 0; i < movieArray.length(); i++) {
            //Log.v(TAG, "movieArray.length()= " + movieArray.length());
            /* These are the values that will be collected */
            String poster_path;
            String overview;
            String release_date;
            String id;
            String original_title;
            String original_language;
            String title;
            String backdrop_path;
            double vote_average;




            /* Get the JSON object representing each movie */
            JSONObject eachMovie = movieArray.getJSONObject(i);



            poster_path = eachMovie.getString("poster_path");
            release_date = eachMovie.getString("release_date");
            overview = eachMovie.getString("overview");
            id = eachMovie.getString("id");
            original_title = eachMovie.getString("original_title");
            original_language = eachMovie.getString("original_language");
            backdrop_path = eachMovie.getString("backdrop_path");
            title = eachMovie.getString("title");
            //Log.v(TAG, "title= " + title);
            vote_average = eachMovie.getDouble("vote_average");


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


        final String j_result = "results";
        String[] parsedData = null;
        JSONObject trailersJson = new JSONObject(JsonStr);

        JSONArray mArray = trailersJson.getJSONArray(j_result);

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
                Log.d("pref",  "getFavoriteMovies:" + String.valueOf(parsedMovieData[i].toString()));
                i++;
            }
        } finally {
            cursor.close();
        }
        return parsedMovieData;
    }
}