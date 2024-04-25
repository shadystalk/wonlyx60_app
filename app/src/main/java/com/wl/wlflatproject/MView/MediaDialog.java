package com.wl.wlflatproject.MView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.blankj.utilcode.util.ScreenUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.wl.wlflatproject.R;

public class MediaDialog extends Dialog {
    private boolean isImage;

    private String mUrl;

    private ImageView imageView, playIv;
    private ConstraintLayout mainLayout;

    private VideoView videoView;

    private MediaController mediaController;
    private MediaPlayer mediaPlayer;

    private ProgressBar loadingBar;

    /**
     * 媒体显示窗
     *
     * @param context 上下文
     * @param url     图片或视频地址
     * @param image   是否是图片
     */
    public MediaDialog(@NonNull Context context, String url, boolean image, int themeResId) {
        super(context, themeResId);
        mUrl = url;
        isImage = image;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        initView();
        // 设置对话框的宽高
        Window window = getWindow();
        if (window != null) {
            window.setLayout(ScreenUtils.getScreenHeight() / 16 * 9, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    protected void initView() {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.media_popup_layout, null);

        imageView = mView.findViewById(R.id.pic_iv);
        videoView = mView.findViewById(R.id.videoView);
        loadingBar = mView.findViewById(R.id.loadProgress);
        playIv = mView.findViewById(R.id.play_iv);
        mainLayout = mView.findViewById(R.id.popup_layout);
        if (isImage) {
            initPicView();
        } else {
            initVideoView();
        }
        setContentView(mView);
    }

    /**
     * 显示图片
     */
    private void initPicView() {
        imageView.setVisibility(VISIBLE);

        Glide.with(getContext()).load(mUrl).into(imageView);
    }

    /**
     * 显示视频
     */
    private void initVideoView() {
        videoView.setVisibility(VISIBLE);

        // 设置视频路径（URL）
        Uri videoUri = Uri.parse(mUrl);
        videoView.setVideoURI(videoUri);

        // 初始化 MediaController
        mediaController = new MediaController(getContext());
        mediaController.setMediaPlayer(videoView);
        videoView.setMediaController(mediaController);

        // 准备 MediaPlayer
        videoView.setOnPreparedListener(mp -> {
            mediaPlayer = mp;
            // 在准备完成后开始播放视频
            videoView.start();
        });

        // 监听缓冲状态，显示加载动画
        videoView.setOnInfoListener((mp, what, extra) -> {
            if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                // 缓冲开始，显示加载动画
                loadingBar.setVisibility(VISIBLE);
            } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                // 缓冲结束，隐藏加载动画
                loadingBar.setVisibility(GONE);
            }
            return false;
        });
        mainLayout.setOnClickListener(v -> {
            if (videoView.isPlaying()) {
                // 暂停视频播放
                videoView.pause();
                playIv.setVisibility(VISIBLE);
            } else {
                videoView.start();
                playIv.setVisibility(GONE);
            }
        });

        videoView.setOnCompletionListener(mp -> {
            // 视频播放结束
            videoView.pause();
            playIv.setVisibility(VISIBLE);
        });
    }

    @Override
    protected void onStop() {
        if (videoView != null && videoView.isPlaying()) {
            videoView.pause();
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        super.onStop();
    }
}
