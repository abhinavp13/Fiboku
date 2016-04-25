package com.pabhinav.fiboku.booksearch;

import android.os.AsyncTask;

import com.pabhinav.fiboku.BuildConfig;
import com.pabhinav.fiboku.R;
import com.pabhinav.fiboku.util.FLog;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import lombok.Cleanup;
import lombok.Setter;

/**
 * @author pabhinav
 */
public class AsyncBookDataFetch extends AsyncTask<String, Void, String>{

    /** Time limit **/
    private static final int CONNECTION_TIMEOUT = 10000;

    @Setter
    private TaskCompleteEvent taskCompleteEvent;

    /** Callback for Async Task Completion **/
    public interface TaskCompleteEvent{
        void onTaskComplete(String result);
        void onTaskFailed();
    }

    @Override
    protected String doInBackground(String... params) {

        /** URL must not be null or empty **/
        if (params == null || params.length == 0){

            /** empty url, handle gracefully **/
            return "";
        }

        /** Only Take first url **/
        String url = params[0];

        try {
            /** Try make a connection and return response **/
            return makeConnectionAndFetchResult(url);

        } catch (Exception e){

            /** Could not fetch data, handle gracefully **/
            return "";
        }
    }

    @Override
    protected void onPostExecute(String result){

        /** Nothing returned, task failed **/
        if(result.equals("")){
            if(taskCompleteEvent != null){
                taskCompleteEvent.onTaskFailed();
            }
            return;
        }

        /** Call back for task completion event **/
        if(taskCompleteEvent != null){
            taskCompleteEvent.onTaskComplete(result);
        }
    }

    /** Handles connection and fetches response **/
    private String makeConnectionAndFetchResult(String url) throws Exception{

        /** Clean up ensures cleaning up of resources **/
        @Cleanup("disconnect")HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
        urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
        urlConnection.setReadTimeout(CONNECTION_TIMEOUT);

        /** Need to set api key **/
        urlConnection.setRequestProperty("key", BuildConfig.GOOGLE_BOOK_KEY);

        /** Check for a successful connection **/
        if(urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK){

            /** Connection failed, handle gracefully **/
            return "";
        }

        /** Convert read stream to string and return it **/
        @Cleanup InputStream inputStream = urlConnection.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        String inputStr;
        StringBuilder responseStrBuilder = new StringBuilder();
        while ((inputStr = bufferedReader.readLine()) != null) {
            responseStrBuilder.append(inputStr);
        }
        return responseStrBuilder.toString();
    }
}
