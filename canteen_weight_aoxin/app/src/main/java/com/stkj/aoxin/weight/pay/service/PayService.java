package com.stkj.aoxin.weight.pay.service;

import com.stkj.aoxin.weight.base.model.BaseNetResponse;
import com.stkj.aoxin.weight.base.model.BaseResponse;
import com.stkj.aoxin.weight.pay.goods.model.AddGoodsCateParams;
import com.stkj.aoxin.weight.pay.goods.model.DelGoodsCateParams;
import com.stkj.aoxin.weight.pay.goods.model.DelGoodsSpecParams;
import com.stkj.aoxin.weight.pay.goods.model.GoodsBaseInfo;
import com.stkj.aoxin.weight.pay.goods.model.GoodsCate;
import com.stkj.aoxin.weight.pay.goods.model.GoodsIdBaseListInfo;
import com.stkj.aoxin.weight.pay.goods.model.GoodsSaleListInfo;
import com.stkj.aoxin.weight.pay.goods.model.GoodsSpec;
import com.stkj.aoxin.weight.pay.goods.model.RecheckOrderParams;
import com.stkj.aoxin.weight.pay.goods.model.RecheckOrderResult;
import com.stkj.aoxin.weight.pay.model.ConsumerRecordListResponse;
import com.stkj.aoxin.weight.pay.model.FoodBillStatDayPageResponse;
import com.stkj.aoxin.weight.pay.model.FoodCategoryListInfo;
import com.stkj.aoxin.weight.pay.model.FoodConsumeDetailResponse;
import com.stkj.aoxin.weight.pay.model.FoodConsumePageResponse;
import com.stkj.aoxin.weight.pay.model.IntervalCardType;
import com.stkj.aoxin.weight.pay.model.ModifyBalanceResult;
import com.stkj.aoxin.weight.pay.model.CanteenCurrentTimeInfo;
import com.stkj.aoxin.weight.pay.model.OrderInfoBean;
import com.stkj.aoxin.weight.pay.model.OrderItemBean;
import com.stkj.aoxin.weight.pay.model.TakeMealListResult;
import com.stkj.aoxin.weight.pay.model.TakeMealResult;
import com.stkj.aoxin.weight.pay.goods.model.AddGoodsSpecParams;
import com.stkj.aoxin.weight.setting.model.FoodSave;

import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface PayService {

    //添加商品
    @POST("/api/webapp/service/spgoodsinfo/add")
    Observable<BaseResponse<String>> addGoods(@Body GoodsBaseInfo requestParams);

    //获取消费账单列表
    @GET("home/v3/index")
    Observable<BaseNetResponse<ConsumerRecordListResponse>> getConsumerRecordList(@QueryMap Map<String, String> requestParams);

    //获取消费账单列表
    @GET("home/v3/index")
    Observable<BaseNetResponse<FoodConsumePageResponse>> foodConsumePage(@QueryMap Map<String, String> requestParams);


    //获取食品签收列表
//    @POST("api/webapp/dradmin/supplyProductOrder/pageOrderSupply")
//    Observable<BaseNetResponse<OrderItemBean>> pageOrderSupply(@Body Map<String, String> requestParams);


    //订单信息分页查询-学校端
    @POST("api/webapp/dradmin/supplyProductOrder/pageOrder")
    Observable<BaseNetResponse<OrderItemBean>> pageOrder(@Body Map<String, String> requestParams);


    //查询供货单-验收单-学校端
    @GET("/api/webapp/dradmin/supplyProductOrder/orderInfo")
    Observable<BaseNetResponse<OrderInfoBean>> orderInfo(@QueryMap Map<String, String> requestParams);


    //复核产品数据
    @POST("api/webapp/dradmin/supplyProductOrder/recheckOrder")
    Observable<BaseResponse<RecheckOrderResult>> recheckOrder(@Body RecheckOrderParams requestParams);


    //获取消费账单列表详情
    @GET("home/v3/index")
    Observable<BaseNetResponse<FoodConsumeDetailResponse>> foodConsumeDetail(@QueryMap Map<String, String> requestParams);


    //获取消费账单列表每日统计
    @GET("home/v3/index")
    Observable<BaseNetResponse<FoodBillStatDayPageResponse>> foodBillStatDayPage(@QueryMap Map<String, String> requestParams);

    //获取退款账单列表
    @POST("home/v3/index")
    Observable<BaseNetResponse<ConsumerRecordListResponse>> getRefundOrderList(@QueryMap Map<String, String> requestParams);

    //去退款账单
    @POST("home/v3/index")
    Observable<BaseNetResponse<Object>> refundOrder(@Body Map<String, String> paramsMap);

    //去支付
    @GET("home/v3/index")
    Observable<BaseNetResponse<ModifyBalanceResult>> goToPay(@QueryMap Map<String, String> requestParams);

    //去支付 foods
    @GET("home/v3/index")
    Observable<BaseNetResponse<ModifyBalanceResult>> goToPayFoods(@QueryMap Map<String, String> requestParams);

    //菜品新增 foods
    @GET("home/v3/index")
    Observable<FoodSave> foodSave(@QueryMap Map<String, String> requestParams);

    //获取支付状态
    @GET("home/v3/index")
    Observable<BaseNetResponse<ModifyBalanceResult>> getPayStatus(@QueryMap Map<String, String> requestParams);

    //获取取餐列表
    @GET("home/v3/index")
    Observable<TakeMealListResult> takeMealList(@QueryMap Map<String, String> requestParams);

    //获取取餐列表
    @GET("home/v3/index")
    Observable<TakeMealListResult> takeCodeQuery(@QueryMap Map<String, String> requestParams);

    //出餐
    @POST("home/v3/index")
    Observable<BaseNetResponse<TakeMealResult>> takeMeal(@QueryMap Map<String, String> requestParams);

    //获取当前餐厅时段信息
    @GET("home/v3/index")
    Observable<BaseNetResponse<CanteenCurrentTimeInfo>> getCanteenTimeInfo(@QueryMap Map<String, String> requestParams);

    //获取按次消费的金额信息
    @GET("home/v3/index")
    Observable<BaseNetResponse<List<IntervalCardType>>> getIntervalCardType(@QueryMap Map<String, String> requestParams);


    //快速查找商品列表
    @GET("/api/webapp/service/spgoodsinfo/listTree")
    Observable<BaseResponse<List<GoodsIdBaseListInfo>>> searchGoodsList(@Query("searchKey") String requestParams);



    //获取商品详情
    @GET("/api/webapp/service/spgoodsinfo/detail")
    Observable<BaseResponse<GoodsSaleListInfo>> getGoodsDetail(@Query("id") String requestParams);

    @GET("/api/webapp/service/spgoodscategory/list")
    Call<BaseResponse<List<GoodsCate>>> getGoodsCateListSync(@Query("type") int goodsType);

    //请求商品规格
    @GET("/api/webapp/service/spgoodsspec/list")
    Call<BaseResponse<List<GoodsSpec>>> getGoodsSpecListSync(@Query("type") int goodsType);

    //请求商品分类
    @GET("/api/webapp/service/spgoodscategory/list")
    Observable<BaseResponse<List<GoodsCate>>> getGoodsCateList(@Query("type") int goodsType);


    //请求商品规格
    @GET("/api/webapp/service/spgoodsspec/list")
    Observable<BaseResponse<List<GoodsSpec>>> getGoodsSpecList(@Query("type") int goodsType);


    //添加商品分类
    @POST("/api/webapp/service/spgoodscategory/add")
    Observable<BaseResponse<String>> addGoodsCate(@Body AddGoodsCateParams addGoodsCateParams);


    //添加商品规格
    @POST("/api/webapp/service/spgoodsspec/add")
    Observable<BaseResponse<String>> addGoodsSpec(@Body AddGoodsSpecParams addGoodsSpecParams);

    //删除商品分类
    @POST("/api/webapp/service/spgoodscategory/delete")
    Observable<BaseResponse<String>> delGoodsCate(@Body List<DelGoodsCateParams> delGoodsCateParams);

    //删除商品规格
    @POST("/api/webapp/service/spgoodsspec/delete")
    Observable<BaseResponse<String>> delGoodsSpec(@Body List<DelGoodsSpecParams> delGoodsSpecParams);

    /**
     * 同步菜品
     */
    @GET("home/v3/index")
    Observable<BaseNetResponse<FoodCategoryListInfo>> foodCategory(@QueryMap Map<String, String> requestParams);

}
