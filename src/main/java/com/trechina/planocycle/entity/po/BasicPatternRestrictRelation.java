package com.trechina.planocycle.entity.po;

import java.io.Serializable;

/**
 * 
 * @TableName basic_pattern_restrict_relation
 */
public class BasicPatternRestrictRelation implements Serializable {
    /**
     * 
     */
    private Long priorityOrderCd;

    /**
     * 
     */
    private String companyCd;

    /**
     * 
     */
    private String authorCd;

    /**
     * 
     */
    private Integer taiCd;

    /**
     * 
     */
    private Integer tanaCd;

    /**
     * 
     */
    private Integer tanaPosition;
    private Integer areaPosition;

    /**
     * 
     */
    private Long restrictCd;
    private Integer janCount;

    /**
     * 
     */
    private Long area;

    private static final long serialVersionUID = 1L;

    public Integer getJanCount() {
        return janCount;
    }

    public void setJanCount(Integer janCount) {
        this.janCount = janCount;
    }

    /**
     * 
     */
    public Long getPriorityOrderCd() {
        return priorityOrderCd;
    }

    /**
     * 
     */
    public void setPriorityOrderCd(Long priorityOrderCd) {
        this.priorityOrderCd = priorityOrderCd;
    }

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
    public String getAuthorCd() {
        return authorCd;
    }

    /**
     * 
     */
    public void setAuthorCd(String authorCd) {
        this.authorCd = authorCd;
    }

    /**
     * 
     */
    public Object getTaiCd() {
        return taiCd;
    }

    /**
     * 
     */
    public void setTaiCd(Integer taiCd) {
        this.taiCd = taiCd;
    }

    /**
     * 
     */
    public Object getTanaCd() {
        return tanaCd;
    }

    /**
     * 
     */
    public void setTanaCd(Integer tanaCd) {
        this.tanaCd = tanaCd;
    }

    /**
     * 
     */
    public Integer getTanaPosition() {
        return tanaPosition;
    }

    /**
     * 
     */
    public void setTanaPosition(Integer tanaPosition) {
        this.tanaPosition = tanaPosition;
    }

    /**
     * 
     */
    public Long getRestrictCd() {
        return restrictCd;
    }

    /**
     * 
     */
    public void setRestrictCd(Long restrictCd) {
        this.restrictCd = restrictCd;
    }

    /**
     * 
     */
    public Long getArea() {
        return area;
    }

    /**
     * 
     */
    public void setArea(Long area) {
        this.area = area;
    }

    public Integer getAreaPosition() {
        return areaPosition;
    }

    public void setAreaPosition(Integer areaPosition) {
        this.areaPosition = areaPosition;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", priorityOrderCd=").append(priorityOrderCd);
        sb.append(", companyCd=").append(companyCd);
        sb.append(", authorCd=").append(authorCd);
        sb.append(", taiCd=").append(taiCd);
        sb.append(", tanaCd=").append(tanaCd);
        sb.append(", tanaPosition=").append(tanaPosition);
        sb.append(", restrictCd=").append(restrictCd);
        sb.append(", area=").append(area);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}