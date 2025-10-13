package com.stkj.aoxin.weight.home.model;

public class CategoryItem {
    private String name;
    private int status = 0;
    
    public CategoryItem(String name, int status) {
        this.name = name;
        this.status = status;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}