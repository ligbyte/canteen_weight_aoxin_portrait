package com.stkj.aoxin.weight.pay.callback;

public interface OnHandListener {

    void onConfirmMoney(String payMoney);

    void onCancel();

    void onDeleteLongClickListener();
    default void onClickDisableConfirm() {

    }

}
