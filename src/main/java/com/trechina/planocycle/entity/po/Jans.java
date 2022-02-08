package com.trechina.planocycle.entity.po;

public class Jans {
    private String jan;

    private String janname;

    public String getJan() {
        return jan;
    }

    public void setJan(String jan) {
        this.jan = jan == null ? null : jan.trim();
    }

    public String getJanname() {
        return janname;
    }

    public void setJanname(String janname) {
        this.janname = janname == null ? null : janname.trim();
    }
}