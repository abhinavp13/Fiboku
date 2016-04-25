package com.pabhinav.fiboku.activities;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.client.FirebaseError;
import com.pabhinav.fiboku.R;
import com.pabhinav.fiboku.adapter.ConnectionsAdapter;
import com.pabhinav.fiboku.alertDialogs.FibokuAlertMessageDialog;
import com.pabhinav.fiboku.application.FibokuApplication;
import com.pabhinav.fiboku.firebase.RPNFirebaseHelper;
import com.pabhinav.fiboku.util.Constants;
import com.pabhinav.fiboku.util.NetworkUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity displays contacts for the user, marking all those
 * who are using the same app.
 *
 * @author pabhinav
 */
public class ConnectionsActivity extends BaseAbstractActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * User Name
     */
    TextView agentNameTextView;

    /**
     * Phone number for user
     */
    TextView phoneNumberTextView;

    /**
     * List view displaying contacts
     */
    ListView connectionsListView;

    /**
     * Adapter for list view displaying contacts
     */
    ConnectionsAdapter connectionsAdapter;

    /**
     * List of rpn strings.
     */
    List<String> rpnData;

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
        setContentViewAndId(R.layout.activity_connections, Constants.connectionsActivity);

        /**
         * Initialize views.
         */
        loadingTextView = (TextView) findViewById(R.id.loading_text_view);
        nothingFoundTextView = (TextView) findViewById(R.id.nothing_found_text_view);
        mainRelativeLayout = (RelativeLayout) findViewById(R.id.relative_layout_connections);

        /**
         * Sets agent name
         */
        agentNameTextView = (TextView) findViewById(R.id.agent_name_text_view);
        agentNameTextView.setText(FibokuApplication.getInstance().getAgentName());

        /**
         * Set phone number
         */
        phoneNumberTextView = (TextView) findViewById(R.id.phone_number_text_view);
        phoneNumberTextView.setText(FibokuApplication.getInstance().getPhoneNumber());

        /**
         * Initialize contacts.
         */
        connectionsListView = (ListView) findViewById(R.id.connections_list_view);

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
            Snackbar.make(mainRelativeLayout, R.string.no_internet_connection, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Sync button clicked by user. </br>
     * This will reload contacts for the user.
     * Also, shows rotating effect on sync button.
     *
     * @param v view for sync icon.
     */
    public void syncClicked(View v){
        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.clockwise_rotate);
        rotation.setRepeatCount(Animation.INFINITE);
        v.startAnimation(rotation);

        getLoaderManager().restartLoader(1,null,this);
    }

    /**
     * Share button clicked by user. </br>
     *
     * This will display a share alert box to user, which will ask user whether to
     * share this app with his/her friends or not.</br>
     *
     * If yes, is answered a share intent is created, else dialog gets closed.
     *
     *
     * @param v view for the share icon.
     */
    public void shareClicked(View v){
        sendShareIntent();
    }


    /**
     * This will display a share alert box to user, which will ask user whether to
     * share this app with his/her friends or not.</br>
     *
     * If yes, is answered a share intent is created, else dialog gets closed.
     *
     */
    public void showShareAlert(){

        /**
         * Initialize alert message dialog
         */
        FibokuAlertMessageDialog fibokuAlertMessageDialog = new FibokuAlertMessageDialog(
                this,
                getString(R.string.invite_people),
                getString(R.string.want_to_invite),
                getString(R.string.no),
                getString(R.string.yes)
        );

        /**
         * Show alert message
         */
        fibokuAlertMessageDialog.show();

        /**
         * Listen to alert message button click events.
         */
        fibokuAlertMessageDialog.setOnAlertButtonClicked(new FibokuAlertMessageDialog.OnAlertButtonClicked() {
            @Override
            public void onLeftButtonClicked(View v) {
            }

            @Override
            public void onRightButtonClicked(View v) {
                sendShareIntent();
            }
        });
    }

    /**
     * Creates a share intent and user chooses to share with friends using
     * various social sharing apps present on the device.
     */
    public void sendShareIntent(){

        Uri imageUri = Uri.parse(getString(R.string.android_resource_link) + getPackageName() + getString(R.string.drawable_directory) + getString(R.string.fiboku_icon));
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.i_love_fiboku));
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setType(getString(R.string.image_type_jpeg));
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, getString(R.string.invite_friends_using)));
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

        /**
         * The cursor loader
         */
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
        data.moveToFirst();
        if(data.getCount() == 0){
            nothingFoundTextView.setVisibility(View.VISIBLE);
            loadingTextView.setVisibility(View.GONE);
        } else {
            loadingTextView.setVisibility(View.GONE);
            nothingFoundTextView.setVisibility(View.GONE);
        }
        getDataFromFirebase(data);

        ImageView syncImageView = (ImageView)findViewById(R.id.syncButton);
        syncImageView.clearAnimation();
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
                if(firebaseError == null){
                    rpnData = new ArrayList<>(rpnList);
                }

                /**
                 * Initialize connections adapter, since data is now available for it.
                 */
                connectionsAdapter = new ConnectionsAdapter(ConnectionsActivity.this, data, R.layout.connections_list_view_item, rpnData);

                /**
                 * Set on item click event.
                 */
                connectionsAdapter.setConnectionItemClickListener(new ConnectionsAdapter.ConnectionItemClickListener() {
                    @Override
                    public void onItemClicked(View v) {
                        showShareAlert();
                    }
                });

                /**
                 * Update the list view with the adapter.
                 */
                connectionsListView.setAdapter(connectionsAdapter);
            }
        });
    }
}
