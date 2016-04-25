package com.pabhinav.fiboku.firebase;

import android.text.TextUtils;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.pabhinav.fiboku.BuildConfig;
import com.pabhinav.fiboku.application.FibokuApplication;
import com.pabhinav.fiboku.models.SearchedBook;
import com.pabhinav.fiboku.models.UploadedBook;
import com.pabhinav.fiboku.util.BookUtil;
import com.pabhinav.fiboku.util.Constants;
import com.pabhinav.fiboku.util.FLog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import lombok.Setter;

/**
 * @author pabhinav
 */
public class UploadedBooksFirebaseHelper {

    @Setter
    UploadedBookFirebaseEvents uploadedBookFirebaseEvents;

    /**
     * This method updates and insert data in the firebase database model
     * Given a uploaded book domain object, it saves it in json format in database.
     *
     * @param uploadedBook {@link UploadedBook} domain object.
     */
    public void uploadBook(final UploadedBook uploadedBook){

        final Firebase firebase  = new Firebase(BuildConfig.FIREBASE_DASHBOARD_LINK + Constants.BOOKs);
        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String title = uploadedBook.getSearchedBook().getTitle();
                final String subTitle = uploadedBook.getSearchedBook().getSubTitle();
                final String authors = TextUtils.join(",", uploadedBook.getSearchedBook().getAuthors());
                final String publishers = uploadedBook.getSearchedBook().getPublisher();
                final String publishingDate = uploadedBook.getSearchedBook().getPublishedDate();
                final String description = uploadedBook.getSearchedBook().getDescription();
                final String industryIdentifier = uploadedBook.getSearchedBook().getIndustryIdentifier();
                final String thumbnailLink = uploadedBook.getSearchedBook().getThumbnailLink();
                final String condition  = BookUtil.getStringForBookConditionEnum(uploadedBook.getCondition());
                final String conditionDescription = uploadedBook.getConditionDescription();
                final String uploadedTimeStamp = uploadedBook.getUploadTimestamp();

                HashMap<String, String> map = new HashMap<String, String>() {{
                    put("subtitle",subTitle);
                    put("authors", authors);
                    put("publisher", publishers);
                    put("publishingDate", publishingDate);
                    put("description", description);
                    put("industryIdentifier", industryIdentifier);
                    put("thumbnailLink", thumbnailLink);
                    put("condition", condition);
                    put("conditionDescription", conditionDescription);
                    put("uploadedTimestamp", uploadedTimeStamp);
                }};


                firebase.child(FibokuApplication.getInstance().getUid()).child(title).setValue(map);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                FLog.d(this, firebaseError.toString());
            }
        });
    }


    /**
     * Deletes data in firebase real time database,
     * Firebase guarantees deletion even when user is offline,
     * it maintains an offline copy and as soon as user appears online,
     * it makes data consistent with online database.
     *
     * @param uploadedBook {@link UploadedBook} domain object model
     */
    public void deleteBook(UploadedBook uploadedBook){
        final Firebase firebase  = new Firebase(BuildConfig.FIREBASE_DASHBOARD_LINK + Constants.BOOKs);
        firebase.child(FibokuApplication.getInstance().getUid()).child(uploadedBook.getSearchedBook().getTitle()).setValue(null);
    }

    /**
     * This method fetches all uploaded books present in firebase database.
     * <br/>
     * This is called once in the beginning, when app is newly installed
     * and user is already registered.
     * Since, user is identified by phone number. So, if already registered
     * phone number is found, all her uploaded books are in sync with
     * online database.
     * <br/>
     */
    public void fetchAllUploadedBooks(){
        Firebase firebase = new Firebase(BuildConfig.FIREBASE_DASHBOARD_LINK + Constants.BOOKs + "/" + FibokuApplication.getInstance().getUid());
        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<UploadedBook> uploadedBooklist = new ArrayList<UploadedBook>();
                HashMap<String, ?> map = (HashMap < String,?>)dataSnapshot.getValue();
                for (Map.Entry<String, ?> bookDataEntry : map.entrySet()) {
                    String bookTitle = bookDataEntry.getKey();
                    HashMap<String, String> bookDataMap = (HashMap<String,String>)bookDataEntry.getValue();
                    UploadedBook uploadedBook = new UploadedBook.UploadedBookBuilder()
                            .addCondition(BookUtil.getBookConditionEnumFromString(bookDataMap.get("condition")))
                            .addBookImage(null)
                            .addConditionDescription(bookDataMap.get("conditionDescription"))
                            .addUploadTimestamp("uploadedTimestamp")
                            .addSearchedBook(new SearchedBook.SearchedBookBuilder()
                                            .addTitle(bookTitle)
                                            .addAuthors(new ArrayList<String>(Arrays.asList(bookDataMap.get("authors").split(" , "))))
                                            .addSubtitle(bookDataMap.get("subtitle"))
                                            .addPublisher(bookDataMap.get("publisher"))
                                            .addPublishedDate(bookDataMap.get("publishingDate"))
                                            .addDescription(bookDataMap.get("description"))
                                            .addIndustryIdentifier(bookDataMap.get("industryIdentifier"))
                                            .addThumbnailLink(bookDataMap.get("thumbnailLink"))
                                            .build()
                            ).build();
                    uploadedBooklist.add(uploadedBook);
                }
                if(uploadedBookFirebaseEvents != null){
                    uploadedBookFirebaseEvents.onFetchAllUploadedBooks(uploadedBooklist);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });
    }

    public interface UploadedBookFirebaseEvents{
        void onFetchAllUploadedBooks(ArrayList<UploadedBook> uploadedBookArrayList);
    }
}
