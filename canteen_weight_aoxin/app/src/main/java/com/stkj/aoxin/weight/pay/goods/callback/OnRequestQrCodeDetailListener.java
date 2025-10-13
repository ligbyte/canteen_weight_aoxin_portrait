package com.stkj.aoxin.weight.pay.goods.callback;


import com.stkj.aoxin.weight.pay.goods.model.GoodsQrCodeDetail;

public interface OnRequestQrCodeDetailListener {

    void onRequestDetailSuccess(GoodsQrCodeDetail data);

    void onRequestDetailError(String qrcode, String msg);
}
