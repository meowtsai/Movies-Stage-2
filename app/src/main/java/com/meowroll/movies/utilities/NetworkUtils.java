package com.meowroll.movies.utilities;

/**
 * Created by Meow on 2017/1/3.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * These utilities will be used to communicate with the weather servers.
 */
public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();
    private static final String apiKey = "YOUR_KEY";
    

    private static final String THEMOVIEDB_API_URL =
            "https://api.themoviedb.org/3/movie/";
    private static final String API_KEY_PARAM="api_key";
    private static final String LANG_PARAM="language";
    private static final String PAGE_PARAM="page";

    private static final String THEMOVIEDB_POSTER_URL ="https://image.tmdb.org/t/p/w500";


    /**
     * Builds the URL used to talk to the weather server using a location. This location is based
     * on the query capabilities of the weather provider that we are using.
     *
     * @param rankingType The rankingType that will be queried for.
     * @return The URL to use to query the weather server.
     */
    public static URL buildUrl(String rankingType) {
        Uri builtUri = Uri.parse(THEMOVIEDB_API_URL + rankingType).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, apiKey)
                .appendQueryParameter(LANG_PARAM, "en-us")
                .appendQueryParameter(PAGE_PARAM, "1")
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }


    public static URL buildUrlForTrailers(String movieId) {
        Uri builtUri = Uri.parse(THEMOVIEDB_API_URL + movieId + "/videos").buildUpon()
                .appendQueryParameter(API_KEY_PARAM, apiKey)
                .appendQueryParameter(LANG_PARAM, "en-US")
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    public static URL buildUrlForReviews(String movieId) {
        Uri builtUri = Uri.parse(THEMOVIEDB_API_URL + movieId + "/reviews").buildUpon()
                .appendQueryParameter(API_KEY_PARAM, apiKey)
                .appendQueryParameter(LANG_PARAM, "en-US")
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    public static URL buildUrlForPoster(String imageFileName) {
        Uri builtUri = Uri.parse(THEMOVIEDB_POSTER_URL + imageFileName).buildUpon()
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        //Log.v(TAG, "buildUrlForPoster" + url);

        return url;
    }
    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                //Log.v(TAG, "return " );
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static Bitmap getImageStreamFromHttpUrl(URL url) throws IOException
    {
        OkHttpClient client = new OkHttpClient();



        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response=null;
        Bitmap bitmap = null;
        try {
            response = client.newCall(request).execute();
            bitmap = BitmapFactory.decodeStream(response.body().byteStream());

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        if (response!=null) {
            return bitmap;
        }
        else
        {
            return null;
        }

    }
}