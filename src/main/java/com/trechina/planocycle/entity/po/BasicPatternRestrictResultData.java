package com.trechina.planocycle.entity.po;

import java.io.Serializable;

/**
 * 
 * @TableName work_basic_pattern_restrict_result_data
 */
public class BasicPatternRestrictResultData implements Serializable {
    /**
     * 
     */
    private Long restrictCd;

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
    private Long face;

    /**
     * 
     */
    private Long faceFact;

    private static final long serialVersionUID = 1L;

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
    public Long getFace() {
        return face;
    }

    /**
     * 
     */
    public void setFace(Long face) {
        this.face = face;
    }

    /**
     * 
     */
    public Long getFaceFact() {
        return faceFact;
    }

    /**
     * 
     */
    public void setFaceFact(Long faceFact) {
        this.faceFact = faceFact;
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
        BasicPatternRestrictResultData other = (BasicPatternRestrictResultData) that;
        return (this.getRestrictCd() == null ? other.getRestrictCd() == null : this.getRestrictCd().equals(other.getRestrictCd()))
            && (this.getPriorityOrderCd() == null ? other.getPriorityOrderCd() == null : this.getPriorityOrderCd().equals(other.getPriorityOrderCd()))
            && (this.getCompanyCd() == null ? other.getCompanyCd() == null : this.getCompanyCd().equals(other.getCompanyCd()))
            && (this.getAuthorCd() == null ? other.getAuthorCd() == null : this.getAuthorCd().equals(other.getAuthorCd()))
            && (this.getFace() == null ? other.getFace() == null : this.getFace().equals(other.getFace()))
            && (this.getFaceFact() == null ? other.getFaceFact() == null : this.getFaceFact().equals(other.getFaceFact()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getRestrictCd() == null) ? 0 : getRestrictCd().hashCode());
        result = prime * result + ((getPriorityOrderCd() == null) ? 0 : getPriorityOrderCd().hashCode());
        result = prime * result + ((getCompanyCd() == null) ? 0 : getCompanyCd().hashCode());
        result = prime * result + ((getAuthorCd() == null) ? 0 : getAuthorCd().hashCode());
        result = prime * result + ((getFace() == null) ? 0 : getFace().hashCode());
        result = prime * result + ((getFaceFact() == null) ? 0 : getFaceFact().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", restrictCd=").append(restrictCd);
        sb.append(", priorityOrderCd=").append(priorityOrderCd);
        sb.append(", companyCd=").append(companyCd);
        sb.append(", authorCd=").append(authorCd);
        sb.append(", face=").append(face);
        sb.append(", faceFact=").append(faceFact);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}