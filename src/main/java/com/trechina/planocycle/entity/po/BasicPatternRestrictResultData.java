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