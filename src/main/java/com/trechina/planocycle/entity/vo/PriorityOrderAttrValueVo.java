package com.trechina.planocycle.entity.vo;

import lombok.Data;

import java.util.List;
@Data
public class PriorityOrderAttrValueVo {
   private Integer attrCd;
   private String attrName;
   private Integer sort;
   private List<PriorityOrderAttrValue> children;
   private Boolean show =true;

}
