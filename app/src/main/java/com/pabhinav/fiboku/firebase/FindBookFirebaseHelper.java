package com.pabhinav.fiboku.firebase;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.pabhinav.fiboku.BuildConfig;
import com.pabhinav.fiboku.models.FindBookMetaData;
import com.pabhinav.fiboku.util.Constants;
import com.pabhinav.fiboku.util.FLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import lombok.Setter;

/**
 * @author pabhinav
 */
public class FindBookFirebaseHelper {

    @Setter
    FindBookFirebaseEvents findBookFirebaseEvents;
    @Setter
    FindBookFirebaseCompletionEvents findBookFirebaseCompletionEvents;

    public void findBook(final String searchBookTitle, final FindBookMetaData findBookMetaData){

        String bId = findBookMetaData.getBId();
        Firebase firebase = new Firebase(BuildConfig.FIREBASE_DASHBOARD_LINK + Constants.BOOKs + "/" + bId);
        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, ?> map = (HashMap < String,?>)dataSnapshot.getValue();
                if(map == null
                        || map.size() == 0){
                    if(findBookFirebaseCompletionEvents != null){
                        findBookFirebaseCompletionEvents.onSearchComplete(false, null);
                    }
                    return;
                }
                for (Map.Entry<String, ?> bookDataEntry : map.entrySet()) {
                    if(bookDataEntry.getKey() == null){
                        continue;
                    }
                    String bookTitle = bookDataEntry.getKey();
                    if(bookTitle.equals(searchBookTitle)){
                        if(findBookFirebaseCompletionEvents != null){
                            findBookFirebaseCompletionEvents.onSearchComplete(true, findBookMetaData);
                            return;
                        }
                    }
                }
                if(findBookFirebaseCompletionEvents != null) {
                    findBookFirebaseCompletionEvents.onSearchComplete(false, null);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });
    }


    public void completeFindBookMetaDataList(final ArrayList<FindBookMetaData> findBookMetaDataArrayList){

        Firebase firebase = new Firebase(BuildConfig.FIREBASE_DASHBOARD_LINK + Constants.USERs);
        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                HashMap<String, String> numberbIdMap = new HashMap<String, String>();
                HashMap<String, ?> map = (HashMap < String, ?>)dataSnapshot.getValue();
                if(map == null
                        || map.size() == 0){

                    if(findBookFirebaseEvents != null){
                        findBookFirebaseEvents.onAllFindBookMetaDataFetched(new ArrayList<FindBookMetaData>());
                    }
                    return;
                }
                for(Map.Entry<String, ?> bIdDataEntry : map.entrySet()){
                    String bId = (String)bIdDataEntry.getKey();
                    HashMap<String, String> uDatamap = (HashMap<String, String>) bIdDataEntry.getValue();
                    numberbIdMap.put(String.valueOf(uDatamap.get("Phone Number")), String.valueOf(bId));
                }

                ArrayList<FindBookMetaData> returningFindBookMetaDataArrayList = new ArrayList<FindBookMetaData>();

                /** Looping through each find book meta data **/
                for(int i = 0; i<findBookMetaDataArrayList.size(); i++){

                    String bId = numberbIdMap.get(findBookMetaDataArrayList.get(i).getPhoneNumber());
                    if(bId != null){
                        FindBookMetaData findBookMetaData = new FindBookMetaData.FindBookMetaDataBuilder()
                                .addName(findBookMetaDataArrayList.get(i).getName())
                                .addBId(bId)
                                .addPhoneNumber(findBookMetaDataArrayList.get(i).getPhoneNumber())
                                .build();
                        returningFindBookMetaDataArrayList.add(findBookMetaData);
                    }
                }

                if(findBookFirebaseEvents != null){
                    findBookFirebaseEvents.onAllFindBookMetaDataFetched(returningFindBookMetaDataArrayList);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });
    }

    public interface FindBookFirebaseEvents{
        void onAllFindBookMetaDataFetched(ArrayList<FindBookMetaData> findBookMetaDataArrayList);
    }

    public interface FindBookFirebaseCompletionEvents{
        void onSearchComplete(boolean isSuccessful, FindBookMetaData findBookMetaData);
    }
}
