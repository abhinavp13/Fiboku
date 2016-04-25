package com.pabhinav.fiboku.firebase;

import android.support.design.widget.Snackbar;
import android.view.View;

import com.firebase.client.FirebaseError;
import com.pabhinav.fiboku.util.FLog;

/**
 * @author pabhinav
 */
public class FirebaseErrorHandler {

    View rootView;

    public FirebaseErrorHandler(View rootView){
        this.rootView = rootView;
    }

    public void processError(FirebaseError firebaseError){

        /** Catch the type of exception **/
        switch(firebaseError.getCode()){
            case FirebaseError.EMAIL_TAKEN:
                showSnackBar("Email id already exists");
                break;
            case FirebaseError.INVALID_CREDENTIALS:
                showSnackBar("Username/Password is incorrect");
                break;
            case FirebaseError.INVALID_EMAIL:
                showSnackBar("Invalid Email");
                break;
            case FirebaseError.INVALID_PASSWORD:
                showSnackBar("Invalid Password");
                break;
            case FirebaseError.NETWORK_ERROR:
                showSnackBar("Please check your internet connection");
                break;
            case FirebaseError.USER_DOES_NOT_EXIST:
                showSnackBar("Username does not exists");
                break;
            default:
                showSnackBar("Unknown error occurred, please try again !");
        }

        FLog.e(this, firebaseError.toString());
    }

    private void showSnackBar(String mssg){
        Snackbar.make(rootView, mssg, Snackbar.LENGTH_LONG).show();
    }
}
