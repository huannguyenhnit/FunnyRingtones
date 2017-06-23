package com.coderalone.admin.funnyringtones.util;

import android.content.ContentValues;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.coderalone.admin.funnyringtones.R;

import java.io.File;

public class PhoneSoundSetting {

    /**
     * Called when user change ringtone or alarm for device.
     *
     * @param file         file path.
     * @param context      Context of Activity.
     * @param ringtone     true if set the file to ringtone of device.
     * @param notification true if set the file to notification alarm of device.
     * @param alarm        true if set the file to messenger alarm of device.
     * @return true if change phone sound success | false is change phone sound error.
     */
    public static boolean setPhoneSound(Context context, String file, String fileName, boolean ringtone,
                                              boolean notification, boolean alarm) {

        //Check file path is null or empty.
        if (TextUtils.isEmpty(file)) return false;
        // set File from path
        File k = new File(file);

        String mimeType;
        if (file.endsWith(".m4a")) {
            mimeType = "audio/mp4a-latm";
        } else if (file.endsWith(".wav")) {
            mimeType = "audio/wav";
        } else {
            mimeType = "audio/mpeg";
        }
        // file.exists
        if (file != null) {

            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DATA, k.getAbsolutePath());
            values.put(MediaStore.MediaColumns.TITLE, fileName);
            values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
            values.put(MediaStore.MediaColumns.SIZE, k.length());
            values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
            values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);

            values.put(MediaStore.Audio.Media.ARTIST, R.string.app_name);
            if (ringtone) {
                values.put(MediaStore.Audio.Media.ALBUM, "ringtones");
            } else if (notification) {
                values.put(MediaStore.Audio.Media.ALBUM, "notifications");
            } else if (alarm) {
                values.put(MediaStore.Audio.Media.ALBUM, "alarms");
            }
            values.put(MediaStore.Audio.Media.IS_RINGTONE, ringtone);
            values.put(MediaStore.Audio.Media.IS_NOTIFICATION, notification);
            values.put(MediaStore.Audio.Media.IS_ALARM, alarm);
            values.put(MediaStore.Audio.Media.IS_MUSIC, false);
            // get Uri from file.
            Uri uri = MediaStore.Audio.Media.getContentUriForPath(k
                    .getAbsolutePath());
            //Delete if file is exist.
            context.getContentResolver().delete(uri, MediaStore.MediaColumns.DATA + "=\"" + k.getAbsolutePath() + "\"", null);
            //Insert the file to system.
            Uri newUri = context.getContentResolver().insert(uri, values);
            if (ringtone) {
                //Set actual default ringtone.
                RingtoneManager.setActualDefaultRingtoneUri(
                        context, RingtoneManager.TYPE_RINGTONE,
                        newUri);
            } else if (notification) {
                //Set actual default notification.
                RingtoneManager.setActualDefaultRingtoneUri(
                        context, RingtoneManager.TYPE_NOTIFICATION,
                        newUri);
            } else if (alarm) {
                //Set actual default alarm.
                RingtoneManager.setActualDefaultRingtoneUri(
                        context, RingtoneManager.TYPE_ALARM,
                        newUri);
            }

            return true;
        }

        return false;
    }

}

