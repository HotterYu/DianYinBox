package com.znt.speaker.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.znt.lib.utils.NetWorkUtils;
import com.znt.lib.utils.SystemUtils;
import com.znt.speaker.R;

public class DevInfoView extends RelativeLayout {

    private Context mContext = null;

    protected View mTopView;
    private TextView tvDevNum;
    private TextView tvDevStatus;
    private TextView tvDevPlayStatus;
    private TextView tvDevVersion;
    private TextView tvNetInfo;
    private TextView tvProcStatus;

    private TextView tvLogin;
    private TextView tvSetting;
    private TextView tvUpdate;
    private TextView tvClose;

    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    public DevInfoView(Context context) {
        super(context);
        init(context);
    }

    public DevInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DevInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context)
    {

        this.mContext = context;

        View parentView = LayoutInflater.from(context).inflate(R.layout.activity_dev_info,this,true);

        mTopView = parentView.findViewById(R.id.view_jz_top);
        tvDevNum = (TextView) parentView.findViewById(R.id.jz_tv_dev_num);
        tvDevStatus = (TextView) parentView.findViewById(R.id.jz_tv_dev_status);
        tvDevPlayStatus = (TextView) parentView.findViewById(R.id.jz_tv_dev_play_status);
        tvDevVersion = (TextView) parentView.findViewById(R.id.jz_tv_dev_version);
        tvProcStatus = (TextView) parentView.findViewById(R.id.jz_tv_dev_pro_status);
        tvNetInfo = (TextView) parentView.findViewById(R.id.jz_tv_dev_net_info);
        tvLogin = (TextView) parentView.findViewById(R.id.jz_tv_login);
        tvClose = (TextView) parentView.findViewById(R.id.jz_tv_close);
        tvSetting = (TextView) parentView.findViewById(R.id.jz_tv_setting);
        tvUpdate = (TextView) parentView.findViewById(R.id.jz_tv_update);

    }

    public void setDevInfor(final String info, final String version)
    {
        mHandler.post(new Runnable() {
            @Override
            public void run()
            {
                if(isShown())
                {
                    setDevInfor(info);
                    setDevVersion(version);
                }

            }
        });
    }

    public void setDevInfor(String title)
    {
        if(isShown())
            tvDevNum.setText(title);
    }

    public void setDevStatus(boolean isOnLine)
    {
        if(isShown())
        {
            if(isOnLine)
            {
                tvDevStatus.setText("在线状态：在线");
                tvDevStatus.setTextColor(getResources().getColor(R.color.white));
            }
            else
            {
                tvDevStatus.setText("在线状态：离线");
                tvDevStatus.setTextColor(getResources().getColor(R.color.color_red));
            }
        }

    }

    private int updateNetInfoCount = 0;
    public void setDevPlayStatus(String status)
    {
        if(isShown())
        {
            if(status.equals("0"))//正常
            {
                tvDevPlayStatus.setText("播放状态：正常");
                tvDevPlayStatus.setTextColor(getResources().getColor(R.color.white));
            }
            else if(status.equals("1"))//停止，当前时段没有
            {
                tvDevPlayStatus.setText("当前时间无播放内容");
                tvDevPlayStatus.setTextColor(getResources().getColor(R.color.color_red));
            }
            else if(status.equals("2"))//停止，没有设置播放计划
            {
                tvDevPlayStatus.setText("当前时间无播放计划");
                tvDevPlayStatus.setTextColor(getResources().getColor(R.color.color_red));
            }
            if(updateNetInfoCount >= 12)
            {
                updateNetInfoCount = 0;
                showNetWorkInfo();
            }
            else
                updateNetInfoCount++;
        }
    }

    public String getShowStatus()
    {
        if(tvDevPlayStatus != null)
            return tvDevPlayStatus.getText().toString();
        else
            return "";
    }

    public void showNetWorkInfo()
    {
        if(isShown())
        {
            String ip = SystemUtils.getIP();
            String netType = "";
            if(NetWorkUtils.checkEthernet(getContext()))
                netType = "有线网络";
            else
            {
                netType = "WIFI:"+NetWorkUtils.getWifiName(getContext());
            }
            if(TextUtils.isEmpty(netType))
                netType = "无网络连接";

            tvNetInfo.setText(netType+"\n"+ip);
        }
    }

    public void showProcStatus(String status)
    {
        tvProcStatus.setText("进程状态："+status);
    }

    public void setDevVersion(String version)
    {
        tvDevVersion.setText(version);
    }

    public void setOnCloseClickListener(OnClickListener l)
    {
        tvClose.setOnClickListener(l);
    }
    public void setOnSettingClickListener(OnClickListener l)
    {
        tvSetting.setOnClickListener(l);
    }
    public void setOnDevVersionClickListener(OnClickListener l)
    {
        tvDevVersion.setOnClickListener(l);
    }

    public void setOnLoginClickListener(OnClickListener l)
    {
        tvLogin.setOnClickListener(l);
    }

    public void setOnUpdateCheckClickListener(OnClickListener l)
    {
        tvUpdate.setOnClickListener(l);
    }
}
