package com.stkj.aoxin.weight.setting.service;

import com.stkj.aoxin.weight.base.model.BaseNetResponse;
import com.stkj.aoxin.weight.machine.model.PlateBinding;
import com.stkj.aoxin.weight.setting.model.CheckAppVersion;
import com.stkj.aoxin.weight.setting.model.FacePassPeopleListInfo;
import com.stkj.aoxin.weight.setting.model.FoodListInfo;
import com.stkj.aoxin.weight.setting.model.FoodSyncCallback;

import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface SettingService {
    /**
     * 设备录入人员信息接口
     */
    @GET("home/v3/index")
    Observable<BaseNetResponse<FacePassPeopleListInfo>> getAllFacePass(@QueryMap Map<String, String> requestParams);

    /**
     * 设备录入人员回调
     */
    @GET("home/v3/index")
    Observable<BaseNetResponse<String>> facePassCallback(@QueryMap Map<String, String> requestParams);

    /**
     * 绑盘接口
     */
    @GET("home/v3/index")
    Observable<BaseNetResponse<PlateBinding>> plateBinding(@QueryMap Map<String, String> requestParams);


    /**
     * 获取当日营业情况
     */
    @GET("home/v3/index")
    Observable<BaseNetResponse<PlateBinding>> consumeDaySummary(@QueryMap Map<String, String> requestParams);


    /**
     * 设备录入人员回调(同步)
     */
    @GET("home/v3/index")
    Call<BaseNetResponse<String>> syncFacePassCallback(@QueryMap Map<String, String> requestParams);

    /**
     * 检查更新
     */
    @GET("home/v3/index")
    Observable<BaseNetResponse<CheckAppVersion>> checkAppVersion(@QueryMap Map<String, String> requestParams);


    /**
     * 设备 APP 升级回调接口
     */
    @GET("home/v3/index")
    Call<BaseNetResponse<String>> appUpgradeCallback(@QueryMap Map<String, String> requestParams);


    /**
     * 同步菜品
     */
    @GET("home/v3/index")
    Observable<BaseNetResponse<FoodListInfo>> getAllFood(@QueryMap Map<String, String> requestParams);


    /**
     * 同步菜品回调
     */
    @GET("home/v3/index")
    Observable<BaseNetResponse<FoodSyncCallback>> foodSyncCallback(@QueryMap Map<String, String> requestParams);


}
