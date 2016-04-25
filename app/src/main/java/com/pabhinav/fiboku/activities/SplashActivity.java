package com.pabhinav.fiboku.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.firebase.client.FirebaseError;
import com.pabhinav.fiboku.R;
import com.pabhinav.fiboku.application.FibokuApplication;
import com.pabhinav.fiboku.firebase.LoginFirebaseHelper;
import com.pabhinav.fiboku.fragment.LogoFragment;
import com.pabhinav.fiboku.util.FLog;

/**
 * Splash displaying activity
 *
 * @author pabhinav
 */
public class SplashActivity extends AppCompatActivity {

    /**
     * Animated logo starting offset
     */
    private static final long ANIMATED_LOGO_STARTOFFSET = 500;

    /**
     * Animated Login starting offset
     */
    private static final long ANIMATED_LOGIN_STARTOFFSET = 3500;

    /**
     * {@link LogoFragment} displays animated logo
     */
    private LogoFragment logoFragment;

    /**
     * Handler for logo animation
     */
    private Handler logoHandler;

    /**
     * Runnable for handler for logo animation
     */
    private Runnable logoRunnable;

    /**
     * handler for login activity startup
     */
    private Handler loginHandler;

    /**
     * Runnable for handler for login activity startup
     */
    private Runnable loginRunnable;

    /**
     * Listening to login events
     */
    private LoginEventListener loginEventListener;

    /**
     * Boolean value denoting activity's lifecycle end
     */
    private static boolean isDestroyed;

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
        setContentView(R.layout.activity_splash);

        setupAnimatedLogo();
        isDestroyed = false;
    }


    /**
     * Initializes animated logo fragment for {@link android.graphics.DashPathEffect} animation.
     * It uses svg glyph data to fetch {@link android.graphics.Path} object, which helps
     * render logo text on screen.
     */
    public void setupAnimatedLogo(){

        /** Animated Logo Fragment initialization **/
        logoFragment = (LogoFragment) getFragmentManager().findFragmentById(R.id.animated_logo_fragment);
        logoFragment.reset();
        logoHandler = new Handler();
        logoRunnable = new Runnable() {
            @Override
            public void run() {

                /** Start the animation **/
                logoFragment.start();
            }
        };
        logoHandler.postDelayed(logoRunnable, ANIMATED_LOGO_STARTOFFSET);


        loginHandler = new Handler();
        loginRunnable = new Runnable() {
            @Override
            public void run() {

                /** Check user login **/
                LoginFirebaseHelper loginFirebaseHelper = new LoginFirebaseHelper();
                loginFirebaseHelper.checkAuthentication();
                loginEventListener = new LoginEventListener(loginFirebaseHelper);
                loginFirebaseHelper.setLoginEvents(loginEventListener);
            }
        };
        loginHandler.postDelayed(loginRunnable, ANIMATED_LOGIN_STARTOFFSET);
    }


    /** Listening login events **/
    class LoginEventListener implements LoginFirebaseHelper.LoginEvents{
        
        private LoginFirebaseHelper loginFirebaseHelper;
        
        public LoginEventListener(LoginFirebaseHelper loginFirebaseHelper){
            this.loginFirebaseHelper = loginFirebaseHelper;    
        }

        @Override
        public void onUserAuthCheck(boolean isLoggedIn, String uid) {
            
            /** Auth check done, check whether user is already logged in or not **/
            if(isLoggedIn){

                /** Already logged in **/

                if(!isDestroyed) {

                    /** Start the main Activity **/
                    Intent intent = new Intent(SplashActivity.this, MainScreenActivity.class);
                    SplashActivity.this.startActivity(intent);
                }
                SplashActivity.this.finish();
                
            } else {

                /** Not logged in **/

                String email = FibokuApplication.getInstance().getEmail();
                String password = FibokuApplication.getInstance().getPassword();
                if(email == null 
                        || password == null
                        || email.length() == 0
                        || password.length() == 0){
                    
                    /** Its first login **/
                    Intent intent = new Intent(SplashActivity.this, PhoneNumberVerificationActivity.class);
                    SplashActivity.this.startActivity(intent);
                    SplashActivity.this.finish();

                } else {

                    /** There has been a token expiry **/
                    loginFirebaseHelper.existingLogIn(email, password);
                }
            }
        }

        @Override
        public void onUserCreated(boolean isSuccessfullyCreated, FirebaseError error) {

        }

        @Override
        public void onFirstTimeUserLogIn(boolean isSuccessfullyLoggedIn, FirebaseError error) {

        }

        @Override
        public void onExistingUser(boolean isExisting, String email, String password) {

        }

        @Override
        public void onExistingUserLogIn(boolean isSuccessfullyLoggedIn, String uid, FirebaseError error) {

            if(isSuccessfullyLoggedIn){


                /** Start the main Activity **/
                Intent intent = new Intent(SplashActivity.this, MainScreenActivity.class);
                SplashActivity.this.startActivity(intent);
                SplashActivity.this.finish();

            } else {


                /** Its first login **/
                Intent intent = new Intent(SplashActivity.this, PhoneNumberVerificationActivity.class);
                SplashActivity.this.startActivity(intent);
                SplashActivity.this.finish();
            }
        }

        @Override
        public void onUserNameFetchedFromExistingUser(String userName) {

        }

        @Override
        public void onUserUploadedBookIdFetchedFromExistingUser(String bId) {

        }
    }

    public void onDestroy(){
        logoHandler.removeCallbacks(logoRunnable);
        loginHandler.removeCallbacks(loginRunnable);
        loginEventListener = null;
        isDestroyed = true;
        super.onDestroy();
    }
}
