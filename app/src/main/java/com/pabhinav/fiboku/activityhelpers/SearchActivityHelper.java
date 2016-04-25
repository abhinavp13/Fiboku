package com.pabhinav.fiboku.activityhelpers;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.pabhinav.fiboku.R;
import com.pabhinav.fiboku.adapter.BaseAdapter;
import com.pabhinav.fiboku.adapter.SearchedBookAdapter;
import com.pabhinav.fiboku.booksearch.BookSearchHandler;
import com.pabhinav.fiboku.models.BookSearchFailure;
import com.pabhinav.fiboku.models.DotsState;
import com.pabhinav.fiboku.models.SearchedBook;
import com.pabhinav.fiboku.util.BookUtil;
import com.pabhinav.fiboku.util.FLog;
import com.pabhinav.fiboku.util.NetworkUtil;

import java.util.ArrayList;

import lombok.Setter;

/**
 * @author pabhinav
 */
public class SearchActivityHelper {

    @Setter
    private ItemClickEvent itemClickEvent;
    private Context context;
    private SearchedBookAdapter searchedBookAdapter;
    private BookSearchHandler bookSearchHandler;

    /** Constructor for this helper class **/
    public SearchActivityHelper(Context context){
        this.context = context;
        bookSearchHandler = new BookSearchHandler();
        bookSearchHandler.setBookSearchEvents(new BookSearchListener());
    }

    /**
     * This will set recycler view with fixed size and having linear layout manager.
     * Also, sets the adapter and handle click events.
     *
     * @param recyclerView attached to the calling activity
     */
    public void linkRecyclerViewAndAdapter(RecyclerView recyclerView) {
        searchedBookAdapter = new SearchedBookAdapter(R.layout.search_recycler_view_item, new ArrayList<SearchedBook>(),context, true, R.layout.search_recycler_view_footer_item);
        if(recyclerView == null){
            return;
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(searchedBookAdapter);

        /** Demand for more data **/
        searchedBookAdapter.setMoreDataRequired(new SearchedBookAdapter.MoreDataRequired() {
            @Override
            public void onDataDemanded() {
                if(bookSearchHandler != null){
                    bookSearchHandler.fetchNextBatch();
                }
            }
        });

        /** Click Events **/
        searchedBookAdapter.setRecyclerViewAdapterEvents(new BaseAdapter.RecyclerViewAdapterEvents() {
            @Override
            public void onItemClick(int position, View v) {
                if (itemClickEvent != null) {
                    ImageView transitionImageView = (ImageView)v.findViewById(R.id.search_result_book_image_view);
                    transitionImageView.setTransitionName(context.getString(R.string.common_image_transition_view));
                    itemClickEvent.onItemClicked(transitionImageView, searchedBookAdapter.getItemAtPosition(position));
                }
            }
        });

        /** Update Footer **/
        searchedBookAdapter.updateFooter(View.GONE, View.GONE, View.VISIBLE, DotsState.STOP, "");
    }

    /** Hide retry button, retry text, and show still dots, also update adapter; whenever search query is empty **/
    public void handleEmptyQueryEvent(){

        /** Change the visibility of retry View group **/
        searchedBookAdapter.updateFooter(View.GONE, View.GONE, View.VISIBLE, DotsState.STOP, "");

        /** Delete all items from adapter **/
        if(searchedBookAdapter != null) {
            searchedBookAdapter.deleteAllItems();
        }
    }

    /** There is some query made by user, hide retry button, retry text, and show dots animating **/
    public void handleNewQueryEvent(String query){

        /** Change retry view group visibility **/
        searchedBookAdapter.updateFooter(View.GONE, View.GONE, View.VISIBLE, DotsState.START, "");

        /** Delete all items from adapter **/
        if(searchedBookAdapter != null) {
            searchedBookAdapter.deleteAllItems();
        }

        /** Initiate book search **/
        if(bookSearchHandler != null) {
            bookSearchHandler.initiateQuery(query);
        }
    }

    /** Some failure occurred while searching books online, show retry button, retry text, and hide dots **/
    public void handleSearchQueryFailedEvent(String failureMessage){

        /** Change retry view group visibility **/
        searchedBookAdapter.updateFooter(View.VISIBLE, View.VISIBLE, View.GONE, DotsState.STOP, failureMessage);

    }

    /** If user demands retrying, retry for next batch **/
    public void retrySearching(){

        /** Change visibility of retry view group **/
        searchedBookAdapter.updateFooter(View.GONE, View.GONE, View.VISIBLE, DotsState.START, "");

        /** Ask handler to retry **/
        if(bookSearchHandler != null) {
            bookSearchHandler.retryFetchBatch();
        }
    }

    /** Class for book searching and handing its events **/
    class BookSearchListener implements BookSearchHandler.BookSearchEvents {

        @Override
        public void onBookSearchFailed(BookSearchFailure bookSearchFailure) {

            if(context != null) {
                String message = "";

                /** Check for internet connection **/
                if(!NetworkUtil.isNetworkConnected(context)) {
                    message = context.getString(R.string.seems_like_no_internet_connection);
                }

                /** ask failure handler to take from here **/
                handleSearchQueryFailedEvent(message);
            }
        }

        @Override
        public void onBookSearchCompleted(boolean searchHasCompleted, ArrayList<SearchedBook> searchedBookArrayList) {

            /** Got the data, notify adapter **/
            if(searchedBookArrayList != null && searchedBookArrayList.size() > 0){
                searchedBookAdapter.addMultipleItems(BookUtil.sterilizeSearchedBooks(searchedBookArrayList));
            }

            /** if search has completed, remove dot, retry text, retry button **/
            if(searchHasCompleted && context != null){

                /** If received output has zero size and adapter also has no data (except footer), this means : 'nothing found' **/
                if((searchedBookArrayList == null || searchedBookArrayList.size() == 0) && searchedBookAdapter.getItemCount() == 1){
                    searchedBookAdapter.updateFooter(View.GONE, View.GONE, View.VISIBLE, DotsState.STOP, context.getResources().getString(R.string.nothing_found_for_the_given_keyword));
                } else {
                    searchedBookAdapter.updateFooter(View.GONE, View.GONE, View.VISIBLE, DotsState.STOP, "");
                }
            }
        }
    }

    /** Callback for item click event **/
    public interface ItemClickEvent{
        void onItemClicked(ImageView transitionImageView, SearchedBook searchedBook);
    }
}
