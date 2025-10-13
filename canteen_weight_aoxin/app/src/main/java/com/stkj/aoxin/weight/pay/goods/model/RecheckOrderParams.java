package com.stkj.aoxin.weight.pay.goods.model;

import com.stkj.aoxin.weight.pay.model.OrderInfoBean;

import java.util.List;

/**
 * 复核订单请求参数
 */
public class RecheckOrderParams {
    private String id;
    private  List<OrderInfoBean.SupplyProductOrderDetailListBean> supplyProductRecheckData;


    public RecheckOrderParams(String id, List<OrderInfoBean.SupplyProductOrderDetailListBean> supplyProductRecheckData) {
        this.id = id;
        this.supplyProductRecheckData = supplyProductRecheckData;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<OrderInfoBean.SupplyProductOrderDetailListBean> getSupplyProductRecheckData() {
        return supplyProductRecheckData;
    }

    public void setSupplyProductRecheckData(List<OrderInfoBean.SupplyProductOrderDetailListBean> supplyProductRecheckData) {
        this.supplyProductRecheckData = supplyProductRecheckData;
    }
}
