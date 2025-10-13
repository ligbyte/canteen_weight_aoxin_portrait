package com.stkj.aoxin.weight.pay.callback;

public interface OnCalculateListener {

    void onConfirmMoney(String payMoney);

    void onCancel();

    default void onClickDisableConfirm() {

    }

}
