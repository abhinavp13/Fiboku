package com.pabhinav.fiboku.activities;

import android.os.Bundle;

import com.pabhinav.fiboku.R;
import com.pabhinav.fiboku.util.Constants;

/**
 * Activity for describing about app.
 *
 * @author pabhinav
 */
public class AboutActivity extends BaseAbstractActivity {

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

        /**
         * Set activity content view by calling base activity method
         */
        super.setContentViewAndId(R.layout.activity_about, Constants.aboutActivity);
    }
}
