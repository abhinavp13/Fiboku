package com.pabhinav.fiboku.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pabhinav
 */
public class PermissionUtil {

    /**
     * Checks whether permission is granted or denied.
     *
     * @param context of the calling activity
     * @param permission required by app
     *
     * @return true, if permission is granted, else false
     */
    public static boolean isPermissionGranted(Context context, String permission){
        int permissionValue = ContextCompat.checkSelfPermission(context, permission);
        return permissionValue == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Given a list of permissions, returns the list of denied permissions
     *
     * @param context of the calling activity
     * @param permissions required by app
     * @return denied permissions list
     */
    public static List<String> deniedPermissions(Context context, String... permissions){

        /** Keeps the list of ungranted permissions **/
        List<String> deniedPermissions = new ArrayList<String>();
        for (String permission : permissions) {
            if(!isPermissionGranted(context, permission)){
                deniedPermissions.add(permission);
            }
        }
        return deniedPermissions;
    }

    /**
     * Confirms whether granted permissions result has all permissions granted.
     *
     * @param grantResult is the result received from onRequestPermissionsResult.
     * @return true, if all are granted, else false
     */
    public static boolean confirmGrantedPermissions(int[] grantResult){
        for (int aGrantResult : grantResult) {
            if (aGrantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }
}
