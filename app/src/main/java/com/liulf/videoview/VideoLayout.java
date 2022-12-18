package com.liulf.videoview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class VideoLayout extends FrameLayout {
  /**
   * 由ijkplayer提供，用于播放视频，需要给他传入一个surfaceView
   */
  final static String TAG = VideoLayout.class.getSimpleName();
  private final IjkMediaPlayer mMediaPlayer;
  /**
   * 视频文件地址
   */
  private String mPath = "";
  private SurfaceView surfaceView;
  private Context mContext;
  private IMediaPlayer.OnCompletionListener onCompletionListener;
  private IMediaPlayer.OnPreparedListener onPreparedListener;
  private IMediaPlayer.OnErrorListener onErrorListener;

  public VideoLayout(@NonNull Context context) {
    super(context);
    setContext(context);
    mMediaPlayer = createPlayer();
  }

  public VideoLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    setContext(context);
    mMediaPlayer = createPlayer();
  }

  public VideoLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    setContext(context);
    mMediaPlayer = createPlayer();
  }

  private IjkMediaPlayer createPlayer() {
    IjkMediaPlayer mMediaPlayer = new IjkMediaPlayer();
    mMediaPlayer.setOption(1, "analyzemaxduration", 100);
    mMediaPlayer.setOption(1, "probesize", 10240);
    mMediaPlayer.setOption(1, "flush_packets", 1);
    mMediaPlayer.setOption(4, "packet-buffering", 0);
    mMediaPlayer.setOption(4, "framedrop", 1);
    mMediaPlayer.setOption(4, "max-fps", 30);
    mMediaPlayer.setOption(2, "skip_loop_filter", 0);
    mMediaPlayer.setOption(1, "analyzemaxduration", 100);
    mMediaPlayer.setOption(4, "max-buffer-size", 1024 * 1024 * 10);
    mMediaPlayer.setOption(4, "packet-buffering", 1);
    mMediaPlayer.setOption(1, "dns_cache_clear", 1);
    mMediaPlayer.setOption(4, "mediacodec", 1);
    mMediaPlayer.setOption(4, "mediacodec-auto-rotate", 1);
    mMediaPlayer.setOption(4, "mediacodec-handle-resolution-change", 1);
    IjkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_SILENT);
    return mMediaPlayer;
  }


  public void setOnErrorListener(IMediaPlayer.OnErrorListener onErrorListener) {
    this.onErrorListener = onErrorListener;
  }

  public void setOnCompletionListener(IMediaPlayer.OnCompletionListener onCompletionListener) {
    this.onCompletionListener = onCompletionListener;
  }

  public void setOnPreparedListener(IMediaPlayer.OnPreparedListener onPreparedListener) {
    this.onPreparedListener = onPreparedListener;
  }

  public void setContext(Context mContext) {
    this.mContext = mContext;
  }

  /**
   * 设置视频地址。
   * 根据是否第一次播放视频，做不同的操作。
   *
   * @param path the path of the video.
   */
  public void setVideoPath(String path) {
    Log.i(TAG,"setVideoPath");
    mPath = path;
      //如果是第一次播放视频，那就创建一个新的surfaceView
    createSurfaceView();
  }

  /**
   * 新建一个surfaces
   */
  private void createSurfaceView() {
    Log.i(TAG,"createSurfaceView");
    //生成一个新的surface view
    surfaceView = new SurfaceView(mContext);
    surfaceView.getHolder().addCallback(new LmnSurfaceCallback());

    LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT
        , LayoutParams.MATCH_PARENT, Gravity.CENTER);
    surfaceView.setLayoutParams(layoutParams);
    this.removeAllViews();
    this.addView(surfaceView);
  }

  /**
   * 加载视频
   */
  private void load() {
    //每次都要重新创建IMediaPlayer
    Log.i(TAG,"load");
    mMediaPlayer.setDisplay(surfaceView.getHolder());

    setCallBack();
    try {
      mMediaPlayer.setDataSource(mPath);
    } catch (IOException e) {
      e.printStackTrace();
    }
    //给mediaPlayer设置视图
    mMediaPlayer.prepareAsync();
  }

  /**
   * 创建一个新的player
   */
  private void setCallBack() {
    Log.i(TAG,"setCallBack");
    if (onCompletionListener != null) {
      mMediaPlayer.setOnCompletionListener(onCompletionListener);
    }

    if(onPreparedListener != null){
      mMediaPlayer.setOnPreparedListener(onPreparedListener);
    }

    if(onErrorListener != null){
      mMediaPlayer.setOnErrorListener(onErrorListener);
    }
  }


  @SuppressWarnings("unused")
  public void pause() {
    Log.i(TAG,"pause");
    mMediaPlayer.pause();
  }

  public void playVideo(String filePath) {
    Log.i(TAG,"from lib playVideo");
    setVideoPath(filePath);
  }

  /**
   * surfaceView的监听器
   */
  private class LmnSurfaceCallback implements SurfaceHolder.Callback {
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
      load();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
      Log.i(TAG,"surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
      Log.i(TAG,"surfaceDestroyed");
      mMediaPlayer.release();
    }
  }
}
