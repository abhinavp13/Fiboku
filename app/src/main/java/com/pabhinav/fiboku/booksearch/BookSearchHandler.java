package com.pabhinav.fiboku.booksearch;

import com.pabhinav.fiboku.models.BookSearchFailure;
import com.pabhinav.fiboku.models.SearchedBook;

import java.util.ArrayList;

import lombok.Setter;

/**
 * @author pabhinav
 */
public class BookSearchHandler {

    private BookSearchRequestHandler bookSearchRequestHandler;
    private BookSearchResponseHandler bookSearchResponseHandler;
    private BookSearchRequestListener bookSearchRequestListener;
    private BookSearchResponseListener bookSearchResponseListener;
    private String query;
    private boolean searchHasCompleted;

    @Setter
    private BookSearchEvents bookSearchEvents;

    /** Initialize objects in constructor **/
    public BookSearchHandler(){
        searchHasCompleted = false;
        init();
    }

    private void init(){
        bookSearchRequestHandler = new BookSearchRequestHandler();
        bookSearchRequestListener = new BookSearchRequestListener();
        bookSearchRequestHandler.setBookSearchRequestEvents(bookSearchRequestListener);
        bookSearchResponseHandler = new BookSearchResponseHandler();
        bookSearchResponseListener = new BookSearchResponseListener();
        bookSearchResponseHandler.setBookSearchResponseEvents(bookSearchResponseListener);
    }

    /** Initiate request-response handling for the given search query **/
    public void initiateQuery(String query){

        /** Clear previous objects and initialize again **/
        clean();
        init();

        /** save the query for further use **/
        this.query = query;
        searchHasCompleted = false;

        /** Start the request **/
        bookSearchRequestHandler.fetchBookData(query);
    }

    /** Tries to fetch more data for same request query term **/
    public void fetchNextBatch(){

        /** If there are still pages left to be fetched **/
        if(!searchHasCompleted) {

            /** Need to increase start index of request handler for fetching next set of data **/
            bookSearchRequestHandler.setStartIndex(bookSearchRequestHandler.getStartIndex() + bookSearchRequestHandler.getMaxResults());

            /** Start the request **/
            bookSearchRequestHandler.fetchBookData(query);
        }
    }

    /** Retries to fetch more data for same request query term **/
    public void retryFetchBatch(){

        /** Need to retry fetching the batch, which previously might have failed **/
        bookSearchRequestHandler.fetchBookData(query);
    }


    /** Listener for book search response events **/
    private class BookSearchResponseListener implements BookSearchResponseHandler.BookSearchResponseEvents {

        @Override
        public void onBookSearchResponseSuccessfullyParsed(ArrayList<SearchedBook> searchedBookArrayList) {

            /** If there is an empty array list, this means nothing found **/
            if(searchedBookArrayList == null || searchedBookArrayList.size() == 0){
                searchHasCompleted = true;

                /** Notify that search has completed with no data **/
                if(bookSearchEvents != null){
                    bookSearchEvents.onBookSearchCompleted(true, new ArrayList<SearchedBook>());
                }
            }
            else { /** Data is present **/

                /** Return the whole list **/
                if(bookSearchEvents != null){
                    bookSearchEvents.onBookSearchCompleted(false, searchedBookArrayList);
                }
            }
        }

        @Override
        public void onBookSearchResponseParsedFailed() {

            /** Failed to parse **/
            if(bookSearchEvents != null){
                bookSearchEvents.onBookSearchFailed(BookSearchFailure.RESPONSE_FAILURE);
            }
        }

        @Override
        public void onBookSearchResponseItemsBodyEmpty() {
            searchHasCompleted = true;

            /** Notify that search has completed with no data **/
            if(bookSearchEvents != null){
                bookSearchEvents.onBookSearchCompleted(true, new ArrayList<SearchedBook>());
            }
        }
    }

    /** Listener for book search request events **/
    private class BookSearchRequestListener implements BookSearchRequestHandler.BookSearchRequestEvents {

        @Override
        public void onBookSearchRequestCompleted(String data) {
            bookSearchResponseHandler.handleBookSearchResponse(data);
        }

        @Override
        public void onBookSearchRequestFailed() {
            if(bookSearchEvents != null){
                bookSearchEvents.onBookSearchFailed(BookSearchFailure.REQUEST_FAILURE);
            }
        }
    }

    public interface BookSearchEvents{
        void onBookSearchFailed(BookSearchFailure bookSearchFailure);
        void onBookSearchCompleted(boolean searchHasCompleted, ArrayList<SearchedBook> searchedBookArrayList);
    }

    private void clean(){
        bookSearchRequestHandler = null;
        bookSearchRequestListener = null;
        bookSearchResponseHandler = null;
        bookSearchResponseListener = null;
    }
}
