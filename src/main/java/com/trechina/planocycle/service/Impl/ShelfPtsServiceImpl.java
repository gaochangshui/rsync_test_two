package com.trechina.planocycle.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.trechina.planocycle.entity.dto.PriorityOrderPtsDataDto;
import com.trechina.planocycle.entity.dto.ShelfPtsDto;
import com.trechina.planocycle.entity.dto.ShelfPtsJoinPatternDto;
import com.trechina.planocycle.entity.dto.WorkPriorityOrderResultDataDto;
import com.trechina.planocycle.entity.po.*;
import com.trechina.planocycle.entity.vo.*;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.*;
import com.trechina.planocycle.service.PriorityOrderMstService;
import com.trechina.planocycle.service.ShelfPatternService;
import com.trechina.planocycle.service.ShelfPtsService;
import com.trechina.planocycle.utils.ResultMaps;
import de.siegmar.fastcsv.writer.CsvWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriUtils;
import org.yaml.snakeyaml.util.UriEncoder;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ShelfPtsServiceImpl implements ShelfPtsService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private HttpSession httpSession;
    @Autowired
    private ShelfPtsDataMapper shelfPtsDataMapper;
    @Autowired
    private ShelfPatternService shelfPatternService;
    @Autowired
    private PriorityOrderMstAttrSortMapper priorityOrderMstAttrSortMapper;
    @Autowired
    private PriorityOrderMstService priorityOrderMstService;
    @Autowired
    private WorkPriorityOrderResultDataMapper workPriorityOrderResultDataMapper;
    @Autowired
    private WorkPriorityOrderMstMapper workPriorityOrderMstMapper;
    @Autowired
    private ShelfPtsDataVersionMapper shelfPtsDataVersionMapper;
    @Autowired
    private ShelfPtsDataTaimstMapper shelfPtsDataTaimstMapper;
    @Autowired
    private ShelfPtsDataTanamstMapper shelfPtsDataTanamstMapper;
    @Autowired
    private ShelfPtsDataJandataMapper shelfPtsDataJandataMapper;

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
                logger.info("用到的patternid：" + patternId);
                // 清空patternid
                shelfPtsDataMapper.updateSingle(patternId, authorCd);
                shelfPtsDataMapper.updatePtsHistoryFlgSingle(patternId, authorCd);
                // 写入patternid
                shelfPtsDataMapper.updateShelfPtsOfAutoInner(shelfPtsData.getId(), patternId, authorCd);
                // 写入history
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Calendar calendar = Calendar.getInstance();
                Date date = calendar.getTime();
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
     * 获取棚pattern关联的pts的棚/段数
     *
     * @param patternCd
     * @return
     */
    @Override
    public Map<String, Object> getTaiNumTanaNum(Integer patternCd,Integer priorityOrderCd) {
        Map<String,Object> taiTanaNum = new HashMap<>();
        //台数
        Integer taiNum = shelfPtsDataMapper.getTaiNum(patternCd);
        //段数
        Integer tanaNum = shelfPtsDataMapper.getTanaNum(patternCd);
        //face数
        Integer faceNum = shelfPtsDataMapper.getFaceNum(patternCd);
        //sku数
        Integer skuNum = shelfPtsDataMapper.getSkuNum(patternCd);
        //pattern名称
        String shelfPatternName = shelfPtsDataMapper.getPatternName(patternCd);

        Integer newSkuNum = shelfPtsDataMapper.getNewSkuNum(priorityOrderCd);
        if (newSkuNum == null) {
            newSkuNum = 0;
        }
        Integer newFaceNum = shelfPtsDataMapper.getNewFaceNum(priorityOrderCd);
        if (newFaceNum == null){
            newFaceNum = 0;
        }
        //棚名称
        String shelfName = shelfPtsDataMapper.getPengName(patternCd);


        PtsDetailDataVo newPtsDetailData = shelfPtsDataMapper.getPtsNewDetailData(priorityOrderCd);
        List<PtsTaiVo> taiData = shelfPtsDataMapper.getNewTaiData(priorityOrderCd);
        List<PtsTanaVo> tanaData = shelfPtsDataMapper.getNewTanaData(priorityOrderCd);
        List<PtsJanDataVo> janData = shelfPtsDataMapper.getNewJanData(priorityOrderCd);
        if (newPtsDetailData != null){
            newPtsDetailData.setPtsTaiList(taiData);
        }
        if (newPtsDetailData != null) {
            newPtsDetailData.setPtsTanaVoList(tanaData);
        }
        if (newPtsDetailData != null) {
            newPtsDetailData.setPtsJanDataList(janData);
        }


        taiTanaNum.put("taiNum",taiNum);
        taiTanaNum.put("tanaNum",tanaNum);
        taiTanaNum.put("faceNum",faceNum);
        taiTanaNum.put("skuNum",skuNum);
        taiTanaNum.put("shelfPatternName",shelfPatternName);
        taiTanaNum.put("shelfName",shelfName);
        taiTanaNum.put("newSkuNum",newSkuNum);
        taiTanaNum.put("newFaceNum",newFaceNum);
        taiTanaNum.put("newPtsDetailData",newPtsDetailData);
        return ResultMaps.result(ResultEnum.SUCCESS,taiTanaNum);
    }

    @Override
    public Map<String, Object> getPtsDetailData(Integer patternCd,String companyCd,Integer priorityOrderCd) {
        PtsDetailDataVo ptsDetailData = shelfPtsDataMapper.getPtsDetailData(patternCd);
        List<PtsTaiVo> taiData = shelfPtsDataMapper.getTaiData(patternCd);
        List<PtsTanaVo> tanaData = shelfPtsDataMapper.getTanaData(patternCd);
        List<PtsJanDataVo> janData = shelfPtsDataMapper.getJanData(patternCd);
        if (ptsDetailData != null){
            ptsDetailData.setPtsTaiList(taiData);
        }
       if (ptsDetailData != null) {
           ptsDetailData.setPtsTanaVoList(tanaData);
       }
       if (ptsDetailData != null) {
           ptsDetailData.setPtsJanDataList(janData);
       }
        return ResultMaps.result(ResultEnum.SUCCESS,ptsDetailData);
    }
    /**
     * 陈列顺设定添加
     * @param workPriorityOrderSort
     * @return
     */
    @Override
    public Map<String, Object> setDisplay(List<WorkPriorityOrderSort> workPriorityOrderSort) {
        String aud = httpSession.getAttribute("aud").toString();
        String companyCd = null;
        for (WorkPriorityOrderSort priorityOrderSort : workPriorityOrderSort) {
            companyCd=  priorityOrderSort.getCompanyCd();
        }
        shelfPtsDataMapper.deleteDisplay(companyCd,aud);
        if (workPriorityOrderSort.isEmpty()){
            return ResultMaps.result(ResultEnum.SUCCESS);
        }
        shelfPtsDataMapper.setDisplay(workPriorityOrderSort,aud);
        return ResultMaps.result(ResultEnum.SUCCESS);
    }
    /**
     * 陈列顺设定展示
     * @param companyCd
     * @return
     */
    @Override
    public Map<String, Object> getDisplay(String companyCd,Integer priorityOrderCd) {
        String aud = httpSession.getAttribute("aud").toString();
        List<WorkPriorityOrderSort> displayList = shelfPtsDataMapper.getDisplay(companyCd, aud,priorityOrderCd);

        return ResultMaps.result(ResultEnum.SUCCESS,displayList);
    }

    @Override
    public void downloadPtsCsv(PtsCsvVO ptsCsvVO, HttpServletResponse response) throws IOException {
        String authorCd = httpSession.getAttribute("aud").toString();
        Integer type = ptsCsvVO.getType();
        Long shelfPatternCd = ptsCsvVO.getShelfPatternCd();
        String companyCd = ptsCsvVO.getCompanyCd();
        Integer typeCd = ptsCsvVO.getTypeCd();

        ShelfPtsDataVersion shelfPtsDataVersion=null;
        List<ShelfPtsDataTaimst> shelfPtsDataTaimst = null;
        List<ShelfPtsDataTanamst> shelfPtsDataTanamst = null;
        List<ShelfPtsDataJandata> shelfPtsDataJandata = null;
        String fileName = "";

        if(type == 0){
            //旧パターン
            ShelfPtsData ptsData = shelfPtsDataMapper.selectPtsCdByPatternCd(companyCd, shelfPatternCd);
            Integer ptsCd = ptsData.getId();
            fileName = ptsData.getFileName();

            shelfPtsDataVersion = shelfPtsDataVersionMapper.selectByPrimaryKey(companyCd, ptsCd);
            shelfPtsDataTaimst = shelfPtsDataTaimstMapper.selectByPtsCd(companyCd, ptsCd);
            shelfPtsDataTanamst = shelfPtsDataTanamstMapper.selectByPtsCd(companyCd, ptsCd);
            shelfPtsDataJandata = shelfPtsDataJandataMapper.selectByPtsCd(companyCd, ptsCd);
        }else if(type == 1){
            //新基本パターン
            ShelfPtsData ptsData = shelfPtsDataMapper.selectPtsCdByAuthorCd(companyCd, authorCd, typeCd, shelfPatternCd);
            Integer ptsCd = ptsData.getId();
            fileName = ptsData.getFileName();

            shelfPtsDataVersion = shelfPtsDataVersionMapper.selectNewByPtsCd(companyCd, ptsCd);
            shelfPtsDataTaimst = shelfPtsDataTaimstMapper.selectNewByPtsCd(companyCd, ptsCd);
            shelfPtsDataTanamst = shelfPtsDataTanamstMapper.selectNewByPtsCd(companyCd, ptsCd);
            shelfPtsDataJandata = shelfPtsDataJandataMapper.selectNewByPtsCd(companyCd, ptsCd);
        }else{
            //新全パターン
            return;
        }

        response.setHeader(HttpHeaders.CONTENT_TYPE, "text/csv;charset=utf-8");

        PrintWriter printWriter = response.getWriter();
        //为了解决excel打开乱码的问题
        byte[] bom = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
        printWriter.write(new String(bom));

        String format = MessageFormat.format("attachment;filename={0};", fileName);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, format);
        this.generateCsv(shelfPtsDataVersion, shelfPtsDataTaimst, shelfPtsDataTanamst, shelfPtsDataJandata, printWriter);
    }

    public void generateCsv(ShelfPtsDataVersion shelfPtsDataVersion, List<ShelfPtsDataTaimst> shelfPtsDataTaimst,
                            List<ShelfPtsDataTanamst> shelfPtsDataTanamst,  List<ShelfPtsDataJandata> shelfPtsDataJandata, PrintWriter printWriter){
        CsvWriter csvWriter = CsvWriter.builder().build(printWriter);
        csvWriter.writeRow(Lists.newArrayList(shelfPtsDataVersion.getCommoninfo(),
                shelfPtsDataVersion.getVersioninfo(), shelfPtsDataVersion.getOutflg()));
        csvWriter.writeRow(shelfPtsDataVersion.getModename());
        csvWriter.writeRow(shelfPtsDataVersion.getTaiHeader().split(","));

        for (ShelfPtsDataTaimst ptsDataTaimst : shelfPtsDataTaimst) {
            csvWriter.writeRow(Lists.newArrayList(ptsDataTaimst.getTaiCd()+"",
                    ptsDataTaimst.getTaiHeight()+"", ptsDataTaimst.getTaiWidth()+"", ptsDataTaimst.getTaiDepth()+"",
                    Optional.ofNullable(ptsDataTaimst.getTaiName()).orElse("")));
        }

        csvWriter.writeRow(shelfPtsDataVersion.getTanaHeader().split(","));
        for (ShelfPtsDataTanamst ptsDataTanamst : shelfPtsDataTanamst) {
            csvWriter.writeRow(Lists.newArrayList(ptsDataTanamst.getTaiCd()+"",
                    ptsDataTanamst.getTanaCd()+"", ptsDataTanamst.getTanaHeight()+"", ptsDataTanamst.getTanaWidth()+"",
                    ptsDataTanamst.getTanaDepth()+"", ptsDataTanamst.getTanaThickness()+"", ptsDataTanamst.getTanaType()+""));
        }

        String[] janHeaders = shelfPtsDataVersion.getJanHeader().split(",");
        csvWriter.writeRow(janHeaders);
        for (ShelfPtsDataJandata ptsDataJandatum : shelfPtsDataJandata) {
            List<String> janData = Lists.newArrayList(ptsDataJandatum.getTaiCd() + "",
                    ptsDataJandatum.getTanaCd() + "", ptsDataJandatum.getTanapositionCd() + "", ptsDataJandatum.getJan() + "",
                    ptsDataJandatum.getFaceCount() + "", ptsDataJandatum.getFaceMen() + "", ptsDataJandatum.getFaceKaiten() + "",
                    ptsDataJandatum.getTumiagesu() + "",
                    Optional.ofNullable(ptsDataJandatum.getZaikosu()).orElse(0) + "", Optional.ofNullable(ptsDataJandatum.getFaceDisplayflg()).orElse(0) + "",
                    Optional.ofNullable(ptsDataJandatum.getFacePosition()).orElse(0) + "", Optional.ofNullable(ptsDataJandatum.getDepthDisplayNum()).orElse(0) + "");
            csvWriter.writeRow(janData.subList(0, janHeaders.length));
        }

        try {
            csvWriter.close();
        } catch (IOException e) {
            logger.error("csv writer 关闭异常",e);
        }
    }
    /**
     * 保存pts数据到临时表里
     * @param companyCd
     * @param authorCd
     * @param priorityOrderCd
     */
    @Override
    public void saveWorkPtsData(String companyCd, String authorCd, Integer priorityOrderCd) {
        WorkPriorityOrderMst workPriorityOrderMst = workPriorityOrderMstMapper.selectByAuthorCd(companyCd, authorCd, priorityOrderCd);
        Long shelfPatternCd = workPriorityOrderMst.getShelfPatternCd();

        //查询出所有采纳了的商品，按照台棚进行排序，标记商品在棚上的位置
        List<WorkPriorityOrderResultDataDto> workPriorityOrderResultData = workPriorityOrderResultDataMapper.selectByAuthorCd(companyCd, authorCd, priorityOrderCd);
        List<WorkPriorityOrderResultDataDto> positionResultData = this.calculateTanaPosition(workPriorityOrderResultData);

        //查出已有的新pts，先删掉再保存
        //新pts中已有数据的ptsCd
        ShelfPtsData shelfPtsData = shelfPtsDataMapper.selectWorkPtsCdByAuthorCd(companyCd, authorCd, priorityOrderCd, shelfPatternCd);
        //临时表中的ptscd
        Integer ptsCd = shelfPtsDataMapper.getPtsCd(shelfPatternCd.intValue());

        PriorityOrderPtsDataDto priorityOrderPtsDataDto = PriorityOrderPtsDataDto.PriorityOrderPtsDataDtoBuilder.aPriorityOrderPtsDataDto()
                .withPriorityOrderCd(priorityOrderCd)
                .withOldPtsCd(ptsCd)
                .withCompanyCd(companyCd)
                .withAuthorCd(authorCd).build();

        if(Optional.ofNullable(shelfPtsData).isPresent()){
            Integer oldPtsCd = shelfPtsData.getId();
            shelfPtsDataMapper.deletePtsData(oldPtsCd);
            shelfPtsDataMapper.deletePtsTaimst(oldPtsCd);
            shelfPtsDataMapper.deletePtsTanamst(oldPtsCd);
            shelfPtsDataMapper.deletePtsVersion(oldPtsCd);
            shelfPtsDataMapper.deletePtsDataJandata(oldPtsCd);
        }

        ShelfPtsDataVersion shelfPtsDataVersion = shelfPtsDataVersionMapper.selectByPrimaryKey(companyCd, ptsCd);
        String modeName = shelfPtsDataVersion.getModename();
        //modeName作为下载pts的文件名
        priorityOrderPtsDataDto.setFileName(modeName+"_new.csv");
        //从已有的pts中查询出数据
        shelfPtsDataMapper.insertPtsData(priorityOrderPtsDataDto);
        Integer id = priorityOrderPtsDataDto.getId();
        shelfPtsDataMapper.insertPtsTaimst(ptsCd, id, authorCd);
        shelfPtsDataMapper.insertPtsTanamst(ptsCd, id, authorCd);
        shelfPtsDataMapper.insertPtsVersion(ptsCd, id, authorCd);

        if (!positionResultData.isEmpty()) {
            shelfPtsDataMapper.insertPtsDataJandata(positionResultData, id, companyCd, authorCd);
        }
    }
    /**
     * 保存pts数据到最终表里
     * @param companyCd
     * @param authorCd
     * @param priorityOrderCd
     */
    @Override
    public void saveFinalPtsData(String companyCd, String authorCd, Integer priorityOrderCd) {
        Integer newId = shelfPtsDataMapper.getNewId(companyCd, priorityOrderCd);
        Integer id = shelfPtsDataMapper.getId(companyCd, priorityOrderCd);
        shelfPtsDataMapper.deleteFinalPtsData(newId);
        shelfPtsDataMapper.deleteFinalPtsTaimst(newId);
        shelfPtsDataMapper.deleteFinalPtsTanamst(newId);
        shelfPtsDataMapper.deleteFinalPtsVersion(newId);
        shelfPtsDataMapper.deleteFinalPtsDataJandata(newId);

        shelfPtsDataMapper.insertFinalPtsData(companyCd,authorCd,priorityOrderCd);
        if (id!=null){
            shelfPtsDataMapper.insertFinalPtsTaiData(companyCd,authorCd,id);
            shelfPtsDataMapper.insertFinalPtsTanaData(companyCd,authorCd,id);
            shelfPtsDataMapper.insertFinalPtsVersionData(companyCd,authorCd,id);
            shelfPtsDataMapper.insertFinalPtsDataJanData(companyCd,authorCd,id);
        }
    }


    /**
     * 先按照台遍历再遍历棚，同一个棚的商品从左到右的顺序从1开始进行标号，棚变了，重新标号
     *
     * @param workPriorityOrderResultData
     * @return
     */
    private List<WorkPriorityOrderResultDataDto> calculateTanaPosition(List<WorkPriorityOrderResultDataDto> workPriorityOrderResultData) {
        //有棚位置的resultdata数据
        List<WorkPriorityOrderResultDataDto> positionResultData = new ArrayList<>(workPriorityOrderResultData.size());
        //根据台进行分组遍历
        Map<Integer, List<WorkPriorityOrderResultDataDto>> workPriorityOrderResultDataByTai = workPriorityOrderResultData.stream()
                .collect(Collectors.groupingBy(WorkPriorityOrderResultDataDto::getTaiCd, LinkedHashMap::new, Collectors.toList()));

        for (Map.Entry<Integer, List<WorkPriorityOrderResultDataDto>> entrySet : workPriorityOrderResultDataByTai.entrySet()) {
            Integer taiCd = entrySet.getKey();

            List<WorkPriorityOrderResultDataDto> workPriorityOrderResultDataDtos = workPriorityOrderResultDataByTai.get(taiCd);
            //根据棚进行分组遍历
            Map<Integer, List<WorkPriorityOrderResultDataDto>> workPriorityOrderResultDataByTana = workPriorityOrderResultDataDtos.stream()
                    .collect(Collectors.groupingBy(WorkPriorityOrderResultDataDto::getTanaCd, LinkedHashMap::new, Collectors.toList()));
            for (Map.Entry<Integer, List<WorkPriorityOrderResultDataDto>> entry : workPriorityOrderResultDataByTana.entrySet()) {
                //同一个棚，序号从1开始累加，下一个棚重新从1开始加
                Integer tantaPositionCd=0;
                Integer tanaCd = entry.getKey();

                for (WorkPriorityOrderResultDataDto currentDataDto : workPriorityOrderResultDataByTana.get(tanaCd)) {
                    currentDataDto.setTanaPositionCd(++tantaPositionCd);
                    currentDataDto.setFaceMen(1);
                    currentDataDto.setFaceKaiten(0);
                    currentDataDto.setTumiagesu(1);
                    currentDataDto.setFaceDisplayflg(0);
                    currentDataDto.setFacePosition(1);
                    currentDataDto.setDepthDisplayNum(1);
                    positionResultData.add(currentDataDto);
                }
            }
        }
        return positionResultData;
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
