package com.pabhinav.fiboku.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.os.Bundle;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.pabhinav.fiboku.R;
import com.pabhinav.fiboku.application.FibokuApplication;
import com.pabhinav.fiboku.customanimation.PressEffectAnimation;
import com.pabhinav.fiboku.util.Constants;

public class MainScreenActivity extends BaseAbstractActivity {

    /**
     * {@link InterstitialAd} instance.
     */
    InterstitialAd mInterstitialAd;

    /**
     * Called when the activity is starting.
     *
     * <p>This activity extends {@link BaseAbstractActivity}, an abstract activity,
     * which mainly handles permissions grant and navigation drawer related events. </p>
     *
     * <p>Need to call {@link BaseAbstractActivity#setContentViewAndId(int, int)}, instead
     * of {@link android.support.v7.app.AppCompatActivity#setContentView(int)}, because
     * the abstract activity handles all layout inflation by itself, when setContentViewAndId
     * is called with a constant Activity Id for each Activity.</p>
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /** Full Screen flag : this draws behind status bar. Our status bar is translucent **/
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        /** Set activity content view by calling base activity method **/
        super.setContentViewAndId(R.layout.activity_main_screen, Constants.homeActivity);

        /** Press Effect on find book view **/
        new PressEffectAnimation(this, new ImageView[]{
                (ImageView)findViewById(R.id.find_books_colored_circular_view_with_shadow),
                (ImageView)findViewById(R.id.find_books_white_circular_view_with_shadow),
        });

        /** Press Effect on upload book view **/
        new PressEffectAnimation(this, new ImageView[]{
                (ImageView)findViewById(R.id.upload_books_colored_circular_view_with_shadow),
                (ImageView)findViewById(R.id.upload_books_white_circular_view_with_shadow)
        });

        /** Capture click event for find books **/
        findViewById(R.id.find_books_colored_circular_view_with_shadow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findBooksClicked(v);
            }
        });

        /** Capture click event for upload books **/
        findViewById(R.id.upload_books_colored_circular_view_with_shadow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadBooksClicked(v);
            }
        });

        /** Set up name in drawer  **/
        ((TextView)findViewById(R.id.agent_name_text_view)).setText(FibokuApplication.getInstance().getAgentName());

        /** Ad Mob **/
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        /**
         * Interstitial ads
         */
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_id));
        requestNewInterstitial();
    }

    /** Request for interstitial ad **/
    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        mInterstitialAd.loadAd(adRequest);
    }


    /** Find books clicked **/
    public void findBooksClicked(View v){

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mInterstitialAd.isLoaded()){
                    mInterstitialAd.show();
                    mInterstitialAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdClosed() {
                            super.onAdClosed();

                            /** Start the search book activity **/
                            Intent intent = new Intent(MainScreenActivity.this, SearchBookActivity.class);
                            startActivity(intent);
                        }
                    });
                }else {

                    /** Start the search book activity **/
                    Intent intent = new Intent(MainScreenActivity.this, SearchBookActivity.class);
                    startActivity(intent);
                }
            }
        }, 500);
    }

    /** Upload books clicked **/
    public void uploadBooksClicked(View v){

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                /** Shared Element Activity Transition **/
                Intent intent = new Intent(MainScreenActivity.this, UploadBookActivity.class);
                Pair<View, String> pair1 = Pair.create(findViewById(R.id.upload_books_colored_circular_view), getString(R.string.upper_half));
                Pair<View, String> pair2 = Pair.create(findViewById(R.id.upload_books_white_circular_view), getString(R.string.lower_half));
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainScreenActivity.this, pair1, pair2);
                startActivity(intent, options.toBundle());
            }
        }, 500);
    }

}
