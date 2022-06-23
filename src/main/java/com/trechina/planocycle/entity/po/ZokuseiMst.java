package com.trechina.planocycle.entity.po;

import java.io.Serializable;

/**
 * 
 * @TableName planocycle_zokusei_mst
 */
public class ZokuseiMst implements Serializable {
    /**
     * 
     */
    private String companyCd;

    /**
     * 
     */
    private String classCd;

    /**
     * 
     */
    private Integer zokuseiId;

    /**
     * 
     */
    private String zokuseiNm;

    /**
     * 
     */
    private Integer zokuseiSort;

    /**
     * 
     */
    private Integer type;

    /**
     * 
     */
    private Integer sortType;

    /**
     * 
     */
    private String zokuseiCol;

    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public String getCompanyCd() {
        return companyCd;
    }

    /**
     * 
     */
    public void setCompanyCd(String companyCd) {
        this.companyCd = companyCd;
    }

    /**
     * 
     */
    public String getClassCd() {
        return classCd;
    }

    /**
     * 
     */
    public void setClassCd(String classCd) {
        this.classCd = classCd;
    }

    /**
     * 
     */
    public Integer getZokuseiId() {
        return zokuseiId;
    }

    /**
     * 
     */
    public void setZokuseiId(Integer zokuseiId) {
        this.zokuseiId = zokuseiId;
    }

    /**
     * 
     */
    public String getZokuseiNm() {
        return zokuseiNm;
    }

    /**
     * 
     */
    public void setZokuseiNm(String zokuseiNm) {
        this.zokuseiNm = zokuseiNm;
    }

    /**
     * 
     */
    public Integer getZokuseiSort() {
        return zokuseiSort;
    }

    /**
     * 
     */
    public void setZokuseiSort(Integer zokuseiSort) {
        this.zokuseiSort = zokuseiSort;
    }

    /**
     * 
     */
    public Integer getType() {
        return type;
    }

    /**
     * 
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * 
     */
    public Integer getSortType() {
        return sortType;
    }

    /**
     * 
     */
    public void setSortType(Integer sortType) {
        this.sortType = sortType;
    }

    /**
     * 
     */
    public String getZokuseiCol() {
        return zokuseiCol;
    }

    /**
     * 
     */
    public void setZokuseiCol(String zokuseiCol) {
        this.zokuseiCol = zokuseiCol;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        ZokuseiMst other = (ZokuseiMst) that;
        return (this.getCompanyCd() == null ? other.getCompanyCd() == null : this.getCompanyCd().equals(other.getCompanyCd()))
            && (this.getClassCd() == null ? other.getClassCd() == null : this.getClassCd().equals(other.getClassCd()))
            && (this.getZokuseiId() == null ? other.getZokuseiId() == null : this.getZokuseiId().equals(other.getZokuseiId()))
            && (this.getZokuseiNm() == null ? other.getZokuseiNm() == null : this.getZokuseiNm().equals(other.getZokuseiNm()))
            && (this.getZokuseiSort() == null ? other.getZokuseiSort() == null : this.getZokuseiSort().equals(other.getZokuseiSort()))
            && (this.getType() == null ? other.getType() == null : this.getType().equals(other.getType()))
            && (this.getSortType() == null ? other.getSortType() == null : this.getSortType().equals(other.getSortType()))
            && (this.getZokuseiCol() == null ? other.getZokuseiCol() == null : this.getZokuseiCol().equals(other.getZokuseiCol()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getCompanyCd() == null) ? 0 : getCompanyCd().hashCode());
        result = prime * result + ((getClassCd() == null) ? 0 : getClassCd().hashCode());
        result = prime * result + ((getZokuseiId() == null) ? 0 : getZokuseiId().hashCode());
        result = prime * result + ((getZokuseiNm() == null) ? 0 : getZokuseiNm().hashCode());
        result = prime * result + ((getZokuseiSort() == null) ? 0 : getZokuseiSort().hashCode());
        result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
        result = prime * result + ((getSortType() == null) ? 0 : getSortType().hashCode());
        result = prime * result + ((getZokuseiCol() == null) ? 0 : getZokuseiCol().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", companyCd=").append(companyCd);
        sb.append(", classCd=").append(classCd);
        sb.append(", zokuseiId=").append(zokuseiId);
        sb.append(", zokuseiNm=").append(zokuseiNm);
        sb.append(", zokuseiSort=").append(zokuseiSort);
        sb.append(", type=").append(type);
        sb.append(", sortType=").append(sortType);
        sb.append(", zokuseiCol=").append(zokuseiCol);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}