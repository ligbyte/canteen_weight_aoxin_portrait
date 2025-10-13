package com.stkj.aoxin.weight.base.ui.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.stkj.aoxin.weight.AppApplication;
import com.stkj.aoxin.weight.R;
import com.stkj.aoxin.weight.base.model.FaceChooseItemEntity;
import com.stkj.aoxin.weight.base.utils.PriceUtils;
import com.stkj.aoxin.weight.base.utils.StarUtils;
import com.stkj.aoxin.weight.home.ui.activity.CheckActivity;
import com.stkj.aoxin.weight.pay.model.OrderInfoBean;
import com.stkj.aoxin.weight.utils.OccurrenceConverter;

public class GoodsCountAdapter extends BaseQuickAdapter<OrderInfoBean.SupplyProductOrderDetailListBean, BaseViewHolder> {

    private Context context;

    public GoodsCountAdapter(Context context) {
        super(R.layout.adapter_goods_count);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder holder, OrderInfoBean.SupplyProductOrderDetailListBean item) {
        holder.setText(R.id.tv_review_gross_summary, OccurrenceConverter.getOccurrence(getItemPosition(item)) + "毛重");
        holder.setText(R.id.tv_review_trae_summary, OccurrenceConverter.getOccurrence(getItemPosition(item)) + "皮重");
        holder.setText(R.id.tv_review_gross_value, PriceUtils.formatPrice(item.getGrossWeight()) + CheckActivity.globalUnit);
        holder.setText(R.id.tv_review_tare_value, PriceUtils.formatPrice(item.getTareWeight()) + CheckActivity.globalUnit);
    }
}
