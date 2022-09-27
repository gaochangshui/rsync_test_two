package com.trechina.planocycle.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.trechina.planocycle.constant.MagicString;
import com.trechina.planocycle.entity.dto.*;
import com.trechina.planocycle.entity.po.*;
import com.trechina.planocycle.entity.vo.*;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.*;
import com.trechina.planocycle.service.*;
import com.trechina.planocycle.utils.ResultMaps;
import de.siegmar.fastcsv.writer.CsvWriter;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriUtils;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStreamWriter;
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
    private PriorityOrderJanNewService janNewService;
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
    @Autowired
    private BasicPatternRestrictRelationMapper basicPatternRestrictRelationMapper;
    @Autowired
    private PriorityOrderMstAttrSortMapper priorityOrderMstAttrSortMapper;
    @Autowired
    private CommonMstService commonMstService;
    @Autowired
    private PriorityAllPtsMapper priorityAllPtsMapper;
    @Autowired
    private PriorityOrderMstMapper priorityOrderMstMapper;
    @Autowired
    private BasicPatternMstService basicPatternMstService;
    @Autowired
    private ZokuseiMapper zokuseiMapper;
    @Autowired
    private ZokuseiMstMapper zokuseiMstMapper;
    @Autowired
    private PriorityAllMstMapper priorityAllMstMapper;
    @Autowired
    private PriorityOrderMstAttrSortMapper attrSortMapper;


    /**
     * 棚割pts情報の取得
     *
     * @param companyCd
     * @return
     */
    @Override
    public Map<String, Object> getShelfPtsInfo(String companyCd) {
        logger.info("棚割pts情報パラメータの取得：{}",companyCd);
        List<ShelfPtsData> shelfPtsData = shelfPtsDataMapper.selectByPrimaryKey(companyCd);
        logger.info("棚割pts情報の値を返す：{}", shelfPtsData);
        return ResultMaps.result(ResultEnum.SUCCESS, shelfPtsData);
    }

    /**
     * 棚割pts情報の保存
     *
     * @param shelfPtsDto
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> setShelfPtsInfo(ShelfPtsDto shelfPtsDto, Integer flg) {
        String authorCd = httpSession.getAttribute("aud").toString();
        Date now = Calendar.getInstance().getTime();
        logger.info("棚割ptsデータのパラメータを保存する：{}", shelfPtsDto);
        ShelfPtsData shelfPtsData = new ShelfPtsData();
        shelfPtsData.setConpanyCd(shelfPtsDto.getCompanyCd());
        shelfPtsData.setFileName(shelfPtsDto.getFileName());
        shelfPtsData.setAuthorcd(authorCd);
        shelfPtsDataMapper.insert(shelfPtsData);
        logger.info("保存後のパラメータ：{}", shelfPtsData);
        if (flg == 0) {
            // check patternid
            String[] ptsKeyList = shelfPtsData.getFileName().split("_");
            logger.info("戻るptskey：{}", ptsKeyList);

            StringBuilder ptsKey = new StringBuilder();
            if (ptsKeyList.length > 3) {
                for (int i = 0; i < 4; i++) {
                    ptsKey.append(ptsKeyList[i]).append("_");
                }
            } else {
                ptsKey = new StringBuilder("__");
            }
            logger.info("手動で組み合わせたptskey：{}", ptsKey);
            List<Integer> patternIdList = shelfPatternService.getpatternIdOfFilename(shelfPtsDto.getFileName(),shelfPtsDto.getCompanyCd());
            logger.info("組み合わせのptskeyによってpatternidを探します：{}", patternIdList);
            if (!patternIdList.isEmpty()) {
                Integer patternId = patternIdList.get(0);
                logger.info("使用されるpatternid：{}", patternId);
                logger.info("使用されるpatternid：{}", patternId);
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
     * pts関連pattern
     *
     * @param shelfPtsJoinPatternDto
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> saveShelfPts(List<ShelfPtsJoinPatternDto> shelfPtsJoinPatternDto) {
        logger.info("ptd関連patternのパラメータ:{}", shelfPtsJoinPatternDto);

        String authorCd = httpSession.getAttribute("aud").toString();
        shelfPtsDataMapper.updateByPrimaryKey(shelfPtsJoinPatternDto);
        shelfPtsDataMapper.updatePtsHistoryFlg(shelfPtsJoinPatternDto);
        shelfPtsJoinPatternDto.forEach(item -> {

            if (item.getShelfPatternCd() != null) {
                Integer existsCount = shelfPtsDataMapper.selectExistsCount(item);
                if (existsCount == 0) {
                    shelfPtsDataMapper.insertPtsHistory(item, authorCd);
                } else {
                    // テーブルの更新
                    shelfPtsDataMapper.updatePtsHistory(item, authorCd);
                    //
                    //テーブルの挿入
                }
            }
        });
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    /**
     * 棚pattern別pts関連pattern
     *
     * @param shelfPtsJoinPatternDto
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> saveShelfPtsOfPattern(List<ShelfPtsJoinPatternDto> shelfPtsJoinPatternDto) {
        logger.info("ptd関連patternのパラメータ:{}", shelfPtsJoinPatternDto);
        // 有効無効flg有効1無効0を変更してすべて0に変更
        // テーブルデータのすべてのポジションが空です
        shelfPtsDataMapper.updateAll(shelfPtsJoinPatternDto);
        // 社員番号check
        String authorCd = httpSession.getAttribute("aud").toString();
        // テーブルデータの変更
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
     * 棚割pts情報の削除
     *
     * @param
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> delShelfPtsInfo(JSONObject jsonObject) {
        if (((Map) jsonObject.get("param")).get("id") != null) {
            Integer id = Integer.valueOf(String.valueOf(((Map) jsonObject.get("param")).get("id")));
            //つかむ取authorid
            String authorCd = httpSession.getAttribute("aud").toString();

            shelfPtsDataMapper.delShelfPtsInfo(id, authorCd);

            shelfPtsDataMapper.delShelfHistoryInfo(id, authorCd);

        }
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    /**
     * 棚pattern関連ptsの棚/セグメント数を取得
     *
     * @param patternCd
     * @return
     */
    @Override
    public Map<String, Object>
    getTaiNumTanaNum(Integer patternCd,Integer priorityOrderCd) {
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


        //棚名称
        String shelfName = shelfPtsDataMapper.getPengName(patternCd);

        Integer newSkuNum = shelfPtsDataMapper.getNewSkuNum(priorityOrderCd);
        if (newSkuNum == null) {
            newSkuNum = 0;
        }
        Integer newFaceNum = shelfPtsDataMapper.getNewFaceNum(priorityOrderCd);
        if (newFaceNum == null){
            newFaceNum = 0;
        }
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

    /**
     * 棚pattern関連ptsの詳細の取得
     * @param patternCd
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Map<String, Object> getPtsDetailData(Integer patternCd,String companyCd,Integer priorityOrderCd,Integer flag) {
        String authorCd = httpSession.getAttribute("aud").toString();
        if (flag == null) {
            //ptsCdの取得
            Integer id = shelfPtsDataMapper.getId(companyCd, priorityOrderCd);
            if (id != null) {
                //クリアワーク_priority_order_pts_data
                shelfPtsDataMapper.deletePtsData(id);
                //クリアワーク_priority_order_pts_data_taimst
                shelfPtsDataMapper.deletePtsTaimst(id);
                //クリアワーク_priority_order_pts_data_tanamst
                shelfPtsDataMapper.deletePtsTanamst(id);
                //クリアワーク_priority_order_pts_data_version
                shelfPtsDataMapper.deletePtsVersion(id);

            }
            Integer ptsCd = shelfPtsDataMapper.getPtsCd(patternCd);
            PriorityOrderPtsDataDto priorityOrderPtsDataDto = PriorityOrderPtsDataDto.PriorityOrderPtsDataDtoBuilder.aPriorityOrderPtsDataDto()
                    .withPriorityOrderCd(priorityOrderCd)
                    .withOldPtsCd(ptsCd)
                    .withCompanyCd(companyCd)
                    .withAuthorCd(authorCd).build();
            ShelfPtsDataVersion shelfPtsDataVersion = shelfPtsDataVersionMapper.selectByPrimaryKey(companyCd, ptsCd);
            String modeName = shelfPtsDataVersion.getModename();
            //modeNameはptsをダウンロードするファイル名として
            priorityOrderPtsDataDto.setFileName(modeName + "_new.csv");
            //既存のptsからデータをクエリーする
            if (id != null) {
                priorityOrderPtsDataDto.setId(id);
                shelfPtsDataMapper.insertPtsData1(priorityOrderPtsDataDto);
            } else {
                shelfPtsDataMapper.insertPtsData(priorityOrderPtsDataDto);
            }

            Integer newId = priorityOrderPtsDataDto.getId();
            shelfPtsDataMapper.insertPtsTaimst(ptsCd, newId, authorCd);
            shelfPtsDataMapper.insertPtsTanamst(ptsCd, newId, authorCd);
            shelfPtsDataMapper.insertPtsVersion(ptsCd, newId, authorCd);
        }

        PtsDetailDataVo ptsDetailData = shelfPtsDataMapper.getPtsDetailData(patternCd);
        PriorityOrderAttrDto attrDto =null;
        if (flag != null){
             attrDto = priorityOrderMstMapper.getCommonPartsData(companyCd, priorityOrderCd);
             workPriorityOrderMstMapper.deleteByAuthorCd(companyCd,authorCd,priorityOrderCd);
            workPriorityOrderMstMapper.setWorkForFinal(companyCd, priorityOrderCd, authorCd,priorityOrderCd);

        }else {
             attrDto = priorityOrderMstMapper.selectCommonPartsData(companyCd, priorityOrderCd);
        }

        GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(attrDto.getCommonPartsData(),companyCd);
        List<Map<String,Object>> attrList = priorityOrderMstAttrSortMapper.getAttrCol(companyCd, priorityOrderCd,commonTableName.getProdIsCore(),commonTableName.getProdMstClass());
        List<Map<String, Object>> attrCol = attrSortMapper.getAttrColForName(companyCd, priorityOrderCd, commonTableName.getProdIsCore(),commonTableName.getProdMstClass());
        String proAttrTable = MessageFormat.format(MagicString.PROD_JAN_ATTR_HEADER_SYS, MagicString.SPECIAL_SCHEMA_CD, MagicString.FIRST_CLASS_CD);
        List<Map<String, Object>> janSizeCol = zokuseiMstMapper.getJanSizeCol(proAttrTable);

        if (ptsDetailData != null){
            String zokuseiNm = Joiner.on(",").join(attrList.stream().map(map -> MapUtils.getString(map, "zokusei_nm","")).collect(Collectors.toList()));
            String janHeader = ptsDetailData.getJanHeader()+",商品名,"+zokuseiNm+",幅,高,奥行";
            ptsDetailData.setJanHeader(janHeader);
            StringBuilder s = new StringBuilder("taiCd,tanaCd,tanapositionCd,jan,faceCount,faceMen,faceKaiten,tumiagesu,zaikosu");
            if ("V3.0".equals(ptsDetailData.getVersioninfo())){
                s.append(",faceDisplayflg,facePosition,depthDisplayNum");
            }
            s.append(",janName");
            for (Map<String, Object> map : attrCol) {
                s.append(",").append(map.get("zokusei_colcd"));
            }
            s.append(",plano_width,plano_height,plano_depth");
            ptsDetailData.setJanColumns(s.toString());
            ptsDetailData.setTaiNum(shelfPtsDataMapper.getTaiNum(patternCd));
            ptsDetailData.setTanaNum(shelfPtsDataMapper.getTanaNum(patternCd));
            ptsDetailData.setFaceNum(shelfPtsDataMapper.getFaceNum(patternCd));
            ptsDetailData.setSkuNum(shelfPtsDataMapper.getSkuNum(patternCd));

            List<PtsTaiVo> taiData = shelfPtsDataMapper.getTaiData(patternCd);
            List<PtsTanaVo> tanaData = shelfPtsDataMapper.getTanaData(patternCd);
            String proTableName = MessageFormat.format(MagicString.PROD_JAN_INFO, MagicString.SPECIAL_SCHEMA_CD, MagicString.FIRST_CLASS_CD);
            List<LinkedHashMap<String,Object>> janData = shelfPtsDataMapper.getJanData(patternCd,attrCol,commonTableName.getProInfoTable(),janSizeCol,
                    proTableName);

            ptsDetailData.setPtsTaiList(taiData);
            ptsDetailData.setPtsTanaVoList(tanaData);
            ptsDetailData.setPtsJanDataList(janData);
        }

        return ResultMaps.result(ResultEnum.SUCCESS,ptsDetailData);
    }


    /**
     * 陳列順設定追加
     * @param workPriorityOrderSort
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> setDisplay(List<WorkPriorityOrderSort> workPriorityOrderSort) {
        String aud = httpSession.getAttribute("aud").toString();
        String companyCd = workPriorityOrderSort.get(0).getCompanyCd();
        Integer priorityOrderCd = workPriorityOrderSort.get(0).getPriorityOrderCd();
        shelfPtsDataMapper.deleteDisplay(companyCd,priorityOrderCd);
        if (workPriorityOrderSort.get(0).getSortNum() == null){
            return ResultMaps.result(ResultEnum.SUCCESS);
        }
        shelfPtsDataMapper.setDisplay(workPriorityOrderSort,aud);
        return ResultMaps.result(ResultEnum.SUCCESS);
    }
    /**
     * 陳列順設定展示
     * @param companyCd
     * @return
     */
    @Override
    public Map<String, Object> getDisplay(String companyCd,Integer priorityOrderCd) {
        String aud = httpSession.getAttribute("aud").toString();
        List<WorkPriorityOrderSort> displayList = shelfPtsDataMapper.getDisplay(companyCd, aud,priorityOrderCd);

        return ResultMaps.result(ResultEnum.SUCCESS,displayList);
    }

    /**
     * csvファイルのダウンロード
     * @param ptsCsvVO
     * @param response
     * @throws IOException
     */
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
            ShelfPtsData ptsData = priorityAllPtsMapper.selectPtsCdByAuthorCd(companyCd, authorCd, typeCd, shelfPatternCd);
            Integer ptsCd = ptsData.getId();
            fileName = ptsData.getFileName();

            shelfPtsDataVersion = priorityAllPtsMapper.selectAllVersionByPtsCd(companyCd, ptsCd);
            shelfPtsDataTaimst = priorityAllPtsMapper.selectAllTaimstByPtsCd(companyCd, ptsCd);
            shelfPtsDataTanamst = priorityAllPtsMapper.selectAllTanamstByPtsCd(companyCd, ptsCd);
            shelfPtsDataJandata = priorityAllPtsMapper.selectAllJandataByPtsCd(companyCd, ptsCd);
        }

        response.setHeader(HttpHeaders.CONTENT_TYPE, "text/csv;charset=Shift_JIS");

        OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream(), "Shift_JIS");
        String format = MessageFormat.format("attachment;filename={0};",  UriUtils.encode(fileName, "utf-8"));
        response.setHeader("Content-Disposition", format);

        this.generateCsv(shelfPtsDataVersion, shelfPtsDataTaimst, shelfPtsDataTanamst, shelfPtsDataJandata, writer);
    }

    public void generateCsv(ShelfPtsDataVersion shelfPtsDataVersion, List<ShelfPtsDataTaimst> shelfPtsDataTaimst,
                            List<ShelfPtsDataTanamst> shelfPtsDataTanamst,  List<ShelfPtsDataJandata> shelfPtsDataJandata, OutputStreamWriter printWriter){
        try(CsvWriter csvWriter = CsvWriter.builder().build(printWriter)) {
            csvWriter.writeRow(Lists.newArrayList(shelfPtsDataVersion.getCommoninfo(),
                    shelfPtsDataVersion.getVersioninfo(), shelfPtsDataVersion.getOutflg()));
            csvWriter.writeRow(shelfPtsDataVersion.getModename());
            csvWriter.writeRow(shelfPtsDataVersion.getTaiHeader().split(","));

            for (ShelfPtsDataTaimst ptsDataTaimst : shelfPtsDataTaimst) {
                csvWriter.writeRow(Lists.newArrayList(ptsDataTaimst.getTaiCd() + "",
                        ptsDataTaimst.getTaiHeight() + "", ptsDataTaimst.getTaiWidth() + "", ptsDataTaimst.getTaiDepth() + "",
                        Optional.ofNullable(ptsDataTaimst.getTaiName()).orElse("")));
            }

            csvWriter.writeRow(shelfPtsDataVersion.getTanaHeader().split(","));
            for (ShelfPtsDataTanamst ptsDataTanamst : shelfPtsDataTanamst) {
                csvWriter.writeRow(Lists.newArrayList(ptsDataTanamst.getTaiCd() + "",
                        ptsDataTanamst.getTanaCd() + "", ptsDataTanamst.getTanaHeight() + "", ptsDataTanamst.getTanaWidth() + "",
                        ptsDataTanamst.getTanaDepth() + "", ptsDataTanamst.getTanaThickness() + "", ptsDataTanamst.getTanaType() + ""));
            }

            String[] janHeaders = shelfPtsDataVersion.getJanHeader().split(",");
            csvWriter.writeRow(janHeaders);
            List<String> janData = null;
            for (ShelfPtsDataJandata ptsDataJandata : shelfPtsDataJandata) {
                janData = Lists.newArrayList(ptsDataJandata.getTaiCd() + "",
                        ptsDataJandata.getTanaCd() + "", ptsDataJandata.getTanapositionCd() + "", ptsDataJandata.getJan() + "",
                        ptsDataJandata.getFaceCount() + "", ptsDataJandata.getFaceMen() + "", ptsDataJandata.getFaceKaiten() + "",
                        ptsDataJandata.getTumiagesu() + "",
                        Optional.ofNullable(ptsDataJandata.getZaikosu()).orElse(0) + "", Optional.ofNullable(ptsDataJandata.getFaceDisplayflg()).orElse(0) + "",
                        Optional.ofNullable(ptsDataJandata.getFacePosition()).orElse(0) + "", Optional.ofNullable(ptsDataJandata.getDepthDisplayNum()).orElse(0) + "");
                csvWriter.writeRow(janData.subList(0, janHeaders.length));
            }
        } catch (IOException e) {
            logger.error("", e);
        }
    }

    @Override
    public void basicSaveWorkPtsData(String companyCd, String authorCd, Integer priorityOrderCd, List<PriorityOrderResultDataDto> resultData,
                                     int isReOrder) {
        WorkPriorityOrderMst workPriorityOrderMst = workPriorityOrderMstMapper.selectByAuthorCd(companyCd, authorCd, priorityOrderCd);
        Long shelfPatternCd = workPriorityOrderMst.getShelfPatternCd();

        //採用された商品をすべて検索し、棚順に並べ替え、棚上の商品の位置をマークする
        List<PriorityOrderResultDataDto> positionResultData = resultData;

        //既存の新しいptsを検出し,削除してから保存する
        //新しいptsにデータがあるptsCd
        ShelfPtsData shelfPtsData = shelfPtsDataMapper.selectWorkPtsCdByAuthorCd(companyCd, authorCd, priorityOrderCd, shelfPatternCd);

        if(Optional.ofNullable(shelfPtsData).isPresent()){
            Integer oldPtsCd = shelfPtsData.getId();
            shelfPtsDataMapper.deletePtsDataJandata(oldPtsCd);
            if (positionResultData!=null && !positionResultData.isEmpty()) {
                positionResultData = positionResultData.stream()
                        .sorted(Comparator.comparing(PriorityOrderResultDataDto::getTanapositionCd))
                        .sorted(Comparator.comparing(PriorityOrderResultDataDto::getTanaCd))
                        .sorted(Comparator.comparing(PriorityOrderResultDataDto::getTaiCd)).collect(Collectors.toList());


                List<PriorityOrderResultDataDto> finalPositionResultData = this.ptsPositionProcessing(positionResultData);
                int currentTai=0;
                int currentTana=0;
                int currentTanaPosition=1;
                for (int i = 0; i < finalPositionResultData.size(); i++) {
                    PriorityOrderResultDataDto positionResultDatum = finalPositionResultData.get(i);

                    if(!Objects.equals(currentTai, positionResultDatum.getTaiCd()) || !Objects.equals(currentTana, positionResultDatum.getTanaCd())){
                        currentTanaPosition = 1;
                    }

                    positionResultDatum.setTanapositionCd(currentTanaPosition++);
                    positionResultData.set(i, positionResultDatum);

                    currentTai = positionResultDatum.getTaiCd();
                    currentTana = positionResultDatum.getTanaCd();

                }

                shelfPtsDataMapper.insertPtsDataJandata(positionResultData, oldPtsCd, companyCd, authorCd);
            }
        }
    }

    public List<PriorityOrderResultDataDto> ptsPositionProcessing(List<PriorityOrderResultDataDto> positionResultData){
        List<PriorityOrderResultDataDto> finalPositionResultData = new ArrayList<>();
        Map<String, List<PriorityOrderResultDataDto>> positionResultDataByTaiTana = positionResultData.stream().collect(Collectors.groupingBy(data -> data.getTaiCd() + "_" + data.getTanaCd()));
        for (Map.Entry<String, List<PriorityOrderResultDataDto>> entry : positionResultDataByTaiTana.entrySet()) {
            List<PriorityOrderResultDataDto> value = entry.getValue();
            List<PriorityOrderResultDataDto> noChangeJanList = value.stream().filter(data ->
                    Joiner.on("_").join(Lists.newArrayList(data.getOldTaiCd()+"", data.getOldTanaCd()+"", data.getOldTanapositionCd()+""))
                            .equals(Joiner.on("_").join(Lists.newArrayList(data.getTaiCd(), data.getTanaCd(), data.getTanapositionCd())))).collect(Collectors.toList());
            List<PriorityOrderResultDataDto> changeJanList = value.stream()
                    .filter(data ->!Joiner.on("_").join(Lists.newArrayList(data.getOldTaiCd()+"", data.getOldTanaCd()+"", data.getOldTanapositionCd()+""))
                            .equals(Joiner.on("_").join(Lists.newArrayList(data.getTaiCd(), data.getTanaCd(), data.getTanapositionCd())))).collect(Collectors.toList());

            for (PriorityOrderResultDataDto dataDto : noChangeJanList) {
                Integer tanapositionCd = dataDto.getTanapositionCd();
                if (tanapositionCd<=changeJanList.size()) {
                    changeJanList.add(tanapositionCd-1, dataDto);
                }else{
                    changeJanList.add(dataDto);
                }
            }

            finalPositionResultData.addAll(changeJanList);
        }
        return finalPositionResultData;
    }
    /**
     * ptsデータを最終テーブルに保存
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
     * 棚pattern関連csv履歴データの取得
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
     * 棚pattern関連ptsのドロップダウンボックスデータ
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
     * 棚pattern別pts情報の取得
     *
     * @param companyCd
     * @param
     * @return
     */
    @Override
    public Map<String, Object> getPtsInfoOfPattern(String companyCd) {
        logger.info("つかむ取棚pattern別的pts信息参数：{}", companyCd);
        List<ShelfPtsData> shelfPtsNameVOList = shelfPtsDataMapper.selectPtsInfoOfPattern(companyCd);
        return ResultMaps.result(ResultEnum.SUCCESS, shelfPtsNameVOList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String,Object> setPtsTanaSize(List<PtsTanaVo> ptsTanaVoList) {
        try {

            Integer priorityOrderCd = ptsTanaVoList.get(0).getPriorityOrderCd();
            String companyCd = ptsTanaVoList.get(0).getCompanyCd();
            String authorCd = httpSession.getAttribute("aud").toString();
            Integer taiCd = ptsTanaVoList.get(0).getTaiCd();
            shelfPtsDataMapper.deletePtsJandataByPriorityOrderCd(priorityOrderCd);
            Integer id = shelfPtsDataMapper.getId(companyCd, priorityOrderCd);
            for (PtsTanaVo ptsTanaVo : ptsTanaVoList) {
                if (ptsTanaVo.getGroup().isEmpty()){
                    ArrayList<BasicPatternRestrictRelation> group = new ArrayList<>();
                    BasicPatternRestrictRelation basicPatternRestrictRelation = new BasicPatternRestrictRelation();
                    basicPatternRestrictRelation.setTaiCd(ptsTanaVo.getTaiCd());
                    basicPatternRestrictRelation.setTanaCd(ptsTanaVo.getTanaCd());
                    basicPatternRestrictRelation.setTanaPosition(1);
                    basicPatternRestrictRelation.setRestrictCd(9999L);
                    basicPatternRestrictRelation.setArea(100L);
                    group.add(basicPatternRestrictRelation);
                    ptsTanaVo.setGroup(group);
                }
            }
            shelfPtsDataMapper.deleteTana(taiCd,id);
            shelfPtsDataMapper.updTanaSize(ptsTanaVoList,id,authorCd,companyCd);
            basicPatternRestrictRelationMapper.deleteTana(taiCd,companyCd,priorityOrderCd);
            basicPatternRestrictRelationMapper.setTaiInfo(ptsTanaVoList,companyCd,priorityOrderCd,authorCd);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    /**
     * 棚pattern関連ptsの詳細(新)の取得
     * @param patternCd
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Map<String, Object> getNewPtsDetailData(Integer patternCd, String companyCd, Integer priorityOrderCd) {
        PriorityOrderAttrDto attrDto = priorityOrderMstMapper.selectCommonPartsData(companyCd, priorityOrderCd);
        GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(attrDto.getCommonPartsData(),companyCd);
        List<Map<String,Object>> attrList = priorityOrderMstAttrSortMapper.getAttrCol(companyCd, priorityOrderCd,commonTableName.getProdIsCore(),commonTableName.getProdMstClass());
        PtsDetailDataVo ptsDetailData = shelfPtsDataMapper.getPtsNewDetailData(priorityOrderCd);


        List<Map<String, Object>> attrCol = attrSortMapper.getAttrColForName(companyCd, priorityOrderCd, commonTableName.getProdIsCore(),commonTableName.getProdMstClass());
        List<Map<String, Object>> janSizeCol = zokuseiMstMapper.getJanSizeCol(commonTableName.getProAttrTable());
        if (ptsDetailData != null) {

            String zokuseiNm = Joiner.on(",").join(attrList.stream().map(map -> MapUtils.getString(map, "zokusei_nm")).collect(Collectors.toList()));
            String janHeader = ptsDetailData.getJanHeader()+","+"備考"+",商品名,"+zokuseiNm+",幅,高,奥行";
            ptsDetailData.setJanHeader(janHeader);

            ptsDetailData.setJanColumns(this.colHeader(attrCol,ptsDetailData).toString());
            ptsDetailData.setTanaHeader(ptsDetailData.getTanaHeader()+","+"備考");
            ptsDetailData.setTaiNum(shelfPtsDataMapper.getNewTaiNum(priorityOrderCd));
            ptsDetailData.setTanaNum(shelfPtsDataMapper.getNewTanaNum(priorityOrderCd));
            ptsDetailData.setFaceNum(shelfPtsDataMapper.getNewFaceNum(priorityOrderCd)==null?0:shelfPtsDataMapper.getNewFaceNum(priorityOrderCd));
            ptsDetailData.setSkuNum(shelfPtsDataMapper.getNewSkuNum(priorityOrderCd));
            //新台、棚、商品データ
            List<PtsTaiVo> newTaiData = shelfPtsDataMapper.getNewTaiData(priorityOrderCd);
            List<PtsTanaVo> newTanaData = shelfPtsDataMapper.getNewTanaData(priorityOrderCd);
            String proTableName = MessageFormat.format(MagicString.PROD_JAN_INFO, MagicString.SPECIAL_SCHEMA_CD, MagicString.FIRST_CLASS_CD);
            List<LinkedHashMap<String,Object>> newJanData = shelfPtsDataMapper.getNewJanDataTypeMap(priorityOrderCd,attrCol,
                    commonTableName.getProInfoTable(),janSizeCol, proTableName);


                //既存台、棚、商品データ
            List<PtsTaiVo> taiData = shelfPtsDataMapper.getTaiData(patternCd);
            List<PtsTanaVo> tanaData = shelfPtsDataMapper.getTanaData(patternCd);
            List<LinkedHashMap<String,Object>> janData = shelfPtsDataMapper.getJanData(patternCd,attrCol,commonTableName.getProInfoTable(),janSizeCol, proTableName);
            //棚、商品の変更チェック
            //棚変更：高さ変更
            newTanaData.stream()
                    .filter(map -> tanaData.stream().anyMatch(map1 -> map1.getTaiCd().equals(map.getTaiCd())
                            && map1.getTanaCd().equals(map.getTanaCd())
                    ))
                    .forEach(map ->
                            getNewPtsTanaHeightChangeRemarks(newTanaData, map, newTaiData, tanaData, taiData)
                    );
            //棚変更：棚新規作成
            newTanaData.stream()
                    .filter(map -> tanaData.stream().noneMatch(map1 -> map1.getTaiCd().equals(map.getTaiCd())
                            && map1.getTanaCd().equals(map.getTanaCd())
                    ))
                    .forEach(map -> map.setRemarks(MagicString.MSG_NEW_TANA));
            //商品変更：新規商品
            newJanData.stream()
                    .filter(map -> janData.stream().noneMatch(map1 -> MapUtils.getString(map1,MagicString.JAN).equals(MapUtils.getString(map,MagicString.JAN))
                    ))
                    .forEach(map -> map.put("remarks",MagicString.MSG_NEW_JAN));
            //商品変更：位置変更
            newJanData.stream()
                    .filter(map -> janData.stream().noneMatch(map1 -> MapUtils.getString(map1,MagicString.JAN).equals(MapUtils.getString(map,MagicString.JAN))
                            && MapUtils.getInteger(map1,MagicString.TAI_CD).equals(MapUtils.getInteger(map,MagicString.TAI_CD))
                            && MapUtils.getInteger(map1,MagicString.TANA_CD).equals(MapUtils.getInteger(map,MagicString.TANA_CD))
                            && MapUtils.getInteger(map1,MagicString.TANAPOSITIONCD).equals(MapUtils.getInteger(map,MagicString.TANAPOSITIONCD)))
                    )
                    .forEach(map -> {
                        if(!MagicString.MSG_NEW_JAN.equals(map.get(MagicString.REMARKS))){
                            map.put(MagicString.REMARKS,MagicString.MSG_TANA_POSITION_CHANGE);
                        }
                    });
            //商品変更：フェース変更
            newJanData.stream()
                    .filter(map -> janData.stream().anyMatch(map1 -> MapUtils.getString(map1,MagicString.JAN).equals(MapUtils.getString(map,MagicString.JAN))
                            && MapUtils.getInteger(map1,MagicString.TAI_CD).equals(MapUtils.getInteger(map,MagicString.TAI_CD))
                            && MapUtils.getInteger(map1,MagicString.TANA_CD).equals(MapUtils.getInteger(map,MagicString.TANA_CD))
                            && MapUtils.getInteger(map1,MagicString.TANAPOSITIONCD).equals(MapUtils.getInteger(map,MagicString.TANAPOSITIONCD))
                            && !MapUtils.getInteger(map1,MagicString.FACE_COUNT).equals(MapUtils.getInteger(map,MagicString.FACE_COUNT))
                    ))
                    .forEach(map -> map.put(MagicString.REMARKS,(StringUtils.hasLength(map.get(MagicString.REMARKS).toString()) ? map.get(MagicString.REMARKS).toString() + "," : "")
                            + MagicString.MSG_FACE_CHANGE
                            + janData.stream().filter(map1 -> MapUtils.getString(map1,MagicString.JAN).equals(MapUtils.getString(map,MagicString.JAN))
                                    && MapUtils.getInteger(map1,MagicString.TAI_CD).equals(MapUtils.getInteger(map,MagicString.TAI_CD))
                                    && MapUtils.getInteger(map1,MagicString.TANA_CD).equals(MapUtils.getInteger(map,MagicString.TANA_CD))
                                    && MapUtils.getInteger(map1,MagicString.TANAPOSITIONCD).equals(MapUtils.getInteger(map,MagicString.TANAPOSITIONCD))
                            ).findFirst().get().get(MagicString.FACE_COUNT)));
            ptsDetailData.setPtsTaiList(newTaiData);
            ptsDetailData.setPtsTanaVoList(newTanaData);
            ptsDetailData.setPtsJanDataList(newJanData);
        }
        return ResultMaps.result(ResultEnum.SUCCESS,ptsDetailData);
    }

        public StringBuilder colHeader(List<Map<String, Object>> attrCol,PtsDetailDataVo ptsDetailData){
            StringBuilder s = new StringBuilder("taiCd,tanaCd,tanapositionCd,jan,faceCount,faceMen,faceKaiten,tumiagesu,zaikosu,");
            if ("V3.0".equals(ptsDetailData.getVersioninfo())){
                s.append("faceDisplayflg,facePosition,depthDisplayNum,remarks");
            }else {
                s.append(MagicString.REMARKS);
            }
            s.append(",janName");
            for (Map<String, Object> map : attrCol) {
                s.append(",").append(map.get("zokusei_colcd"));
            }
            s.append(",plano_width,plano_height,plano_depth");
            return s;
        }
    /**
     * 新ptsの棚高さの備考
     * @param newTanaData    　新棚のList
     * @param newTanaEntity　　比較する新棚
     * @param newTaiData    新台
     * @param tanaData      既存棚
     * @param taiData       既存台
     */
    public void getNewPtsTanaHeightChangeRemarks(List<PtsTanaVo> newTanaData, PtsTanaVo newTanaEntity, List<PtsTaiVo> newTaiData,
                                                 List<PtsTanaVo> tanaData, List<PtsTaiVo> taiData) {
        Integer newHeight = 0;
        Integer oldHeight = 0;
        Integer oldTanaHeight = 0;
        //高さの計算
        Optional<PtsTanaVo> nextNewTana = newTanaData.stream().filter(next -> next.getTaiCd().equals(newTanaEntity.getTaiCd())
                && next.getTanaCd().equals(newTanaEntity.getTanaCd() + 1)).findFirst();
        if (nextNewTana.isPresent()) {
            newHeight = nextNewTana.get().getTanaHeight() - newTanaEntity.getTanaHeight() - nextNewTana.get().getTanaThickness();
        } else {
            Optional<PtsTaiVo> newTaiOptional = newTaiData.stream().filter(tai -> tai.getTaiCd().equals(newTanaEntity.getTanaCd())).findFirst();
            if (newTaiOptional.isPresent()) {
                newHeight = newTaiOptional.get().getTaiHeight() - newTanaEntity.getTanaHeight();
            }
        }
        //旧高さの計算
        Optional<PtsTanaVo> oldTanaOptional = tanaData.stream().filter(next -> next.getTaiCd().equals(newTanaEntity.getTaiCd())
                && next.getTanaCd().equals(newTanaEntity.getTanaCd())).findFirst();

        if (oldTanaOptional.isPresent()) {
            oldTanaHeight = oldTanaOptional.get().getTanaHeight();
        }
        Optional<PtsTanaVo> nextOldTana = tanaData.stream().filter(next -> next.getTaiCd().equals(newTanaEntity.getTaiCd())
                && next.getTanaCd().equals(newTanaEntity.getTanaCd() + 1)).findFirst();
        if (nextOldTana.isPresent()) {
            oldHeight = nextOldTana.get().getTanaHeight() - oldTanaHeight - nextOldTana.get().getTanaThickness();
        } else {
            Optional<PtsTaiVo> oldTaiOptional = taiData.stream().filter(tai -> tai.getTaiCd().equals(newTanaEntity.getTanaCd())).findFirst();
            if (oldTaiOptional.isPresent()) {
                oldHeight = oldTaiOptional.get().getTaiHeight() - oldTanaHeight;
            }
        }
        //変更あるの場合
        if (!newHeight.equals(oldHeight)) {
            newTanaEntity.setRemarks(MagicString.MSG_HEIGHT_CHANGE.replace("{height}", String.valueOf(oldHeight)));
        }
    }
}
