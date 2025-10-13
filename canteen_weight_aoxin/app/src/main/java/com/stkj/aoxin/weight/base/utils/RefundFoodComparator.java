package com.stkj.aoxin.weight.base.utils;

import com.stkj.aoxin.weight.pay.model.RefundFood;

import java.util.Comparator;

public class RefundFoodComparator implements Comparator<RefundFood> {
    @Override
    public int compare(RefundFood o1, RefundFood o2) {
        return o1.getFoodId().compareTo(o2.getFoodId());
    }
}
