package com.stkj.aoxin.weight.login.service;

import com.stkj.aoxin.weight.base.model.BaseResponse;
import com.stkj.aoxin.weight.login.model.GetPhonevalidcode;
import com.stkj.aoxin.weight.login.model.GetPicCaptchaInfo;
import com.stkj.aoxin.weight.login.model.UserInfo;

import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * 登录接口
 */
public interface LoginService {

    // 设备端登录接口
    @POST("/api/webapp/auth/device/deviceLogin")
    Observable<BaseResponse<UserInfo>> login(@Body Map<String, String> paramsMap);

    // web端登录接口
    @POST("/api/webapp/auth/b/deviceLogin")
    Observable<BaseResponse<UserInfo>> loginWeb(@Body Map<String, String> paramsMap);


    // 图片验证码
    @GET("/api/webapp//auth/b/getPicCaptcha")
    Observable<BaseResponse<GetPicCaptchaInfo>> getPicCaptcha(@QueryMap Map<String, String> paramsMap);


    // 手机验证码
    @GET("/api/webapp/auth/b/getPhoneValidCode")
    Observable<BaseResponse<GetPhonevalidcode>> getPhoneValidCode(@QueryMap Map<String, String> paramsMap);


}
