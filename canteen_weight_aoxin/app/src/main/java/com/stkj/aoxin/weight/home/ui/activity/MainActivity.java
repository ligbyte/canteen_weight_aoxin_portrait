package com.stkj.aoxin.weight.home.ui.activity;


import static com.stkj.aoxin.weight.home.helper.SystemEventHelper2.WIFI_NET_TYPE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.icu.util.Calendar;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.alibaba.fastjson.JSON;
import com.lztek.toolkit.Lztek;
import com.stkj.aoxin.weight.base.utils.CommonDialogUtils;
import com.stkj.aoxin.weight.home.helper.HeartBeatHelper;
import com.stkj.aoxin.weight.home.model.StoreInfo;
import com.stkj.aoxin.weight.login.helper.LoginHelper;
import com.stkj.aoxin.weight.pay.model.InitWeightEvent;
import com.stkj.aoxin.weight.setting.helper.StoreInfoHelper;
import com.stkj.common.core.AppManager;
import com.stkj.common.core.CountDownHelper;
import com.stkj.common.download.DownloadFileInfo;
import com.stkj.common.glide.GlideApp;
import com.stkj.common.log.LogHelper;
import com.stkj.common.net.retrofit.RetrofitManager;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;
import com.stkj.common.ui.activity.BaseActivity;
import com.stkj.common.ui.toast.AppToast;
import com.stkj.common.ui.widget.common.CircleImageView;
import com.stkj.common.utils.ActivityUtils;
import com.stkj.common.utils.AndroidUtils;
import com.stkj.common.utils.CollectUtils;
import com.stkj.common.utils.FileUtils;
import com.stkj.common.utils.KeyBoardUtils;
import com.stkj.common.utils.NetworkUtils;
import com.stkj.aoxin.weight.BuildConfig;
import com.stkj.aoxin.weight.AppApplication;
import com.stkj.aoxin.weight.R;
import com.stkj.aoxin.weight.base.callback.AppNetCallback;
import com.stkj.aoxin.weight.base.device.DeviceManager;
import com.stkj.aoxin.weight.base.greendao.AppGreenDaoOpenHelper;
import com.stkj.aoxin.weight.base.greendao.GreenDBConstants;
import com.stkj.aoxin.weight.base.greendao.generate.DaoMaster;
import com.stkj.aoxin.weight.base.greendao.generate.DaoSession;
import com.stkj.aoxin.weight.base.greendao.generate.FoodInfoTableDao;
import com.stkj.aoxin.weight.base.model.BaseNetResponse;
import com.stkj.aoxin.weight.base.net.AppNetManager;
import com.stkj.aoxin.weight.base.net.ParamsUtils;
import com.stkj.aoxin.weight.base.tts.TTSVoiceHelper;
import com.stkj.aoxin.weight.base.ui.dialog.CommonAlertDialogFragment;
import com.stkj.aoxin.weight.base.ui.dialog.CommonInputDialogFragment;
import com.stkj.aoxin.weight.base.utils.PriceUtils;
import com.stkj.aoxin.weight.consumer.ConsumerManager;
import com.stkj.aoxin.weight.consumer.callback.ConsumerListener;
import com.stkj.aoxin.weight.home.helper.CBGCameraHelper;
import com.stkj.aoxin.weight.home.helper.ScreenProtectHelper;
import com.stkj.aoxin.weight.home.helper.SystemEventWatcherHelper;
import com.stkj.aoxin.weight.home.model.HomeMenuList;
import com.stkj.aoxin.weight.home.model.HomeTabInfo;
import com.stkj.aoxin.weight.home.ui.adapter.HomeTabPageAdapter;
import com.stkj.aoxin.weight.home.ui.widget.HomeTabLayout;
import com.stkj.aoxin.weight.home.ui.widget.HomeTitleLayout;
import com.stkj.aoxin.weight.machine.model.AddFoodPreBean;
import com.stkj.aoxin.weight.machine.model.AddOrderFoodResult;
import com.stkj.aoxin.weight.machine.model.DeviceFoodConsumeParam;
import com.stkj.aoxin.weight.machine.service.MachineService;
import com.stkj.aoxin.weight.machine.utils.ToastUtils;
import com.stkj.aoxin.weight.pay.model.BindFragmentBackEvent;
import com.stkj.aoxin.weight.pay.model.BindFragmentSwitchEvent;
import com.stkj.aoxin.weight.pay.model.TTSSpeakEvent;
import com.stkj.aoxin.weight.setting.data.ServerSettingMMKV;
import com.stkj.aoxin.weight.setting.helper.AppUpgradeHelper;
import com.stkj.aoxin.weight.setting.model.FoodInfoTable;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.QueryBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.TreeMap;

import io.reactivex.rxjava3.schedulers.Schedulers;



public class MainActivity extends BaseActivity implements AppNetCallback, ConsumerListener,SystemEventWatcherHelper.OnSystemEventListener,AppUpgradeHelper.OnAppUpgradeListener {

    public final static String TAG = "MainActivity";
    //当前TAB界面
    private static final String TAB_CURRENT_PAGE = "currentTabPage";
    private View scanHolderView;
    private ViewPager2 vp2Content;
    private HomeTabPageAdapter homeTabPageAdapter;
    private FoodInfoTable foodInfoTable;
    private HomeTabLayout htlLeftNav;

    private TextView tvUsername;
    private ImageView ivSysWifi;

    private TextView tvNetDelayTime;

    private CircleImageView iv_icon;

    //是否需要重新恢复消费者页面
    private boolean needRestartConsumer;
    //是否初始化了菜单数据
    private boolean hasInitMenuData;
    private boolean isSoftKeyboardShow;
    private int saveStateCurrentTabPage;
    private long lastBackClickTime = 0;
    private CBGCameraHelper cbgCameraHelper;
    private String currentTrayNo;
    public static String queryPricingMethod =  "2";
    private FoodInfoTableDao foodInfoTableDao;
    private DaoSession daoSession;
    private List<FoodInfoTable>  foods;
    private String customerId = "";
    private final double START_WEIGHT = 0;
    public String plateCode = "";
    private double orderPrice = 0.0;
    private double balance = 0.0;
    private String currentWeight;
    private int weightStatus;
    private double weight;
    private long beforeWeightTime = 0;
    private long beforeBalanceTime = 0;
    private long beforeMaxWeightTime = 0;

    private double beforeWeight = -10000;
    private double maxWeight = 0;
    private boolean trakCardStatus;
    private boolean hasAddOrderFood = false;
    private boolean canHandleQRData = true;


    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        //检查更新状态
        AppUpgradeHelper appUpgradeHelper = getWeakRefHolder(AppUpgradeHelper.class);
        appUpgradeHelper.setOnAppUpgradeListener(this);
//        Log.d(TAG, "limeMD5Utils: " + MD5Utils.encrypt("ly0379"));

//        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
//
//        Runnable task = new Runnable() {
//            private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//            @Override
//            public void run() {
//                String currentTime = sdf.format(new Date());
//            }
//        };
//        scheduler.scheduleAtFixedRate(task, 0, 60, TimeUnit.SECONDS);

        //异步处理人脸识别照片缓存目录
        Schedulers.io().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                try {
//                    Log.d(TAG, "limeFilePath onCreate: " + FileUtils.getFaceCachePath());
//                    FileUtils.putKeyFaceCachePathsValue("");
//                    FileUtils.createDir(new File(FileUtils.getFaceCachePathParent() + "20250201"));
//                    FileUtils.createDir(new File(FileUtils.getFaceCachePathParent() + "20250202"));
//                    FileUtils.createDir(new File(FileUtils.getFaceCachePathParent() + "20250203"));
//                    FileUtils.createDir(new File(FileUtils.getFaceCachePathParent() + "20250204"));

                    FileUtils.createDir(new File(FileUtils.getFaceCachePath()));
                    FileUtils.clearFaceCache(FileUtils.getKeyFaceCachePathsValue());
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });



        // 避免从桌面启动程序后，会重新实例化入口类的activity
        // 判断当前activity是不是所在任务栈的根
        Intent intent = getIntent();
        if (intent != null) {
            String action = intent.getAction();
            //1.避免从桌面启动程序后，会重新实例化入口类的activity , 判断当前activity是不是所在任务栈的根
            if (!isTaskRoot()) {
                if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) {
                    finish();
                    return;
                }
            }
            //2.经过路由跳转的，判断当前应用是否已经初始化过，首页是否存在并且未销毁
            if (Intent.ACTION_VIEW.equals(action)) {
                Activity homeActivity = AppManager.INSTANCE.getMainActivity();
                if (!ActivityUtils.isActivityFinished(homeActivity)) {
                    finish();
                    return;
                }
            }
        }
        AppManager.INSTANCE.setMainActivity(this);
        readSaveInstanceState(savedInstanceState);
        setContentView(com.stkj.aoxin.weight.R.layout.activity_main);

        findViews();
        initApp();
        LogHelper.print("-main--getDisplayMetrics--" + getResources().getDisplayMetrics());

        checkCameraPermission();


    }


    /**
     * 检查并申请相机权限
     */
    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    1001);
        }
    }
    private void initFoodInfoTableDao() {

        AppGreenDaoOpenHelper daoOpenHelper = new AppGreenDaoOpenHelper(AppManager.INSTANCE.getApplication(), GreenDBConstants.FACE_DB_NAME, null);
        Database database = daoOpenHelper.getWritableDb();
        DaoMaster daoMaster = new DaoMaster(database);
        daoSession = daoMaster.newSession();
        foodInfoTableDao = daoSession.getFoodInfoTableDao();

    }

    public void getChooseFood() {
        if (foodInfoTableDao == null){
            initFoodInfoTableDao();
        }
        Schedulers.io().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "limenextPage refreshFourPageData: " + 355);
                //totalCount = foodInfoTableDao.queryBuilder().where(FoodInfoTableDao.Properties.Status.eq(1)).count();
                QueryBuilder<FoodInfoTable> qbCount   = foodInfoTableDao.queryBuilder();
                qbCount.where(FoodInfoTableDao.Properties.Status.eq(1));
                qbCount.where(FoodInfoTableDao.Properties.DeleteFlag.eq("NOT_DELETE"));

                if (!TextUtils.isEmpty(queryPricingMethod)) {
                    qbCount.where(FoodInfoTableDao.Properties.PricingMethod.eq(queryPricingMethod));
                }

                qbCount.where(FoodInfoTableDao.Properties.HasChoose.eq(1));

                long totalCount = qbCount.count();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (totalCount <= 0){
                        }else {
                            foods   =  qbCount.list();
                        }

                    }
                });

            }
        });
    }



    @Override
    public int getContentPlaceHolderId() {
        return R.id.fl_main_content;
    }


    @Override
    protected void onPause() {
        super.onPause();
        //关闭屏幕常亮
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //打开屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (needRestartConsumer) {
            needRestartConsumer = false;
        }
//        EventBus.getDefault().post(new FindViewResumeEvent());
//        try {
//
//            if (cbgCameraHelper!= null){
//                cbgCameraHelper.getCameraHelper().onResume();
//            }
//
//        } catch (Throwable e) {
//            e.printStackTrace();
//        }
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * 初始化app
     */
    private void initApp() {

        initData();


    }


    /**
     * 清理焦点
     */
    public void clearMainFocus() {
        //清理焦点信息
        View currentFocus = getCurrentFocus();
        if (currentFocus != null) {
            currentFocus.clearFocus();
        }
        if (DeviceManager.INSTANCE.getDeviceInterface().isCanDispatchKeyEvent()) {
            scanHolderView.requestFocus();
        }
    }

    private void findViews() {



        tvUsername = findViewById(R.id.tv_username);
        ivSysWifi = (ImageView) findViewById(R.id.iv_sys_wifi);
        tvNetDelayTime = (TextView) findViewById(R.id.tv_net_delay_time);
        if (LoginHelper.INSTANCE.getUserInfo() != null) {
            tvUsername.setText(LoginHelper.INSTANCE.getUserInfo().getUserInfo().getName());
            iv_icon = findViewById(R.id.iv_icon);

            if (!TextUtils.isEmpty(LoginHelper.INSTANCE.getUserInfo().getUserInfo().getFaceImg())) {
                GlideApp.with(MainActivity.this).load(LoginHelper.INSTANCE.getUserInfo().getUserInfo().getFaceImg()).placeholder(R.mipmap.icon_no_person)
                        .into(iv_icon);
            }
        }
        scanHolderView = findViewById(R.id.scan_holder_view);
        htlLeftNav = findViewById(R.id.htl_left_nav);
        vp2Content = findViewById(R.id.vp2_content);
        getChooseFood();
//        tv_food_name.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (tv_food_name.getText().toString().equals("暂未选择菜品")){
//                    initTvUnit();
//                }else {
//                    fl_screen_success.setVisibility(View.VISIBLE);
//                }
//            }
//        });

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        registerReceiver(mSysEventReceiver, intentFilter);


        ivSysWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        LogHelper.print("---MainActivity--dispatchKeyEvent--activity event: " + event);
        if (isSoftKeyboardShow && DeviceManager.INSTANCE.getDeviceInterface().isFinishDispatchKeyEvent()) {
            return super.dispatchKeyEvent(event);
        }
        //判断扫码枪是否连接
        if (DeviceManager.INSTANCE.getDeviceInterface().isCanDispatchKeyEvent()) {
            Schedulers.io().scheduleDirect(() -> {
                try {
            if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                KeyBoardUtils.hideSoftKeyboard(this, scanHolderView);
            } else {
                if (!scanHolderView.hasFocus()) {
                    scanHolderView.requestFocus();
                }
            }
            DeviceManager.INSTANCE.getDeviceInterface().dispatchKeyEvent(event);
                } catch (Exception e) {
                    Log.e(TAG, "Network request failed: " + e.getMessage());
                }
            });
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
            ScreenProtectHelper screenProtectHelper = getWeakRefHolder(ScreenProtectHelper.class);
            screenProtectHelper.stopScreenProtect();
        } else if (action == MotionEvent.ACTION_UP) {
            ScreenProtectHelper screenProtectHelper = getWeakRefHolder(ScreenProtectHelper.class);
            screenProtectHelper.startScreenProtect();
        }
        return super.dispatchTouchEvent(event);
    }

    private void initData() {
        initHomeContent();
        initMinuteAlarm();

    }

    @Override
    public void onNetInitSuccess() {
        hideLoadingDialog();
        Log.d(TAG, "limeonNetInitSuccess: " + 387);
    }

    @Override
    public void onNetInitError(String message) {
        hideLoadingDialog();
        showAppNetInitErrorDialog(message);
    }

    /**
     * 展示 app 初始化失败弹窗
     */
    private void showAppNetInitErrorDialog(String errorMsg) {
        CommonAlertDialogFragment commonAlertDialogFragment = CommonAlertDialogFragment.build()
                .setAlertTitleTxt("提示")
                .setAlertContentTxt("初始化失败,错误原因:\n" + errorMsg)
                .setLeftNavTxt("重试")
                .setLeftNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                    @Override
                    public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                        showLoadingDialog();
                        AppNetManager.INSTANCE.initAppNet();
                    }
                });
        if (BuildConfig.DEBUG) {
            commonAlertDialogFragment.setRightNavTxt("切换服务器")
                    .setRightNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                        @Override
                        public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                            showInputServerAddressDialog();
                        }
                    })
                    .show(MainActivity.this);
        } else {
            commonAlertDialogFragment.setRightNavTxt("关闭App")
                    .setRightNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                        @Override
                        public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                            DeviceManager.INSTANCE.getDeviceInterface().release();
                            AndroidUtils.killApp(MainActivity.this);
                        }
                    })
                    .show(MainActivity.this);
        }
    }

    /**
     * 显示修改服务器地址
     */
    private void showInputServerAddressDialog() {
        CommonInputDialogFragment.build()
                .setTitle("修改服务器地址")
                .setOnInputListener(new CommonInputDialogFragment.OnInputListener() {
                    @Override
                    public void onInputEnd(String input) {
                        ServerSettingMMKV.handleChangeServerAddress(MainActivity.this, input);
                    }
                }).show(this);
    }



    /**
     * 加载主页内容
     */
    private void initHomeContent() {
        //添加左侧tab列表
        List<HomeTabInfo<HomeMenuList.Menu>> homeTabInfoList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            if (i == 0) {
                        //订单签收
                        HomeTabInfo<HomeMenuList.Menu> paymentTabInfo = new HomeTabInfo<>();
                        paymentTabInfo.setExtraInfo(new HomeMenuList.Menu("cashier","订单签收"));
                        paymentTabInfo.setSelectRes(R.mipmap.ic_order_select);
                        paymentTabInfo.setUnSelectRes(R.mipmap.ic_order_unselect);
                        homeTabInfoList.add(paymentTabInfo);

            } else if (i == 1) {
                //商品出库
                HomeTabInfo<HomeMenuList.Menu> paymentTabInfo = new HomeTabInfo<>();
                paymentTabInfo.setExtraInfo(new HomeMenuList.Menu("stat","商品出库"));
                paymentTabInfo.setSelectRes(R.mipmap.ic_good_select);
                paymentTabInfo.setUnSelectRes(R.mipmap.ic_good_unselect);
                homeTabInfoList.add(paymentTabInfo);
            }  else {
                //称重
                HomeTabInfo<HomeMenuList.Menu> paymentTabInfo = new HomeTabInfo<>();
                paymentTabInfo.setExtraInfo(new HomeMenuList.Menu("set","称重"));
                paymentTabInfo.setSelectRes(R.mipmap.ic_weight_select);
                paymentTabInfo.setUnSelectRes(R.mipmap.ic_weight_unselect);
                homeTabInfoList.add(paymentTabInfo);
            }
        }

        htlLeftNav.addTabList(homeTabInfoList);
        htlLeftNav.setOnTabChangeListener(new HomeTabLayout.OnTabChangeListener() {
            @Override
            public void onTabSelected(int tabIndex) {
                clearMainFocus();
                vp2Content.setCurrentItem(tabIndex, false);

            }
        });

        //添加右侧内容页面
        homeTabPageAdapter = new HomeTabPageAdapter(this, homeTabInfoList);
        //禁止viewPager左右滑动切换tab页
        vp2Content.setUserInputEnabled(false);
        vp2Content.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "limeonPageSelected position: " + position);
                    htlLeftNav.setCurrentTab(position);
                    homeTabPageAdapter.createFragment(position);

                    if (position != 2){
                        EventBus.getDefault().post(new InitWeightEvent(0));
                    }else {
                        EventBus.getDefault().post(new InitWeightEvent(1));
                    }

//                }
            }
        });
        vp2Content.setAdapter(homeTabPageAdapter);
        vp2Content.setOffscreenPageLimit(1);
        vp2Content.setCurrentItem(0, false);
        htlLeftNav.setCurrentTab(saveStateCurrentTabPage);
        htlLeftNav.setEnableTabClick(true);

        //每秒回调helper
        CountDownHelper countDownHelper = getWeakRefHolder(CountDownHelper.class);
        countDownHelper.startCountDown();
        //开始心跳设置
        HeartBeatHelper heartBeatHelper = getWeakRefHolder(HeartBeatHelper.class);
        heartBeatHelper.requestHeartBeat();
        countDownHelper.addCountDownListener(heartBeatHelper);
//        //请求设备信息
//        StoreInfoHelper storeInfoHelper = getWeakRefHolder(StoreInfoHelper.class);
//        storeInfoHelper.requestStoreInfo();
//        storeInfoHelper.addGetStoreInfoListener(new OnGetStoreInfoListener() {
//            @Override
//            public void onGetStoreInfo(StoreInfo storeInfo) {
//                htlConsumer.getTv_canteen_name().setText(storeInfo.getDeviceName());
//            }
//        });
//        //获取餐厅时段信息
//        ConsumerModeHelper consumerModeHelper = getWeakRefHolder(ConsumerModeHelper.class);
//        consumerModeHelper.requestCanteenCurrentTimeInfo();
//        countDownHelper.addCountDownListener(consumerModeHelper);

        //初始化语音
        TTSVoiceHelper ttsVoiceHelper = getWeakRefHolder(TTSVoiceHelper.class);
        ttsVoiceHelper.initTTSVoice(null);

        //网络状态回调
//        SystemEventWatcherHelper systemEventWatcherHelper = getWeakRefHolder(SystemEventWatcherHelper.class);
//        countDownHelper.addCountDownListener(systemEventWatcherHelper);

        //启动检查升级
        AppUpgradeHelper appUpgradeHelper = getWeakRefHolder(AppUpgradeHelper.class);
        appUpgradeHelper.checkAppVersion();
        hasInitMenuData = true;
    }

    @Override
    protected void onSaveInstanceState(@androidx.annotation.NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(TAB_CURRENT_PAGE, vp2Content.getCurrentItem());
    }

    private void readSaveInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            saveStateCurrentTabPage = savedInstanceState.getInt(TAB_CURRENT_PAGE, 0);
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {

        if (vp2Content.getCurrentItem() == 0) {

            long currentTime = System.currentTimeMillis();
            if ((currentTime - lastBackClickTime) > 2000) {
                AppToast.toastMsg("再按一次退出程序");
                lastBackClickTime = currentTime;
            } else {
                //杀掉进程
                DeviceManager.INSTANCE.getDeviceInterface().release();
                AndroidUtils.killApp(this);
            }
        }else {
            EventBus.getDefault().post(new BindFragmentBackEvent(vp2Content.getCurrentItem()));
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.INSTANCE.clearMainActivity();
        EventBus.getDefault().unregister(this);
        unregisterReceiver(mSysEventReceiver);
    }

    @Override
    public void onCreateFacePreviewView(SurfaceView previewView, SurfaceView irPreview) {

    }

    @Override
    public void onConsumerDismiss() {
        needRestartConsumer = true;
        ConsumerManager.INSTANCE.clearConsumerPresentation();
        //清理相机相关引用,释放相机
        CBGCameraHelper cbgCameraHelper = getWeakRefHolder(CBGCameraHelper.class);
        cbgCameraHelper.releaseCameraHelper();
        clearWeakRefHolder(CBGCameraHelper.class);
    }


    /**
     * 字体颜色渐变
     * @param textView
     */
    private void setTextViewStyles(TextView textView) {
        float x1=textView.getPaint().measureText(textView.getText().toString());
        float y1=textView.getPaint().getTextSize();
        int c1=Color.parseColor("#307EFE");
        int c2= Color.parseColor("#70DDFF");
        LinearGradient topToBottomLG = new LinearGradient(0, 0, 0, y1,c1, c2, Shader.TileMode.CLAMP);
        textView.getPaint().setShader(topToBottomLG);
        textView.invalidate();
    }

    @Override
    public void onCreateTitleLayout(HomeTitleLayout homeTitleLayout) {

    }

    @Override
    public void addContentPlaceHolderFragment(Fragment fragment) {
        super.addContentPlaceHolderFragment(fragment);
    }

    public Fragment getCurrentTabFragment() {
        if (vp2Content != null && homeTabPageAdapter != null) {
            int currentItem = vp2Content.getCurrentItem();
            return homeTabPageAdapter.findPageFragment(this, currentItem);
        }
        return null;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBindFragmentSwitchEvent(BindFragmentSwitchEvent eventBus) {
        Log.d(TAG, "limeonBindFragmentSwitchEvent 700 eventBus: " + eventBus.getPosition());
        vp2Content.setCurrentItem(eventBus.getPosition(), false);
        if (eventBus.getPosition() == 0){
            AppApplication.barcode = "";

            vp2Content.setVisibility(View.GONE);
            getChooseFood();


        }else {
//            if (yxDevicePortCtrl != null && yxDevicePortCtrl.isOpen()){
//                yxDevicePortCtrl.closeDevice();
//            }
            canHandleQRData = false;
            vp2Content.setVisibility(View.VISIBLE);
        }
    }

    private AlarmManager mAlarmManager;
    private PendingIntent mPendingIntent;

    private void initMinuteAlarm() {
        mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, MinuteReceiver.class);
        mPendingIntent = PendingIntent.getBroadcast(
                this, 0, intent, PendingIntent.FLAG_IMMUTABLE
        );

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.MINUTE, 1);

        mAlarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                60 * 1000,
                mPendingIntent
        );
    }

    @Override
    public void onCheckVersionEnd(String msg) {
        if (!TextUtils.isEmpty(msg) && !msg.contains("最新版本")) {
            CommonDialogUtils.showTipsDialog(MainActivity.this, msg);
        }
    }

    @Override
    public void onCheckVersionStart() {

    }

    @Override
    public void onCheckVersionError(String msg) {
        CommonDialogUtils.showTipsDialog(MainActivity.this, "检查更新失败:" + msg);
    }

    @Override
    public void onNoVersionUpgrade() {

    }

    @Override
    public void onDownloadStart(DownloadFileInfo downloadFileInfo) {
    }

    @Override
    public void onDownloadProgress(int progress) {
    }

    @Override
    public void onDownloadError(String msg) {
        AppToast.toastMsg("下载app失败");
    }

    @Override
    public void onDownloadSuccess(DownloadFileInfo downloadFileInfo, boolean isForceUpdate) {
        if (!isForceUpdate) {
            CommonAlertDialogFragment commonAlertDialogFragment = CommonAlertDialogFragment.build()
                    .setAlertTitleTxt("版本升级")
                    .setAlertContentTxt("新版本下完毕，点击更新")
                    .setLeftNavTxt("更新")
                    .setNeedHandleDismiss(true)
                    .setLeftNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                        @Override
                        public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                            //去安装
                            alertDialogFragment.dismiss();
                            DeviceManager.INSTANCE.getDeviceInterface().silenceInstallApk(downloadFileInfo.getLocalUri());
                            Lztek.create(MainActivity.this).installApplication(downloadFileInfo.getLocalUri());
                        }
                    })
                    .setRightNavTxt("取消")
                    .setRightNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                        @Override
                        public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                            alertDialogFragment.dismiss();
                        }
                    });
            commonAlertDialogFragment.show(MainActivity.this);
        }
    }

    public static class MinuteReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
        }
    }





    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTTSSpeakEvent(TTSSpeakEvent eventBus) {
        Log.d(TAG, "limeonTTSSpeakEvent: " + 832);
        if (!TextUtils.isEmpty(eventBus.getContent())){
            Log.d(TAG, "limeonTTSSpeakEvent: " + 834);
            Activity mainActivity = AppManager.INSTANCE.getMainActivity();
            if (mainActivity instanceof BaseActivity) {
                Log.d(TAG, "limeonTTSSpeakEvent: " + 837);
                BaseActivity baseActivity = (BaseActivity) mainActivity;
                baseActivity.getWeakRefHolder(TTSVoiceHelper.class).speakByTTSVoice(eventBus.getContent());
            }
        }
    }



    /**
     * 绑盘接口
     */
    @SuppressLint("AutoDispose")
    public void plateBinding() {
//        if (yxDevicePortCtrl != null && yxDevicePortCtrl.isOpen()) {
//            yxDevicePortCtrl.closeDevice();
//        }
        canHandleQRData = false;
        if (!NetworkUtils.isConnected()) {
            ToastUtils.toastMsgError("网络已断开，绑盘失败");
            onTTSSpeakEvent(new TTSSpeakEvent("网络已断开，绑盘失败"));

            return;
        }
        showLoadingDialog("绑盘中");
        Log.i(TAG, "limefoodSyncCallback: " + 177);
        TreeMap<String, String> paramsMap = ParamsUtils.newSortParamsMapWithMode("addFoodPre");
        paramsMap.put("plateCode", AppApplication.barcode);
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(MachineService.class)
                .addFoodPre(ParamsUtils.signSortParamsMap(paramsMap))
                .compose(RxTransformerUtils.mainSchedulers())
                .subscribe(new DefaultObserver<BaseNetResponse<AddFoodPreBean>>() {
                    @Override
                    protected void onSuccess(BaseNetResponse<AddFoodPreBean> baseNetResponse) {
                        hideLoadingDialog();
                        try {
                            if (baseNetResponse.isSuccess() && baseNetResponse.getData() != null && baseNetResponse.getData().getUser() != null) {
                                hasAddOrderFood = true;
                                canHandleQRData = true;
                                beforeWeight = -10000;
                                maxWeight = 0;
                                customerId = baseNetResponse.getData().getUser().getId();
                                balance = baseNetResponse.getData().getAmount().getAmount();
                                orderPrice = baseNetResponse.getData().getOrderAmount().getAmount();
                                updateWeightAndPrice(0,0,baseNetResponse.getData().getOrderAmount().getAmount());
                                onTTSSpeakEvent(new TTSSpeakEvent("绑定成功，请取餐"));
                                openScale();
                            } else {
                                ToastUtils.toastMsgError(TextUtils.isEmpty(baseNetResponse.getMsg()) ? baseNetResponse.getMessage() : baseNetResponse.getMsg());
                                onTTSSpeakEvent(new TTSSpeakEvent(TextUtils.isEmpty(baseNetResponse.getMsg()) ? baseNetResponse.getMessage() : baseNetResponse.getMsg()));

                            }

                        } catch (Exception e) {
                            Log.e(TAG, "limeplateBinding 342: " + e.getMessage());
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        hideLoadingDialog();
                        //AppToast.toastMsg(e.getMessage());
                        Log.e(TAG, "limeplateBinding: " + e.getMessage());
                        ToastUtils.toastMsgError("系统异常,请联系管理员");
                        onTTSSpeakEvent(new TTSSpeakEvent("系统异常,请联系管理员"));

                    }
                });
    }

    public void openScale() {


    }

    private void updateWeightAndPrice(double weight,double price,double orderPrice) {
        if (weight > maxWeight){
            maxWeight = weight;
        }
        if ((orderPrice + price) > balance) {
            if (System.currentTimeMillis() - beforeBalanceTime > 2000) {
                beforeBalanceTime = System.currentTimeMillis();
                ToastUtils.toastMsgError("余额不足");
                onTTSSpeakEvent(new TTSSpeakEvent("余额不足"));
            }
        }else {
        }

    }

    /**
     * 加菜
     */
    @SuppressLint("AutoDispose")
    public void addOrderFood() {

        if (!NetworkUtils.isConnected()) {
            ToastUtils.toastMsgError("网络已断开，结算失败");
            onTTSSpeakEvent(new TTSSpeakEvent("网络已断开，结算失败"));
            return;
        }

//        if (yxDevicePortCtrl != null && yxDevicePortCtrl.isOpen()) {
//            yxDevicePortCtrl.closeDevice();
//        }
        canHandleQRData = false;
        showLoadingDialog("计费中");
        TreeMap<String, String> paramsMap = ParamsUtils.newSortParamsMapWithMode("addOrderFood");

        DeviceFoodConsumeParam deviceFoodConsumeParam = new DeviceFoodConsumeParam(DeviceManager.INSTANCE.getDeviceInterface().getMachineNumber(),
                customerId, plateCode, foodInfoTable.getId(), foodInfoTable.getName(), 1, weight, foodInfoTable.getUnitPriceMoney_amount(), Double.parseDouble(PriceUtils.formatPrice((foodInfoTable.getUnitPriceMoney_amount() * weight) / 1000.00)));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            paramsMap.put("deviceFoodConsumeParam", Base64.getEncoder().encodeToString(JSON.toJSONString(deviceFoodConsumeParam).getBytes()));
        }
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(MachineService.class)
                .addOrderFood(ParamsUtils.signSortParamsMap(paramsMap))
                .compose(RxTransformerUtils.mainSchedulers())
                .subscribe(new DefaultObserver<BaseNetResponse<AddOrderFoodResult>>() {
                    @Override
                    protected void onSuccess(BaseNetResponse<AddOrderFoodResult> baseNetResponse) {
                        hideLoadingDialog();
                        try {
                            if (baseNetResponse.isSuccess()) {
                                plateCode = "";
                                ToastUtils.toastMsgSuccess("取餐成功");
                                onTTSSpeakEvent(new TTSSpeakEvent("取餐成功"));
                            }else {
                                ToastUtils.toastMsgError(TextUtils.isEmpty(baseNetResponse.getMsg()) ? baseNetResponse.getMessage() : baseNetResponse.getMsg());
                                onTTSSpeakEvent(new TTSSpeakEvent(TextUtils.isEmpty(baseNetResponse.getMsg()) ? baseNetResponse.getMessage() : baseNetResponse.getMsg()));
                            }

                            canHandleQRData = true;
                        } catch (Exception e) {
                            Log.e(TAG, "limeplateBinding 342: " + e.getMessage());
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        //AppToast.toastMsg(e.getMessage());
                        hideLoadingDialog();
                        canHandleQRData = true;
                        Log.e(TAG, "limeplateBinding: " + e.getMessage());
                        ToastUtils.toastMsgError("系统异常,请联系管理员");
                        onTTSSpeakEvent(new TTSSpeakEvent("系统异常,请联系管理员"));
                    }
                });
    }

    public void onNetworkAvailable() {
        LogHelper.print("--HomeTitleLayout--onNetworkAvailable");
        //获取当wifi信号强度
        refreshWifiLay();
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
        Log.d("SystemEventHelper", "level: " +level);
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

    /**
     * 系统事件监听（时间、电池、wifi强度）
     */
    private final BroadcastReceiver mSysEventReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }
            String action = intent.getAction();
            if (action == null || action.isEmpty()) {
                return;
            }

            switch (action) {
                case WifiManager.WIFI_STATE_CHANGED_ACTION:
                case WifiManager.NETWORK_STATE_CHANGED_ACTION:
                case WifiManager.RSSI_CHANGED_ACTION:
                    Log.d("SystemEventHelper", "RSSI_CHANGED_ACTION");
                    NetworkCapabilities networkCapabilities = getActiveNetworkCapabilities();
                    if (networkCapabilities != null) {
                        if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                            boolean isConnected = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
                            int rssi = intent.getIntExtra(WifiManager.EXTRA_NEW_RSSI, -1);
                            //计算出信号的等级
                            int level = WifiManager.calculateSignalLevel(rssi, 4);
                            WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                            boolean isWifiClosed = wifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED;
                            boolean isWifiClosed2 = !wifiManager.isWifiEnabled();
                            if (isWifiClosed || isWifiClosed2){
                                onNetworkRssiChange(0,4);
                            } else {
                                onNetworkRssiChange(level,4);
                            }

                        }
                    }

                    break;
            }
        }
    };


    /**
     * 获取当前正在连接的网络
     */
    private NetworkCapabilities getActiveNetworkCapabilities() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) AppManager.INSTANCE.getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
        Network activeNetwork = connectivityManager.getActiveNetwork();
        if (activeNetwork != null) {
            return connectivityManager.getNetworkCapabilities(activeNetwork);
        }
        return null;
    }




}