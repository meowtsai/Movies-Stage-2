package com.meowroll.movies;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.support.v4.app.LoaderManager;
import android.widget.Toast;

import com.meowroll.movies.utilities.MovieDBJsonUtils;
import com.meowroll.movies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;


import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import static com.meowroll.movies.R.string.error_request_permission_storage;
import static com.meowroll.movies.data.MovieContract.MovieEntry;

public class DetailActivity extends AppCompatActivity
        implements MovieTrailersAdapter.MovieTrailersAdapterOnClickHandler,
         LoaderManager.LoaderCallbacks<JSONArray>
{

    //<!--title, release date, movie poster, vote average, and plot synopsis.-->
    private TextView tvTitle ;
    private TextView tvReleaseDate ;
    private TextView tvVoteAverate ;
    private TextView tvPlot ;
    private ImageView ivPoster;
    private String mMovie;
    private TextView tv_original_title;
    private ProgressBar mLoadingIndicator;

    private CheckBox check_favorite;

    private RecyclerView mRecyclerView;
    private MovieTrailersAdapter mMovieTrailersAdapter;

    private RecyclerView mReviewsRecyclerView;
    private MovieReviewsAdapter mReviewsAdapter;


    private TextView mErrorMessageDisplay;

    private String mMovieId;
    private URL mFilePath;
    private String mFileLocalPath;
    private static final int LOADERID_TRAILERS_QUERY=129;
    private static final int LOADERID_GETREVIEWS =209;
    private static String QUERY_MOVIEID_EXTRA="MOVIEID";

    private static final int REQUEST_EXTERNAL_STORAGE = 929;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    private static final String YOUTUBE_URL =
            "https://www.youtube.com/watch/";

    private String mFirstTrailerUrl=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ActionBar actionBar = this.getSupportActionBar();

        // Set the action bar back button to look like an up button
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        tvTitle = (TextView) findViewById(R.id.tv_title);
        tv_original_title= (TextView) findViewById(R.id.tv_original_title);
        tvReleaseDate = (TextView) findViewById(R.id.tv_release_date);
        tvVoteAverate = (TextView) findViewById(R.id.tv_vote_average );
        tvPlot = (TextView) findViewById(R.id.tv_plot_synopsis);
        ivPoster =(ImageView)findViewById(R.id.iv_poster);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_trailers );

        mReviewsRecyclerView= (RecyclerView)findViewById(R.id.rv_reviews);

        check_favorite =(CheckBox) findViewById(R.id.check_favorite);



        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra("thatMovie")) {
                //parsedMovieData[i] = title + " - " + original_title + " - " + poster_path + " - " + vote_average + " - " + release_date+ " - " + overview;
                mMovie = intentThatStartedThisActivity.getStringExtra("thatMovie");


                String[] tmpArray = mMovie.split("#");
                tvTitle.setText(tmpArray[0]);
                tv_original_title.setText(tmpArray[1]);
                tvReleaseDate.setText("Release Date: " + tmpArray[4]);
                tvVoteAverate.setText("Vote Average: " + tmpArray[3]);
                tvPlot.setText(tmpArray[5]);
                mMovieId= tmpArray[6];
                //mFilePath = "https://image.tmdb.org/t/p/w500"+tmpArray[2].trim();

                if (tmpArray[2].trim().indexOf(mMovieId)>0)
                {
                    //Log.d("pref","onBindViewHolder" + tmpArray[2].trim());

                    File imgFile = new  File(tmpArray[2].trim());
                    Picasso.with(this).load(imgFile).into(ivPoster);

                }
                else {
                    mFilePath= NetworkUtils.buildUrlForPoster(tmpArray[2].trim());
                    Picasso.with(this).load(mFilePath.toString()).into(ivPoster);
                }



                mLoadingIndicator =(ProgressBar)findViewById(R.id.pb_loading_indicator);
                mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

                mRecyclerView= (RecyclerView) findViewById(R.id.rv_trailers);
                LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                mRecyclerView.setLayoutManager(layoutManager);
                mRecyclerView.setHasFixedSize(true);

                mMovieTrailersAdapter = new MovieTrailersAdapter(this);
                mRecyclerView.setAdapter(mMovieTrailersAdapter);
                loadTrailersData(mMovieId);


                mReviewsAdapter = new MovieReviewsAdapter();

                mReviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                mReviewsRecyclerView.setHasFixedSize(true);
                mReviewsRecyclerView.setAdapter(mReviewsAdapter);

                loadReviews(mMovieId);

                showFavorite();


            }
        }
    }

    @Override
    public void onClick(String aTrailerKey) {
        Log.v("onClick", " aTrailerKey " + aTrailerKey);
        Uri uriUrl = Uri.parse(YOUTUBE_URL).buildUpon()
                .appendQueryParameter("v", aTrailerKey)
                .build();
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }

    private void loadTrailersData(String MovieID) {
        //String location = SunshinePreferences.getPreferredWeatherLocation(this);
        //new FetchWeatherTask().execute(sRankBy);
        Bundle queryBundle = new Bundle();
        queryBundle.putString(QUERY_MOVIEID_EXTRA,MovieID);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String[]> trailersSearchLoader =loaderManager.getLoader(LOADERID_TRAILERS_QUERY);

        if (trailersSearchLoader==null)
        {
            loaderManager.initLoader(LOADERID_TRAILERS_QUERY,queryBundle,this);

        }
        else
        {
            loaderManager.restartLoader(LOADERID_TRAILERS_QUERY,queryBundle,this);
        }
    }

    private void loadReviews(String MovieID)
    {
        Bundle reviewBundle = new Bundle();
        reviewBundle.putString(QUERY_MOVIEID_EXTRA,MovieID);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<JSONArray> reviewQueryLoader =loaderManager.getLoader(LOADERID_GETREVIEWS);
        if (reviewQueryLoader == null)
        {
            loaderManager.initLoader(LOADERID_GETREVIEWS,reviewBundle,this);
        }
        else
        {
            loaderManager.restartLoader(LOADERID_GETREVIEWS,reviewBundle,this);
        }
    }
    private void showFavorite()
    {
        Uri queryUri = MovieEntry.CONTENT_URI.buildUpon().appendPath(mMovieId).build();
        Cursor cursor= getContentResolver().query(queryUri,null,null,null,null);

        if (cursor.getCount()>0)
        {
            check_favorite.setChecked(true);
        }

    }

    @Override
    public Loader<JSONArray> onCreateLoader(final int id, final Bundle args) {
        return new AsyncTaskLoader<JSONArray>(this) {
            //String[] mTrailersQueryResult;
            JSONArray reviewJsonArray;
            JSONArray trailersJsonArray;

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if (args==null)
                {
                    return;
                }
                mLoadingIndicator.setVisibility(View.VISIBLE);

                switch (id)
                {
                    case LOADERID_GETREVIEWS:
                        if (reviewJsonArray!=null)
                        {
                            mLoadingIndicator.setVisibility(View.INVISIBLE);
                            deliverResult(reviewJsonArray);
                        }
                        else {
                            forceLoad();
                        }
                        break;
                    case LOADERID_TRAILERS_QUERY:
                        if (trailersJsonArray!=null)
                        {
                            mLoadingIndicator.setVisibility(View.INVISIBLE);
                            deliverResult(trailersJsonArray);
                        }
                        else {
                            forceLoad();
                        }
                        break;
                }

            }

            @Override
            public JSONArray loadInBackground() {
                String sMovieID = args.getString(QUERY_MOVIEID_EXTRA);
                if (sMovieID==null || TextUtils.isEmpty(sMovieID)) {
                    return null;
                }

                URL tmpURL=NetworkUtils.buildUrlForTrailers(sMovieID);
                switch (id)
                {
                    case LOADERID_GETREVIEWS:
                        tmpURL=NetworkUtils.buildUrlForReviews(sMovieID);
                        break;
                    case LOADERID_TRAILERS_QUERY:
                        tmpURL=NetworkUtils.buildUrlForTrailers(sMovieID);
                        break;
                }


                try {
                    String jsonResponse = NetworkUtils
                            .getResponseFromHttpUrl(tmpURL);
                      JSONArray aJsonArray = MovieDBJsonUtils.getJSONArray(DetailActivity.this, jsonResponse);

                    return aJsonArray;

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }


            }

            @Override
            public void deliverResult(JSONArray data) {
                switch (id)
                {
                    case LOADERID_GETREVIEWS:
                        reviewJsonArray=data;
                        break;
                    case LOADERID_TRAILERS_QUERY:
                        trailersJsonArray=data;
                        break;
                }
                super.deliverResult(data);
            }
        };
    }



    @Override
    public void onLoadFinished(Loader<JSONArray> loader, JSONArray data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        if (data != null && !data.equals("")) {
            switch (loader.getId())
            {
                case LOADERID_GETREVIEWS:
                    showReviewDataView();
                    mReviewsAdapter.setReviewData(data);
                    break;
                case LOADERID_TRAILERS_QUERY:
                    showTrailersDataView();
                    mMovieTrailersAdapter.setData(data);

                    try {
                        String tmpKey =MovieDBJsonUtils.getFirstTrailerUrl(data);

                        if (tmpKey !=null && !TextUtils.isEmpty(tmpKey)) {
                            mFirstTrailerUrl = YOUTUBE_URL + MovieDBJsonUtils.getFirstTrailerUrl(data);

                        }
                        else
                        {
                            mFirstTrailerUrl =null;
                        }
                    }
                    catch (JSONException e)
                    {

                        e.printStackTrace();
                    }
                    break;
            }

        } else {
            showErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(Loader<JSONArray> loader) {

    }


    private void showTrailersDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }
    private void showReviewDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        mReviewsRecyclerView.setVisibility(View.VISIBLE);
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

    public void addToFavorite(View view)
    {
      boolean isChecked= check_favorite.isChecked();
        if (isChecked)
        {

            int requestCode =ActivityCompat.checkSelfPermission(DetailActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (PackageManager.PERMISSION_GRANTED!=  requestCode) {
                verifyStoragePermissions(this);
            }
            else
            {
                networkAsyncTask task = new networkAsyncTask();
                task.execute();

            }



        }
        else
        {
            Uri uriToDelete = MovieEntry.CONTENT_URI.buildUpon().appendPath(mMovieId).build();
            int rowsDeleted= getContentResolver().delete(uriToDelete,null,null);
            //insert
            //Toast.makeText(this,"Delete  from db:" + rowsDeleted ,Toast.LENGTH_LONG).show();
        }

    }



    private class networkAsyncTask extends AsyncTask<Void, Void, Bitmap>
    {
        @Override
        protected Bitmap doInBackground(Void... voids) {
            Bitmap bitmap =null;
            if (mFilePath==null)
            {
                return bitmap;
            }
            try
            {
                //Log.d("error",mFilePath.toString());
                bitmap =NetworkUtils.getImageStreamFromHttpUrl(mFilePath);

            }
            catch (IOException e) {
                return null;
            }

            return bitmap;

        }

        @Override
        protected void onPostExecute(Bitmap s) {
            super.onPostExecute(s);
            //mData=s;
            // mTextViewResult.setText(mData);
            //Picasso.with(getApplicationContext()).load(filePath).into(ivPoster);
            try {
                //File myFile = new File("/sdcard/moviedb/" +mMovieId + ".jpg");
                File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES); //Creates app specific folder
                path.mkdirs();
                File imageFile = new File(path, mMovieId + ".jpg"); // Imagename.png
                mFileLocalPath=imageFile.getAbsolutePath().toString();

                if (!imageFile.exists()) {
                    FileOutputStream myOutWriter = new FileOutputStream(imageFile);
                    s.compress(Bitmap.CompressFormat.PNG, 100, myOutWriter); // Compress Image

                    myOutWriter.flush();
                    myOutWriter.close();
                }
                ContentValues cv= new ContentValues();
                cv.put(MovieEntry.COLUMN_ID,mMovieId );
                cv.put(MovieEntry.COLUMN_TITLE, tvTitle.getText().toString());
                cv.put(MovieEntry.COLUMN_RELEASEDATE, tvReleaseDate.getText().toString());

                cv.put(MovieEntry.COLUMN_AVGVOTE, tvVoteAverate.getText().toString());
                cv.put(MovieEntry.COLUMN_PLOT, tvPlot.getText().toString());
                cv.put(MovieEntry.COLUMN_POSTER, mFileLocalPath);
                Uri rowsAddedUri=getContentResolver().insert(MovieEntry.CONTENT_URI,cv);
                Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.add_completed), Toast.LENGTH_SHORT).show();

            }
            catch (Exception e)
            {
                Log.d("error",e.getMessage());
                //Toast.makeText(this,"error_request_permission_storage",Toast.LENGTH_LONG).show();
                //mTextViewResult.setText(e.getMessage());

            }


        }
    }


    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        Log.d("error",String.valueOf(permission));
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    AsyncTask task = new networkAsyncTask();
                    task.execute("");

                } else {

                    Toast.makeText(this,error_request_permission_storage,Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private Intent createShareTrailerIntent() {
        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(tvTitle.getText().toString() + "  trailer: " + mFirstTrailerUrl)
                .getIntent();
        return shareIntent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        getMenuInflater().inflate(R.menu.detail_menu, menu);
        //MenuItem menuItem = menu.findItem(R.id.action_share);
        //menuItem.setIntent(createShareTrailerIntent());
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_setting) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }
        if (id == R.id.action_share) {

            if (mFirstTrailerUrl!=null) {
                Intent shareIntent = createShareTrailerIntent();
                startActivity(shareIntent);
                return true;
            }
            else
                return false;
        }
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }
}
