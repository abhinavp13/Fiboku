package com.pabhinav.fiboku.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.pabhinav.fiboku.firebase.ACRAFirebaseHelper;
import com.pabhinav.fiboku.firebase.FeedbackFirebaseHelper;
import com.pabhinav.fiboku.preferences.SharedPreferencesMap;
import com.pabhinav.fiboku.util.NetworkUtil;

import java.util.Map;

/**
 * @author pabhinav
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        /** Check for internet connectivity **/
        if(NetworkUtil.isNetworkConnected(context)) {

            /** Get shared preferences instance **/
            SharedPreferencesMap sharedPreferencesMap = new SharedPreferencesMap(context);

            /** Report any stale acra crash reports **/
            ACRAFirebaseHelper acraFirebaseHelper = new ACRAFirebaseHelper(context);
            Map<String, String> acraMap = sharedPreferencesMap.loadAcraMap();

            if(acraMap != null && acraMap.size() > 0){

                /** Need to call Firebase to set value **/
                acraFirebaseHelper.sendAcraMap(acraMap);
            }

            /** Report any stale feedback **/
            FeedbackFirebaseHelper feedbackFirebaseHelper = new FeedbackFirebaseHelper(context);
            String feedback = sharedPreferencesMap.loadFeedback();

            if(feedback != null){

                /** Need to call Firebase to set value **/
                feedbackFirebaseHelper.sendFeedback(feedback);
            }
        }
    }
}
