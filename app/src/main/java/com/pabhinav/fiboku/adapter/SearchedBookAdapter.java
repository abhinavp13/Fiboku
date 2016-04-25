package com.pabhinav.fiboku.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.pabhinav.fiboku.R;
import com.pabhinav.fiboku.activities.SearchBookActivity;
import com.pabhinav.fiboku.models.DotsState;
import com.pabhinav.fiboku.models.SearchedBook;
import com.pabhinav.fiboku.util.NetworkUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import lombok.Setter;
import pl.tajchert.sample.DotsTextView;

/**
 * @author pabhinav
 */
public class SearchedBookAdapter extends BaseAdapter<SearchedBook> {

    @Setter
    private MoreDataRequired moreDataRequired;
    private int retryButtonVisibility;
    private int retryTextViewVisibility;
    private int dotsTextViewVisibility;
    private DotsState dotsState;
    private String defaultMessage;
    private String alteredMessage;

    /** Constructor for Search Book Adapter **/
    public SearchedBookAdapter(int childLayoutResId, ArrayList<SearchedBook> searchedBookArrayList, Context context, boolean footerAdded, int footerLayoutResId) {
        super(childLayoutResId,searchedBookArrayList, context, footerAdded, footerLayoutResId);
        if(getContext() != null) {
            defaultMessage = getContext().getResources().getString(R.string.could_not_complete_search_query);
        }
    }

    @Override
    public DataObjectHolder onCreateInitializeDataObjectHolder(View v) {
        return new SearchedBookViewHolder(v);
    }

    @Override
    public DataObjectHolder onCreateFooterInitializeDataObjectHolder(View v) {
        return new SearchedBookFooterViewHolder(v);
    }

    @Override
    public void onBindItemViewHolder(DataObjectHolder holder, int position, SearchedBook searchedBookValueAtGivenPosition) {

        if(searchedBookValueAtGivenPosition != null) {
            SearchedBookViewHolder searchedBookViewHolder = (SearchedBookViewHolder) holder;

            /** Title **/
            if(searchedBookViewHolder.searchBookTitleView != null) {
                searchedBookViewHolder.searchBookTitleView.setText(searchedBookValueAtGivenPosition.getTitle());
            }

            /** Author **/
            if(searchedBookViewHolder.searchBookAuthorView != null) {
                searchedBookViewHolder.searchBookAuthorView.setText(TextUtils.join(",", searchedBookValueAtGivenPosition.getAuthors()));
            }

            /** ISBN **/
            if(searchedBookViewHolder.searchBookISBN != null) {
                searchedBookViewHolder.searchBookISBN.setText(getContext().getString(R.string.isbn) + searchedBookValueAtGivenPosition.getIndustryIdentifier());
            }

            /** Thumbnail **/
            String thumbnailUrl = searchedBookValueAtGivenPosition.getThumbnailLink();
            if(getContext() != null && searchedBookViewHolder.searchBookImageView != null){
                searchedBookViewHolder.searchBookImageView.setBackground(getContext().getResources().getDrawable(R.drawable.loading));
            }
            if(thumbnailUrl != null && !thumbnailUrl.equals("") && searchedBookViewHolder.searchBookImageView != null && getContext() != null) {
                Picasso.with(getContext())
                        .load(thumbnailUrl)
                        .error(R.drawable.no_preview_available)
                        .into(searchedBookViewHolder.searchBookImageView);
            } else if ((thumbnailUrl == null || thumbnailUrl.equals("")) && getContext() != null){
                searchedBookViewHolder.searchBookImageView.setBackground(getContext().getResources().getDrawable(R.drawable.no_preview_available));
            }
        }

        /** Reached close to end ask for more data demand **/
        if(Math.abs(getItemCount() - position) <= 3){
            if(moreDataRequired != null){
                moreDataRequired.onDataDemanded();
            }
        }
    }

    @Override
    public void onBindFooterItemViewHolder(DataObjectHolder holder) {
        SearchedBookFooterViewHolder searchedBookFooterViewHolder = (SearchedBookFooterViewHolder) holder;
        searchedBookFooterViewHolder.retryButton.setVisibility(retryButtonVisibility);
        searchedBookFooterViewHolder.retryTextView.setVisibility(retryTextViewVisibility);
        searchedBookFooterViewHolder.dotsTextView.setVisibility(dotsTextViewVisibility);
        if(getContext() != null){
            searchedBookFooterViewHolder.retryTextView.setText(defaultMessage);
        }
        if(dotsState == DotsState.START){
            searchedBookFooterViewHolder.dotsTextView.start();
        } else if(dotsState == DotsState.STOP){
            searchedBookFooterViewHolder.dotsTextView.stop();
        }

        /** If some alternate message is set by adapter, display that first **/
        if(alteredMessage != null && alteredMessage.length() >0){
            searchedBookFooterViewHolder.retryTextView.setVisibility(View.VISIBLE);
            searchedBookFooterViewHolder.retryTextView.setText(alteredMessage);
            alteredMessage = "";
        }
    }

    /** Add Multiple items at the end **/
    @Override
    public void addMultipleItems(ArrayList<SearchedBook> searchedBookArrayList){

        /** If empty data is given, ask for more data immediately **/
        if(searchedBookArrayList == null || searchedBookArrayList.size() == 0){
            if(moreDataRequired != null){
                moreDataRequired.onDataDemanded();
            }
        } else {
            super.addMultipleItems(searchedBookArrayList);
        }
    }

    /** View holder for Searched Book Adapter **/
    public class SearchedBookViewHolder extends DataObjectHolder {

        ImageView searchBookImageView;
        TextView searchBookTitleView;
        TextView searchBookAuthorView;
        TextView searchBookISBN;

        /** Constructor with root view **/
        public SearchedBookViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void initialize(View rootView) {
            if(rootView != null) {
                searchBookImageView = (ImageView) rootView.findViewById(R.id.search_result_book_image_view);
                searchBookTitleView = (TextView) rootView.findViewById(R.id.search_result_book_title);
                searchBookAuthorView = (TextView) rootView.findViewById(R.id.search_result_book_authors);
                searchBookISBN = (TextView) rootView.findViewById(R.id.search_result_book_isbn);
            }
        }
    }

    /** Footer View holder for Searched Book Adapter **/
    public class SearchedBookFooterViewHolder extends DataObjectHolder {

        Button retryButton;
        TextView retryTextView;
        DotsTextView dotsTextView;

        /** Constructor with root view **/
        public SearchedBookFooterViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void initialize(View rootView) {
            if(rootView != null){
                retryButton = (Button) rootView.findViewById(R.id.retry_button_search_book);
                retryTextView = (TextView) rootView.findViewById(R.id.retry_text_search_book);
                dotsTextView = (DotsTextView) rootView.findViewById(R.id.dots_search_book);
            }
        }
    }

    /** Updates footer for adapter **/
    public void updateFooter(int retryButtonVisibility, int retryTextViewVisibility, int dotsTextViewVisibility, DotsState dotsState, String alteredMessage){
        this.retryButtonVisibility = retryButtonVisibility;
        this.retryTextViewVisibility = retryTextViewVisibility;
        this.dotsTextViewVisibility = dotsTextViewVisibility;
        this.dotsState = dotsState;
        if(alteredMessage != null && alteredMessage.length() >0){
            this.alteredMessage = alteredMessage;
        }
        notifyDataSetChanged();
    }

    public interface MoreDataRequired {
        void onDataDemanded();
    }
}
