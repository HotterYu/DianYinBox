package com.znt.push.httpmodel;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.znt.lib.bean.CurPlanInfor;
import com.znt.lib.bean.CurPlanResultBean;
import com.znt.lib.bean.CurPlanSubInfor;
import com.znt.lib.bean.MediaInfor;
import com.znt.lib.bean.ResponseBean;
import com.znt.lib.utils.UrlUtil;
import com.znt.push.db.DBMediaHelper;
import com.znt.push.http.HttpRequestID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.Response;

/**
 * Created by hzwangchenyan on 2017/2/8.
 */
public abstract class CurPlanCallback<T>  extends BaseCallBack<T>
{

    protected String RESULT_INFO = "data";
    protected String RESULT_OK = "resultcode";

    private Class<T> clazz;
    private Gson gson;

    public CurPlanCallback(Class<T> clazz)
    {
        this.clazz = clazz;
        gson = new Gson();
    }

    @Override
    public T parseNetworkResponse(Response response, int id) throws IOException
    {
        try
        {
            String jsonString = response.body().string();
            saveJsonToLocal(response,jsonString);

            CurPlanResultBean curPlanResultBean = (CurPlanResultBean) gson.fromJson(jsonString, clazz);

            CurPlanInfor planInfo = curPlanResultBean.getData();
            curPlanResultBean.getData().clearSubPlanInfos();
            DBMediaHelper.getInstance().deleteAllMedias();
            DBMediaHelper.getInstance().deleteCurSubPlanInfors();

            List<CurPlanSubInfor> subPlanInfos = new ArrayList<CurPlanSubInfor>();
            int count = planInfo.getScheIds().length;
            for(int i=0;i<count;i++)
            {
                CurPlanSubInfor tempSubInfo = new CurPlanSubInfor();

                String tempScheId = planInfo.getScheIds()[i];

                String tempCycleType = planInfo.getCycleTypes()[i];
                String tempStartTime = planInfo.getStartTimes()[i];
                String tempEndTime = planInfo.getEndTimes()[i];
                String scheId = planInfo.getScheIds()[i];

                String tempCategoryIds = planInfo.getCategoryIds()[i] ;

                if(tempCategoryIds.contains(";"))
                    tempCategoryIds = tempCategoryIds.replace(";",",");

                tempSubInfo.setPlanId(planInfo.getId());
                tempSubInfo.setPlanName(planInfo.getPlanName());
                tempSubInfo.setStartDate(planInfo.getStartDate());
                tempSubInfo.setEndDate(planInfo.getEndDate());
                tempSubInfo.setStartTime(tempStartTime);
                tempSubInfo.setEndTime(tempEndTime);
                tempSubInfo.setWeek(tempCycleType);
                tempSubInfo.setCategoryIds(tempCategoryIds);
                tempSubInfo.setScheId(scheId);

                List<MediaInfor> tempList = getScheduleMusics(planInfo.getId(),tempScheId,tempCategoryIds, tempStartTime,tempEndTime,tempCycleType);
                if(tempList != null)
                {
                    DBMediaHelper.getInstance().addMedias(tempList);
                }

                tempSubInfo.checkParams();

                curPlanResultBean.getData().addCurPlanSubInfors(tempSubInfo);
                subPlanInfos.add(tempSubInfo);
            }
            DBMediaHelper.getInstance().addCurSubPlanInfors(subPlanInfos);
            /*DBMediaHelper.getInstance().deleteAllMedias();
            DBMediaHelper.getInstance().addMedias(mediaList);*/

            return (T) curPlanResultBean;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    private List<MediaInfor> getScheduleMusics(String planId, String scheId, String categoryIds,String startPlayTime, String endPlayTime, String week)
    {
        List<MediaInfor> tempList = new ArrayList<MediaInfor>();

        Map<String, String> params = new HashMap<String, String>();
        params.put("planId", planId);
        params.put("scheId", scheId);
        params.put("categoryIds", categoryIds);
        /*params.put("pageSize", 25 + "");
        params.put("pageNo", scheduleMusicPageNum + "");*/

        try
        {
            Response response = OkHttpUtils.get().url(HttpAPI.GET_SCHEDULE_MUSICS).id(HttpRequestID.GET_SCHEDULE_MUSICS).params(params).build().execute();
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
                            tempInfor.setMediaId(musicId);
                            tempInfor.setMediaType(musicType);

                            tempInfor.setStartPlayTime(startPlayTime);
                            tempInfor.setEndPlayTime(endPlayTime);
                            tempInfor.setPlayWeek(week);
                            tempInfor.setPlan_id(planId);
                            tempInfor.setSche_id(scheId);

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
                            //DBMediaHelper.getInstance().addMedia(tempInfor);

                        }
                        /*if(tempList.size() >= 25)
                        {
                            scheduleMusicPageNum ++;
                            getScheduleMusics( planId,  scheId,  categoryIds, startPlayTime,  endPlayTime,  week);
                        }*/
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

    private void saveJsonToLocal(Response response,String jsonString)
    {
        try
        {
            HttpUrl httpUrl = response.request().url();
            URL url = httpUrl.url();
            String key = url.toString();

            if(key.equals(HttpAPI.GET_CUR_ADV_PLAN)
                    ||key.equals(HttpAPI.GET_CUR_PLAN))
            {
                ResponseBean mResponseBean = new ResponseBean();
                mResponseBean.setKey(key);
                mResponseBean.setValue(jsonString);

                DBMediaHelper.getInstance().addResponseInfo(mResponseBean);
                //String res = DBMediaHelper.getInstance().getResposeInfo(key);
            }



        } catch (Exception e) {
            e.printStackTrace();
        }



    }
}