package com.trechina.planocycle.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.trechina.planocycle.entity.dto.GetCommonPartsDataDto;
import com.trechina.planocycle.entity.dto.ShelfPatternBranchDto;
import com.trechina.planocycle.entity.dto.ShelfPatternDto;
import com.trechina.planocycle.entity.po.ShelfPatternBranch;
import com.trechina.planocycle.entity.po.ShelfPatternMst;
import com.trechina.planocycle.entity.vo.*;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.exception.BusinessException;
import com.trechina.planocycle.mapper.ShelfNameMstMapper;
import com.trechina.planocycle.mapper.ShelfPatternBranchMapper;
import com.trechina.planocycle.mapper.ShelfPatternMstMapper;
import com.trechina.planocycle.service.BasicPatternMstService;
import com.trechina.planocycle.service.ShelfPatternAreaService;
import com.trechina.planocycle.service.ShelfPatternService;
import com.trechina.planocycle.utils.ListDisparityUtils;
import com.trechina.planocycle.utils.ResultMaps;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
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
     * 棚pattern情報の取得
     * @param companyCd
     * @return
     */
    @Override
    public Map<String, Object> getShelfPatternInfo(String companyCd) {
        logger.info("棚pattern情報のパラメータの取得：{}",companyCd);
        List<ShelfPatternMst> resultInfo = shelfPatternMstMapper.selectByPrimaryKey(companyCd);
        resultInfo = resultInfo.stream().peek(result -> {
            if (result.getStoreCdStr()==null) {
                result.setStoreCd(new String[]{});
                result.setBranchNum(0);
                result.setSpecialFlag(0);
            }else{
                String storeCdStr = result.getStoreCdStr();
                String[] storeCdStrList = storeCdStr.split(",");
                result.setStoreCd(storeCdStrList);
                result.setBranchNum(storeCdStrList.length);
                GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(result.getCommonPartsData(), companyCd);
                Integer existSpecialUse = shelfPatternMstMapper.getExistSpecialUse(commonTableName.getStoreInfoTable(), Arrays.asList(storeCdStrList));
                result.setSpecialFlag(existSpecialUse == 0 ? 0 : 1);
            }
        }).collect(Collectors.toList());
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
        //つかむ取authorid
        String authorCd = session.getAttribute("aud").toString();
        logger.info("pattern情報変換後のパラメータを保存：{}",shelfPatternMst);
        try {
            Integer resultInfo = shelfPatternMstMapper.insert(shelfPatternMst);
            logger.info("保存棚名情報保存後に戻る情報：{}" ,resultInfo);
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
        Integer resultNum = shelfPatternMstMapper.selectDistinctName(shelfPatternDto);
        if (!shelfPatternDto.getShelfPatternCd().equals(resultNum)){
            return ResultMaps.result(ResultEnum.NAMEISEXISTS);
        }
        //削除するコレクション

        //追加するコレクション
        ShelfPatternMst shelfPatternMst = new ShelfPatternMst();
        shelfPatternMst.setShelfPatternCd(shelfPatternDto.getShelfPatternCd());
        shelfPatternMst.setConpanyCd(shelfPatternDto.getCompanyCd());
        shelfPatternMst.setShelfNameCd(shelfPatternDto.getShelfNameCD());
        shelfPatternMst.setShelfPatternName(shelfPatternDto.getShelfPatternName());
        shelfPatternMst.setPtsRelationID(shelfPatternDto.getPtsRelationID());
        shelfPatternMst.setMaintainerCd(session.getAttribute("aud").toString());
        shelfPatternMst.setCommonPartsData(shelfPatternDto.getCommonPartsData());
        //ユーザーIDの取得
        String authorCd = session.getAttribute("aud").toString();
        logger.info("修改pattern信息変換后的参数：{}",shelfPatternMst);
        try {
            int resultInfo = shelfPatternMstMapper.update(shelfPatternMst);
            logger.info("保存棚名称信息保存后返回的信息：{}" , resultInfo);
            //つかむ取棚pattern関連的Area
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
            list.forEach(item -> branchList.add(String.valueOf(item.getBranch())));
            shelfPatternBranchVO.setBranchCd(branchList);
            shelfPatternBranchVO.setShelfPatternCd(list.get(0).getShelfPatternCd());
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

      if (!delBranchList.isEmpty()){
          delBranchList.forEach(item->{
              ShelfPatternBranch shelfPatternBranch = new ShelfPatternBranch();
              shelfPatternBranch.setShelfPatternCd(shelfPatternBranchVO.getShelfPatternCd());
              shelfPatternBranch.setBranch(item);
              shelfPatternBranch.setStartTime(shelfPatternBranchVO.getStartTime());
              delList.add(shelfPatternBranch);
          });
          //関連branchの削除

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
    public List<Integer> getpatternIdOfFilename(String fileName, String companyCd) {
        return shelfPatternMstMapper.getpatternIdOfFilename(fileName,companyCd);
    }

    @Override
    public Map<String, Object> getShelfPatternForArea(String companyCd) {
        List<ShelfNamePatternVo> shelfPatternForArea = shelfPatternMstMapper.getShelfPatternForArea(companyCd);
        return ResultMaps.result(ResultEnum.SUCCESS,shelfPatternForArea);
    }


    /**
     * 批量保存棚pattern
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
        List<ShelfPatternBranch> branchList = new ArrayList();
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
        List<ShelfPatternNameVO> patternForStorel = shelfPatternMstMapper.getPatternForStorel(storeIsCore, companyCd);
        for (ShelfPatternNameVO shelfPatternNameVO : patternForStorel) {
            String prodIsCore = shelfPatternNameVO.getStoreIsCore();
            JSONObject jsonObject = JSON.parseObject(prodIsCore);
            shelfPatternNameVO.setStoreIsCore(jsonObject.get("storeIsCore").toString());
        }
        return ResultMaps.result(ResultEnum.SUCCESS,patternForStorel);
    }


    void excelUtil(List<ShelfPatternMst> patternDataList,List<ShelfPatternBranch> patternBranch,List<ShelfNameDataVO> shelfNameList ,List<ShelfPatternBranchDto> BranchList ,HttpServletResponse response){
        int rowIndex = 0;
        String[] patternHeader = {"ID","棚パターン名称","ptskey","棚名称ID","棚名称","1","2"};
        String[] relevancyBranchHeader = {"棚パターン名称","店舗番号"};
        String [] shelfName = {"棚名称ID","棚名称"};
        String [] branchHeader = {"店舗番号","店舗名"};

        try(XSSFWorkbook workbook = new XSSFWorkbook()){
            XSSFCellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setLocked(true);
            XSSFSheet sheet1 = workbook.createSheet();
            sheet1.protectSheet("123");
            XSSFSheet sheet2 = workbook.createSheet();

            XSSFSheet sheet3 = workbook.createSheet();
            XSSFSheet sheet4 = workbook.createSheet();
            workbook.setSheetName(0,"棚パターン");
            workbook.setSheetName(1,"棚パターン&店舗");
            workbook.setSheetName(2,"棚名称");
            workbook.setSheetName(3,"店舗");

            //棚パターン
            if (rowIndex == 0) {
                XSSFRow row = sheet1.createRow(rowIndex);
                for (int i = 0; i < patternHeader.length; i++) {
                    row.createCell(i).setCellValue(patternHeader[i]);

                }
                rowIndex++;
            }
            for (ShelfPatternMst patternMst : patternDataList) {
                XSSFRow row = sheet1.createRow(rowIndex);
                int sellIndex = 0;

                row.createCell(sellIndex++).setCellValue(patternMst.getShelfPatternCd());
                row.createCell(sellIndex++).setCellValue(patternMst.getShelfPatternName());
                row.createCell(sellIndex++).setCellValue(patternMst.getPtsRelationID());
                row.createCell(sellIndex++).setCellValue(patternMst.getShelfName());
                row.createCell(sellIndex++).setCellValue(patternMst.getShelfNameCd());
                row.createCell(sellIndex++).setCellValue(patternMst.getAuthorCd());
                row.createCell(sellIndex++).setCellValue(patternMst.getCreateTime());
                rowIndex++;
            }
            //棚パターン&店舗
            rowIndex = 0;
            XSSFRow row = sheet2.createRow(rowIndex);
            for (int i = 0; i < relevancyBranchHeader.length; i++) {
                row.createCell(i).setCellValue(relevancyBranchHeader[i]);
                rowIndex++;
            }
            for (ShelfPatternBranch branch : patternBranch) {
                int sellIndex = 0;
                row = sheet2.createRow(rowIndex);
                row.createCell(sellIndex++).setCellValue(branch.getShelfPatternCd());
                row.createCell(sellIndex++).setCellValue(branch.getBranch());
                rowIndex++;
            }
            rowIndex = 0;
            //棚名称
             row = sheet3.createRow(rowIndex);
            for (int i = 0; i < shelfName.length; i++) {

                row.createCell(i).setCellValue(shelfName[i]);
            }
            rowIndex ++;
            for (ShelfNameDataVO shelfNameDataVO : shelfNameList) {
                int sellIndex = 0;

                 row = sheet3.createRow(rowIndex);
                row.createCell(sellIndex++).setCellValue(shelfNameDataVO.getId());
                row.createCell(sellIndex++).setCellValue(shelfNameDataVO.getShelfName());
                rowIndex++;
            }
            rowIndex = 0;
            //店舗
            row = sheet4.createRow(rowIndex);
            for (int i = 0; i < branchHeader.length; i++) {
                row.createCell(i).setCellValue(branchHeader[i]);
            }
            rowIndex++;
            for (ShelfPatternBranchDto shelfPatternBranchDto : BranchList) {
                int sellIndex = 0;
                 row = sheet4.createRow(rowIndex);
                row.createCell(sellIndex++).setCellValue(shelfPatternBranchDto.getId());
                row.createCell(sellIndex++).setCellValue(shelfPatternBranchDto.getName());
                rowIndex++;
            }


            try {
                ServletOutputStream outputStream = response.getOutputStream();
                workbook.write(outputStream);
                outputStream.flush();
            } catch (IOException e) {
                logger.error("io閉じる異常", e);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
