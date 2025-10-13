package com.stkj.aoxin.weight.pay.goods.callback;


import com.stkj.aoxin.weight.pay.goods.model.GoodsSpec;

import java.util.List;

public interface OnGetGoodsSpecListListener {
    void onGetSpecListSuccess(int goodsType, List<GoodsSpec> goodsSpecList);

    default void onGetSpecListError(int goodsType, String msg) {

    }
}
