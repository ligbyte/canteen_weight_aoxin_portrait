package com.stkj.aoxin.weight.pay.goods.callback;



import com.stkj.aoxin.weight.pay.model.FoodConsumeBean;

import java.util.List;

public interface OrderNameAutoSearchListener {
    void onStartGetGoodsItemDetail(FoodConsumeBean goodsIdBaseListInfo);

    void onSuccessGetGoodsItemDetail(FoodConsumeBean saleListInfo);

    void onErrorGetGoodsItemDetail(FoodConsumeBean goodsIdBaseListInfo, String msg);

    void onSearchGoodsList(String key, List<FoodConsumeBean> goodsIdBaseListInfoList);
}
