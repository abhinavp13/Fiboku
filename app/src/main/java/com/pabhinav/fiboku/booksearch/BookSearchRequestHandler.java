package com.pabhinav.fiboku.booksearch;

import android.support.annotation.NonNull;

import com.pabhinav.fiboku.util.FLog;

import lombok.Getter;
import lombok.Setter;

/**
 * @author pabhinav
 */
public class BookSearchRequestHandler {

    @Setter @Getter
    private int startIndex;

    @Getter
    private int maxResults;

    @Setter
    private BookSearchRequestEvents bookSearchRequestEvents;

    public BookSearchRequestHandler(){
        this.startIndex = 0;
        this.maxResults = 10;
    }

    /** Only this method can be called from an outer class,
     * it handles all fetch related preparations and executions. **/
    public void fetchBookData(@NonNull String query){

        String url = getRequestUrl(query);

        if (url.equals("")) {

            /** Call back for book search failed event **/
            if(bookSearchRequestEvents != null){
                bookSearchRequestEvents.onBookSearchRequestFailed();
            }
            return;
        }

        /** logging complete url **/
        FLog.i(this, url);

        /** async call for fetching google book api data **/
        AsyncBookDataFetch asyncBookDataFetch = new AsyncBookDataFetch();
        asyncBookDataFetch.setTaskCompleteEvent(new AsyncBookDataFetch.TaskCompleteEvent() {

            @Override
            public void onTaskComplete(String result) {

                /** Call back for book search complete event **/
                if(bookSearchRequestEvents != null){
                    bookSearchRequestEvents.onBookSearchRequestCompleted(result);
                }
            }

            @Override
            public void onTaskFailed(){

                /** Call back for book search failed event **/
                if(bookSearchRequestEvents != null){
                    bookSearchRequestEvents.onBookSearchRequestFailed();
                }
            }

        });
        asyncBookDataFetch.execute(url);
    }

    /** Tries to build google book api search url, given a query string. **/
    private String getRequestUrl(@NonNull String query){

        BookSearchUrl bookSearchUrl;
        try {
            bookSearchUrl = new BookSearchUrl.BookSearchUrlBuilder()
                    .addSearchQuery(query)
                    .addStartIndex(startIndex)
                    .addMaxResults(maxResults)
                    .build();
        } catch (Exception e){
            return "";
        }
        return bookSearchUrl.getGoogleBookSearchApiUrl();
    }

    /** Callback for every events that can happen while requesting for a url **/
    public interface BookSearchRequestEvents{
        void onBookSearchRequestCompleted(String data);
        void onBookSearchRequestFailed();
    }
}
