package com.pabhinav.fiboku.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.firebase.client.FirebaseError;
import com.pabhinav.fiboku.R;
import com.pabhinav.fiboku.application.FibokuApplication;
import com.pabhinav.fiboku.firebase.FirebaseErrorHandler;
import com.pabhinav.fiboku.firebase.LoginFirebaseHelper;
import com.pabhinav.fiboku.models.UserData;
import com.pabhinav.fiboku.util.BundleUtil;
import com.pabhinav.fiboku.util.Constants;
import com.pabhinav.fiboku.util.FLog;
import com.pabhinav.fiboku.util.NetworkUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.tajchert.sample.DotsTextView;

/**
 * This activity helps logging in a user.
 *
 * @author pabhinav
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Input name for user
     */
    @Bind(R.id.text_input_name)
    TextInputLayout nameTextInputLayout;

    /**
     * Email input for user
     */
    @Bind(R.id.text_input_email)
    TextInputLayout emailTextInputLayout;

    /**
     * password input for user
     */
    @Bind(R.id.text_input_password)
    TextInputLayout passwordTextInputLayout;

    /**
     * Edit text for name
     */
    @Bind(R.id.name_edit_text)
    AppCompatEditText nameEditText;

    /**
     * Edit text for email
     */
    @Bind(R.id.email_edit_text)
    AppCompatEditText emailEditText;

    /**
     * Edit text for password
     */
    @Bind(R.id.password_edit_text)
    AppCompatEditText passwordEditText;

    /**
     * Loading view
     */
    @Bind(R.id.loading_view)
    View loadingView;

    /**
     * view pretending as card view background.
     */
    @Bind(R.id.card_view_1)
    View cardView;

    /**
     * loading text view.
     */
    @Bind(R.id.loading_text_view)
    TextView loadingTextView;

    /**
     * {@link DotsTextView} for displaying loading dots.
     */
    @Bind(R.id.dots_text_view)
    DotsTextView dotsTextView;

    /**
     * {@link UserData} object.
     */
    private UserData userData;

    /**
     * Firebase error handling class instance.
     */
    private FirebaseErrorHandler firebaseErrorHandler;

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
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        /**
         * Get UserData From Bundle
         */
        userData = BundleUtil.getUserDataFromBundle(savedInstanceState, getIntent().getExtras(), Constants.USER_DATA_KEY, null);

        /**
         * Firebase error handler
         */
        firebaseErrorHandler = new FirebaseErrorHandler(findViewById(R.id.main_container_relative_layout));

        if(!NetworkUtil.isNetworkConnected(this)){
            Snackbar.make(findViewById(R.id.main_container_relative_layout), getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG).show();
        }
    }

    /** Cancel Clicked **/
    public void cancelClicked(View v){
        super.onBackPressed();
    }

    /**
     * Sign Up Button clicked by user
     *
     * @param v of the button clicked
     */
    public void signUpClicked(View v){

        /** Validate all inputs **/
        if(validateName() && validateEmail() && validatePassword()){

            /** Show Loading view **/
            loadingView.setVisibility(View.VISIBLE);
            cardView.setVisibility(View.GONE);
            
            /** Correct phone number **/
            String phoneNumber = userData.getPhoneNumber();
            if(phoneNumber.length() > 10){
                int startIndex = phoneNumber.length() - 10;
                phoneNumber = phoneNumber.substring(startIndex);
            }
            userData.setPhoneNumber(phoneNumber);
            userData.setEmailId(emailEditText.getText().toString());
            userData.setUserName(nameEditText.getText().toString());
            LoginFirebaseHelper loginFirebaseHelper = new LoginFirebaseHelper();
            loginFirebaseHelper.createNewUser(userData, passwordEditText.getText().toString());
            loginFirebaseHelper.setLoginEvents(new LoginEventListener(loginFirebaseHelper, passwordEditText.getText().toString()));
        }
    }

    /**
     * Validate Email
     *
     * @return true, if it is a valid email, else false.
     */
    private boolean validateEmail(){
        String email = emailEditText.getText().toString();
        if(email.isEmpty()
                || TextUtils.isEmpty(email)
                || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailTextInputLayout.setError(getString(R.string.enter_valid_email_address));
            requestFocus(emailEditText);
            return false;
        } else {
            emailTextInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    /**
     * Validate Password
     *
     * @return true, if it is a valid password, else false
     */
    private boolean validatePassword() {
        if (passwordEditText.getText().toString().trim().isEmpty()) {
            passwordTextInputLayout.setError(getString(R.string.enter_password));
            requestFocus(passwordEditText);
            return false;
        } else {
            passwordTextInputLayout.setErrorEnabled(false);
        }

        return true;
    }

    /**
     * Validate Name
     *
     * @return true, if it is a valid name, else false.
     */
    private boolean validateName() {
        if (nameEditText.getText().toString().trim().isEmpty()) {
            nameTextInputLayout.setError(getString(R.string.enter_your_name));
            requestFocus(nameEditText);
            return false;
        } else {
            nameTextInputLayout.setErrorEnabled(false);
        }

        return true;
    }

    /** Request focus for a view **/
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    /** Login Event Listener class **/
    class LoginEventListener implements LoginFirebaseHelper.LoginEvents{

        LoginFirebaseHelper loginFirebaseHelper;
        String password;

        public LoginEventListener(LoginFirebaseHelper loginFirebaseHelper, String password){
            this.loginFirebaseHelper = loginFirebaseHelper;
            this.password = password;
        }

        @Override
        public void onUserAuthCheck(boolean isLoggedIn, String uid) {}

        @Override
        public void onUserCreated(boolean isSuccessfullyCreated, FirebaseError error) {
            if(isSuccessfullyCreated){
                loginFirebaseHelper.firstLogIn(userData, password);
            } else {
                firebaseErrorHandler.processError(error);

                /** Take back loading view **/
                loadingView.setVisibility(View.GONE);
                cardView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onFirstTimeUserLogIn(boolean isSuccessfullyLoggedIn, FirebaseError error) {
            if(isSuccessfullyLoggedIn){

                /** Set global elements **/
                FibokuApplication.getInstance().setAgentName(userData.getUserName());
                FibokuApplication.getInstance().setEmail(userData.getEmailId());
                FibokuApplication.getInstance().setPassword(password);
                FibokuApplication.getInstance().setPhoneNumber(userData.getPhoneNumber());

                /** Hide few loading related stuff **/
                dotsTextView.setVisibility(View.GONE);
                loadingTextView.setText(String.format(getString(R.string.welcome), userData.getUserName()));

                /** Delay a little, the start of new activity **/
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        /** Start the main Activity **/
                        Intent intent = new Intent(LoginActivity.this, MainScreenActivity.class);
                        LoginActivity.this.startActivity(intent);
                        LoginActivity.this.finish();
                    }
                }, 2500);

            } else {
                firebaseErrorHandler.processError(error);

                /** Take back laoding view **/
                loadingView.setVisibility(View.GONE);
                cardView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onExistingUser(boolean isExisting, String email, String password) {}

        @Override
        public void onExistingUserLogIn(boolean isSuccessfullyLoggedIn, String userName, FirebaseError error) {}

        @Override
        public void onUserNameFetchedFromExistingUser(String userName) {}

        @Override
        public void onUserUploadedBookIdFetchedFromExistingUser(String bId) {}
    }


}
