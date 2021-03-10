package com.vaaq.fixmyphone.models;

public class ActiveOrder {
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

    public ActiveOrder(String userName, String vendorName, String userId, String vendorId, String brand, String model, String description, String shopName, String message, String quote, long time) {
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
}
