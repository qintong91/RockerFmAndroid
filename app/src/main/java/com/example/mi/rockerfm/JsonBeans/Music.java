package com.example.mi.rockerfm.JsonBeans;

import java.io.Serializable;

/**
 * Created by qintong on 16-4-27.
 */
public class Music implements Serializable {
    public String getHerf() {
        return herf;
    }

    public void setHerf(String herf) {
        this.herf = herf;
    }

    private String herf;
 }
