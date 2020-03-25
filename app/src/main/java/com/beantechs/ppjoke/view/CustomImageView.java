package com.beantechs.ppjoke.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.databinding.BindingAdapter;

import com.beantechs.libcommon.utils.PixUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.ViewTarget;
import com.bumptech.glide.request.transition.Transition;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class CustomImageView extends AppCompatImageView {
    public CustomImageView(Context context) {
        super(context);
    }

    public CustomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setImageUrl(String imageUrl) {
        setImageUrl(this, imageUrl, false);
    }

    @BindingAdapter(value = {"image_url", "isCircle"}, requireAll = false)
    public static void setImageUrl(CustomImageView iamgeView, String imageUrl, boolean isCircle) {
        iamgeView.setImageUrl(iamgeView, imageUrl, isCircle, 0);
    }

    @BindingAdapter(value = {"image_url", "isCircle", "radius"}, requireAll = false)
    public static void setImageUrl(CustomImageView imageView, String imageUrl, boolean isCircle, int radius) {
        RequestBuilder<Drawable> builder = Glide.with(imageView).load(imageUrl);
        if (isCircle) {
            builder.transform(new CircleCrop());
        } else if (radius > 0) {
            builder.transform(new RoundedCornersTransformation(PixUtils.dp2px(radius), 0));
        }
        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        if (layoutParams != null && layoutParams.width > 0 && layoutParams.height > 0) {
            builder.override(layoutParams.width, layoutParams.height);
        }
        builder.into(imageView);
    }


    public void bindData(int widthPx, int heightPx, int marginLeft, String imageUrl) {
        bindData(widthPx, heightPx, PixUtils.getScreenWidth(), PixUtils.getScreenWidth(), marginLeft, imageUrl);
    }

    public void bindData(int widthPx, int heightPx, int maxWidth, int maxHeight, int leftMargin, String imageUrl) {
        if (TextUtils.isEmpty(imageUrl)) {
            setVisibility(GONE);
            return;
        } else {
            setVisibility(VISIBLE);
        }
        if (widthPx <= 0 || heightPx <= 0) {
            Glide.with(this).load(imageUrl).into(new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                    int width = resource.getIntrinsicWidth();
                    int height = resource.getIntrinsicHeight();
                    setSize(width, height, maxWidth, maxHeight, leftMargin);
                    setImageDrawable(resource);
                }
            });
            return;
        }
        setSize(widthPx, heightPx, maxWidth, maxHeight, leftMargin);
        setImageUrl(this, imageUrl, false);
    }

    private void setSize(int width, int height, int maxWidth, int maxHeight, int leftMargin) {
        int finalWidth;
        int finalHeight;
        if (width > height) {
            finalWidth = maxWidth;
            finalHeight = (int) (height / (width * 1.0f / finalWidth));
        } else {
            finalHeight = maxHeight;
            finalWidth = (int) (width / (height * 1.0f / finalHeight));
        }

        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.width = finalWidth;
        layoutParams.height = finalHeight;
        if (layoutParams instanceof FrameLayout.LayoutParams) {
            ((FrameLayout.LayoutParams) layoutParams).leftMargin = height > width ? PixUtils.dp2px(leftMargin) : 0;
        } else if (layoutParams instanceof LinearLayout.LayoutParams) {
            ((LinearLayout.LayoutParams) layoutParams).leftMargin = height > width ? PixUtils.dp2px(leftMargin) : 0;
        }
        setLayoutParams(layoutParams);
    }

    @BindingAdapter(value = {"blur_url", "radius"})
    public static void setBlurImageUrl(ImageView imageView, String blurUrl, int radius) {
        Glide.with(imageView).load(blurUrl).override(radius)
                .transform(new BlurTransformation())
                .dontAnimate()
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        imageView.setBackground(resource);
                    }
                });
    }
}
