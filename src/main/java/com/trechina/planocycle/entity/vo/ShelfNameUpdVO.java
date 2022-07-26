package com.trechina.planocycle.entity.vo;

import lombok.Data;

import java.util.List;

@Data
public class ShelfNameUpdVO {
    private Integer id;

    private String shelfName;

    private List<Integer> area;

}
