package com.stkj.aoxin.weight.home.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stkj.aoxin.weight.BuildConfig;
import com.stkj.aoxin.weight.R;
import com.stkj.aoxin.weight.base.device.DeviceManager;
import com.stkj.aoxin.weight.base.ui.dialog.BindingPwdAlertDialogFragment;
import com.stkj.aoxin.weight.home.callback.OnGetStoreInfoListener;
import com.stkj.aoxin.weight.home.helper.SystemEventWatcherHelper;
import com.stkj.aoxin.weight.home.model.StoreInfo;
import com.stkj.aoxin.weight.pay.callback.OnConsumerModeListener;
import com.stkj.aoxin.weight.pay.data.PayConstants;
import com.stkj.aoxin.weight.pay.helper.ConsumerModeHelper;
import com.stkj.aoxin.weight.setting.helper.StoreInfoHelper;
import com.stkj.common.core.ActivityHolderFactory;
import com.stkj.common.core.AppManager;
import com.stkj.common.log.LogHelper;
import com.stkj.common.ui.widget.shapelayout.ShapeTextView;
import com.stkj.common.utils.IntentUtils;
import com.stkj.common.utils.NetworkUtils;

/**
 * 首页顶部title
 */
public class BindingHomeTitleLayout extends FrameLayout implements SystemEventWatcherHelper.OnSystemEventListener, OnGetStoreInfoListener, OnConsumerModeListener {

    private ImageView ivLogoIcon;
    private TextView tvStoreName;
    private TextView tvStoreId;
    private ImageView ivSysWifi;
    private ImageView ivSysSettings;
    private TextView tvSysTime;
    private TextView tvSysMonthWeek;
    private TextView tv_canteen_name;
    private TextView tvNetDelayTime;
    private ShapeTextView stvConsumerMode;
    private Context context;
    private BindingPwdAlertDialogFragment bindingPwdAlertDialogFragment;

    public BindingHomeTitleLayout(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public BindingHomeTitleLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public BindingHomeTitleLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        int consumeLayRes = 0;
        boolean mIsLightMode = false;
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HomeTitleLayout);
            mIsLightMode = a.getBoolean(R.styleable.HomeTitleLayout_isLightMode, false);
            consumeLayRes = a.getInteger(R.styleable.HomeTitleLayout_htl_consume_lay_res, 0);
        }
        LayoutInflater.from(context).inflate(R.layout.include_home_title_binding, this);

        ivLogoIcon = (ImageView) findViewById(R.id.iv_super_market_icon);
        tvStoreName = (TextView) findViewById(R.id.tv_store_name);
        tvStoreId = (TextView) findViewById(R.id.tv_store_id);
        tvStoreId.setText("序列号: " + DeviceManager.INSTANCE.getDeviceInterface().getMachineNumber());
        ivSysWifi = (ImageView) findViewById(R.id.iv_sys_wifi);
        ivSysSettings = (ImageView) findViewById(R.id.iv_sys_settings);
        tvSysTime = (TextView) findViewById(R.id.tv_sys_time);
        tvSysMonthWeek = (TextView) findViewById(R.id.tv_sys_month_week);
        tvNetDelayTime = (TextView) findViewById(R.id.tv_net_delay_time);
        stvConsumerMode = (ShapeTextView) findViewById(R.id.stv_consumer_mode);
        tv_canteen_name = (TextView) findViewById(R.id.tv_canteen_name);
        LogHelper.print("--HomeTitleLayout-getContext-" + getContext());
        if (mIsLightMode) {
            setLightMode(true);
        } else {
            Activity mainActivity = AppManager.INSTANCE.getMainActivity();
            if (mainActivity != null) {
                ConsumerModeHelper consumerModeHelper = ActivityHolderFactory.get(ConsumerModeHelper.class, mainActivity);
                if (consumerModeHelper != null) {
                    int currentConsumerMode = consumerModeHelper.getCurrentConsumerMode();
                    stvConsumerMode.setText(PayConstants.getConsumerModeStr(currentConsumerMode));
                    if (BuildConfig.DEBUG) {
                        stvConsumerMode.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                consumerModeHelper.showSelectConsumerModeDialog();
                            }
                        });
                    }
                }
            }
            ivSysWifi.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppManager.INSTANCE.getApplication().startActivity(IntentUtils.getToNetworkSetting());
                }
            });
        }

        ivSysSettings.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //EventBus.getDefault().post(new BindFragmentSwitchEvent(1));
                if (bindingPwdAlertDialogFragment != null){
                    bindingPwdAlertDialogFragment.dismiss();
                }
                bindingPwdAlertDialogFragment = BindingPwdAlertDialogFragment.build()
                        .setAlertTitleTxt("管理员密码")
                        .setRightNavTxt("取消")
                        .setLeftNavClickListener(new BindingPwdAlertDialogFragment.OnSweetClickListener() {
                            @Override
                            public void onClick(BindingPwdAlertDialogFragment alertDialogFragment) {

                            }
                        });
                bindingPwdAlertDialogFragment.show(getContext());
            }
        });
        refreshDate();
        refreshWifiLay();
    }

    private void setLightMode(boolean isLightMode) {
        Resources resources = getResources();
        int textColor;
        if (isLightMode) {
            textColor = resources.getColor(R.color.white);
            ivLogoIcon.setVisibility(VISIBLE);
            tvStoreName.setTextColor(textColor);
            tvStoreId.setTextColor(textColor);
            tvSysTime.setTextColor(textColor);
            tvSysMonthWeek.setTextColor(textColor);
            ivSysWifi.setImageResource(R.mipmap.icon_wifi_light);
            tvNetDelayTime.setVisibility(GONE);
            stvConsumerMode.setVisibility(GONE);
        } else {
            textColor = resources.getColor(com.stkj.common.R.color.color_333333);
            ivLogoIcon.setVisibility(GONE);
            tvStoreName.setTextColor(textColor);
            tvStoreId.setTextColor(textColor);
            tvSysTime.setTextColor(textColor);
            tvSysMonthWeek.setTextColor(textColor);
            ivSysWifi.setImageResource(R.mipmap.icon_wifi_level3);
            tvNetDelayTime.setVisibility(VISIBLE);
            stvConsumerMode.setVisibility(VISIBLE);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Activity mainActivity = AppManager.INSTANCE.getMainActivity();
        if (mainActivity != null) {
            SystemEventWatcherHelper systemEventWatcherHelper = ActivityHolderFactory.get(SystemEventWatcherHelper.class, mainActivity);
            if (systemEventWatcherHelper != null) {
                systemEventWatcherHelper.removeSystemEventListener(this);
            }
            StoreInfoHelper storeInfoHelper = ActivityHolderFactory.get(StoreInfoHelper.class, mainActivity);
            if (storeInfoHelper != null) {
                storeInfoHelper.removeGetStoreInfoListener(this);
            }
            ConsumerModeHelper consumerModeHelper = ActivityHolderFactory.get(ConsumerModeHelper.class, mainActivity);
            if (consumerModeHelper != null) {
                consumerModeHelper.removeConsumerModeListener(this);
            }
        }
    }

    private void refreshDate() {
        try {
            String formatDate = DateFormat.format("yyyy-MM-dd HH:mm", System.currentTimeMillis()).toString();
            String[] split = formatDate.split(" ");
//            tvSysMonthWeek.setText(split[0] + "\n" + split[1]);
            tvSysTime.setText(formatDate);
            LogHelper.print("---HomeTitleLayout----refreshDate: " + formatDate);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDateTick() {
        refreshDate();
    }

    @Override
    public void onDateChange() {
        refreshDate();
    }

    @Override
    public void onNetworkAvailable() {
        LogHelper.print("--HomeTitleLayout--onNetworkAvailable");
        //获取当wifi信号强度
        refreshWifiLay();
    }

    @Override
    public void onNetworkLost() {
        LogHelper.print("--HomeTitleLayout--onNetworkLost");
        ivSysWifi.setImageResource(R.mipmap.icon_wifi_no);
        tvNetDelayTime.setTextColor(0xff999999);
        tvNetDelayTime.setText("网络已断开");
    }

    @Override
    public void onNetworkUnavailable() {
        LogHelper.print("--HomeTitleLayout--onNetworkUnavailable");
        ivSysWifi.setImageResource(R.mipmap.icon_wifi_no);
        tvNetDelayTime.setTextColor(0xff999999);
        tvNetDelayTime.setText("网络已断开");
    }

    @Override
    public void onNetworkRssiChange(int level, long delayTime) {
        LogHelper.print("--HomeTitleLayout--onNetworkRssiChange level: " + level);
        switch (level) {
            case 0:
                tvNetDelayTime.setTextColor(0xffFF3030);
                ivSysWifi.setImageResource(R.mipmap.icon_wifi_level0);
                break;
            case 1:
                tvNetDelayTime.setTextColor(0xffFF3030);
                ivSysWifi.setImageResource(R.mipmap.icon_wifi_level1);
                break;
            case 2:
                tvNetDelayTime.setTextColor(0xffEE9A00);
                ivSysWifi.setImageResource(R.mipmap.icon_wifi_level2);
                break;
            case 3:
            default:
                tvNetDelayTime.setTextColor(0xff00EE76);
                ivSysWifi.setImageResource(R.mipmap.icon_wifi_level3);
                break;
        }
        if (delayTime <= 0) {
            tvNetDelayTime.setText("");
        } else {
            tvNetDelayTime.setText(SystemEventWatcherHelper.getNetworkLevelTips(level));
        }
    }

    @Override
    public void onGetStoreInfo(StoreInfo storeInfo) {
        tvStoreName.setText(storeInfo.getDeviceName());
    }

    private void refreshWifiLay() {
        if (NetworkUtils.isConnected()) {
//            //有网络
//            boolean wifiConnected = NetworkUtils.isWifiConnected();
//            if (wifiConnected) {
//                int wifiNetworkRSSI = NetworkUtils.getWifiNetworkRSSILevel(4);
//                onNetworkRssiChange(wifiNetworkRSSI);
//            } else {
//                //获取移动信号强度等待回调
//                onNetworkRssiChange(4);
//            }
            onNetworkRssiChange(4, 0);
        } else {
            //无网络
            onNetworkUnavailable();
        }
    }


    public TextView getTv_canteen_name() {
        return tv_canteen_name;
    }

    @Override
    public void onChangeConsumerMode(int consumerMode, int lastConsumerMode) {
        if (stvConsumerMode != null) {
            stvConsumerMode.setText(PayConstants.getConsumerModeStr(consumerMode));
        }
    }
}
