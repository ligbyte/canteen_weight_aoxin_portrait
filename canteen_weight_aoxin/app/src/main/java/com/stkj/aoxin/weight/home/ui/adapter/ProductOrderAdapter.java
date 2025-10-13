package com.stkj.aoxin.weight.home.ui.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stkj.aoxin.weight.R;
import com.stkj.aoxin.weight.home.ui.activity.CheckActivity;
import com.stkj.aoxin.weight.home.ui.activity.OrderActivity;
import com.stkj.aoxin.weight.pay.model.OrderInfoBean;
import com.stkj.common.ui.widget.common.RoundImageView;

import java.text.DecimalFormat;
import java.util.List;

public class ProductOrderAdapter extends RecyclerView.Adapter<ProductOrderAdapter.ViewHolder> {

    
    private static final String TAG = "ProductOrderAdapter";
    private List<OrderInfoBean.SupplyProductOrderDetailListBean> productList;
    private OnItemClickListener listener;
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");
    
    // Photo types
    public static final int PHOTO_TYPE_PRODUCT = 0;
    public static final int PHOTO_TYPE_PACKAGE = 1;

    public interface OnItemClickListener {
        void onPhotoClick(int position, int photoType);
        void onQuantityChange(int position, double newQuantity);
        void onRestockClick(int position);
    }

    public ProductOrderAdapter(List<OrderInfoBean.SupplyProductOrderDetailListBean> productList, OnItemClickListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderInfoBean.SupplyProductOrderDetailListBean product = productList.get(position);
        
        // Set product image (placeholder for now)
//        holder.ivProduct.setImageResource(R.drawable.photo_placeholder);
        
        // Set product details
        holder.tvProductName.setText(product.getProductName());
        holder.tvBrand.setText("品牌：" + (TextUtils.isEmpty(product.getProductBranch()) ? "--" : product.getProductBranch()));
        holder.tvQuality.setText("质量等级：" + "--");
        holder.tvUnitPrice.setText("单价：" + DECIMAL_FORMAT.format(product.getUnitPrice()/100.0) + " 元/" + product.getPackageUnit());
        holder.tvOrigin.setText("产地：" + (TextUtils.isEmpty(product.getProductAddr()) ? "--" : product.getProductAddr()));
        holder.tvPackaging.setText("包装规格：" + product.getSpecification());
        
        // Set quantity and amount

        if (product.getPackageUnit().contains("公斤") || product.getPackageUnit().contains("斤") || product.getPackageUnit().contains("kg") || product.getPackageUnit().contains("g") || product.getPackageUnit().contains("克") || product.getPackageUnit().contains("千克")) {
            holder.tvOrderQuantity.setText("订单重量：" + DECIMAL_FORMAT.format(product.getPurchaseNumber()) + "" + product.getPackageUnit());
            holder.tvVerifiedQuantity.setText("复核重量：" + DECIMAL_FORMAT.format(product.getReviewNumber()) + "" + product.getPackageUnit());
        }else {
            holder.tvOrderQuantity.setText("订单数量：" + product.getPurchaseNumber() + "" + product.getPackageUnit());
            holder.tvVerifiedQuantity.setText("复核数量：" + product.getReviewNumber() + "" + product.getPackageUnit());
        }




        holder.tvOrderAmount.setText("订单金额：¥" + DECIMAL_FORMAT.format(product.getOrderFee() / 100.0));
        holder.tvVerifiedAmount.setText("复核金额：¥" + DECIMAL_FORMAT.format(product.getReviewFee()));

        // Set verified quantity and amount
//        if (product.isReturned()) {
//            holder.tvVerifiedQuantity.setText("复核数量：--");
//            holder.tvVerifiedAmount.setText("复核金额：¥--");
//        } else {
//            holder.tvVerifiedQuantity.setText("复核数量：" + DECIMAL_FORMAT.format(product.getVerifiedQuantity()) + product.getUnit());
//            holder.tvVerifiedAmount.setText("复核金额：¥" + DECIMAL_FORMAT.format(product.getVerifiedAmount()));
//        }
        
        // Set photos
        if (!TextUtils.isEmpty(product.getReviewImageUrl()) && !product.getReviewImageUrl().equals("--")) {
            holder.ivPhoto1.setImageBitmap(BitmapFactory.decodeFile(product.getReviewImageUrl()));
            holder.ivMode.setVisibility(View.VISIBLE);
            if (CheckActivity.currentMode == 1){
              holder.ivMode.setImageResource(R.mipmap.ic_use_mode);
            }else {
                holder.ivMode.setImageResource(R.mipmap.ic_ruku_mode);
            }
        } else {
            holder.ivMode.setVisibility(View.GONE);
            holder.ivPhoto1.setImageResource(R.mipmap.img_default);
        }

        Log.d(TAG, "limeonBindViewHolder: " + product.getPassImageUrl());
        
        if (!TextUtils.isEmpty(product.getPassImageUrl()) && !product.getPassImageUrl().equals("--")) {
            holder.ivPhoto2.setImageBitmap(BitmapFactory.decodeFile(product.getPassImageUrl()));
        } else {
            holder.ivPhoto2.setImageResource(R.mipmap.img_default);
        }
        
        // Set photo click listeners
        holder.ivPhoto1.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPhotoClick(position, PHOTO_TYPE_PRODUCT);
            }
        });
        
        holder.ivPhoto2.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPhotoClick(position, PHOTO_TYPE_PACKAGE);
            }
        });
        
        // Handle returned items differently
//        if (product.isReturned()) {
//            // Add visual indication for returned items
//            holder.itemView.setAlpha(0.7f);
//            holder.tvVerifiedQuantity.setTextColor(holder.itemView.getContext().getColor(R.color.text_gray));
//            holder.tvVerifiedAmount.setTextColor(holder.itemView.getContext().getColor(R.color.text_gray));
//        } else {
//            holder.itemView.setAlpha(1.0f);
//            holder.tvVerifiedQuantity.setTextColor(holder.itemView.getContext().getColor(R.color.button_blue));
//            holder.tvVerifiedAmount.setTextColor(holder.itemView.getContext().getColor(R.color.button_blue));
//        }
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }
    
    public void setProductPhoto(int position, int photoType, Bitmap photo) {
        if (position >= 0 && position < productList.size()) {
            OrderInfoBean.SupplyProductOrderDetailListBean product = productList.get(position);
//            if (photoType == PHOTO_TYPE_PRODUCT) {
//                product.setProductPhoto(photo);
//            } else if (photoType == PHOTO_TYPE_PACKAGE) {
//                product.setPackagePhoto(photo);
//            }
            notifyItemChanged(position);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        RoundImageView ivProduct;
        TextView tvProductName;
        TextView tvBrand;
        TextView tvQuality;
        TextView tvUnitPrice;
        TextView tvOrigin;
        TextView tvPackaging;
        TextView tvOrderQuantity;
        TextView tvVerifiedQuantity;
        TextView tvOrderAmount;
        TextView tvVerifiedAmount;

        ImageView ivMode;
        RoundImageView ivPhoto1;
        RoundImageView ivPhoto2;
        View layoutPhoto2Container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            
            ivProduct = itemView.findViewById(R.id.iv_product);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvBrand = itemView.findViewById(R.id.tv_brand);
            tvQuality = itemView.findViewById(R.id.tv_quality);
            tvUnitPrice = itemView.findViewById(R.id.tv_unit_price);
            tvOrigin = itemView.findViewById(R.id.tv_origin);
            tvPackaging = itemView.findViewById(R.id.tv_packaging);
            tvOrderQuantity = itemView.findViewById(R.id.tv_order_quantity);
            tvVerifiedQuantity = itemView.findViewById(R.id.tv_verified_quantity);
            tvOrderAmount = itemView.findViewById(R.id.tv_order_amount);
            tvVerifiedAmount = itemView.findViewById(R.id.tv_verified_amount);
            ivPhoto1 = itemView.findViewById(R.id.iv_photo1);
            ivPhoto2 = itemView.findViewById(R.id.iv_photo2);
            layoutPhoto2Container = itemView.findViewById(R.id.layout_photo2_container);
            ivMode = itemView.findViewById(R.id.iv_mode);
        }
    }
}