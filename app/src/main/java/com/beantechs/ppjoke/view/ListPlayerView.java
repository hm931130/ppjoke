package com.beantechs.ppjoke.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.beantechs.libcommon.utils.PixUtils;
import com.beantechs.ppjoke.R;
import com.beantechs.ppjoke.exoplayer.IPlayTarget;
import com.beantechs.ppjoke.exoplayer.PageListPlay;
import com.beantechs.ppjoke.exoplayer.PageListPlayManager;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;

/**
 * 视频播放器View
 */
public class ListPlayerView extends FrameLayout implements IPlayTarget, PlayerControlView.VisibilityListener, Player.EventListener {
    public ProgressBar bufferView;
    public CustomImageView blurBackGround, coverView;
    protected ImageView playBtn;
    protected String mVideoUrl;
    private String mCategory;
    protected boolean isPlaying;

    public ListPlayerView(@NonNull Context context) {
        this(context, null);
    }

    public ListPlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ListPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(getContext()).inflate(R.layout.layout_player_view, this, true);
        //缓冲加载view

        bufferView = findViewById(R.id.buffer_view);
        //背景高斯模糊
        blurBackGround = findViewById(R.id.blur_background);
        //封面
        coverView = findViewById(R.id.cover);
        //播放按钮
        playBtn = findViewById(R.id.play_btn);

        playBtn.setOnClickListener(v -> {
            if (isPlaying()) {
                inActive();
            } else {
                onActive();
            }
        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //点击该区域时 我们诸主动让视频控制器显示出来
        PageListPlay pageListPlay = PageListPlayManager.get(mCategory);
        pageListPlay.controlView.show();
        return true;
    }
    public void bindData(String category, int widthPx, int heightPx, String coverUrl, String videoUrl) {
        this.mCategory = category;
        this.mVideoUrl = videoUrl;

        coverView.setImageUrl(coverUrl);
        if (widthPx < heightPx) {
            CustomImageView.setBlurImageUrl(blurBackGround, coverUrl, 10);
            blurBackGround.setVisibility(VISIBLE);
        } else {
            blurBackGround.setVisibility(INVISIBLE);
        }
        setSize(widthPx, heightPx);
    }

    private void setSize(int widthPx, int heightPx) {
        int maxWidth = PixUtils.getScreenWidth();
        int maxHeight = maxWidth;
        int layoutWidth = maxWidth;
        int layoutHeight = 0;
        int coverWidth;
        int coverHeight;

        if (widthPx >= heightPx) {
            coverWidth = maxWidth;
            layoutHeight = coverHeight = (int) (heightPx / (widthPx * 1.0f / maxWidth));
        } else {
            layoutHeight = coverHeight = maxHeight;
            coverWidth = (int) (widthPx / (heightPx * 1.0f / maxHeight));
        }

        //播放父控件
        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = layoutWidth;
        params.height = layoutHeight;
        setLayoutParams(params);

        ViewGroup.LayoutParams blurParams = blurBackGround.getLayoutParams();
        blurParams.width = layoutWidth;
        blurParams.height = layoutHeight;
        blurBackGround.setLayoutParams(blurParams);

        FrameLayout.LayoutParams coverParams = (LayoutParams) coverView.getLayoutParams();
        coverParams.width = coverWidth;
        coverParams.height = coverHeight;
        coverParams.gravity = Gravity.CENTER;
        coverView.setLayoutParams(coverParams);

        FrameLayout.LayoutParams playBtnParams = (LayoutParams) playBtn.getLayoutParams();
        playBtnParams.gravity = Gravity.CENTER;
        playBtn.setLayoutParams(playBtnParams);

    }

    @Override
    public ViewGroup getOwner() {
        return this;
    }

    @Override
    public void onActive() {

        //视频播放,或恢复播放

        //通过该View所在页面的mCategory(比如首页列表tab_all,沙发tab的tab_video,标签帖子聚合的tag_feed) 字段，
        //取出管理该页面的Exoplayer播放器，ExoplayerView播放View,控制器对象PageListPlay
        PageListPlay pageListPlay = PageListPlayManager.get(mCategory);

        PlayerView playerView = pageListPlay.playerView;
        PlayerControlView controlView = pageListPlay.controlView;
        SimpleExoPlayer exoPlayer = pageListPlay.exoPlayer;
        if (playerView == null) {
            return;
        }
        //此处我们需要主动调用一次 switchPlayerView，把播放器Exoplayer和展示视频画面的View ExoplayerView相关联
        //为什么呢？因为在列表页点击视频Item跳转到视频详情页的时候，详情页会复用列表页的播放器Exoplayer，然后和新创建的展示视频画面的View ExoplayerView相关联，达到视频无缝续播的效果
        //如果 我们再次返回列表页，则需要再次把播放器和ExoplayerView相关联
        pageListPlay.switchPlayerView(playerView, true);
        ViewParent parent = playerView.getParent();
        if (parent != this) {
            //把展示视频画面的View添加到ItemView的容器上
            if (parent != null) {
                ((ViewGroup) parent).removeView(playerView);
                //还应该暂停掉列表上正在播放的那个
                ((ListPlayerView) parent).inActive();
            }
            ViewGroup.LayoutParams coverParams = coverView.getLayoutParams();
            this.addView(playerView, 1, coverParams);
        }
        ViewParent ctrlParent = controlView.getParent();


        if (ctrlParent != this) {
            //把视频控制器 添加到ItemView的容器上
            if (ctrlParent != null) {
                ((ViewGroup) ctrlParent).removeView(controlView);
            }
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.BOTTOM;
            this.addView(controlView, params);
        }

        //如果是同一个视频资源,则不需要从重新创建mediaSource。
        //但需要onPlayerStateChanged 否则不会触发onPlayerStateChanged()
        if (TextUtils.equals(pageListPlay.playUrl, mVideoUrl)) {
            onPlayerStateChanged(true, Player.STATE_READY);
        } else {
            MediaSource mediaSource = PageListPlayManager.createMediaSource(mVideoUrl);
            exoPlayer.prepare(mediaSource);
            exoPlayer.setRepeatMode(Player.REPEAT_MODE_ONE);
            pageListPlay.playUrl = mVideoUrl;
        }
        controlView.show();
        controlView.setVisibilityListener(this);
        exoPlayer.addListener(this);
        exoPlayer.setPlayWhenReady(true);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isPlaying = false;
        bufferView.setVisibility(GONE);
        coverView.setVisibility(VISIBLE);
        playBtn.setVisibility(VISIBLE);
        playBtn.setImageResource(R.drawable.icon_video_play);
    }

    @Override
    public void inActive() {
        //暂停视频的播放并让封面图和 开始播放按钮 显示出来
        PageListPlay pageListPlay = PageListPlayManager.get(mCategory);
        if (pageListPlay.exoPlayer == null || pageListPlay.controlView == null || pageListPlay.exoPlayer == null)
            return;
        pageListPlay.exoPlayer.setPlayWhenReady(false);
        pageListPlay.controlView.setVisibilityListener(null);
        pageListPlay.exoPlayer.removeListener(this);
        coverView.setVisibility(VISIBLE);
        playBtn.setVisibility(VISIBLE);
        playBtn.setImageResource(R.drawable.icon_video_play);
    }

    @Override
    public boolean isPlaying() {
        return isPlaying;
    }


    @Override
    public void onVisibilityChange(int visibility) {
        playBtn.setVisibility(visibility);
        playBtn.setImageResource(isPlaying() ? R.drawable.icon_video_pause : R.drawable.icon_video_play);
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        //监听视频播放的状态
        PageListPlay pageListPlay = PageListPlayManager.get(mCategory);
        SimpleExoPlayer exoPlayer = pageListPlay.exoPlayer;
        if (playbackState == Player.STATE_READY && exoPlayer.getBufferedPosition() != 0 && playWhenReady) {
            coverView.setVisibility(GONE);
            bufferView.setVisibility(GONE);
        } else if (playbackState == Player.STATE_BUFFERING) {
            bufferView.setVisibility(VISIBLE);
        }
        isPlaying = playbackState == Player.STATE_READY && exoPlayer.getBufferedPosition() != 0 && playWhenReady;
        playBtn.setImageResource(isPlaying ? R.drawable.icon_video_pause : R.drawable.icon_video_play);
    }
    public View getPlayController() {
        PageListPlay listPlay = PageListPlayManager.get(mCategory);
        return listPlay.controlView;
    }

}
