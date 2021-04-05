package com.vaaq.fixmyphone.models;

import java.io.Serializable;
import java.util.ArrayList;

public class GetQuote implements Serializable {
    private String name;
    private String brand;
    private String model;
    private String description;
    private String uid;
    private String userRequestId;
    long time;
    ArrayList<Quote> list;

    public GetQuote(){}

    public GetQuote(String name, String brand, String model, String description, String uid, long time) {
        this.name = name;
        this.brand = brand;
        this.model = model;
        this.description = description;
        this.uid = uid;
        this.time = time;
    }

    public GetQuote(String name, String brand, String model, String description, String uid, String userRequestId, long time) {
        this.name = name;
        this.brand = brand;
        this.model = model;
        this.description = description;
        this.uid = uid;
        this.userRequestId = userRequestId;
        this.time = time;
    }

    public GetQuote(String name, String brand, String model, String description, String uid, long time, ArrayList<Quote> list) {
        this.name = name;
        this.brand = brand;
        this.model = model;
        this.description = description;
        this.uid = uid;
        this.time = time;
        this.list = list;
    }

    public String getUserRequestId() {
        return userRequestId;
    }

    public String getName() {
        return name;
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

    public String getUid() {
        return uid;
    }

    public long getTime() {
        return time;
    }

    public ArrayList<Quote> getList() {
        return list;
    }
}
