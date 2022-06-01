package com.trechina.planocycle.entity.po;

public class MstKiGyoCore {
    private String companyCd;

    private String dateMst;

    private String tenpoKaisouMst;

    private String selectedTenpo;

    private String shouhinKaisouMst;

    private String selectedShouhin;

    public String getCompanyCd() {
        return companyCd;
    }

    public void setCompanyCd(String companyCd) {
        this.companyCd = companyCd == null ? null : companyCd.trim();
    }

    public String getDateMst() {
        return dateMst;
    }

    public void setDateMst(String dateMst) {
        this.dateMst = dateMst == null ? null : dateMst.trim();
    }

    public String getTenpoKaisouMst() {
        return tenpoKaisouMst;
    }

    public void setTenpoKaisouMst(String tenpoKaisouMst) {
        this.tenpoKaisouMst = tenpoKaisouMst == null ? null : tenpoKaisouMst.trim();
    }

    public String getSelectedTenpo() {
        return selectedTenpo;
    }

    public void setSelectedTenpo(String selectedTenpo) {
        this.selectedTenpo = selectedTenpo == null ? null : selectedTenpo.trim();
    }

    public String getShouhinKaisouMst() {
        return shouhinKaisouMst;
    }

    public void setShouhinKaisouMst(String shouhinKaisouMst) {
        this.shouhinKaisouMst = shouhinKaisouMst == null ? null : shouhinKaisouMst.trim();
    }

    public String getSelectedShouhin() {
        return selectedShouhin;
    }

    public void setSelectedShouhin(String selectedShouhin) {
        this.selectedShouhin = selectedShouhin == null ? null : selectedShouhin.trim();
    }

    @Override
    public String toString() {
        return "MstKiGyoCore{" +
                "companyCd='" + companyCd + '\'' +
                ", dateMst='" + dateMst + '\'' +
                ", tenpoKaisouMst='" + tenpoKaisouMst + '\'' +
                ", selectedTenpo='" + selectedTenpo + '\'' +
                ", shouhinKaisouMst='" + shouhinKaisouMst + '\'' +
                ", selectedShouhin='" + selectedShouhin + '\'' +
                '}';
    }
}
