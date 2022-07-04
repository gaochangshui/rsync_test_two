package com.trechina.planocycle.mapper;


import com.trechina.planocycle.entity.po.ZokuseiMst;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface JanInfoMapper {
    List<String> getSchemaOrTableName();
    int setFinalForWork(@Param("finalTable")String finalTable,@Param("wkTable")String wkTable);
    void dropFinal(@Param("tableName") String tableName);

    List<Map<String, Object>> selectJanClassify(String janInfoTb, Integer shelfPatternCd, List<ZokuseiMst> zokuseiList, List<Integer> cdList,
                                                Map<String, String> sizeAndIrisuMap);
}
