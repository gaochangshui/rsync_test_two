package com.trechina.planocycle.entity.dto;

import lombok.Data;

@Data
public class GetPtsCsvCgiDto {
    private String company;
    private String guid;
    private String mode;
    private String ptsIdCsvNm;

}
