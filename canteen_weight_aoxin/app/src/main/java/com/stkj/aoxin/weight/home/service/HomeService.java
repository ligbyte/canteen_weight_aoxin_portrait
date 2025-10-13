package com.stkj.aoxin.weight.home.service;

import com.stkj.aoxin.weight.base.model.BaseNetResponse;
import com.stkj.aoxin.weight.base.model.BaseResponse;
import com.stkj.aoxin.weight.home.model.HeartBeatInfo;
import com.stkj.aoxin.weight.home.model.HomeMenuList;
import com.stkj.aoxin.weight.home.model.OfflineSetInfo;
import com.stkj.aoxin.weight.home.model.StoreInfo;

import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * 首页相关请求接口
 */
public interface HomeService {
    /**
     * 查询首页左侧菜单
     */
    @GET("/api/webapp/drapp/menu/list")
    Observable<BaseResponse<List<HomeMenuList>>> menuList();

    /**
     * 设备查询心跳接口
     */
    @GET("home/v3/heartBeat")
    Observable<BaseNetResponse<HeartBeatInfo>> heartBeat(@QueryMap Map<String, String> params);

    /**
     * 设备查询心跳接口
     */
    @GET("home/v3/index")
    Observable<BaseNetResponse<HeartBeatInfo>> reportDeviceStatus(@QueryMap Map<String, String> params);

    /**
     * 设备录入公司名称接口
     */
    @GET("home/v3/index")
    Observable<BaseNetResponse<StoreInfo>> getStoreInfo(@QueryMap Map<String, String> params);

    /**
     * 设备录入脱机参数接口
     */
    @GET("home/v3/index")
    Observable<BaseNetResponse<OfflineSetInfo>> offlineSet(@QueryMap Map<String, String> params);
}
