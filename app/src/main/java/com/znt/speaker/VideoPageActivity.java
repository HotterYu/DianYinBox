package com.znt.speaker;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jungle.mediaplayer.widgets.JungleMediaPlayer;
import com.jungle.mediaplayer.widgets.MediaPlayerFrame;
import com.znt.lib.bean.DeviceStatusInfor;
import com.znt.lib.bean.LocalMediaInfor;
import com.znt.lib.bean.MediaInfor;
import com.znt.lib.bean.SoftVersionResultBean;
import com.znt.lib.bean.StaticIpInfor;
import com.znt.lib.bean.UpdateInfor;
import com.znt.lib.utils.DateUtils;
import com.znt.lib.utils.NetWorkUtils;
import com.znt.lib.utils.PluginConstant;
import com.znt.lib.utils.SystemUtils;
import com.znt.lib.utils.ViewUtils;
import com.znt.push.db.DBMediaHelper;
import com.znt.push.entity.LocalDataEntity;
import com.znt.push.httpmodel.HttpAPI;
import com.znt.push.httpmodel.HttpCallback;
import com.znt.push.httpmodel.HttpClient;
import com.znt.push.reboot.RebootModel;
import com.znt.push.v.IDevStatusView;
import com.znt.speaker.dialog.LoginDialog;
import com.znt.speaker.dialog.SettingDialog;
import com.znt.speaker.dialog.UpdateDialog;
import com.znt.speaker.factory.HardWarePlayFactory;
import com.znt.speaker.media.MediaScanFactory;
import com.znt.speaker.model.SDCardMountModel;
import com.znt.speaker.permission.PermissionHelper;
import com.znt.speaker.permission.PermissionInterface;
import com.znt.speaker.video.VideoPlayer;
import com.znt.speaker.view.DevInfoView;
import com.znt.speaker.view.ISDCardMountView;

import java.io.File;
import java.util.List;

import service.ZNTDownloadServiceManager;
import service.ZNTPushServiceManager;
import service.ZNTWifiServiceManager;


public class VideoPageActivity extends AppCompatActivity implements
        ZNTDownloadServiceManager.DownlaodCallBack, IDevStatusView, com.znt.wifimodel.v.INetWorkView
    ,PermissionInterface,VideoPlayer.OnMediaPlayListsner,ISDCardMountView
{

    private final String TAG = "VideoPageActivity";

    private JungleMediaPlayer mVideoPlayer = null;
    private DevInfoView mDevInfoView = null;
    private View topView = null;
    private TextView tvTitle;
    private TextView tvDevName;

    private MediaScanFactory mMediaScanFactory = null;
    private SDCardMountModel mSDCardMountPresenter = null;

    private ZNTDownloadServiceManager mZNTDownloadServiceManager;
    private ZNTPushServiceManager mZNTPushServiceManager;
    private ZNTWifiServiceManager mZNTWifiServiceManager = null;

    private NotificationManager mNotificationManager = null;

    private PermissionHelper mPermissionHelper;

    private UpdateDialog mUpdateDialog = null;
    private LoginDialog mLoginDialog = null;
    private SettingDialog mSettingDialog = null;

    private HardWarePlayFactory mPlayFactory = null;

    private RebootModel mRebootModel = null;

    private volatile long curServerTime = 0;
    private volatile String curDevName = "";

    private final int MSG_GET_CUR_PLAN = 0;
    private final int MSG_GET_CUR_AD_PLAN = 1;
    private final int MSG_UPDATE_TIME = 2;
    private final int MSG_ON_TIMEING_PUSH = 3;
    private final int MSG_ON_INTERNAL_PUSH = 4;
    private final int MSG_ON_MEDIA_PLAY = 5;
    private final int MSG_ON_UPDATE = 6;
    private final int MSG_ON_PLAY_STATUS = 7;
    private final int MSG_ON_PUSH_MEDIA = 8;
    private final int MSG_ON_MEDIA_CHANGED = 9;

    private Handler mHandler = new Handler(new Handler.Callback()
    {
        @Override
        public boolean handleMessage(Message msg)
        {
            if(msg.what == MSG_GET_CUR_PLAN)
            {
                List<MediaInfor> tempList = (List<MediaInfor>) msg.obj;
                mZNTDownloadServiceManager.addSonginfor(tempList);
                mPlayFactory.playNext();
            }
            else if(msg.what == MSG_GET_CUR_AD_PLAN)
            {
                mPlayFactory.playNext();
            }
            else if(msg.what == MSG_ON_PUSH_MEDIA)
            {
                List<MediaInfor> pushMedias = (List<MediaInfor>) msg.obj;
                mPlayFactory.addPushMedias(pushMedias);
            }
            else if(msg.what == MSG_UPDATE_TIME)
            {
                showTitleInfo();
            }
            else if(msg.what == MSG_ON_PLAY_STATUS)
            {
                String status = (String) msg.obj;
                mDevInfoView.setDevPlayStatus(status);
                if(status.equals("1") || status.equals("2"))
                {
                    if(mPlayFactory.isCurPlayerCanStop())
                        mPlayFactory.stopPlayByForce(mDevInfoView.getShowStatus());
                }
            }
            else if(msg.what == MSG_ON_TIMEING_PUSH)
            {
                MediaInfor tempInfo = (MediaInfor) msg.obj;
                if(tempInfo != null)
                {
                    tempInfo.setMediaType(MediaInfor.MEDIA_TYPE_ADV);
                    mPlayFactory.playPushMedia(tempInfo);
                }
            }
            else if(msg.what == MSG_ON_INTERNAL_PUSH)
            {
                MediaInfor tempInfo = (MediaInfor) msg.obj;
                if(tempInfo != null)
                {
                    tempInfo.setMediaType(MediaInfor.MEDIA_TYPE_ADV);
                    mPlayFactory.playPushMedia(tempInfo);
                }
            }
            else if(msg.what == MSG_ON_UPDATE)
            {
                if(mUpdateDialog != null && mUpdateDialog.isShowing())
                {
                    mUpdateDialog.dismiss();
                    mUpdateDialog = null;
                }
                UpdateInfor tempInfor = (UpdateInfor)msg.obj;
                mUpdateDialog = new UpdateDialog(VideoPageActivity.this, tempInfor.getVersionName(), tempInfor.getVersionNum(), tempInfor.getApkUrl(), new UpdateDialog.OnUpdateResultListener() {
                    @Override
                    public void onPluginUpdateCallBack() {
                        LocalDataEntity.newInstance(getApplicationContext()).setRestartAppFlag("1");//允许再启动一次
                        restartApp();
                    }
                });
                mUpdateDialog.show();
            }
            else if(msg.what == MSG_ON_MEDIA_CHANGED)
            {
                StaticIpInfor staticIpInfor = (StaticIpInfor) msg.obj;

                if(staticIpInfor == null)
                {
                    showToast(getResources().getString(R.string.static_ip_set_file_read_error));
                }
                else
                {
                    Intent intent = getPackageManager().getLaunchIntentForPackage("com.znt.install");
                    if(intent != null)
                    {
                        intent.putExtra("ip_set_addr", staticIpInfor.getIp());
                        intent.putExtra("ip_set_gateway", staticIpInfor.getGateway());
                        intent.putExtra("ip_set_dns1", staticIpInfor.getDns1());
                        intent.putExtra("ip_set_dns2", staticIpInfor.getDns2());
                        startActivity(intent);
                        showToast(getResources().getString(R.string.static_ip_set_success));
                    }
                    else
                        showToast(getResources().getString(R.string.static_ip_set_install_pkg_not_find));
                }
            }
            return false;
        }
    });

    public void showLog(String text)
    {
        Log.e("","*****************************"+text);
    }

    private int procCheckCount = 12;
    private String procStatus = "";
    private void showProcStatus()
    {
        try
        {
            if(procCheckCount >= 12)
            {
                procCheckCount = 0;
                String procStatus1 = "";
                if(mZNTPushServiceManager.isActive())
                    procStatus1 = "P正常";
                else
                {
                    procStatus1 = "P未开启";
                    mZNTPushServiceManager.bindService();
                }

                String procStatus2 = "";
                if(mZNTDownloadServiceManager.isActive())
                    procStatus2 = "D正常";
                else
                {
                    procStatus2 = "D未开启";
                    mZNTDownloadServiceManager.bindService();
                }

                String procStatus3 = "";
                if(mZNTWifiServiceManager.isActive())
                    procStatus3 = "W正常";
                else
                {
                    procStatus3 = "W未开启";
                    mZNTWifiServiceManager.bindService();
                }
                procStatus = procStatus1 + "-" + procStatus2 + "-" + procStatus3;
                mZNTPushServiceManager.updateProcInfo(procStatus);
                mDevInfoView.showProcStatus(procStatus);
            }
            else
                procCheckCount ++;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    Runnable  runnable = new Runnable() {

        @Override
        public void run() {

            mHandler.postDelayed(this, 1000);
            showTitleInfo();
        }
    };

    public VideoPageActivity() {
        mZNTDownloadServiceManager = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        try
        {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            getSupportActionBar().hide();
            hideBottomUIMenu();
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
            /*setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);*/
            setContentView(R.layout.activity_video_page);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ((AlarmManager)getSystemService(Context.ALARM_SERVICE)).setTimeZone("Asia/Shanghai");
            }
            else
            {
                AlarmManager mAlarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                mAlarmManager.setTimeZone("GMT+08:00");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            if(e !=  null)
                showToast("初始化异常：" + e.getMessage());
            else
                showToast("初始化异常：");
        }
        try
        {
            mHandler.postDelayed(runnable, 1000);

            String source = getIntent().getStringExtra("ZNT_SOURCE");
            if(source == null || !source.equals("1"))
            {
                PluginConstant.isPlugin = false;
                mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                showNotification();
            }
            else
            {
                PluginConstant.isPlugin = true;
                String oldId = getIntent().getStringExtra("ZNT_OLD_ID");
                LocalDataEntity.newInstance(getApplicationContext()).setOldDeviceId(oldId);
            }

            mPermissionHelper = new PermissionHelper(this, this);
            mPermissionHelper.requestPermissions();

            initData();

            showDevInfor();
        }
        catch (Exception e)
        {
            if(e !=  null)
                showToast("初始化异常：" + e.getMessage());
            else
                showToast("初始化异常：");
            Log.e("","");
        }
    }

    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);

        }
    }
    private void initData()
    {
        if(mZNTPushServiceManager == null)
        {
            mZNTPushServiceManager = new ZNTPushServiceManager(getContext(), this);
            mZNTPushServiceManager.bindService();

            mZNTDownloadServiceManager = new ZNTDownloadServiceManager(getContext(), this);
            mZNTDownloadServiceManager.bindService();

            mZNTWifiServiceManager = new ZNTWifiServiceManager(getContext(), this);
            mZNTWifiServiceManager.bindService();

            tvTitle = (TextView) findViewById(R.id.jz_tv_title);
            tvDevName = (TextView) findViewById(R.id.jz_tv_title_name);
            topView = findViewById(R.id.view_title);

            mDevInfoView = (DevInfoView)findViewById(R.id.dev_infor_view);
            mVideoPlayer = (JungleMediaPlayer)findViewById(R.id.player_view);
            mVideoPlayer.setOnCallBackListener(new MediaPlayerFrame.OnCallBackListener() {
                @Override
                public void onDevInfoShow(boolean show) {
                    mDevInfoView.setVisibility(show ? View.VISIBLE : View.GONE);
                }

                @Override
                public void onLogoViewClick() {
                    Intent intent = new Intent(getContext() , SettingActivity.class);
                    startActivityForResult(intent,1);
                }

                @Override
                public void onCloseClick() {
                    close();
                }
            });

            mZNTPushServiceManager.updateVolumeSetStatus(LocalDataEntity.newInstance(getContext()).isVolumeSetOpen());

            mDevInfoView.setOnSettingClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mSettingDialog == null || !mSettingDialog.isShowing())
                    {
                        boolean wifiSet = LocalDataEntity.newInstance(getContext()).isWifiSetOpen();
                        boolean volumeSet = LocalDataEntity.newInstance(getContext()).isVolumeSetOpen();

                        mSettingDialog = new SettingDialog(VideoPageActivity.this, wifiSet, volumeSet);
                        mSettingDialog.show();
                        mSettingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                if(mSettingDialog.isUpdateEnable())
                                {
                                    LocalDataEntity.newInstance(getContext()).setWifiSet(mSettingDialog.isWifiSetOpen());
                                    LocalDataEntity.newInstance(getContext()).setVolumeSet(mSettingDialog.isVolumeSetOpen());
                                    mZNTPushServiceManager.updateVolumeSetStatus(LocalDataEntity.newInstance(getContext()).isVolumeSetOpen());
                                }
                            }
                        });
                    }
                }
            });

            mDevInfoView.setOnCloseClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    close();
                }
            });

            mDevInfoView.setOnLoginClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mLoginDialog == null || !mLoginDialog.isShowing())
                    {
                        String name = LocalDataEntity.newInstance(getContext()).getDeviceName();
                        String code = LocalDataEntity.newInstance(getContext()).getDeviceCode();
                        String devId = LocalDataEntity.newInstance(getContext()).getDeviceId();
                        mLoginDialog = new LoginDialog(VideoPageActivity.this, name, code, devId);
                        mLoginDialog.show();
                        mLoginDialog.setOnRegisterByClickFinish(new LoginDialog.OnRegisterByClickFinish() {
                            @Override
                            public void onRegisterByClickFinish(String devId, String devName) {
                                onRegisterFinish(devId,devName,"");
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mLoginDialog.dismiss();
                                        showToast("登陆成功");
                                    }
                                });
                            }
                        });
                    }
                }
            });
            mDevInfoView.setOnUpdateCheckClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkUpdate();
                }
            });

            mDevInfoView.setOnDevVersionClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mRebootModel.rebootBox(5);
                }
            });

            mMediaScanFactory = new MediaScanFactory(getContext());
            mMediaScanFactory.scanLocalMedias();

            mSDCardMountPresenter = new SDCardMountModel(this, this);
            mSDCardMountPresenter.registerStorageMount();

            mPlayFactory = new HardWarePlayFactory(this);
            mPlayFactory.setVideoPlayer(mVideoPlayer);
            mPlayFactory.setZNTDownloadServiceManager(mZNTDownloadServiceManager);
            mPlayFactory.setZNTPushServiceManager(mZNTPushServiceManager);

            mRebootModel = new RebootModel(this);
        }
    }

    private void close()
    {
        if(mZNTPushServiceManager != null)
            mZNTPushServiceManager.unBindService();
        if(mZNTWifiServiceManager != null)
            mZNTWifiServiceManager.unBindService();
        if(mZNTDownloadServiceManager != null)
            mZNTDownloadServiceManager.unBindService();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    /**
     * 重新启动App -> 杀进程,会短暂黑屏,启动慢
     */
    public void restartApp()
    {
        Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
        PendingIntent restartIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager mgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 2000, restartIntent);

        close();
    }

    final String CHANNEL_ID = "channel_id_1";
    final String CHANNEL_NAME = "channel_name_1";
    private void showNotification()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            //只在Android O之上需要渠道
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,CHANNEL_NAME, NotificationManager.IMPORTANCE_MIN);
            //如果这里用IMPORTANCE_NOENE就需要在系统的设置里面开启渠道，
            //通知才能正常弹出
            notificationChannel.setSound(null,null);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder= new NotificationCompat.Builder(this,CHANNEL_ID);
        Intent intent2 = new Intent(getApplicationContext(),VideoPageActivity.class);
        PendingIntent pendingIntent2 = PendingIntent.getActivity(this,0,intent2,PendingIntent.FLAG_UPDATE_CURRENT);
        //RemoteViews remoteViews=new RemoteViews(getPackageName(),R.layout.view_update_dialog);

        builder.setSmallIcon(R.mipmap.logo)
                .setTicker(getResources().getString(R.string.app_name))
                .setContentTitle(getResources().getString(R.string.app_name_running))
                .setContentText(getResources().getString(R.string.app_desc))
                .setPriority(Notification.PRIORITY_HIGH)
                /**向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：Notification.DEFAULT_ALL就是3种全部提醒**/
                //.setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS)
                .setSound(null)
                .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE)
                .setContentIntent(pendingIntent2)
                .setAutoCancel(false);
        Notification  notification=builder.build();

        notification.flags |= Notification.FLAG_NO_CLEAR;

        mNotificationManager.notify(18, notification);

    }

    private Context getContext()
    {
        return getApplicationContext();
    }

    public void showToast(String text)
    {
        Log.e("","***************************:"+text);
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode)
        {
            case 1:
                String wifiName = data.getStringExtra("WIFI_NAME");
                String wifiPwd = data.getStringExtra("WIFI_PWD");
                mZNTWifiServiceManager.startConnectCurWifi(wifiName,wifiPwd);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        /**暂停系统其它媒体的状态*/
        //MediaUtils.muteAudioFocus(getApplicationContext(), false);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onDestroy()
    {
        //mVideoPlayer.stopPlay();
        mNotificationManager.cancel(18);
        super.onDestroy();

    }

    @Override
    public void onPushSuccess(DeviceStatusInfor devStatusInfor)
    {
        mZNTWifiServiceManager.devStatusCheck(true);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mDevInfoView.setDevStatus(true);
            }
        });
        mPlayFactory.setOnLineStatus(true);
    }

    @Override
    public void onPushFail(int count)
    {
        mPlayFactory.setOnLineStatus(false);
        mZNTWifiServiceManager.devStatusCheck(false);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mDevInfoView.setDevStatus(false);
            }
        });
    }

    @Override
    public void onPushCheck(final int count)
    {
        if(count == 5)
        {
            mPlayFactory.updatePushParams(null,false);
            mRebootModel.checkRebootDevice(curServerTime);
        }
    }

    @Override
    public void onInitterminalFinish(String time)
    {
        if(!TextUtils.isEmpty(time))
        {
            curServerTime = Long.parseLong(time);
        }
    }

    @Override
    public void onUpdateServerTime(String time)
    {
        if(!TextUtils.isEmpty(time))
        {
            curServerTime = Long.parseLong(time);
        }
    }

    @Override
    public void onRegisterFinish(String devId, String devName, String shopCode) {
        curDevName = devName;
        LocalDataEntity.newInstance(getApplicationContext()).setDeviceId(devId);
        LocalDataEntity.newInstance(getApplicationContext()).setDeviceName(devName);
        showDevInfor();
        HttpClient.updateDevInfo(getApplicationContext());

        if(!TextUtils.isEmpty(shopCode))
        {
            try
            {
                String userId = shopCode.substring(2,4);
                if(userId.equals("78"))//属于喜士多的设备，要添加默认wifi
                    mZNTWifiServiceManager.updateDefaultWifi(true);
                else//不是喜士多的设备就去掉
                    mZNTWifiServiceManager.updateDefaultWifi(false);
            }
            catch (Exception e)
            {
                // TODO: handle exception
            }
        }
    }

    private void showTitleInfo()
    {
        showProcStatus();

        if(curServerTime == 0)
        {
            if(mZNTPushServiceManager != null)
                curServerTime = mZNTPushServiceManager.getServerTime();
            if(curServerTime <= 0)
                curServerTime = System.currentTimeMillis();
        }
        if(curServerTime > 0)
        {
            curServerTime += 1000;
            if(TextUtils.isEmpty(curDevName))
                curDevName = LocalDataEntity.newInstance(getApplicationContext()).getDeviceName();
            if(mPlayFactory != null)
            {
                mPlayFactory.setDevName(curDevName);
                mPlayFactory.setSystemTime(DateUtils.getDateFromLong(curServerTime));
            }

            if(mZNTPushServiceManager != null)
                mZNTPushServiceManager.updateProcessByTime(curServerTime);
        }
    }

    private void showDevInfor()
    {
        String devId = LocalDataEntity.newInstance(getApplicationContext()).getDeviceId();
        //String devName = LocalDataEntity.newInstance(getApplicationContext()).getDeviceName();
        String versionName = "";
        if(HttpAPI.SERVER_ADDRESS.contains("zhunit.com"))
            versionName = SystemUtils.getVersionName(getContext()) + "   " + getResources().getString(R.string.version_release);
        else
            versionName = SystemUtils.getVersionName(getContext()) + "   " + getResources().getString(R.string.version_debug);
        if(mDevInfoView != null)
            mDevInfoView.setDevInfor( "编号: " + devId, " 版本:" + versionName);
    }

    @Override
    public void onSpaceCheck(final long size)
    {
        DBMediaHelper.getInstance().checkAndReleaseSpace(size);
    }

    @Override
    public void onGetCurPlanFinish()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    List<MediaInfor> tempList = DBMediaHelper.getInstance().getAllMedias();
                    ViewUtils.sendMessage(mHandler,MSG_GET_CUR_PLAN,tempList);
                }
                catch (Exception e)
                {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mPlayFactory.playNext();
                        }
                    },1000);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onGetAdPlanFinish()
    {
        try
        {
            ViewUtils.sendMessage(mHandler,MSG_GET_CUR_AD_PLAN,null);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onTimeingPushNotify() {
        try
        {
            //收到push的通知
            MediaInfor curPushMedia = mZNTPushServiceManager.getCurTimeingAd();
            ViewUtils.sendMessage(mHandler,MSG_ON_TIMEING_PUSH,curPushMedia);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onInternalTimePushNotify()
    {
        MediaInfor curPushMedia = mZNTPushServiceManager.getCurTimeInternalAd();
        ViewUtils.sendMessage(mHandler,MSG_ON_INTERNAL_PUSH,curPushMedia);
    }

    @Override
    public void onPushMediaNotify()
    {
        List<MediaInfor> pushMedias = mZNTPushServiceManager.getPushMedias();
        ViewUtils.sendMessage(mHandler,MSG_ON_PUSH_MEDIA,pushMedias);
    }

    @Override
    public void onWifiConfig(String wifiName, String wifiPwd)
    {
        if(LocalDataEntity.newInstance(getContext()).isWifiSetOpen())
            mZNTWifiServiceManager.startConnectCurWifi(wifiName, wifiPwd);
    }

    @Override
    public void onUpdateCheck(final String vName, final String vNum, final String url) {

        UpdateInfor tempInfor = new UpdateInfor();
        tempInfor.setApkUrl(url);
        tempInfor.setVersionName(vName);
        tempInfor.setVersionNum(vNum);
        ViewUtils.sendMessage(mHandler,MSG_ON_UPDATE,tempInfor);
    }

    @Override
    public void onPlayStatus(String status) {
        ViewUtils.sendMessage(mHandler,MSG_ON_PLAY_STATUS,status);
    }

    @Override
    public void onDownloadSpaceCheck(final String size)
    {
        if(!TextUtils.isEmpty(size))
            DBMediaHelper.getInstance().checkAndReleaseSpace(Long.parseLong(size));
    }

    @Override
    public void onDownloadRecordInsert(final String mediaName, final String mediaUrl, final String modifyTime)
    {
        LocalMediaInfor tempInfo = new LocalMediaInfor();
        if(!TextUtils.isEmpty(modifyTime))
            tempInfo.setModifyTime(Long.parseLong(modifyTime));
        tempInfo.setMediaName(mediaName);
        File file = new File(mediaUrl);
        if(file.exists())
            tempInfo.setMediaSize(file.length());
        tempInfo.setMediaUrl(mediaUrl);
        DBMediaHelper.getInstance().addLocalMedia(tempInfo);
    }

    @Override
    public void onRemoveLargeSize(String url)
    {
        //DBManager.INSTANCE.
        Log.d(TAG, "onRemoveLargeSize: "+url);
    }
    @Override
    public void onBackPressed() {
        //System.exit(0);
        moveTaskToBack(false);
        //bootProcess(5);
        //super.onBackPressed();
    }

    @Override
    public void connectWifiSatrt(String wifiName) {
        if(NetWorkUtils.isNetConnected(getApplicationContext()))
            HttpClient.wifiConfigReport(getApplicationContext(),"1000");
    }

    @Override
    public void connectWifiFailed(String wifiName, String wifipwd) {
        if(NetWorkUtils.isNetConnected(getApplicationContext()))
            HttpClient.wifiConfigReport(getApplicationContext(),"1001");
    }

    @Override
    public void connectWifiSuccess(String wifiName, String wifipwd) {
        if(NetWorkUtils.isNetConnected(getApplicationContext()))
            HttpClient.wifiConfigReport(getApplicationContext(),"1002");
    }
    @Override
    public void openWifiFail() {
        if(NetWorkUtils.isNetConnected(getApplicationContext()))
            HttpClient.wifiConfigReport(getApplicationContext(),"1003");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(mPermissionHelper.requestPermissionsResult(requestCode, permissions, grantResults)){
            //权限请求结果，并已经处理了该回调
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public int getPermissionsRequestCode() {
        return 10000;
    }

    @Override
    public String[] getPermissions() {
        return new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION

        };
    }
    @Override
    public void requestPermissionsSuccess()
    {
        //权限请求用户已经全部允许
        try
        {
            initData();
        }
        catch (Exception e)
        {
            if(e == null)
                showToast("初始化失败");
            else
                showToast("初始化失败："+e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void requestPermissionsFail() {
        //权限请求不被用户允许。可以提示并退出或者提示权限的用途并重新发起权限申请。
        showPermissions();
        //mPermissionHelper.requestPermissions();
        //close();
    }

    private void showPermissions(){
        final Dialog dialog=new android.app.AlertDialog.Builder(this).create();
        View v=LayoutInflater.from(this).inflate(R.layout.dialog_permissions,null);
        dialog.show();
        dialog.setContentView(v);

        Button btn_add= (Button) v.findViewById(R.id.btn_add);
        Button btn_diss= (Button) v.findViewById(R.id.btn_diss);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPermissionHelper.toPermissionSetting(VideoPageActivity.this);
                dialog.dismiss();
                close();
            }
        });

        btn_diss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                close();
            }
        });
    }

    @Override
    public void onMediaPlay(MediaInfor mediaInfor) {
        if(mediaInfor != null)
            ViewUtils.sendMessage(mHandler,MSG_ON_MEDIA_PLAY,mediaInfor.getMediaUrl());
    }

    private boolean isMediaChangeRunning = false;
    @Override
    public void onMediaChange(boolean isAdd, final String path) {
        if(isAdd && !isMediaChangeRunning)
        {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //U盘拔插状态
                    isMediaChangeRunning = true;
                    StaticIpInfor staticIpInfor = NetWorkUtils.getWifiSetInfoFromUsb(path);
                    ViewUtils.sendMessage(mHandler,MSG_ON_MEDIA_CHANGED,staticIpInfor);
                    isMediaChangeRunning = false;
                }
            }).start();
        }
    }

    private void checkUpdate()
    {
        String id = LocalDataEntity.newInstance(getApplicationContext()).getDeviceId();

        HttpClient.checkUpdate(id,new HttpCallback<SoftVersionResultBean>()
        {
            @Override
            public void onSuccess(SoftVersionResultBean initTerminalResultBean)
            {
                if(initTerminalResultBean.isSuccess())
                {
                    //mUpdateManager.doApkInstall(initTerminalResultBean.getData().getUrl());
                    int curAppNum = SystemUtils.getVersionCode(getApplicationContext());
                    int sAppNum = 0;
                    String serverNum = initTerminalResultBean.getData().getVersionNum();
                    if(!TextUtils.isEmpty(serverNum))
                    {
                        sAppNum = Integer.parseInt(serverNum);
                    }
                    if(curAppNum < sAppNum)
                    {
                        showToast("检测到了新版本：" + initTerminalResultBean.getData().getVersion());
                        onUpdateCheck(initTerminalResultBean.getData().getVersion()
                                , initTerminalResultBean.getData().getVersionNum(), initTerminalResultBean.getData().getUrl());

                    }
                    else
                        showToast("当前版本是最新的");
                }
                else
                {
                    showToast(initTerminalResultBean.getMessage());
                }
            }

            @Override
            public void onFail(Exception e)
            {
                if(e != null)
                    showToast(e.getMessage());
                else
                    showToast("升级检测失败");
            }
        });
    }
}
