package com.example.android.newsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class NewsAdapter extends ArrayAdapter<News> {

    public NewsAdapter(Context context, List<News> news) {
        super(context, 0, news);
    }

    /**
     * Returns a list item view that displays news articles
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.news_list_item, parent, false);
        }

        // Find the news at the given position in the list of news
        News currentNews = getItem(position);

        // Check if Thumbnail is available and set using Picasso library
        ImageView thumbnailView = listItemView.findViewById(R.id.thumbnail);
        if (thumbnailView != null) {
            Picasso.get()
                    .load(currentNews.getThumbnail())
                    .into(thumbnailView);
        } else {
            thumbnailView.setVisibility(View.GONE);
        }

        // Find the TextView with view ID title
        TextView titleView = listItemView.findViewById(R.id.title);
        titleView.setText(currentNews.getTitle());

        // Find the TextView with view ID section_name
        TextView sectionView = (TextView) listItemView.findViewById(R.id.section_name);
        // Display the section name of the current News in that TextView
        sectionView.setText(currentNews.getSectionName());

        // Find the TextView with view ID author
        TextView authorView = (TextView) listItemView.findViewById(R.id.author);
        if (currentNews.getAuthor() != null) {
            // Display the author of the current News in that TextView
            authorView.setText(currentNews.getAuthor());
        } else {
            authorView.setVisibility(View.INVISIBLE);
        }

        // Find the TextView with view ID date
        TextView dateView = listItemView.findViewById(R.id.date);
        // Display the date of the current News in that TextView
        dateView.setText(currentNews.getDate());

        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }
}