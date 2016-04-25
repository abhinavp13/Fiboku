package com.pabhinav.fiboku.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.DigitsAuthButton;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.firebase.client.FirebaseError;
import com.pabhinav.fiboku.R;
import com.pabhinav.fiboku.application.FibokuApplication;
import com.pabhinav.fiboku.firebase.FirebaseErrorHandler;
import com.pabhinav.fiboku.firebase.LoginFirebaseHelper;
import com.pabhinav.fiboku.firebase.UploadedBooksFirebaseHelper;
import com.pabhinav.fiboku.models.UploadedBook;
import com.pabhinav.fiboku.models.UserData;
import com.pabhinav.fiboku.util.Constants;
import com.pabhinav.fiboku.util.FLog;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.tajchert.sample.DotsTextView;

/**
 * Activity confirms phone number.
 *
 * @author pabhinav
 */
public class PhoneNumberVerificationActivity extends AppCompatActivity {

    /**
     * Loading view
     */
    @Bind(R.id.loading_view)
    View loadingView;

    /**
     * view pretending card view as background
     */
    @Bind(R.id.card_view_1)
    View cardView;

    /**
     * Loading text view.
     */
    @Bind(R.id.loading_text_view)
    TextView loadingTextView;

    /**
     * {@link DotsTextView} for displaying loading dots.
     */
    @Bind(R.id.dots_text_view)
    DotsTextView dotsTextView;

    /**
     * {@link FirebaseErrorHandler} instance for handling error cases.
     */
    FirebaseErrorHandler firebaseErrorHandler;

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
        setContentView(R.layout.activity_phone_number_verification);
        ButterKnife.bind(this);

        /** Firebase Error Handler **/
        firebaseErrorHandler = new FirebaseErrorHandler(findViewById(R.id.main_container));

        /** Using twitter digits for phone number verification **/
        DigitsAuthButton digitsButton = (DigitsAuthButton) findViewById(R.id.auth_button);
        digitsButton.setAuthTheme(R.style.CustomDigitsTheme);
        digitsButton.setCallback(new AuthCallback() {
            @Override
            public void success(DigitsSession session, String phoneNumber) {

                /** Wrong phone Number returned **/
                if (phoneNumber == null
                        || phoneNumber.isEmpty()
                        || phoneNumber.length() < 10) {

                    showFailureSnackBar();
                    return;
                }

                FLog.d(PhoneNumberVerificationActivity.this, getString(R.string.phone_number_verified) + phoneNumber);
                loadingView.setVisibility(View.VISIBLE);
                cardView.setVisibility(View.GONE);

                /** Correct phone number **/
                if (phoneNumber.length() > 10) {
                    int startIndex = phoneNumber.length() - 10;
                    phoneNumber = phoneNumber.substring(startIndex);
                }

                /** Check whether the phone number is already registered or not **/
                /** If already registered don't go to login screen **/
                LoginFirebaseHelper loginFirebaseHelper = new LoginFirebaseHelper();
                loginFirebaseHelper.checkWhetherPhoneNumberAlreadyExists(phoneNumber);
                loginFirebaseHelper.setLoginEvents(new LoginEventsListener(loginFirebaseHelper, phoneNumber));
            }

            @Override
            public void failure(DigitsException exception) {

                /** Failure in phone verification **/
                FLog.d(PhoneNumberVerificationActivity.this, getString(R.string.sign_in_with_digits_failure) + exception);
                showFailureSnackBar();
            }
        });

    }

    /** Displays failure Snack Bar **/
    private void showFailureSnackBar(){

        /** Show snackbar **/
        Snackbar.make(findViewById(R.id.main_container), R.string.verification_failed, Snackbar.LENGTH_LONG).show();
    }

    /** Login Events Listener **/
    class LoginEventsListener implements LoginFirebaseHelper.LoginEvents {

        LoginFirebaseHelper loginFirebaseHelper;
        String phoneNumber;
        String email;
        String password;
        String uid;

        public LoginEventsListener(LoginFirebaseHelper loginFirebaseHelper, String phoneNumber){
            this.phoneNumber = phoneNumber;
            this.loginFirebaseHelper = loginFirebaseHelper;
        }

        @Override
        public void onUserAuthCheck(boolean isLoggedIn, String uid) {}

        @Override
        public void onUserCreated(boolean isSuccessfullyCreated, FirebaseError error) {}

        @Override
        public void onFirstTimeUserLogIn(boolean isSuccessfullyLoggedIn, FirebaseError error) {}

        @Override
        public void onExistingUser(boolean isExisting, String email, String password) {
            if(isExisting){
                FLog.d(PhoneNumberVerificationActivity.this, getString(R.string.existing_phone_number));
                this.email = email;
                this.password = password;
                loginFirebaseHelper.existingLogIn(email, password);
            } else {
                FLog.d(PhoneNumberVerificationActivity.this, getString(R.string.new_phone_number));

                /** Else go to login screen **/
                Intent intent = new Intent(PhoneNumberVerificationActivity.this, LoginActivity.class);
                intent.putExtra(Constants.USER_DATA_KEY, new UserData("", "", phoneNumber));
                startActivity(intent);
                finish();
            }
        }

        @Override
        public void onExistingUserLogIn(boolean isSuccessfullyLoggedIn, String uid, FirebaseError error) {
            if(isSuccessfullyLoggedIn){
                FLog.d(PhoneNumberVerificationActivity.this, getString(R.string.successfully_logged_in_existing_phone_number));
                this.uid = uid;
                loginFirebaseHelper.getNameForExistingUser(uid);
            } else {
                FLog.e(PhoneNumberVerificationActivity.this, getString(R.string.could_not_log_in_existing_phon_number)+error.toString());

                /** Else go to login screen **/
                Intent intent = new Intent(PhoneNumberVerificationActivity.this, LoginActivity.class);
                intent.putExtra(Constants.USER_DATA_KEY, new UserData("", "", phoneNumber));
                startActivity(intent);
                finish();
            }
        }

        @Override
        public void onUserNameFetchedFromExistingUser(String userName) {

            /** Set global elements **/
            FibokuApplication.getInstance().setAgentName(userName);
            FibokuApplication.getInstance().setUid(uid);
            FibokuApplication.getInstance().setPhoneNumber(phoneNumber);
            FibokuApplication.getInstance().setEmail(email);
            FibokuApplication.getInstance().setPassword(password);

            /** Hide few loading related stuff **/
            dotsTextView.setVisibility(View.GONE);
            loadingTextView.setText(String.format(getString(R.string.welcome_back), userName));

            /** Delay a little, the start of new activity **/
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    UploadedBooksFirebaseHelper uploadedBooksFirebaseHelper = new UploadedBooksFirebaseHelper();
                    uploadedBooksFirebaseHelper.fetchAllUploadedBooks();

                    /** Start the main Activity **/
                    Intent intent = new Intent(PhoneNumberVerificationActivity.this, MainScreenActivity.class);
                    PhoneNumberVerificationActivity.this.startActivity(intent);
                    PhoneNumberVerificationActivity.this.finish();
                }
            }, 2500);
        }

        @Override
        public void onUserUploadedBookIdFetchedFromExistingUser(String bId) {}
    }
}
