package com.stkj.aoxin.weight.pay.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.stkj.aoxin.weight.base.model.BaseNetResponse;
import com.stkj.aoxin.weight.base.net.ParamsUtils;
import com.stkj.aoxin.weight.base.ui.dialog.CommonAlertDialogFragment;
import com.stkj.aoxin.weight.base.utils.CommonDialogUtils;
import com.stkj.aoxin.weight.home.ui.activity.LoginLandActivity;
import com.stkj.aoxin.weight.pay.model.FoodConsumeBean;
import com.stkj.aoxin.weight.pay.model.FoodConsumePageResponse;
import com.stkj.aoxin.weight.pay.model.OrderCheckSuccesskEvent;
import com.stkj.aoxin.weight.pay.model.OrderItemBean;
import com.stkj.aoxin.weight.pay.service.PayService;
import com.stkj.aoxin.weight.stat.ui.adapter.OrderCompleteListAdapter;
import com.stkj.common.net.retrofit.RetrofitManager;
import com.stkj.common.rx.AutoDisposeUtils;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;
import com.stkj.common.ui.fragment.BaseRecyclerFragment;
import com.stkj.aoxin.weight.R;
import com.stkj.aoxin.weight.base.ui.widget.AppSmartRefreshLayout;
import com.stkj.aoxin.weight.base.utils.EventBusUtils;
import com.stkj.aoxin.weight.pay.model.RefreshConsumerGoodsModeEvent;
import com.stkj.common.ui.toast.AppToast;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.TreeMap;

/**
 * 待签收页面
 */
public class OrderCompleteFragment extends BaseRecyclerFragment {


    public final String TAG = "OrderCompleteFragment";

    private int pageTotal = 1;
    private int pageIndex = 1;
    private AppSmartRefreshLayout srlRecordList;
    private RecyclerView rvContent;
    private OrderCompleteListAdapter orderCompleteListAdapter;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_order_complete;
    }



    @Override
    protected void initViews(View rootView) {
        srlRecordList = (AppSmartRefreshLayout) findViewById(R.id.srl_record_list);
        rvContent = (RecyclerView) findViewById(R.id.rv_content);

        srlRecordList.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                pageIndex += 1;
                getOrderCompleteList();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                pageIndex = 1;
                getOrderCompleteList();
            }
        });

        orderCompleteListAdapter = new OrderCompleteListAdapter(getActivity());

        orderCompleteListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
//                showLoadingDialog();
//                getOrderDetail(orderCompleteListAdapter.getData().get(position).getId());
            }
        });

        rvContent.setAdapter(orderCompleteListAdapter);
        rvContent.setItemAnimator(null);
        rvContent.setItemViewCacheSize(12);
        rvContent.setHasFixedSize(true);


        orderCompleteListAdapter.setUseEmpty(true);
        orderCompleteListAdapter.setEmptyView(R.layout.layout_empty_view);

        pageIndex = 1;
        getOrderCompleteList();
    }


    @Override
    protected void onFragmentResume(boolean isFirstOnResume) {
        EventBusUtils.registerEventBus(this);



    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOrderCheckSuccesskEvent(OrderCheckSuccesskEvent eventBus) {
        getOrderCompleteList();
    }




    @Override
    public void onDetach() {
        super.onDetach();
        EventBusUtils.unRegisterEventBus(this);
    }


    private void getOrderCompleteList() {
        Log.d(TAG, "limegetOrderCompleteList pageIndex: " + pageIndex);
        if (pageIndex > 1 && pageIndex  > pageTotal){
            hideLoadingDialog();
            srlRecordList.finishRefresh();
            srlRecordList.finishLoadMore();
            AppToast.toastMsg("没有更多数据了!");
            return;
        }

        Log.d(TAG, "limegetOrderCompleteList: " + 293);
        TreeMap<String, String> paramsMap = ParamsUtils.newSortParamsMapWithMode("pageOrder");
        paramsMap.put("current",String.valueOf(pageIndex));
        paramsMap.put("size",String.valueOf(20));
        paramsMap.put("orderStatus",String.valueOf(4));


        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(PayService.class)
                .pageOrder(ParamsUtils.signSortParamsMap(paramsMap))
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose(this))
                .subscribe(new DefaultObserver<BaseNetResponse<OrderItemBean>>() {
                    @Override
                    protected void onSuccess(BaseNetResponse<OrderItemBean> responseBaseResponse) {
                        try {
                            hideLoadingDialog();
                            //FileUtils.saveContentToSdcard("foodConsumePage.txt",JSON.toJSONString(responseBaseResponse) + "\n\n");
                            srlRecordList.finishRefresh();
                            srlRecordList.finishLoadMore();


                            if (responseBaseResponse.isTokenInvalid()){
                                CommonAlertDialogFragment.build()
                                        .setAlertTitleTxt("提示")
                                        .setAlertContentTxt("登录失效，请重新登录")
                                        .setLeftNavTxt("确定")
                                        .setLeftNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                                            @Override
                                            public void onClick(CommonAlertDialogFragment alertDialogFragment) {

                                                Intent loginIntent = new Intent(getActivity(), LoginLandActivity.class);
                                                loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(loginIntent);
                                                if (getActivity() instanceof Activity) {
                                                    getActivity().finish();
                                                }

                                            }
                                        })
                                        .show(mActivity);
                                return;
                            }

                            OrderItemBean responseData = responseBaseResponse.getData();
                            if (responseBaseResponse.isSuccess() && responseData != null) {

                                pageTotal = responseData.getPages();
                                List<OrderItemBean.RecordsBean> dataRecords = responseData.getRecords();

                                if (dataRecords != null && !dataRecords.isEmpty()) {

//                                    orderCompleteListAdapter.setUseEmpty(true);
//                                    orderCompleteListAdapter.setEmptyView(R.layout.layout_empty_view);

                                    if (pageIndex == 1) {
                                        orderCompleteListAdapter.getData().clear();
                                        orderCompleteListAdapter.notifyDataSetChanged();
                                    }
                                    orderCompleteListAdapter.addData(dataRecords);

                                } else {
                                    Log.d(TAG, "limeFoodConsumePageResponse: " + 358);
                                    if (pageIndex == 1) {
                                        orderCompleteListAdapter.getData().clear();
                                        orderCompleteListAdapter.notifyDataSetChanged();

                                    }else {
                                        AppToast.toastMsg("没有更多数据了");
                                    }

                                }
                            } else {
                                if (pageIndex == 1) {
                                    orderCompleteListAdapter.getData().clear();
                                    orderCompleteListAdapter.notifyDataSetChanged();

                                }else {
                                    AppToast.toastMsg("没有更多数据了");
                                }

                            }

                        }catch (Exception e){
                            Log.e(TAG, "limeFoodConsumePageResponse: " + e.getMessage());
                            AppToast.toastMsg("获取数据异常");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideLoadingDialog();
                        srlRecordList.finishRefresh();
                        srlRecordList.finishLoadMore();
                        CommonDialogUtils.showTipsDialog(mActivity, "请求数据失败!" + e.getMessage());
                    }
                });
    }

}
