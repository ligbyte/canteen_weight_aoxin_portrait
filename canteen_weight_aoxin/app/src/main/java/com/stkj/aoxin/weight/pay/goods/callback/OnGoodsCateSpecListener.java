package com.stkj.aoxin.weight.pay.goods.callback;

public interface OnGoodsCateSpecListener {

    void onGetCateSpecListEnd();

    default void onGetCateSpecError(String msg) {

    }
}
