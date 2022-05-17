package com.trechina.planocycle.entity.dto;

public class DownloadDto {

        private String jan;
        private Integer taiCd;
        private Integer tanaCd;
        private String attr1;
        private String attr2;
        private Integer tanapositionCd;
        private String companyCd;
        private Integer priorityOrderCd;
        private Integer rank;
        private String name;

        public String getName() {
                return name;
        }

        public void setName(String name) {
                this.name = name;
        }

        public String getAttr1() {
                return attr1;
        }

        public void setAttr1(String attr1) {
                this.attr1 = attr1;
        }

        public String getAttr2() {
                return attr2;
        }

        public void setAttr2(String attr2) {
                this.attr2 = attr2;
        }

        public Integer getRank() {
                return rank;
        }

        public void setRank(Integer rank) {
                this.rank = rank;
        }

        public String getJan() {
                return jan;
        }

        public void setJan(String jan) {
                this.jan = jan;
        }

        public Integer getTaiCd() {
                return taiCd;
        }

        public void setTaiCd(Integer taiCd) {
                this.taiCd = taiCd;
        }

        public Integer getTanaCd() {
                return tanaCd;
        }

        public void setTanaCd(Integer tanaCd) {
                this.tanaCd = tanaCd;
        }

        public Integer getTanapositionCd() {
                return tanapositionCd;
        }

        public void setTanapositionCd(Integer tanapositionCd) {
                this.tanapositionCd = tanapositionCd;
        }

        public String getCompanyCd() {
                return companyCd;
        }

        public void setCompanyCd(String companyCd) {
                this.companyCd = companyCd;
        }

        public Integer getPriorityOrderCd() {
                return priorityOrderCd;
        }

        public void setPriorityOrderCd(Integer priorityOrderCd) {
                this.priorityOrderCd = priorityOrderCd;
        }



        @Override
        public String toString() {
                return "DownloadDto{" +
                        "jan='" + jan + '\'' +
                        ", taiCd=" + taiCd +
                        ", tanaCd=" + tanaCd +
                        ", tanapositionCd=" + tanapositionCd +
                        ", companyCd='" + companyCd + '\'' +
                        ", priorityOrderCd=" + priorityOrderCd +
                        ", rank=" + rank +
                        '}';
        }
}
