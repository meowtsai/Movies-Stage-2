package com.meowroll.movies;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



/**
 * Created by Sophie on 2017/3/28.
 */

public class MovieReviewsAdapter extends RecyclerView.Adapter<MovieReviewsAdapter.MovieReviewsViewHolder> {

    private int mItemCount;

    private JSONArray mReviewData;

    public MovieReviewsAdapter()
    {
        //mItemCount=iCount;
    }


    public void setReviewData(JSONArray reviewData) {
        mReviewData = reviewData;
        notifyDataSetChanged();
    }

    @Override
    public MovieReviewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context =parent.getContext();
        int reviewitemlayout = R.layout.reviews_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachImmediately =false;
        View view = inflater.inflate(reviewitemlayout,parent,shouldAttachImmediately);
        MovieReviewsViewHolder viewHolder = new MovieReviewsViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MovieReviewsViewHolder holder, int position)  {

        try {
            JSONObject theReview = mReviewData.getJSONObject(position);

            String author;
            String content;
            String url;
            String id;

            author = theReview.getString("author");
            content = theReview.getString("content");
            id = theReview.getString("id");
            url = theReview.getString("url");


            holder.mTextViewReviewAutor.setText(author);
            holder.mTextViewReviewContent.setText(content);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if (null == mReviewData) return 0;
        return mReviewData.length();
    }

    class MovieReviewsViewHolder extends RecyclerView.ViewHolder
    {
        TextView mTextViewReviewAutor;
        TextView mTextViewReviewContent;

        public MovieReviewsViewHolder(View itemView) {
            super(itemView);
            mTextViewReviewAutor = (TextView) itemView.findViewById(R.id.textview_reviewed_by);
            mTextViewReviewContent = (TextView) itemView.findViewById(R.id.textview_review_content);

        }


    }
}
