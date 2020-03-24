package com.beantechs.ppjoke.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.beantechs.libcommon.utils.PixUtils;
import com.beantechs.ppjoke.R;

/**
 * 视频播放器View
 */
public class ListPlayerView extends FrameLayout {
    public ProgressBar bufferView;
    public CustomImageView blurBackGround, coverView;
    protected ImageView playBtn;
    private String category;
    private String videoUrl;


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

    }


    public void bindData(String category, int widthPx, int heightPx, String coverUrl, String videoUrl) {
        this.category = category;
        this.videoUrl = videoUrl;

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

}
