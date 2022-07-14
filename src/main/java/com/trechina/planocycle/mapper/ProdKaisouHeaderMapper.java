package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.vo.ProductItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProdKaisouHeaderMapper {
    int insert(@Param("itemVO") ProductItemVO productItemVO, @Param("maxCol")String maxCol, @Param("kaisouMaxCol") String kaisouMaxCol);

    int selectMaxByCol(String targetColumn);

    Integer selectItemName(String itemName);
}
