package com.trechina.planocycle.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.trechina.planocycle.entity.dto.ShelfPtsDto;
import com.trechina.planocycle.entity.dto.ShelfPtsJoinPatternDto;
import com.trechina.planocycle.entity.po.ShelfPtsData;
import com.trechina.planocycle.entity.vo.ShelfPtsDataHistoryVO;
import com.trechina.planocycle.entity.vo.ShelfPtsNameVO;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.ShelfPtsDataMapper;
import com.trechina.planocycle.service.ShelfPatternService;
import com.trechina.planocycle.service.ShelfPtsService;
import com.trechina.planocycle.utils.ResultMaps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ShelfPtsServiceImpl implements ShelfPtsService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private HttpSession httpSession;
    @Autowired
    private ShelfPtsDataMapper shelfPtsDataMapper;
    @Autowired
    private ShelfPatternService shelfPatternService;

    /**
     * 获取棚割pts信息
     *
     * @param companyCd
     * @return
     */
    @Override
    public Map<String, Object> getShelfPtsInfo(String companyCd, Integer rangFlag, String areaList) {
        logger.info("获取棚割pts信息参数：" + companyCd + "," + areaList);
        String[] strArr = areaList.split(",");
        List<Integer> list = new ArrayList<>();
        if (strArr.length > 0 && !areaList.equals("")) {
            for (int i = 0; i < strArr.length; i++) {
                list.add(Integer.valueOf(strArr[i]));
            }
        }
        logger.info("处理area信息：" + list);
        List<ShelfPtsData> shelfPtsData = shelfPtsDataMapper.selectByPrimaryKey(companyCd, rangFlag, list);
        logger.info("返回棚割pts信息的值：" + shelfPtsData);
        return ResultMaps.result(ResultEnum.SUCCESS, shelfPtsData);
    }

    /**
     * 保存棚割pts数据
     *
     * @param shelfPtsDto
     * @return
     */
    @Override
    public Map<String, Object> setShelfPtsInfo(ShelfPtsDto shelfPtsDto, Integer flg) {
        String authorCd = httpSession.getAttribute("aud").toString();
        Date now = Calendar.getInstance().getTime();
        logger.info("保存棚割pts数据的参数：" + shelfPtsDto);
        ShelfPtsData shelfPtsData = new ShelfPtsData();
        shelfPtsData.setConpanyCd(shelfPtsDto.getCompanyCd());
        shelfPtsData.setFileName(shelfPtsDto.getFileName());
        shelfPtsData.setAuthorcd(authorCd);
        shelfPtsDataMapper.insert(shelfPtsData);
        logger.info("保存后的参数：" + shelfPtsData);
        if (flg == 0) {
            // 查询patternid
            String[] ptsKeyList = shelfPtsData.getFileName().split("_");
            logger.info("返回的ptskey：" + ptsKeyList);

            String ptsKey = "";
            if (ptsKeyList.length > 3) {
                for (int i = 0; i < 4; i++) {
                    ptsKey += ptsKeyList[i] + "_";
                }
            } else {
                ptsKey = "__";
            }
            logger.info("手动组合的ptskey：" + ptsKey);
            List<Integer> patternIdList = shelfPatternService.getpatternIdOfPtsKey(ptsKey.substring(0, ptsKey.length() - 1));
            logger.info("根据组合的ptskey找patternid：" + patternIdList.toString());
            if (!patternIdList.isEmpty()) {
                Integer patternId = patternIdList.get(0);
                logger.info("用到的patternid：" + patternId);
                // 清空patternid
                shelfPtsDataMapper.updateSingle(patternId, authorCd);
                shelfPtsDataMapper.updatePtsHistoryFlgSingle(patternId, authorCd);
                // 写入patternid
                shelfPtsDataMapper.updateShelfPtsOfAutoInner(shelfPtsData.getId(), patternId, authorCd);
                // 写入history
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                ShelfPtsJoinPatternDto shelfPtsJoinPatternDto = new ShelfPtsJoinPatternDto();
                shelfPtsJoinPatternDto.setCompanyCd(shelfPtsDto.getCompanyCd());
                shelfPtsJoinPatternDto.setShelfPtsCd(shelfPtsData.getId());
                shelfPtsJoinPatternDto.setShelfPatternCd(patternId);
                shelfPtsJoinPatternDto.setStartDay(simpleDateFormat.format(now));
                shelfPtsDataMapper.insertPtsHistory(shelfPtsJoinPatternDto, authorCd);
            }
        }
        return ResultMaps.result(ResultEnum.SUCCESS, shelfPtsData.getId());
    }

    /**
     * pts关联pattern
     *
     * @param shelfPtsJoinPatternDto
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> saveShelfPts(List<ShelfPtsJoinPatternDto> shelfPtsJoinPatternDto) {
        logger.info("ptd关联pattern的参数:" + shelfPtsJoinPatternDto);
        // 修改有效无效flg 有效1 无效0 全改为0
//        shelfPtsDataMapper.updateByValidFlg(shelfPtsJoinPatternDto.get(0).getCompanyCd());
        // 修改表数据
        // shujucheck
//        if (shelfPtsDataMapper.checkPtsData(shelfPtsJoinPatternDto) == 0) {
//            return ResultMaps.result(ResultEnum.FAILURE);
//        }

        String authorCd = httpSession.getAttribute("aud").toString();
        shelfPtsDataMapper.updateByPrimaryKey(shelfPtsJoinPatternDto);
        shelfPtsDataMapper.updatePtsHistoryFlg(shelfPtsJoinPatternDto);
        shelfPtsJoinPatternDto.forEach(item -> {

            if (item.getShelfPatternCd() != null) {
                Integer existsCount = shelfPtsDataMapper.selectExistsCount(item);
                if (existsCount == 0) {
                    shelfPtsDataMapper.insertPtsHistory(item, authorCd);
                } else {
                    // 更新表
                    shelfPtsDataMapper.updatePtsHistory(item, authorCd);
                    // 插入表
                }
            }
        });
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    /**
     * pettern别pts关联pattern
     *
     * @param shelfPtsJoinPatternDto
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> saveShelfPtsOfPattern(List<ShelfPtsJoinPatternDto> shelfPtsJoinPatternDto) {
        logger.info("ptd关联pattern的参数:" + shelfPtsJoinPatternDto);
        // 修改有效无效flg 有效1 无效0 全改为0
//        shelfPtsDataMapper.updateByValidFlg(shelfPtsJoinPatternDto.get(0).getCompanyCd());
        // 表数据都职位空
        shelfPtsDataMapper.updateAll(shelfPtsJoinPatternDto);
        // shujucheck
//        if (shelfPtsDataMapper.checkPatternData(shelfPtsJoinPatternDto) == 0) {
//            return ResultMaps.result(ResultEnum.FAILURE);
//        }
        String authorCd = httpSession.getAttribute("aud").toString();
        // 修改表数据
        shelfPtsDataMapper.updateByPrimaryKeyOfPattern(shelfPtsJoinPatternDto);
        shelfPtsDataMapper.updatePtsHistoryFlg(shelfPtsJoinPatternDto);
        shelfPtsJoinPatternDto.forEach(item -> {
            if (item.getShelfPtsCd() != null) {
                Integer existsCount = shelfPtsDataMapper.selectExistsCount(item);
                if (existsCount == 0) {
                    shelfPtsDataMapper.insertPtsHistory(item, authorCd);
                } else {
                    // 更新表
                    shelfPtsDataMapper.updatePtsHistory(item, authorCd);
                    // 插入表
                }
            }
        });
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    /**
     * 删除棚割pts信息
     *
     * @param
     * @return
     */
    @Override
    public Map<String, Object> delShelfPtsInfo(JSONObject jsonObject) {
        if (((Map) jsonObject.get("param")).get("id") != null) {
            Integer id = Integer.valueOf(String.valueOf(((Map) jsonObject.get("param")).get("id")));
            //获取用户id
            String authorCd = httpSession.getAttribute("aud").toString();

            shelfPtsDataMapper.delShelfPtsInfo(id, authorCd);

            shelfPtsDataMapper.delShelfHistoryInfo(id, authorCd);

        }
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    /**
     * 获取棚pattern关联过的csv履历数据
     *
     * @param companyCd
     * @return
     */
    @Override
    public Map<String, Object> getHistoryData(String companyCd) {
        List<ShelfPtsDataHistoryVO> shelfPtsDataHistoryVOList = shelfPtsDataMapper.selectHistoryData(companyCd);
        return ResultMaps.result(ResultEnum.SUCCESS, shelfPtsDataHistoryVOList);
    }

    /**
     * 棚pattern关联pts的下拉框数据
     *
     * @param companyCd
     * @return
     */
    @Override
    public Map<String, Object> getPtsName(String companyCd) {
        List<ShelfPtsNameVO> shelfPtsNameVOList = shelfPtsDataMapper.selectPtsName(companyCd);
        return ResultMaps.result(ResultEnum.SUCCESS, shelfPtsNameVOList);
    }

    /**
     * 获取棚pattern别的pts信息
     *
     * @param companyCd
     * @param rangFlag
     * @param areaList
     * @return
     */
    @Override
    public Map<String, Object> getPtsInfoOfPattern(String companyCd, Integer rangFlag, String areaList) {
        logger.info("获取棚pattern别的pts信息参数：" + companyCd + "," + areaList);
        String[] strArr = areaList.split(",");
        List<Integer> list = new ArrayList<>();
        if (strArr.length > 0 && !areaList.equals("")) {
            for (int i = 0; i < strArr.length; i++) {
                list.add(Integer.valueOf(strArr[i]));
            }
        }
        logger.info("处理area信息：" + list);
        List<ShelfPtsData> shelfPtsNameVOList = shelfPtsDataMapper.selectPtsInfoOfPattern(companyCd, rangFlag, list);
        return ResultMaps.result(ResultEnum.SUCCESS, shelfPtsNameVOList);
    }
}
