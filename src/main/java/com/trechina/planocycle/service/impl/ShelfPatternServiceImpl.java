package com.trechina.planocycle.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.trechina.planocycle.entity.dto.ShelfPatternDto;
import com.trechina.planocycle.entity.po.ShelfPatternArea;
import com.trechina.planocycle.entity.po.ShelfPatternBranch;
import com.trechina.planocycle.entity.po.ShelfPatternMst;
import com.trechina.planocycle.entity.vo.ShelfNamePatternVo;
import com.trechina.planocycle.entity.vo.ShelfPatternBranchVO;
import com.trechina.planocycle.entity.vo.ShelfPatternNameVO;
import com.trechina.planocycle.entity.vo.ShelfPatternTreeVO;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.exception.BusinessException;
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
     * 棚pattern情報の取得
     * @param companyCd
     * @return
     */
    @Override
    public Map<String, Object> getShelfPatternInfo(String companyCd) {
        logger.info("棚pattern情報のパラメータの取得：{}",companyCd);
        List<ShelfPatternMst> resultInfo = shelfPatternMstMapper.selectByPrimaryKey(companyCd);
        logger.info("棚pattern情報の戻り値の取得：{}",resultInfo);
        return ResultMaps.result(ResultEnum.SUCCESS,resultInfo);
    }

    /**
     * 保存棚pattern情報
     * @param shelfPatternDto
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> setShelfPatternInfo(ShelfPatternDto shelfPatternDto) {
        logger.info("棚pattern情報を保存するパラメータ：{}",shelfPatternDto);
        // 名称check 同一个棚名称棚パータン名唯一
        List<Integer> result = shelfPatternMstMapper.selectDistinctName(shelfPatternDto);
        if (result!=null && !result.isEmpty()){
            return ResultMaps.result(ResultEnum.NAMEISEXISTS);
        }
        List<ShelfPatternArea> list = new ArrayList<>();
        ShelfPatternMst shelfPatternMst = new ShelfPatternMst();
        shelfPatternMst.setConpanyCd(shelfPatternDto.getCompanyCd());
        shelfPatternMst.setShelfNameCd(shelfPatternDto.getShelfNameCD());
        shelfPatternMst.setShelfPatternName(shelfPatternDto.getShelfPatternName());
        shelfPatternMst.setPtsRelationID(shelfPatternDto.getPtsRelationID());
        shelfPatternMst.setAuthorCd(session.getAttribute("aud").toString());
        shelfPatternMst.setMaintainerCd(session.getAttribute("aud").toString());
        //つかむ取authorid
        String authorCd = session.getAttribute("aud").toString();
        logger.info("pattern情報変換後のパラメータを保存：{}",shelfPatternMst);
        try {
            Integer resultInfo = shelfPatternMstMapper.insert(shelfPatternMst);
            logger.info("保存棚名情報保存後に戻る情報：{}" ,resultInfo);

            shelfPatternDto.getArea().forEach(item -> {
                ShelfPatternArea shelfPatternArea = new ShelfPatternArea();
                shelfPatternArea.setCompanyCd(shelfPatternDto.getCompanyCd());
                shelfPatternArea.setShelfPatternCd(shelfPatternMst.getShelfPatternCd());

                shelfPatternArea.setAreacd(item);
                list.add(shelfPatternArea);
            });
            logger.info("pattern情報変換後のareaパラメータを保存：{}",list);
            shelfPatternAreaService.setShelfPatternArea(list,authorCd);
        } catch (Exception e) {
            logger.error(e.toString());
            throw new BusinessException(e.toString());
        }
        return ResultMaps.result(ResultEnum.SUCCESS);
    }
    /**
     * 修正棚pattern情報
     * @param shelfPatternDto
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> updateShelfPatternInfo(ShelfPatternDto shelfPatternDto) {
        logger.info("棚pattern情報のパラメータの変更：{}",shelfPatternDto);
        // 名称check 同一个棚名称棚パータン名唯一
        List<Integer> resultNum = shelfPatternMstMapper.selectDistinctName(shelfPatternDto);
        if (resultNum!=null &&resultNum.size()>1){
            return ResultMaps.result(ResultEnum.NAMEISEXISTS);
        }
        //削除するコレクション
        List<ShelfPatternArea> delList = new ArrayList<>();
        //追加するコレクション
        List<ShelfPatternArea> setList = new ArrayList<>();
        ShelfPatternMst shelfPatternMst = new ShelfPatternMst();
        shelfPatternMst.setShelfPatternCd(shelfPatternDto.getShelfPatternCd());
        shelfPatternMst.setConpanyCd(shelfPatternDto.getCompanyCd());
        shelfPatternMst.setShelfNameCd(shelfPatternDto.getShelfNameCD());
        shelfPatternMst.setShelfPatternName(shelfPatternDto.getShelfPatternName());
        shelfPatternMst.setPtsRelationID(shelfPatternDto.getPtsRelationID());
        shelfPatternMst.setMaintainerCd(session.getAttribute("aud").toString());
        //ユーザーIDの取得
        String authorCd = session.getAttribute("aud").toString();
        logger.info("修改pattern信息変換后的参数：{}",shelfPatternMst);
        try {
            int resultInfo = shelfPatternMstMapper.update(shelfPatternMst);
            logger.info("保存棚名称信息保存后返回的信息：{}" , resultInfo);
            //つかむ取棚pattern関連的Area
            List<Integer> getShelfPatternArea = shelfPatternAreaService.getShelfPatternArea(shelfPatternMst.getShelfPatternCd(),shelfPatternMst.getConpanyCd());
            logger.info("棚pattern関連的所有Area：{}" , getShelfPatternArea);
            //database中修改重複数据
            shelfPatternDto.getArea().forEach(item->{
                for (Integer area : getShelfPatternArea) {
                    if (item.equals(area)){
                        shelfPatternAreaService.setDelFlg(item,shelfPatternDto.getShelfPatternCd(),authorCd);
                    }
                }
            });
            //削除するareaコレクション
            List<Integer> deleteAreaList = ListDisparityUtils.getListDisparit(getShelfPatternArea, shelfPatternDto.getArea());
            //areaの集合を追加するには
            List<Integer> setAreaList = ListDisparityUtils.getListDisparit( shelfPatternDto.getArea(),getShelfPatternArea);
            if (deleteAreaList.size()>0){
                deleteAreaList.forEach(item -> {
                    ShelfPatternArea shelfPatternArea = new ShelfPatternArea();
                    shelfPatternArea.setCompanyCd(shelfPatternDto.getCompanyCd());
                    shelfPatternArea.setShelfPatternCd(shelfPatternMst.getShelfPatternCd());
                    shelfPatternArea.setAreacd(item);

                    delList.add(shelfPatternArea);
                });
                logger.info("削除棚pattern信息変換后的area参数：{}",delList);

                // 削除棚pattern関連的area
                logger.info("削除棚pattern信息的area参数：{}" , deleteAreaList);
                int deleteAreaCdInfo = shelfPatternAreaService.deleteAreaCd(deleteAreaList, shelfPatternDto.getShelfPatternCd(), authorCd);
                logger.info("削除棚名称信息保存后返回的信息：{}",deleteAreaCdInfo);

            }
            if (setAreaList.size()>0) {
                setAreaList.forEach(item -> {
                    ShelfPatternArea shelfPatternArea = new ShelfPatternArea();
                    shelfPatternArea.setCompanyCd(shelfPatternDto.getCompanyCd());
                    shelfPatternArea.setShelfPatternCd(shelfPatternMst.getShelfPatternCd());
                    shelfPatternArea.setAreacd(item);
                    setList.add(shelfPatternArea);
                });
                logger.info("添加棚pattern信息変換后的area参数：{}" , setList);
                Map<String, Object> setAreaInfo = shelfPatternAreaService.setShelfPatternArea(setList, authorCd);
                logger.info("修改棚名称信息保存后返回的信息：{}",setAreaInfo);
            }

        }catch (Exception e) {
            logger.error(e.toString());
            throw new BusinessException(e.getMessage());
        }
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    /**通を過ぎて棚名称棚pattern
     * @param companyCd
     * @param shelfNameCd
     * @return
     */
    @Override
    public List<Integer> getShelfPattern(String companyCd, Integer shelfNameCd) {
        return  shelfPatternMstMapper.getShelfPattern(companyCd,shelfNameCd);

    }

    /**
     * 棚pattern関連店cdを取得
     * @param shelfPatternCd
     * @return
     */
    @Override
    public Map<String, Object> getShelfPatternBranch(Integer shelfPatternCd) {
        logger.info("つかむ取棚pattern関連的店cd的参数：{}",shelfPatternCd);
        List<ShelfPatternBranch> list = shelfPatternBranchMapper.selectByPrimaryKey(shelfPatternCd);
        logger.info("つかむ取棚pattern関連的店cd：{}",list);
        ShelfPatternBranchVO shelfPatternBranchVO=new ShelfPatternBranchVO();
        List<String> branchList = new ArrayList<>();
        if (!list.isEmpty()) {
            list.forEach(item -> {
                branchList.add(String.valueOf(item.getBranch()));
            });
            shelfPatternBranchVO.setBranchCd(branchList);
            shelfPatternBranchVO.setShelfPatternCd(list.get(0).getShelfPattrenCd());
            shelfPatternBranchVO.setStartTime(list.get(0).getStartTime());
            logger.info("つかむ取棚pattern関連的店cd変換クラス型后：{}",shelfPatternBranchVO);
            return ResultMaps.result(ResultEnum.SUCCESS,shelfPatternBranchVO);
        }else {
            return ResultMaps.result(ResultEnum.SUCCESS,null);
        }
    }

    /**
     * 保存棚patternのお店cd
     * @param shelfPatternBranchVO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> setShelfPatternBranch(ShelfPatternBranchVO shelfPatternBranchVO) {
        logger.info("保存棚pattern関連的店cd的参数：{}",shelfPatternBranchVO);
        //作成者cdの取得
        String authorCd = session.getAttribute("aud").toString();
        //削除するコレクション
        List<ShelfPatternBranch> delList = new ArrayList<>();
        //追加するコレクション
        List<ShelfPatternBranch> setList = new ArrayList<>();
        //棚pattern関連branchの取得
        List<String> getShelfPatternBranch = shelfPatternBranchMapper.getShelfPatternBranch(shelfPatternBranchVO.getShelfPatternCd());
        //データベースと重複データの変更
        shelfPatternBranchVO.getBranchCd().forEach(item->{
            for (String shelfPatternBranch : getShelfPatternBranch) {
                if (item.equals(shelfPatternBranch)){
                    shelfPatternBranchMapper.setDelFlg(item,shelfPatternBranchVO.getShelfPatternCd(),authorCd);
                }
            }

        });
        //削除するareaコレクション
        List<String> delBranchList = ListDisparityUtils.getListDisparitStr(getShelfPatternBranch, shelfPatternBranchVO.getBranchCd());
        //areaの集合を追加するには
        List<String> setBranchList = ListDisparityUtils.getListDisparitStr( shelfPatternBranchVO.getBranchCd(),getShelfPatternBranch);

      if (delBranchList.size()>0){
          delBranchList.forEach(item->{
              ShelfPatternBranch shelfPatternBranch = new ShelfPatternBranch();
              shelfPatternBranch.setShelfPattrenCd(shelfPatternBranchVO.getShelfPatternCd());
              shelfPatternBranch.setBranch(item);
              shelfPatternBranch.setStartTime(shelfPatternBranchVO.getStartTime());
              delList.add(shelfPatternBranch);
          });
          //関連branchの削除

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
     * すべての棚patternのnameを取得
     * @param companyCd
     * @return
     */
    @Override
    public Map<String, Object> getShelfPatternName(String companyCd) {
        logger.info("つかむ取所有棚pattern的name的参数：{}",companyCd);
        List<ShelfPatternNameVO> shelfPatternNameVO = shelfPatternMstMapper.selectPatternName(companyCd);
        return ResultMaps.result(ResultEnum.SUCCESS,shelfPatternNameVO);
    }

    /**
     * 店舗に関連付けられた棚patternのnameを取得（優先順位表用）
     * @param companyCd
     * @return
     */
    @Override
    public Map<String, Object> getShelfPatternNameBranch(String companyCd) {
        logger.info("つかむ取関連了店舗的棚pattern的name的参数：{}",companyCd);
        List<ShelfPatternTreeVO> shelfPatternNameVOS = shelfPatternMstMapper.selectPatternNameBranch(companyCd);
        return ResultMaps.result(ResultEnum.SUCCESS,shelfPatternNameVOS);
    }

    /**
     * 棚の削除
     *
     * @param jsonObject
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> delShelfPatternInfo(JSONObject jsonObject) {
        logger.info("削除棚pattern的参数：{}",jsonObject.toString());
        if (((Map) jsonObject.get("param")).get("id")!=null ){
            Integer id = Integer.valueOf(String.valueOf(((Map) jsonObject.get("param")).get("id")));
            //作成者cdの取得
            String authorCd = session.getAttribute("aud").toString();
            // 棚の削除
            shelfPatternMstMapper.deleteByShelfName(id,authorCd);
            // 関連店を削除
            shelfPatternBranchMapper.deleteByPrimaryKey(id,authorCd);
            // 棚pattern関連areaの削除
            shelfPatternAreaService.delShelfPatternArea(id,authorCd);
            // 管理那の小屋ptsを修正する
            shelfPatternMstMapper.updateByPtsForShelfPdCd(id,authorCd);
            // 履歴書の棚ptsの修正
            shelfPatternMstMapper.deleteShelfPdCdHistory(id,authorCd);
        }

        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    /**
     * つかむ取shelfPattern 1@棚パータン名称１,2@棚パータン名称2 格式的字符串
     *
     * @param shelfPatternNo
     * @return
     */
    @Override
    public String getShePatternNoNm(String shelfPatternNo) {

        return shelfPatternMstMapper.selectByShePatternNoNm(shelfPatternNo);
    }

    /**
     * 根据ptsKeyつかむ取patternid
     *
     * @param ptsKey
     * @return
     */
    @Override
    public List<Integer> getpatternIdOfPtsKey(String ptsKey) {
        return shelfPatternMstMapper.getpatternIdOfPtsKey(ptsKey);
    }

    @Override
    public Map<String, Object> getShelfPatternForArea(String companyCd, int[] areaCds) {
        List<ShelfNamePatternVo> shelfPatternForArea = shelfPatternMstMapper.getShelfPatternForArea(companyCd, areaCds);
        return ResultMaps.result(ResultEnum.SUCCESS,shelfPatternForArea);
    }
}
