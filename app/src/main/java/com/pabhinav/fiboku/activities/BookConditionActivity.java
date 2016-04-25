package com.pabhinav.fiboku.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pabhinav.fiboku.R;
import com.pabhinav.fiboku.models.BookCondition;
import com.pabhinav.fiboku.models.SearchedBook;
import com.pabhinav.fiboku.models.UploadedBook;
import com.pabhinav.fiboku.util.BookUtil;
import com.pabhinav.fiboku.util.BundleUtil;
import com.pabhinav.fiboku.util.Constants;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Book Condition describing activity.
 *
 * @author pabhinav
 */
public class BookConditionActivity extends AppCompatActivity {

    /**
     * Seek bar used in this activity for rating book condition
     */
    @Bind(R.id.seek_bar_book_condition)
    SeekBar seekBar;

    /**
     * Text view which displays detail on chosen book condition.
     */
    @Bind(R.id.condition_detail_text_view)
    TextView conditionalDetailTextView;

    /**
     * Text view which displays book condition.
     */
    @Bind(R.id.text_view_book_condition)
    TextView conditionalNameTextView;

    /**
     * {@link UploadedBook} instance.
     */
    UploadedBook uploadedBook;

    /**
     * Bitmap image for book
     */
    Bitmap bookImage;

    /**
     * boolean value for update required state.
     */
    boolean updateRequired;

    /**
     * The id of book as in SQLite database. </br>
     * This is used to update record in SQLite.
     */
    int bookId;

    /**
     * Image View used to portray book condition.
     */
    @Bind(R.id.image_view_book_condition)
    ImageView bookConditionImageView;

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
        setContentView(R.layout.activity_book_condition);

        /**
         * Bind {@link ButterKnife}
         */
        ButterKnife.bind(this);

        /**
         * Fetch dynamic states passed with intent or saved on orientation changes.
         */
        uploadedBook = BundleUtil.getUploadedBookFromBundle(savedInstanceState, getIntent().getExtras(), Constants.UPLOADED_BOOK_KEY, null);
        bookImage = BookUtil.convertByteArrayToBitmap(uploadedBook.getBookImage());
        updateRequired = BundleUtil.getBooleanFromBundle(savedInstanceState, getIntent().getExtras(), Constants.UPDATE_REQUIRED, false);
        bookId = BundleUtil.getIntFromBundle(savedInstanceState, getIntent().getExtras(), Constants.BOOK_ID, -1);

        /**
         * If we have condition already, set that up in seek bar
         */
        if(uploadedBook.getCondition() != null){
            BookCondition bookCondition = uploadedBook.getCondition();
            seekBar.setProgress(getProgressFromBookCondition(bookCondition));
        }

        /**
         * Set book condition details based on book condition set in seek bar.
         */
        setConditionalDetails(seekBar.getProgress());

        /**
         * Listen to seek bar events
         */
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                setConditionalDetails(progressValue);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.

        /**
         * Important to save some dynamic elements.
         */
        savedInstanceState.putParcelable(Constants.UPLOADED_BOOK_KEY, uploadedBook);
        savedInstanceState.putBoolean(Constants.UPDATE_REQUIRED, updateRequired);
        savedInstanceState.putInt(Constants.BOOK_ID, bookId);
    }

    /**
     * Tick button clicked by user.</br>
     * This method prepares {@link UploadedBook} data object,
     * and passes this data to {@link ConfirmUploadActivity}.
     *
     * @param v view for tick button.
     */
    public void tickClicked(View v){
        Intent intent = new Intent(this, ConfirmUploadActivity.class);
        UploadedBook uploadedBookData = new UploadedBook.UploadedBookBuilder()
                .addSearchedBook(uploadedBook.getSearchedBook())
                .addCondition(getBookCondition(seekBar.getProgress()))
                .addBookImage(BookUtil.convertBitmapToByteArray(bookImage))
                .build();
        intent.putExtra(Constants.UPLOADED_BOOK_KEY, uploadedBookData);
        intent.putExtra(Constants.UPDATE_REQUIRED, updateRequired);
        intent.putExtra(Constants.BOOK_ID, bookId);
        startActivityForResult(intent, 7);
    }

    /**
     * Progress in seek bar mapped to {@link BookCondition} enum.
     *
     * @param progress in seek bar.
     * @return {@link BookCondition} instance corresponding to given progress.
     */
    private BookCondition getBookCondition(int progress){
        switch (progress){
            case 0 :
                return BookCondition.POOR;
            case 1 :
                return BookCondition.LOOSE_BINDING;
            case 2 :
                return BookCondition.BINDING_COPY;
            case 3 :
                return BookCondition.FAIR;
            case 4 :
                return BookCondition.GOOD;
            case 5 :
                return BookCondition.FINE;
            case 6 :
                return BookCondition.NEW;
        }
        return null;
    }

    /**
     * {@link BookCondition} mapped to progress int value in Seek Bar.
     *
     * @param bookCondition {@link BookCondition} enum
     * @return progress int value corresponding to {@link BookCondition} enum.
     */
    private int getProgressFromBookCondition(BookCondition bookCondition){
        switch (bookCondition){
            case POOR:
                return 0;
            case LOOSE_BINDING:
                return 1;
            case BINDING_COPY:
                return 2;
            case FAIR:
                return 3;
            case GOOD:
                return 4;
            case FINE:
                return 5;
            case NEW :
                return 6;
        }
        return -1;
    }

    /**
     * Given the progress integer value of seek bar, this method sets book condition
     * details, which include : setting book condition name, setting book condition
     * detail, and setting book condition image.
     *
     * @param progress int value for seek bar.
     */
    private void setConditionalDetails(int progress){
        BookCondition condition = getBookCondition(progress);
        if(condition == null){
            return;
        }
        switch (condition){
            case  POOR :
                bookConditionImageView.setImageDrawable(getResources().getDrawable(R.drawable.poor_book_condition));
                conditionalNameTextView.setText(R.string.poor);
                conditionalDetailTextView.setText(R.string.poor_description);
                break;
            case LOOSE_BINDING :
                bookConditionImageView.setImageDrawable(getResources().getDrawable(R.drawable.loose_binding));
                conditionalNameTextView.setText(R.string.losse_binding);
                conditionalDetailTextView.setText(R.string.loose_binding_description);
                break;
            case BINDING_COPY :
                bookConditionImageView.setImageDrawable(getResources().getDrawable(R.drawable.binding_copy));
                conditionalNameTextView.setText(R.string.binding_copy);
                conditionalDetailTextView.setText(R.string.binding_copy_description);
                break;
            case FAIR:
                bookConditionImageView.setImageDrawable(getResources().getDrawable(R.drawable.fair_book_condition));
                conditionalNameTextView.setText(R.string.fair);
                conditionalDetailTextView.setText(R.string.fair_description);
                break;
            case GOOD:
                bookConditionImageView.setImageDrawable(getResources().getDrawable(R.drawable.good_book_condition));
                conditionalNameTextView.setText(R.string.good);
                conditionalDetailTextView.setText(R.string.good_description);
                break;
            case FINE :
                bookConditionImageView.setImageDrawable(getResources().getDrawable(R.drawable.fine_book_condition));
                conditionalNameTextView.setText(R.string.fine);
                conditionalDetailTextView.setText(R.string.fine_description);
                break;
            case NEW :
                bookConditionImageView.setImageDrawable(getResources().getDrawable(R.drawable.new_book_condition));
                conditionalNameTextView.setText(R.string.new_string);
                conditionalDetailTextView.setText(R.string.new_description);
                break;
        }
    }

    /**
     * Called when result is set by called activity. </br>
     * if result code orders to close this activity, then
     * close this activity.
     *
     * @param requestCode used to call the activity.
     * @param resultCode used to capture result type from called activity.
     * @param data {@link Intent} data.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, requestCode, data);
        if(requestCode == 7 && resultCode == Constants.CLOSE_ACTIVITY){
            setResult(Constants.CLOSE_ACTIVITY);
            finish();
        }
    }

    /**
     * Back icon clicked by user. </br>
     * This method simply kills the activity.
     *
     * @param v view for back icon.
     */
    public void backClicked(View v){
        super.onBackPressed();
    }
}
