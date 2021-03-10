package com.vaaq.fixmyphone.models;

import java.io.Serializable;

public class Quote implements Serializable {
    private String shopName;
    private String message;
    private String quote;
    private String vendorId;
    private long time;

    public Quote(){}

    public Quote(String shopName, String message, String quote, String vendorId, long time) {
        this.shopName = shopName;
        this.message = message;
        this.quote = quote;
        this.vendorId = vendorId;
        this.time = time;
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

    public String getVendorId() {
        return vendorId;
    }

    public long getTime() {
        return time;
    }
}
