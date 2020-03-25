package com.beantechs.ppjoke.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.ItemKeyedDataSource;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;

import com.beantechs.libnavannotation.FragmentDestination;
import com.beantechs.ppjoke.R;
import com.beantechs.ppjoke.model.Feed;
import com.beantechs.ppjoke.ui.AbsListFragment;
import com.beantechs.ppjoke.ui.MutablePageKeyedDataSource;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.List;

@FragmentDestination(pageUrl = "main/tabs/home", asStarter = true)
public class HomeFragment extends AbsListFragment<Feed, HomeViewModel> {



    @Override
    protected PagedListAdapter getAdapter() {
        return new FeedAdapter(getContext(),"all"){

        };
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        final PagedList<Feed> currentList = adapter.getCurrentList();
        if (currentList == null || currentList.size() <= 0) {
            finishRefresh(false);
            return;
        }

        Feed feed = currentList.get(adapter.getItemCount() - 1);
        mViewModel.loadAfter(feed.id, new ItemKeyedDataSource.LoadCallback<Feed>() {
            @Override
            public void onResult(@NonNull List<Feed> data) {
                PagedList.Config config = currentList.getConfig();
                if (data != null && data.size() > 0) {
                    //这里 咱们手动接管 分页数据加载的时候 使用MutableItemKeyedDataSource也是可以的。
                    //由于当且仅当 paging不再帮我们分页的时候，我们才会接管。所以 就不需要ViewModel中创建的DataSource继续工作了，所以使用
                    //MutablePageKeyedDataSource也是可以的
                    MutablePageKeyedDataSource dataSource = new MutablePageKeyedDataSource();

                    //这里要把列表上已经显示的先添加到dataSource.data中
                    //而后把本次分页回来的数据再添加到dataSource.data中
                    dataSource.data.addAll(currentList);
                    dataSource.data.addAll(data);
                    PagedList pagedList = dataSource.buildNewPagedList(config);
                    submitList(pagedList);
                }
            }
        });

    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        //invalidate 之后Paging会重新创建一个DataSource 重新调用它的loadInitial方法加载初始化数据
        //详情见：LivePagedListBuilder#compute方法
        mViewModel.getDataSource().invalidate();
    }
}