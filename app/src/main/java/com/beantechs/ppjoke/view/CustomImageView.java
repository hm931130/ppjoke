package com.beantechs.ppjoke.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.databinding.BindingAdapter;

import com.beantechs.libcommon.utils.PixUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.target.ViewTarget;

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

    @BindingAdapter(value = {"image_url","isCircle"},requireAll = false)
    public void setImageUrl(CustomImageView iamgeView,String imageUrl,boolean isCircle){
        iamgeView.setImageUrl(iamgeView, imageUrl, isCircle, 0);
    }

    @BindingAdapter(value = {"image_url", "isCircle"}, requireAll = false)
    public void setImageUrl(CustomImageView imageView, String imageUrl, boolean isCircle, int radius) {
        RequestBuilder<Drawable> builder = Glide.with(getContext()).load(imageUrl);
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
}
