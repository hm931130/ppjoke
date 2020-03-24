package com.beantechs.ppjoke.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.beantechs.libcommon.extention.AbsPagedListAdapter;
import com.beantechs.ppjoke.BR;
import com.beantechs.ppjoke.R;
import com.beantechs.ppjoke.databinding.LayoutFeedTypeImageBinding;
import com.beantechs.ppjoke.databinding.LayoutFeedTypeVideoBinding;
import com.beantechs.ppjoke.model.Feed;
import com.beantechs.ppjoke.view.ListPlayerView;

public class FeedAdapter extends AbsPagedListAdapter<Feed, FeedAdapter.ViewHolder> {
    private final LayoutInflater inflater;
    private Context context;
    private String category;

    public FeedAdapter(Context context, String category) {
        super(new DiffUtil.ItemCallback<Feed>() {
            @Override
            public boolean areItemsTheSame(@NonNull Feed oldItem, @NonNull Feed newItem) {
                return oldItem.id == newItem.id;
            }

            @Override
            public boolean areContentsTheSame(@NonNull Feed oldItem, @NonNull Feed newItem) {
                return oldItem.equals(newItem);
            }
        });
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.category = category;
    }

    @Override
    protected int getItemViewType2(int position) {
        Feed feed = getItem(position);
        if (feed.itemType == Feed.TYPE_IMAGE_TEXT) {
            return R.layout.layout_feed_type_image;
        } else if (feed.itemType == Feed.TYPE_VIDEO) {
            return R.layout.layout_feed_type_video;
        }
        return 0;
    }

    @Override
    protected ViewHolder onCreateViewHolder2(ViewGroup parent, int viewType) {
        ViewDataBinding binding = DataBindingUtil.inflate(inflater, viewType, parent, false);
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    protected void onBindViewHolder2(ViewHolder holder, int position) {
        final Feed feed = getItem(position);
        holder.bindData(feed);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private ViewDataBinding binding;
        public ImageView feedImage;
        public ListPlayerView listPlayerView;

        public ViewHolder(@NonNull View itemView, ViewDataBinding binding) {
            super(itemView);
            this.binding = binding;
        }

        public void bindData(Feed feed) {
            //这里之所以手动绑定数据的原因是 图片 和视频区域都是需要计算的
            //而dataBinding的执行默认是延迟一帧的。
            //当列表上下滑动的时候 ，会明显的看到宽高尺寸不对称的问题

            binding.setLifecycleOwner((LifecycleOwner) context);
            binding.setVariable(com.beantechs.ppjoke.BR.feed, feed);

            if (binding instanceof LayoutFeedTypeImageBinding) {
                LayoutFeedTypeImageBinding imageBinding = (LayoutFeedTypeImageBinding) binding;
                feedImage = imageBinding.feedImage;
                imageBinding.feedImage.bindData(feed.width, feed.height, 16, feed.cover);
            } else if (binding instanceof LayoutFeedTypeVideoBinding) {
                LayoutFeedTypeVideoBinding videoBinding = (LayoutFeedTypeVideoBinding) binding;
                listPlayerView = videoBinding.listPlayerView;
                videoBinding.listPlayerView.bindData(category, feed.width, feed.height, feed.cover, feed.url);
            }


        }

        public boolean isVideoItem() {
            return binding instanceof LayoutFeedTypeVideoBinding;
        }

        public ListPlayerView getListPlayerView() {
            return listPlayerView;
        }
    }
}
