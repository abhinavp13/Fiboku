package com.pabhinav.fiboku.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.client.FirebaseError;
import com.pabhinav.fiboku.R;
import com.pabhinav.fiboku.alertDialogs.FibokuAlertMessageDialog;
import com.pabhinav.fiboku.firebase.MessagesFirebaseHelper;
import com.pabhinav.fiboku.firebase.UploadedBooksFirebaseHelper;
import com.pabhinav.fiboku.models.Message;
import com.pabhinav.fiboku.util.Constants;
import com.pabhinav.fiboku.util.PermissionUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import lombok.Getter;
import lombok.Setter;

/**
 * This is an abstract activity, which mainly handles permissions grant and
 * navigation drawer related events.
 *
 * @author pabhinav
 */
public abstract class BaseAbstractActivity extends AppCompatActivity {

    /**
     * Drawer Layout attached with this activity
     */
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    /**
     * Message counter displaying view group.
     */
    @Bind(R.id.messsage_counter_relative_layout)
    RelativeLayout messageCounterRelativeLayout;

    /**
     * Message count displaying text view.
     */
    @Bind(R.id.message_count_text_view)
    TextView messageCountTextView;

    /**
     * Boolean value denoting drawer open state.
     */
    @Getter
    private boolean isDrawerOpen;

    /**
     * Activity Id corresponding to each extending activity. <br/>
     * This id is used by abstract base activity to identify which activity is calling it.
     */
    @Setter
    public int activityId;

    /**
     * HashMap mapping friend's name in String to unread message count in Integer.
     */
    HashMap<String, Integer> numberUnreadCountMap;

    /**
     * Event for message count changed.
     */
    @Setter
    MessageCountChangeEvent messageCountChangeEvent;

    /**
     * Called when the activity is starting.
     *
     * <p>All extending Activities need to call {@link BaseAbstractActivity#setContentViewAndId(int, int)},
     * instead of {@link android.support.v7.app.AppCompatActivity#setContentView(int)}, because
     * this activity handles all layout inflation by itself, when setContentViewAndId
     * is called with a constant Activity Id for each Activity.</p>
     *
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.
     */
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        /**
         * Set the main Content View.
         */
        setContentView(R.layout.activity_base);

        /**
         * Binding with {@link ButterKnife}.
         */
        ButterKnife.bind(this);

        /**
         * Need to ask for permissions for android M and above
         */
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            confirmAllPermissions();
        }

        /**
         * Drawer Listener
         */
        isDrawerOpen = false;
        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                isDrawerOpen = true;
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                isDrawerOpen = false;
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });


        /**
         * Get all unread messages count and set in the message row in the drawer
         */
        MessagesFirebaseHelper messagesFirebaseHelper = new MessagesFirebaseHelper();

        /**
         * Call all unread message count method in firebase message helper.
         */
        messagesFirebaseHelper.getAllUnreadMessageCount();

        /**
         * Listen to message Firebase events.
         */
        messagesFirebaseHelper.setMessageFirebaseEvents(new MessagesFirebaseHelper.MessageFirebaseEvents() {
            @Override
            public void onAllMessagesFetched(ArrayList<Message> messageArrayList, FirebaseError firebaseError) {
            }

            @Override
            public void onAllUnReadMessageCountFetched(HashMap<String, Integer> numberUnreadMessageMap, int totalCount, FirebaseError firebaseError) {

                /**
                 * No error received.</br>
                 * Need not handle error received case, because message count won't get updated,
                 * which is the expected behaviour.
                 */
                if (firebaseError == null) {

                    if (totalCount > 0) {

                        /**
                         * If message counter is more than zero, then make counter visible.
                         */
                        messageCounterRelativeLayout.setVisibility(View.VISIBLE);
                        messageCountTextView.setText(String.valueOf(totalCount));
                    } else {

                        /**
                         * Else set visibility to GONE.
                         */
                        messageCounterRelativeLayout.setVisibility(View.GONE);
                    }
                    /**
                     * Initialize new map received from unread message count event.
                     */
                    numberUnreadCountMap = new HashMap<>(numberUnreadMessageMap);

                    /**
                     * Notify message count change event.
                     */
                    if (messageCountChangeEvent != null) {
                        messageCountChangeEvent.notifyMessageCountChange();
                    }
                }
            }
        });

        /**
         * Default activity id
         */
        activityId = -1;
    }

    /**
     * Message Count Change Event Notifier. </br>
     * Implemented by extending activities for notification of such event.
     */
    public interface MessageCountChangeEvent{

        /**
         * Called whenever message count has changed.
         */
        void notifyMessageCountChange();
    }

    /**
     * Inflates given layout id in its main content frame layout.
     * Also, activity Id is required to identify extending activity.
     * This method must be called by every extending activity.
     *
     * @param layoutId layout to be inflated in the main content frame
     * @param activityId this id has to be unique for every extending activity.
     */
    public void setContentViewAndId(int layoutId, int activityId){

        /**
         * Get the layout inflater
         */
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        /**
         * Get the main frame layout
         */
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.main_frame_layout);

        /**
         * Inflate the given layout
         */
        layoutInflater.inflate(layoutId, frameLayout, true);

        /**
         *  Activity id
         */
        this.activityId = activityId;
    }

    /**
     * Toggle Drawer's state.
     * Capture click events on menu icon from child activities.
     *
     * @param v view used to toggle drawer state.
     */
    public void toggleDrawer(View v){
        if(isDrawerOpen){
            closeDrawer();
        } else {
            openDrawer();
        }
        isDrawerOpen = !isDrawerOpen;
    }

    /**
     * Opens the drawer.
     */
    private void openDrawer(){
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    /**
     * Closes the drawer.
     */
    private void closeDrawer(){
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    /**
     * Home drawer item clicked
     *
     * @param v view for drawer item
     */
    public  void homeClicked(View v){
        handleItemClicked(Constants.homeDrawerItemCode);
    }

    /**
     * Uploaded Books drawer item clicked
     *
     * @param v view for drawer item
     */
    public void uploadedBooksClicked(View v){
        handleItemClicked(Constants.uploadedBooksDrawerItemCode);
    }

    /**
     * Messages drawer item clicked
     *
     * @param v view for drawer item
     */
    public void messagesClicked(View v){
        handleItemClicked(Constants.messagesDrawerItemCode);
    }

    /**
     * Connections drawer item clicked
     *
     * @param v view for drawer item
     */
    public void connectionsClicked(View v){
        handleItemClicked(Constants.connectionsDrawerItemCode);
    }

    /**
     * Feedback drawer item clicked
     *
     * @param v view for drawer item
     */
    public void feedbackClicked(View v){
        handleItemClicked(Constants.feedbackDrawerItemCode);
    }

    /**
     * About drawer item clicked
     *
     * @param v view for drawer item
     */
    public void aboutClicked(View v){
        handleItemClicked(Constants.aboutDrawerItemCode);
    }

    /**
     * Handler for every drawer item click
     *
     * @param drawerItemCode it is the code given to each drawer item.
     */
    public void handleItemClicked(int drawerItemCode){

        if(activityId == Constants.homeActivity){
            toggleDrawer(null);
            if(drawerItemCode != Constants.homeDrawerItemCode){
                startCorrespondingActivity(drawerItemCode);
            }
        } else {
            if(activityId == drawerItemCode){
                toggleDrawer(null);
            } else if(drawerItemCode == Constants.homeDrawerItemCode){
                finish();
            } else {
                startCorrespondingActivity(drawerItemCode);
                finish();
            }
        }
    }

    /**
     * Start the activity corresponding to drawer item code given
     *
     * @param drawerItemCode it is the code given to each drawer item
     */
    public void startCorrespondingActivity(int drawerItemCode){

        Intent intent = null;
        switch (drawerItemCode){
            case Constants.homeDrawerItemCode:
                intent = new Intent(this,MainScreenActivity.class);
                break;
            case Constants.uploadedBooksDrawerItemCode:
                intent = new Intent(this, UploadedBookActivity.class);
                break;
            case Constants.messagesDrawerItemCode :
                intent = new Intent(this, MessagesNamesActivity.class);
                break;
            case Constants.connectionsDrawerItemCode :
                intent = new Intent(this, ConnectionsActivity.class);
                break;
            case Constants.feedbackDrawerItemCode :
                intent = new Intent(this, FeedbackActivity.class);
                break;
            case Constants.aboutDrawerItemCode :
                intent = new Intent(this, AboutActivity.class);
                break;
        }
        if(intent != null) {
            startActivity(intent);
        }
    }

    /**
     * This captures navigation header click events,
     * this does nothing, just capture click events,
     * if this event is not captured, the click gets
     * passed to parent activity, which was not the
     * required behaviour.
     *
     * @param v view of the navigation header
     */
    public void navigationDrawerHeaderClicked(View v){
        // Nothing to be done
    }

    /**
     * Captures back presses, if drawer is open, closes
     * the drawer first and then if again back is pressed
     * with drawer closed, it closes the corresponding
     * activity
     */
    @Override
    public void onBackPressed(){
        if(isDrawerOpen){
            toggleDrawer(null);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * This function confirms that all permissions required to run
     * this app are present, if not, ask for permissions.
     */
    private void confirmAllPermissions(){

        /**
         * Get list of not granted permissions
         */
        List<String> deniedPermissions = PermissionUtil.deniedPermissions(
                this,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.ACCESS_NETWORK_STATE
        );

        /**
         * if all the permissions granted, nothing needs to be done
         */
        if(deniedPermissions.isEmpty()){
            return;
        }

        /**
         * Request for permissions
         */
        ActivityCompat.requestPermissions(this, deniedPermissions.toArray(new String[deniedPermissions.size()]), Constants.REQUEST_CODE_SOME_FEATURES_PERMISSIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        /**
         * if request code matches the request code for asking permissions, confirm that all permissions are granted
         */
        if(requestCode == Constants.REQUEST_CODE_SOME_FEATURES_PERMISSIONS){
            if(!PermissionUtil.confirmGrantedPermissions(grantResults)){
                needPermissionsDialog();
            }
        }
    }

    /**
     * Permissions denied by user, ask him to allow again or close the app
     */
    public void needPermissionsDialog(){

        /**
         * Alert Message Dialog initialization.
         */
        FibokuAlertMessageDialog fibokuAlertMessageDialog = new FibokuAlertMessageDialog(
                this,
                getString(R.string.permissions_denied),
                getString(R.string.go_to_app_settings),
                getString(R.string.cancel),
                getString(R.string.app_settings)
        );


        /**
         * Show the alert dialog
         */
        fibokuAlertMessageDialog.show();

        /**
         * capture click events
         */
        fibokuAlertMessageDialog.setOnAlertButtonClicked(new FibokuAlertMessageDialog.OnAlertButtonClicked() {
            @Override
            public void onLeftButtonClicked(View v) {
                finish();
            }

            @Override
            public void onRightButtonClicked(View v) {

                /**
                 * Take user to app settings
                 */
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts(getString(R.string.package_string), getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);

                finish();
            }
        });
    }
}
