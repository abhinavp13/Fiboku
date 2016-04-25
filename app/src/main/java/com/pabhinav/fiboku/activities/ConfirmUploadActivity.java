package com.pabhinav.fiboku.activities;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pabhinav.fiboku.R;
import com.pabhinav.fiboku.contentprovider.UploadedBookProvider;
import com.pabhinav.fiboku.db.UploadedBookTable;
import com.pabhinav.fiboku.firebase.UploadedBooksFirebaseHelper;
import com.pabhinav.fiboku.models.BookCondition;
import com.pabhinav.fiboku.models.SearchedBook;
import com.pabhinav.fiboku.models.UploadedBook;
import com.pabhinav.fiboku.util.BookUtil;
import com.pabhinav.fiboku.util.BundleUtil;
import com.pabhinav.fiboku.util.Constants;
import com.pabhinav.fiboku.util.FLog;
import com.pabhinav.fiboku.widget.StackWidgetProvider;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * This activity confirms upload of book.
 *
 * @author pabhinav
 */
public class ConfirmUploadActivity extends AppCompatActivity {

    /**
     * Book's image
     */
    @Bind(R.id.book_preview_image_view)
    ImageView bookPreviewImageView;

    /**
     * Book's title
     */
    @Bind(R.id.preview_title_text_view)
    TextView previewTitleTextView;

    /**
     * Book's authors
     */
    @Bind(R.id.preview_author_text_view)
    TextView previewAuthorTextView;

    /**
     * Book's description
     */
    @Bind(R.id.preview_description_text_view)
    TextView previewDescriptionTextView;

    /**
     * Book's publishing details
     */
    @Bind(R.id.preview_publishing_details_text_view)
    TextView previewPublishingDetailsTextView;

    /**
     * Book's condition
     */
    @Bind(R.id.book_condition_text_view)
    TextView conditionalNameTextView;

    /**
     * Book's condition details
     */
    @Bind(R.id.book_condition_details_text_view)
    TextView conditionalDetailTextView;

    /**
     * Book's upload timestamp
     */
    @Bind(R.id.preview_timestamp_text_view)
    TextView previewTimestampTextView;

    /**
     * Additional details edit text.
     */
    @Bind(R.id.additional_details_app_compat_edit_text)
    TextView additionalDetailsAppCompatEditText;

    /**
     * {@link UploadedBook} domain object.
     */
    UploadedBook uploadedBook;

    /**
     * Bitmap image of book
     */
    Bitmap bookImage;

    /**
     * boolean value denoting update required for uploaded book
     */
    boolean updateRequired;

    /**
     * Book id for the book present in database, used to update book in database.
     */
    int bookId;

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
        setContentView(R.layout.activity_confirm_upload);

        /**
         * Bind {@link ButterKnife}
         */
        ButterKnife.bind(this);

        /**
         * Get the dynamic elements passed on with intent or from saved instance state.
         */
        uploadedBook = BundleUtil.getUploadedBookFromBundle(savedInstanceState, getIntent().getExtras(), Constants.UPLOADED_BOOK_KEY, null);
        bookImage = BookUtil.convertByteArrayToBitmap(uploadedBook.getBookImage());
        updateRequired = BundleUtil.getBooleanFromBundle(savedInstanceState, getIntent().getExtras(), Constants.UPDATE_REQUIRED, false);
        bookId = BundleUtil.getIntFromBundle(savedInstanceState, getIntent().getExtras(), Constants.BOOK_ID, -1);
        SearchedBook searchedBook = uploadedBook.getSearchedBook();

        /**
         * Set Searched Book Details
         */
        setTitle(searchedBook.getTitle(), searchedBook.getSubTitle());
        setAuthors(searchedBook.getAuthors());
        setDescription(searchedBook.getDescription());
        setPublishingDetails(searchedBook.getPublisher(), searchedBook.getPublishedDate());
        BookCondition bookCondition = uploadedBook.getCondition();
        setConditionalDetails(bookCondition);

        /**
         * Get date formatted
         */
        DateFormat df = new SimpleDateFormat(getString(R.string.date_format));
        String date = df.format(Calendar.getInstance().getTime());
        previewTimestampTextView.setText(date);

        /**
         * if book image is null, picasso was not able to load image, now load it.
         */
        if(bookImage == null && searchedBook.getThumbnailLink().length() != 0){
            Picasso.with(this).load(searchedBook.getThumbnailLink()).into(target);
        } else {
            bookPreviewImageView.setImageBitmap(bookImage);
        }
    }

    /**
     * {@link Picasso} target for loading bitmap images.
     */
    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            bookImage = bitmap;
            bookPreviewImageView.setImageBitmap(bitmap);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {}

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {}
    };

    public void onSaveInstanceState(Bundle outState){

        /**
         * Saving dynamic elements for this activity.
         */
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constants.UPLOADED_BOOK_KEY, uploadedBook);
        outState.putBoolean(Constants.UPDATE_REQUIRED, updateRequired);
        outState.putInt(Constants.BOOK_ID, bookId);
    }

    /**
     * Back icon pressed by user
     *
     * @param v view for the back icon
     */
    public void backClicked(View v){
        super.onBackPressed();
    }

    /**
     * Tick clicked by user.</br>
     * This method prepares {@link UploadedBook} domain object.
     * Then, further if update is required, it updates using {@link UploadedBookProvider}
     * custom content provider into SQLite database, else inserts data.
     *
     * @param v view for the tick icon
     */
    public void tickClicked(View v){

        /** Prepares {@link UploadedBook} domain object **/
        UploadedBook uploadedBookData = new UploadedBook.UploadedBookBuilder()
                .addSearchedBook(uploadedBook.getSearchedBook())
                .addCondition(uploadedBook.getCondition())
                .addBookImage(BookUtil.convertBitmapToByteArray(bookImage))
                .addConditionDescription(additionalDetailsAppCompatEditText.getText().toString())
                .addUploadTimestamp(previewTimestampTextView.getText().toString())
                .build();

        if(updateRequired){

            /**
             * Update Data
             */
            int rowsAffected = getContentResolver().update(UploadedBookProvider.CONTENT_URI, UploadedBookTable.getContentValues(uploadedBookData),  UploadedBookTable.KEY_ID + " = " + bookId, null);

            FLog.d(this, String.valueOf(rowsAffected));

        } else {

            /**
             * Insert data
             */
            Uri result = getContentResolver().insert(UploadedBookProvider.CONTENT_URI, UploadedBookTable.getContentValues(uploadedBookData));

            FLog.d(this, uploadedBookData.toString());
            FLog.d(this, String.valueOf(result));
        }

        /**
         * Update in online firebase database
         */
        UploadedBooksFirebaseHelper uploadedBooksFirebaseHelper = new UploadedBooksFirebaseHelper();
        uploadedBooksFirebaseHelper.uploadBook(uploadedBookData);

        /**
         * Update Widget
         */
        Intent intent = new Intent(this,StackWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        sendBroadcast(intent);

        /**
         * close this activity and below activities
         */
        setResult(Constants.CLOSE_ACTIVITY);
        finish();
    }

    /**
     * Sets title for the book
     *
     * @param title of the book
     * @param subTitle of the book
     */
    public void setTitle(String title, String subTitle){
        if(subTitle == null
                || subTitle.length() == 0){
            previewTitleTextView.setText(title);
        } else {
            previewTitleTextView.setText(String.format("%s:%s", title, subTitle));
        }
    }

    /**
     * Set authors for the book
     *
     * @param authors of the book
     */
    public void setAuthors(ArrayList<String> authors) {
        if (authors == null
                || authors.size() == 0) {
            previewAuthorTextView.setText("");
        } else {
            previewAuthorTextView.setText(getString(R.string.by) + TextUtils.join(",", authors));
        }
    }

    /**
     * Sets book description.
     *
     * @param description of the book.
     */
    public void setDescription(String description) {
        if (description == null
                || description.length() == 0) {
            previewDescriptionTextView.setText(R.string.no_description_found);
        } else {
            previewDescriptionTextView.setText(description);
        }
    }

    /**
     * Set publishing details for the book
     *
     * @param publisher of the book
     * @param publishingDate of the book.
     */
    public void setPublishingDetails(String publisher, String publishingDate) {
        if (publisher == null
                || publisher.length() == 0) {
            previewPublishingDetailsTextView.setText("");
        } else {
            if(publishingDate == null
                    || publishingDate.length() == 0){
                previewPublishingDetailsTextView.setText(publisher);
            } else {
                previewPublishingDetailsTextView.setText(String.format("%s - %s", publisher, publishingDate));
            }
        }
    }

    /**
     * Given the {@link BookCondition} enum, this method sets book condition
     * details, which include : setting book condition name and setting book condition
     * detail.
     *
     * @param condition {@link BookCondition} enum
     */
    private void setConditionalDetails(BookCondition condition){
        if(condition == null){
            return;
        }
        switch (condition){
            case  POOR :
                conditionalNameTextView.setText(R.string.poor);
                conditionalDetailTextView.setText(R.string.poor_description);
                break;
            case LOOSE_BINDING :
                conditionalNameTextView.setText(R.string.losse_binding);
                conditionalDetailTextView.setText(R.string.loose_binding_description);
                break;
            case BINDING_COPY :
                conditionalNameTextView.setText(R.string.binding_copy);
                conditionalDetailTextView.setText(R.string.binding_copy_description);
                break;
            case FAIR:
                conditionalNameTextView.setText(R.string.fair);
                conditionalDetailTextView.setText(R.string.fair_description);
                break;
            case GOOD:
                conditionalNameTextView.setText(R.string.good);
                conditionalDetailTextView.setText(R.string.good_description);
                break;
            case FINE :
                conditionalNameTextView.setText(R.string.fine);
                conditionalDetailTextView.setText(R.string.fine_description);
                break;
            case NEW :
                conditionalNameTextView.setText(R.string.new_string);
                conditionalDetailTextView.setText(R.string.new_description);
                break;
        }
    }

    @Override
    public void onDestroy() {
        Picasso.with(this).cancelRequest(target);
        super.onDestroy();
    }
}
