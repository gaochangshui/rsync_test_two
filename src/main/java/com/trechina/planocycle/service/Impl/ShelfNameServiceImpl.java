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
import com.trechina.planocycle.service.ShelfPatternAreaService;
import com.trechina.planocycle.service.ShelfPatternService;
import com.trechina.planocycle.utils.ListDisparityUtils;
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
    @Autowired
    private ShelfPatternAreaService shelfPatternAreaService;

    /**
     * 获取棚名称信息
     * @param companyCd
     * @return
     */
    @Override
    public Map<String, Object> getShelfNameInfo(String companyCd) {
        logger.info("获取棚名称信息的参数：{}",companyCd);
        List<ShelfNameDataVO> resultInfo = shelfNameMstMapper.selectShelfNameInfo(companyCd);
        logger.info("获取棚名称信息的返回值：{}",resultInfo);
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
        logger.info("保存棚名称信息的参数：{}",shelfNameDto);
        // check名称 同一个企业名称不能重复
        Integer resultNum = shelfNameMstMapper.selectDistinctName(shelfNameDto);
        if (resultNum!=null){
            return ResultMaps.result(ResultEnum.NAMEISEXISTS);
        }
        String authorCd = session.getAttribute("aud").toString();
        List<ShelfNameArea> list = new ArrayList<>();
        ShelfNameMst shelfNameMst = new ShelfNameMst();
        shelfNameMst.setShelfName(shelfNameDto.getShelfName());
        shelfNameMst.setConpanyCd(shelfNameDto.getCompanyCd());
        shelfNameMst.setAuthorCd(authorCd);
        logger.info("保存棚名称信息转换后的参数：{}",shelfNameMst);
        Integer resultInfo = shelfNameMstMapper.insert(shelfNameMst);
        //获取用户id

        shelfNameDto.getArea().forEach(item -> {
            ShelfNameArea shelfNameArea = new ShelfNameArea();
            shelfNameArea.setCompanyCd(shelfNameDto.getCompanyCd());
            shelfNameArea.setAreacd(item);
            shelfNameArea.setShelfNameCd(shelfNameMst.getId());
            list.add(shelfNameArea);
        });
        logger.info("保存棚名称信息转换后的area参数：{}",list);

        shelfNameAreaService.setShelfNameArea(list,authorCd);
        logger.info("保存棚名称信息保存后返回的信息：{}",resultInfo);
        return ResultMaps.result(ResultEnum.SUCCESS);
    }
    /**
     * 修改棚名称信息
     * @param shelfNameDto
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> updateShelfName(ShelfNameDto shelfNameDto) {
        logger.info("修改棚名称信息的参数：{}",shelfNameDto);
        // check名称 同一个企业名称不能重复
        Integer resultNum = shelfNameMstMapper.selectDistinctName(shelfNameDto);
        if (resultNum!=null && !resultNum.equals(shelfNameDto.getId())){
            return ResultMaps.result(ResultEnum.NAMEISEXISTS);
        }
        //获取用户id
        String authorCd = session.getAttribute("aud").toString();
        //要删除的ShelfNameArea集合
        List<ShelfNameArea> delList = new ArrayList<>();
        //要添加的ShelfNameArea集合
        List<ShelfNameArea> setList = new ArrayList<>();
        ShelfNameMst shelfNameMst = new ShelfNameMst();
        shelfNameMst.setId(shelfNameDto.getId());
        shelfNameMst.setShelfName(shelfNameDto.getShelfName());
        shelfNameMst.setConpanyCd(shelfNameDto.getCompanyCd());
        shelfNameMst.setAuthorCd(authorCd);
        logger.info("修改棚名称信息转换后的参数：{}",shelfNameMst);
        int resultInfo = shelfNameMstMapper.update(shelfNameMst);
        logger.info("修改棚名称信息后返回的信息：{}",resultInfo);
        //获取shelfName关联的Area
        List<Integer> getShelfNameArea = shelfNameAreaService.getShelfNameArea(shelfNameMst.getId(),shelfNameMst.getConpanyCd());
        logger.info("shelfName关联的所有Area：{}",getShelfNameArea);
        //数据库中和修改重复数据
        shelfNameDto.getArea().forEach(item->{
            for (Integer area : getShelfNameArea) {
                if (item.equals(area)){
                    shelfNameAreaService.setDelFlg(item,shelfNameDto.getId(),authorCd);
                }
            }
        });
        //要删除的area集合
        List<Integer> deleteAreaList = ListDisparityUtils.getListDisparit(getShelfNameArea, shelfNameDto.getArea());
        //要新增area的集合
        List<Integer> setAreaList = ListDisparityUtils.getListDisparit( shelfNameDto.getArea(),getShelfNameArea);
        if (deleteAreaList.size()>0) {
            deleteAreaList.forEach(item -> {

                ShelfNameArea shelfNameArea = new ShelfNameArea();
                shelfNameArea.setCompanyCd(shelfNameDto.getCompanyCd());
                shelfNameArea.setAreacd(item);
                shelfNameArea.setShelfNameCd(shelfNameMst.getId());
                delList.add(shelfNameArea);
            });
            logger.info("删除棚名称信息转换后的area参数：{}",delList);
            //删除关联的area
            shelfNameAreaService.delAreaCd(deleteAreaList,shelfNameDto.getId(),authorCd);
            //查询棚名称下的棚pattern
            List<Integer> shelfPatternList = shelfPatternService.getShelfPattern(shelfNameDto.getCompanyCd(), shelfNameDto.getId());
            //删除棚pattern下的area
            if (shelfPatternList!=null) {
                shelfPatternList.forEach(item -> {
                    shelfPatternAreaService.deleteAreaCd(deleteAreaList, item, authorCd);
                });
            }

        }
        if (setAreaList.size()>0) {
            setAreaList.forEach(item -> {
                ShelfNameArea shelfNameArea = new ShelfNameArea();
                shelfNameArea.setCompanyCd(shelfNameDto.getCompanyCd());
                shelfNameArea.setAreacd(item);
                shelfNameArea.setShelfNameCd(shelfNameMst.getId());
                setList.add(shelfNameArea);
            });
            logger.info("添加棚名称信息转换后的area参数：{}", setList);
            //添加关联的area
            Map<String, Object> setAreaInfo = shelfNameAreaService.setShelfNameArea(setList, authorCd);

            logger.info("添加棚名称信息保存后返回的信息：{}",setAreaInfo);
        }


        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    /**
     * 获取棚名称Name
     * @param companyCd
     * @return
     */
    @Override
    public Map<String, Object> getShelfName(String companyCd) {
        logger.info("获取棚名称Name的参数：{}",companyCd);
        List<ShelfNameVO> resultInfo = shelfNameMstMapper.selectShelfName(companyCd);
        logger.info("获取棚名称Name的返回值：{}",resultInfo);
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
        logger.info("删除棚名称Name的参数：{}",jsonObject.toString());
        //获取创建者cd
        String authorCd = session.getAttribute("aud").toString();
        // 删除棚名称
        Integer id = Integer.valueOf(String.valueOf(((Map) jsonObject.get("param")).get("id")));
        shelfNameMstMapper.deleteShelfNameInfo(id,authorCd);
        // 查询要删掉的棚patternid
        List<Integer> patternId = shelfNameMstMapper.selectPatternCd(id);
        patternId.forEach(item->{
            logger.info("棚patternid{}",patternId);
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
