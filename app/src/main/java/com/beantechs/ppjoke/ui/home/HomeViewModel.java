package com.beantechs.ppjoke.ui.home;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import androidx.paging.ItemKeyedDataSource;
import androidx.paging.PagedList;

import com.alibaba.fastjson.TypeReference;
import com.beantechs.libnetwork.ApiResponse;
import com.beantechs.libnetwork.ApiService;
import com.beantechs.libnetwork.JsonCallback;
import com.beantechs.libnetwork.Request;
import com.beantechs.ppjoke.model.Feed;
import com.beantechs.ppjoke.ui.AbsViewModel;
import com.beantechs.ppjoke.ui.MutablePageKeyedDataSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class HomeViewModel extends AbsViewModel<Feed> {


    private volatile boolean withCache = false;
    private MutableLiveData<PagedList<Feed>> cacheLiveData = new MutableLiveData<>();
    private AtomicBoolean loadAfter = new AtomicBoolean(false);

    public HomeViewModel() {
    }

    @Override
    public DataSource createDataSource() {
        return new FeedDataSource();
    }


    class FeedDataSource extends ItemKeyedDataSource<Integer, Feed> {

        @Override
        public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Feed> callback) {
            //加载初始化数据的
            Log.e("homeviewmodel", "loadInitial: ");
            loadData(0, params.requestedLoadSize, callback);
        }

        @Override
        public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Feed> callback) {
            //向后加载分页数据的
            Log.e("homeviewmodel", "loadAfter: ");
            loadData(params.key, params.requestedLoadSize, callback);
        }

        @Override
        public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Feed> callback) {
            callback.onResult(Collections.emptyList());
            //能够向前加载数据的
        }

        @NonNull
        @Override
        public Integer getKey(@NonNull Feed item) {
            return item.id;
        }
    }

    private void loadData(int key, int count, ItemKeyedDataSource.LoadCallback<Feed> callback) {
        Request request = ApiService.get("/feeds/queryHotFeedsList")
                .addParam("feedType", "all")
                .addParam("userId", "")
                .addParam("feedId", key)
                .addParam("pageCount", count)
                .responseType(new TypeReference<ArrayList<Feed>>() {
                }.getType());

        if (withCache) {
            request.cacheStrategy(Request.CACHE_ONLY);
            request.execute(new JsonCallback<List<Feed>>() {
                @Override
                public void onCacheSuccess(ApiResponse<List<Feed>> response) {
                    Log.e("loadData", "onCacheSuccess: ");
                    MutablePageKeyedDataSource dataSource = new MutablePageKeyedDataSource<Feed>();
                    dataSource.data.addAll(response.body);

                    PagedList pagedList = dataSource.buildNewPagedList(config);
                    cacheLiveData.postValue(pagedList);

                    //下面的不可取，否则会报
                    // java.lang.IllegalStateException: callback.onResult already called, cannot call again.
                    //if (response.body != null) {
                    //  callback.onResult(response.body);
                    // }
                }
            });
        }

        try {

            Request netRequest = withCache ? request.clone() : request;
            netRequest.cacheStrategy(key == 0 ? Request.NET_CACHE : Request.NET_ONLY);
            netRequest.cacheStrategy(Request.NET_ONLY);
            ApiResponse<List<Feed>> response = netRequest.execute();
            List<Feed> data = response.body == null ? Collections.emptyList() : response.body;
            callback.onResult(data);
            if (key > 0) {
                ((MutableLiveData) getBoundaryPageData()).postValue(data.size() > 0);
                loadAfter.set(false);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public void loadAfter(int id, ItemKeyedDataSource.LoadCallback<Feed> callback) {
        if (loadAfter.get()) {
            callback.onResult(Collections.emptyList());
            return;
        }
        ArchTaskExecutor.getIOThreadExecutor().execute(() -> loadData(id, config.pageSize, callback));
    }

}