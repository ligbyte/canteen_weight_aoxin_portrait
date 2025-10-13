package com.stkj.aoxin.weight.base.net;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.stkj.aoxin.weight.AppApplication;
import com.stkj.aoxin.weight.base.callback.AppNetCallback;
import com.stkj.aoxin.weight.base.model.AppNetInitResponse;
import com.stkj.aoxin.weight.base.model.ShopInitInfo;
import com.stkj.aoxin.weight.base.service.AppService;
import com.stkj.common.log.LogHelper;
import com.stkj.common.net.retrofit.RetrofitManager;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * app 网络管理类
 */
public enum AppNetManager {
    INSTANCE;

    // 新安县私有化部署
      public static final String API_OFFICIAL_URL = "https://xaapi.shengtudx.cn/";

    // 盛图云平台 正式服务器
//    public static final String API_OFFICIAL_URL = "https://restaurant.shengtudx.cn/";

     // 万基正式
    //public static final String API_OFFICIAL_URL = "https://cater.wanjigroup.com:9997";


       // 盛图云平台  测试服务器
       public static final String API_TEST_URL = "http://101.43.252.67:9003";


    //子寓电脑
//    public static final String API_TEST_URL = "http://192.168.1.18:9003";


    private AppOkhttpIntercept appOkhttpIntercept;
    private AppRetrofitJsonConvertListener retrofitJsonConvertListener;
    private ShopInitInfo mShopInitInfo;
    private boolean isRequestingDeviceDomain;
    private Set<AppNetCallback> netCallbackSet = new HashSet<>();

    public AppOkhttpIntercept getAppOkhttpHttpIntercept() {
        if (appOkhttpIntercept == null) {
            appOkhttpIntercept = new AppOkhttpIntercept();
        }
        return appOkhttpIntercept;
    }

    public AppRetrofitJsonConvertListener getRetrofitJsonConvertListener() {
        if (retrofitJsonConvertListener == null) {
            retrofitJsonConvertListener = new AppRetrofitJsonConvertListener();
        }
        return retrofitJsonConvertListener;
    }

    public void initAppNet() {
        isRequestingDeviceDomain = true;
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(AppService.class)
                .appInit(ParamsUtils.newMachineParamsMap())
                .compose(RxTransformerUtils.mainSchedulers())
                .subscribe(new DefaultObserver<AppNetInitResponse>() {
                    @Override
                    protected void onSuccess(AppNetInitResponse response) {
                        isRequestingDeviceDomain = false;
                        LogHelper.print("AppNetManager -- initAppNet --success: " + response.toString());
                        if (response.isSuccess()) {
                            ShopInitInfo shopInitInfo = response.getData();
                            if (shopInitInfo != null && !TextUtils.isEmpty(shopInitInfo.getDomain())) {
                                AppApplication.setDomainSuccess = true;
                                mShopInitInfo = shopInitInfo;
                                for (AppNetCallback callback : netCallbackSet) {
                                    callback.onNetInitSuccess();
                                }
                            } else {
                                for (AppNetCallback callback : netCallbackSet) {
                                    callback.onNetInitError("DeviceDomain为空!");
                                }
                            }
                        } else {
                            for (AppNetCallback callback : netCallbackSet) {
                                callback.onNetInitError("响应Code: " + response.getCode() + " msg: " + response.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        isRequestingDeviceDomain = false;
                        for (AppNetCallback callback : netCallbackSet) {
                            callback.onNetInitError("error: " + e.getMessage());
                        }
                        LogHelper.print("AppNetManager -- initAppNet --error: " + e.getMessage());
                    }
                });
    }

    public String getDeviceDomain() {
        return mShopInitInfo == null ? "" : mShopInitInfo.getDomain();
    }

    public String getShopId() {
        return mShopInitInfo == null ? "" : mShopInitInfo.getShopId();
    }

    public String getDeviceId() {
        return mShopInitInfo == null ? "" : mShopInitInfo.getDeviceId();
    }

    public void clearAppNetCache() {
        mShopInitInfo = null;
        isRequestingDeviceDomain = false;
        RetrofitManager.INSTANCE.removeAllRetrofit();
    }

    public boolean isRequestingDeviceDomain() {
        return isRequestingDeviceDomain;
    }

    public void addNetCallback(@NonNull AppNetCallback appNetCallback) {
        netCallbackSet.add(appNetCallback);
    }

    public void removeNetCallback(@NonNull AppNetCallback appNetCallback) {
        netCallbackSet.remove(appNetCallback);
    }
}