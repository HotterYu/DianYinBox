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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.znt.lib.bean.RegisterTerminalResultBean;
import com.znt.lib.bean.TerminalRunstatusInfo;
import com.znt.push.entity.LocalDataEntity;
import com.znt.push.httpmodel.HttpCallback;
import com.znt.push.httpmodel.HttpClient;
import com.znt.speaker.R;


/**
 * Created by prize on 2018/11/23.
 */

public class SettingDialog extends Dialog
{

    private final String TAG = "SettingDialog";

    private TextView textTitle = null;
    private Button btnLeft = null;
    private Button btnRight = null;
    private TextView tvErrorHint = null;

    private CheckBox cbVolume = null;
    private CheckBox cbWifi = null;

    private Activity activity = null;

    private boolean isWifiSetOpen = false;
    private boolean isVolumeSetOpen = false;

    @SuppressLint("HandlerLeak")

    /**
     * <p>Title: </p>
     * <p>Description: </p>
     * @param context
     */
    public SettingDialog(Activity context, boolean isWifiSetOpen , boolean isVolumeSetOpen)
    {
        super(context, R.style.custom_dialog);
        // TODO Auto-generated constructor stub
        this.activity = context;
        this.isWifiSetOpen = isWifiSetOpen;
        this.isVolumeSetOpen = isVolumeSetOpen;

    }

    public SettingDialog(Activity context, int themeCustomdialog)
    {
        super(context, themeCustomdialog);
        // TODO Auto-generated constructor stub
        this.activity = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_setting);
        setScreenBrightness();


        btnRight = (Button) SettingDialog.this.findViewById(R.id.btn_dialog_name_edit_right);
        btnLeft = (Button) SettingDialog.this.findViewById(R.id.btn_dialog_name_edit_left);
        textTitle = (TextView) SettingDialog.this.findViewById(R.id.tv_dialog_name_edit_title);
        tvErrorHint = (TextView) SettingDialog.this.findViewById(R.id.tv_error_hint);
        cbVolume = (CheckBox) SettingDialog.this.findViewById(R.id.cb_setting_volume);
        cbWifi = (CheckBox) SettingDialog.this.findViewById(R.id.cb_setting_wifi);

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

        cbWifi.setChecked(isWifiSetOpen);
        cbVolume.setChecked(isVolumeSetOpen);

    }

    public boolean isWifiSetOpen()
    {
        return cbWifi.isChecked();
    }
    public boolean isVolumeSetOpen()
    {
        return cbVolume.isChecked();
    }

    private boolean isUpdateEnable = false;
    public boolean isUpdateEnable()
    {
        return isUpdateEnable;
    }
    private void initViews()
    {

        setCanceledOnTouchOutside(false);

        btnRight.requestFocus();

        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isUpdateEnable = true;
                dismiss();

            }
        });

        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isUpdateEnable = false;
                dismiss();
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