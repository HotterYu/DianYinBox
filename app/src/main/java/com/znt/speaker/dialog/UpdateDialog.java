package com.znt.speaker.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.znt.lib.utils.ApkTools;
import com.znt.lib.utils.SystemUtils;
import com.znt.lib.utils.ViewUtils;
import com.znt.push.entity.DownloadFileInfo;
import com.znt.push.entity.LocalDataEntity;
import com.znt.push.entity.PushModelConstant;
import com.znt.push.httpmodel.HttpClient;
import com.znt.push.update.ApkDownLoadManager;
import com.znt.push.update.ApkDownloadListener;
import com.znt.push.update.UpdateManager;
import com.znt.speaker.R;

import java.io.File;
import java.util.List;


/**
 * Created by prize on 2018/11/23.
 */

public class UpdateDialog extends Dialog implements ApkDownloadListener
{

    private final String TAG = "UpdateDialog";

    private TextView textTitle = null;
    private Button btnLeft = null;
    private Button btnRight = null;
    private TextView tvDesc = null;

    private View viewProgess = null;
    private TextView tvProgess = null;
    private ProgressBar progressBar = null;

    private Activity activity = null;

    private File apkFile = null;
    private File apkDir = null;
    private String pkgInstallerName = "com.znt.install";
    private String pkgSpeakerName = "com.znt.speaker";

    private String vName = "";
    private String vCode = "";
    private String vUrl = "";
    private final int DOWNLOAD_FILE = 3;
    private final int DOWNLOAD_FILE_SUCCESS = 4;
    private final int DOWNLOAD_FILE_FAIL = 5;
    private final int DOWNLOAD_FILE_PROGRESS = 6;

    public interface OnUpdateResultListener
    {
        void onPluginUpdateCallBack();
    }
    public OnUpdateResultListener mOnUpdateResultListener = null;


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            if(msg.what == DOWNLOAD_FILE)
            {
                Toast.makeText(activity,"升级包开始下载...",Toast.LENGTH_LONG).show();

            }
            else if(msg.what == DOWNLOAD_FILE_SUCCESS)
            {
                progressBar.setProgress(100);

                startInstallApk();
            }
            else if(msg.what == DOWNLOAD_FILE_FAIL)
            {
                String error = (String)msg.obj;
                Toast.makeText(activity,"升级包下载失败:"+error,Toast.LENGTH_LONG).show();
                tvProgess.setText("升级包下载失败，请重试");
            }
            else if(msg.what == DOWNLOAD_FILE_PROGRESS)
            {
                long progress = (Long) msg.obj;
                progressBar.setProgress((int) progress);

            }
        };
    };

    /**
     * <p>Title: </p>
     * <p>Description: </p>
     * @param context
     */
    public UpdateDialog(Activity context, String vName,String vCode,String vUrl,OnUpdateResultListener mOnUpdateResultListener)
    {
        super(context, R.style.custom_dialog);
        // TODO Auto-generated constructor stub
        this.activity = context;
        this.vName = vName;
        this.vCode = vCode;
        this.vUrl = vUrl;
        this.mOnUpdateResultListener = mOnUpdateResultListener;

        initData();
    }

    public UpdateDialog(Activity context, int themeCustomdialog)
    {
        super(context, themeCustomdialog);
        // TODO Auto-generated constructor stub
        this.activity = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_update);
        setScreenBrightness();


        btnRight = (Button) UpdateDialog.this.findViewById(R.id.btn_dialog_name_edit_right);
        btnLeft = (Button) UpdateDialog.this.findViewById(R.id.btn_dialog_name_edit_left);
        textTitle = (TextView) UpdateDialog.this.findViewById(R.id.tv_dialog_name_edit_title);
        tvDesc = (TextView) UpdateDialog.this.findViewById(R.id.tv_update_desc);

        viewProgess =  UpdateDialog.this.findViewById(R.id.view_update_progress);
        tvProgess = (TextView) UpdateDialog.this.findViewById(R.id.tv_update_progress);
        progressBar = (ProgressBar) UpdateDialog.this.findViewById(R.id.pb_update_progress);

        textTitle.setText(activity.getResources().getString(R.string.update_title) + vName);
        tvDesc.setText(activity.getResources().getString(R.string.update_hint));

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

    }

    private void showDownloadProgress(boolean show)
    {
        if(show)
        {
            viewProgess.setVisibility(View.VISIBLE);

        }
        else
        {
            viewProgess.setVisibility(View.GONE);

        }

    }

    private void initViews()
    {

        setCanceledOnTouchOutside(false);

        btnRight.requestFocus();

        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(apkFile != null && apkFile.exists())
                    installByClick(apkFile.getPath());
                else
                {
                    tvProgess.setText("升级包不存在，开始下载升级包");
                    downloadApkFile(vUrl);
                }
            }
        });

        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(activity,"无法退出，请先升级!",Toast.LENGTH_LONG).show();
            }
        });

        downloadApkFile(vUrl);
    }

    private void setScreenBrightness()
    {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();

        lp.dimAmount = 0;
        window.setAttributes(lp);
    }

    private void onErroLogReport(String error)
    {
        HttpClient.errorReport(activity,error);
    }

    private boolean isVersionNew(int updateVersionNum)
    {
        try
        {
            int curAppVersionNum = SystemUtils.getVersionCode(activity);
            if(updateVersionNum > curAppVersionNum)
                return true;
            else
                onErroLogReport("isVersionNew is false, cur version-->"+curAppVersionNum);

        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            onErroLogReport("isVersionNew check excetion-->"+e.getMessage());
        }

        return false;
    }

    private void initData()
    {
        apkDir = SystemUtils.getAvailableDir(activity, PushModelConstant.WORK_DIR + "/update/");
        if (!apkDir.exists())
        {
            apkDir.mkdirs();
        }
        apkFile = SystemUtils.getAvailableDir(activity, PushModelConstant.WORK_DIR + "/update/DianYinBox.apk");

    }

    public void doApkInstall(String url)
    {
        try
        {
            if(apkFile.exists() && isSignatureMatch())
            {
                int apkFileVersion = SystemUtils.getApkFileInfor(activity, apkFile.getAbsolutePath()).versionCode;
                if(isVersionNew(apkFileVersion))
                    startInstallApk();
                else
                {
                    apkFile.delete();
                    downloadApkFile(url);
                }
            }
            else
                downloadApkFile(url);
        }
        catch (Exception e)
        {
            // TODO: handle exception

            onErroLogReport("download file error-->"+e.getMessage());

        }
    }

    private boolean isDownloadRunning = false;
    private void downloadApkFile(final String downUrl)
    {
        if(isDownloadRunning)
            return;
        showDownloadProgress(true);
        ApkDownLoadManager.getInstance().startDownload(downUrl, apkDir.getAbsolutePath(), this);
        //downHelper.downloadFile(downUrl, apkFile.getAbsolutePath());
    }

    public void startInstallApk()
    {
        if(isFileValid())
        {
            installByAuto();
        }
    }
    private void installByClick(String filePath) {

        tvProgess.setText("安装包已准备好，请手动安装");

        Log.i(TAG, "开始执行安装: " + filePath);
        File apkFile = new File(filePath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Log.w(TAG, "版本大于 N ，开始使用 fileProvider 进行安装");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(activity, "com.znt.speaker.fileprovider" , apkFile);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            Log.w(TAG, "正常进行安装");
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }
        activity.startActivity(intent);
    }

    private void installByAuto()
    {
        Intent intent = activity.getPackageManager().getLaunchIntentForPackage(pkgInstallerName);
        if(intent != null)
        {
            try
            {
                tvProgess.setText("安装包已准备好，正在进行自动安装");

                intent.putExtra("pkg_name", pkgSpeakerName);
                intent.putExtra("apk_path", apkFile.getAbsolutePath());
                activity.startActivity(intent);

                System.exit(0);
                android.os.Process.killProcess(android.os.Process.myPid());
            }
            catch (Exception e)
            {
                if(mOnUpdateResultListener != null)
                {
                    mOnUpdateResultListener.onPluginUpdateCallBack();
                }
                //installByClick(apkFile.getAbsolutePath());
                //installByClick();
                // TODO: handle exception
                onErroLogReport("installByAuto exception-->" + e.getMessage());
            }
        }
        else
        {
            if(mOnUpdateResultListener != null)
            {
                mOnUpdateResultListener.onPluginUpdateCallBack();
            }
            //installByClick(apkFile.getAbsolutePath());
            onErroLogReport("installByAuto but intent is null");
        }

    }

    private boolean isFileValid()
    {
        if(!apkFile.exists())
        {
            onErroLogReport("apk file not found");
            tvProgess.setText("安装包找不到，请重试");
            return false;
        }
        if(apkFile.length() == 0)
        {
            apkFile.delete();
            onErroLogReport("apk file length==0 and delete it");
            return false;
        }


        return isSignatureMatch();
    }

    private boolean isSignatureMatch()
    {
        String curSign = ApkTools.getSignature(activity);
        List<String> signs = ApkTools.getSignaturesFromApk(apkFile);

        if(curSign == null || signs.size() == 0
                || signs.get(0) == null || !curSign.equals(signs.get(0)))
        {
            apkFile.delete();
            tvProgess.setText("安装包签名不一致，请联系客服");
            onErroLogReport("apk sign is not match and delete it");
            return false;
        }

        return true;
    }

    @Override
    public void onDownloadStart(DownloadFileInfo info)
    {
        // TODO Auto-generated method stub
        ViewUtils.sendMessage(handler, DOWNLOAD_FILE);
        isDownloadRunning = true;
    }

    @Override
    public void onFileExist(DownloadFileInfo info)
    {
        // TODO Auto-generated method stub
        ViewUtils.sendMessage(handler, DOWNLOAD_FILE_SUCCESS);
        isDownloadRunning = false;
    }

    @Override
    public void onDownloadProgress(final long progress, final long size)
    {
        // TODO Auto-generated method stub
        isDownloadRunning = true;
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressBar.setMax((int) size);
                progressBar.setProgress((int) progress);
            }
        });
        //ViewUtils.sendMessage(handler, DOWNLOAD_FILE_PROGRESS, progress);
    }

    @Override
    public void onDownloadError(DownloadFileInfo info,String error)
    {
        // TODO Auto-generated method stub
        ViewUtils.sendMessage(handler, DOWNLOAD_FILE_FAIL, error);
        onErroLogReport(error);
        isDownloadRunning = false;
    }

    @Override
    public void onDownloadFinish(File info)
    {
        // TODO Auto-generated method stub
        ViewUtils.sendMessage(handler, DOWNLOAD_FILE_SUCCESS);
        isDownloadRunning = false;

    }

    @Override
    public void onDownloadExit(DownloadFileInfo info)
    {
        // TODO Auto-generated method stub
        isDownloadRunning = false;
    }

    @Override
    public void onSpaceCheck(long size)
    {
        // TODO Auto-generated method stub
        if(mSpaceCheckListener != null)
            mSpaceCheckListener.onSpaceCheck(size);
    }

    private UpdateManager.SpaceCheckListener mSpaceCheckListener = null;
    public interface SpaceCheckListener
    {
        public void onSpaceCheck(long size);
    }
}