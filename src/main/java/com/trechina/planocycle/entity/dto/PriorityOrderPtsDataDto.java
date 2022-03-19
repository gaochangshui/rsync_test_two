package com.trechina.planocycle.entity.dto;


public class PriorityOrderPtsDataDto {
    private Integer id;
    private Integer oldPtsCd;
    private Integer priorityOrderCd;
    private String authorCd;
    private String companyCd;

    public String getCompanyCd() {
        return companyCd;
    }

    public void setCompanyCd(String companyCd) {
        this.companyCd = companyCd;
    }

    public String getAuthorCd() {
        return authorCd;
    }

    public void setAuthorCd(String authorCd) {
        this.authorCd = authorCd;
    }

    public Integer getPriorityOrderCd() {
        return priorityOrderCd;
    }

    public void setPriorityOrderCd(Integer priorityOrderCd) {
        this.priorityOrderCd = priorityOrderCd;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOldPtsCd() {
        return oldPtsCd;
    }

    public void setOldPtsCd(Integer oldPtsCd) {
        this.oldPtsCd = oldPtsCd;
    }

    public static final class PriorityOrderPtsDataDtoBuilder {
        private Integer id;
        private Integer oldPtsCd;
        private Integer priorityOrderCd;
        private String authorCd;
        private String companyCd;

        private PriorityOrderPtsDataDtoBuilder() {
        }

        public static PriorityOrderPtsDataDtoBuilder aPriorityOrderPtsDataDto() {
            return new PriorityOrderPtsDataDtoBuilder();
        }

        public PriorityOrderPtsDataDtoBuilder withId(Integer id) {
            this.id = id;
            return this;
        }

        public PriorityOrderPtsDataDtoBuilder withOldPtsCd(Integer oldPtsCd) {
            this.oldPtsCd = oldPtsCd;
            return this;
        }

        public PriorityOrderPtsDataDtoBuilder withPriorityOrderCd(Integer priorityOrderCd) {
            this.priorityOrderCd = priorityOrderCd;
            return this;
        }

        public PriorityOrderPtsDataDtoBuilder withAuthorCd(String authorCd) {
            this.authorCd = authorCd;
            return this;
        }

        public PriorityOrderPtsDataDtoBuilder withCompanyCd(String companyCd) {
            this.companyCd = companyCd;
            return this;
        }

        public PriorityOrderPtsDataDto build() {
            PriorityOrderPtsDataDto priorityOrderPtsDataDto = new PriorityOrderPtsDataDto();
            priorityOrderPtsDataDto.setId(id);
            priorityOrderPtsDataDto.setOldPtsCd(oldPtsCd);
            priorityOrderPtsDataDto.setPriorityOrderCd(priorityOrderCd);
            priorityOrderPtsDataDto.setAuthorCd(authorCd);
            priorityOrderPtsDataDto.setCompanyCd(companyCd);
            return priorityOrderPtsDataDto;
        }
    }
}
