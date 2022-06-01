package com.trechina.planocycle.service.impl;

import com.trechina.planocycle.entity.po.ShelfNameArea;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.ShelfNameAreaMapper;
import com.trechina.planocycle.service.ShelfNameAreaService;
import com.trechina.planocycle.utils.ResultMaps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ShelfNameAreaServiceImpl implements ShelfNameAreaService {
    @Autowired
    private ShelfNameAreaMapper shelfNameAreaMapper;

    /**
     * shelfpattern関連areaの保存
     *
     * @param shelfNameArea
     * @return
     */
    @Override
    public Map<String, Object> setShelfNameArea(List<ShelfNameArea> shelfNameArea,String authorCd) {
        shelfNameAreaMapper.insert(shelfNameArea,authorCd);
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    /**
     * shelfNameに関連付けられたareaを削除
     *
     * @param id
     * @return
     */
    @Override
    public Integer delShelfNameArea(Integer id,String authorCd) {
        return shelfNameAreaMapper.deleteByPrimaryKey(id,authorCd);
    }

    @Override
    public List<Integer> getShelfNameArea(Integer id,String companyCd) {
        return shelfNameAreaMapper.getShelfNameArea(id,companyCd);
    }

    @Override
    public Integer delAreaCd(List<Integer> areaCd, Integer id,String authorCd) {
        return shelfNameAreaMapper.deleteAreaCd(areaCd,id,authorCd);
    }

    @Override
    public Integer setDelFlg(Integer areaCd, Integer id, String authorCd) {
        return shelfNameAreaMapper.setDelFlg(areaCd,id,authorCd);
    }
}
