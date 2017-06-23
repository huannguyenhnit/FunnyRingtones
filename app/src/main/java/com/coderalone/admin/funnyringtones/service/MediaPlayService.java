package com.coderalone.admin.funnyringtones.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.coderalone.admin.funnyringtones.util.Constant;
import com.coderalone.admin.funnyringtones.util.Utils;

import java.io.IOException;

public class MediaPlayService extends Service implements OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener {

    private static final String TAG = MediaPlayService.class.getSimpleName();

    private Messenger messenger = new Messenger(new ServiceHandler());

    private Messenger messengerFragment;

    public static final int CASE_PLAYING = 0;
    public static final int CASE_PAUSE = 1;
    public static final int CASE_RESUME = 2;
    public static final int CASE_STATUS = 3;
    public static final int CASE_STATUS_UPDATE_VIEW = 8;
    public static final int CASE_PLAYED = 4;
    public static final int CASE_FINISHED = 5;
    public static final int CASE_OVERLEAP = 6;
    public static final int CASE_ERROR = 7;

    private MediaPlayer mMediaPlayer;

    private Handler mHandler = new Handler();


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Message message = Message.obtain(null, CASE_FINISHED);
        sendMessenger(message);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d(TAG, "Load media onError");
        Message message = Message.obtain(null, CASE_ERROR);
        sendMessenger(message);
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (mMediaPlayer == null) {
            Log.d(TAG, "onPrepared: " + mMediaPlayer);
            return;
        }
        Log.d(TAG, "onPrepared: " + mMediaPlayer.isPlaying());

        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
        mp.start();
        //Update progress;
        updateProgress();
        //Send status is played to Fragment.
        Message message = Message.obtain(null, MediaPlayService.CASE_PLAYED);
        sendMessenger(message);
    }

    /**
     * Receive data from Fragment and control media player.
     */
    class ServiceHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CASE_PLAYING:
                    String url = (String) msg.obj;
                    if (!TextUtils.isEmpty(url)) {
                        playMedia(url);
                    } else {
                        Log.d(TAG, "URL ERROR url = " + url);
                    }
                    break;
                case CASE_PAUSE:
                    if (mMediaPlayer != null) {
                        mMediaPlayer.pause();
                    }
                    Message messagePause = Message.obtain(null, CASE_PAUSE);
                    sendMessenger(messagePause);
                    break;
                case CASE_RESUME:
                    if (mMediaPlayer != null &&
                            Utils.getProgressPercentage(mMediaPlayer.getCurrentPosition(), mMediaPlayer.getDuration()) < 96) {
                        mMediaPlayer.seekTo(mMediaPlayer.getCurrentPosition());
                        mMediaPlayer.start();
                    } else if (mMediaPlayer != null &&
                            Utils.getProgressPercentage(mMediaPlayer.getCurrentPosition(), mMediaPlayer.getDuration()) >= 96) {
                        mMediaPlayer.start();
                    }
                    Message messageResume = Message.obtain(null, CASE_RESUME);
                    //Send messenger to fragment.
                    sendMessenger(messageResume);
                    break;
                case CASE_STATUS:
                    boolean playing = false;
                    if (mMediaPlayer != null) {
                        playing = mMediaPlayer.isPlaying();
                    }
                    Message messageStatus = Message.obtain(null, CASE_STATUS, playing);
                    //Send messenger to fragment.
                    sendMessenger(messageStatus);
                    break;
                case CASE_STATUS_UPDATE_VIEW:
                    boolean playingStatus = false;
                    if (mMediaPlayer != null) {
                        playingStatus = mMediaPlayer.isPlaying();
                    }
                    Message messagePlaying = Message.obtain(null, CASE_STATUS_UPDATE_VIEW, playingStatus);
                    //Send messenger to fragment.
                    sendMessenger(messagePlaying);
                    break;
                case CASE_OVERLEAP:
                    if (mMediaPlayer != null) {
                        int totalTimer = mMediaPlayer.getDuration();
                        int progress = (int) msg.obj;
                        mMediaPlayer.seekTo(Utils.progressToTimer(progress, totalTimer));
                    }
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

    /**
     * Play media.
     *
     * @param url Link file
     */
    private void playMedia(final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mMediaPlayer == null) {
                    mMediaPlayer = new MediaPlayer();
                }
                try {
                    mMediaPlayer.reset();
                    mMediaPlayer.setDataSource(url);
                    if (mMediaPlayer != null) {
                        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
                        mMediaPlayer.setOnPreparedListener(MediaPlayService.this);
                        mMediaPlayer.setOnErrorListener(MediaPlayService.this);
                        mMediaPlayer.setOnCompletionListener(MediaPlayService.this);
                        mMediaPlayer.prepareAsync();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Update progress to view.
     */
    private void updateProgress() {
        mHandler.postAtTime(mUpdateTimer, 100);
    }

    private Runnable mUpdateTimer = new Runnable() {
        @Override
        public void run() {
            if (mMediaPlayer != null) {
                if (mMediaPlayer.isPlaying()) {
                    long totalDuration = mMediaPlayer.getDuration();
                    long currentDuration = mMediaPlayer.getCurrentPosition();

                    Bundle bundle = new Bundle();
                    bundle.putLong(Constant.KEY_TOTAL_DURATION, totalDuration);
                    bundle.putLong(Constant.KEY_CURRENT_DURATION, currentDuration);

                    Message message = Message.obtain(null, CASE_PLAYING);
                    message.obj = bundle;
                    //Send messenger to fragment.
                    sendMessenger(message);
                }
                mHandler.postDelayed(this, 100);
            }
        }
    };

    /**
     * Called when need to send data to Fragment.
     *
     * @param message Message.
     */
    private void sendMessenger(Message message) {
        try {
            messengerFragment.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        mMediaPlayer = new MediaPlayer();
        if (intent != null) {
            Log.d(TAG, "onBind: ");
            messengerFragment = (Messenger) intent.getExtras().get(Constant.MESSENGER);
        }
        return messenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }
        mMediaPlayer = null;
        return super.onUnbind(intent);
    }
}
