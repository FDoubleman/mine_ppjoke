package cn.xdf.mine_ppjoke.utils;

import android.content.ComponentName;

import androidx.fragment.app.FragmentActivity;
import androidx.navigation.ActivityNavigator;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavGraphNavigator;
import androidx.navigation.NavigatorProvider;
import androidx.navigation.fragment.FragmentNavigator;

import java.util.HashMap;
import java.util.Iterator;

import cn.xdf.libcommon.AppGlobals;
import cn.xdf.mine_ppjoke.model.Destination;
import cn.xdf.mine_ppjoke.ui.FixFragmentNavigator;

/**
 * author:fumm
 * Date : 2021/ 03/ 19 11:47 AM
 * Dec : TODO
 **/
public class NavGraphBuilder {


    public static void build(NavController controller,FragmentActivity activity,int containId) {
        NavigatorProvider provider = controller.getNavigatorProvider();
        NavGraph navGraph = new NavGraph(new NavGraphNavigator(provider));

        // FragmentNavigator fragmentNavigator = provider.getNavigator(FragmentNavigator.class);

        FixFragmentNavigator fragmentNavigator = new FixFragmentNavigator(activity,activity.getSupportFragmentManager(),containId);
        provider.addNavigator(fragmentNavigator);

        ActivityNavigator activityNavigator = provider.getNavigator(ActivityNavigator.class);

        HashMap<String, Destination> destConfig = AppConfig.getDestConfig();
        Iterator<Destination> iterator = destConfig.values().iterator();
        while (iterator.hasNext()) {
            Destination node = iterator.next();
            if (node.isFragment) {
                FragmentNavigator.Destination destination = fragmentNavigator.createDestination();
                destination.setClassName(node.className);
                destination.setId(node.id);
                destination.addDeepLink(node.pageUrl);
                navGraph.addDestination(destination);
            } else {
                ActivityNavigator.Destination destination = activityNavigator.createDestination();
                destination.setId(node.id);
                destination.setComponentName(new ComponentName(AppGlobals.getApplication().getPackageName(), node.className));
                destination.addDeepLink(node.pageUrl);
                navGraph.addDestination(destination);
            }
            if (node.asStarter) {
                navGraph.setStartDestination(node.id);
            }
        }
        controller.setGraph(navGraph);
    }
}
