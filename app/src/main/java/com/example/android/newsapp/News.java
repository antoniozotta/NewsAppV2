package com.example.android.newsapp;

public class News {
    /**
     * News Thumbnail
     */
    private String mThumbnail;
    /**
     * News Title
     */
    private String mTitle;
    /**
     * News Section
     */
    private String mSectionName;
    /**
     * News Author
     */
    private String mAuthor;
    /**
     * News report date
     */
    private String mDate;
    /**
     * News URL
     */
    private String mUrl;

    public News(String thumbnail, String title, String sectionName, String author, String date, String url) {
        mThumbnail = thumbnail;
        mTitle = title;
        mSectionName = sectionName;
        mAuthor = author;
        mDate = date;
        mUrl = url;
    }


    public String getThumbnail() {
        return mThumbnail;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getSectionName() {
        return mSectionName;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getDate() {
        return mDate;
    }

    public String getUrl() {
        return mUrl;
    }
}