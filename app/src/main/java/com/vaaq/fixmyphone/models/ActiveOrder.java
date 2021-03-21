package com.vaaq.fixmyphone.models;

import java.io.Serializable;

public class ActiveOrder implements Serializable {
    private String orderId;
    private String orderStatus;
    private String paymentStatus;
    private String rateAndReviewStatus;
    private String userName;
    private String vendorName;
    private String userId;
    private String vendorId;
    private String brand;
    private String model;
    private String description;
    private String shopName;
    private String message;
    private String quote;
    private long time;

    public ActiveOrder() {
    }


    public ActiveOrder(String orderId, String orderStatus, String paymentStatus, String rateAndReviewStatus, String userName, String vendorName, String userId, String vendorId, String brand, String model, String description, String shopName, String message, String quote, long time) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.paymentStatus = paymentStatus;
        this.rateAndReviewStatus = rateAndReviewStatus;
        this.userName = userName;
        this.vendorName = vendorName;
        this.userId = userId;
        this.vendorId = vendorId;
        this.brand = brand;
        this.model = model;
        this.description = description;
        this.shopName = shopName;
        this.message = message;
        this.quote = quote;
        this.time = time;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public String getRateAndReviewStatus() {
        return rateAndReviewStatus;
    }

    public String getUserName() {
        return userName;
    }

    public String getVendorName() {
        return vendorName;
    }

    public String getUserId() {
        return userId;
    }

    public String getVendorId() {
        return vendorId;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public String getDescription() {
        return description;
    }

    public String getShopName() {
        return shopName;
    }

    public String getMessage() {
        return message;
    }

    public String getQuote() {
        return quote;
    }

    public long getTime() {
        return time;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public void setRateAndReviewStatus(String rateAndReviewStatus) {
        this.rateAndReviewStatus = rateAndReviewStatus;
    }
}
