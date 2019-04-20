package com.znt.push.httpmodel;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.zhy.http.okhttp.OkHttpUtils;
import com.znt.lib.bean.AdPlanResultBean;
import com.znt.lib.bean.CurPlanResultBean;
import com.znt.lib.bean.DeviceInfor;
import com.znt.lib.bean.DeviceStatusResultBean;
import com.znt.lib.bean.InitTerminalResultBean;
import com.znt.lib.bean.MusicListResultBean;
import com.znt.lib.bean.PushMediaResultBean;
import com.znt.lib.bean.RegisterTerminalResultBean;
import com.znt.lib.bean.SoftVersionResultBean;
import com.znt.lib.bean.TMECommonResultBean;
import com.znt.lib.bean.TMEOauthInfo;
import com.znt.lib.bean.WifiInfoResultBean;
import com.znt.lib.utils.SystemUtils;
import com.znt.push.entity.Constant;
import com.znt.push.entity.LocalDataEntity;
import com.znt.push.http.TMEHttpAPI;

import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;

/**
 * Created by hzwangchenyan on 2017/2/8.
 */
public class HttpClient extends HttpAPI {


    static {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(new HttpInterceptor())
                .build();
        OkHttpUtils.initClient(okHttpClient);
    }

    public static void initTerminal(String terminalId, @NonNull final HttpCallback<InitTerminalResultBean> callback) {

        OkHttpUtils.post().url(INIT_TERMINAL)
                .addParams("terminalId", terminalId)
                .addParams("code", Constant.deviceCode)
                .build()
                .execute(new CommonCallback<InitTerminalResultBean>(InitTerminalResultBean.class) {
                    @Override
                    public void onResponse(InitTerminalResultBean response, int id) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void checkUpdate( String id,@NonNull final HttpCallback<SoftVersionResultBean> callback) {

        OkHttpUtils.post().url(CHECK_UPDATE)
                .addParams("id", id)
                .addParams("softCode", Constant.softCode)
                .build()
                .execute(new CommonCallback<SoftVersionResultBean>(SoftVersionResultBean.class) {
                    @Override
                    public void onResponse(SoftVersionResultBean response, int id) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void register(Context context, @NonNull final HttpCallback<RegisterTerminalResultBean> callback) {

        String shopName = LocalDataEntity.newInstance(context).getDeviceName();
        String shopCode = "";
        String userShopCode = "";
        String videoWhirl = "";
        String wifiName = "";
        String wifiPassword = "";
        String netInfo = "";
        String longitude = "";
        String latitude = "";
        String address = "";
        String country = "";
        String province = "";
        String city = "";
        String region = "";


        Log.e("", "********************************softVersion:" + SystemUtils.getVersionCode(context) + "");

        OkHttpUtils.post().url(REGISTER)
                .addParams("code", Constant.deviceCode)
                .addParams("shopName", shopName)
                .addParams("shopCode", shopCode)
                .addParams("userShopCode", userShopCode)
                //.addParams("bindCode", Constant.DEFAULT_BIND_CODE)
                .addParams("softCode", Constant.softCode)
                .addParams("softVersion", SystemUtils.getVersionCode(context) + "")
                .addParams("hardVersion", android.os.Build.MODEL + "")
                .addParams("terminalType", "2")
                .addParams("volume", SystemUtils.getCurrentVolume(context) + "")
                .addParams("videoWhirl", videoWhirl)
                .addParams("wifiName", wifiName)
                .addParams("wifiPassword", wifiPassword)
                .addParams("ip", SystemUtils.getIP())
                .addParams("netInfo", netInfo)
                .addParams("longitude", longitude)
                .addParams("latitude", latitude)
                .addParams("address", address)
                .addParams("country", country)
                .addParams("province", province)
                .addParams("city", city)
                .addParams("region", region)
                .build()
                .execute(new CommonCallback<RegisterTerminalResultBean>(RegisterTerminalResultBean.class) {
                    @Override
                    public void onResponse(RegisterTerminalResultBean response, int id) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }



    public static void addBox(Context context, String id, String oldId, @NonNull final HttpCallback<RegisterTerminalResultBean> callback) {

        String shopName = LocalDataEntity.newInstance(context).getDeviceName();
        String shopCode = "";
        String userShopCode = "";
        String videoWhirl = "";
        String wifiName = "";
        String wifiPassword = "";
        String netInfo = "";
        String longitude = "";
        String latitude = "";
        String address = "";
        String country = "";
        String province = "";
        String city = "";
        String region = "";

        //设备在后台已被删除，如果是盒子，那么重新调用login接口绑定到默认店铺
        OkHttpUtils.post().url(ADD_BOX)
                .addParams("id", id)
                .addParams("code", Constant.deviceCode)
                .addParams("shopName", shopName)
                .addParams("shopCode", shopCode)
                .addParams("userShopCode", userShopCode)
                //.addParams("bindCode", Constant.DEFAULT_BIND_CODE)
                .addParams("softCode", Constant.softCode)
                .addParams("softVersion", SystemUtils.getVersionCode(context) + "")
                .addParams("hardVersion", android.os.Build.MODEL + "")
                .addParams("terminalType", "2")
                .addParams("volume", SystemUtils.getCurrentVolume(context) + "")
                .addParams("videoWhirl", videoWhirl)
                .addParams("wifiName", wifiName)
                .addParams("wifiPassword", wifiPassword)
                .addParams("ip", SystemUtils.getIP())
                .addParams("netInfo", netInfo)
                .addParams("longitude", longitude)
                .addParams("latitude", latitude)
                .addParams("address", address)
                .addParams("country", country)
                .addParams("province", province)
                .addParams("city", city)
                .addParams("region", region)
                .addParams("oldId", oldId)
                .build()
                .execute(new CommonCallback<RegisterTerminalResultBean>(RegisterTerminalResultBean.class) {
                    @Override
                    public void onResponse(RegisterTerminalResultBean response, int id) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });

    }

    public static void login(Context context, String id, String bindCode,String shopName, @NonNull final HttpCallback<RegisterTerminalResultBean> callback) {

        String shopCode = "";
        String userShopCode = "";
        String videoWhirl = "";
        String wifiName = "";
        String wifiPassword = "";
        String netInfo = "";
        String longitude = "";
        String latitude = "";
        String address = "";
        String country = "";
        String province = "";
        String city = "";
        String region = "";

        //设备在后台已被删除，如果是盒子，那么重新调用login接口绑定到默认店铺
        OkHttpUtils.post().url(LOGIN)
                .addParams("id", id)
                .addParams("code", Constant.deviceCode)
                .addParams("shopName", shopName)
                .addParams("shopCode", shopCode)
                .addParams("userShopCode", userShopCode)
                .addParams("bindCode", bindCode)
                .addParams("softCode", Constant.softCode)
                .addParams("softVersion", SystemUtils.getVersionCode(context) + "")
                .addParams("hardVersion", android.os.Build.MODEL + "")
                .addParams("terminalType", "2")
                .addParams("volume", SystemUtils.getCurrentVolume(context) + "")
                .addParams("videoWhirl", videoWhirl)
                .addParams("wifiName", wifiName)
                .addParams("wifiPassword", wifiPassword)
                .addParams("ip", SystemUtils.getIP())
                .addParams("netInfo", netInfo)
                .addParams("longitude", longitude)
                .addParams("latitude", latitude)
                .addParams("address", address)
                .addParams("country", country)
                .addParams("province", province)
                .addParams("city", city)
                .addParams("region", region)
                .build()
                .execute(new CommonCallback<RegisterTerminalResultBean>(RegisterTerminalResultBean.class) {
                    @Override
                    public void onResponse(RegisterTerminalResultBean response, int id) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });

    }

    public static void getDevStatus(String id, String playingSongId, String playingSong,
                                    String playingSongType, String playingPos, String playSeek, String netInfo, @NonNull final HttpCallback<DeviceStatusResultBean> callback) {

        //设备在后台已被删除，如果是盒子，那么重新调用login接口绑定到默认店铺
        OkHttpUtils.post().url(GET_DEVICE_STATUS)
                .addParams("id", id)
                .addParams("playingSongId", playingSongId)
                .addParams("playingSong", playingSong)
                .addParams("playingSongType", playingSongType)
                .addParams("playingPos", playingPos)
                .addParams("playSeek", playSeek)
                .addParams("netInfo", netInfo)
                .build()
                .execute(new CommonCallback<DeviceStatusResultBean>(DeviceStatusResultBean.class) {
                    @Override
                    public void onResponse(DeviceStatusResultBean response, int id) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });

    }

    public static void getCurAdPlan(String adplanId, @NonNull final HttpCallback<AdPlanResultBean> callback) {

        OkHttpUtils.post().url(GET_CUR_ADV_PLAN)
                .addParams("adplanId", adplanId)
                .build()
                .execute(new CommonCallback<AdPlanResultBean>(AdPlanResultBean.class) {
                    @Override
                    public void onResponse(AdPlanResultBean response, int id) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });

    }
    public static void getCurPlan(String planId, @NonNull final HttpCallback<CurPlanResultBean> callback) {

        OkHttpUtils.post().url(GET_CUR_PLAN)
                .addParams("planId", planId)
                .build()
                .execute(new CurPlanCallback<CurPlanResultBean>(CurPlanResultBean.class) {
                    @Override
                    public void onResponse(CurPlanResultBean response, int id) {
                        callback.onSuccess(response);
                    }
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }
                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void getCurPlayMusics(String terminalId
            ,@NonNull final HttpCallback<MusicListResultBean> callback) {
        OkHttpUtils.get().url(GET_CUR_PLAY_MUSICS)
                .addParams("terminalId", terminalId)
                .build()
                .execute(new CommonCallback<MusicListResultBean>(MusicListResultBean.class) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }
                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }

                    @Override
                    public void onResponse(MusicListResultBean response, int id) {
                        callback.onSuccess(response);
                    }

                });
    }

    public static void getPushMedias(String id, @NonNull final HttpCallback<PushMediaResultBean> callback) {

        OkHttpUtils.post().url(GET_PUSH_MEDIAS)
                .addParams("id", id)
                .build()
                .execute(new CommonCallback<PushMediaResultBean>(PushMediaResultBean.class) {
                    @Override
                    public void onResponse(PushMediaResultBean response, int id) {
                        callback.onSuccess(response);
                    }
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }
                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void getWifiInfo(String shopCode, String id, @NonNull final HttpCallback<WifiInfoResultBean> callback) {

        OkHttpUtils.post().url(DEV_GET_WIFI_INFO)
                .addParams("shopCode", shopCode)
                .addParams("id", id)
                .build()
                .execute(new CommonCallback<WifiInfoResultBean>(WifiInfoResultBean.class) {
                    @Override
                    public void onResponse(WifiInfoResultBean response, int id) {
                        callback.onSuccess(response);
                    }
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }
                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void playRecordReport(Context context, String dataId, String playType, String dataType, CommonCallback callback)
    {

        String terminalId = LocalDataEntity.newInstance(context).getDeviceId();
        OkHttpUtils.post().url(PLAY_RECORD_REPORT)
                .addParams("terminalId", terminalId)
                .addParams("dataId", dataId)
                .addParams("playType", playType)//0-计划 1-插播
                .addParams("dataType", dataType)//1-歌曲 2-广告
                .build()
                .execute(callback);

    }

    public static void errorReport(Context context, String errormsg)
    {

        String terminalId = LocalDataEntity.newInstance(context).getDeviceId();
        if(TextUtils.isEmpty(terminalId))
            return;
        String softCode = SystemUtils.getVersionCode(context)+"";
        String softVersion = SystemUtils.getVersionName(context);
        if(errormsg == null)
            errormsg = "";
        OkHttpUtils.post().url(DEV_ERROR_REPORT)
                .addParams("terminalId", terminalId)
                .addParams("softCode", softCode)
                .addParams("softVersion", softVersion)
                .addParams("errormsg", errormsg)
                .build()
                .execute(new CommonCallback<RegisterTerminalResultBean>(RegisterTerminalResultBean.class) {
                    @Override
                    public void onResponse(RegisterTerminalResultBean response, int id) {

                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onAfter(int id) {

                    }
                });

    }

    public static void wifiConfigReport(Context context, String wifiUpdateCode)
    {
        String terminalId = LocalDataEntity.newInstance(context).getDeviceId();
        OkHttpUtils.post().url(WIFI_CONFIG_CHECK)
                .addParams("id", terminalId)
                .addParams("wifiUpdateCode", wifiUpdateCode)
                .build()
                .execute(new CommonCallback<RegisterTerminalResultBean>(RegisterTerminalResultBean.class) {
                    @Override
                    public void onResponse(RegisterTerminalResultBean response, int id) {

                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onAfter(int id) {

                    }
                });

    }

    public static void updateDevInfo(Context context)
    {

        String terminalId = LocalDataEntity.newInstance(context).getDeviceId();
        if(TextUtils.isEmpty(terminalId))
            return;
        String softCode = SystemUtils.getVersionCode(context)+"";
        String softVersion = SystemUtils.getVersionName(context);
        String hardVersion = SystemUtils.getOsVersion() + "";
        String videoWhirl = LocalDataEntity.newInstance(context).getVideoWhirl();
        DeviceInfor tempInfor = LocalDataEntity.newInstance(context).getDeviceInfor();

        OkHttpUtils.post().url(UPDATE_DEV_INFO)
                .addParams("id", terminalId)
                .addParams("softCode", softCode)
                .addParams("softVersion", softVersion)
                .addParams("hardVersion", hardVersion)
                .addParams("volume", SystemUtils.getCurrentVolume(context) + "")
                .addParams("videoWhirl", videoWhirl)
                .addParams("wifiName", tempInfor.getWifiName())
                .addParams("wifiPassword", tempInfor.getWifiPwd())
                .addParams("ip", SystemUtils.getIP())
                .addParams("netInfo", tempInfor.getNetInfo())
                .addParams("longitude", tempInfor.getLon())
                .addParams("latitude", tempInfor.getLat())
                .addParams("address", tempInfor.getAddr())
                .addParams("name", tempInfor.getName())
                .build()
                .execute(new CommonCallback<RegisterTerminalResultBean>(RegisterTerminalResultBean.class) {
                    @Override
                    public void onResponse(RegisterTerminalResultBean response, int id) {

                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onAfter(int id) {

                    }
                });

    }

    /*public static void getAllShops(String token, String pageNo, String pageSize,String merchId, String groupId, String memberId,
                                   String name, String shopCode, String userShopCode, @NonNull final HttpCallback<Shopinfo[]> callback) {
        OkHttpUtils.post().url(GET_SHOP_LIST)
                .addHeader("token", token)
                .addParams("clientType", "1")
                .addParams("merchId", merchId)
                .addParams("groupId", groupId)
                .addParams("memberId", memberId)
                .addParams("name", name)
                .addParams("shopCode", shopCode)
                .addParams("userShopCode", userShopCode)
                .addParams("pageSize", pageSize)
                .addParams("pageNo", pageNo)
                .build()
                .execute(new ShopListCallback<Shopinfo[]>(Shopinfo[].class) {
                    @Override
                    public void onResponse(Shopinfo[] response, int id) {
                        if(response == null)
                            callback.onFail(null);
                        else
                            callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void getMyAlbums(String token, String pageNo, String pageSize,String merchId,
                                   String name, String typeId, @NonNull final HttpCallback<AlbumInfo[]> callback) {
        OkHttpUtils.post().url(GET_My_ALBUM_LIST)
                .addHeader("token", token)
                .addParams("merchId", merchId)
                .addParams("typeId", typeId)
                .addParams("name", name)
                .addParams("pageSize", pageSize)
                .addParams("pageNo", pageNo)
                .build()
                .execute(new AlbumListCallback<AlbumInfo[]>(AlbumInfo[].class) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onResponse(AlbumInfo[] response, int id) {
                        if(response == null)
                            callback.onFail(null);
                        else
                            callback.onSuccess(response);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }

                });
    }*/


    /********************TME业务********************/

    public static void TME_Register(String udid, String positionDesc, String remark, @NonNull final HttpCallback<TMECommonResultBean> callback) {


        OkHttpUtils.post().url(TMEHttpAPI.REGISTER)
                .addParams("clientId", TME_CLIENT_ID)
                .addParams("udid", udid)
                .addParams("positionDesc", positionDesc)
                .addParams("remark", "")
                .build()
                .execute(new CommonCallback<TMECommonResultBean>(TMECommonResultBean.class) {
                    @Override
                    public void onResponse(TMECommonResultBean response, int id) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void TME_UpdateStatus(String status, String access_token,@NonNull final HttpCallback<InitTerminalResultBean> callback) {

        OkHttpUtils.get().url(TMEHttpAPI.GET_DEVICE_STATUS)
                .addHeader("Authorization", access_token)
                .addParams("status", status)

                .build()
                .execute(new CommonCallback<InitTerminalResultBean>(InitTerminalResultBean.class) {
                    @Override
                    public void onResponse(InitTerminalResultBean response, int id) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void TME_GetDevInfo(String status, String access_token,@NonNull final HttpCallback<InitTerminalResultBean> callback) {

        OkHttpUtils.get().url(TMEHttpAPI.DEV_INFO)
                .addHeader("Authorization", access_token)

                .build()
                .execute(new CommonCallback<InitTerminalResultBean>(InitTerminalResultBean.class) {
                    @Override
                    public void onResponse(InitTerminalResultBean response, int id) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void TME_SongReport(String access_token,String clientId, String udid,
                                      String songName, String duration, String playTime, String songId, @NonNull final HttpCallback<InitTerminalResultBean> callback) {

        OkHttpUtils.post().url(TMEHttpAPI.SongReport)
                .addHeader("Authorization", access_token)
                .addParams("clientId", clientId)
                .addParams("udid", udid)
                .addParams("songName", songName)
                .addParams("duration", duration)
                .addParams("playTime", playTime)
                .addParams("songId", songId)
                .build()
                .execute(new CommonCallback<InitTerminalResultBean>(InitTerminalResultBean.class) {
                    @Override
                    public void onResponse(InitTerminalResultBean response, int id) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }


    public static String TME_CLIENT_ID = "dianyin";
    public static String TME_CLIENT_SECRET = "123456";
    public static void TME_Oauth( @NonNull final HttpCallback<TMEOauthInfo> callback) {

        OkHttpUtils.post().url(TMEHttpAPI.OAUTH)
                .addParams("grant_type", "client_credentials")
                .addParams("scope", "server")
                .addParams("client_id", TME_CLIENT_ID)
                .addParams("client_secret", TME_CLIENT_SECRET)
                .build()
                .execute(new CommonCallback<TMEOauthInfo>(TMEOauthInfo.class) {
                    @Override
                    public void onResponse(TMEOauthInfo response, int id) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

}
