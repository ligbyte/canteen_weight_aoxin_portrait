package com.stkj.aoxin.weight.home.ui.adapter;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.stkj.aoxin.weight.home.model.HomeMenuList;
import com.stkj.aoxin.weight.home.model.HomeTabInfo;
import com.stkj.common.ui.adapter.CommonFragmentPageAdapter;
import com.stkj.aoxin.weight.pay.model.ConsumerOrderRefreshEvent;
import com.stkj.aoxin.weight.pay.ui.fragment.OrderFragment;
import com.stkj.aoxin.weight.setting.ui.fragment.TabWeightFragment;
import com.stkj.aoxin.weight.stat.ui.fragment.TabOutBoundFragment;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * 首页全部界面
 */
public class HomeTabPageAdapter extends CommonFragmentPageAdapter {

    public static final String TAB_PAYMENT_TAG = "cashier";
    public static final String TAB_STAT_TAG = "stat";
    public static final String TAB_SETTING_TAG = "set";
    private List<HomeTabInfo<HomeMenuList.Menu>> homeTabInfoList;
    private FragmentActivity fragmentActivity = null;
    public HomeTabPageAdapter(@NonNull FragmentActivity fragmentActivity, List<HomeTabInfo<HomeMenuList.Menu>> tabInfoList) {
        super(fragmentActivity);
        homeTabInfoList = tabInfoList;
        this.fragmentActivity = fragmentActivity;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        HomeTabInfo<HomeMenuList.Menu> homeTabInfo = homeTabInfoList.get(position);
        HomeMenuList.Menu extraInfo = homeTabInfo.getExtraInfo();
        String path = extraInfo.getPath();
        if (TextUtils.equals(path, TAB_PAYMENT_TAG)) {
            return new OrderFragment();
        } else if (TextUtils.equals(path, TAB_STAT_TAG)) {
            EventBus.getDefault().post(new ConsumerOrderRefreshEvent());
            return  new TabOutBoundFragment();
        } else {
            return new TabWeightFragment();
        }
    }

    @Override
    public int getItemCount() {
        return homeTabInfoList == null ? 0 : homeTabInfoList.size();
    }

    public int findTabPageIndex(String path) {
        if (homeTabInfoList != null) {
            int size = homeTabInfoList.size();
            for (int i = 0; i < size; i++) {
                HomeMenuList.Menu extraInfo = homeTabInfoList.get(i).getExtraInfo();
                if (TextUtils.equals(path, extraInfo.getPath())) {
                    return i;
                }
            }
        }
        return -1;
    }
}
