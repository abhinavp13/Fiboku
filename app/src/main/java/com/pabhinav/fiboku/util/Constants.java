package com.pabhinav.fiboku.util;

/**
 * @author pabhinav
 */
public class Constants {

    /** Book Api related constants **/
    public static final String BOOK_API_RESPONSE_ITEMS = "items";
    public static final String BOOK_API_RESPONSE_VOLUME_INFO = "volumeInfo";
    public static final String BOOK_API_RESPONSE_TITLE = "title";
    public static final String BOOK_API_RESPONSE_AUTHORS = "authors";
    public static final String BOOK_API_RESPONSE_SUBTITLE = "subtitle";
    public static final String BOOK_API_RESPONSE_PUBLISHER = "publisher";
    public static final String BOOK_API_RESPONSE_PUBLISHED_DATE = "publishedDate";
    public static final String BOOK_API_RESPONSE_INDUSTRY_IDENTIFIER = "industryIdentifiers";
    public static final String BOOK_API_RESPONSE_INDUSTRY_IDENTIFIER_TYPE = "type";
    public static final String BOOK_API_RESPONSE_INDUSTRY_IDENTIFIER_IDENTIFIER = "identifier";
    public static final String BOOK_API_RESPONSE_DESCRIPTION= "description";
    public static final String BOOK_API_RESPONSE_ISBN_13 = "ISBN_13";
    public static final String BOOK_API_RESPONSE_IMAGELINKS = "imageLinks";
    public static final String BOOK_API_RESPONSE_IMAGELINKS_SMALLTHUMBNAIL = "smallThumbnail";
    public static final String BOOK_API_RESPONSE_IMAGELINKS_THUMBNAIL = "thumbnail";
    public static final String BOOK_API_RESPONSE_IMAGELINKS_SMALL = "small";
    public static final String BOOK_API_RESPONSE_IMAGELINKS_MEDIUM = "medium";
    public static final String BOOK_API_RESPONSE_IMAGELINKS_LARGE = "large";
    public static final String BOOK_API_RESPONSE_IMAGELINKS_XLARGE = "extraLarge";
    public static final String BOOK_API_RESPONSE_KIND = "kind";

    /** Layout Manager key for parcel object **/
    public static final String SAVED_LAYOUT_MANAGER_KEY = "SAVED_LAYOUT_MANAGER_KEY";

    /** Firebase helper constants **/
    public static final String feedbackPrefs = "Fiboku.feedbackPrefs";
    public static final String acraPrefs = "Fiboku.acraPrefs";
    public static final String feedbackMap = "FEEBACKMAP";
    public static final String acraMap = "ACRAMAP";
    public static final String agentName = "AGENTNAME";
    public static final String agentPrefs = "Fiboku.agentPrefs";
    public static final String emailPrefs = "Fiboku.emailPrefs";
    public static final String emailName = "EMAILNAME";
    public static final String passwordPrefs = "Fiboku.passwordPrefs";
    public static final String passwordName = "PASSWORDNAME";
    public static final String phoneNumberPrefs = "Fiboku.phoneNumberPrefs";
    public static final String phoneNumberName = "PHONENUMBERNAME";
    public static final String uidPrefs = "Fiboku.uidPrefs";
    public static final String uidName = "UIDNAME";
    public static final String RPNs = "RPNs";
    public static final String BOOKs = "books";
    public static final String USERs = "users";
    public static final String NAME = "Name";
    public static final String MESSAGES = "Messages";

    /** Intent Keys **/
    public static final String USER_DATA_KEY = "USERDATAKEY";
    public static final String ISBN_KEY = "ISBN_KEY";
    public static final String IS_FROM_UPLOAD = "IS_FROM_UPLOAD";
    public static final int RESULT_OK = 9;
    public static final int RESULT_FAILURE = 8;
    public static final int RESULT_CANCELLED = 7;
    public static final int ISBN_REQUEST = 6;
    public static final int REQUEST_CODE_SOME_FEATURES_PERMISSIONS = 5;
    public static final String SEARCHED_BOOK_KEY = "SEARCHED_BOOK_KEY";
    public static final String UPLOADED_BOOK_KEY = "UPLOADED_BOOK_KEY";
    public static final String BOOK_IMAGE_KEY = "BOOK_IMAGE_KEY";
    public static final int CLOSE_ACTIVITY = 0;
    public static final String UPDATE_REQUIRED = "UPDATE_REQUIRED";
    public static final String BOOK_ID = "BOOK_ID";
    public static final String MESSSAGE_PHONE_NUMBER = "MESSAGE_PHONE_NUMBER";
    public static final String SEARCHED_BOOK_TITLE = "SEARCHED_BOOK_TITLE";

    /** Activity ids **/
    public static final int homeActivity = 0;
    public static final int uploadedBooksActivity = 1;
    public static final int messagesActivity = 2;
    public static final int connectionsActivity = 3;
    public static final int feedbackActivity = 4;
    public static final int aboutActivity = 5;
    
    /** Drawer item ids **/
    public static final int homeDrawerItemCode = 0;
    public static final int uploadedBooksDrawerItemCode = 1;
    public static final int messagesDrawerItemCode = 2;
    public static final int connectionsDrawerItemCode = 3;
    public static final int feedbackDrawerItemCode = 4;
    public static final int aboutDrawerItemCode = 5;
}
