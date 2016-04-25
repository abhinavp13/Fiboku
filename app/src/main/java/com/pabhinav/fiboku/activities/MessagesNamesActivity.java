package com.pabhinav.fiboku.activities;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.client.FirebaseError;
import com.pabhinav.fiboku.R;
import com.pabhinav.fiboku.adapter.MessagesNamesAdapter;
import com.pabhinav.fiboku.application.FibokuApplication;
import com.pabhinav.fiboku.firebase.MessagesFirebaseHelper;
import com.pabhinav.fiboku.firebase.RPNFirebaseHelper;
import com.pabhinav.fiboku.models.FindBookMetaData;
import com.pabhinav.fiboku.models.Message;
import com.pabhinav.fiboku.models.MessageNames;
import com.pabhinav.fiboku.util.Constants;
import com.pabhinav.fiboku.util.FLog;
import com.pabhinav.fiboku.util.NetworkUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This activity list all friend's who can be messaged using this app.
 *
 * @author pabhinav
 */
public class MessagesNamesActivity extends BaseAbstractActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * List view displaying contacts available for messaging.
     */
    ListView messagesNamesListView;

    /**
     * Adapter for list view.
     */
    MessagesNamesAdapter messagesNamesAdapter;

    /**
     * Loading text view shown when loading of data is going on in background.
     */
    TextView loadingTextView;

    /**
     * Nothing found text view shown when no data is found.
     */
    TextView nothingFoundTextView;

    /**
     * Main relative layout for this activity.
     */
    RelativeLayout mainRelativeLayout;

    /**
     * Called when the activity is starting.
     *
     * <p>This activity extends {@link BaseAbstractActivity}, an abstract activity,
     * which mainly handles permissions grant and navigation drawer related events. </p>
     *
     * <p>Need to call {@link BaseAbstractActivity#setContentViewAndId(int, int)}, instead
     * of {@link android.support.v7.app.AppCompatActivity#setContentView(int)}, because
     * the abstract activity handles all layout inflation by itself, when setContentViewAndId
     * is called with a constant Activity Id for each Activity.</p>
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewAndId(R.layout.activity_messages_names, Constants.messagesActivity);

        /**
         * Initialize views.
         */
        loadingTextView = (TextView) findViewById(R.id.loading_text_view);
        nothingFoundTextView = (TextView) findViewById(R.id.nothing_found_text_view);
        mainRelativeLayout = (RelativeLayout) findViewById(R.id.relative_layout_massages_names);
        messagesNamesListView = (ListView)findViewById(R.id.messages_list_view);

        /**
         * Notify adapter on message counter changed
         */
        messageCountChangeEvent = new MessageCountChangeEvent() {
            @Override
            public void notifyMessageCountChange() {
                if(messagesNamesAdapter != null){
                    messagesNamesAdapter.updateNumberUnreadCountMap(numberUnreadCountMap);
                }
            }
        };

        /**
         * If internet connectivity present, show loading icon and start
         * loading data, else, show no internet connection snack bar.
         */
        if(NetworkUtil.isNetworkConnected(this)){

            loadingTextView.setVisibility(View.VISIBLE);
            nothingFoundTextView.setVisibility(View.GONE);

            /** Loader initialize **/
            getLoaderManager().initLoader(1, null, this);
        } else {
            Snackbar.make(mainRelativeLayout, getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        /** the cursor loader **/
        return new CursorLoader(
                this,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + getString(R.string.asc)
        );
    }

    /**
     * Called when a previously created loader has finished its load.
     *
     * <p>This function is guaranteed to be called prior to the release of
     * the last data that was supplied for this Loader. The Loader will take care of
     * management of its data so you don't have to.
     *
     *
     * @param loader The Loader that has finished.
     * @param data The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if(data.getCount() == 0){
            loadingTextView.setVisibility(View.GONE);
            nothingFoundTextView.setVisibility(View.VISIBLE);
        } else {
            loadingTextView.setVisibility(View.GONE);
            nothingFoundTextView.setVisibility(View.GONE);
        }
        data.moveToFirst();
        getDataFromFirebase(data);
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

    /**
     * Fetches rpn data from firebase,
     * only authenticated user can do this, and can only read those rpns
     * which are in his connections.
     *
     * @param data cursor for loaded connections
     */
    public void getDataFromFirebase(final Cursor data){

        RPNFirebaseHelper rpnFirebaseHelper = new RPNFirebaseHelper();
        rpnFirebaseHelper.fetchListOfRPN();
        rpnFirebaseHelper.setRpnEvents(new RPNFirebaseHelper.RPNEvents() {
            @Override
            public void onRPNFetched(List<String> rpnList, FirebaseError firebaseError) {

                ArrayList<String> rpnData = new ArrayList<String>();
                if(firebaseError == null){
                    rpnData = new ArrayList<>(rpnList);
                }

                final ArrayList<MessageNames> messageNamesArrayList = createMessageNamesList(data, rpnData);

                /**
                 * Initialize connections adapter, since data is now available for it.
                 */
                messagesNamesAdapter = new MessagesNamesAdapter(MessagesNamesActivity.this, messageNamesArrayList, R.layout.messages_names_list_view_item, numberUnreadCountMap);

                if(messageNamesArrayList.size() == 0){
                    loadingTextView.setVisibility(View.GONE);
                    nothingFoundTextView.setVisibility(View.VISIBLE);
                }

                /**
                 * Set on item click event.
                 */
                messagesNamesAdapter.setMessagesNamesItemClickEvent(new MessagesNamesAdapter.MessagesNamesItemClickEvent() {

                    @Override
                    public void onItemClicked(View v, int position) {
                        Intent intent = new Intent(MessagesNamesActivity.this, MessagesActivity.class);
                        intent.putExtra(Constants.MESSSAGE_PHONE_NUMBER, messageNamesArrayList.get(position).getPhoneNumber());
                        Pair<View, String> pair1 = Pair.create(findViewById(R.id.fake_action_bar), getString(R.string.fake_action_bar_transition));
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(MessagesNamesActivity.this, pair1);
                        startActivity(intent, options.toBundle());
                    }
                });
                messagesNamesListView.setAdapter(messagesNamesAdapter);
            }
        });
    }

    /**
     * Called after {@link #onRestoreInstanceState}, {@link #onRestart}, or
     * {@link #onPause}, for activity to start interacting with the user.
     */
    public void onResume(){
        super.onResume();
        if(messagesNamesAdapter != null){
            messagesNamesAdapter.updateNumberUnreadCountMap(numberUnreadCountMap);
        }
    }


    /**
     * Create {@link MessageNames} domain object.</br>
     * Requires cursor data and list of rpns.
     *
     * @param data {@link Cursor} object.
     * @param rpnData list of rpn strings
     * @return list of {@link MessageNames} objects.
     */
    private ArrayList<MessageNames> createMessageNamesList(Cursor data, ArrayList<String> rpnData){

        ArrayList<MessageNames> messageNamesArrayList = new ArrayList<>();
        while(data.moveToNext()){
            String pN = data.getString(data.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            if (rpnData.contains(pN)
                    || (pN.length() > 3 && rpnData.contains(pN.substring(3)))) {

                /**
                 * If phone number is current user's only, leave it
                 */
                String myNumber = FibokuApplication.getInstance().getPhoneNumber();
                if(myNumber.equals(pN)
                        || (pN.length() > 3 && myNumber.equals(pN.substring(3)))){
                    continue;
                }

                /**
                 * Try to get the bitmap image.
                 */
                Bitmap bitmap;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(
                            MessagesNamesActivity.this.getContentResolver(),
                            Uri.parse(data.getString(data.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)))
                    );
                } catch (Exception e){
                    bitmap = null;
                }

                MessageNames messagesNames = new MessageNames.MessageNamesBuilder()
                        .addBitmap(bitmap)
                        .addName(data.getString(data.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)))
                        .addPhoneNumber(data.getString(data.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)))
                        .build();

                messageNamesArrayList.add(messagesNames);
            }
        }

        return messageNamesArrayList;
    }
}
