package com.trechina.planocycle.service.Impl;

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
     * 保存shelfpattern关联的area
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
     * 删除shelfName关联的area
     *
     * @param id
     * @return
     */
    @Override
    public Integer delShelfNameArea(Integer id,String authorCd) {
        return shelfNameAreaMapper.deleteByPrimaryKey(id,authorCd);
    }
}
