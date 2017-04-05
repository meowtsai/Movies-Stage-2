package com.meowroll.movies;

import android.content.ContentProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.meowroll.movies.data.MovieContract;
import com.meowroll.movies.utilities.MovieDBJsonUtils;
import com.meowroll.movies.utilities.NetworkUtils;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.net.URL;

public class MainActivity extends AppCompatActivity
        implements movieDBAdapter.movieDBAdapterOnClickHandler,
        SharedPreferences.OnSharedPreferenceChangeListener, LoaderManager.LoaderCallbacks<String[]>

    {

    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private TextView mErrorMessageDisplay;
    private movieDBAdapter mMovieDBAdapterAdapter;
    private ProgressBar mLoadingIndicator;

    private static final int LOADERID_MOVIESQUERY=118;
    private static final String  QUERY_SORTEDBY_EXTRA = "condition";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Context mContext = this;

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_moviegrid );
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);


        GridLayoutManager layoutManager = new GridLayoutManager(this,3 );

        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);

        mMovieDBAdapterAdapter = new movieDBAdapter(this);
        mRecyclerView.setAdapter(mMovieDBAdapterAdapter);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        /* Once all of our views are setup, we can load the weather data.*/


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        String sSortingBy  = sharedPreferences.getString(getString(R.string.pref_sorting_key),getString(R.string.pref_sorting_by_popularity_value));
        loadMovieData(sSortingBy);

    }


    private void loadMovieData(String sRankBy) {
        //String location = SunshinePreferences.getPreferredWeatherLocation(this);
        //new FetchWeatherTask().execute(sRankBy);
        Bundle queryBundle = new Bundle();
        queryBundle.putString(QUERY_SORTEDBY_EXTRA,sRankBy);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String[]> movieSearchLoader =loaderManager.getLoader(LOADERID_MOVIESQUERY);

        if (movieSearchLoader==null)
        {
            loaderManager.initLoader(LOADERID_MOVIESQUERY,queryBundle,this);

        }
        else
        {
            loaderManager.restartLoader(LOADERID_MOVIESQUERY,queryBundle,this);
        }
    }


        @Override
        public Loader<String[]> onCreateLoader(int id, final Bundle args) {
            return new AsyncTaskLoader<String[]>(this) {
                String[] mMoviesQueryResult;
                @Override
                protected void onStartLoading() {
                    super.onStartLoading();
                    if (args==null)
                    {
                        return;
                    }
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    if (mMoviesQueryResult!=null)
                    {
                        mLoadingIndicator.setVisibility(View.INVISIBLE);
                        deliverResult(mMoviesQueryResult);
                    }
                    else {
                        forceLoad();
                    }
                }

                @Override
                public String[] loadInBackground() {
                    String sCondition = args.getString(QUERY_SORTEDBY_EXTRA);
                    if (sCondition==null || TextUtils.isEmpty(sCondition)) {
                        return null;
                    }

                    Log.d("pref","loadInBackground" + sCondition);
                    String[] simpleJsonMovieData=null;
                    switch (sCondition)
                    {
                        case "favorites":
                            Cursor cursor= getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,null,null,null,null);
                            simpleJsonMovieData = MovieDBJsonUtils.getFavoriteMovies(cursor);

                            break;
                        default:
                            URL movieRequestUrl = NetworkUtils.buildUrl(sCondition);
                            try {
                                String jsonMovieResponse = NetworkUtils
                                        .getResponseFromHttpUrl(movieRequestUrl);

                                 simpleJsonMovieData = MovieDBJsonUtils
                                        .getSimpleMovieStringsFromJson(MainActivity.this, jsonMovieResponse);



                            } catch (Exception e) {
                                e.printStackTrace();
                                return null;
                            }

                    }
                    return simpleJsonMovieData;



                }

                @Override
                public void deliverResult(String[] data) {
                    mMoviesQueryResult=data;
                    super.deliverResult(data);
                }
            };

        }

        @Override
        public void onLoadFinished(Loader<String[]> loader, String[] data) {

            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (data != null && !data.equals("")) {
                showMovieDataView();
                mMovieDBAdapterAdapter.setmMovieData(data);
            } else {
                showErrorMessage();
            }
        }

        @Override
        public void onLoaderReset(Loader<String[]> loader) {

        }

        public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(String... params) {


            if (params.length == 0) {
                return null;
            }

            String sType = params[0];
            URL movieRequestUrl = NetworkUtils.buildUrl(sType);

            try {
                String jsonMovieResponse = NetworkUtils
                        .getResponseFromHttpUrl(movieRequestUrl);

                //Log.v(TAG, "jsonMovieResponse= " + jsonMovieResponse);

                String[] simpleJsonMovieData = MovieDBJsonUtils
                        .getSimpleMovieStringsFromJson(MainActivity.this, jsonMovieResponse);

                return simpleJsonMovieData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] movieData) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (movieData != null) {
                showMovieDataView();
                mMovieDBAdapterAdapter.setmMovieData(movieData);
            } else {
                showErrorMessage();
            }
        }
    }
    /**
     * This method will make the View for the weather data visible and
     * hide the error message.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showMovieDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the error message visible and hide the weather
     * View.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(String thatMovie) {
        Intent intent= new Intent(this,DetailActivity.class);
        intent.putExtra("thatMovie", thatMovie);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater =getMenuInflater();
        menuInflater.inflate(R.menu.movies_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id==R.id.action_setting)
        {
            Intent settingsIntent = new Intent(this,SettingsActivity.class);
            startActivity(settingsIntent);
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String sKey) {

        if (sKey.equals(getString(R.string.pref_sorting_key)))
        {
            String sSortingBy  = sharedPreferences.getString(sKey,getString(R.string.pref_sorting_by_popularity_value));
            Log.d("pref",sSortingBy);
            loadMovieData(sSortingBy);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister VisualizerActivity as an OnPreferenceChangedListener to avoid any memory leaks.
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
