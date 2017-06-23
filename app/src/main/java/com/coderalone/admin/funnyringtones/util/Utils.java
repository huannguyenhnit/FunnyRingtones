package com.coderalone.admin.funnyringtones.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.io.File;

public class Utils {

    /**
     * Check access internet
     *
     * @param context
     * @return
     */
    public static boolean isConnectInternet(@NonNull Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /**
     * Check json regex.
     *
     * @param json
     * @return
     */
    public static boolean checkJsonRegex(String json, String... param) {
        if (param == null || param.length == 0) {
            return false;
        }
        for (int i = 0; i < param.length; i++) {
            if (!json.contains(param[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check permistion change default setting.
     *
     * @param activity
     * @param permission
     * @return
     */
    public static boolean checkPermission(@NonNull Activity activity, String permission) {
        boolean isPermission;
        //Check permission can change default setting.
        if (Manifest.permission.WRITE_SETTINGS.equals(permission)) {
            isPermission = checkPermissionSetting(activity);
        } else {
            isPermission = ContextCompat.checkSelfPermission(activity, permission)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return isPermission;
    }

    /**
     * Request permission.
     *
     * @param activity
     * @param permission
     * @param requestCode
     */
    public static void requestPermission(@NonNull Activity activity, @NonNull String permission, int requestCode) {
        if (Manifest.permission.WRITE_SETTINGS.equals(permission)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                openSettingPermissionScreen(activity, requestCode);
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_SETTINGS}, Constant.MY_PERMISSIONS_REQUEST_SETTING);
            }
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
        }
    }

    /**
     * Create the local file path.
     *
     * @param title
     * @return String path.
     */
    public static String createLocalFilePath(CharSequence title) {
        String subdir;
        String externalRootDir = Environment.getExternalStorageDirectory().getPath();
        if (!externalRootDir.endsWith("/")) {
            externalRootDir += "/";
        }

        subdir = Constant.PATH_LOCAL_AUDIO_DOWNLOAD;
        String parentdir = externalRootDir + subdir;

        // Create the parent directory
        File parentDirFile = new File(parentdir);
        parentDirFile.mkdirs();

        // If we can't write to that special path, try just writing
        // directly to the sdcard
        if (!parentDirFile.isDirectory()) {
            parentdir = externalRootDir;
        }

        // Turn the title into a filename
        String filename = "";
        for (int i = 0; i < title.length(); i++) {
            if (Character.isLetterOrDigit(title.charAt(i))) {
                filename += title.charAt(i);
            }
        }

        String filePath;
        filePath = parentdir + filename + Constant.MP3_EXTENSION;

        return filePath;
    }

    /**
     * Function to convert milliseconds time to
     * Timer Format
     * Hours:Minutes:Seconds
     */
    public static String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString;

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

    /**
     * Function to get Progress percentage
     *
     * @param currentDuration
     * @param totalDuration
     */
    public static int getProgressPercentage(long currentDuration, long totalDuration) {
        Double percentage;

        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        // calculating percentage
        percentage = (((double) currentSeconds) / totalSeconds) * 100;

        // return percentage
        return percentage.intValue();
    }

    /**
     * Function to change progress to timer
     *
     * @param progress      -
     * @param totalDuration returns current duration in milliseconds
     */
    public static int progressToTimer(int progress, int totalDuration) {
        int currentDuration;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double) progress) / 100) * totalDuration);

        // return current duration in milliseconds
        return currentDuration * 1000;
    }

    ////////////////////////////////////////////////////////////////////////////
    // private methods
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Check permission SETTING .
     * If API > 23 using method {@link Settings canWrite() }
     *
     * @param activity
     */
    private static boolean checkPermissionSetting(@NonNull Activity activity) {
        boolean isPermission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            isPermission = Settings.System.canWrite(activity);
        } else {
            isPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_SETTINGS)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return isPermission;
    }

    /**
     * Called when need open permission setting screen.
     *
     * @param activity
     * @param requestCode
     */
    private static void openSettingPermissionScreen(Activity activity, int requestCode) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        intent.setData(Uri.parse(Constant.PACKAGE + activity.getPackageName()));
        activity.startActivityForResult(intent, requestCode);
    }

}