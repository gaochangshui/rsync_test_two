package com.trechina.planocycle.service.Impl;

import com.trechina.planocycle.entity.po.ShelfPatternArea;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.ShelfPatternAreaMapper;
import com.trechina.planocycle.service.ShelfPatternAreaService;
import com.trechina.planocycle.utils.ResultMaps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ShelfPatternAreaServiceImpl implements ShelfPatternAreaService {
    @Autowired
    private ShelfPatternAreaMapper shelfPatternAreaMapper;
    /**
     * 保存shelfPattern关联的area
     *
     * @param shelfPatternArea
     * @return
     */
    @Override
    public Map<String, Object> setShelfPatternArea(List<ShelfPatternArea> shelfPatternArea,String authorCd) {
        shelfPatternAreaMapper.insert(shelfPatternArea,authorCd);
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    /**
     * 删除shelfPattern关联的area
     *
     * @param id
     * @return
     */
    @Override
    public Integer delShelfPatternArea(Integer id,String authorCd) {
        return shelfPatternAreaMapper.deleteByPrimaryKey(id,authorCd);

    }

    @Override
    public List<Integer> getShelfPatternArea(Integer id,String companyCd) {
        return shelfPatternAreaMapper.getShelfPatternArea(id,companyCd);
    }

    @Override
    public int deleteAreaCd(List<Integer> areaCd, Integer shelfPatternCd, String authorCd) {
        return shelfPatternAreaMapper.deleteAreaCd(areaCd,shelfPatternCd,authorCd);
    }

    @Override
    public Integer setDelFlg(Integer areaCd, Integer shelfPatternCd, String authorCd) {
        return shelfPatternAreaMapper.setDelFlg(areaCd,shelfPatternCd,authorCd);
    }


}
