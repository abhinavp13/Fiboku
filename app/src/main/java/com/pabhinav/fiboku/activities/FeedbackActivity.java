package com.pabhinav.fiboku.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.pabhinav.fiboku.R;
import com.pabhinav.fiboku.firebase.FeedbackFirebaseHelper;
import com.pabhinav.fiboku.util.Constants;

/**
 * This activity captures user feedback for the app.
 *
 * @author pabhinav
 */
public class FeedbackActivity extends BaseAbstractActivity {

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
         * Set content view and id in base abstract activity
         */
        setContentViewAndId(R.layout.activity_feedback, Constants.feedbackActivity);
    }

    /**
     * Captures feedback entered by user.</br>
     * Also, finishes the activity.
     *
     * @param v view for take feedback icon
     */
    public void takeFeedback(View v){
        EditText headingText = (EditText)findViewById(R.id.heading_message);
        EditText longMessageText = (EditText)findViewById(R.id.long_message);

        if(headingText.getText().toString().length() == 0 && longMessageText.getText().toString().length() == 0){
            Toast.makeText(this, R.string.empty_feedback_recorded, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        /**
         * Linked with firebase for receiving feedback.
         * If there is no internet connectivity, then
         * feedback is sent whenever internet connection
         * comes back again. {@link NetworkChangeRecevier}
         * does this work.
         */
        final String feedback = headingText.getText().toString() + getString(R.string.details) + longMessageText.getText().toString();
        FeedbackFirebaseHelper firebaseHelper = new FeedbackFirebaseHelper(this);
        firebaseHelper.sendFeedback(feedback);

        Toast.makeText(this, R.string.thanks_for_your_response, Toast.LENGTH_LONG).show();

        finish();
    }
}
