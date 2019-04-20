package com.znt.push.http.callback;

import com.zhy.http.okhttp.callback.Callback;
import com.znt.lib.bean.AdPlanInfor;
import com.znt.push.db.DBMediaHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import okhttp3.Response;

public abstract class AdPlanCallBack extends Callback<AdPlanInfor>
{
    @Override
    public AdPlanInfor parseNetworkResponse(Response response,int requestId) throws IOException
    {
        AdPlanInfor mAdPlanInfor = null;
    	if(response.isSuccessful())
    	{
    		String string = response.body().string();
    		try
    		{
    			JSONObject jsonObject = new JSONObject(string);
    			int result = jsonObject.getInt(RESULT_OK);
				if(result == RESULT_SUCCESS)
				{
					String info = getInforFromJason(jsonObject, RESULT_INFO);
					JSONObject json = new JSONObject(info);
                    mAdPlanInfor = new AdPlanInfor();
					String id = getInforFromJason(json, "id");
					String name = getInforFromJason(json, "name");
					String planModel = getInforFromJason(json, "planModel");
					String startDate = getInforFromJason(json, "startDate");
					String endDate = getInforFromJason(json, "endDate");
					String scheIds = getInforFromJason(json, "scheIds");
					String cycleTypes = getInforFromJason(json, "cycleTypes");
					String playModels = getInforFromJason(json, "playModels");
					String startTimes = getInforFromJason(json, "startTimes");
					String endTimes = getInforFromJason(json, "endTimes");
					String musicNums = getInforFromJason(json, "musicNums");
					String adinfoIds = getInforFromJason(json, "adinfoIds");
					String adinfoNames = getInforFromJason(json, "adinfoNames");
					String adUrls = getInforFromJason(json, "adUrls");
					String merchId = getInforFromJason(json, "merchId");
					String merchName = getInforFromJason(json, "merchName");
					String groupName = getInforFromJason(json, "groupName");
					String groupId = getInforFromJason(json, "groupId");

					mAdPlanInfor.setAdId(id);
					mAdPlanInfor.setName(name);
					mAdPlanInfor.setPlanModel(planModel);
					mAdPlanInfor.setStartDate(startDate);
					mAdPlanInfor.setEndDate(endDate);
					mAdPlanInfor.setScheIds(scheIds);
					mAdPlanInfor.setCycleTypes(cycleTypes);
					mAdPlanInfor.setPlayModels(playModels);
					mAdPlanInfor.setStartTimes(startTimes);
					mAdPlanInfor.setEndTimes(endTimes);
					mAdPlanInfor.setMusicNums(musicNums);
					mAdPlanInfor.setAdinfoIds(adinfoIds);
					mAdPlanInfor.setAdinfoNames(adinfoNames);

					if(adUrls.contains("\\"))
						adUrls = adUrls.replace("\\","");

					mAdPlanInfor.setAdUrls(adUrls);
					mAdPlanInfor.setMerchId(merchId);
					mAdPlanInfor.setMerchName(merchName);
					mAdPlanInfor.setGroupName(groupName);
					mAdPlanInfor.setGroupId(groupId);

                    //List<String> list = Arrays.asList(startTimes);

					DBMediaHelper.getInstance().deleteCurAdPlan();
					DBMediaHelper.getInstance().addAdPlanInfor(mAdPlanInfor);
                    //List<AdPlanInfor> list = DBMediaHelper.getInstance().getCurAdPlanInfor();

					return mAdPlanInfor;


				}
    		}
    		catch (Exception e)
    		{
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
        return null;
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

	String[] getArrayFromJson(JSONObject json, String key)
	{

		if(json == null || key == null)
			return null;
		if(json.has(key))
		{
			try
			{
				String jsonStr = json.getString(key);
				JSONArray jsonArray = new JSONArray(jsonStr);
				int count = jsonArray.length();
				String[] result = new String[count];
				for(int i=0;i<count;i++)
				{
					result[i] = (String) jsonArray.get(i);
				}
				return result;
				//return StringUtils.decodeStr(result);
			}
			catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}


}