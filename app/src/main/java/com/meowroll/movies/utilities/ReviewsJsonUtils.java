package com.meowroll.movies.utilities;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Sophie on 2017/3/28.
 */


public class ReviewsJsonUtils {
    private static final String TAG = ReviewsJsonUtils.class.getSimpleName();


    public static JSONArray getSimpleReviewJso(Context context, String trailersJsonStr)
            throws JSONException {


        final String j_result = "results";
        String[] parsedData = null;
        JSONObject trailersJson = new JSONObject(trailersJsonStr);

        JSONArray mArray = trailersJson.getJSONArray(j_result);

        return mArray;


    }
}