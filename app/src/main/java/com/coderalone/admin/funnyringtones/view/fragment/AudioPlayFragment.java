package com.coderalone.admin.funnyringtones.view.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.coderalone.admin.funnyringtones.model.RingtoneEntity;
import com.coderalone.admin.funnyringtones.util.DownloadFileAsynTask;
import com.coderalone.admin.funnyringtones.R;
import com.coderalone.admin.funnyringtones.app.MyApplication;
import com.coderalone.admin.funnyringtones.model.RingtoneEntity;
import com.coderalone.admin.funnyringtones.model.SubCategoryEntity;
import com.coderalone.admin.funnyringtones.service.MediaPlayService;
import com.coderalone.admin.funnyringtones.util.CallBackToRingtoneListActivityInterface;
import com.coderalone.admin.funnyringtones.util.Constant;
import com.coderalone.admin.funnyringtones.util.DownloadFileAsynTask;
import com.coderalone.admin.funnyringtones.util.PhoneSoundSetting;
import com.coderalone.admin.funnyringtones.util.Utils;
import com.coderalone.admin.funnyringtones.view.activity.DisconnectActivity;

public class AudioPlayFragment extends Fragment implements View.OnClickListener, DownloadFileAsynTask.DownloadFileAsynTaskInterface {

    private RingtoneEntity mRingtoneEntity;
    private View mRootView;
    private ProgressBar mPlayProgress;
    private ProgressBar mLoadingProgress;
    private TextView mTimeTotalText;
    private RelativeLayout mPlayPauseLayout;
    private ImageView mPlayPauseImg;
    private TextView mNameFileText;
    private TextView mNameTypeText;
    private LinearLayout mDownloadLayout;
    private ImageView mDownloadImg;

    private LinearLayout mLinearControllSetting;
    private Button mSaveFileBtn;
    private SwitchCompat mPhoneSwitch;
    private SwitchCompat mNotificationSwitch;
    private SwitchCompat mAlarmSwitch;
    private boolean isChangeSetting = false;
    private String mDownloadedFile = null;
    private Messenger serviceMessenger;
    private Messenger myMessenger = new Messenger(new MyHandler());
    private ProgressDialog mProgressDownload;
    private CallBackToRingtoneListActivityInterface mCallBackToRingtoneListActivityInterface;
    private SubCategoryEntity mSubCategoryEntity;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            serviceMessenger = new Messenger(service);
            startPlayMusic();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceMessenger = null;
        }
    };


    ////////////////////////////////////////////////////////////////////////////
    // public methods
    ////////////////////////////////////////////////////////////////////////////

    public void setSubCategoryEntity(SubCategoryEntity subCategoryEntity) {
        this.mSubCategoryEntity = subCategoryEntity;
    }

    public void setRingtoneEntity(RingtoneEntity ringtoneEntity) {
        if (mNameTypeText != null && mNameFileText != null) {
            if (!mRingtoneEntity.equals(ringtoneEntity)) {
                resetControllSetting();
            }
            mNameTypeText.setText(ringtoneEntity.getSubCategory());
            mNameFileText.setText(ringtoneEntity.getName());
        }
        this.mRingtoneEntity = ringtoneEntity;
    }

    public void setCallBackToRingtoneListActivityInterface(CallBackToRingtoneListActivityInterface mCallBackToRingtoneListActivityInterface) {
        this.mCallBackToRingtoneListActivityInterface = mCallBackToRingtoneListActivityInterface;
    }

    public static AudioPlayFragment newInstance() {
        AudioPlayFragment audioPlayFragment = new AudioPlayFragment();
        return audioPlayFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.from(getContext()).inflate(R.layout.audio_play_layout, container, false);

        initView();

        return mRootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (serviceMessenger == null)
            return;
        sendToService(MediaPlayService.CASE_PAUSE, null);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (serviceMessenger == null)
            return;
        sendToService(MediaPlayService.CASE_RESUME, null);
    }

    @Override
    public void onDestroyView() {
        MyApplication.getInstance().getApplicationContext().unbindService(serviceConnection);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        //Callback to RingtoneListActivity about status of fragment is destroy.
        mCallBackToRingtoneListActivityInterface.isDestroyAudioPlayFragment();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ringtone_play_pause_relative:
                sendToService(MediaPlayService.CASE_STATUS, null);
                break;
            case R.id.ringtone_play_icon_download_img:
                if (mLinearControllSetting.getVisibility() == View.GONE) {
                    mDownloadImg.setImageResource(R.mipmap.ic_download_blue);
                    mLinearControllSetting.setVisibility(View.VISIBLE);
                } else {
                    mDownloadImg.setImageResource(R.mipmap.ic_download);
                    mLinearControllSetting.setVisibility(View.GONE);
                }
                if (!isChangeSetting) {
                    mPhoneSwitch.setChecked(false);
                    mNotificationSwitch.setChecked(false);
                    mAlarmSwitch.setChecked(false);
                }
                break;
            case R.id.ringtone_ok_btn:
                boolean ringtone = mPhoneSwitch.isChecked();
                boolean notification = mNotificationSwitch.isChecked();
                boolean alarm = mAlarmSwitch.isChecked();
                if (!ringtone && !notification && !alarm) {
                    Toast.makeText(getActivity(), R.string.not_choose_setting, Toast.LENGTH_SHORT).show();
                } else {
                    checkDownload();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constant.MY_PERMISSIONS_REQUEST_SETTING:
                //If user access for the this permission
                // Then we're will change ringtone default setting.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (changePhoneSoundSetting()) {
                        changePhoneSoundSettingSuccess();
                        return;
                    }
                }
                //If user not access this permission , show dialog setting failure.
                changeSettingNotPermission();
                break;
            case Constant.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                //If user access for the this permission
                // Then we're continue checkDownload.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    downloadFile();
                    return;
                }
                //If user not access this permission , show snackbar setting failure.
                changeNotPermission();
                break;
            default:
                break;
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.MY_PERMISSIONS_REQUEST_SETTING) {
            if (Settings.System.canWrite(getActivity()) ||
                    Utils.checkPermission(getActivity(), Manifest.permission.WRITE_SETTINGS)) {
                if (changePhoneSoundSetting()) {
                    changePhoneSoundSettingSuccess();
                    return;
                }
            }
            changeSettingNotPermission();
        } else if (requestCode == Constant.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (Utils.checkPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                downloadFile();
            } else {
                changeNotPermission();
            }
        }

    }


    /**
     * Called when user clicked to item in the list.
     */
    public void startPlayMusic() {
        if (mLoadingProgress != null && mPlayPauseImg != null && mPlayProgress != null) {
            mPlayProgress.setProgress(0);
            mLoadingProgress.setVisibility(View.VISIBLE);
            mPlayPauseImg.setVisibility(View.GONE);
        }
        sendToService(MediaPlayService.CASE_PLAYING, mRingtoneEntity.getUrlFile());
    }

    @Override
    public void onDownloadStarted() {
        if (mProgressDownload == null) {
            mProgressDownload = new ProgressDialog(getContext());
            mProgressDownload.setTitle(getString(R.string.download_progress_title));
            mProgressDownload.setMessage(getString(R.string.download_progress_content));
            mProgressDownload.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDownload.setCancelable(false);
            mProgressDownload.setMax(100);
        }
        if (mProgressDownload.isShowing()) {
            mProgressDownload.dismiss();
        }

        mProgressDownload.show();
    }

    @Override
    public void onProgressUpdate(int progress) {
        mProgressDownload.setProgress(progress);
    }

    @Override
    public void onDownloadCompleted(String filePath) {
        mProgressDownload.dismiss();
        //If file path is null or empty then return.
        if (TextUtils.isEmpty(filePath)) {
            return;
        }
        mDownloadedFile = filePath;
        //Check permission WRITE_SETTINGS.
        if (Utils.checkPermission(getActivity(), Manifest.permission.WRITE_SETTINGS)) {

            if (changePhoneSoundSetting()) {
                //If change setting phone sound success.
                changePhoneSoundSettingSuccess();
            } else {
                //If change setting phone sound error.
                changePhoneSoundSettingError();
            }
        } else {//Request permission WRITE_SETTINGS.
            Utils.requestPermission(getActivity(), Manifest.permission.WRITE_SETTINGS,
                    Constant.MY_PERMISSIONS_REQUEST_SETTING);
        }

    }

    @Override
    public void onDownloadError() {
        mProgressDownload.dismiss();
        downloadFileError();
    }


    ////////////////////////////////////////////////////////////////////////////
    // private methods
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Called when screen init.
     */
    private void initView() {
        mPlayProgress = (ProgressBar) mRootView.findViewById(R.id.ringtone_play_progress);
        mLoadingProgress = (ProgressBar) mRootView.findViewById(R.id.ringtone_loading_progress);
        mPlayPauseImg = (ImageView) mRootView.findViewById(R.id.ringtone_play_pause_img);
        mPlayPauseLayout = (RelativeLayout) mRootView.findViewById(R.id.ringtone_play_pause_relative);
        mPlayPauseLayout.setOnClickListener(this);
        mTimeTotalText = (TextView) mRootView.findViewById(R.id.ringtone_play_time_tv);
        mNameFileText = (TextView) mRootView.findViewById(R.id.ringtone_play_name_file_tv);
        mNameTypeText = (TextView) mRootView.findViewById(R.id.ringtone_play_name_type_tv);
        mDownloadImg = (ImageView) mRootView.findViewById(R.id.ringtone_play_icon_download_img);
        mDownloadImg.setOnClickListener(this);
        mLinearControllSetting = (LinearLayout) mRootView.findViewById(R.id.ringtone_controller_linear);
        mLinearControllSetting.setOnClickListener(this);
        mPhoneSwitch = (SwitchCompat) mRootView.findViewById(R.id.ringtone_phone_switch);
        mNotificationSwitch = (SwitchCompat) mRootView.findViewById(R.id.ringtone_notification_switch);
        mAlarmSwitch = (SwitchCompat) mRootView.findViewById(R.id.ringtone_alarm_switch);
        mSaveFileBtn = (Button) mRootView.findViewById(R.id.ringtone_ok_btn);
        mSaveFileBtn.setOnClickListener(this);

        mNameTypeText.setText(mRingtoneEntity.getSubCategory());
        mNameFileText.setText(mRingtoneEntity.getName());

        mPlayProgress.setMax(Constant.MAX_PROGRESS);
        mPlayProgress.setProgress(0);

        // Bind service and attach the  messenger.
        Intent intent = new Intent(getContext(), MediaPlayService.class);
        intent.putExtra(Constant.MESSENGER, myMessenger);
        MyApplication.getInstance().getApplicationContext().bindService(intent, serviceConnection, Service.BIND_AUTO_CREATE);
    }

    /**
     * Reset controller phone sound setting view.
     */
    private void resetControllSetting() {
        mDownloadImg.setImageResource(R.mipmap.ic_download);
        mPhoneSwitch.setChecked(false);
        mNotificationSwitch.setChecked(false);
        mAlarmSwitch.setChecked(false);
        mLinearControllSetting.setVisibility(View.GONE);
    }

    /**
     * Called when need to send data or change media play status to service.
     *
     * @param caseStatus
     * @param data
     */
    private void sendToService(int caseStatus, @Nullable Object data) {
        if (serviceMessenger == null)
            return;
        Message message = Message.obtain(null, caseStatus, data);
        try {
            serviceMessenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Start Media play.
     */
    private void playMedia() {
        sendToService(MediaPlayService.CASE_RESUME, null);
    }

    /**
     * Pause media play.
     */
    private void pauseMedia() {
        sendToService(MediaPlayService.CASE_PAUSE, null);
    }

    /**
     * Change icon to pause.
     */
    private void pauseImage() {
        mPlayPauseImg.setImageResource(R.mipmap.ic_pause);
    }

    /**
     * Change icon to play.
     */
    private void playImage() {
        mPlayPauseImg.setImageResource(R.mipmap.ic_play);
    }

    /**
     * Check download file.
     */
    private void checkDownload() {
        if (Utils.isConnectInternet(MyApplication.getInstance().getApplicationContext())) {
            // Check permission WRITE_EXTERNAL_STORAGE.
            if (Utils.checkPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                downloadFile();
            } else {// Request permission WRITE_EXTERNAL_STORAGE.
                Utils.requestPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Constant.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            }
        } else {
            Intent intent = DisconnectActivity.getNewActivityStartIntent(getActivity());
            intent.putExtra(Constant.SUB_CATEGORY, mSubCategoryEntity);
            startActivityForResult(intent, Constant.REQUEST_DISCONNECT_CODE);
        }

    }

    /**
     * Download file from server.
     */
    private void downloadFile() {
        new DownloadFileAsynTask(AudioPlayFragment.this).execute(
                new String[]{mRingtoneEntity.getUrlFile()});

    }

    /**
     * Called when dowload file error.
     */
    private void downloadFileError() {
        Toast.makeText(getActivity(), R.string.download_error_title, Toast.LENGTH_SHORT).show();
    }


    /**
     * Change default ringtone, notification and alarm.
     *
     * @return
     */
    private boolean changePhoneSoundSetting() {
        boolean isSetting = false;
        boolean isAlarm = mAlarmSwitch.isChecked();
        boolean isNotification = mNotificationSwitch.isChecked();
        boolean isPhone = mPhoneSwitch.isChecked();

        if (isAlarm) {
            isSetting = PhoneSoundSetting.setPhoneSound(MyApplication.getInstance().getApplicationContext(),
                    mDownloadedFile, mRingtoneEntity.getName(), false, false, mAlarmSwitch.isChecked());
        } else if (isNotification) {
            isSetting = PhoneSoundSetting.setPhoneSound(MyApplication.getInstance().getApplicationContext(),
                    mDownloadedFile, mRingtoneEntity.getName(), false, mNotificationSwitch.isChecked(), false);
        } else if (isPhone) {
            isSetting = PhoneSoundSetting.setPhoneSound(MyApplication.getInstance().getApplicationContext(),
                    mDownloadedFile, mRingtoneEntity.getName(), mPhoneSwitch.isChecked(), false, false);
        }
        if (!isSetting) {
            return isSetting;
        }
        return isSetting;
    }

    /**
     * Called when change phone sound setting success.
     */
    private void changePhoneSoundSettingSuccess() {
        Toast.makeText(getActivity(), R.string.change_setting_success, Toast.LENGTH_SHORT).show();

        isChangeSetting = true;
        mLinearControllSetting.setVisibility(View.GONE);
        mDownloadImg.setImageResource(R.mipmap.ic_download);

    }

    /**
     * Called when change phone sound setting error.
     */
    private void changePhoneSoundSettingError() {
        Toast.makeText(getActivity(), R.string.change_setting_error, Toast.LENGTH_SHORT).show();
    }

    /**
     * Called when not access setting permission.
     */
    private void changeSettingNotPermission() {
        Toast.makeText(getActivity(), R.string.permission_error, Toast.LENGTH_SHORT).show();
    }

    /**
     * Called when not access other permission.
     */
    private void changeNotPermission() {
        Toast.makeText(getActivity(), R.string.permission_error, Toast.LENGTH_SHORT).show();
    }

    /**
     * Receive data from service and update to view.
     */
    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case MediaPlayService.CASE_PLAYED:
                    mLoadingProgress.setVisibility(View.GONE);
                    mPlayPauseImg.setVisibility(View.VISIBLE);
                    pauseImage();
                    break;
                case MediaPlayService.CASE_PLAYING:
                    Bundle bundle = (Bundle) msg.obj;
                    long totalDuration = bundle.getLong(Constant.KEY_TOTAL_DURATION);
                    long currentDuration = bundle.getLong(Constant.KEY_CURRENT_DURATION);
                    mTimeTotalText.setText(Utils.milliSecondsToTimer(totalDuration));
                    int progress = Utils.getProgressPercentage(currentDuration, totalDuration);
                    mPlayProgress.setProgress(progress);
                    break;
                case MediaPlayService.CASE_STATUS:
                    boolean playing = (boolean) msg.obj;
                    if (playing) {
                        pauseMedia();
                    } else {
                        playMedia();
                    }
                    break;
                case MediaPlayService.CASE_PAUSE:
                    playImage();
                    break;
                case MediaPlayService.CASE_RESUME:
                    pauseImage();
                    break;
                case MediaPlayService.CASE_FINISHED:
                    playImage();
                    break;
                case MediaPlayService.CASE_ERROR:
                    // Error
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

}