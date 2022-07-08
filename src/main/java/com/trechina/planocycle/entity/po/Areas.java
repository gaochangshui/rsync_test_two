package com.trechina.planocycle.entity.po;

public class Areas {
    private Integer areacd;

    private String areaname;


    public Integer getAreacd() {
        return areacd;
    }

    public void setAreacd(Integer areacd) {
        this.areacd = areacd;
    }

    public String getAreaname() {
        return areaname;
    }

    public void setAreaname(String areaname) {
        this.areaname = areaname == null ? null : areaname.trim();
    }
}
