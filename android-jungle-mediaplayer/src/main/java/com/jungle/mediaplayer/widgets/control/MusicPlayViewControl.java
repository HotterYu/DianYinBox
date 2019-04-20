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
import android.util.MonthDisplayHelper;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.jungle.mediaplayer.R;

public class MusicPlayViewControl extends FrameLayout {

    public MusicPlayViewControl(Context context) {
        super(context);
        initLayout(context);
    }

    public MusicPlayViewControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
    }

    public MusicPlayViewControl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout(context);
    }

    public void setOnClickListener(OnClickListener mOnClickListener)
    {
        ivLogo.setOnClickListener(mOnClickListener);
    }

    private ImageView ivLogo = null;
    private void initLayout(Context context) {
        View.inflate(context, R.layout.layout_music_play_view, this);

        ivLogo = (ImageView) findViewById(R.id.iv_media_play_logo);
        /*mLoadingIcon = findViewById(R.id.loading_icon);
        mLoadingContainer = findViewById(R.id.loading_container);
        mLoadingErrorContainer = findViewById(R.id.loading_error_container);*/
    }


}
