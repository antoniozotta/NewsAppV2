package com.example.android.newsapp;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Helper methods related to requesting and receiving news data from Guardian API.
 */
public final class QueryUtils {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the Guardian API and return a list of {@link News} objects.
     */
    public static List<News> fetchNewsData(String requestUrl) {

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link News}
        List<News> news = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link News}
        return news;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the news JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link News} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<News> extractFeatureFromJson(String newsJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding news to
        List<News> news = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(newsJSON);

            // Extract the JSONArray associated with the key called "response",
            // which represents a list of news.
            JSONObject response = baseJsonResponse.getJSONObject("response");

            //In the variable resultsArray gets the JSONArray results stored
            JSONArray resultsArray = response.getJSONArray("results");

            // For each news in the newsArray, create an {@link News} object
            for (int i = 0; i < resultsArray.length(); i++) {

                // Get a single news at position i within the list of news articles
                JSONObject currentNews = resultsArray.getJSONObject(i);

                // Extract the value for the key called 'thumbnail'
                String thumbnail;
                if (currentNews.has("fields")) {
                    thumbnail = currentNews.getJSONObject("fields").getString("thumbnail");
                } else {
                    thumbnail = "https://via.placeholder.com/500x300/fafafa/?text=No+image+available";
                }

                String title = currentNews.getString("webTitle");

                // Extract the value for the key called "sectionName"
                String sectionName = currentNews.getString("sectionName");

                String author;
                // Extract the value for the key called "webTitle" under "tags"
                JSONArray tagAuthor = currentNews.getJSONArray("tags");
                //Get the tag value at the 0th place of the array
                if (tagAuthor.length() != 0) {
                    JSONObject currentAuthor = tagAuthor.getJSONObject(0);
                    // Get the tag value for the key called 'webTitle' (news author)
                    author = currentAuthor.getString("webTitle");
                } else {
                    author = "N/A";
                }

                // Extract the value for the key called "webPublicationDate"
                String date = currentNews.getString("webPublicationDate");
                // Format time/date to remove seconds and display in local format
                String dateFormatted = "";
                try {
                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
                    Date dateFMT = format.parse(date);
                    DateFormat format2 = new SimpleDateFormat("HH:mm dd MMM yy", Locale.getDefault());
                    dateFormatted = format2.format(dateFMT);
                } catch (ParseException e) {
                    Log.e(LOG_TAG, "Problem extracting date from JSON: ", e);
                }

                // Extract the value for the key called "webUrl"
                String url = currentNews.getString("webUrl");

                // Create a new {@link News} object with the thumbnail, section, title, date,
                // and url from the JSON response.
                News newsArticle = new News(thumbnail, title, sectionName, author, dateFormatted, url);

                // Add the new {@link News} to the list of news.
                news.add(newsArticle);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the Guardian JSON results", e);
        }

        // Return the list of news articles
        return news;
    }
}