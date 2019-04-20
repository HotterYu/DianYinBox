package com.znt.push;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.znt.lib.bean.MediaInfor;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MediaInfor mediaInfor = new MediaInfor();

    }
}
