package com.trechina.planocycle.entity.dto;

import lombok.Data;

@Data
public class PriorityOrderPtsDataDto {
    private Integer id;
    private Integer oldPtsCd;
    private Integer priorityOrderCd;
    private String authorCd;
    private String companyCd;
    private String fileName;

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
