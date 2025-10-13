package com.stkj.aoxin.weight.pay.goods.callback;


import com.stkj.aoxin.weight.setting.model.FoodInfoTable;

import java.util.List;

public interface GoodsAutoSearchListener {
    void onStartGetGoodsItemDetail(FoodInfoTable goodsIdBaseListInfo);

    void onSuccessGetGoodsItemDetail(FoodInfoTable saleListInfo);

    void onErrorGetGoodsItemDetail(FoodInfoTable goodsIdBaseListInfo, String msg);

    void onSearchGoodsList(String key, List<FoodInfoTable> goodsIdBaseListInfoList);
}
