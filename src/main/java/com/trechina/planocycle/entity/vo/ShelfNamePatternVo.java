package com.trechina.planocycle.entity.vo;


import lombok.Data;

import java.util.List;
@Data
public class ShelfNamePatternVo {
    private Integer value;
    private String label;
    private List<ShelfPatternChidrenVo> children;



}
