package com.stkj.aoxin.weight.base.utils;

import com.stkj.aoxin.weight.pay.model.CategoryBean;

import java.util.Comparator;

public class CategoryComparator implements Comparator<CategoryBean> {
    @Override
    public int compare(CategoryBean o1, CategoryBean o2) {
        return o1.getName().compareTo(o2.getName());
    }
}
