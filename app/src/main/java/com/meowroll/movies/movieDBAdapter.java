package com.meowroll.movies;

import android.app.Application;
import android.content.Context;
import android.nfc.Tag;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.TextView;

import com.meowroll.movies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by Meow on 2017/1/3.
 */

//public class movieDBAdapter {
public class movieDBAdapter extends RecyclerView.Adapter<movieDBAdapter.movieDBAdapterViewHolder> {
    private String[] mMovieData;
    private Context mContext;

    private static final String TAG = movieDBAdapter.class.getSimpleName();

    private final movieDBAdapterOnClickHandler mClickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface movieDBAdapterOnClickHandler {
        void onClick(String thatMovie);
    }



    public movieDBAdapter(movieDBAdapterOnClickHandler clickHandler ) {
        mClickHandler = clickHandler;
    }


    @Override
    public int getItemCount() {
        if (null == mMovieData) return 0;
        return mMovieData.length;
    }

    /**
     * This method is used to set the weather forecast on a ForecastAdapter if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new ForecastAdapter to display it.
     *
     * @param movieData The new weather data to be displayed.
     */
    public void setmMovieData(String[] movieData) {
        mMovieData = movieData;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(movieDBAdapterViewHolder holder, int position) {
        String thisMovie = mMovieData[position];

        String splitLetter= "#";
        String[] tmpArray = thisMovie.split(splitLetter);
        //holder.mMovieTextView.setText(tmpArray[0]);

        String filePath = "https://image.tmdb.org/t/p/w500" + tmpArray[2].trim();
        if (tmpArray[2].trim().indexOf(tmpArray[6].trim())>0)
        {
            //Log.d("pref","onBindViewHolder" + tmpArray[2].trim());
             filePath =  tmpArray[2].trim();
            File imgFile = new  File(filePath);
            Picasso.with(mContext).load(imgFile).into(holder.mMovieImageView);

        }
        else {
             filePath = "https://image.tmdb.org/t/p/w500" + tmpArray[2].trim();
            Picasso.with(mContext).load(filePath).into(holder.mMovieImageView);
        }

        //Log.v(TAG, "filePath= " + filePath);
        //Picasso.with().load(filePath).into(mMovieImageView);
       // holder.mMovieImageView.setI


        //holder. .setText(tmpArray[0]);
    }

    @Override
    public movieDBAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        mContext = viewGroup.getContext();
        int layoutIdForListItem = R.layout.moviedb_list_item ;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new movieDBAdapterViewHolder(view);

    }

    public class movieDBAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //public final TextView mMovieTextView;
        public final ImageView mMovieImageView;

        public movieDBAdapterViewHolder(View view) {
            super(view);
            //mMovieTextView = (TextView) view.findViewById(R.id.tv_movie_data);
            mMovieImageView = (ImageView) view.findViewById(R.id.iv_poster );
            view.setOnClickListener(this);
        }

        /**
         * This gets called by the child views during a click.
         *
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            String thatMovie = mMovieData[adapterPosition];
            mClickHandler.onClick(thatMovie);
        }
    }

}
