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
    private Object taiCd;

    /**
     * 
     */
    private Object tanaCd;

    /**
     * 
     */
    private Integer tanaPosition;

    /**
     * 
     */
    private Long restrictCd;

    /**
     * 
     */
    private Long area;

    private static final long serialVersionUID = 1L;

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
    public void setTaiCd(Object taiCd) {
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
    public void setTanaCd(Object tanaCd) {
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
        BasicPatternRestrictRelation other = (BasicPatternRestrictRelation) that;
        return (this.getPriorityOrderCd() == null ? other.getPriorityOrderCd() == null : this.getPriorityOrderCd().equals(other.getPriorityOrderCd()))
            && (this.getCompanyCd() == null ? other.getCompanyCd() == null : this.getCompanyCd().equals(other.getCompanyCd()))
            && (this.getAuthorCd() == null ? other.getAuthorCd() == null : this.getAuthorCd().equals(other.getAuthorCd()))
            && (this.getTaiCd() == null ? other.getTaiCd() == null : this.getTaiCd().equals(other.getTaiCd()))
            && (this.getTanaCd() == null ? other.getTanaCd() == null : this.getTanaCd().equals(other.getTanaCd()))
            && (this.getTanaPosition() == null ? other.getTanaPosition() == null : this.getTanaPosition().equals(other.getTanaPosition()))
            && (this.getRestrictCd() == null ? other.getRestrictCd() == null : this.getRestrictCd().equals(other.getRestrictCd()))
            && (this.getArea() == null ? other.getArea() == null : this.getArea().equals(other.getArea()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getPriorityOrderCd() == null) ? 0 : getPriorityOrderCd().hashCode());
        result = prime * result + ((getCompanyCd() == null) ? 0 : getCompanyCd().hashCode());
        result = prime * result + ((getAuthorCd() == null) ? 0 : getAuthorCd().hashCode());
        result = prime * result + ((getTaiCd() == null) ? 0 : getTaiCd().hashCode());
        result = prime * result + ((getTanaCd() == null) ? 0 : getTanaCd().hashCode());
        result = prime * result + ((getTanaPosition() == null) ? 0 : getTanaPosition().hashCode());
        result = prime * result + ((getRestrictCd() == null) ? 0 : getRestrictCd().hashCode());
        result = prime * result + ((getArea() == null) ? 0 : getArea().hashCode());
        return result;
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