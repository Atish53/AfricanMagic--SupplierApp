package com.example.africanmagic_supplierapp;

import net.sourceforge.jtds.jdbc.DateTime;

import java.sql.Date;
import java.text.DateFormat;

public class ClassListSupplier {
    public String productString;
    public Integer orderId;

    public ClassListSupplier(String prodString, Integer orderNo) {
        this.productString = prodString;
        this.orderId = orderNo;
    }

    public String getProductString() { return productString; }

    public Integer getOrderId() { return orderId; }
{}


}


