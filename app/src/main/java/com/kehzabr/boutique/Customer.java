package com.kehzabr.boutique;

import java.util.List;

public class Customer {

    private String customerName;
    private String mobile;
    private String barcode;
    private String date;

    //    public Customer(String customerName, String mobile, String barcode) {
//        this.customerName = customerName;
//        this.mobile = mobile;
//        this.barcode = barcode;
//    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
