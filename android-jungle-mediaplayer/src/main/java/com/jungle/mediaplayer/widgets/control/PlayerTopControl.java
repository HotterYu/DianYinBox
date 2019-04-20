/**
 * Android Jungle-MediaPlayer framework project.
 *
 * Copyright 2016 Arno Zhang <zyfgood12@163.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jungle.mediaplayer.widgets.control;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jungle.mediaplayer.R;

public class PlayerTopControl extends FrameLayout {

    private TextView tvDevName = null;
    private TextView tvServerTime = null;

    public interface Listener {
        void onBackBtnClicked();
    }


    private Listener mListener;


    public PlayerTopControl(Context context) {
        super(context);
        initLayout(context);
    }

    public PlayerTopControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
    }

    public PlayerTopControl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout(context);
    }

    private void initLayout(Context context) {




    }

    public void createDefault() {
        create(R.layout.layout_default_player_top_control);
    }

    public void create(int resId) {
        View.inflate(getContext(), resId, this);

        findViewById(R.id.player_back_zone).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onBackBtnClicked();
            }
        });

        tvDevName = (TextView) findViewById(R.id.player_title_dev_name);
        tvServerTime = (TextView) findViewById(R.id.player_title_dev_time);

    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public void show() {
        setVisibility(View.VISIBLE);
    }

    public void setDevName(String devName)
    {
        tvDevName.setText(devName);
    }

    public void setSystemTime(String systemTime)
    {
        tvServerTime.setText(systemTime);
    }

    public void setTitle(String title) {
        TextView titleView = (TextView) findViewById(R.id.player_title);
        titleView.setText(title);
    }

    public ViewGroup getTitleBarExtraContainer() {
        return (ViewGroup) findViewById(R.id.player_title_extra_container);
    }

    public void doDestroy() {

    }

    public void hide() {
        setVisibility(View.GONE);
    }
}