package com.pabhinav.fiboku.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.pabhinav.fiboku.R;
import com.pabhinav.fiboku.models.BookCondition;
import com.pabhinav.fiboku.models.SearchedBook;
import com.pabhinav.fiboku.models.UploadedBook;
import com.pabhinav.fiboku.util.BookUtil;
import com.pabhinav.fiboku.util.BundleUtil;
import com.pabhinav.fiboku.util.Constants;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.w3c.dom.Text;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * This activity displays book details.
 *
 * @author pabhinav
 */
public class BookDetailActivity extends AppCompatActivity {

    /**
     * Title text view
     */
    @Bind(R.id.title_text_view)
    TextView titleTextView;

    /**
     * Title with subtitle text view
     */
    @Bind(R.id.title_text_view_1)
    TextView fullTitleTextView;

    /**
     * Authors text view
     */
    @Bind(R.id.authors_text_view)
    TextView authorsTextView;

    /**
     * {@link CardView} for displaying authors.
     */
    @Bind(R.id.authors_card_view)
    CardView authorsCardView;

    /**
     * Book description text view
     */
    @Bind(R.id.description_text_view)
    TextView descriptionTextView;

    /**
     * {@link CardView} for displaying book description
     */
    @Bind(R.id.description_card_view)
    CardView descriptionCardView;

    /**
     * Publishing details displaying text view
     */
    @Bind(R.id.publishing_details_text_view)
    TextView publishingDetailsTextView;

    /**
     * {@link CardView} for displaying publishing details.
     */
    @Bind(R.id.publisher_card_view)
    CardView publishingCardView;

    /**
     * Yes text view in the layout
     */
    @Bind(R.id.yes_text_view)
    TextView yesTextView;

    /**
     * No text view in the layout.
     */
    @Bind(R.id.no_text_view)
    TextView noTextView;

    /**
     * {@link CardView} for displaying sharing text view
     */
    @Bind(R.id.sharing_card_view)
    CardView sharingCardView;

    /**
     * Title text for toolbar.
     */
    @Bind(R.id.toolbar_title_text_view)
    TextView toolbarTitle;

    /**
     * {@link AppBarLayout} in this activity.
     */
    @Bind(R.id.appbar)
    AppBarLayout appBarLayout;

    /**
     * {@link CardView} for displaying book required text
     */
    @Bind(R.id.need_book_card_view)
    CardView needBookCardView;

    /**
     * Yes text view for find book
     */
    @Bind(R.id.yes_find_text_view)
    TextView yesFindTextView;

    /**
     * No text view for find book
     */
    @Bind(R.id.no_find_text_view)
    TextView noFindTextView;

    /**
     * Image for book on display
     */
    @Bind(R.id.book_image_view)
    ImageView bookImageView;

    /**
     * {@link SearchedBook} model.
     */
    SearchedBook searchedBook;

    /**
     * Boolean value denoting call made for uploading a book.
     */
    Boolean isFromUpload;

    /**
     * Bitmap book image.
     */
    Bitmap bookImage;

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
        setContentView(R.layout.activity_book_detail);

        /**
         * Bind {@link ButterKnife}
         */
        ButterKnife.bind(this);

        /**
         * Get the dynamic elements passed on with intent or from saved instance state.
         */
        searchedBook = BundleUtil.getSearchedBookFromBundle(savedInstanceState, getIntent().getExtras(), Constants.SEARCHED_BOOK_KEY, null);
        isFromUpload = BundleUtil.getBooleanFromBundle(savedInstanceState, getIntent().getExtras(), Constants.IS_FROM_UPLOAD, false);
        bookImage = BookUtil.convertByteArrayToBitmap(BundleUtil.getByteArrayFromBundle(savedInstanceState, getIntent().getExtras(), Constants.BOOK_IMAGE_KEY, null));

        /**
         * if book image is null, picasso was not able to load image, now load it.
         */
        if(bookImage == null && searchedBook.getThumbnailLink().length() != 0){
            Picasso.with(this).load(searchedBook.getThumbnailLink()).into(target);
        } else {
            bookImageView.setImageBitmap(bookImage);
        }

        /**
         * If call came from upload activity, then don't show need this book tile, else don't show share this book tile
         */
        if(isFromUpload){
            needBookCardView.setVisibility(View.GONE);
        } else {
            sharingCardView.setVisibility(View.GONE);
        }

        /**
         * Set Searched Book Details
         */
        setTitle(searchedBook.getTitle(), searchedBook.getSubTitle());
        setAuthors(searchedBook.getAuthors());
        setDescription(searchedBook.getDescription());
        setPublishingDetails(searchedBook.getPublisher(), searchedBook.getPublishedDate());

        /**
         * Upload book Yes clicked
         */
        yesTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookDetailActivity.this, BookConditionActivity.class);
                UploadedBook uploadedBook = new UploadedBook.UploadedBookBuilder()
                        .addSearchedBook(searchedBook)
                        .addBookImage(BookUtil.convertBitmapToByteArray(bookImage))
                        .build();
                intent.putExtra(Constants.UPLOADED_BOOK_KEY, uploadedBook);
                startActivityForResult(intent, 6);
            }
        });

        /**
         * Find book Yes clicked
         */
        yesFindTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookDetailActivity.this, FindBookInFriendActivity.class);
                intent.putExtra(Constants.SEARCHED_BOOK_TITLE, searchedBook.getTitle());
                startActivity(intent);
            }
        });

        /**
         * find book No clicked
         */
        noFindTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Animation slideAnimation = AnimationUtils.loadAnimation(BookDetailActivity.this, R.anim.slide_right_to_left);
                slideAnimation.setFillAfter(true);
                needBookCardView.startAnimation(slideAnimation);
                slideAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        needBookCardView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
            }
        });

        /**
         * upload book No clicked
         */
        noTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Animation slideAnimation = AnimationUtils.loadAnimation(BookDetailActivity.this, R.anim.slide_right_to_left);
                slideAnimation.setFillAfter(true);
                sharingCardView.startAnimation(slideAnimation);
                slideAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        sharingCardView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
            }
        });

        /**
         * app bar offset listener
         */
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }

                /**
                 * For a fixed scroll range, display toolbar text title, else hide toolbar text.
                 */
                if (scrollRange + verticalOffset >= -1*getResources().getDimension(R.dimen.density_56dp) && scrollRange + verticalOffset <= getResources().getDimension(R.dimen.density_56dp)) {
                    /**
                     * Show title on toolbar
                     */
                    setToolbarTitle(searchedBook.getTitle(), searchedBook.getSubTitle());
                    isShow = true;
                } else if(isShow) {

                    /**
                     * Empty text in toolbar title.
                     */
                    setToolbarTitle("","");
                    isShow = false;
                }
            }
        });
    }

    /**
     * {@link Picasso} target for loading bitmap images.
     */
    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            bookImage = bitmap;
            bookImageView.setImageBitmap(bitmap);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {}

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {}
    };

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.

        /**
         * Saving dynamic elements for this activity.
         */
        savedInstanceState.putParcelable(Constants.SEARCHED_BOOK_KEY, searchedBook);
        savedInstanceState.putBoolean(Constants.IS_FROM_UPLOAD, isFromUpload);
        savedInstanceState.putByteArray(Constants.BOOK_IMAGE_KEY, BookUtil.convertBitmapToByteArray(bookImage));
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
            titleTextView.setText(title);
            fullTitleTextView.setText(title);
        } else {
            titleTextView.setText(String.format("%s:%s", title, subTitle));
            fullTitleTextView.setText(String.format("%s:%s", title, subTitle));
        }
    }


    /**
     * Sets title in the toolbar.
     *
     * @param title of the book
     * @param subTitle of the book
     */
    public void setToolbarTitle(String title, String subTitle){
        if(title.length() == 0
                && subTitle.length() == 0){
            toolbarTitle.setText("");
        }
        if(subTitle.length() == 0){
            toolbarTitle.setText(title);
        } else {
            toolbarTitle.setText(String.format("%s:%s", title, subTitle));
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
            authorsCardView.setVisibility(View.GONE);
        } else {
            authorsTextView.setText(TextUtils.join(",", authors));
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
            descriptionCardView.setVisibility(View.GONE);
        } else {
            descriptionTextView.setText(description);
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
            publishingCardView.setVisibility(View.GONE);
        } else {
            if(publishingDate == null
                    || publishingDate.length() == 0){
                publishingDetailsTextView.setText(publisher);
            } else {
                publishingDetailsTextView.setText(String.format("%s - %s", publisher, publishingDate));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, requestCode, data);
        if(requestCode == 6 && resultCode == Constants.CLOSE_ACTIVITY){
            setResult(Constants.CLOSE_ACTIVITY);
            finish();
        }
    }

    /**
     * Back icon pressed by user
     *
     * @param v view for the back icon
     */
    public void backClicked(View v){
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        Picasso.with(this).cancelRequest(target);
        super.onDestroy();
    }
}
