package com.pabhinav.fiboku.util;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * @author pabhinav
 */
public class HardwareUtil {

    /**
     * Detects camera presence in device
     *
     * @param context of the calling activity.
     * @return true, if camera is present in device, else false.
     */
    public static boolean isCameraAvailable(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }
}
