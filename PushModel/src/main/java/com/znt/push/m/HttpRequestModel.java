/*
package com.znt.push.m;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.cookie.CookieJarImpl;
import com.znt.lib.bean.AdPlanInfor;
import com.znt.lib.bean.DeviceInfor;
import com.znt.lib.bean.DeviceStatusInfor;
import com.znt.lib.bean.MediaInfor;
import com.znt.lib.utils.ShellUtils;
import com.znt.lib.utils.SystemUtils;
import com.znt.lib.utils.UrlUtil;
import com.znt.push.callback.InitTerminalCallBack;
import com.znt.push.entity.Constant;
import com.znt.push.entity.LocalDataEntity;
import com.znt.push.http.HttpAPI;
import com.znt.push.http.HttpRequestID;
import com.znt.push.http.callback.AdPlanCallBack;
import com.znt.push.http.callback.CheckUpdateCallBack;
import com.znt.push.http.callback.DevStatusCallBack;
import com.znt.push.http.callback.PushMediasCallBack;
import com.znt.push.http.callback.RegisterCallBack;
import com.znt.push.v.IHttpRequestView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.CookieJar;
import okhttp3.Request;
import okhttp3.Response;

@SuppressLint("UseSparseArrays")
public class HttpRequestModel extends HttpAPI
{

	private String TAG = "HttpRequestModel";


	private final String mBaseUrl = "";
	private final int MAX_PAGE_SIZE = 300;
	private Context activity = null;
	private IHttpRequestView iHttpRequestView = null;
	
	private volatile HashMap<Integer, Boolean> runStatus = new HashMap<Integer, Boolean>();
	
	public HttpRequestModel(Context activity, IHttpRequestView iHttpRequestView)
	{
		this.activity = activity;
		this.iHttpRequestView = iHttpRequestView;
	}
	
	private void requestStart(final int requestId)
	{
		iHttpRequestView.requestStart(requestId);
	}
	private void requestError(final int requestId, final String error)
	{
		iHttpRequestView.requestError(requestId, error);
	}
	private void requestSuccess(final String obj, final int requestId)
	{
		iHttpRequestView.requestSuccess(obj, requestId);
	}
	private void requestSuccess(final Object obj, final int requestId)
	{
		iHttpRequestView.requestSuccess(obj, requestId);
	}
	private void requestNetWorkError()
	{
		iHttpRequestView.requestNetWorkError();
	}
	
	private void setRunStatusDoing(int key)
	{
		runStatus.put(key, true);
	}
	private void setRunStatusFinish(int key)
	{
		runStatus.put(key, false);
	}
	public boolean isRuning(int key)
	{
		if(runStatus.containsKey(key))
			return runStatus.get(key);
		return false;
	}


	public void initTerminal()
	{
		int requestId = HttpRequestID.INIT_TERMINAL;
		if(isRuning(requestId))
		{
			Log.e("", "initTerminal is running");
			return;
		}
		setRunStatusDoing(requestId);

		Map<String, String> params = new HashMap<String, String>();
		String terminalId = LocalDataEntity.newInstance(activity).getDeviceId();
		if(!TextUtils.isEmpty(terminalId))
		{
			params.put("id", terminalId);
		}

		params.put("code", Constant.deviceCode);

		OkHttpUtils//
				.post()//
				.url(INIT_TERMINAL)//
				.id(requestId)
				.params(params)//
				.build()//
				.execute(new InitTerminalCallBack(activity)//
				{
					@Override
					public void onError(Call call, Exception e, int requestId)
					{
						//mTv.setText("onError:" + e.getMessage());
						requestError(requestId, e.getMessage());
						setRunStatusFinish(requestId);
					}

					@Override
					public void onResponse(String response, int requestId)
					{
						if(response == null)
						{
							requestError(requestId, response + "");
							return;
						}
						requestSuccess(response, requestId);
						setRunStatusFinish(requestId);
					}

					@Override
					public void onBefore(Request request, int requestId)
					{
						// TODO Auto-generated method stub
						super.onBefore(request, requestId);
						requestStart(requestId);
					}

					@Override
					public String parseNetworkResponse(Response response, int requestId) throws IOException
					{
						// TODO Auto-generated method stub
						return super.parseNetworkResponse(response, requestId);
					}
				});
	}

	*/
/**
	 * 注册
	 *//*

	private int registerRunningCount = 0;
	public void register()
	{

		int requestId = HttpRequestID.REGISTER;
		if(isRuning(requestId))
		{
			Log.d(TAG, "register: is running");
			registerRunningCount ++;
			if(registerRunningCount > 3)
			{
				setRunStatusFinish(requestId);
				registerRunningCount = 0;
			}
			return;
		}
		setRunStatusDoing(requestId);

		Map<String, String> params = new HashMap<String, String>();

		DeviceInfor infor = LocalDataEntity.newInstance(activity).getDeviceInfor();
		String name = infor.getName();
		//String code = infor.getCode();

		String wifiName = infor.getWifiName();
		String wifiPassword = infor.getWifiPwd();
		String longitude = LocalDataEntity.newInstance(activity).getDeviceLon();
		String latitude = LocalDataEntity.newInstance(activity).getDeviceLat();
		String addr = LocalDataEntity.newInstance(activity).getDeviceAddr();
		String videoWhirl = LocalDataEntity.newInstance(activity).getVideoWhirl();
		String netInfo = "这里是测试的netInfo";
		try
		{
			params.put("softCode", "DianYinMusicBox");
			params.put("softVersion", SystemUtils.getPkgInfo(activity).versionCode + "");
			*/
/*String s1 = android.os.Build.BRAND;
			String s2 = android.os.Build.DEVICE;
			String s3 = android.os.Build.DISPLAY;
			String s4 = android.os.Build.HARDWARE;
			String s5 = android.os.Build.MODEL;
			String s6 = android.os.Build.PRODUCT;*//*

			params.put("hardVersion", android.os.Build.MODEL + "");
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		params.put("volume", SystemUtils.getCurrentVolume(activity) + "");
		params.put("shopName", name);
		params.put("code", Constant.deviceCode);
		params.put("terminalType", "2");//1-pc 2-box 3-tv，不传服务根据softcode判断
		params.put("bindCode", Constant.DEFAULT_BIND_CODE);//默认绑定的账户
		params.put("ip", SystemUtils.getIP());
		if(!TextUtils.isEmpty(videoWhirl))
			params.put("videoWhirl", videoWhirl);
		params.put("netInfo", netInfo);
		if(!TextUtils.isEmpty(wifiName))
			params.put("wifiName", wifiName);
		params.put("wifiPassword", wifiPassword);
		if(!TextUtils.isEmpty(longitude))
			params.put("longitude", longitude);
		if(!TextUtils.isEmpty(latitude))
			params.put("latitude", latitude);
		if(!TextUtils.isEmpty(addr))
			params.put("address", addr);

		OkHttpUtils//
				.post()//
				.url(REGISTER)//
				.id(requestId)
				.params(params)//
				.build()//
				.execute(new RegisterCallBack()//
				{
					@Override
					public void onError(Call call, Exception e, int requestId)
					{
						//mTv.setText("onError:" + e.getMessage());
						requestError(requestId, e.getMessage());
						setRunStatusFinish(requestId);
					}

					@Override
					public void onResponse(String response, int requestId)
					{
						setRunStatusFinish(requestId);
						//mTv.setText("onResponse:" + response);
						if(response == null)
						{
							requestError(requestId, response + "");
							return;
						}

                        try
                        {
                            JSONObject json = new JSONObject(response);

                            String terminalId = getInforFromJason(json,"terminalId");
                            LocalDataEntity.newInstance(activity).setDeviceId(terminalId);
                            ShellUtils.setCode(terminalId);
                            requestSuccess(response, requestId);

                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
					}

					@Override
					public void onBefore(Request request, int requestId)
					{
						// TODO Auto-generated method stub
						super.onBefore(request, requestId);
						requestStart(requestId);
					}

					@Override
					public String parseNetworkResponse(Response response, int requestId) throws IOException
					{
						// TODO Auto-generated method stub
						setRunStatusFinish(requestId);
						return super.parseNetworkResponse(response, requestId);
					}

				});
	}

	*/
/**
	 * 检查更新
	 *//*

	public void checkUpdate()
	{
		Map<String, String> params = new HashMap<String, String>();
		int softCode = SystemUtils.getVersionCode(activity);
		params.put("softCode", softCode + "");

		OkHttpUtils//
		.get()//
		.url(CHECK_UPDATE)//
		.id(HttpRequestID.CHECK_UPDATE)
		.params(params)//
		.build()//
		.execute(new CheckUpdateCallBack()//
		{
			@Override
			public void onError(Call call, Exception e, int requestId)
			{
				//mTv.setText("onError:" + e.getMessage());
				requestError(requestId, e.getMessage());
			}
			
			@Override
			public void onResponse(String response, int requestId)
			{
				//mTv.setText("onResponse:" + response);
				if(response == null)
        		{
					requestError(requestId, response + "");
					return;
        		}
				requestSuccess(response, requestId);
			}
			
			@Override
			public void onBefore(Request request, int requestId)
			{
				// TODO Auto-generated method stub
				super.onBefore(request, requestId);
				requestStart(requestId);
			}
			
			@Override
			public String parseNetworkResponse(Response response, int requestId) throws IOException 
			{
				// TODO Auto-generated method stub
				return super.parseNetworkResponse(response, requestId);
			}
		});
	}
	
	private int isDevRunningCheckCount = 0;
	

	public void getDevStatus(int playSeek, String playingSong, String playingSongType, String id, String netInfo)
	{
		int requestId = HttpRequestID.GET_DEVICE_STATUS;
		if(!SystemUtils.isNetConnected(activity))
		{
			requestError(requestId, "net work error");
			return;
		}
		if(isRuning(requestId))
		{
			if(isDevRunningCheckCount >= 3)
			{
				setRunStatusFinish(requestId);
				isDevRunningCheckCount = 0;
			}
			else
				isDevRunningCheckCount ++;
			return;
		}
		setRunStatusDoing(requestId);
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", id);
		params.put("playSeek", playSeek + "");
		params.put("playingSong", playingSong);
		params.put("playingSongType", playingSongType);
		params.put("netInfo", netInfo);
		
		OkHttpUtils//
		.get()//
		.url(GET_DEVICE_STATUS)//
		.id(requestId)
		.params(params)//
		.build()//
		.execute(new DevStatusCallBack()//
		{
			@Override
			public void onError(Call call, Exception e, int requestId)
			{
				//mTv.setText("onError:" + e.getMessage());
				setRunStatusFinish(requestId);
				requestError(requestId, e.getMessage());
			}
			
			@Override
			public void onResponse(DeviceStatusInfor response, int requestId)
			{
				//mTv.setText("onResponse:" + response);
				setRunStatusFinish(requestId);
				*/
/*if(response == null)
        		{
					requestError(requestId, response + "");
					return;
        		}*//*

				requestSuccess(response, requestId);
			}
			
			@Override
			public void onBefore(Request request, int requestId)
			{
				// TODO Auto-generated method stub
				super.onBefore(request, requestId);
				requestStart(requestId);
			}
			
			@Override
			public DeviceStatusInfor parseNetworkResponse(Response response, int requestId) throws IOException
			{
				// TODO Auto-generated method stub
				return super.parseNetworkResponse(response, requestId);
			}
		});
	}


	*/
/**
	 * 获取当前推送的歌曲列表
	 * @param devId
	 *//*

	public void getPushMedias(String devId)
	{
		int requestId = HttpRequestID.GET_PUSH_MUSIC;

		if(isRuning(requestId))
		{
			Log.d(TAG, "getPushMedias: is running");
			return;
		}

		Map<String, String> params = new HashMap<String, String>();
		params.put("id", devId);
		OkHttpUtils//
				.get()//
				.url(GET_PUSH_MEDIAS)//
				.id(requestId)
				.params(params)//
				.build()//
				.execute(new PushMediasCallBack()//
				{
					@Override
					public void onError(Call call, Exception e, int requestId)
					{
						//mTv.setText("onError:" + e.getMessage());
						setRunStatusFinish(requestId);
						requestError(requestId, e.getMessage());
					}

					@Override
					public void onResponse(AdPlanInfor response, int requestId)
					{
						//mTv.setText("onResponse:" + response);
						setRunStatusFinish(requestId);
						*/
/*if(response == null)
						{
							requestError(requestId, response + "");
							return;
						}*//*

						requestSuccess(response, requestId);
					}

					@Override
					public void onBefore(Request request, int requestId)
					{
						// TODO Auto-generated method stub
						super.onBefore(request, requestId);
						requestStart(requestId);
					}

					@Override
					public AdPlanInfor parseNetworkResponse(Response response, int requestId) throws IOException
					{
						// TODO Auto-generated method stub
						return super.parseNetworkResponse(response, requestId);
					}
				});
	}

	*/
/**
	 * 获取广告计划
	 * @param adplanId
	 *//*

	public void getCurAdvPlan(String adplanId)
	{
		int requestId = HttpRequestID.GET_ADV_PLAN;

		if(isRuning(requestId))
		{
			Log.d(TAG, "getCurPlan: is running");
			return;
		}

		Map<String, String> params = new HashMap<String, String>();
		params.put("adplanId", adplanId);
		OkHttpUtils//
				.get()//
				.url(GET_CUR_ADV_PLAN)//
				.id(requestId)
				.params(params)//
				.build()//
				.execute(new AdPlanCallBack()//
				{
					@Override
					public void onError(Call call, Exception e, int requestId)
					{
						//mTv.setText("onError:" + e.getMessage());
						setRunStatusFinish(requestId);
						requestError(requestId, e.getMessage());
					}

					@Override
					public void onResponse(AdPlanInfor response, int requestId)
					{
						//mTv.setText("onResponse:" + response);
						setRunStatusFinish(requestId);
						*/
/*if(response == null)
						{
							requestError(requestId, response + "");
							return;
						}*//*

						requestSuccess(response, requestId);
					}

					@Override
					public void onBefore(Request request, int requestId)
					{
						// TODO Auto-generated method stub
						super.onBefore(request, requestId);
						requestStart(requestId);
					}

					@Override
					public AdPlanInfor parseNetworkResponse(Response response, int requestId) throws IOException
					{
						// TODO Auto-generated method stub
						return super.parseNetworkResponse(response, requestId);
					}
				});
	}

	*/
/**
	 * 获取当前的播放计划
	
	 *//*

	public void getCurPlan(String planId)
	{
		*/
/*int requestId = HttpRequestID.GET_CUR_PLAN;

		if(isRuning(requestId))
		{
			Log.d(TAG, "getCurPlan: is running");
			return;
		}

		Map<String, String> params = new HashMap<String, String>();
		params.put("planId", planId);

		iHttpRequestView.requestStart(requestId);
		CurPlanInfor curPlanInfor = null;
		try
		{
			Response response = OkHttpUtils.get().url(GET_CUR_PLAN).id(requestId).params(params).build().execute();
			setRunStatusFinish(requestId);
			if(response.isSuccessful())
			{
				String string = response.body().string();

				try
				{
					JSONObject jsonObject = new JSONObject(string);
					int result = jsonObject.getInt(RESULT_OK);
					String message = jsonObject.getString("message");
					if(result == 1)
					{
						String info = jsonObject.getString(RESULT_INFO);
						if(TextUtils.isEmpty(info))
						{
							requestError(requestId, "info is empty");
							return ;
						}
						curPlanInfor = new CurPlanInfor();

						DBMediaHelper.getInstance().deleteAllMedias();
						DBMediaHelper.getInstance().deleteCurplan();

						JSONObject json = new JSONObject(info);
						String id = getInforFromJason(json, "id");
						String planName = getInforFromJason(json, "planName");
						String planType = getInforFromJason(json, "planType");
						String cycleTypes = getInforFromJason(json, "cycleTypes");
						String scheIds = getInforFromJason(json, "scheIds");
						String startTimes = getInforFromJason(json, "startTimes");
						String endTimes = getInforFromJason(json, "endTimes");
						String categoryIds = getInforFromJason(json, "categoryIds");
						String categoryNames = getInforFromJason(json, "categoryNames");
						String startDate = getInforFromJason(json, "startDate");
						String endDate = getInforFromJason(json, "endDate");


						curPlanInfor.setStartDate(startDate);
						curPlanInfor.setEndDate(endDate);
						curPlanInfor.setPlanName(planName);
						curPlanInfor.setId(id);
						curPlanInfor.setPlanType(planType);
						curPlanInfor.setCycleTypes(cycleTypes);
						curPlanInfor.setScheIds(scheIds);
						curPlanInfor.setStartTimes(startTimes);
						curPlanInfor.setEndTimes(endTimes);
						curPlanInfor.setCategoryIds(categoryIds);
						curPlanInfor.setCategoryNames(categoryNames);

						//save curplaninfor
						DBMediaHelper.getInstance().addPlanInfor(curPlanInfor);

						String[] scheIdsArray = curPlanInfor.getScheIds();
						String[] cycleTypesArray = curPlanInfor.getCycleTypes();
						String[] startTimesArray = curPlanInfor.getStartTimes();
						String[] endTimesArray = curPlanInfor.getEndTimes();
						String[] categoryIdsArray = curPlanInfor.getCategoryIds();
						String[] categoryNamesArray = curPlanInfor.getCategoryNames();

						int count = scheIdsArray.length;
						for(int i=0;i<count;i++)
						{
							String tempScheId = scheIdsArray[i];

							String tempCycleType = cycleTypesArray[i];
							String tempStartTime = startTimesArray[i];
							String tempEndTime = endTimesArray[i];

							String[] tempCategoryIdsArray = categoryIdsArray[i].split(";");
							String tempCategoryIds = "";
							int tempLen = tempCategoryIdsArray.length;
							for(int j=0;j<tempLen;j++)
							{
								tempCategoryIds += tempCategoryIdsArray[j] + ",";
							}
							if(tempCategoryIds.endsWith(","))
								tempCategoryIds = tempCategoryIds.substring(0, tempCategoryIds.length() - 1);
							List<MediaInfor> tempList = getScheduleMusics(id,tempScheId,tempCategoryIds, tempStartTime,tempEndTime,tempCycleType);

							//save all medias to db
							DBMediaHelper.getInstance().addMedias(tempList);

						}
						requestSuccess(curPlanInfor, requestId);
					}
					else
						requestError(requestId, message);
				}
				catch (Exception e)
				{
					setRunStatusFinish(requestId);
					e.printStackTrace();
					requestError(requestId, e.getMessage());
				}
			}
			else
				requestError(requestId, "http request fail");
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			setRunStatusFinish(requestId);
			e.printStackTrace();
			requestError(requestId, e.getMessage());
		}*//*

	}

	private List<MediaInfor> getScheduleMusics(String planId, String scheId, String categoryIds,String startPlayTime, String endPlayTime, String week)
	{
		List<MediaInfor> tempList = new ArrayList<MediaInfor>();
		int scheduleMusicPageNum = 1;
		Map<String, String> params = new HashMap<String, String>();
		params.put("planId", planId);
		params.put("scheId", scheId);
		params.put("categoryIds", categoryIds);
		params.put("pageSize", MAX_PAGE_SIZE + "");
		params.put("pageNo", scheduleMusicPageNum + "");
		try
		{
			Response response = OkHttpUtils.get().url(GET_SCHEDULE_MUSICS).id(HttpRequestID.GET_SCHEDULE_MUSICS).params(params).build().execute();
			if(response.isSuccessful())
			{
				String string = response.body().string();
				try
				{
					JSONObject jsonObject = new JSONObject(string);
					int result = jsonObject.getInt(RESULT_OK);
					if(result == 1)
					{
						String info = getInforFromJason(jsonObject, RESULT_INFO);
						JSONArray jsonArray = new JSONArray(info);
						int count = jsonArray.length();
						for(int i=0;i<count;i++)
						{
							JSONObject json = jsonArray.getJSONObject(i);
							String musicId = getInforFromJason(json, "id");
							String musicName = getInforFromJason(json, "musicName");
							String musicSing = getInforFromJason(json, "musicSing");
							String musicAlbum = getInforFromJason(json, "musicAlbum");
							String musicDuration = getInforFromJason(json, "musicDuration");
							String musicType = getInforFromJason(json, "musicType");
							String sourceType = getInforFromJason(json, "sourceType");
							String sourceId = getInforFromJason(json, "sourceId");
							String singerid = getInforFromJason(json, "singerid");
							String albumid = getInforFromJason(json, "albumid");
							String fileSize = getInforFromJason(json, "fileSize");

							String musicUrl = getInforFromJason(json, "musicUrl");
							if(!TextUtils.isEmpty(musicUrl))
								musicUrl = UrlUtil.decodeUrl(musicUrl);
							MediaInfor tempInfor = new MediaInfor();
							tempInfor.setMediaName(musicName);
							tempInfor.setMediaUrl(musicUrl);
							tempInfor.setArtistName(musicSing);
							tempInfor.setAlbumName(musicAlbum);

							tempInfor.setStartPlayTime(startPlayTime);
							tempInfor.setEndPlayTime(endPlayTime);
							tempInfor.setPlayWeek(week);

							if(!TextUtils.isEmpty(musicDuration))
							{
								int mediaDuration = Integer.parseInt(musicDuration);
								tempInfor.setDuration(mediaDuration);
							}
							if(!TextUtils.isEmpty(fileSize))
							{
								long mediaSize = Long.parseLong(fileSize);
								tempInfor.setMediaSize(mediaSize);
							}

							tempList.add(tempInfor);
							//DBManager.INSTANCE.addCurPlanMusic(tempInfor, planId);

						}
					}
				}
				catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return tempList;
	}

	*/
/**
	 * 上报播放记录
	 * @param terminalId
	 * @param mediaInfor
	 *//*

	public void reportPlayRecord(String terminalId, MediaInfor mediaInfor)
	{

		Map<String, String> params = new HashMap<String, String>();
		params.put("dataId", mediaInfor.getMediaId() + "");
		params.put("terminalId", terminalId + "");

		String playType = "0";
		if(mediaInfor.isPush())
			playType = "1";
		params.put("playType", playType + "");

		String dataType = "0";
		if(mediaInfor.isAdv())
			dataType = "1";
		params.put("dataType", dataType + "");

		OkHttpUtils//
				.get()//
				.url(PLAY_RECORD_REPORT)//
				.id(HttpRequestID.PLAY_RECORD_REPORT)
				.params(params)//
				.build()//
				.execute(new StringCallback()//
				{
					@Override
					public void onError(Call call, Exception e, int requestId)
					{
						//mTv.setText("onError:" + e.getMessage());
						requestError(requestId, e.getMessage());
					}

					@Override
					public void onResponse(String response, int requestId)
					{
						//mTv.setText("onResponse:" + response);
						if(response == null)
						{
							requestError(requestId, response + "");
							return;
						}
						requestSuccess(response, requestId);
					}

					@Override
					public void onBefore(Request request, int requestId)
					{
						// TODO Auto-generated method stub
						super.onBefore(request, requestId);
						requestStart(requestId);
					}

					@Override
					public String parseNetworkResponse(Response response, int requestId) throws IOException
					{
						// TODO Auto-generated method stub
						return super.parseNetworkResponse(response, requestId);
					}
				});
	}


	*/
/**
	 * 系统异常log上报
	 * @param softCode
	 * @param terminalId
	 * @param softVersion
	 * @param errormsg
	 *//*

	public void reportErrorLog(String softCode, String terminalId, String softVersion, String errormsg)
	{

		Map<String, String> params = new HashMap<String, String>();

		params.put("softCode", softCode + "");
		params.put("terminalId", terminalId + "");
		params.put("softVersion", softVersion + "");
		params.put("errormsg", errormsg + "");

		OkHttpUtils//
				.get()//
				.url(DEV_ERROR_REPORT)//
				.id(HttpRequestID.DEV_ERROR_REPORT)
				.params(params)//
				.build()//
				.execute(new StringCallback()//
				{
					@Override
					public void onError(Call call, Exception e, int requestId)
					{
						//mTv.setText("onError:" + e.getMessage());
						requestError(requestId, e.getMessage());
					}

					@Override
					public void onResponse(String response, int requestId)
					{
						//mTv.setText("onResponse:" + response);
						if(response == null)
						{
							requestError(requestId, response + "");
							return;
						}
						requestSuccess(response, requestId);
					}

					@Override
					public void onBefore(Request request, int requestId)
					{
						// TODO Auto-generated method stub
						super.onBefore(request, requestId);
						requestStart(requestId);
					}

					@Override
					public String parseNetworkResponse(Response response, int requestId) throws IOException
					{
						// TODO Auto-generated method stub
						return super.parseNetworkResponse(response, requestId);
					}
				});
	}

    public void clearSession(View view)
    {
        CookieJar cookieJar = OkHttpUtils.getInstance().getOkHttpClient().cookieJar();
        if (cookieJar instanceof CookieJarImpl)
        {
            ((CookieJarImpl) cookieJar).getCookieStore().removeAll();
        }
    }

    public void cancelHttp(int requestId)
    {
    	OkHttpUtils.getInstance().cancelTag(requestId);
    	//OkHttpUtils.getInstance().cancelTag(this);
    }

	protected String RESULT_INFO = "data";
	protected String RESULT_OK = "resultcode";
	protected int RESULT_SUCCESS = 1;
	protected int RESULT_FAILE = 0;

	protected String getInforFromJason(JSONObject json, String key)
	{
		if(json == null || key == null)
			return "";
		if(json.has(key))
		{
			try
			{
				String result = json.getString(key);
				if(result.equals("null"))
					result = "";
				return result;
				//return StringUtils.decodeStr(result);
			}
			catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "";
	}
}*/
