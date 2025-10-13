package com.stkj.aoxin.weight.pay.model;

import java.util.List;

/**
 * 订单信息
 */
public class OrderItemBean {

    /**
     * records : [{"id":"1968216756009218050","tenantId":"1959817597411151873","extJson":null,"deleteFlag":null,"createTime":"2025-09-17 15:33:37","createUser":"1959817602163298305","updateTime":"2025-09-22 10:43:03","updateUser":"1959817602163298305","supplierId":"1968216382800048130","supplierName":"测试供应商","settlementDiscount":20,"schoolName":"新安县学校智慧食堂","orderStatus":4,"status":"已完成","orderStartDate":null,"orderEndDate":null,"orderNo":"1968216755946303490","orderFee":480,"createUserName":"刘总","acceptOrdersId":null,"acceptOrdersName":"供应商","deliveryId":null,"deliveryName":null,"deliveryPlateNumber":null,"acceptOrdersTime":"2025-09-22 10:42:27","deliveryTime":"2025-09-11 15:33:35","reviewOrderFee":480,"checkTime":"2025-09-22 10:43:03"},{"id":"1968220471432736769","tenantId":"1959817597411151873","extJson":null,"deleteFlag":null,"createTime":"2025-09-17 15:48:23","createUser":"1959817602163298305","updateTime":"2025-09-22 11:52:11","updateUser":"1959817602163298305","supplierId":"1968216382800048130","supplierName":"测试供应商","settlementDiscount":20,"schoolName":"新安县学校智慧食堂","orderStatus":4,"status":"已完成","orderStartDate":null,"orderEndDate":null,"orderNo":"1968220471382405121","orderFee":240,"createUserName":"刘总","acceptOrdersId":null,"acceptOrdersName":"供应商","deliveryId":null,"deliveryName":null,"deliveryPlateNumber":null,"acceptOrdersTime":"2025-09-22 11:41:26","deliveryTime":"2025-09-18 15:48:19","reviewOrderFee":240,"checkTime":"2025-09-22 11:52:11"},{"id":"1969970710263009281","tenantId":"1959817597411151873","extJson":null,"deleteFlag":null,"createTime":"2025-09-22 11:43:12","createUser":"1959817602163298305","updateTime":"2025-09-22 11:52:15","updateUser":"1959817602163298305","supplierId":"1968216382800048130","supplierName":"测试供应商","settlementDiscount":50,"schoolName":"新安县学校智慧食堂","orderStatus":4,"status":"已完成","orderStartDate":null,"orderEndDate":null,"orderNo":"1969970710229454849","orderFee":15225,"createUserName":"刘总","acceptOrdersId":null,"acceptOrdersName":"供应商","deliveryId":null,"deliveryName":null,"deliveryPlateNumber":null,"acceptOrdersTime":"2025-09-22 11:43:37","deliveryTime":"2025-09-26 15:47:10","reviewOrderFee":15225,"checkTime":"2025-09-22 11:52:15"},{"id":"1969971062752317442","tenantId":"1959817597411151873","extJson":null,"deleteFlag":null,"createTime":"2025-09-22 11:44:36","createUser":"1959817602163298305","updateTime":"2025-09-22 11:52:43","updateUser":"1959817602163298305","supplierId":"1968216382800048130","supplierName":"测试供应商","settlementDiscount":50,"schoolName":"新安县学校智慧食堂","orderStatus":4,"status":"已完成","orderStartDate":null,"orderEndDate":null,"orderNo":"1969971062689402882","orderFee":10000,"createUserName":"刘总","acceptOrdersId":null,"acceptOrdersName":"供应商","deliveryId":null,"deliveryName":null,"deliveryPlateNumber":null,"acceptOrdersTime":"2025-09-22 11:45:02","deliveryTime":"2025-09-28 15:40:16","reviewOrderFee":10000,"checkTime":"2025-09-22 11:52:43"}]
     * total : 4
     * size : 20
     * current : 1
     * orders : []
     * optimizeCountSql : true
     * searchCount : true
     * maxLimit : null
     * countId : null
     * pages : 1
     */

    private int total;
    private int size;
    private int current;
    private boolean optimizeCountSql;
    private boolean searchCount;
    private Object maxLimit;
    private Object countId;
    private int pages;
    private List<RecordsBean> records;
    private List<?> orders;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public boolean isOptimizeCountSql() {
        return optimizeCountSql;
    }

    public void setOptimizeCountSql(boolean optimizeCountSql) {
        this.optimizeCountSql = optimizeCountSql;
    }

    public boolean isSearchCount() {
        return searchCount;
    }

    public void setSearchCount(boolean searchCount) {
        this.searchCount = searchCount;
    }

    public Object getMaxLimit() {
        return maxLimit;
    }

    public void setMaxLimit(Object maxLimit) {
        this.maxLimit = maxLimit;
    }

    public Object getCountId() {
        return countId;
    }

    public void setCountId(Object countId) {
        this.countId = countId;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public List<RecordsBean> getRecords() {
        return records;
    }

    public void setRecords(List<RecordsBean> records) {
        this.records = records;
    }

    public List<?> getOrders() {
        return orders;
    }

    public void setOrders(List<?> orders) {
        this.orders = orders;
    }

    public static class RecordsBean {
        /**
         * id : 1968216756009218050
         * tenantId : 1959817597411151873
         * extJson : null
         * deleteFlag : null
         * createTime : 2025-09-17 15:33:37
         * createUser : 1959817602163298305
         * updateTime : 2025-09-22 10:43:03
         * updateUser : 1959817602163298305
         * supplierId : 1968216382800048130
         * supplierName : 测试供应商
         * settlementDiscount : 20
         * schoolName : 新安县学校智慧食堂
         * orderStatus : 4
         * status : 已完成
         * orderStartDate : null
         * orderEndDate : null
         * orderNo : 1968216755946303490
         * orderFee : 480
         * createUserName : 刘总
         * acceptOrdersId : null
         * acceptOrdersName : 供应商
         * deliveryId : null
         * deliveryName : null
         * deliveryPlateNumber : null
         * acceptOrdersTime : 2025-09-22 10:42:27
         * deliveryTime : 2025-09-11 15:33:35
         * reviewOrderFee : 480
         * checkTime : 2025-09-22 10:43:03
         */

        private String id;
        private String tenantId;
        private Object extJson;
        private Object deleteFlag;
        private String createTime;
        private String createUser;
        private String updateTime;
        private String updateUser;
        private String supplierId;
        private String supplierName;
        private int settlementDiscount;
        private String schoolName;
        private int orderStatus;
        private String status;
        private Object orderStartDate;
        private Object orderEndDate;
        private String orderNo;
        private int orderFee;
        private String createUserName;
        private Object acceptOrdersId;
        private String acceptOrdersName;
        private Object deliveryId;
        private Object deliveryName;
        private Object deliveryPlateNumber;
        private String acceptOrdersTime;
        private String deliveryTime;
        private int reviewOrderFee;
        private String checkTime;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTenantId() {
            return tenantId;
        }

        public void setTenantId(String tenantId) {
            this.tenantId = tenantId;
        }

        public Object getExtJson() {
            return extJson;
        }

        public void setExtJson(Object extJson) {
            this.extJson = extJson;
        }

        public Object getDeleteFlag() {
            return deleteFlag;
        }

        public void setDeleteFlag(Object deleteFlag) {
            this.deleteFlag = deleteFlag;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getCreateUser() {
            return createUser;
        }

        public void setCreateUser(String createUser) {
            this.createUser = createUser;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public String getUpdateUser() {
            return updateUser;
        }

        public void setUpdateUser(String updateUser) {
            this.updateUser = updateUser;
        }

        public String getSupplierId() {
            return supplierId;
        }

        public void setSupplierId(String supplierId) {
            this.supplierId = supplierId;
        }

        public String getSupplierName() {
            return supplierName;
        }

        public void setSupplierName(String supplierName) {
            this.supplierName = supplierName;
        }

        public int getSettlementDiscount() {
            return settlementDiscount;
        }

        public void setSettlementDiscount(int settlementDiscount) {
            this.settlementDiscount = settlementDiscount;
        }

        public String getSchoolName() {
            return schoolName;
        }

        public void setSchoolName(String schoolName) {
            this.schoolName = schoolName;
        }

        public int getOrderStatus() {
            return orderStatus;
        }

        public void setOrderStatus(int orderStatus) {
            this.orderStatus = orderStatus;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Object getOrderStartDate() {
            return orderStartDate;
        }

        public void setOrderStartDate(Object orderStartDate) {
            this.orderStartDate = orderStartDate;
        }

        public Object getOrderEndDate() {
            return orderEndDate;
        }

        public void setOrderEndDate(Object orderEndDate) {
            this.orderEndDate = orderEndDate;
        }

        public String getOrderNo() {
            return orderNo;
        }

        public void setOrderNo(String orderNo) {
            this.orderNo = orderNo;
        }

        public int getOrderFee() {
            return orderFee;
        }

        public void setOrderFee(int orderFee) {
            this.orderFee = orderFee;
        }

        public String getCreateUserName() {
            return createUserName;
        }

        public void setCreateUserName(String createUserName) {
            this.createUserName = createUserName;
        }

        public Object getAcceptOrdersId() {
            return acceptOrdersId;
        }

        public void setAcceptOrdersId(Object acceptOrdersId) {
            this.acceptOrdersId = acceptOrdersId;
        }

        public String getAcceptOrdersName() {
            return acceptOrdersName;
        }

        public void setAcceptOrdersName(String acceptOrdersName) {
            this.acceptOrdersName = acceptOrdersName;
        }

        public Object getDeliveryId() {
            return deliveryId;
        }

        public void setDeliveryId(Object deliveryId) {
            this.deliveryId = deliveryId;
        }

        public Object getDeliveryName() {
            return deliveryName;
        }

        public void setDeliveryName(Object deliveryName) {
            this.deliveryName = deliveryName;
        }

        public Object getDeliveryPlateNumber() {
            return deliveryPlateNumber;
        }

        public void setDeliveryPlateNumber(Object deliveryPlateNumber) {
            this.deliveryPlateNumber = deliveryPlateNumber;
        }

        public String getAcceptOrdersTime() {
            return acceptOrdersTime;
        }

        public void setAcceptOrdersTime(String acceptOrdersTime) {
            this.acceptOrdersTime = acceptOrdersTime;
        }

        public String getDeliveryTime() {
            return deliveryTime;
        }

        public void setDeliveryTime(String deliveryTime) {
            this.deliveryTime = deliveryTime;
        }

        public int getReviewOrderFee() {
            return reviewOrderFee;
        }

        public void setReviewOrderFee(int reviewOrderFee) {
            this.reviewOrderFee = reviewOrderFee;
        }

        public String getCheckTime() {
            return checkTime;
        }

        public void setCheckTime(String checkTime) {
            this.checkTime = checkTime;
        }
    }
}