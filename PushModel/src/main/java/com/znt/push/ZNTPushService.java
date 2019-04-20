package com.znt.push;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.google.gson.Gson;
import com.znt.lib.bean.AdPlanResultBean;
import com.znt.lib.bean.BaseResultBean;
import com.znt.lib.bean.CurAdPlanInfor;
import com.znt.lib.bean.CurPlanInfor;
import com.znt.lib.bean.CurPlanResultBean;
import com.znt.lib.bean.CurPlanSubInfor;
import com.znt.lib.bean.DeviceStatusInfor;
import com.znt.lib.bean.DeviceStatusResultBean;
import com.znt.lib.bean.InitTerminalResultBean;
import com.znt.lib.bean.MediaInfor;
import com.znt.lib.bean.MusicListResultBean;
import com.znt.lib.bean.PushMediaResultBean;
import com.znt.lib.bean.RegisterTerminalResultBean;
import com.znt.lib.bean.SoftVersionResultBean;
import com.znt.lib.bean.TMECommonResultBean;
import com.znt.lib.bean.TMEOauthInfo;
import com.znt.lib.bean.TerminalRunstatusInfo;
import com.znt.lib.bean.WifiInfoResultBean;
import com.znt.lib.utils.NetWorkUtils;
import com.znt.lib.utils.PluginConstant;
import com.znt.lib.utils.SystemUtils;
import com.znt.push.db.DBMediaHelper;
import com.znt.push.entity.Constant;
import com.znt.push.entity.LocalDataEntity;
import com.znt.push.entity.PushModelConstant;
import com.znt.push.factory.CurPlayMediaManager;
import com.znt.push.httpmodel.CommonCallback;
import com.znt.push.httpmodel.HttpAPI;
import com.znt.push.httpmodel.HttpCallback;
import com.znt.push.httpmodel.HttpClient;
import com.znt.push.location.LocationModel;
import com.znt.push.update.UpdateManager;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class ZNTPushService extends Service implements  UpdateManager.SpaceCheckListener
{

    private String TAG = "ZNTPushService";

    private ServiceBinder mBinder;

    private Context mContext = null;
    private UpdateManager mUpdateManager = null;


    private CurPlayMediaManager mCurPlayMediaManager = null;

    private DeviceStatusInfor curDeviceStatusInfor = null;

    private LocationModel mLocationModel = null;

    private boolean isStop = false;
    private boolean checkUpdateRunning = false;
    private int checkFailCount = 0;

    private boolean isInitRunning = false;
    private boolean isInitFinished = false;
    private long curServerTime = 0;

    private boolean isRegisterRunning = false;
    private boolean isRegisterFinished = false;

    private boolean isLoginRunning = false;
    private boolean isLoginFinished = false;

    private boolean isGetCurPlayMediaRunning = false;
    private boolean isCurPlanGetFinished = false;
    private boolean isCurAdPlanGetFinished = false;

    private volatile boolean isVolumeSetEnable = true;

    private int locationFailCount = 0;

    private List<MediaInfor> pushMedias = new ArrayList<>();

    private TerminalRunstatusInfo curTerminalRunstatusInfo = null;

    public static final String ACTION = "com.znt.speaker.DEV_STATUS";

    private int checkTime = PushModelConstant.STATUS_CHECK_TIME_MAX;
    //private int checkTimeMax = CHECK_TIME_MAX;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {

            super.handleMessage(msg);
        }
    };

    private void doUpdateProcessByTime(long time)
    {
        this.curServerTime = time;
        //更新现在的时间
        mCurPlayMediaManager.updateCurServerTime(curServerTime);
        if(mCurPlayMediaManager.checkTimeingPushAd())
        {
            //定时点播
            callback(PushModelConstant.CALL_BACK_ON_TIMEING_PUSH_NOTYFY,  "", null, null);
        }
        else if(mCurPlayMediaManager.checkTimeInternalPushAd())
        {
            //间隔时间点播
            callback(PushModelConstant.CALL_BACK_ON_INTERNAL_TIME_PUSH_NOTYFY,  "", null, null);
        }
        else if(mCurPlayMediaManager.isCurPlanNone())
        {
            //当前时段没有播放内容
            callback(PushModelConstant.CALL_BACK_ON_PLAY_STATUS, "1", null, null);
        }
        else
        {
            callback(PushModelConstant.CALL_BACK_ON_PLAY_STATUS, "0", null, null);
        }
    }

    private void startCheckDevStatus()
    {
        isStop = false;
        new Thread(new CheckDevStatusTask()).start();
    }
    private void stopCheckDevStatus()
    {
        isStop = true;
    }

    public ZNTPushService()
    {

    }
    private void showErrorLog(Exception e)
    {
        Log.e(TAG, e.getMessage());
    }

    @Override
    public void onCreate()
    {
        // TODO Auto-generated method stub

        super.onCreate();
    }

    @Override
    public void onDestroy()
    {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent)
    {

        if(mContext == null)
        {
            mContext = getApplicationContext();
            onBindInit();
        }
        if (mBinder == null)
            mBinder = new ServiceBinder();


        return mBinder;
    }

    private int rebootCount = 0;

    private void onBindInit()
    {
        try
        {
            Constant.deviceCode = SystemUtils.getAndroidId(mContext) + "_BOX";

            LocalDataEntity.newInstance(getApplicationContext()).setPlanId("");
            LocalDataEntity.newInstance(getApplicationContext()).setMusicUpdateTime("");
            LocalDataEntity.newInstance(getApplicationContext()).setAdPlanId("");
            LocalDataEntity.newInstance(getApplicationContext()).setAdPlanTime("");

            if(NetWorkUtils.isNetConnected(mContext))
            {
                HttpClient.updateDevInfo(getApplicationContext());
            }

            mLocationModel = new LocationModel(mContext, new LocationModel.OnLocationResultListener()
            {
                @Override
                public void onLocationSuccess(AMapLocation location)
                {
                    String lat = location.getLatitude() + "";
                    String lon = location.getLongitude() + "";
                    String addr = location.getAddress();
                    LocalDataEntity.newInstance(getApplicationContext()).setDeviceLocation(lon, lat);
                    LocalDataEntity.newInstance(getApplicationContext()).setDeviceAddr(addr);
                    if(NetWorkUtils.isNetConnected(mContext))
                    {
                        HttpClient.updateDevInfo(getApplicationContext());
                    }

                    showLog("定位成功："+addr);

                    mLocationModel.stopLocation();
                }
                @Override
                public void onLocationFail(String error)
                {
                    showLog("定位失败："+error);
                    locationFailCount ++;
                    if(locationFailCount >= 3)
                    {
                        locationFailCount = 0;
                        mLocationModel.stopLocation();
                    }
                }
            });

            mCurPlayMediaManager = new CurPlayMediaManager();

            mLocationModel.startLocation();

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    rebootCount = LocalDataEntity.newInstance(getApplicationContext()).getRebootCount();
                    startCheckDevStatus();
                }
            },3000);
        }
        catch (Exception e)
        {
            // TODO: handle exception
            if(e == null)
                reportError("onBindInit fail!");
            else
                reportError(e.getMessage());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent)
    {
        // TODO Auto-generated method stub
        return super.onUnbind(intent);

    }

    private int playSeek = 0;
    private int playingPos = 0;
    private String playingSongType = "";
    private String playingSong = "";
    private String playingSongId = "";
    private String netInfo = "";
    private String planIdReport = "";
    private class CheckDevStatusTask implements Runnable
    {
        @Override
        public void run()
        {
            // TODO Auto-generated method stub
            while(true)
            {
                if(isStop)
                    break;
                try
                {
                    Thread.sleep(1000);
                    if(checkTime >= PushModelConstant.STATUS_CHECK_TIME_MAX)
                    {
                        initTerminal();
                        getDevStatus();
                        checkTime = 0;
                        if(isCurAdPlanGetFinished || isCurPlanGetFinished)
                            mCurPlayMediaManager.updateCurAllMedias(getApplicationContext());
                    }
                    else
                    {
                        checkTime ++;
                        callback(PushModelConstant.CALL_BACK_PUSH_CHECK, checkTime + "", null, null);
                    }
                }
                catch (Exception e)
                {
                    if(e != null)
                        reportError(e.getMessage());
                    else
                        reportError("CheckDevStatusTask exception");
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public void showLog(String text)
    {
         Log.e(TAG,"*****************************"+text);
    }

    final RemoteCallbackList<IPushCallback> mCallbacks = new RemoteCallbackList <IPushCallback>();
    void callback(int val, String arg1, String arg2, String arg3)
    {
        try
        {
            int N = mCallbacks.beginBroadcast();
            if(N > 1)
                N = N - 1;
            for (int i=0; i<N; i++)
            {
                try
                {
                    mCallbacks.getBroadcastItem(i).actionPerformed(val, arg1, arg2, arg3);
                }
                catch (Exception e)
                {
                    if(e == null)
                        showLog("callback fail");
                    else
                        showLog("callback fail->"+e.getMessage());
                }
            }
            mCallbacks.finishBroadcast();
        }
        catch (Exception e)
        {
            if(e != null)
                reportError(e.getMessage());
            else
                reportError("callback exception");
            // TODO: handle exception
        }
    }

    public class ServiceBinder extends IPushAidlInterface.Stub
    {
        public ServiceBinder()
        {

        }

        @Override
        public void registerCallback(IPushCallback cb) throws RemoteException
        {
            // TODO Auto-generated method stub
            if (cb != null)
            {
                mCallbacks.register(cb);
            }
        }

        @Override
        public void unregisterCallback(IPushCallback cb) throws RemoteException
        {
            if(cb != null)
            {
                mCallbacks.unregister(cb);
            }
        }

        @Override
        public void init(boolean isPlugin) throws RemoteException
        {
            PluginConstant.isPlugin = isPlugin;
        }

        @Override
        public void setRequestParams(MediaInfor mediaInfor, String fnetInfo, boolean updateNow)
                throws RemoteException
        {
            // TODO Auto-generated method stub

            if(fnetInfo == null)
                fnetInfo = "";

            netInfo = fnetInfo + " planId:" + planIdReport  + "  rebootCount:" + rebootCount;
            if(mediaInfor == null)
            {
                playingSongType = "0";
                playingSong = "";
                playingPos = 0;
                playSeek = 0;
                mCurPlayMediaManager.setCurPlayMediaType(playingSongType);
            }
            else
            {
                playingSongType = mediaInfor.getMediaType();
                playingSong = mediaInfor.getHintMsg() + mediaInfor.getMediaName();
                playingSongId = mediaInfor.getMediaId();
                playingPos = mediaInfor.getCurPosition();
                playSeek = mediaInfor.getCurSeek();
                mCurPlayMediaManager.setCurPlayMediaType(playingSongType);
            }
            if(updateNow)
                getDevStatus();
        }

        @Override
        public void updatePlayRecord(MediaInfor mediaInfor)
                throws RemoteException
        {
            // TODO Auto-generated method stub
            if(mediaInfor != null)
                updatePlayReport(mediaInfor);
        }

        @Override
        public void updateVolumeSetStatus(boolean result) throws RemoteException {
            isVolumeSetEnable = result;
            LocalDataEntity.newInstance(mContext).setVolumeSet(result);
        }

        @Override
        public String getDevId()
        {
            if(curTerminalRunstatusInfo == null)
                return LocalDataEntity.newInstance(mContext).getDeviceId();
            else
                return  curTerminalRunstatusInfo.getTerminalId();
        }

        @Override
        public MediaInfor getCurPlayMedia() throws RemoteException
        {
            return mCurPlayMediaManager.getCurPlayMedia();
        }

        @Override
        public MediaInfor getCurTimeInternalAd() throws RemoteException
        {
            return mCurPlayMediaManager.getCurTimeInternalAd();
        }

        @Override
        public List<MediaInfor> getPushMedias() throws RemoteException {
            return pushMedias;
        }

        @Override
        public List<MediaInfor> getCurPlayMedias() throws RemoteException {
            return mCurPlayMediaManager.getCurPlayMedias();
        }

        @Override
        public MediaInfor getCurTimeingAd() throws RemoteException
        {
            return mCurPlayMediaManager.getCurTimeingAd();
        }

        @Override
        public long getCurServerTime()
        {
            return curServerTime;
        }

        @Override
        public void updateProcessByTime(long time) throws RemoteException
        {
            doUpdateProcessByTime(time);
        }

    }

    private void initTerminal()
    {
        if(!NetWorkUtils.isNetConnected(mContext))
        {
            return;
        }
        if(!isInitRunning && !isInitFinished)
        {
            isInitRunning = true;
            try
            {
                String terminalId = LocalDataEntity.newInstance(mContext).getDeviceId();
                HttpClient.initTerminal(terminalId, new HttpCallback<InitTerminalResultBean>() {
                    @Override
                    public void onSuccess(InitTerminalResultBean initTerminalResultBean)
                    {
                        isInitRunning = false;
                        if(initTerminalResultBean != null && initTerminalResultBean.isSuccess())
                        {
                            curTerminalRunstatusInfo = initTerminalResultBean.getData().getTrs();

                            if(curTerminalRunstatusInfo == null)
                            {
                                //设备在后台已被删除，如果是盒子，那么重新调用login接口绑定到默认店铺
                                String localDevId = LocalDataEntity.newInstance(mContext).getDeviceId();
                                addBox(localDevId);
                            }
                            else
                            {
                                String terminalId = initTerminalResultBean.getData().getTrs().getTerminalId();
                                String terminalName = initTerminalResultBean.getData().getTrs().getShopname();
                                String deviceCode = initTerminalResultBean.getData().getTrs().getShopcode();
                                LocalDataEntity.newInstance(mContext).setDeviceId(terminalId);
                                LocalDataEntity.newInstance(mContext).setDeviceName(terminalName);
                                LocalDataEntity.newInstance(mContext).setDeviceCode(deviceCode);
                                if(TextUtils.isEmpty(terminalId))
                                {
                                    register();
                                }
                                else
                                {
                                    callback(PushModelConstant.CALL_BACK_REGISTER_FINISH, terminalId + " ", terminalName+ "   ", deviceCode+ "   ");
                                }
                            }
                            try
                            {
                                String serverTime = initTerminalResultBean.getData().getSystemTime();
                                if(!TextUtils.isEmpty((String) serverTime))
                                    updateLocalTime(Long.parseLong((String) serverTime));
                                isInitFinished = true;
                            }
                            catch (Exception e)
                            {
                                if(e != null)
                                    reportError(e.getMessage());
                                else
                                    reportError("init terminal exception");
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFail(Exception e) {
                        isInitRunning = false;
                        if(e != null)
                            reportError(e.getMessage());
                        else
                            reportError("init terminal exception");
                    }
                });
            }
            catch (Exception e)
            {
                isInitRunning = false;
                if(e != null)
                    reportError(e.getMessage());
                else
                    reportError("init terminal exception");
            }
        }
    }

    private void updateLocalTime(long time)
    {
        if(time > 0)
            curServerTime = time;

        if(curServerTime == 0)
        {
            curServerTime = getCurServerTimeFromLocal();
            if(curServerTime <= 0)
                curServerTime = System.currentTimeMillis();
        }

        callback(PushModelConstant.CALL_BACK_NOTIFY_TIME_UPDATE, curServerTime + "", null, null);
    }

    private void register()
    {
        if(!NetWorkUtils.isNetConnected(mContext))
        {
            return;
        }
        if(!isRegisterRunning && !isRegisterFinished)
        {
            isRegisterRunning = true;
            try
            {
                HttpClient.register(mContext,new HttpCallback<RegisterTerminalResultBean>() {
                    @Override
                    public void onSuccess(RegisterTerminalResultBean registerTerminalResultBean)
                    {
                        isRegisterRunning = false;
                        if(registerTerminalResultBean != null && registerTerminalResultBean.isSuccess())
                        {
                            try
                            {
                                curTerminalRunstatusInfo = registerTerminalResultBean.getData();
                                if(curTerminalRunstatusInfo != null)
                                    callback(PushModelConstant.CALL_BACK_REGISTER_FINISH, curTerminalRunstatusInfo.getTerminalId()
                                            + "   "+curTerminalRunstatusInfo.getShopname(), null, null);
                                isRegisterFinished = true;
                            }
                            catch (Exception e)
                            {
                                if(e != null)
                                    reportError(e.getMessage());
                                else
                                    reportError("register exception");
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFail(Exception e) {
                        isRegisterRunning = false;
                        if(e != null)
                            reportError(e.getMessage());
                        else
                            reportError("register exception");
                    }
                });
            }
            catch (Exception e)
            {
                isRegisterRunning = false;
                if(e != null)
                    reportError(e.getMessage());
                else
                    reportError("register exception");
            }
        }
    }

    private void addBox(String terminalId)
    {
        if(!NetWorkUtils.isNetConnected(mContext))
        {
            return;
        }
        String oldId = LocalDataEntity.newInstance(mContext).getOldDeviceId();

        if(!isLoginRunning && !isLoginFinished)
        {
            isLoginRunning = true;
            try
            {
                HttpClient.addBox(mContext,terminalId,oldId, new HttpCallback<RegisterTerminalResultBean>() {
                    @Override
                    public void onSuccess(RegisterTerminalResultBean registerTerminalResultBean)
                    {
                        isLoginRunning = false;
                        if(registerTerminalResultBean != null && registerTerminalResultBean.isSuccess())
                        {
                            try
                            {
                                curTerminalRunstatusInfo = registerTerminalResultBean.getData();
                                if(curTerminalRunstatusInfo != null)
                                {
                                    String terminalId = curTerminalRunstatusInfo.getTerminalId();
                                    String terminalName = curTerminalRunstatusInfo.getShopname();
                                    String deviceCode = curTerminalRunstatusInfo.getShopcode();
                                    LocalDataEntity.newInstance(mContext).setDeviceId(terminalId);
                                    LocalDataEntity.newInstance(mContext).setDeviceName(terminalName);
                                    LocalDataEntity.newInstance(mContext).setDeviceCode(deviceCode);
                                    callback(PushModelConstant.CALL_BACK_REGISTER_FINISH, curTerminalRunstatusInfo.getTerminalId()
                                            , curTerminalRunstatusInfo.getShopname(), null);
                                }
                                isLoginFinished = true;
                            }
                            catch (Exception e)
                            {
                                if(e != null)
                                    reportError(e.getMessage());
                                else
                                    reportError("addbox exception");
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFail(Exception e) {
                        isLoginRunning = false;
                        if(e != null)
                            reportError(e.getMessage());
                        else
                            reportError("addbox exception");
                    }
                });
            }
            catch (Exception e)
            {
                if(e != null)
                    reportError(e.getMessage());
                else
                    reportError("addbox exception");
                isLoginRunning = false;
            }
        }
    }

    private void updatePlayReport(final MediaInfor mediaInfor)
    {
        if(!NetWorkUtils.isNetConnected(mContext))
        {
            return;
        }
        String dataId = mediaInfor.getMediaId();
        String playType = "0";
        if(mediaInfor.getMediaType().equals(MediaInfor.MEDIA_TYPE_MEDIA))
            playType = "0";
        else if(mediaInfor.getMediaType().equals(MediaInfor.MEDIA_TYPE_PUSH))
            playType = "1";

        String dataType = "1";
        if(mediaInfor.getMediaType().equals(MediaInfor.MEDIA_TYPE_ADV))
            dataType = "2";

        updatePlayReport(dataId, playType, dataType);

    }
    private void updatePlayReport(final String dataId, final String playType, final String dataType)
    {
        HttpClient.playRecordReport(mContext, dataId, playType, dataType, new CommonCallback<BaseResultBean>(BaseResultBean.class) {
        @Override
        public void onResponse(BaseResultBean response, int id) {
            /*if(response != null && response.isSuccess())
            {

            }
            else
            {
                updatePlayReport(dataId, playType, dataType);
            }*/
        }

        @Override
        public void onError(Call call, Exception e, int id) {
            //updatePlayReport(dataId, playType, dataType);
        }

        @Override
        public void onAfter(int id) {

        }});
    }

    private boolean isGetDevStatusRunning = false;
    private void getDevStatus()
    {
        if(!NetWorkUtils.isNetConnected(mContext))
        {
            devStatusFailProcess("no net work");
            return;
        }
        String terminalId = LocalDataEntity.newInstance(mContext).getDeviceId();
        if(!isGetDevStatusRunning)
        {
            isGetDevStatusRunning = true;
            try
            {

                HttpClient.getDevStatus(terminalId,playingSongId,playingSong,playingSongType,playingPos+"",playSeek+"",netInfo, new HttpCallback<DeviceStatusResultBean>() {
                    @Override
                    public void onSuccess(DeviceStatusResultBean deviceStatusResultBean)
                    {
                        isGetDevStatusRunning = false;
                        if(deviceStatusResultBean == null)
                        {
                            devStatusFailProcess("deviceStatusResultBean is null");
                            return;
                        }
                        if(deviceStatusResultBean.isSuccess())
                        {
                            try
                            {
                                DeviceStatusInfor tempInfo  = deviceStatusResultBean.getData();
                                devStatusSuccessProcess(tempInfo);
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                                String error = "加载数据失败";
                                if(e != null)
                                    error = e.getMessage();
                                devStatusFailProcess(error);
                                reportError(error);
                            }
                        }
                        else
                            devStatusFailProcess(deviceStatusResultBean.getMessage());
                    }
                    @Override
                    public void onFail(Exception e) {
                        isGetDevStatusRunning = false;
                        devStatusFailProcess(e.getMessage());
                    }
                });
            }
            catch (Exception e)
            {
                isGetDevStatusRunning = false;
                String error = "";
                if(e != null)
                    error = e.getMessage();
                else
                    error = "加载数据失败";
                devStatusFailProcess(error);
                reportError(error);
            }
        }
    }

    private void devStatusFailProcess(String error)
    {
        curPlanGetFailProcess();
        curAdPlanGetFailProcess();

        checkFailCount++;
        if(curServerTime <= 0 && checkFailCount >= 2)
            updateLocalTime(0);
        callback(PushModelConstant.CALL_BACK_PUSH_FAIL, checkFailCount + "", error, null);
    }
    private boolean isInternalGetPlan = true;
    private void devStatusSuccessProcess(final DeviceStatusInfor info)
    {
        callback(PushModelConstant.CALL_BACK_PUSH_SUCCESS, null, null, null);

        try
        {
            curLocalAdPlanInfor = null;
            mCurPlanInforLocal = null;
            checkFailCount = 0;
            curDeviceStatusInfor = info;
            checkUpdateProcess((DeviceStatusInfor) info);
            if(doGetPushMusic(curDeviceStatusInfor))
            {
                //获取推送歌曲列表
                return;
            }

            if(curServerTime > 0)//如果获取到了系统时间
            {
                if(!isPlayStop((DeviceStatusInfor) info))
                {
                    if(isInternalGetPlan)
                    {
                        playPlanProcess((DeviceStatusInfor) info);
                        isInternalGetPlan = false;
                    }
                    else
                    {
                        adPlanProcess((DeviceStatusInfor) info);
                        isInternalGetPlan = true;
                    }
                }
                wifiProcess((DeviceStatusInfor) info);
                volumeProcess((DeviceStatusInfor) info);
            }

            TME_Process(info.getSourceType());
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block

            e.printStackTrace();
            if(e != null)
                reportError(e.getMessage());
            else
                reportError("devStatusSuccessProcess exception");
        }
    }

    private void playPlanProcess(DeviceStatusInfor deviceStatusInfor)
    {
        if(deviceStatusInfor == null)
            return;
        String localPlanId = LocalDataEntity.newInstance(mContext).getPlanId();
        String planId = deviceStatusInfor.getPlanId();
        Log.e(TAG,"############################-->planId:"+planId);
        if(TextUtils.isEmpty(planId) || planId.equals("0"))
        {
            //没有播放计划了，清除播放计划的数据
            clearMediaPlan();
            return;
        }

        if((TextUtils.isEmpty(localPlanId) || !localPlanId.equals(planId)))
        {
            getCurPlan(planId);
        }
        else
        {
            String musicUpdateTime = deviceStatusInfor.getLastMusicUpdate();
            String localMusicUpdateTime = LocalDataEntity.newInstance(mContext).getMusicUpdateTime();
            if(!TextUtils.isEmpty(musicUpdateTime) && !musicUpdateTime.equals(localMusicUpdateTime))
            {
                getCurPlan(planId);
            }
            else if(!isCurPlanGetFinished)
            {
                getCurPlan(planId);
            }
            else if(!mCurPlayMediaManager.isCurPlanNone() && mCurPlayMediaManager.getCurPlayMedias().size() == 0)
            {
                //从本地获取列表异常，就从服务器获取当前播放列表
                getCurPlayMusics();
            }
        }
    }
    private AudioManager mAudioManager = null;
    private void volumeProcess(DeviceStatusInfor devStatus)
    {
        //isVolumeSetEnable
        boolean b = LocalDataEntity.newInstance(mContext).isVolumeSetOpen();
        if(b)
        {
            if(mAudioManager == null)
                mAudioManager = (AudioManager)getSystemService(mContext.AUDIO_SERVICE);

            int volume = 0;
            if(!TextUtils.isEmpty(devStatus.getVolume()))
                volume = Integer.parseInt(devStatus.getVolume());
            int curVolume = getCurrentVolume();
            //int max = mAudioManager.getStreamMaxVolume( AudioManager.STREAM_MUSIC );
            if((volume >= 0) && (volume != curVolume))
            {
                setCurrentVolume(volume);
            }

            /*if(!TextUtils.isEmpty(devStatus.getVodFlag()))
            {
                Constant.PlayPermission = devStatus.getVodFlag();
                getLocalData().setPlayPermission(devStatus.getVodFlag());
            }*/
        }
    }

    private int getCurrentVolume()
    {
        //int max = mAudioManager.getStreamMaxVolume( AudioManager.STREAM_MUSIC );
        return mAudioManager.getStreamVolume( AudioManager.STREAM_MUSIC );
    }
    private void setCurrentVolume(int volume)
    {
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
    }

    private void wifiProcess(DeviceStatusInfor deviceStatusInfor)
    {
        if(deviceStatusInfor.getWifiFlag().equals("1"))
        {
            String shopCode = LocalDataEntity.newInstance(mContext).getDeviceCode();
            String devId = LocalDataEntity.newInstance(mContext).getDeviceId();
            if(TextUtils.isEmpty(shopCode))
                return;
            if(!NetWorkUtils.isNetConnected(mContext))
            {
                return;
            }
            HttpClient.getWifiInfo(shopCode,devId, new HttpCallback<WifiInfoResultBean>() {
                @Override
                public void onSuccess(WifiInfoResultBean wifiInfoResultBean) {
                    if(wifiInfoResultBean != null && wifiInfoResultBean.isSuccess())
                    {
                        String wifiName = wifiInfoResultBean.getData().getWifiName();
                        String wifiPwd = wifiInfoResultBean.getData().getWifiPassword();
                        callback(PushModelConstant.CALL_BACK_ON_WIFI_CONGFIG_NOTYFY, wifiName, wifiPwd, null);
                    }
                    else
                    {

                    }
                }

                @Override
                public void onFail(Exception e) {
                    if(e != null)
                        reportError(e.getMessage());
                    else
                        reportError("wifiProcess exception");
                }
            });
        }
    }

    private void adPlanProcess(DeviceStatusInfor deviceStatusInfor)
    {

        String localAdPlanId = LocalDataEntity.newInstance(mContext).getAdPlanId();
        String adPlanId = deviceStatusInfor.getAdplanId();

        if(TextUtils.isEmpty(adPlanId) || adPlanId.equals("0"))
        {
            //没有广告计划了，清除广告计划的数据
            clearAdPlan();
            return;
        }
        if((TextUtils.isEmpty(localAdPlanId) || !adPlanId.equals(localAdPlanId)))
        {
            getCurAdPlan(adPlanId);
        }
        else
        {
            String adPlanUpdateTime = deviceStatusInfor.getAdUpdateTime();
            String localAdPlanUpdateTime = LocalDataEntity.newInstance(mContext).getAdPlanTime();
            if(TextUtils.isEmpty(localAdPlanUpdateTime) || !adPlanUpdateTime.equals(localAdPlanUpdateTime))
            {
                getCurAdPlan(adPlanId);
            }
            else if(!isCurAdPlanGetFinished)
                getCurAdPlan(adPlanId);
        }
    }

    private boolean isPlayStop(DeviceStatusInfor deviceStatusInfor)
    {

        String planId = deviceStatusInfor.getPlanId();
        String adPlanId  = deviceStatusInfor.getAdplanId();
        planIdReport = planId +"/" + adPlanId;
        if((TextUtils.isEmpty(planId) || planId.equals("0")) && (TextUtils.isEmpty(adPlanId) || adPlanId.equals("0")))
        {
            //没有播放计划了，清除播放计划的数据
            clearMediaPlan();
            //没有广告计划了，清除广告计划的数据
            clearAdPlan();
            callback(PushModelConstant.CALL_BACK_ON_PLAY_STATUS, "2", null, null);
            return true;
        }
        else
        {
            //callback(PushModelConstant.CALL_BACK_ON_PLAY_OPEN, null, null, null);
            return false;
        }
    }

    private void clearMediaPlan()
    {
        if(mCurPlayMediaManager != null)
            mCurPlayMediaManager.clearNormalPlan();
    }
    private void clearAdPlan()
    {
        if(mCurPlayMediaManager != null)
            mCurPlayMediaManager.clearAdPlan();

        mCurPlayMediaManager.setAdPlanInfor(null);
    }

    private boolean isFirstGetPushMedias = true;

    private boolean doGetPushMusic(DeviceStatusInfor deviceStatusInfor)
    {
        if(deviceStatusInfor == null)
            return false;

        String pushStatus = deviceStatusInfor.getPlayCmd();

        if(!TextUtils.isEmpty(pushStatus) && pushStatus.equals("3") || isFirstGetPushMedias)
        {
            getPushMedias();
            return true;
        }
        return false;
    }

    private void getCurAdPlan(String adPlanId)
    {

        if(!isInitFinished)
        {
            Log.e(TAG, "device not init");
            return;
        }
        if(!TextUtils.isEmpty(adPlanId))
        {
            isCurAdPlanGetFinished = false;

            try
            {
                if(!NetWorkUtils.isNetConnected(mContext))
                {
                    curAdPlanGetFailProcess();
                    return;
                }

                HttpClient.getCurAdPlan(adPlanId, new HttpCallback<AdPlanResultBean>() {
                    @Override
                    public void onSuccess(AdPlanResultBean registerTerminalResultBean)
                    {

                        if(registerTerminalResultBean != null && registerTerminalResultBean.isSuccess())
                        {
                            try
                            {
                                isCurAdPlanGetFinished = true;

                                String adPlanId = curDeviceStatusInfor.getAdplanId();//registerTerminalResultBean.getData().getId()
                                String adPlanUpdateTime = curDeviceStatusInfor.getAdUpdateTime();//
                                LocalDataEntity.newInstance(getApplicationContext()).setAdPlanId(adPlanId);
                                LocalDataEntity.newInstance(getApplicationContext()).setAdPlanTime(adPlanUpdateTime);
                                CurAdPlanInfor tempInfo  = registerTerminalResultBean.getData();
                                updateAdPlanInfor(tempInfo,true);
                            }
                            catch (Exception e)
                            {

                                e.printStackTrace();
                                curAdPlanGetFailProcess();
                                if(e != null)
                                    reportError(e.getMessage());
                                else
                                    reportError("getCurAdPlan exception");
                            }
                        }

                    }

                    @Override
                    public void onFail(Exception e) {

                        curAdPlanGetFailProcess();
                        if(e != null)
                            reportError(e.getMessage());
                        else
                            reportError("getCurAdPlan exception");
                    }
                });
            }
            catch (Exception e)
            {
                curAdPlanGetFailProcess();
                if(e != null)
                    reportError(e.getMessage());
                else
                    reportError("getCurAdPlan exception");
            }

        }
    }

    private CurAdPlanInfor getCurAdPlanInforFromLocal()
    {
        CurAdPlanInfor tempInfor = null;
        Gson gson = new Gson();
        String planJson = DBMediaHelper.getInstance().getResposeInfo(HttpAPI.GET_CUR_ADV_PLAN);
        if(!TextUtils.isEmpty(planJson))
        {
            AdPlanResultBean curPlanResultBean = (AdPlanResultBean) gson.fromJson(planJson, AdPlanResultBean.class);
            tempInfor = curPlanResultBean.getData();
        }
        return tempInfor;
    }

    private void updateAdPlanInfor(CurAdPlanInfor tempInfo, boolean isSucccess)
    {
        if(tempInfo != null)
        {
            tempInfo.decodeUrls();
            mCurPlayMediaManager.setAdPlanInfor(tempInfo);
        }

        mCurPlayMediaManager.updateAdPlanMedias(getApplicationContext());

        if(isSucccess)
            callback(PushModelConstant.CALL_BACK_GET_CUR_ADV_PLAN, null + "", null, null);
    }

    private int adPlanGetFailCount = 0;
    private CurAdPlanInfor curLocalAdPlanInfor = null;
    private boolean isGetCurAdPlanInforFromLocalRunning = false;
    private void curAdPlanGetFailProcess()
    {
        if(adPlanGetFailCount == 1)
        {
            if(isGetCurAdPlanInforFromLocalRunning)
                return;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    isGetCurAdPlanInforFromLocalRunning = true;
                    if(curLocalAdPlanInfor == null)
                        curLocalAdPlanInfor = getCurAdPlanInforFromLocal();
                    updateAdPlanInfor(curLocalAdPlanInfor,false);
                    isGetCurAdPlanInforFromLocalRunning = false;
                }
            }).start();
        }
        else
            adPlanGetFailCount ++;
    }

    private void getPushMedias()
    {
        String devId = LocalDataEntity.newInstance(mContext).getDeviceId();
        if(!TextUtils.isEmpty(devId))
        {
            try
            {
                if(!NetWorkUtils.isNetConnected(mContext))
                {
                    return;
                }
                HttpClient.getPushMedias(devId, new HttpCallback<PushMediaResultBean>() {
                    @Override
                    public void onSuccess(PushMediaResultBean curPlanResultBean)
                    {
                        if(curPlanResultBean != null && curPlanResultBean.isSuccess())
                        {
                            try
                            {
                                isFirstGetPushMedias = false;
                                pushMedias = curPlanResultBean.getDataConvert();
                                if(pushMedias != null && pushMedias.size() > 0)
                                {
                                    callback(PushModelConstant.CALL_BACK_ON_PUSH_MEDIA_NOTYFY,  "", null, null);
                                }
                            }
                            catch (Exception e)
                            {

                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFail(Exception e)
                    {

                    }
                });
            }
            catch (Exception e)
            {

            }
        }
    }

    private void showDebugInfor(String info)
    {
        Log.e(TAG, "showDebugInfor: "+info );
    }


    private int curPlanRuninngCount = 0;
    private volatile boolean isFirstGetPlan = true;
    private boolean isGetCurPlanRunning = false;
    private void getCurPlan(final String planId)
    {
        if(!NetWorkUtils.isNetConnected(mContext))
        {
            curPlanGetFailProcess();
            return;
        }
        if(!isInitFinished)
        {
            Log.e(TAG, "device not init");
            return;
        }
        if(TextUtils.isEmpty(planId) || planId.equals("0"))
        {
            Log.e(TAG, "planId  is  empty");
            return;
        }
        if(!isGetCurPlanRunning || !isCurPlanGetFinished)
        {
            isGetCurPlanRunning = true;

            curPlanRuninngCount = 0;
            isFirstGetPlan = false;
            LocalDataEntity.newInstance(mContext).setPlanTime("");
            try
            {

                HttpClient.getCurPlan(planId, new HttpCallback<CurPlanResultBean>() {
                    @Override
                    public void onSuccess(CurPlanResultBean curPlanResultBean)
                    {
                        if(curPlanResultBean != null && curPlanResultBean.isSuccess())
                        {
                            try
                            {
                                clearMediaPlan();

                                updatePlanPlanInfor(curPlanResultBean.getData(), true);
                                //onGetPlanSuccessProcess(curPlanResultBean.getData());

                                isCurPlanGetFinished = true;
                                isGetCurPlanRunning = false;
                            }
                            catch (Exception e)
                            {
                                isGetCurPlanRunning = false;
                                curPlanGetFailProcess();
                                e.printStackTrace();
                                if(e != null)
                                    reportError(e.getMessage());
                                else
                                    reportError("getCurPlan exception");
                            }
                        }
                    }

                    @Override
                    public void onFail(Exception e)
                    {
                        isGetCurPlanRunning = false;
                        curPlanGetFailProcess();
                        if(e != null)
                            reportError(e.getMessage());
                        else
                            reportError("getCurPlan exception");
                    }
                });
            }
            catch (Exception e)
            {
                isGetCurPlanRunning = false;
                curPlanGetFailProcess();
                if(e != null)
                    reportError(e.getMessage());
                else
                    reportError("getCurPlan exception");
            }
        }
        else
        {
            showDebugInfor(curPlanRuninngCount+"");
            curPlanRuninngCount ++;
            if(curPlanRuninngCount > 15)
            {
                curPlanRuninngCount = 0;
                isGetCurPlanRunning = false;
            }
        }
    }

    public void getCurPlayMusics()
    {
        if(!NetWorkUtils.isNetConnected(mContext))
        {
            return;
        }

        String devId = LocalDataEntity.newInstance(mContext).getDeviceId();
        if(!TextUtils.isEmpty(devId))
        {
            HttpClient.getCurPlayMusics(devId, new HttpCallback<MusicListResultBean>() {
                @Override
                public void onSuccess(MusicListResultBean musicListResultBean) {
                    if(musicListResultBean != null && musicListResultBean.isSuccess())
                    {
                        List<MediaInfor> mediaInfoList = musicListResultBean.getData();
                        if(mediaInfoList != null)
                        {
                            mCurPlayMediaManager.fillPlayList(mediaInfoList);
                            callback(PushModelConstant.CALL_BACK_GET_CUR_PLAN, "" + "", null, null);
                        }
                    }

                }

                @Override
                public void onFail(Exception e) {

                }
            });
        }
    }

    private void onGetPlanFinishProcess(CurPlanInfor mCurPlanInfor, boolean isSuccess)
    {
        if(curDeviceStatusInfor != null)
        {
            String planId = curDeviceStatusInfor.getPlanId();
            String planUpdateTime = curDeviceStatusInfor.getLastMusicUpdate();
            LocalDataEntity.newInstance(getApplicationContext()).setPlanId(planId);
            LocalDataEntity.newInstance(getApplicationContext()).setMusicUpdateTime(planUpdateTime);
        }
        else
        {
            String planId = mCurPlanInfor.getId();
            LocalDataEntity.newInstance(getApplicationContext()).setPlanId(planId);
        }
        mCurPlayMediaManager.updatePlayPlanMedias(getApplicationContext());
        mCurPlayMediaManager.resetMediaPlanLocalStatus();

        if(isSuccess)
            callback(PushModelConstant.CALL_BACK_GET_CUR_PLAN, "" + "", null, null);
    }

    private CurPlanInfor mCurPlanInforLocal = null;
    private boolean isGetCurPlanInforFromLocalRunningf = false;
    private void curPlanGetFailProcess()
    {
        if(isGetCurPlanInforFromLocalRunningf)
            return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                isGetCurPlanInforFromLocalRunningf = true;
                if(mCurPlanInforLocal == null)
                    mCurPlanInforLocal = getCurPlanInforFromLocal();
                updatePlanPlanInfor(mCurPlanInforLocal, false);
                isGetCurPlanInforFromLocalRunningf = false;

            }
        }).start();
    }

    private long getCurServerTimeFromLocal()
    {
        List<CurPlanSubInfor> subPlanInfos = DBMediaHelper.getInstance().getCurSubPlanInfors();
        return subPlanInfos.get(0).getStart_time_sort();

    }

    private void updatePlanPlanInfor(CurPlanInfor curPlanInfor, boolean isSuccess)
    {
        mCurPlayMediaManager.updateCurServerTime(curServerTime);

        if(curPlanInfor != null)
        {
            onGetPlanFinishProcess(curPlanInfor,isSuccess);
        }
    }

    private CurPlanInfor getCurPlanInforFromLocal()
    {
        CurPlanInfor tempInfor = null;
        Gson gson = new Gson();
        String planJson = DBMediaHelper.getInstance().getResposeInfo(HttpAPI.GET_CUR_PLAN);
        if(!TextUtils.isEmpty(planJson))
        {
            CurPlanResultBean curPlanResultBean = (CurPlanResultBean) gson.fromJson(planJson, CurPlanResultBean.class);
            tempInfor = curPlanResultBean.getData();
        }
        return tempInfor;
    }

    private void reportError(String error)
    {
        if(NetWorkUtils.isNetConnected(mContext))
        {
            HttpClient.errorReport(mContext,error);
        }
    }

    private void checkUpdateProcess(DeviceStatusInfor deviceStatusInfor)
    {
        if(deviceStatusInfor == null)
            return;

        if(!NetWorkUtils.isNetConnected(mContext))
        {
            return;
        }

        if(!checkUpdateRunning)
        {
            String softUpdateFlag  = deviceStatusInfor.getSoftUpdateFlag();
            if(!TextUtils.isEmpty(softUpdateFlag) && softUpdateFlag.equals("1"))
            {
                String id = LocalDataEntity.newInstance(mContext).getDeviceId();
                checkUpdateRunning = true;
                HttpClient.checkUpdate(id,new HttpCallback<SoftVersionResultBean>()
                {
                    @Override
                    public void onSuccess(SoftVersionResultBean initTerminalResultBean)
                    {
                        if(initTerminalResultBean != null && initTerminalResultBean.isSuccess())
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
                                showLog("检测到了新版本：" + initTerminalResultBean.getData().getVersion());
                                callback(PushModelConstant.CALL_BACK_ON_UPDATE_CHECK,  initTerminalResultBean.getData().getVersion()
                                        , initTerminalResultBean.getData().getVersionNum(), initTerminalResultBean.getData().getUrl());
                            }
                            else
                                showLog("当前版本是最新的");
                        }
                        else
                        {
                            if(initTerminalResultBean != null)
                                reportError(initTerminalResultBean.getMessage());
                            else
                                reportError("检测更新失败，返回数据空");
                        }
                        checkUpdateRunning = false;
                    }

                    @Override
                    public void onFail(Exception e)
                    {
                        checkUpdateRunning = false;

                        if(e != null)
                            reportError(e.getMessage());
                        else
                            reportError("check update fail");
                    }
                });
            }
        }
    }

    @Override
    public void onSpaceCheck(long size)
    {
        // TODO Auto-generated method stub
        callback(PushModelConstant.CALL_BACK_PUSH_SPACE_CHECK, size + "", null, null);
    }


    /**************TME***************/
    private boolean isTMEOauthed = false;
    private TMEOauthInfo mTMERegisterInfo = null;
    private TMEOauthInfo getTMERegisterInfo()
    {
        /*if(mTMERegisterInfo == null)
            return new TMERegisterInfo();*/
        return mTMERegisterInfo;
    }
    private void TME_Process(String sourceType)
    {
        //腾讯数据上报开关
        if(!TextUtils.isEmpty(sourceType) && sourceType.equals("1"))
        {
            if(mTMERegisterInfo != null)
            {
                TME_Register();
            }
            else
                TME_Oauth();
        }

    }
    private void TME_Oauth()
    {
        if(!isTMEOauthed)
        {
            if(!NetWorkUtils.isNetConnected(mContext))
            {
                return;
            }
            HttpClient.TME_Oauth(new HttpCallback<TMEOauthInfo>() {
                @Override
                public void onSuccess(TMEOauthInfo tmeRegisterCallBack) {
                    mTMERegisterInfo = tmeRegisterCallBack;
                    /*String access_token = tmeRegisterCallBack.getAccess_token();
                    String token_type = tmeRegisterCallBack.getToken_type();
                    String expires_in = tmeRegisterCallBack.getExpires_in();
                    String scope = tmeRegisterCallBack.getScope();*/
                    isTMEOauthed = true;
                }

                @Override
                public void onFail(Exception e) {

                }
            });
        }
    }

    private boolean isTMERegistered = false;
    private void TME_Register()
    {
        if(!isTMERegistered)
        {
            if(!NetWorkUtils.isNetConnected(mContext))
            {
                return;
            }
            String addr = LocalDataEntity.newInstance(mContext).getDeviceAddr();
            String udid = LocalDataEntity.newInstance(mContext).getDeviceCode();
            HttpClient.TME_Register(udid, addr, null, new HttpCallback<TMECommonResultBean>() {
                @Override
                public void onSuccess(TMECommonResultBean initTerminalResultBean) {
                    isTMERegistered = true;
                }

                @Override
                public void onFail(Exception e) {

                }
            });
        }
    }
}