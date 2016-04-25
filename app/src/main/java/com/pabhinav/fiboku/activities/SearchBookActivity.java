package com.pabhinav.fiboku.activities;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.pabhinav.fiboku.R;
import com.pabhinav.fiboku.activityhelpers.SearchActivityHelper;
import com.pabhinav.fiboku.models.SearchedBook;
import com.pabhinav.fiboku.util.BookUtil;
import com.pabhinav.fiboku.util.BundleUtil;
import com.pabhinav.fiboku.util.Constants;
import com.pabhinav.fiboku.util.FLog;

import butterknife.Bind;
import butterknife.ButterKnife;
import lombok.Getter;
import pl.tajchert.sample.DotsTextView;

/**
 * Searches book online in Google Books catalogue
 *
 * @author pabhinav
 */
public class SearchBookActivity extends AppCompatActivity {

    /**
     * Recycler view for displaying searched books
     */
    @Bind(R.id.search_result_recycler_view_search_book)
    RecyclerView searchBookRecyclerView;

    /**
     * Edit text for entering book's name to be searched
     */
    @Bind(R.id.search_edit_text_search_book)
    EditText searchBookEditText;

    /**
     * Activity helper class instance
     */
    SearchActivityHelper searchActivityHelper;

    /**
     * Search query string
     */
    @Getter
    String searchedQuery;

    /**
     * Handler used for waiting for a while before actually
     * searching for a book.
     */
    private Handler handler = new Handler();

    /**
     * Book isbn
     */
    String isbn;

    /**
     * Boolean value denoting whether request came from upload book or not.
     */
    boolean isFromUpload;

    /**
     * Called when the activity is starting.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_book);
        ButterKnife.bind(this);

        /**
         * Recycler View Initializations :
         * Make sure that we do not re-initialize if we have done so already.
         * (Helpful when orientation is changed and onCreate is called again)
         **/
        if(searchActivityHelper == null) {
            searchActivityHelper = new SearchActivityHelper(this);
            searchActivityHelper.linkRecyclerViewAndAdapter(searchBookRecyclerView);

        }

        /** Listening to item click events **/
        searchActivityHelper.setItemClickEvent(new RecyclerViewItemClick());

        /** Text change listener **/
        searchBookEditText.addTextChangedListener(new SearchTextWatcher());

        /** Check whether the request came from upload books, if this is the case, put the isbn in searched string **/
        isFromUpload = BundleUtil.getBooleanFromBundle(savedInstanceState, getIntent().getExtras(), Constants.IS_FROM_UPLOAD, false);
        isbn = BundleUtil.getStringFromBundle(savedInstanceState, getIntent().getExtras(), Constants.ISBN_KEY, "");
        if(isFromUpload){
            searchBookEditText.setText(isbn);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        /** Saving recycler view's adapter data **/
        if(searchBookRecyclerView != null && searchBookRecyclerView.getLayoutManager() != null) {
            outState.putParcelable(Constants.SAVED_LAYOUT_MANAGER_KEY,
                    searchBookRecyclerView.getLayoutManager().onSaveInstanceState());
        }
        outState.putBoolean(Constants.IS_FROM_UPLOAD, isFromUpload);
        outState.putString(Constants.ISBN_KEY, isbn);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        /** Restoring recycler view's adapter data **/
        if(savedInstanceState != null && searchBookRecyclerView != null && searchBookRecyclerView.getLayoutManager() != null){
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(Constants.SAVED_LAYOUT_MANAGER_KEY);
            searchBookRecyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /** Class for handling click events **/
    class RecyclerViewItemClick implements SearchActivityHelper.ItemClickEvent{

        @Override
        public void onItemClicked(ImageView transitionImageView, SearchedBook searchedBook) {

            Intent intent = new Intent(SearchBookActivity.this, BookDetailActivity.class);
            intent.putExtra(Constants.SEARCHED_BOOK_KEY, searchedBook);
            intent.putExtra(Constants.IS_FROM_UPLOAD, isFromUpload);
            BitmapDrawable bitmapDrawable = ((BitmapDrawable) transitionImageView.getDrawable());
            if(bitmapDrawable == null){
                /** This means that still image was not loaded by Picasso. **/
            } else {
                intent.putExtra(Constants.BOOK_IMAGE_KEY, BookUtil.convertBitmapToByteArray(bitmapDrawable.getBitmap()));
            }
            Pair<View, String> pair1 = Pair.create((View)transitionImageView, getString(R.string.common_image_transition_view));
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(SearchBookActivity.this, pair1);
            startActivityForResult(intent, 5, options.toBundle());
        }
    }

    /** Class for handling text changes in edit text **/
    class SearchTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {

            /** Need to remove callback **/
            handler.removeCallbacks(mFilterTask);

            /** If length is zero, immediately remove adapter items **/
            if(s != null && s.length() == 0){
                if(searchActivityHelper != null){
                    searchActivityHelper.handleEmptyQueryEvent();
                }
                searchedQuery = "";
                return;
            }

            /** Wait one second to get stabled paused input **/
            handler.postDelayed(mFilterTask, 1000);
        }
    }


    /** Retry demanded by user **/
    public void retryClicked(View v){
        if(searchActivityHelper != null){
            searchActivityHelper.retrySearching();
        }
    }

    /** Filter task runnable **/
    Runnable mFilterTask = new Runnable() {
        @Override
        public void run() {
            if(searchBookEditText != null){
                String text = searchBookEditText.getText().toString();
                searchActivityHelper.handleNewQueryEvent(text);
                searchedQuery = text;
            }
        }
    };

    @Override
    protected void onDestroy(){
        handler.removeCallbacks(mFilterTask);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, requestCode, data);
        if(requestCode == 5 && resultCode == Constants.CLOSE_ACTIVITY){
            finish();
        }
    }


}
