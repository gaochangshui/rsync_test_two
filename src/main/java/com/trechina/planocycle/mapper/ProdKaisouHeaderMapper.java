package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.vo.ProductItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
@Mapper
public interface ProdKaisouHeaderMapper {
    int insert(@Param("itemVO") ProductItemVO productItemVO, @Param("maxCol")String maxCol, @Param("kaisouMaxCol") String kaisouMaxCol);

    int selectMaxByCol(String targetColumn);

    Integer selectPlanItem(String itemName);

    List<Map<String,Object>> getPlanItem(@Param("tableNameAttr") String tableNameAttr);

    void setItem(LinkedHashMap<String, Object> map, String tableName);

    void delItem(String value,String tableName);

     String getItem(String value, String tableNameAttr);
}
