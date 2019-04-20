// Generated code from Butter Knife. Do not modify!
package com.znt.speaker.video;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.znt.speaker.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class VideoMediaController_ViewBinding<T extends VideoMediaController> implements Unbinder {
  protected T target;

  private View view2131230834;

  private View view2131230835;

  private View view2131230833;

  private View view2131230830;

  @UiThread
  public VideoMediaController_ViewBinding(final T target, View source) {
    this.target = target;

    View view;
    target.pbLoading = Utils.findRequiredViewAsType(source, R.id.pb_loading, "field 'pbLoading'", ProgressBar.class);
    view = Utils.findRequiredView(source, R.id.iv_replay, "field 'ivReplay' and method 'onClick'");
    target.ivReplay = Utils.castView(view, R.id.iv_replay, "field 'ivReplay'", ImageView.class);
    view2131230834 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.iv_share, "field 'ivShare' and method 'onClick'");
    target.ivShare = Utils.castView(view, R.id.iv_share, "field 'ivShare'", ImageView.class);
    view2131230835 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    target.rlPlayFinish = Utils.findRequiredViewAsType(source, R.id.rl_play_finish, "field 'rlPlayFinish'", RelativeLayout.class);
    target.tvTitle = Utils.findRequiredViewAsType(source, R.id.tv_title, "field 'tvTitle'", TextView.class);
    view = Utils.findRequiredView(source, R.id.iv_play, "field 'ivPlay' and method 'onClick'");
    target.ivPlay = Utils.castView(view, R.id.iv_play, "field 'ivPlay'", ImageView.class);
    view2131230833 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    target.tvCurTime = Utils.findRequiredViewAsType(source, R.id.tv_cur_time, "field 'tvCurTime'", TextView.class);
    target.tvUseTime = Utils.findRequiredViewAsType(source, R.id.tv_use_time, "field 'tvUseTime'", TextView.class);
    target.seekBar = Utils.findRequiredViewAsType(source, R.id.seekBar, "field 'seekBar'", SeekBar.class);
    target.tvTime = Utils.findRequiredViewAsType(source, R.id.tv_time, "field 'tvTime'", TextView.class);
    target.mProgressBar = Utils.findRequiredViewAsType(source, R.id.pb_play_loading, "field 'mProgressBar'", ProgressBar.class);
    view = Utils.findRequiredView(source, R.id.iv_fullscreen, "field 'ivFullscreen' and method 'onClick'");
    target.ivFullscreen = Utils.castView(view, R.id.iv_fullscreen, "field 'ivFullscreen'", ImageView.class);
    view2131230830 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    target.llPlayControl = Utils.findRequiredViewAsType(source, R.id.ll_play_control, "field 'llPlayControl'", LinearLayout.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.pbLoading = null;
    target.ivReplay = null;
    target.ivShare = null;
    target.rlPlayFinish = null;
    target.tvTitle = null;
    target.ivPlay = null;
    target.tvCurTime = null;
    target.tvUseTime = null;
    target.seekBar = null;
    target.tvTime = null;
    target.mProgressBar = null;
    target.ivFullscreen = null;
    target.llPlayControl = null;

    view2131230834.setOnClickListener(null);
    view2131230834 = null;
    view2131230835.setOnClickListener(null);
    view2131230835 = null;
    view2131230833.setOnClickListener(null);
    view2131230833 = null;
    view2131230830.setOnClickListener(null);
    view2131230830 = null;

    this.target = null;
  }
}
