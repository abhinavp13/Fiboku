package com.pabhinav.fiboku.application;

import android.app.Application;

import com.digits.sdk.android.Digits;
import com.firebase.client.Firebase;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;
import com.pabhinav.fiboku.BuildConfig;
import com.pabhinav.fiboku.R;
import com.pabhinav.fiboku.acra.ACRAReportSender;
import com.pabhinav.fiboku.googleanalytics.AnalyticsTrackers;
import com.pabhinav.fiboku.preferences.SharedPreferencesMap;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import hugo.weaving.DebugLog;
import io.fabric.sdk.android.Fabric;

/**
 * @author pabhinav
 */
@ReportsCrashes(
        formUri = "",
        mode = ReportingInteractionMode.DIALOG,
        resDialogText = R.string.crash_dialog_text,
        resDialogIcon = android.R.drawable.ic_dialog_info, //optional. default is a warning sign
        resDialogTitle = R.string.crash_dialog_title, // optional. default is your application name
        resDialogCommentPrompt = R.string.crash_dialog_comment_prompt, // optional. When defined, adds a user text field input with this text resource as a label
        resDialogOkToast = R.string.crash_dialog_ok_toast// optional. displays a Toast message when the user accepts to send a report.
)
public class FibokuApplication extends Application {

    private static FibokuApplication mInstance;
    private  String agentName;
    private String email;
    private String password;
    private String phoneNumber;
    private String uId;

    @Override
    public void onCreate() {

        /** Initialize ACRA error reports sending library **/
        try {
            ACRA.init(this);
            ACRAReportSender yourSender = new ACRAReportSender();
            ACRA.getErrorReporter().setReportSender(yourSender);
        } catch (Throwable ignored){ /** Nothing can be done at such early stage **/}

        super.onCreate();
        TwitterAuthConfig authConfig = new TwitterAuthConfig(BuildConfig.TWITTER_KEY, BuildConfig.TWITTER_SECRET);
        Fabric.with(this, new TwitterCore(authConfig), new Digits());
        mInstance = this;

        /** Initialize firebase **/
        Firebase.setAndroidContext(this);

        try {
            /** Google Analytics **/
            AnalyticsTrackers.initialize(this);
            AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
        } catch (Throwable ignored){/** Nothing can be done at such early stage **/}
    }

    /** Get the application instance **/
    public static synchronized FibokuApplication getInstance() {
        return mInstance;
    }


    /**************************************************************************************
     ********************************** Google Analytics **********************************
     **************************************************************************************/

    /** Get google analytics tracker instance **/
    public synchronized Tracker getGoogleAnalyticsTracker() {
        try {
            AnalyticsTrackers analyticsTrackers = AnalyticsTrackers.getInstance();
            return analyticsTrackers.get(AnalyticsTrackers.Target.APP);
        }catch (Throwable ignored){
            return null;
        }
    }

    /***
     * Tracking screen view
     *
     * @param screenName screen name to be displayed on GA dashboard
     */
    public void trackScreenView(String screenName) {
        try {
            Tracker t = getGoogleAnalyticsTracker();

            // Set screen name.
            t.setScreenName(screenName);

            // Send a screen view.
            t.send(new HitBuilders.ScreenViewBuilder().build());

            GoogleAnalytics.getInstance(this).dispatchLocalHits();
        } catch (Throwable ignored){}
    }

    /***
     * Tracking exception
     *
     * @param e exception to be tracked
     */
    public void trackException(Exception e) {
        try {
            if (e != null) {
                Tracker t = getGoogleAnalyticsTracker();

                t.send(new HitBuilders.ExceptionBuilder()
                                .setDescription(
                                        new StandardExceptionParser(this, null)
                                                .getDescription(Thread.currentThread().getName(), e))
                                .setFatal(false)
                                .build()
                );
            }
        }catch (Throwable ignored){}
    }

    /***
     * Tracking event
     *
     * @param category event category
     * @param action   action of the event
     * @param label    label
     */
    public void trackEvent(String category, String action, String label) {
        try {
            Tracker t = getGoogleAnalyticsTracker();

            // Build and send an Event.
            t.send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel(label).build());
        } catch (Throwable ignored){}
    }



    /**************************************************************************************
     ********************************** Agent Name ****************************************
     **************************************************************************************/

    /** Get the agent name **/
    @DebugLog
    public String getAgentName(){
        if(agentName == null || agentName.length() == 0){
            SharedPreferencesMap sharedPreferencesMap = new SharedPreferencesMap(this);
            agentName = sharedPreferencesMap.loadAgentName();
        }
        return agentName;
    }

    /** Sets the agent name **/
    @DebugLog
    public void setAgentName(String agentName){
        this.agentName = agentName;

        /** Save agent name permanently **/
        SharedPreferencesMap sharedPreferencesMap = new SharedPreferencesMap(this);
        sharedPreferencesMap.saveAgentName(agentName);
    }


    /**************************************************************************************
     ********************************** Email  ********************************************
     **************************************************************************************/

    /** Get the email **/
    @DebugLog
    public String getEmail(){
        if(email == null || email.length() == 0){
            SharedPreferencesMap sharedPreferencesMap = new SharedPreferencesMap(this);
            email = sharedPreferencesMap.loadEmail();
        }
        return email;
    }

    /** Sets the email **/
    @DebugLog
    public void setEmail(String email){
        this.email = email;

        /** Save email permanently **/
        SharedPreferencesMap sharedPreferencesMap = new SharedPreferencesMap(this);
        sharedPreferencesMap.saveEmail(email);
    }

    /**************************************************************************************
     ********************************** Password ******************************************
     **************************************************************************************/

    /** Get the password **/
    @DebugLog
    public String getPassword(){
        if(password == null || password.length() == 0){
            SharedPreferencesMap sharedPreferencesMap = new SharedPreferencesMap(this);
            password = sharedPreferencesMap.loadPassword();
        }
        return password;
    }

    /** Sets the password **/
    @DebugLog
    public void setPassword(String password){
        this.password = password;

        /** Save password permanently **/
        SharedPreferencesMap sharedPreferencesMap = new SharedPreferencesMap(this);
        sharedPreferencesMap.savePassword(password);
    }

    /**************************************************************************************
     ********************************** Phone Number **************************************
     **************************************************************************************/

    /** Get the phoneNumber **/
    @DebugLog
    public String getPhoneNumber(){
        if(phoneNumber == null || phoneNumber.length() == 0){
            SharedPreferencesMap sharedPreferencesMap = new SharedPreferencesMap(this);
            phoneNumber = sharedPreferencesMap.loadPhoneNumber();
        }
        return phoneNumber;
    }

    /** Sets the phoneNumber **/
    @DebugLog
    public void setPhoneNumber(String phoneNumber){
        this.phoneNumber = phoneNumber;

        /** Save phoneNumber name permanently **/
        SharedPreferencesMap sharedPreferencesMap = new SharedPreferencesMap(this);
        sharedPreferencesMap.savePhoneNumber(phoneNumber);
    }

    /**************************************************************************************
     ********************************** Uid ***********************************************
     **************************************************************************************/

    /** Get the uId **/
    @DebugLog
    public String getUid(){
        if(uId == null || uId.length() == 0){
            SharedPreferencesMap sharedPreferencesMap = new SharedPreferencesMap(this);
            uId = sharedPreferencesMap.loadUid();
        }
        return uId;
    }

    /** Sets the uId **/
    @DebugLog
    public void setUid(String uId){
        this.uId = uId;

        /** Save uId name permanently **/
        SharedPreferencesMap sharedPreferencesMap = new SharedPreferencesMap(this);
        sharedPreferencesMap.saveUid(uId);
    }
}
