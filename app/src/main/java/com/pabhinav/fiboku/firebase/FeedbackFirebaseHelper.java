package com.pabhinav.fiboku.firebase;

import android.content.Context;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.pabhinav.fiboku.BuildConfig;
import com.pabhinav.fiboku.application.FibokuApplication;
import com.pabhinav.fiboku.preferences.SharedPreferencesMap;

import java.util.Date;

/**
 * @author pabhinav
 */
public class FeedbackFirebaseHelper {

    private Context context;
    private SharedPreferencesMap sharedPreferencesMap;

    public FeedbackFirebaseHelper(Context context){
        this.context = context;
        sharedPreferencesMap = new SharedPreferencesMap(context);
    }

    /** Try to send feedback **/
    public void sendFeedback(final String feedback){

        /** Firebase linked **/
        final Firebase myFirebaseRef = new Firebase(BuildConfig.FIREBASE_DASHBOARD_LINK);

        /** Need to know firebase connection presence **/
        Firebase connectedRef = new Firebase(BuildConfig.FIREBASE_DASHBOARD_LINK + ".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {

                    Date date = new Date(System.currentTimeMillis());
                    myFirebaseRef.child("Feedback " + date).setValue(feedback, new Firebase.CompletionListener() {

                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                            if (firebaseError != null) {
                                sharedPreferencesMap.saveFeedback(feedback);
                            } else {
                                sharedPreferencesMap.removeFeedback();
                                if(FibokuApplication.getInstance() != null) {
                                    FibokuApplication.getInstance().trackEvent("Firebase", "Feedback", "Feedback was sent by user");
                                }
                            }
                        }
                    });

                } else {
                    sharedPreferencesMap.saveFeedback(feedback);
                }
            }

            @Override
            public void onCancelled(FirebaseError error) {
                sharedPreferencesMap.saveFeedback(feedback);
            }
        });

    }

}
