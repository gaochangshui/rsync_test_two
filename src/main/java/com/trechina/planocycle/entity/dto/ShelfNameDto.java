package com.trechina.planocycle.entity.dto;

import lombok.Data;

import java.util.List;

@Data
public class ShelfNameDto {
    private Integer id;
    private String companyCd;
    private String shelfName;
    private List<Integer> area;

}
