package com.stkj.aoxin.weight.pay.goods.callback;


import com.stkj.aoxin.weight.pay.goods.model.GoodsEditBaseInfo;
import com.stkj.aoxin.weight.pay.goods.model.GoodsQrCodeDetail;

public interface GoodsDetailViewController {

    GoodsEditBaseInfo getGoodsDetailEditInfo();

    void setGoodsDetailEditMode();

    void setGoodsQrCodeInfo(GoodsQrCodeDetail goodsQrCodeInfo);

}
