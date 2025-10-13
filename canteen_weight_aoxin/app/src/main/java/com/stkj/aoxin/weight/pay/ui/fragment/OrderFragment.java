package com.stkj.aoxin.weight.pay.ui.fragment;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.stkj.aoxin.weight.R;
import com.stkj.aoxin.weight.base.utils.EventBusUtils;
import com.stkj.aoxin.weight.pay.callback.OnConsumerModeListener;
import com.stkj.aoxin.weight.pay.model.OrderCheckSuccesskEvent;
import com.stkj.aoxin.weight.pay.model.RefreshConsumerGoodsModeEvent;
import com.stkj.common.ui.fragment.BaseRecyclerFragment;
import com.stkj.aoxin.weight.pay.model.TabEntity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * 订单页面
 */
public class OrderFragment extends BaseRecyclerFragment implements OnConsumerModeListener {

    private final String[] mTitles = {"待签收", "已签收"};

    private CommonTabLayout tablayout;
    private ViewPager2 view_pager;
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private MyPagerAdapter mAdapter;
    public static int[] TAB_ICONS_SELECTED = {R.mipmap.tab_home_selected, R.mipmap.tab_userprofile_selected};
    public static int[] TAB_ICONS_NORMAL = {R.mipmap.tab_home_normal, R.mipmap.tab_userprofile_normal};

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_order;
    }


    @Override
    protected void initViews(View rootView) {
        tablayout = (CommonTabLayout) findViewById(R.id.tablayout);
        view_pager = (ViewPager2) findViewById(R.id.view_pager);

        mFragments.add(new OrderHandleFragment());
        mFragments.add(new OrderCompleteFragment());

        mAdapter = new MyPagerAdapter(OrderFragment.this);
        view_pager.setAdapter(mAdapter);
        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], TAB_ICONS_SELECTED[i], TAB_ICONS_NORMAL[i]));
        }

        tablayout.setTabData(mTabEntities);

        tablayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                view_pager.setCurrentItem(position);
                EventBus.getDefault().post(new OrderCheckSuccesskEvent());
            }

            @Override
            public void onTabReselect(int position) {

            }
        });


        view_pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tablayout.setCurrentTab(position);
            }
        });

        view_pager.setOffscreenPageLimit(2);
    }

    @Override
    protected void onFragmentResume(boolean isFirstOnResume) {
        EventBusUtils.registerEventBus(this);



    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshConsumerGoodsModeEvent(RefreshConsumerGoodsModeEvent eventBus) {
//        if (eventBus.getPageMode() == 1){
//            FragmentUtils.safeReplaceFragment(getChildFragmentManager(), new AddGoodsFragment(), R.id.fl_pay_second_content);
//        } else {
//            FragmentUtils.safeReplaceFragment(getChildFragmentManager(), new GoodsConsumerFragment(), R.id.fl_pay_second_content);
//        }
    }


    @Override
    public void onChangeConsumerMode(int consumerMode, int lastConsumerMode) {
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBusUtils.unRegisterEventBus(this);
    }


    private class MyPagerAdapter extends FragmentStateAdapter {

        public MyPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        public MyPagerAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        public MyPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getItemCount() {
            return mFragments.size();
        }
    }
}
