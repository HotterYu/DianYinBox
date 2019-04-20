package com.znt.speaker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.znt.speaker.media.MediaScanFactory;

import service.ZNTDownloadServiceManager;

public class MainActivity extends AppCompatActivity implements ZNTDownloadServiceManager.DownlaodCallBack {

    private final String TAG = "MainActivity";

    private Button btnTest = null;
    private Button btnTest1 = null;
    private Button btnTest2 = null;
    private Button btnTest3 = null;
    private Button btnTest4 = null;




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnTest = (Button)findViewById(R.id.btn_test);
        btnTest1 = (Button)findViewById(R.id.btn_test1);
        btnTest2 = (Button)findViewById(R.id.btn_test2);
        btnTest3 = (Button)findViewById(R.id.btn_test3);
        btnTest4 = (Button)findViewById(R.id.btn_test4);


        btnTest.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //PluginServiceClient.ProxyRePluginServiceClientVar
            }
        });
        btnTest1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                /*MediaInfor tempMedia = new MediaInfor();
                tempMedia.setMediaUrl("http://ubmcvideo.baidustatic.com/media/v1/0f000rbELDnPpJP_xbNBt0.mp4");
                mZNTDownloadServiceManager.addSonginfor(tempMedia);*/
                /*List<MediaInfor> mediaList =  db.getAllMedias();
                List<LocalMediaInfor> localMediaInfors =  db.getAllLocalMedias();*/
            }
        });
        btnTest2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                /*MediaInfor tempMedia = new MediaInfor();
                tempMedia.setMediaUrl("http://ubmcvideo.baidustatic.com/media/v1/0f000rbELDnPpJP_xbNBt0.mp4");
                mZNTDownloadServiceManager.addSonginfor(tempMedia);*/
                //db.deleteAllMedias();
            }
        });
        btnTest3.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                /*MediaInfor tempMedia = new MediaInfor();
                tempMedia.setMediaUrl("http://ubmcvideo.baidustatic.com/media/v1/0f000rbELDnPpJP_xbNBt0.mp4");
                mZNTDownloadServiceManager.addSonginfor(tempMedia);*/
                //db.modifyData();
                //mZNTPushServiceManager.bindService();

                //mMediaScanFactory.scanLocalMedias();
            }
        });
        btnTest4.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                /*MediaInfor tempMedia = new MediaInfor();
                tempMedia.setMediaUrl("http://ubmcvideo.baidustatic.com/media/v1/0f000rbELDnPpJP_xbNBt0.mp4");
                mZNTDownloadServiceManager.addSonginfor(tempMedia);*/

                /*Person p1 = new Person();
                p1.setAge(10000);
                p1.setName("马云");
                p1.setSex("不男不女");
                //p1.setId(36);
                //db.insertTest(p1);
                db.deleteDbFile();*/

                Intent intent = new Intent();
                intent.setClass(getApplicationContext(),VideoPageActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    public void onDownloadSpaceCheck(String size)
    {
        Log.d(TAG, "onDownloadSpaceCheck: ");
    }

    @Override
    public void onDownloadRecordInsert(String mediaName, String mediaUrl, String modifyTime)
    {
        Log.d(TAG, "onDownloadRecordInsert: ");
    }

    @Override
    public void onRemoveLargeSize(String url)
    {
        Log.d(TAG, "onRemoveLargeSize: ");
    }

}
