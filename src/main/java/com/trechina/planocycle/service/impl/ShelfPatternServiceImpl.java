package com.trechina.planocycle.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Sets;
import com.trechina.planocycle.entity.dto.GetCommonPartsDataDto;
import com.trechina.planocycle.entity.dto.ShelfPatternDto;
import com.trechina.planocycle.entity.po.ShelfPatternBranch;
import com.trechina.planocycle.entity.po.ShelfPatternMst;
import com.trechina.planocycle.entity.vo.ShelfNamePatternVo;
import com.trechina.planocycle.entity.vo.ShelfPatternBranchVO;
import com.trechina.planocycle.entity.vo.ShelfPatternNameVO;
import com.trechina.planocycle.entity.vo.ShelfPatternTreeVO;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.ShelfNameMstMapper;
import com.trechina.planocycle.mapper.ShelfPatternBranchMapper;
import com.trechina.planocycle.mapper.ShelfPatternMstMapper;
import com.trechina.planocycle.service.BasicPatternMstService;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ShelfPatternServiceImpl implements ShelfPatternService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private HttpSession session;
    @Autowired
    private ShelfPatternMstMapper shelfPatternMstMapper;
    @Autowired
    private ShelfPatternBranchMapper shelfPatternBranchMapper;
    @Autowired
    private ShelfPatternAreaService shelfPatternAreaService;
    @Autowired
    private ShelfNameMstMapper shelfNameMstMapper;
    @Autowired
    private BasicPatternMstService basicPatternMstService;

    /**
     * ???pattern???????????????
     * @param companyCd
     * @return
     */
    @Override
    public Map<String, Object> getShelfPatternInfo(String companyCd) {
        logger.info("???pattern????????????????????????????????????{}",companyCd);
        List<ShelfPatternMst> resultInfo = shelfPatternMstMapper.selectByPrimaryKey(companyCd);
        List<String> commonPartsDataList = resultInfo.stream().distinct().map(ShelfPatternMst::getCommonPartsData).collect(Collectors.toList());
        List<String> list = new ArrayList<>();
        for (String s : commonPartsDataList) {
            GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(s, companyCd);
            list.add(commonTableName.getStoreInfoTable());
        }
        Set<String> existSpecialUse = shelfPatternMstMapper.getExistSpecialUse(list);
        resultInfo.stream().forEach(result -> {
            if (result.getStoreCdStr()==null) {
                result.setStoreCd(new String[]{});
                result.setBranchNum(0);
                result.setSpecialFlag("-");
            }else{
                String storeCdStr = result.getStoreCdStr();
                String[] storeCdStrList = storeCdStr.split(",");
                long count = Sets.intersection(existSpecialUse, Arrays.stream(storeCdStrList).collect(Collectors.toSet())).stream().count();
                result.setStoreCd(storeCdStrList);
                result.setBranchNum(storeCdStrList.length);
                result.setSpecialFlag(count == 0 ? "-" : "???");
            }
        });
        logger.info("???pattern??????????????????????????????{}",resultInfo);
        return ResultMaps.result(ResultEnum.SUCCESS,resultInfo);
    }

    /**
     * ?????????pattern??????
     * @param shelfPatternDto
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> setShelfPatternInfo(ShelfPatternDto shelfPatternDto) {
        logger.info("???pattern???????????????????????????????????????{}",shelfPatternDto);
        // ??????check ??????????????????????????????????????????
        Integer result = shelfPatternMstMapper.selectDistinctName(shelfPatternDto);
        if (result!=null){
            return ResultMaps.result(ResultEnum.NAMEISEXISTS);
        }
        ShelfPatternMst shelfPatternMst = new ShelfPatternMst();
        shelfPatternMst.setConpanyCd(shelfPatternDto.getCompanyCd());
        shelfPatternMst.setShelfNameCd(shelfPatternDto.getShelfNameCD());
        shelfPatternMst.setShelfPatternName(shelfPatternDto.getShelfPatternName());
        shelfPatternMst.setPtsRelationID(shelfPatternDto.getPtsRelationID());
        shelfPatternMst.setAuthorCd(session.getAttribute("aud").toString());
        shelfPatternMst.setMaintainerCd(session.getAttribute("aud").toString());
        shelfPatternMst.setCommonPartsData(shelfPatternDto.getCommonPartsData());
        //????????????authorid
        String authorCd = session.getAttribute("aud").toString();
        logger.info("pattern?????????????????????????????????????????????{}",shelfPatternMst);
        try {
            Integer resultInfo = shelfPatternMstMapper.insert(shelfPatternMst);
            logger.info("?????????????????????????????????????????????{}" ,resultInfo);
            String[] storeCd = shelfPatternDto.getStoreCd();
            List<ShelfPatternBranch> branchList = new ArrayList<>();
            ShelfPatternBranch shelfPatternBranch = null;
            for (String cd : storeCd) {
                shelfPatternBranch = new ShelfPatternBranch();
                shelfPatternBranch.setBranch(cd);
                shelfPatternBranch.setStartTime(new Date());
                shelfPatternBranch.setShelfPatternCd(shelfPatternMst.getShelfPatternCd());
                branchList.add(shelfPatternBranch);
            }
            shelfPatternBranchMapper.deleteByPatternCd(shelfPatternMst.getShelfPatternCd());
            if(!branchList.isEmpty()){
                shelfPatternBranchMapper.insert(branchList, authorCd);
            }
        } catch (Exception e) {
            logger.error("",e);
        }
        return ResultMaps.result(ResultEnum.SUCCESS);
    }
    /**
     * ?????????pattern??????
     * @param shelfPatternDto
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> updateShelfPatternInfo(ShelfPatternDto shelfPatternDto) {
        logger.info("???pattern????????????????????????????????????{}",shelfPatternDto);
        // ??????check ??????????????????????????????????????????
        Integer resultNum = shelfPatternMstMapper.selectDistinctName(shelfPatternDto);
        if (!shelfPatternDto.getShelfPatternCd().equals(resultNum) && resultNum != null){
            return ResultMaps.result(ResultEnum.NAMEISEXISTS);
        }
        //??????????????????????????????

        //??????????????????????????????
        ShelfPatternMst shelfPatternMst = new ShelfPatternMst();
        shelfPatternMst.setShelfPatternCd(shelfPatternDto.getShelfPatternCd());
        shelfPatternMst.setConpanyCd(shelfPatternDto.getCompanyCd());
        shelfPatternMst.setShelfNameCd(shelfPatternDto.getShelfNameCD());
        shelfPatternMst.setShelfPatternName(shelfPatternDto.getShelfPatternName());
        shelfPatternMst.setPtsRelationID(shelfPatternDto.getPtsRelationID());
        shelfPatternMst.setMaintainerCd(session.getAttribute("aud").toString());
        shelfPatternMst.setCommonPartsData(shelfPatternDto.getCommonPartsData());
        //????????????ID?????????
        String authorCd = session.getAttribute("aud").toString();
        logger.info("??????pattern???????????????????????????{}",shelfPatternMst);
        try {
            int resultInfo = shelfPatternMstMapper.update(shelfPatternMst);
            logger.info("????????????????????????????????????????????????{}" , resultInfo);
            //???????????????pattern?????????Area
            String[] storeCd = shelfPatternDto.getStoreCd();
            List<ShelfPatternBranch> branchList = new ArrayList<>();
            ShelfPatternBranch shelfPatternBranch = null;
            for (String cd : storeCd) {
                shelfPatternBranch = new ShelfPatternBranch();
                shelfPatternBranch.setBranch(cd);
                shelfPatternBranch.setAuthorCd(authorCd);
                shelfPatternBranch.setStartTime(new Date());
                shelfPatternBranch.setShelfPatternCd(shelfPatternMst.getShelfPatternCd());
                branchList.add(shelfPatternBranch);
            }
            shelfPatternBranchMapper.deleteByPatternCd(shelfPatternMst.getShelfPatternCd());
            if(!branchList.isEmpty()){
                shelfPatternBranchMapper.insert(branchList, authorCd);
            }

        }catch (Exception e) {
            logger.error("",e);

        }
        return ResultMaps.result(ResultEnum.SUCCESS);
    }


    /**
     * ???pattern?????????cd?????????
     * @param shelfPatternCd
     * @return
     */
    @Override
    public Map<String, Object> getShelfPatternBranch(Integer shelfPatternCd) {
        logger.info("???????????????pattern????????????cd????????????{}",shelfPatternCd);
        List<ShelfPatternBranch> list = shelfPatternBranchMapper.selectByPrimaryKey(shelfPatternCd);
        logger.info("???????????????pattern????????????cd???{}",list);
        ShelfPatternBranchVO shelfPatternBranchVO=new ShelfPatternBranchVO();
        List<String> branchList = new ArrayList<>();
        if (!list.isEmpty()) {
            list.forEach(item -> branchList.add(String.valueOf(item.getBranch())));
            shelfPatternBranchVO.setBranchCd(branchList);
            shelfPatternBranchVO.setShelfPatternCd(list.get(0).getShelfPatternCd());
            shelfPatternBranchVO.setStartTime(list.get(0).getStartTime());
            logger.info("???????????????pattern????????????cd????????????????????????{}",shelfPatternBranchVO);
            return ResultMaps.result(ResultEnum.SUCCESS,shelfPatternBranchVO);
        }else {
            return ResultMaps.result(ResultEnum.SUCCESS,null);
        }
    }

    /**
     * ?????????pattern?????????cd
     * @param shelfPatternBranchVO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> setShelfPatternBranch(ShelfPatternBranchVO shelfPatternBranchVO) {
        logger.info("?????????pattern????????????cd????????????{}",shelfPatternBranchVO);
        //?????????cd?????????
        String authorCd = session.getAttribute("aud").toString();
        //??????????????????????????????
        List<ShelfPatternBranch> delList = new ArrayList<>();
        //??????????????????????????????
        List<ShelfPatternBranch> setList = new ArrayList<>();
        //???pattern??????branch?????????
        List<String> getShelfPatternBranch = shelfPatternBranchMapper.getShelfPatternBranch(shelfPatternBranchVO.getShelfPatternCd());
        //?????????????????????????????????????????????
        shelfPatternBranchVO.getBranchCd().forEach(item->{
            for (String shelfPatternBranch : getShelfPatternBranch) {
                if (item.equals(shelfPatternBranch)){
                    shelfPatternBranchMapper.setDelFlg(item,shelfPatternBranchVO.getShelfPatternCd(),authorCd);
                }
            }

        });
        //????????????area??????????????????
        List<String> delBranchList = ListDisparityUtils.getListDisparitStr(getShelfPatternBranch, shelfPatternBranchVO.getBranchCd());
        //area??????????????????????????????
        List<String> setBranchList = ListDisparityUtils.getListDisparitStr( shelfPatternBranchVO.getBranchCd(),getShelfPatternBranch);

      if (!delBranchList.isEmpty()){
          delBranchList.forEach(item->{
              ShelfPatternBranch shelfPatternBranch = new ShelfPatternBranch();
              shelfPatternBranch.setShelfPatternCd(shelfPatternBranchVO.getShelfPatternCd());
              shelfPatternBranch.setBranch(item);
              shelfPatternBranch.setStartTime(shelfPatternBranchVO.getStartTime());
              delList.add(shelfPatternBranch);
          });
          //??????branch?????????

              shelfPatternBranchMapper.deleteBranchCd(delBranchList,shelfPatternBranchVO.getShelfPatternCd(),authorCd);

      }

      if (!setBranchList.isEmpty()){
          setBranchList.forEach(item->{
              ShelfPatternBranch shelfPatternBranch = new ShelfPatternBranch();
              shelfPatternBranch.setShelfPatternCd(shelfPatternBranchVO.getShelfPatternCd());
              shelfPatternBranch.setBranch(item);
              shelfPatternBranch.setStartTime(shelfPatternBranchVO.getStartTime());
              setList.add(shelfPatternBranch);
          });
          shelfPatternBranchMapper.insert(setList,authorCd);
      }

        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    /**
     * ???????????????pattern???name?????????
     * @param companyCd
     * @return
     */
    @Override
    public Map<String, Object> getShelfPatternName(String companyCd) {
        logger.info("?????????????????????pattern???name????????????{}",companyCd);
        List<ShelfPatternNameVO> shelfPatternNameVO = shelfPatternMstMapper.selectPatternName(companyCd);
        return ResultMaps.result(ResultEnum.SUCCESS,shelfPatternNameVO);
    }

    /**
     * ?????????????????????????????????pattern???name?????????????????????????????????
     * @param companyCd
     * @return
     */
    @Override
    public Map<String, Object> getShelfPatternNameBranch(String companyCd) {
        logger.info("?????????????????????????????????pattern???name????????????{}",companyCd);
        List<ShelfPatternTreeVO> shelfPatternNameVOS = shelfPatternMstMapper.selectPatternNameBranch(companyCd);
        return ResultMaps.result(ResultEnum.SUCCESS,shelfPatternNameVOS);
    }

    /**
     * ????????????
     *
     * @param jsonObject
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> delShelfPatternInfo(JSONObject jsonObject) {
        logger.info("?????????pattern????????????{}",jsonObject);
        if (((Map) jsonObject.get("param")).get("id")!=null ){
            Integer id = Integer.valueOf(String.valueOf(((Map) jsonObject.get("param")).get("id")));
            //?????????cd?????????
            String authorCd = session.getAttribute("aud").toString();
            // ????????????
            shelfPatternMstMapper.deleteByShelfName(id,authorCd);
            // ??????????????????
            shelfPatternBranchMapper.deleteByPrimaryKey(id,authorCd);
            // ???pattern??????area?????????
            shelfPatternAreaService.delShelfPatternArea(id,authorCd);
            // ??????????????????pts???????????????
            shelfPatternMstMapper.updateByPtsForShelfPdCd(id,authorCd);
            // ???????????????pts?????????
            shelfPatternMstMapper.deleteShelfPdCdHistory(id,authorCd);
        }

        return ResultMaps.result(ResultEnum.SUCCESS);
    }



    @Override
    public List<Integer> getpatternIdOfFilename(String fileName, String companyCd) {
        return shelfPatternMstMapper.getpatternIdOfFilename(fileName,companyCd);
    }

    @Override
    public Map<String, Object> getShelfPatternForArea(String companyCd) {
        List<ShelfNamePatternVo> shelfPatternForArea = shelfPatternMstMapper.getShelfPatternForArea(companyCd);
        return ResultMaps.result(ResultEnum.SUCCESS,shelfPatternForArea);
    }


    /**
     * ???????????????pattern
     * @param shelfPatternDto
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> setPatternList(List<ShelfPatternDto> shelfPatternDto) {
        String companyCd = shelfPatternDto.get(0).getCompanyCd();
        List<String> patternName = shelfPatternMstMapper.getPatternName(shelfPatternDto,companyCd);
        if (!patternName.isEmpty()){
            return ResultMaps.result(ResultEnum.SUCCESS,patternName);
        }

        String authorCd = session.getAttribute("aud").toString();
        shelfPatternMstMapper.setPatternList(shelfPatternDto,companyCd,authorCd);
        List<ShelfPatternBranch> branchList = new ArrayList<>();
        for (ShelfPatternDto patternDto : shelfPatternDto) {
            ShelfPatternBranch shelfPatternBranch= null;
            for (String cd : patternDto.getStoreCd()) {
                shelfPatternBranch = new ShelfPatternBranch();
                shelfPatternBranch.setBranch(cd);
                shelfPatternBranch.setStartTime(new Date());
                shelfPatternBranch.setShelfPatternCd(patternDto.getShelfPatternCd());
                branchList.add(shelfPatternBranch);
                if (branchList.size()==5000){
                    shelfPatternBranchMapper.insert(branchList, authorCd);
                    branchList.clear();
                }
            }

        }
        if(!branchList.isEmpty()){
            shelfPatternBranchMapper.insert(branchList, authorCd);
        }
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    @Override
    public Map<String, Object> getPatternForStorel(String companyCd, String storeIsCore) {
        List<String> commonPartsData = shelfPatternMstMapper.getCommonPartsData(companyCd);
        Map<String,Object> map = new HashMap<>();
        for (int i = 0; i < commonPartsData.size(); i++) {
            GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(commonPartsData.get(i), companyCd);
            map.put("a"+i,commonTableName.getStoreInfoTable());
        }
        if (commonPartsData.isEmpty()){
            return ResultMaps.result(ResultEnum.SUCCESS,new ArrayList<>());
        }
        List<ShelfPatternNameVO> patternForStorel = shelfPatternMstMapper.getPatternForStorel(storeIsCore, companyCd,map);
        for (ShelfPatternNameVO shelfPatternNameVO : patternForStorel) {
            String prodIsCore = shelfPatternNameVO.getStoreIsCore();
            JSONObject jsonObject = JSON.parseObject(prodIsCore);
            shelfPatternNameVO.setStoreIsCore(jsonObject.get("storeIsCore").toString());
        }
        return ResultMaps.result(ResultEnum.SUCCESS,patternForStorel);
    }

    @Override
    public Map<String, Object> getPatternForNoStore(String companyCd, String storeIsCore) {
        List<String> commonPartsData = shelfPatternMstMapper.getCommonPartsData(companyCd);
        Map<String,Object> map = new HashMap<>();
        for (int i = 0; i < commonPartsData.size(); i++) {
            GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(commonPartsData.get(i), companyCd);
            map.put("a"+i,commonTableName.getStoreInfoTable());
        }
        if (commonPartsData.isEmpty()){
            return ResultMaps.result(ResultEnum.SUCCESS,new ArrayList<>());
        }
        List<ShelfPatternNameVO> patternForStorel = shelfPatternMstMapper.getPatternForNoStore(storeIsCore, companyCd,map);
        for (ShelfPatternNameVO shelfPatternNameVO : patternForStorel) {
            String prodIsCore = shelfPatternNameVO.getStoreIsCore();
            JSONObject jsonObject = JSON.parseObject(prodIsCore);
            shelfPatternNameVO.setStoreIsCore(jsonObject.get("storeIsCore").toString());
        }
        return ResultMaps.result(ResultEnum.SUCCESS,patternForStorel);
    }

    @Override
    public Map<String, Object> getComparePattern(Integer priorityOrderCd) {
        List<Map<String, Object>> comparePatternList = shelfPatternMstMapper.selectComparePatternList(priorityOrderCd);
        return ResultMaps.result(ResultEnum.SUCCESS, comparePatternList);
    }
}
