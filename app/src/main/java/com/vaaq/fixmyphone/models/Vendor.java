package com.vaaq.fixmyphone.models;

public class Vendor {

    private String name;
    private String phone;
    private String shopName;
    private String shopAddress;
    private String ibanNumber;
    private String email;
    private String password;

    public Vendor(){}

    public Vendor(String name, String phone, String shopName, String shopAddress, String ibanNumber, String email, String password) {
        this.name = name;
        this.phone = phone;
        this.shopName = shopName;
        this.shopAddress = shopAddress;
        this.ibanNumber = ibanNumber;
        this.email = email;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getShopName() {
        return shopName;
    }

    public String getShopAddress() {
        return shopAddress;
    }

    public String getIbanNumber() {
        return ibanNumber;
    }

    //    public String getEmail() {
//        return email;
//    }
//
//    public String getPassword() {
//        return password;
//    }


}
