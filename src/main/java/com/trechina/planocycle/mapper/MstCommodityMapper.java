package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.vo.MstCommodityVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MstCommodityMapper {

    /**
     * 商品マスタ
     * @return
     */
    List<MstCommodityVO> getCommodityList(String tableName);

}