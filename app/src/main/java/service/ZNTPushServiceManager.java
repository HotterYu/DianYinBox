package service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.znt.lib.bean.MediaInfor;
import com.znt.lib.utils.MacUtils;
import com.znt.lib.utils.NetWorkUtils;
import com.znt.lib.utils.PluginConstant;
import com.znt.lib.utils.SystemUtils;
import com.znt.push.IPushAidlInterface;
import com.znt.push.IPushCallback;
import com.znt.push.ZNTPushService;
import com.znt.push.entity.Constant;
import com.znt.push.entity.LocalDataEntity;
import com.znt.push.entity.PushModelConstant;
import com.znt.push.v.IDevStatusView;
import com.znt.speaker.R;

import java.util.List;

public class ZNTPushServiceManager 
{

	private Context context = null;
	private IPushAidlInterface mIPushAidlInterface = null;
	
	private IDevStatusView mIDevStatusView = null;

	private String TAG = "ZNTPushServiceManager";
	
	
	public ZNTPushServiceManager(Context context, IDevStatusView mIDevStatusView)
	{
		this.context = context;
		this.mIDevStatusView = mIDevStatusView;

	}

	public boolean isActive()
	{
		return mIPushAidlInterface != null;
	}
	
	public boolean isBindSuccess()
	{
		return mIPushAidlInterface != null;
	}
	
	public void putRequestParams(MediaInfor mediaInfor, boolean updateNow)
	{
		try 
		{	if(mIPushAidlInterface != null)
			{
				String netInfo = getNetInfo();
				mIPushAidlInterface.setRequestParams(mediaInfor, netInfo, updateNow);
			}
		}
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void updateVolumeSetStatus(boolean result)
	{
		try
		{	if(mIPushAidlInterface != null)
		{
			mIPushAidlInterface.updateVolumeSetStatus(result);
		}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String procStatusInfo = "";
	public void updateProcInfo(String procStatusInfo)
    {
        this.procStatusInfo = procStatusInfo;
    }

	String mac = "";
    private String getNetInfo()
    {
        try
        {
            String netType = "";
            //String ip = SystemUtils.getIP();
            if(NetWorkUtils.checkEthernet(context))
                netType = context.getResources().getString(R.string.network_type_wired);
            else
                netType = context.getResources().getString(R.string.network_type_wireless);
            String space = SystemUtils.getAvailabeMemorySize();
            int rebootCount = LocalDataEntity.newInstance(context).getRebootCount();

            if(TextUtils.isEmpty(mac))
                mac = MacUtils.getMac(context);
            int rotation = SystemUtils.getScreenRotation(context);

            //String mem = SystemUtils.getMem(context);

            return space + " " + netType + "  " + rotation + "   " + SystemUtils.getIP() + " MAC:"+ mac + "  " + rebootCount + " PROS:"   + procStatusInfo + "  " + Constant.DEBUG_INFO ;
        }
        catch (Exception e)
        {
            // TODO: handle exception
            return e.getMessage();
        }

    }

    public void  updateProcessByTime(long time)
	{
		try
		{	if(mIPushAidlInterface != null)
				mIPushAidlInterface.updateProcessByTime(time);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			String error = e.getMessage();
			e.printStackTrace();
		}
	}

	public MediaInfor getCurPlayMedia()
	{
		try
		{	if(mIPushAidlInterface != null)
				return mIPushAidlInterface.getCurPlayMedia();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			String error = e.getMessage();
			e.printStackTrace();
		}
		return null;
	}

	public MediaInfor getCurTimeingAd()
	{
		try
		{	if(mIPushAidlInterface != null)
			return mIPushAidlInterface.getCurTimeingAd();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public MediaInfor getCurTimeInternalAd()
	{
		try
		{	if(mIPushAidlInterface != null)
			return mIPushAidlInterface.getCurTimeInternalAd();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public List<MediaInfor> getPushMedias()
	{
		try
		{	if(mIPushAidlInterface != null)
			    return mIPushAidlInterface.getPushMedias();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public String getDevId()
	{
		try
		{	if(mIPushAidlInterface != null)
				return mIPushAidlInterface.getDevId();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	public long getServerTime()
	{
		try
		{	if(mIPushAidlInterface != null)
				return mIPushAidlInterface.getCurServerTime();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	public void updatePlayRecord(MediaInfor mediaInfor)
	{
		if(NetWorkUtils.isNetConnected(context))
		{
			try
			{	if(mIPushAidlInterface != null)
				mIPushAidlInterface.updatePlayRecord(mediaInfor);
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void init(boolean isPlugin)
	{

		try
		{	if(mIPushAidlInterface != null)
				mIPushAidlInterface.init(isPlugin);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private ServiceConnection mConn = new ServiceConnection()
	{
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) 
        {
        	mIPushAidlInterface = IPushAidlInterface.Stub.asInterface(iBinder);
            if (mIPushAidlInterface != null)
            {
            	try 
            	{
					init(PluginConstant.isPlugin);
            		mIPushAidlInterface.registerCallback((com.znt.push.IPushCallback) mCallback);
				} 
            	catch (Exception e) 
            	{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            else
                Log.e("", "push service bind error!");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) 
        {
        	mIPushAidlInterface = null;
        }
    };
    
    private IPushCallback mCallback = new IPushCallback.Stub()
    {
		@Override
		public void actionPerformed(int id, String arg1, String arg2, String arg3) throws RemoteException 
		{
			// TODO Auto-generated method stub

			if(id == PushModelConstant.CALL_BACK_PUSH_SUCCESS)
			{
				mIDevStatusView.onPushSuccess(null);
			}
			else if(id == PushModelConstant.CALL_BACK_PUSH_FAIL)
			{
				int failCount = 0;
				try 
				{
					if(!TextUtils.isEmpty(arg1))
						failCount = Integer.parseInt(arg1);
				} 
				catch (Exception e) 
				{
					// TODO: handle exception
				}
				mIDevStatusView.onPushFail(failCount);
			}
			else if(id == PushModelConstant.CALL_BACK_PUSH_SPACE_CHECK)
			{
				try
				{
					if(!TextUtils.isEmpty(arg1))
					{
						long size = Integer.parseInt(arg1);
						mIDevStatusView.onSpaceCheck(size);
					}
				} catch (Exception e) 
				{
					// TODO: handle exception
				}
			}
			else if(id == PushModelConstant.CALL_BACK_PUSH_CHECK)
			{
				try
				{
					if(!TextUtils.isEmpty(arg1))
					{
						int count = Integer.parseInt(arg1);
						mIDevStatusView.onPushCheck(count);
					}
				} catch (Exception e) 
				{
					// TODO: handle exception
				}
			}
			else if(id == PushModelConstant.CALL_BACK_INIT_TERMINAL_FINISH)
			{
				try
				{

					mIDevStatusView.onInitterminalFinish(arg1);
				}
				catch (Exception e)
				{
					// TODO: handle exception
				}
			}
			else if(id == PushModelConstant.CALL_BACK_NOTIFY_TIME_UPDATE)
			{
				try
				{
					mIDevStatusView.onUpdateServerTime(arg1);
				}
				catch (Exception e)
				{
					// TODO: handle exception
				}
			}
			else if(id == PushModelConstant.CALL_BACK_REGISTER_FINISH)
			{
				try
				{
					mIDevStatusView.onRegisterFinish(arg1,arg2,arg3);
				}
				catch (Exception e)
				{
					// TODO: handle exception
				}
			}
			else if(id == PushModelConstant.CALL_BACK_GET_CUR_PLAN)
			{
				try
				{
					Log.e(TAG,"############################-->CALL_BACK_GET_CUR_PLAN："+arg1);
					mIDevStatusView.onGetCurPlanFinish();
				}
				catch (Exception e)
				{
					// TODO: handle exception
				}
			}
			else if(id == PushModelConstant.CALL_BACK_GET_CUR_ADV_PLAN)
			{
				try
				{
					mIDevStatusView.onGetAdPlanFinish();
				}
				catch (Exception e)
				{
					// TODO: handle exception
				}
			}
			else if(id == PushModelConstant.CALL_BACK_ON_TIMEING_PUSH_NOTYFY)
			{
				try
				{
					mIDevStatusView.onTimeingPushNotify();
				}
				catch (Exception e)
				{
					// TODO: handle exception
				}
			}
			else if(id == PushModelConstant.CALL_BACK_ON_INTERNAL_TIME_PUSH_NOTYFY)
			{
				try
				{
					mIDevStatusView.onInternalTimePushNotify();
				}
				catch (Exception e)
				{
					// TODO: handle exception
				}
			}
			else if(id == PushModelConstant.CALL_BACK_ON_PUSH_MEDIA_NOTYFY)
			{
				try
				{
					mIDevStatusView.onPushMediaNotify();
				}
				catch (Exception e)
				{
					// TODO: handle exception
				}
			}
			else if(id == PushModelConstant.CALL_BACK_ON_WIFI_CONGFIG_NOTYFY)
			{
				try
				{
					mIDevStatusView.onWifiConfig(arg1, arg2);
				}
				catch (Exception e)
				{
					// TODO: handle exception
				}
			}
			else if(id == PushModelConstant.CALL_BACK_ON_UPDATE_CHECK)
			{
				try
				{
					mIDevStatusView.onUpdateCheck(arg1, arg2, arg3);
				}
				catch (Exception e)
				{
					// TODO: handle exception
				}
			}
			else if(id == PushModelConstant.CALL_BACK_ON_PLAY_STATUS)//通知停止播放
			{
				try
				{
					mIDevStatusView.onPlayStatus(arg1);
				}
				catch (Exception e)
				{
					// TODO: handle exception
				}
			}
		}  
    };

    public void bindService() 
    {
    	try 
    	{
    		// UnBind
            unBindService();

            Intent intent = new Intent(context, ZNTPushService.class);
            context.bindService(intent, mConn, Context.BIND_AUTO_CREATE);

			/*Intent intent = new Intent(v.getContext(), PluginDemoService1.class);
			intent.setAction("action1");
			context.startService(intent);*/
		}
    	catch (Exception e) 
    	{
			Log.e(TAG,"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@4"+e.getMessage());
			showToast("bindService error-->"+e.getMessage());
			// TODO: handle exception
    		Log.e("", e.getMessage());
		}
    }

    public void unBindService() 
    {
        // Service
        if (mIPushAidlInterface != null) 
        {
        	context.unbindService(mConn);
        	mIPushAidlInterface = null;
        }
        
    }

	public void showToast(String text)
	{
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}
    
}
