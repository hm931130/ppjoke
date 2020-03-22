package com.beantechs.ppjoke.utils;

import android.content.ComponentName;

import androidx.fragment.app.FragmentActivity;
import androidx.navigation.ActivityNavigator;
import androidx.navigation.NavController;
import androidx.navigation.NavGraphNavigator;
import androidx.navigation.NavigatorProvider;
import androidx.navigation.fragment.FragmentNavigator;

import com.beantechs.libcommon.AppGlobals;
import com.beantechs.ppjoke.FixFragmentNavigator;
import com.beantechs.ppjoke.model.Destination;

import java.util.HashMap;

public class NavGraphBuilder {

    public static void build(NavController controller, FragmentActivity activity, int containerId) {
        NavigatorProvider provider = controller.getNavigatorProvider();
//        FragmentNavigator fragmentNavigator = provider.getNavigator(FragmentNavigator.class);
        FixFragmentNavigator fragmentNavigator = new FixFragmentNavigator(activity, activity.getSupportFragmentManager(), containerId);
        provider.addNavigator(fragmentNavigator);

        ActivityNavigator activityNavigator = provider.getNavigator(ActivityNavigator.class);
        androidx.navigation.NavGraph navGraph = new androidx.navigation.NavGraph(new NavGraphNavigator(provider));
        HashMap<String, Destination> mDestMap = AppConfig.getDestConfig();
        for (Destination destination : mDestMap.values()) {
            if (destination.isIsFragment()) {
                FragmentNavigator.Destination fragmentDestination = fragmentNavigator.createDestination();
                fragmentDestination.setId(destination.getId());
                fragmentDestination.setClassName(destination.getClazName());
                fragmentDestination.addDeepLink(destination.getPageUrl());
                navGraph.addDestination(fragmentDestination);
            } else {
                ActivityNavigator.Destination activityDestination = activityNavigator.createDestination();
                activityDestination.setId(destination.getId());
                activityDestination.setComponentName(new ComponentName(AppGlobals.getApplication(), destination.getClazName()));
                activityDestination.addDeepLink(destination.getPageUrl());
                navGraph.addDestination(activityDestination);
            }
            if (destination.isAsStarter()) {
                navGraph.setStartDestination(destination.getId());
            }
        }
        controller.setGraph(navGraph);

    }
}
