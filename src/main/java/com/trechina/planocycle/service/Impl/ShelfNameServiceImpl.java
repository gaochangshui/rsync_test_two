package com.trechina.planocycle.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.trechina.planocycle.entity.dto.ShelfNameDto;
import com.trechina.planocycle.entity.po.ShelfNameArea;
import com.trechina.planocycle.entity.po.ShelfNameMst;
import com.trechina.planocycle.entity.vo.ShelfNameDataVO;
import com.trechina.planocycle.entity.vo.ShelfNameUpdVO;
import com.trechina.planocycle.entity.vo.ShelfNameVO;
import com.trechina.planocycle.entity.vo.ShelfPatternTreeVO;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.ShelfNameMstMapper;
import com.trechina.planocycle.service.ShelfNameAreaService;
import com.trechina.planocycle.service.ShelfNameService;
import com.trechina.planocycle.service.ShelfPatternService;
import com.trechina.planocycle.utils.ResultMaps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class ShelfNameServiceImpl implements ShelfNameService {
    private final Logger logger= LoggerFactory.getLogger(this.getClass());
    @Autowired
    private HttpSession session;
    @Autowired
    private ShelfNameMstMapper shelfNameMstMapper;
    @Autowired
    private ShelfPatternService shelfPatternService;
    @Autowired
    private ShelfNameAreaService shelfNameAreaService;

    /**
     * 获取棚名称信息
     * @param companyCd
     * @return
     */
    @Override
    public Map<String, Object> getShelfNameInfo(String companyCd) {
        logger.info("获取棚名称信息的参数："+companyCd);
        List<ShelfNameDataVO> resultInfo = shelfNameMstMapper.selectShelfNameInfo(companyCd);
        logger.info("获取棚名称信息的返回值："+resultInfo);
        return ResultMaps.result(ResultEnum.SUCCESS,resultInfo);
    }

    /**
     * 保存棚名称信息
     * @param shelfNameDto
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> setShelfName(ShelfNameDto shelfNameDto) {
        logger.info("保存棚名称信息的参数："+shelfNameDto);
        // check名称 同一个企业名称不能重复
        Integer resultNum = shelfNameMstMapper.selectDistinctName(shelfNameDto);
        if (resultNum>0){
            return ResultMaps.result(ResultEnum.NAMEISEXISTS);
        }
        List<ShelfNameArea> list = new ArrayList<>();
        ShelfNameMst shelfNameMst = new ShelfNameMst();
        shelfNameMst.setShelfName(shelfNameDto.getShelfName());
        shelfNameMst.setConpanyCd(shelfNameDto.getCompanyCd());
        shelfNameMst.setAuthorCd(session.getAttribute("aud").toString());
        logger.info("保存棚名称信息转换后的参数："+shelfNameMst);
        Integer resultInfo = shelfNameMstMapper.insert(shelfNameMst);
        //获取用户id
        String authorCd = session.getAttribute("aud").toString();
        shelfNameDto.getArea().forEach(item -> {
            ShelfNameArea shelfNameArea = new ShelfNameArea();
            shelfNameArea.setCompanyCd(shelfNameDto.getCompanyCd());
            shelfNameArea.setAreacd(item);
            shelfNameArea.setShelfNameCd(shelfNameMst.getId());
            list.add(shelfNameArea);
        });
        logger.info("保存棚名称信息转换后的area参数："+list);

        shelfNameAreaService.setShelfNameArea(list,authorCd);
        logger.info("保存棚名称信息保存后返回的信息："+resultInfo);
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    /**
     * 获取棚名称Name
     * @param companyCd
     * @return
     */
    @Override
    public Map<String, Object> getShelfName(String companyCd) {
        logger.info("获取棚名称Name的参数："+companyCd);
        List<ShelfNameVO> resultInfo = shelfNameMstMapper.selectShelfName(companyCd);
        logger.info("获取棚名称Name的返回值："+resultInfo);
        return ResultMaps.result(ResultEnum.SUCCESS,resultInfo);
    }
    /**
     * 删除棚名称
     * @param jsonObject
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> delShelfNameInfo(JSONObject jsonObject) {
        logger.info("删除棚名称Name的参数："+jsonObject.toString());
        //获取创建者cd
        String authorCd = session.getAttribute("aud").toString();
        // 删除棚名称
        Integer id = Integer.valueOf(String.valueOf(((Map) jsonObject.get("param")).get("id")));
        shelfNameMstMapper.deleteShelfNameInfo(id,authorCd);
        // 查询要删掉的棚patternid
        List<Integer> patternId = shelfNameMstMapper.selectPatternCd(id);
        patternId.forEach(item->{
            logger.info("棚patternid"+patternId);
            Map<String,Integer> patternMap =new HashMap<>();
            Map<String,Object> paraMap =new HashMap<>();
            patternMap.put("id",item);
            paraMap.put("param",patternMap);
            // 删除关联的棚pattern
            shelfPatternService.delShelfPatternInfo((JSONObject) JSONObject.toJSON(paraMap));
            // 删除关联的area
        });
        shelfNameAreaService.delShelfNameArea(id,authorCd);
        return ResultMaps.result(ResultEnum.SUCCESS);
    }
    /**
     * 获取单表棚名称信息，用于修改
     * @param id
     * @return
     */
    @Override
    public Map<String, Object> getShelfNameInfoById(Integer id) {
        ShelfNameUpdVO shelfNameUpdVO = shelfNameMstMapper.selShelfNameInfoById(id);
        return ResultMaps.result(ResultEnum.SUCCESS,shelfNameUpdVO);
    }

    /**
     * 获取棚名称和棚pattern的属性结构
     *
     * @param companyCd
     * @return
     */
    @Override
    public Map<String, Object> getShelfPatternMaster(String companyCd) {
        List<ShelfPatternTreeVO> shelfPatternTreeVO = shelfNameMstMapper.selectShelfPatternTree(companyCd);
        return ResultMaps.result(ResultEnum.SUCCESS,shelfPatternTreeVO);
    }

}
