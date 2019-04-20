// Generated code from Butter Knife. Do not modify!
package com.znt.speaker.video;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.znt.speaker.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class VideoPlayer_ViewBinding<T extends VideoPlayer> implements Unbinder {
  protected T target;

  @UiThread
  public VideoPlayer_ViewBinding(T target, View source) {
    this.target = target;

    target.videoView = Utils.findRequiredViewAsType(source, R.id.video_view, "field 'videoView'", TextureVideoPlayer.class);
    target.tvDevInfor = Utils.findRequiredViewAsType(source, R.id.tv_device_info, "field 'tvDevInfor'", TextView.class);
    target.mediaController = Utils.findRequiredViewAsType(source, R.id.mediaController, "field 'mediaController'", VideoMediaController.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.videoView = null;
    target.tvDevInfor = null;
    target.mediaController = null;

    this.target = null;
  }
}
