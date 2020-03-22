package com.beantechs.ppjoke.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.beantechs.ppjoke.R;
import com.beantechs.ppjoke.model.BottomBar;
import com.beantechs.ppjoke.model.Destination;
import com.beantechs.ppjoke.utils.AppConfig;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;

import java.util.List;

public class AppBottomBar extends BottomNavigationView {
    private static int[] sIcons = new int[]{R.drawable.icon_tab_home, R.drawable.icon_tab_sofa, R.drawable.icon_tab_publish, R.drawable.icon_tab_find, R.drawable.icon_tab_mine};

    public AppBottomBar(@NonNull Context context) {
        this(context, null);
    }

    public AppBottomBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("RestrictedApi")
    public AppBottomBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        BottomBar bottomBar = AppConfig.getBottomBar();
        List<BottomBar.Tab> tabs = bottomBar.getTabs();


        int[][] states = new int[2][];
        states[0] = new int[]{android.R.attr.state_selected};
        states[1] = new int[]{};
        int[] color = new int[]{Color.parseColor(bottomBar.getActiveColor()), Color.parseColor(bottomBar.getInActiveColor())};
        ColorStateList stateList = new ColorStateList(states, color);
        setItemTextColor(stateList);
        setItemIconTintList(stateList);
        setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        for (BottomBar.Tab tab : tabs) {
            if (!tab.isEnable()) {
                continue;
            }
            int itemId = getItemId(tab.getPageUrl());
            if (itemId < 0) {
                continue;
            }
            MenuItem menuItem = getMenu().add(0, itemId, tab.getIndex(), tab.getTitle());
            menuItem.setIcon(sIcons[tab.getIndex()]);


        }
        //设置按钮大小

        int index = 0;
        for (BottomBar.Tab tab : tabs) {
            if (!tab.isEnable()) {
                continue;
            }
            int iconSize = dp2Px(tab.getSize());
            BottomNavigationMenuView menuView = (BottomNavigationMenuView) getChildAt(0);
            BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(index);
            itemView.setIconSize(iconSize);

            if (TextUtils.isEmpty(tab.getTitle())) {
                int tintColor = TextUtils.isEmpty(tab.getTintColor()) ? Color.parseColor("#ff678f") : Color.parseColor(tab.getTintColor());
                itemView.setIconTintList(ColorStateList.valueOf(tintColor));
                itemView.setShifting(false);
            }

            index++;
        }

        //底部导航栏默认选中项
        if (bottomBar.selectTab != 0) {
            BottomBar.Tab selectTab = bottomBar.getTabs().get(bottomBar.selectTab);
            if (selectTab.isEnable()) {
                int itemId = getItemId(selectTab.getPageUrl());
                post(() -> {
                    setSelectedItemId(itemId);
                });
            }
        }

    }

    private int dp2Px(int size) {
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        return (int) (metrics.density * size + 0.5f);

    }

    private int getItemId(String pageUrl) {

        Destination destination = AppConfig.getDestConfig().get(pageUrl);
        if (destination == null) {
            return -1;
        }

        return destination.getId();
    }
}
