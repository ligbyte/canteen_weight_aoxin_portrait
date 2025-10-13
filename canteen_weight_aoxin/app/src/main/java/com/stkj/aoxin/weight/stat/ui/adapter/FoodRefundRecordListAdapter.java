package com.stkj.aoxin.weight.stat.ui.adapter;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.stkj.aoxin.weight.R;
import com.stkj.aoxin.weight.base.utils.PriceUtils;
import com.stkj.aoxin.weight.pay.model.RefundFood;
import com.stkj.common.utils.TimeUtils;

public class FoodRefundRecordListAdapter extends BaseQuickAdapter<RefundFood, BaseViewHolder> {

    public final static String TAG = "FoodRefundRecordListAdapter";
    private Context context;

    public FoodRefundRecordListAdapter(Context context)
    {
        super(R.layout.item_order_food_refund);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder holder, RefundFood item) {
            holder.setText(R.id.tv_time, TimeUtils.millis2String(item.getCreateTime()));
            holder.setText(R.id.tv_name, item.getFoodName());
            holder.setText(R.id.tv_price, PriceUtils.formatPrice(item.getFoodUnitPrice()/100.00));
            if (item.getFoodMethod() == 2){
                holder.setText(R.id.tv_count, "1");
                holder.setText(R.id.tv_money, PriceUtils.formatPrice(item.getFoodUnitPrice()/100.0 * item.getFoodWeight()));
            }else {
                holder.setText(R.id.tv_count, String.valueOf(item.getFoodCount()));
                holder.setText(R.id.tv_money, PriceUtils.formatPrice(item.getFoodUnitPrice()/100.0 * item.getFoodCount()));

            }

    }
}
