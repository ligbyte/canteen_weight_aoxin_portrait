package com.stkj.aoxin.weight.home.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.stkj.aoxin.weight.R;
import com.stkj.aoxin.weight.home.model.CategoryItem;
import com.stkj.aoxin.weight.home.ui.activity.CheckActivity;
import com.stkj.aoxin.weight.pay.model.OrderInfoBean;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    
    private List<CategoryItem> categoryList;
    private OnCategoryClickListener listener;
    
    public interface OnCategoryClickListener {
        void onCategoryClick(int position, CategoryItem item);
    }
    
    public CategoryAdapter(List<CategoryItem> categoryList) {
        this.categoryList = categoryList;
    }
    
    public void setOnCategoryClickListener(OnCategoryClickListener listener) {
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        CategoryItem item = categoryList.get(position);
        holder.bind(item, position);
    }
    
    @Override
    public int getItemCount() {
        return categoryList.size();
    }
    
    public void updateSelection(int selectedPosition) {
        for (int i = 0; i < categoryList.size(); i++) {
            categoryList.get(i).setStatus(selectedPosition);
        }
        notifyDataSetChanged();
    }
    
    class CategoryViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCategoryName;
        
        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tv_category_name);
        }
        
        public void bind(CategoryItem item, int position) {
            tvCategoryName.setText(item.getName());

            // Set click listener
            tvCategoryName.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCategoryClick(position, item);
                }
            });

            // Set selection state
            if (item.getStatus() == 1 || item.getStatus() == 3) {
                tvCategoryName.setBackgroundResource(R.drawable.category_selected_bg);
                tvCategoryName.setTextColor(itemView.getContext().getResources().getColor(R.color.primary_blue, null));
            } else if (item.getStatus() == 2) {

                if (CheckActivity.supplyProductOrderDetailList != null){
                    OrderInfoBean.SupplyProductOrderDetailListBean product = CheckActivity.supplyProductOrderDetailList.get(position);
                    if (product.getPackageUnit().contains("公斤") || product.getPackageUnit().contains("斤") || product.getPackageUnit().contains("kg") || product.getPackageUnit().contains("g") || product.getPackageUnit().contains("克") || product.getPackageUnit().contains("千克")) {
                        if (product.getTareWeight() > (product.getPurchaseNumber() *0.3) || Math.abs(product.getNetWeightDifference()) > product.getPurchaseNumber() * 0.1) {
                            tvCategoryName.setBackgroundResource(R.drawable.category_unchecked_bg);
                            tvCategoryName.setTextColor(itemView.getContext().getResources().getColor(R.color.black, null));
                            return;
                        }

                    }else {
                        if (product.getGrossWeight() < (product.getPurchaseNumber() * 0.8) || product.getGrossWeight() > product.getPurchaseNumber()) {
                            tvCategoryName.setBackgroundResource(R.drawable.category_unchecked_bg);
                            tvCategoryName.setTextColor(itemView.getContext().getResources().getColor(R.color.black, null));
                            return;
                        }
                    }
                }

                tvCategoryName.setBackgroundResource(R.drawable.category_checked_bg);
                tvCategoryName.setTextColor(itemView.getContext().getResources().getColor(R.color.black, null));
            } else {
//
//                if (position < CheckActivity.currentIndex){
//                    tvCategoryName.setBackgroundResource(R.drawable.category_unchecked_bg);
//                    tvCategoryName.setTextColor(itemView.getContext().getResources().getColor(R.color.black, null));
//                }else {
//                    tvCategoryName.setBackgroundResource(R.drawable.category_unselected_bg);
//                    tvCategoryName.setTextColor(itemView.getContext().getResources().getColor(R.color.black, null));
//                }

                tvCategoryName.setBackgroundResource(R.drawable.category_unselected_bg);
                tvCategoryName.setTextColor(itemView.getContext().getResources().getColor(R.color.black, null));

            }
            

        }
    }
}