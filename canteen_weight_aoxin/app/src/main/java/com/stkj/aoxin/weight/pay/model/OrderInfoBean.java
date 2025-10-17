package com.stkj.aoxin.weight.pay.model;

import android.text.TextUtils;

import java.util.List;

public class OrderInfoBean {
    /**
     * id : 1969972139170107394
     * createTime : 2025-09-22 11:48:53
     * supplierId : 1968216382800048130
     * supplierName : 测试供应商
     * settlementDiscount : 50
     * schoolName : 新安县学校智慧食堂
     * orderStatus : 3
     * orderNo : 1969972139111387137
     * orderFee : 25000
     * createUserName : 刘总
     * acceptOrdersId : null
     * acceptOrdersName : 供应商
     * deliveryId : null
     * deliveryName : null
     * deliveryPlateNumber : null
     * acceptOrdersTime : 2025-09-22 11:49:05
     * deliveryTime : 2025-09-25 11:48:48
     * reviewOrderFee : null
     * checkTime : null
     * supplyProductOrderDetailList : [{"id":"1969972139186884609","orderId":"1969972139170107394","productId":"1968562019479453697","productName":"测试规格123","measurementUnit":"元","packageNumber":10,"packageUnit":"公斤","unitPrice":5000,"purchaseNumber":3,"orderFee":15000,"reviewNumber":null,"reviewFee":null,"reviewImageUrl":null,"passImageUrl":null,"specification":"袋/kg"},{"id":"1969972139195273217","orderId":"1969972139170107394","productId":"1968558416484237314","productName":"测试用商品","measurementUnit":"元","packageNumber":10,"packageUnit":"公斤","unitPrice":5000,"purchaseNumber":2,"orderFee":10000,"reviewNumber":null,"reviewFee":null,"reviewImageUrl":null,"passImageUrl":null,"specification":"袋/kg"}]
     */

    private String id;
    private String createTime;
    private String supplierId;
    private String supplierName;
    private int settlementDiscount;
    private String schoolName;
    private int orderStatus;
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
    private Object reviewOrderFee;
    private Object checkTime;
    private List<SupplyProductOrderDetailListBean> supplyProductOrderDetailList;


    public OrderInfoBean() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
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

    public Object getReviewOrderFee() {
        return reviewOrderFee;
    }

    public void setReviewOrderFee(Object reviewOrderFee) {
        this.reviewOrderFee = reviewOrderFee;
    }

    public Object getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(Object checkTime) {
        this.checkTime = checkTime;
    }

    public List<SupplyProductOrderDetailListBean> getSupplyProductOrderDetailList() {
        return supplyProductOrderDetailList;
    }

    public void setSupplyProductOrderDetailList(List<SupplyProductOrderDetailListBean> supplyProductOrderDetailList) {
        this.supplyProductOrderDetailList = supplyProductOrderDetailList;
    }

    public static class SupplyProductOrderDetailListBean {
        /**
         * id : 1969972139186884609
         * orderId : 1969972139170107394
         * productId : 1968562019479453697
         * productName : 测试规格123
         * measurementUnit : 元
         * packageNumber : 10
         * packageUnit : 公斤
         * unitPrice : 5000
         * purchaseNumber : 3.0
         * orderFee : 15000
         * reviewNumber : null
         * reviewFee : null
         * reviewImageUrl : null
         * passImageUrl : null
         * specification : 袋/kg
         */

        private String id;
        private String orderId;

        private String productImageUrl;
        private String productId;
        private String productName;
        private String measurementUnit;
        private int packageNumber;
        private String packageUnit;
        private String pricingUnit;
        private int unitPrice;
        private double purchaseNumber;
        private int orderFee;

        private int quPiType = -1;
        private double reviewNumber;
        private double reviewFee;
        private String reviewImageUrl;
        private String passImageUrl;
        private String specification;

        private String productAddr;

        private String productBranch;

        public int getQuPiType() {
            return quPiType;
        }

        public void setQuPiType(int quPiType) {
            this.quPiType = quPiType;
        }

        /**
         * 最后一次皮重
         */
        private double lastTareWeight;

        /**
         * 最后一次毛重
         */
        private double lastGrossWeight;

        /**
         * 毛重、皮重列表
         */
        private String supplyProductOrderDetailList;

        /**
         * 皮重
         */
        private double tareWeight;

        /**
         * 毛重
         */
        private double grossWeight;

        /**
         * 净重差额
         */
        private double netWeightDifference;



        /**
         * 误差金额
         */
        private double differenceAmount;


        /**
         * 签收类型
         */
        private int checkType = 0;

        public double getGrossWeight() {
            return grossWeight;
        }

        public void setGrossWeight(double grossWeight) {
            this.grossWeight = grossWeight;
        }

        public String getPricingUnit() {
            return pricingUnit;
        }

        public void setPricingUnit(String pricingUnit) {
            this.pricingUnit = pricingUnit;
        }

        public int getCheckType() {
            return checkType;
        }

        public void setCheckType(int checkType) {
            this.checkType = checkType;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getProductImageUrl() {
            return productImageUrl;
        }

        public void setProductImageUrl(String productImageUrl) {
            this.productImageUrl = productImageUrl;
        }

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getMeasurementUnit() {
            return measurementUnit;
        }

        public void setMeasurementUnit(String measurementUnit) {
            this.measurementUnit = measurementUnit;
        }

        public int getPackageNumber() {
            return packageNumber;
        }

        public void setPackageNumber(int packageNumber) {
            this.packageNumber = packageNumber;
        }

        public String getPackageUnit() {
//            if ( TextUtils.isEmpty(packageUnit) || packageUnit.equals("kg")){
//                return  "公斤";
//            }
            return packageUnit;
        }

        public void setPackageUnit(String packageUnit) {
            this.packageUnit = packageUnit;
        }

        public int getUnitPrice() {
            return unitPrice;
        }

        public void setUnitPrice(int unitPrice) {
            this.unitPrice = unitPrice;
        }

        public double getPurchaseNumber() {
            return purchaseNumber;
        }

        public void setPurchaseNumber(double purchaseNumber) {
            this.purchaseNumber = purchaseNumber;
        }

        public int getOrderFee() {
            return orderFee;
        }

        public void setOrderFee(int orderFee) {
            this.orderFee = orderFee;
        }

        public double getReviewNumber() {
            return reviewNumber;
        }

        public void setReviewNumber(double reviewNumber) {
            this.reviewNumber = reviewNumber;
        }

        public double getReviewFee() {
            return reviewFee;
        }

        public void setReviewFee(double reviewFee) {
            this.reviewFee = reviewFee;
        }

        public String getReviewImageUrl() {
            return reviewImageUrl;
        }

        public void setReviewImageUrl(String reviewImageUrl) {
            this.reviewImageUrl = reviewImageUrl;
        }

        public String getPassImageUrl() {
            return passImageUrl;
        }

        public void setPassImageUrl(String passImageUrl) {
            this.passImageUrl = passImageUrl;
        }

        public String getSpecification() {
//            if ( TextUtils.isEmpty(specification) || specification.equals("kg")){
//                return  "公斤";
//            }
            return specification;
        }

        public void setSpecification(String specification) {
            this.specification = specification;
        }

        public double getTareWeight() {
            return tareWeight;
        }

        public void setTareWeight(double tareWeight) {
            this.tareWeight = tareWeight;
        }

        public double getNetWeightDifference() {
            return netWeightDifference;
        }

        public void setNetWeightDifference(double netWeightDifference) {
            this.netWeightDifference = netWeightDifference;
        }

        public double getDifferenceAmount() {
            return differenceAmount;
        }

        public void setDifferenceAmount(double differenceAmount) {
            this.differenceAmount = differenceAmount;
        }


        public String getProductAddr() {
            return productAddr;
        }

        public void setProductAddr(String productAddr) {
            this.productAddr = productAddr;
        }

        public String getProductBranch() {
            return productBranch;
        }

        public void setProductBranch(String productBranch) {
            this.productBranch = productBranch;
        }

        public double getLastTareWeight() {
            return lastTareWeight;
        }

        public void setLastTareWeight(double lastTareWeight) {
            this.lastTareWeight = lastTareWeight;
        }

        public double getLastGrossWeight() {
            return lastGrossWeight;
        }

        public void setLastGrossWeight(double lastGrossWeight) {
            this.lastGrossWeight = lastGrossWeight;
        }

        public String getSupplyProductOrderDetailList() {
            return supplyProductOrderDetailList;
        }

        public void setSupplyProductOrderDetailList(String supplyProductOrderDetailList) {
            this.supplyProductOrderDetailList = supplyProductOrderDetailList;
        }
    }
}
