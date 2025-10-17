package com.stkj.aoxin.weight.pay.model;

public class InitWeightEvent {

    private int flag = 0;

    public InitWeightEvent(int flag) {
        this.flag = flag;
    }


    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
