package cn.xdf.mine_ppjoke.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;

import java.util.List;

import cn.xdf.mine_ppjoke.R;
import cn.xdf.mine_ppjoke.model.BottomBar;
import cn.xdf.mine_ppjoke.model.Destination;
import cn.xdf.mine_ppjoke.utils.AppConfig;
import cn.xdf.mine_ppjoke.utils.AppGlobals;

/**
 * author:fumm
 * Date : 2021/ 03/ 20 4:54 PM
 * Dec : 自定义底部导航栏按钮
 **/
public class AppBottomBar extends BottomNavigationView {
    private Context mContext;
    private static int[] sIcons = new int[]{R.drawable.icon_tab_home, R.drawable.icon_tab_shafa, R.drawable.icon_tab_publish, R.drawable.icon_tab_find, R.drawable.icon_tab_mine};
    private BottomBar mConfig;

    public AppBottomBar(@NonNull Context context) {
        this(context, null);
    }

    public AppBottomBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppBottomBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mConfig = AppConfig.getBottomBar();

        initView();
    }


    @SuppressLint("RestrictedApi")
    public void initView() {
        List<BottomBar.Tab> tabs = mConfig.tabs;

        int[][] state = new int[2][];
        state[0] = new int[]{android.R.attr.state_selected};
        state[1] = new int[]{};

        int[] colors = new int[]{Color.parseColor(mConfig.activeColor), Color.parseColor(mConfig.inActiveColor)};
        ColorStateList stateList = new ColorStateList(state, colors);
        // 设置title 颜色 放大效果
        setItemTextColor(stateList);
        setItemIconTintList(stateList);
        setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        // 设置ICon
        for (BottomBar.Tab tab : tabs) {
            if (!tab.enable) {
                continue;
            }
            int itemId = getItemId(tab.pageUrl);
            if (itemId < 0) {
                continue;
            }

            MenuItem item = getMenu().add(0, itemId, tab.index, tab.title);
            item.setIcon(sIcons[tab.index]);
        }
        // 设置放大效果
        for (BottomBar.Tab tab : tabs) {
            int iconSize = dp2Px(tab.size);
            BottomNavigationMenuView menuView = (BottomNavigationMenuView) getChildAt(0);
            BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(tab.index);
            itemView.setIconSize(iconSize);
            if (TextUtils.isEmpty(tab.title)) {
                int tintColor = TextUtils.isEmpty(tab.tintColor) ? Color.parseColor("#ff678f")
                        : Color.parseColor(tab.tintColor);

                itemView.setIconTintList(ColorStateList.valueOf(tintColor));
                // 点击上下浮动效果
                itemView.setShifting(false);
            }
        }
    }

    private static int dp2Px(int dpvalue) {
        if (AppGlobals.getApplication() == null) {
            return 0;
        }
        DisplayMetrics metrics = AppGlobals.getApplication().getResources().getDisplayMetrics();
        return (int) (metrics.density * dpvalue + 0.5f);
    }


    private int getItemId(String pageUrl) {
        Destination destination = AppConfig.getDestConfig().get(pageUrl);
        if (destination == null) {
            return -1;
        }
        return destination.id;
    }


}
