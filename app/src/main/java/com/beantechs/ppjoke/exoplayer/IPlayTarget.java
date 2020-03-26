package com.beantechs.ppjoke.exoplayer;


import android.view.ViewGroup;

public interface IPlayTarget {

    ViewGroup getOwner();

    //活跃状态视频可播
    void onActive();

    //非活跃状态、暂停
    void inActive();

    //是否正在播放
    boolean isPlaying();


}
