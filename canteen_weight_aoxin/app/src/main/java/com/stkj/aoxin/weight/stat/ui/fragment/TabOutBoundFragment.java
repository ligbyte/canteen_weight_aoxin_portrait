package com.stkj.aoxin.weight.stat.ui.fragment;

import android.view.View;

import com.stkj.aoxin.weight.R;
import com.stkj.aoxin.weight.base.model.BaseNetResponse;
import com.stkj.aoxin.weight.base.net.ParamsUtils;
import com.stkj.aoxin.weight.base.utils.EventBusUtils;
import com.stkj.aoxin.weight.pay.model.ConsumerSuccessEvent;
import com.stkj.aoxin.weight.stat.model.CanteenSummary;
import com.stkj.aoxin.weight.stat.service.StatService;
import com.stkj.common.net.retrofit.RetrofitManager;
import com.stkj.common.rx.AutoDisposeUtils;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;
import com.stkj.common.ui.fragment.BaseRecyclerFragment;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.TreeMap;

/**
 * 出库页面
 */
public class TabOutBoundFragment extends BaseRecyclerFragment {

//    private AppSmartRefreshLayout srlRecordList;
//    private RecyclerView rvContent;
//    private BarChart chartConsumerNumber;
//    private TextView tvConsumerNumber;
//    private StatPieChart chartConsumerAmount;
//    private TextView tvConsumerAmount;
//    private CommonRecyclerAdapter orderListAdapter;
//    //网络请求查询参数
//    private ConsumerRecordListQueryInfo queryInfo = new ConsumerRecordListQueryInfo();
//    private int mLastRequestPage;
//    private boolean needRefreshStatData;

    private void resetRequestPage() {
//        mLastRequestPage = 0;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_out_bound;
    }

    @Override
    protected void initViews(View rootView) {
//        srlRecordList = (AppSmartRefreshLayout) findViewById(R.id.srl_record_list);
//        rvContent = (RecyclerView) findViewById(R.id.rv_content);
//        chartConsumerNumber = (BarChart) findViewById(R.id.chart_consumer_number);
//        tvConsumerNumber = (TextView) findViewById(R.id.tv_consumer_number);
//        chartConsumerAmount = (StatPieChart) findViewById(R.id.chart_consumer_amount);
//        tvConsumerAmount = (TextView) findViewById(R.id.tv_consumer_amount);
//        srlRecordList.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
//            @Override
//            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
//                getOrderList();
//            }
//
//            @Override
//            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
//                resetRequestPage();
//                queryInfo.resetDefaultData();
//                getOrderList();
//            }
//        });
//        orderListAdapter = new CommonRecyclerAdapter(false);
//        orderListAdapter.addViewHolderFactory(new StatConsumerRecordListItemViewHolder.Factory());
//        orderListAdapter.addItemEventListener(new CommonRecyclerAdapter.OnItemEventListener() {
//            @Override
//            public void onCustomItemEvent(CommonRecyclerAdapter commonRecyclerAdapter, CommonRecyclerViewHolder viewHolder, int eventId, Object obj) {
//                ConsumerRecordListInfo consumerRecordListInfo = (ConsumerRecordListInfo) obj;
//                if (eventId == StatConsumerRecordListItemViewHolder.EVENT_CLICK) {
//                    CommonDialogUtils.showTipsDialog(mActivity, "账单编号:" + consumerRecordListInfo.getId() +
//                            "\n餐别:" + PayConstants.getFeeTypeStr(consumerRecordListInfo.getFeeType()) +
//                            "\n支付方式:" + PayConstants.getPayTypeStr(consumerRecordListInfo.getConsumeMethod()) +
//                            "\n金额:" + consumerRecordListInfo.getBizAmount() +
//                            "\n支付时间:" + consumerRecordListInfo.getBizDate() +
//                            "\n姓名:" + consumerRecordListInfo.getFull_Name() +
//                            "\n卡号:" + consumerRecordListInfo.getCard_Number() +
//                            "\n手机号:" + consumerRecordListInfo.getUser_Tel());
//                }
//            }
//        });
//        rvContent.setAdapter(orderListAdapter);
    }

    private boolean isOnPause;

    @Override
    protected void onFragmentResume(boolean isFirstOnResume) {

    }

    @Override
    public void onPause() {
        super.onPause();
        isOnPause = true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshConsumerRecordList(ConsumerSuccessEvent eventBus) {

    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBusUtils.unRegisterEventBus(this);
    }

//    private void getOrderList() {
//        int currentPage = mLastRequestPage + 1;
//        queryInfo.setPageIndex(currentPage);
//        TreeMap<String, String> paramsMap = ParamsUtils.newSortParamsMapWithMode("ConsumeRecordList");
//        paramsMap.put("pageIndex", String.valueOf(queryInfo.getPageIndex()));
//        paramsMap.put("pageSize", String.valueOf(queryInfo.getPageSize()));
//        RetrofitManager.INSTANCE.getDefaultRetrofit()
//                .create(PayService.class)
//                .getConsumerRecordList(ParamsUtils.signSortParamsMap(paramsMap))
//                .compose(RxTransformerUtils.mainSchedulers())
//                .to(AutoDisposeUtils.onDestroyDispose(this))
//                .subscribe(new DefaultObserver<BaseNetResponse<ConsumerRecordListResponse>>() {
//                    @Override
//                    protected void onSuccess(BaseNetResponse<ConsumerRecordListResponse> responseBaseResponse) {
//                        hideLoadingDialog();
//                        srlRecordList.finishRefresh();
//                        srlRecordList.finishLoadMore();
//                        //第一页清空旧数据
//                        if (currentPage == 1) {
//                            orderListAdapter.removeAllData();
//                        }
//                        ConsumerRecordListResponse responseData = responseBaseResponse.getData();
//                        if (responseBaseResponse.isSuccess() && responseData != null) {
//                            List<ConsumerRecordListInfo> dataRecords = responseData.getResults();
//                            if (dataRecords != null && !dataRecords.isEmpty()) {
//                                mLastRequestPage = currentPage;
//                                orderListAdapter.addDataList(dataRecords);
//                            } else {
//                                AppToast.toastMsg("没有更多数据了");
//                            }
//                        } else {
//                            AppToast.toastMsg("没有更多数据了");
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        hideLoadingDialog();
//                        srlRecordList.finishRefresh();
//                        srlRecordList.finishLoadMore();
//                        CommonDialogUtils.showTipsDialog(mActivity, "请求数据失败!" + e.getMessage());
//                    }
//                });
//    }

    /**
     * 获取餐厅统计信息
     */
    private void getCanteenSummary() {
        TreeMap<String, String> paramsMap = ParamsUtils.newSortParamsMapWithMode("Canteen_summary");
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(StatService.class)
                .getCanteenSummary(ParamsUtils.signSortParamsMap(paramsMap))
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose(this))
                .subscribe(new DefaultObserver<BaseNetResponse<CanteenSummary>>() {
                    @Override
                    protected void onSuccess(BaseNetResponse<CanteenSummary> baseNetResponse) {
                        if (baseNetResponse.isSuccess()) {
//                            CanteenSummary data = baseNetResponse.getData();
//                            List<CanteenSummary.ConsumeMethodList> consumeMethodList = data.getConsumeMethodList();
//                            if (consumeMethodList != null) {
//                                refreshChartAmount(consumeMethodList);
//                            }
//                            List<CanteenSummary.FeeTypeList> feeTypeList = data.getFeeTypeList();
//                            if (feeTypeList != null) {
//                                refreshChartNumber(feeTypeList);
//                            }
                        }
                    }
                });
    }



}
