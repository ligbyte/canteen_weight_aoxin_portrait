package com.stkj.aoxin.weight.machine.service;

import com.stkj.aoxin.weight.base.model.BaseNetResponse;
import com.stkj.aoxin.weight.machine.model.AddFoodPreBean;
import com.stkj.aoxin.weight.machine.model.AddOrderFoodResult;
import com.stkj.aoxin.weight.machine.model.ConsumeDaySummaryResponse;

import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface MachineService {

    /**
     * 绑盘接口
     */
    @GET("home/v3/index")
    Observable<BaseNetResponse<AddFoodPreBean>> addFoodPre(@QueryMap Map<String, String> requestParams);


    /**
     * 获取当日营业情况
     */
    @GET("home/v3/index")
    Observable<BaseNetResponse<ConsumeDaySummaryResponse>> consumeDaySummary(@QueryMap Map<String, String> requestParams);

    /**
     * 加菜
     */
    @GET("home/v3/index")
    Observable<BaseNetResponse<AddOrderFoodResult>> addOrderFood(@QueryMap Map<String, String> requestParams);
}
