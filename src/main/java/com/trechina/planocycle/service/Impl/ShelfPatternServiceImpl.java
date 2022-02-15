package com.trechina.planocycle.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.trechina.planocycle.entity.dto.ShelfPatternDto;
import com.trechina.planocycle.entity.po.ShelfPatternArea;
import com.trechina.planocycle.entity.po.ShelfPatternBranch;
import com.trechina.planocycle.entity.po.ShelfPatternMst;
import com.trechina.planocycle.entity.vo.ShelfPatternBranchVO;
import com.trechina.planocycle.entity.vo.ShelfPatternNameVO;
import com.trechina.planocycle.entity.vo.ShelfPatternTreeVO;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.ShelfPatternBranchMapper;
import com.trechina.planocycle.mapper.ShelfPatternMstMapper;
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
import java.util.List;
import java.util.Map;
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

    /**
     * 获取棚pattern信息
     * @param companyCd
     * @return
     */
    @Override
    public Map<String, Object> getShelfPatternInfo(String companyCd) {
        logger.info("获取棚pattern信息的参数："+companyCd);
        List<ShelfPatternMst> resultInfo = shelfPatternMstMapper.selectByPrimaryKey(companyCd);
        logger.info("获取棚pattern信息的返回值："+resultInfo);
        return ResultMaps.result(ResultEnum.SUCCESS,resultInfo);
    }

    /**
     * 保存棚pattern信息
     * @param shelfPatternDto
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> setShelfPatternInfo(ShelfPatternDto shelfPatternDto) {
        logger.info("保存棚pattern信息的参数："+shelfPatternDto);
        // 名称check 同一个棚名称棚パータン名唯一
        List<Integer> result = shelfPatternMstMapper.selectDistinctName(shelfPatternDto);
        if (result!=null && result.size()>0){
            return ResultMaps.result(ResultEnum.NAMEISEXISTS);
        }
        List<ShelfPatternArea> list = new ArrayList<>();
        ShelfPatternMst shelfPatternMst = new ShelfPatternMst();
        shelfPatternMst.setConpanyCd(shelfPatternDto.getCompanyCd());
        shelfPatternMst.setShelfNameCd(shelfPatternDto.getShelfNameCD());
        shelfPatternMst.setShelfPatternName(shelfPatternDto.getShelfPatternName());
        shelfPatternMst.setPtsRelationID(shelfPatternDto.getPtsRelationID());
//        shelfPatternMst.setArea(item);
        shelfPatternMst.setAuthorCd(session.getAttribute("aud").toString());
        shelfPatternMst.setMaintainerCd(session.getAttribute("aud").toString());
        //获取用户id
        String authorCd = session.getAttribute("aud").toString();
        logger.info("保存pattern信息转换后的参数："+shelfPatternMst);
        try {
            Integer resultInfo = shelfPatternMstMapper.insert(shelfPatternMst);
            logger.info("保存棚名称信息保存后返回的信息：" + resultInfo);

            shelfPatternDto.getArea().forEach(item -> {
                ShelfPatternArea shelfPatternArea = new ShelfPatternArea();
                shelfPatternArea.setCompanyCd(shelfPatternDto.getCompanyCd());
                shelfPatternArea.setShelfPatternCd(shelfPatternMst.getShelfPatternCd());

                shelfPatternArea.setAreacd(item);
                list.add(shelfPatternArea);
            });
            logger.info("保存pattern信息转换后的area参数："+list);
            shelfPatternAreaService.setShelfPatternArea(list,authorCd);
        } catch (Exception e) {
            logger.error(e.toString());
            return ResultMaps.result(ResultEnum.FAILURE);
        }
        return ResultMaps.result(ResultEnum.SUCCESS);
    }
    /**
     * 修改棚pattern信息
     * @param shelfPatternDto
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> updateShelfPatternInfo(ShelfPatternDto shelfPatternDto) {
        logger.info("修改棚pattern信息的参数："+shelfPatternDto);
        // 名称check 同一个棚名称棚パータン名唯一
        List<Integer> resultNum = shelfPatternMstMapper.selectDistinctName(shelfPatternDto);
        if (resultNum!=null &&resultNum.size()>1){
            return ResultMaps.result(ResultEnum.NAMEISEXISTS);
        }
        //要删除的集合
        List<ShelfPatternArea> delList = new ArrayList<>();
        //要添加的集合
        List<ShelfPatternArea> setList = new ArrayList<>();
        ShelfPatternMst shelfPatternMst = new ShelfPatternMst();
        shelfPatternMst.setShelfPatternCd(shelfPatternDto.getShelfPatternCd());
        shelfPatternMst.setConpanyCd(shelfPatternDto.getCompanyCd());
        shelfPatternMst.setShelfNameCd(shelfPatternDto.getShelfNameCD());
        shelfPatternMst.setShelfPatternName(shelfPatternDto.getShelfPatternName());
        shelfPatternMst.setPtsRelationID(shelfPatternDto.getPtsRelationID());
        shelfPatternMst.setMaintainerCd(session.getAttribute("aud").toString());
        //获取用户id
        String authorCd = session.getAttribute("aud").toString();
        logger.info("修改pattern信息转换后的参数："+shelfPatternMst);
        try {
            int resultInfo = shelfPatternMstMapper.update(shelfPatternMst);
            logger.info("保存棚名称信息保存后返回的信息：" + resultInfo);
            //获取棚pattern关联的Area
            List<Integer> getShelfPatternArea = shelfPatternAreaService.getShelfPatternArea(shelfPatternMst.getShelfPatternCd(),shelfPatternMst.getConpanyCd());
            //数据库中和修改重复数据
            shelfPatternDto.getArea().forEach(item->{
                for (Integer area : getShelfPatternArea) {
                    if (item.equals(area)){
                        shelfPatternAreaService.setDelFlg(item,shelfPatternDto.getShelfPatternCd(),authorCd);
                    }
                }
            });
            //要删除的area集合
            List<Integer> deleteAreaList = ListDisparityUtils.getListDisparit(getShelfPatternArea, shelfPatternDto.getArea());
            //要新增area的集合
            List<Integer> setAreaList = ListDisparityUtils.getListDisparit( shelfPatternDto.getArea(),getShelfPatternArea);
            if (deleteAreaList.size()>0){
                deleteAreaList.forEach(item -> {
                    ShelfPatternArea shelfPatternArea = new ShelfPatternArea();
                    shelfPatternArea.setCompanyCd(shelfPatternDto.getCompanyCd());
                    shelfPatternArea.setShelfPatternCd(shelfPatternMst.getShelfPatternCd());
                    shelfPatternArea.setAreacd(item);

                    delList.add(shelfPatternArea);
                });
                logger.info("删除棚pattern信息转换后的area参数："+delList);

                // 删除棚pattern关联的area

                    shelfPatternAreaService.deleteAreaCd(deleteAreaList,shelfPatternDto.getShelfPatternCd(),authorCd);

            }
            if (setAreaList.size()>0) {
                setAreaList.forEach(item -> {
                    ShelfPatternArea shelfPatternArea = new ShelfPatternArea();
                    shelfPatternArea.setCompanyCd(shelfPatternDto.getCompanyCd());
                    shelfPatternArea.setShelfPatternCd(shelfPatternMst.getShelfPatternCd());
                    shelfPatternArea.setAreacd(item);
                    setList.add(shelfPatternArea);
                });
                logger.info("添加棚pattern信息转换后的area参数：" + setList);
                Map<String, Object> setAreaInfo = shelfPatternAreaService.setShelfPatternArea(setList, authorCd);
                logger.info("修改棚名称信息保存后返回的信息："+setAreaInfo);
            }

        }catch (Exception e) {
            logger.error(e.toString());
            return ResultMaps.result(ResultEnum.FAILURE);
        }
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    /**通过棚名称棚pattern
     * @param companyCd
     * @param shelfNameCd
     * @return
     */
    @Override
    public List<Integer> getShelfPattern(String companyCd, Integer shelfNameCd) {
        return  shelfPatternMstMapper.getShelfPattern(companyCd,shelfNameCd);

    }

    /**
     * 获取棚pattern关联的店cd
     * @param shelfPatternCd
     * @return
     */
    @Override
    public Map<String, Object> getShelfPatternBranch(Integer shelfPatternCd) {
        logger.info("获取棚pattern关联的店cd的参数："+shelfPatternCd);
        List<ShelfPatternBranch> list = shelfPatternBranchMapper.selectByPrimaryKey(shelfPatternCd);
        logger.info("获取棚pattern关联的店cd："+list);
        ShelfPatternBranchVO shelfPatternBranchVO=new ShelfPatternBranchVO();
        List<String> branchList = new ArrayList<>();
        if (list.size() > 0) {
            list.forEach(item -> {
                branchList.add(String.valueOf(item.getBranch()));
            });
            shelfPatternBranchVO.setBranchCd(branchList);
            shelfPatternBranchVO.setShelfPatternCd(list.get(0).getShelfPattrenCd());
            shelfPatternBranchVO.setStartTime(list.get(0).getStartTime());
            logger.info("获取棚pattern关联的店cd转换类型后："+shelfPatternBranchVO);
            return ResultMaps.result(ResultEnum.SUCCESS,shelfPatternBranchVO);
        }else {
            return ResultMaps.result(ResultEnum.SUCCESS,null);
        }
    }

    /**
     * 保存棚pattern关联的店cd
     * @param shelfPatternBranchVO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> setShelfPatternBranch(ShelfPatternBranchVO shelfPatternBranchVO) {
        logger.info("保存棚pattern关联的店cd的参数："+shelfPatternBranchVO);
        //获取创建者cd
        String authorCd = session.getAttribute("aud").toString();
        //要删除的集合
        List<ShelfPatternBranch> delList = new ArrayList<>();
        //要添加的集合
        List<ShelfPatternBranch> setList = new ArrayList<>();
        //获取棚pattern关联的branch
        List<String> getShelfPatternBranch = shelfPatternBranchMapper.getShelfPatternBranch(shelfPatternBranchVO.getShelfPatternCd());
        //数据库中和修改重复数据
        shelfPatternBranchVO.getBranchCd().forEach(item->{
            for (String shelfPatternBranch : getShelfPatternBranch) {
                if (item.equals(shelfPatternBranch)){
                    shelfPatternBranchMapper.setDelFlg(item,shelfPatternBranchVO.getShelfPatternCd(),authorCd);
                }
            }

        });
        //要删除的area集合
        List<String> delBranchList = ListDisparityUtils.getListDisparitStr(getShelfPatternBranch, shelfPatternBranchVO.getBranchCd());
        //要新增area的集合
        List<String> setBranchList = ListDisparityUtils.getListDisparitStr( shelfPatternBranchVO.getBranchCd(),getShelfPatternBranch);

      if (delBranchList.size()>0){
          delBranchList.forEach(item->{
              ShelfPatternBranch shelfPatternBranch = new ShelfPatternBranch();
              shelfPatternBranch.setShelfPattrenCd(shelfPatternBranchVO.getShelfPatternCd());
              shelfPatternBranch.setBranch(item);
              shelfPatternBranch.setStartTime(shelfPatternBranchVO.getStartTime());
              delList.add(shelfPatternBranch);
          });
          //删除关联的branch

              shelfPatternBranchMapper.deleteBranchCd(delBranchList,shelfPatternBranchVO.getShelfPatternCd(),authorCd);

      }

      if (setBranchList.size()>0){
          setBranchList.forEach(item->{
              ShelfPatternBranch shelfPatternBranch = new ShelfPatternBranch();
              shelfPatternBranch.setShelfPattrenCd(shelfPatternBranchVO.getShelfPatternCd());
              shelfPatternBranch.setBranch(item);
              shelfPatternBranch.setStartTime(shelfPatternBranchVO.getStartTime());
              setList.add(shelfPatternBranch);
          });
          shelfPatternBranchMapper.insert(setList,authorCd);
      }

        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    /**
     * 获取所有pattern的name
     * @param companyCd
     * @return
     */
    @Override
    public Map<String, Object> getShelfPatternName(String companyCd) {
        logger.info("获取所有棚pattern的name的参数："+companyCd);
        List<ShelfPatternNameVO> shelfPatternNameVO = shelfPatternMstMapper.selectPatternName(companyCd);
        return ResultMaps.result(ResultEnum.SUCCESS,shelfPatternNameVO);
    }

    /**
     * 获取关联了店铺的棚pattern的name（优先顺位表用）
     * @param companyCd
     * @return
     */
    @Override
    public Map<String, Object> getShelfPatternNameBranch(String companyCd) {
        logger.info("获取关联了店铺的棚pattern的name的参数："+companyCd);
        List<ShelfPatternTreeVO> shelfPatternNameVOS = shelfPatternMstMapper.selectPatternNameBranch(companyCd);
        return ResultMaps.result(ResultEnum.SUCCESS,shelfPatternNameVOS);
    }

    /**
     * 删除棚pattern
     *
     * @param jsonObject
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> delShelfPatternInfo(JSONObject jsonObject) {
        logger.info("删除棚pattern的参数："+jsonObject.toString());
        if (((Map) jsonObject.get("param")).get("id")!=null ){
            Integer id = Integer.valueOf(String.valueOf(((Map) jsonObject.get("param")).get("id")));
            //获取创建者cd
            String authorCd = session.getAttribute("aud").toString();
            // 删除棚pattern
            shelfPatternMstMapper.deleteByShelfName(id,authorCd);
            // 删除关联的店
            shelfPatternBranchMapper.deleteByPrimaryKey(id,authorCd);
            // 删除棚pattern关联的area
            shelfPatternAreaService.delShelfPatternArea(id,authorCd);
            // 修改管理那的棚pts
            shelfPatternMstMapper.updateByPtsForShelfPdCd(id,authorCd);
            // 修改履历表的棚pts
            shelfPatternMstMapper.deleteShelfPdCdHistory(id,authorCd);
        }

        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    /**
     * 获取shelfPattern 1@棚パータン名称１,2@棚パータン名称2 格式的字符串
     *
     * @param shelfPatternNo
     * @return
     */
    @Override
    public String getShePatternNoNm(String shelfPatternNo) {

        return shelfPatternMstMapper.selectByShePatternNoNm(shelfPatternNo);
    }

    /**
     * 根据ptsKey获取patternid
     *
     * @param ptsKey
     * @return
     */
    @Override
    public List<Integer> getpatternIdOfPtsKey(String ptsKey) {
        return shelfPatternMstMapper.getpatternIdOfPtsKey(ptsKey);
    }
}
