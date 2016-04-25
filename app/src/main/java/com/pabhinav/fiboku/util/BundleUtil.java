package com.pabhinav.fiboku.util;

import android.os.Bundle;

import com.pabhinav.fiboku.models.SearchedBook;
import com.pabhinav.fiboku.models.UploadedBook;
import com.pabhinav.fiboku.models.UserData;

/**
 * @author pabhinav
 */
public class BundleUtil {

    /** Returns string value from initialized bundle **/
    public static String getStringFromBundle(Bundle savedInstanceState, Bundle intentExtras, String token, String defaultValue){

        String returnValue = null;
        if(intentExtras != null){
            returnValue = (String) intentExtras.getString(token);
        }
        if(returnValue != null){
            return returnValue;
        }
        if (savedInstanceState != null) {
            returnValue = (String) savedInstanceState.getSerializable(token);
        }
        if(returnValue != null){
            return returnValue;
        }
        returnValue = defaultValue;
        return returnValue;
    }

    /** Returns string value from initialized bundle **/
    public static int getIntFromBundle(Bundle savedInstanceState, Bundle intentExtras, String token, int defaultValue){

        Integer returnValue = null;
        if(intentExtras != null){
            returnValue = (Integer) intentExtras.getInt(token);
        }
        if(returnValue != null){
            return returnValue;
        }
        if (savedInstanceState != null) {
            returnValue = (Integer) savedInstanceState.getSerializable(token);
        }
        if(returnValue != null){
            return returnValue;
        }
        returnValue = defaultValue;
        return returnValue;
    }

    /** Returns boolean value from initialized bundle **/
    public static boolean getBooleanFromBundle(Bundle savedInstanceState, Bundle intentExtras, String token, boolean defaultValue){

        Boolean returnValue = null;
        if(intentExtras != null){
            returnValue = (boolean) intentExtras.getBoolean(token);
        }
        if(returnValue != null){
            return returnValue;
        }
        if (savedInstanceState != null) {
            returnValue = (boolean) savedInstanceState.getSerializable(token);
        }
        if(returnValue != null){
            return returnValue;
        }
        returnValue = defaultValue;
        return returnValue;
    }

    /** Returns int value from initialized bundle **/
    public static Long getLongFromBundle(Bundle savedInstanceState, Bundle intentExtras, String token, Long defaultValue){

        Long returnValue = null;
        if(intentExtras != null){
            returnValue = (Long) intentExtras.getLong(token);
        }
        if(returnValue != null){
            return returnValue;
        }
        if (savedInstanceState != null) {
            returnValue = (Long) savedInstanceState.getSerializable(token);
        }
        if(returnValue != null){
            return returnValue;
        }
        returnValue = defaultValue;
        return returnValue;
    }

    /** Get UserData model object from bundle instance **/
    public static UserData getUserDataFromBundle(Bundle savedInstanceState, Bundle intentExtras, String token, UserData defaultValue){

        UserData userData = null;
        if (savedInstanceState != null) {
            userData = (UserData) savedInstanceState.getParcelable(token);
        }
        if(userData != null){
            return userData;
        }
        if(intentExtras != null){
            userData = (UserData) intentExtras.getParcelable(token);
        }
        if(userData != null){
            return userData;
        }
        userData = defaultValue;
        return userData;
    }

    /** Get Searched Book From Bundle object **/
    public static SearchedBook getSearchedBookFromBundle(Bundle savedInstanceState, Bundle intentExtras, String token, SearchedBook defaultValue){

        SearchedBook searchedBook = null;
        if (savedInstanceState != null) {
            searchedBook = (SearchedBook) savedInstanceState.getParcelable(token);
        }
        if(searchedBook != null){
            return searchedBook;
        }
        if(intentExtras != null){
            searchedBook = (SearchedBook) intentExtras.getParcelable(token);
        }
        if(searchedBook != null){
            return searchedBook;
        }
        searchedBook = defaultValue;
        return searchedBook;
    }

    /** Get Uploaded Book from Bundle object **/
    public static UploadedBook getUploadedBookFromBundle(Bundle savedInstanceState, Bundle intentExtras, String token, UploadedBook defaultValue){

        UploadedBook uploadedBook = null;
        if (savedInstanceState != null) {
            uploadedBook = (UploadedBook) savedInstanceState.getParcelable(token);
        }
        if(uploadedBook != null){
            return uploadedBook;
        }
        if(intentExtras != null){
            uploadedBook = (UploadedBook) intentExtras.getParcelable(token);
        }
        if(uploadedBook != null){
            return uploadedBook;
        }
        uploadedBook = defaultValue;
        return uploadedBook;
    }

    /** Get byte array from bundle **/
    public static byte[] getByteArrayFromBundle(Bundle savedInstanceState, Bundle intentExtras, String token, byte[] defaultValue){

        byte[] returnValue = null;
        if(intentExtras != null){
            returnValue = (byte[]) intentExtras.getByteArray(token);
        }
        if(returnValue != null){
            return returnValue;
        }
        if (savedInstanceState != null) {
            returnValue = (byte[]) savedInstanceState.getByteArray(token);
        }
        if(returnValue != null){
            return returnValue;
        }
        returnValue = defaultValue;
        return returnValue;
        
    }
}
