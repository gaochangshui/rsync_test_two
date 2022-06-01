package com.trechina.planocycle.entity.vo;

public class ProductOrderAttrAndItemVO {
    private Integer itemFlg;
    private String attrStr;

    public Integer getItemFlg() {
        return itemFlg;
    }

    public void setItemFlg(Integer itemFlg) {
        this.itemFlg = itemFlg;
    }

    public String getAttrStr() {
        return attrStr;
    }

    public void setAttrStr(String attrStr) {
        this.attrStr = attrStr;
    }

    @Override
    public String toString() {
        return "ProductOrderAttrAndItemVO{" +
                "itemFlg=" + itemFlg +
                ", attrStr='" + attrStr + '\'' +
                '}';
    }
}
