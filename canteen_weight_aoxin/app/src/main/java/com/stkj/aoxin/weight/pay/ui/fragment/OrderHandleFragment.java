package com.stkj.aoxin.weight.pay.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.stkj.aoxin.weight.base.device.DeviceManager;
import com.stkj.aoxin.weight.base.model.BaseNetResponse;
import com.stkj.aoxin.weight.base.net.ParamsUtils;
import com.stkj.aoxin.weight.base.ui.dialog.CommonAlertDialogFragment;
import com.stkj.aoxin.weight.base.utils.CommonDialogUtils;
import com.stkj.aoxin.weight.home.ui.activity.LoginLandActivity;
import com.stkj.aoxin.weight.machine.utils.ToastUtils;
import com.stkj.aoxin.weight.pay.model.DeviceFoodConsumePageParam;
import com.stkj.aoxin.weight.pay.model.FoodConsumeBean;
import com.stkj.aoxin.weight.pay.model.FoodConsumePageResponse;
import com.stkj.aoxin.weight.pay.model.OrderCheckSuccesskEvent;
import com.stkj.aoxin.weight.pay.service.PayService;
import com.stkj.common.net.retrofit.RetrofitManager;
import com.stkj.common.rx.AutoDisposeUtils;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;
import com.stkj.common.ui.fragment.BaseRecyclerFragment;
import com.stkj.aoxin.weight.R;
import com.stkj.aoxin.weight.base.ui.widget.AppSmartRefreshLayout;
import com.stkj.aoxin.weight.base.utils.EventBusUtils;
import com.stkj.aoxin.weight.home.ui.activity.CheckActivity;
import com.stkj.aoxin.weight.pay.model.OrderItemBean;
import com.stkj.aoxin.weight.pay.model.RefreshConsumerGoodsModeEvent;
import com.stkj.aoxin.weight.stat.ui.adapter.OrderHandleListAdapter;
import com.stkj.common.ui.toast.AppToast;
import com.stkj.common.utils.AndroidUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Base64;
import java.util.List;
import java.util.TreeMap;


/**
 * 待签收页面
 */
public class OrderHandleFragment extends BaseRecyclerFragment {


    public final String TAG = "OrderHandleFragment";

    private int pageTotal = 1;
    private int pageIndex = 1;

    private AppSmartRefreshLayout srlRecordList;
    private RecyclerView rvContent;
    private OrderHandleListAdapter orderHandleListAdapter;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_order_handle;
    }

    @Override
    protected void initViews(View rootView) {
        srlRecordList = (AppSmartRefreshLayout) findViewById(R.id.srl_record_list);
        rvContent = (RecyclerView) findViewById(R.id.rv_content);

        srlRecordList.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                pageIndex += 1;
                getOrderHandleList(false);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                pageIndex = 1;
                getOrderHandleList(false);
            }
        });

        orderHandleListAdapter = new OrderHandleListAdapter(getActivity());

//        orderHandleListAdapter.setOnItemClickListener(new OnItemClickListener() {
//            @Override
//            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
//                Intent intent = new Intent(getActivity(), CheckActivity.class);
//                intent.putExtra("orderID", orderHandleListAdapter.getData().get(position).getId());
//                getActivity().startActivity(intent);
//            }
//        });

        rvContent.setAdapter(orderHandleListAdapter);
        rvContent.setItemAnimator(null);
        rvContent.setItemViewCacheSize(12);
        rvContent.setHasFixedSize(true);

        orderHandleListAdapter.setUseEmpty(true);
        orderHandleListAdapter.setEmptyView(R.layout.layout_empty_view);

        orderHandleListAdapter.addChildClickViewIds(R.id.tv_qianshou);
        orderHandleListAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                Intent intent = new Intent(getActivity(), CheckActivity.class);
                intent.putExtra("orderID", orderHandleListAdapter.getData().get(position).getId());
                getActivity().startActivity(intent);
            }
        });

        pageIndex = 1;
        getOrderHandleList(true);
    }

    @Override
    protected void onFragmentResume(boolean isFirstOnResume) {
        EventBusUtils.registerEventBus(this);



    }


    private void getOrderHandleList(boolean showLoadingDialog) {
        Log.d(TAG, "limegetOrderHandleList pageIndex: " + pageIndex);
        if (pageIndex > 1 && pageIndex  > pageTotal){
            hideLoadingDialog();
            srlRecordList.finishRefresh();
            srlRecordList.finishLoadMore();
            AppToast.toastMsg("没有更多数据了!");
            return;
        }

        Log.d(TAG, "limegetOrderHandleList: " + 293);
        TreeMap<String, String> paramsMap = ParamsUtils.newSortParamsMapWithMode("pageOrder");
        paramsMap.put("current",String.valueOf(pageIndex));
        paramsMap.put("size",String.valueOf(20));
        paramsMap.put("orderStatus",String.valueOf(3));


        if (showLoadingDialog) {
            showLoadingDialog("Loading", "加载中");
        }
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(PayService.class)
                .pageOrder(ParamsUtils.signSortParamsMap(paramsMap))
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose(this))
                .subscribe(new DefaultObserver<BaseNetResponse<OrderItemBean>>() {
                    @Override
                    protected void onSuccess(BaseNetResponse<OrderItemBean> responseBaseResponse) {
                        try {
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

//                                    orderHandleListAdapter.setUseEmpty(true);
//                                    orderHandleListAdapter.setEmptyView(R.layout.layout_empty_view);

                                    if (pageIndex == 1) {
                                        orderHandleListAdapter.getData().clear();
                                        orderHandleListAdapter.notifyDataSetChanged();
                                    }
                                    orderHandleListAdapter.addData(dataRecords);

                                } else {
                                    Log.d(TAG, "limeFoodConsumePageResponse: " + 358);
                                    if (pageIndex == 1) {
                                        orderHandleListAdapter.getData().clear();
                                        orderHandleListAdapter.notifyDataSetChanged();

                                    }else {
                                        AppToast.toastMsg("没有更多数据了");
                                    }

                                }
                            } else {
                                if (pageIndex == 1) {
                                    orderHandleListAdapter.getData().clear();
                                    orderHandleListAdapter.notifyDataSetChanged();

                                }else {
                                    AppToast.toastMsg("没有更多数据了");
                                }

                            }

                        }catch (Exception e){
                            Log.e(TAG, "limeFoodConsumePageResponse: " + e.getMessage());
                            AppToast.toastMsg("获取数据异常");
                        }finally {
                            hideLoadingDialog();
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


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOrderCheckSuccesskEvent(OrderCheckSuccesskEvent eventBus) {
        getOrderHandleList(false);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        EventBusUtils.unRegisterEventBus(this);
    }


}
