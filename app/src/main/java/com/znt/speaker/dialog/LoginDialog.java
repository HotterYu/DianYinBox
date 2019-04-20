package com.znt.speaker.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.znt.lib.bean.InitTerminalResultBean;
import com.znt.lib.bean.RegisterTerminalResultBean;
import com.znt.lib.bean.TerminalRunstatusInfo;
import com.znt.lib.utils.SystemUtils;
import com.znt.push.entity.LocalDataEntity;
import com.znt.push.entity.PushModelConstant;
import com.znt.push.httpmodel.HttpCallback;
import com.znt.push.httpmodel.HttpClient;
import com.znt.speaker.R;


/**
 * Created by prize on 2018/11/23.
 */

public class LoginDialog extends Dialog
{

    private final String TAG = "UpdateDialog";

    private TextView textTitle = null;
    private Button btnLeft = null;
    private Button btnRight = null;
    private TextView tvErrorHint = null;

    private EditText etCode = null;
    private EditText etName = null;

    private Activity activity = null;

    private String name = "";
    private String code = "";
    private String devId = "";
    private final int DOWNLOAD_FILE = 3;
    private final int DOWNLOAD_FILE_SUCCESS = 4;
    private final int DOWNLOAD_FILE_FAIL = 5;
    private final int DOWNLOAD_FILE_PROGRESS = 6;
    @SuppressLint("HandlerLeak")

    private OnRegisterByClickFinish mOnRegisterByClickFinish = null;
    public void setOnRegisterByClickFinish(OnRegisterByClickFinish mOnRegisterByClickFinish )
    {
        this.mOnRegisterByClickFinish = mOnRegisterByClickFinish;
    }
    public interface OnRegisterByClickFinish
    {
        void onRegisterByClickFinish(String devId, String devName);
    }

    /**
     * <p>Title: </p>
     * <p>Description: </p>
     * @param context
     */
    public LoginDialog(Activity context, String name, String code, String devId)
    {
        super(context, R.style.custom_dialog);
        // TODO Auto-generated constructor stub
        this.activity = context;
        this.name = name;
        this.code = code;
        this.devId = devId;

    }

    public LoginDialog(Activity context, int themeCustomdialog)
    {
        super(context, themeCustomdialog);
        // TODO Auto-generated constructor stub
        this.activity = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_login);
        setScreenBrightness();


        btnRight = (Button) LoginDialog.this.findViewById(R.id.btn_dialog_name_edit_right);
        btnLeft = (Button) LoginDialog.this.findViewById(R.id.btn_dialog_name_edit_left);
        textTitle = (TextView) LoginDialog.this.findViewById(R.id.tv_dialog_name_edit_title);
        tvErrorHint = (TextView) LoginDialog.this.findViewById(R.id.tv_error_hint);
        etCode = (EditText) LoginDialog.this.findViewById(R.id.et_login_dialog_code);
        etName = (EditText) LoginDialog.this.findViewById(R.id.et_login_dialog_name);

        this.setOnShowListener(new OnShowListener()
        {
            @Override
            public void onShow(DialogInterface dialog)
            {
                initViews();
            }
        });
        this.setOnDismissListener(new OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface arg0)
            {
                // TODO Auto-generated method stub

            }
        });

        /*etCode.setText("94fb0a580f9f0870");
        etName.setText("测试测试测试");*/
    }

    private void initViews()
    {

        setCanceledOnTouchOutside(false);

        btnRight.requestFocus();

        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(SystemUtils.isNetConnected(activity))
                {
                    String ternimalId = LocalDataEntity.newInstance(activity).getDeviceId();
                    if(!TextUtils.isEmpty(ternimalId))
                        register(ternimalId);
                    else
                        Toast.makeText(activity, "该设备未注册，请联网或者重启软件再试", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(activity, "请先连接网络", Toast.LENGTH_SHORT).show();
            }
        });

        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    private boolean isRegisterRunning = false;
    private void register(String ternimalId)
    {
        if(isRegisterRunning)
            return;
        String code = etCode.getText().toString().trim();
        String name = etName.getText().toString().trim();

        if(TextUtils.isEmpty(code))
        {
            tvErrorHint.setText("请输入激活码(由总部提供，或找店音管理员)");
            return;
        }
        if(TextUtils.isEmpty(name))
        {
            name = Build.MODEL + "_" + devId;
        }

        HttpClient.login(activity,ternimalId, code, name,new HttpCallback<RegisterTerminalResultBean>() {
            @Override
            public void onSuccess(RegisterTerminalResultBean registerTerminalResultBean)
            {
                isRegisterRunning = false;
                if(registerTerminalResultBean.isSuccess())
                {
                    try
                    {
                        TerminalRunstatusInfo curTerminalRunstatusInfo = registerTerminalResultBean.getData();
                        if(curTerminalRunstatusInfo != null)
                        {
                            String terminalId = curTerminalRunstatusInfo.getTerminalId();
                            String terminalName = curTerminalRunstatusInfo.getShopname();
                            String deviceCode = curTerminalRunstatusInfo.getShopcode();
                            LocalDataEntity.newInstance(activity).setDeviceId(terminalId);
                            LocalDataEntity.newInstance(activity).setDeviceName(terminalName);
                            LocalDataEntity.newInstance(activity).setDeviceCode(deviceCode);

                            if(mOnRegisterByClickFinish != null)
                                mOnRegisterByClickFinish.onRegisterByClickFinish(terminalId, terminalName);
                        }
                    }
                    catch (Exception e)
                    {
                        if(e != null)
                            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(activity, "register exception", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
                else
                    Toast.makeText(activity, "登陆失败:"+registerTerminalResultBean.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFail(Exception e) {
                isRegisterRunning = false;
                if(e != null)
                    Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(activity, "register exception", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setScreenBrightness()
    {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();

        lp.dimAmount = 0;
        window.setAttributes(lp);
    }

}