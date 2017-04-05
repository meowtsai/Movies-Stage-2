package com.meowroll.movies.utilities;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Sophie on 2017/3/27.
 */

public class TrailersJsonUtils {
    private static final String TAG = TrailersJsonUtils.class.getSimpleName();


    public static String[] getSimpleTrailersStringsFromJson(Context context, String trailersJsonStr)
            throws JSONException {


        final String j_result = "results";
        String[] parsedData = null;
        JSONObject trailersJson = new JSONObject(trailersJsonStr);

        JSONArray mArray = trailersJson.getJSONArray(j_result);
        parsedData = new String[mArray.length()];

        //"id":"57950369925141136a00772b","iso_639_1":"en","iso_3166_1":"US","key":"Zvjmt4pwtdg","name":"Official Trailer","site":"YouTube","size":1080,"type":"Trailer"}
        for (int i = 0; i < mArray.length(); i++) {
            String id;
            String key;
            String name;
            String site;
            int size;
            String type;

            JSONObject eachMovie = mArray.getJSONObject(i);

            id = eachMovie.getString("id");
            key = eachMovie.getString("key");
            name = eachMovie.getString("name");
            site = eachMovie.getString("site");
            size = eachMovie.getInt("size");
            type = eachMovie.getString("type");

            parsedData[i] = id + "%" + key + "%" + name + "%" + site + "%" + size+ "%" + type;
        }

        return parsedData;
    }
}
