package com.pabhinav.fiboku.firebase;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.pabhinav.fiboku.BuildConfig;
import com.pabhinav.fiboku.application.FibokuApplication;
import com.pabhinav.fiboku.models.UserData;
import com.pabhinav.fiboku.util.Constants;
import com.pabhinav.fiboku.util.FLog;

import java.util.HashMap;
import java.util.Map;

import hugo.weaving.DebugLog;
import lombok.Setter;

/**
 * @author pabhinav
 */
public class LoginFirebaseHelper {

    @Setter
    LoginEvents loginEvents;
    Firebase firebase;

    public LoginFirebaseHelper(){
        firebase = new Firebase(BuildConfig.FIREBASE_DASHBOARD_LINK);
    }

    @DebugLog
    public void checkAuthentication(){

        /** Check user's logged in status **/
        firebase.addAuthStateListener(new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {

                /** If callback is not present, return **/
                if (loginEvents == null) {
                    return;
                }
                /** authData decides user logged in state **/
                if (authData != null) {
                    loginEvents.onUserAuthCheck(true, authData.getUid());
                } else {
                    loginEvents.onUserAuthCheck(false, "");
                }
            }
        });
    }

    /** Just creates a new user, does not logs him in **/
    @DebugLog
    public void createNewUser(UserData userData, String password){

        /** Create new user **/
        firebase.createUser(userData.getEmailId(), password, new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> stringObjectMap) {
                if (loginEvents != null) {
                    loginEvents.onUserCreated(true, null);
                }
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                if (loginEvents != null) {
                    loginEvents.onUserCreated(false, firebaseError);
                }
            }
        });
    }

    /** Logs in user, also updates user's information to firebase database **/
    @DebugLog
    public void firstLogIn(final UserData userData, final String password){

        /** Log in user **/
        firebase.authWithPassword(userData.getEmailId(), password, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {

                /** global update **/
                FibokuApplication.getInstance().setUid(authData.getUid());

                /** Put data in firebase **/
                Map<String, String> userDataMap = new HashMap<>();
                userDataMap.put(Constants.NAME, userData.getUserName());
                userDataMap.put("Phone Number", userData.getPhoneNumber());
                firebase.child(Constants.USERs).child(authData.getUid()).setValue(userDataMap);

                /** Phone data map **/
                Map<String, String> phoneNumberDataMap = new HashMap<>();

                /** Correct Email **/
                String email = userData.getEmailId();
                email = email.replaceAll("[.]", "_dot_");
                email = email.replaceAll("[@]", "_at_the_rate_");
                phoneNumberDataMap.put(email, password);
                firebase.child(Constants.RPNs).child(userData.getPhoneNumber()).setValue(phoneNumberDataMap);
                if (loginEvents != null) {
                    loginEvents.onFirstTimeUserLogIn(true, null);
                }
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                if (loginEvents != null) {
                    loginEvents.onFirstTimeUserLogIn(false, firebaseError);
                }
            }
        });
    }

    /** Logs in for an existing user  **/
    @DebugLog
    public void existingLogIn(String email, String password){

        /** Log in user **/
        firebase.authWithPassword(email, password, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(final AuthData authData) {
                if(loginEvents != null){
                    loginEvents.onExistingUserLogIn(true, authData.getUid(), null);
                }
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                if (loginEvents != null) {
                    loginEvents.onExistingUserLogIn(false, "", firebaseError);
                }
            }
        });

    }

    /** Get the user name for the existing user **/
    @DebugLog
    public void getNameForExistingUser(String uid){
        final Firebase firebase = new Firebase(BuildConfig.FIREBASE_DASHBOARD_LINK + Constants.USERs + "/" + uid);
        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (loginEvents != null) {
                    loginEvents.onUserNameFetchedFromExistingUser((String) dataSnapshot.child(Constants.NAME).getValue());
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                FLog.d(this, firebaseError.toString());
            }
        });
    }


    /** Checks whether the phone number already exists in database or not **/
    @DebugLog
    public void checkWhetherPhoneNumberAlreadyExists(final String phoneNumber){

        /** Checks whether phone number already exists or not **/
        Firebase firebase = new Firebase(BuildConfig.FIREBASE_DASHBOARD_LINK + Constants.RPNs);
        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(loginEvents == null){
                    return;
                }

                /** If phone number already exists, we have an existing user **/
                if(dataSnapshot.hasChild(phoneNumber)){
                    HashMap<String, String> emailPassMap = (HashMap < String, String >)dataSnapshot.child(phoneNumber).getValue();
                    for(Map.Entry<String,String> entry : emailPassMap.entrySet()){
                        String email = entry.getKey();
                        email = email.replaceAll("_dot_",".");
                        email = email.replaceAll("_at_the_rate_", "@");
                        loginEvents.onExistingUser(true, email, entry.getValue());
                    }
                } else {
                    loginEvents.onExistingUser(false, "", "");
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                loginEvents.onExistingUser(false, "", "");
            }
        });
    }

    /** Callback events from firebase functions **/
    public interface LoginEvents{
        void onUserAuthCheck(boolean isLoggedIn, String uid);
        void onUserCreated(boolean isSuccessfullyCreated, FirebaseError error);
        void onFirstTimeUserLogIn(boolean isSuccessfullyLoggedIn, FirebaseError error);
        void onExistingUser(boolean isExisting, String email, String password);
        void onExistingUserLogIn(boolean isSuccessfullyLoggedIn, String uid ,FirebaseError error);
        void onUserNameFetchedFromExistingUser(String userName);
        void onUserUploadedBookIdFetchedFromExistingUser(String bId);
    }
}
