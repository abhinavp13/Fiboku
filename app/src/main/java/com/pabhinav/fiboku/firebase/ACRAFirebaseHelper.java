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
import java.util.Map;

/**
 * @author pabhinav
 */
public class ACRAFirebaseHelper {

    private Context context;
    private SharedPreferencesMap sharedPreferencesMap;

    public ACRAFirebaseHelper(Context context){
        this.context = context;
        sharedPreferencesMap = new SharedPreferencesMap(context);
    }

    /** Try to send ACRA Map **/
    public void sendAcraMap(final Map<String,String> acraMap){

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
                    myFirebaseRef.child("ACRA " + date).setValue(acraMap, new Firebase.CompletionListener() {

                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                            if (firebaseError != null) {
                                sharedPreferencesMap.saveAcraMap(acraMap);
                            } else {
                                sharedPreferencesMap.removeAcraMap();
                                if (FibokuApplication.getInstance() != null) {
                                    FibokuApplication.getInstance().trackEvent("Firebase", "Error", "Exception caught by ACRA !!!");
                                }
                            }
                        }
                    });

                } else {
                    sharedPreferencesMap.saveAcraMap(acraMap);
                }
            }

            @Override
            public void onCancelled(FirebaseError error) {
                sharedPreferencesMap.saveAcraMap(acraMap);
            }
        });

    }

}
