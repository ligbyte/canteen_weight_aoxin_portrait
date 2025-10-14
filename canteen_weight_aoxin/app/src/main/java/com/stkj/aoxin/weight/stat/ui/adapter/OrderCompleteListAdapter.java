package com.stkj.aoxin.weight.stat.ui.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.stkj.aoxin.weight.R;
import com.stkj.aoxin.weight.base.utils.PriceUtils;
import com.stkj.aoxin.weight.pay.model.OrderItemBean;

public class OrderCompleteListAdapter extends BaseQuickAdapter<OrderItemBean.RecordsBean, BaseViewHolder> {

    private Context context;

    public OrderCompleteListAdapter(Context context)
    {
        super(R.layout.item_order_complete_list_goods);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder holder, OrderItemBean.RecordsBean item) {
        holder.setText(R.id.tv_order_time, "下单时间:    " + item.getCreateTime());
        holder.setText(R.id.tv_order_no, "订单编号:    " + item.getOrderNo());
        holder.setText(R.id.tv_order_price, PriceUtils.formatPrice(item.getOrderFee()/100.0));
        holder.setText(R.id.tv_order_username, "发  起  人:    " + item.getCreateUserName());
        holder.setText(R.id.tv_order_create_time, item.getAcceptOrdersTime());
        holder.setText(R.id.tv_commany, "供货单位:    " + item.getSupplierName());
        holder.setText(R.id.tv_order_status, item.getOrderStatus() == 4 ? "已完成" : "待签收");
//        holder.setText(R.id.tv_qianshou, "签收");
    }
}
