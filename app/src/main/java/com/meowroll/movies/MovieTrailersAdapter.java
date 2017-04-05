package com.meowroll.movies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Sophie on 2017/3/27.
 */

public class MovieTrailersAdapter extends RecyclerView.Adapter<MovieTrailersAdapter.MovieTrailersAdapterViewHolder> {

    private int mItemsCount;
    //private String[] mTrailersData;
    private JSONArray mTrailersData;
    private final MovieTrailersAdapterOnClickHandler mClickHandler;

    public MovieTrailersAdapter(MovieTrailersAdapterOnClickHandler clickHandler)
    {
        mClickHandler=clickHandler;
        //this.mItemsCount= itemsCount;
    }

    public interface MovieTrailersAdapterOnClickHandler {
        void onClick(String aTrailerKey);
    }

    public void setData(JSONArray trailersData) {
        mTrailersData = trailersData;
        notifyDataSetChanged();
    }

    @Override
    public MovieTrailersAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context mContext = parent.getContext();
        int layoutIdForListItem =R.layout.trailers_list_item;
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        boolean shouldAttachToParentImmediately=false;
        View view= layoutInflater.inflate(layoutIdForListItem,parent,shouldAttachToParentImmediately);
        MovieTrailersAdapterViewHolder trailersAdapterViewHolder =new MovieTrailersAdapterViewHolder(view);
        return trailersAdapterViewHolder;
    }

    @Override
    public void onBindViewHolder(MovieTrailersAdapterViewHolder holder, int position) {

        try {
            JSONObject eachMovie = mTrailersData.getJSONObject(position);

            String key;
            String name;
            String site;
            String id;
            int size;
            String type;

            id = eachMovie.getString("id");
            key = eachMovie.getString("key");
            name = eachMovie.getString("name");
            site = eachMovie.getString("site");
            size = eachMovie.getInt("size");
            type = eachMovie.getString("type");

            holder.mTrailersTitleTextView.setText(name);

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        //String aTrailer = mTrailersData[position];

        //String splitLetter= "%";
        //String[] tmpArray = aTrailer.split(splitLetter);

        //holder.mTrailersTitleTextView.setText(tmpArray[2]);
        //holder.mMovieTextView.setText(tmpArray[0]);
        //String filePath = "https://image.tmdb.org/t/p/w500"+tmpArray[2].trim();

        //Picasso.with(mContext).load(filePath).into(holder.mMovieImageView);
    }

    @Override
    public int getItemCount() {
        if (null == mTrailersData) return 0;
        return mTrailersData.length();
    }

    class MovieTrailersAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView mTrailersTitleTextView;

        public MovieTrailersAdapterViewHolder(View itemView)
        {
            super(itemView);
            mTrailersTitleTextView = (TextView) itemView.findViewById(R.id.textview_trailer_title);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Log.v("SEE", adapterPosition +"");
            //String aTrailer = mTrailersData[adapterPosition];
            try {
                JSONObject aTrailer = mTrailersData.getJSONObject(adapterPosition);
                String key = aTrailer.getString("key");
                mClickHandler.onClick(key);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

        }
    }



}
