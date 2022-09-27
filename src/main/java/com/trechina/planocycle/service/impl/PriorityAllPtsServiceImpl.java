package com.trechina.planocycle.service.impl;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.trechina.planocycle.constant.MagicString;
import com.trechina.planocycle.entity.dto.GetCommonPartsDataDto;
import com.trechina.planocycle.entity.dto.PriorityAllPtsDataDto;
import com.trechina.planocycle.entity.dto.PriorityOrderResultDataDto;
import com.trechina.planocycle.entity.po.*;
import com.trechina.planocycle.entity.vo.*;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.*;
import com.trechina.planocycle.service.CommonMstService;
import com.trechina.planocycle.service.PriorityAllPtsService;
import com.trechina.planocycle.utils.ResultMaps;
import de.siegmar.fastcsv.writer.CsvWriter;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class PriorityAllPtsServiceImpl implements PriorityAllPtsService {
    @Autowired
    private CommonMstService commonMstService;
    @Autowired
    private PriorityAllPtsMapper priorityAllPtsMapper;
    @Autowired
    private ShelfPtsDataMapper shelfPtsDataMapper;
    @Autowired
    private PriorityAllMstMapper priorityAllMstMapper;
    @Autowired
    private ShelfPtsDataVersionMapper shelfPtsDataVersionMapper;
    @Autowired
    private PriorityOrderMstMapper priorityOrderMstMapper;
    @Autowired
    private BasicPatternMstServiceImpl basicPatternMstService;
    @Autowired
    private PriorityOrderMstAttrSortMapper priorityOrderMstAttrSortMapper;
    @Autowired
    private ShelfPtsServiceImpl shelfPtsService;
    @Autowired
    private ZokuseiMstMapper zokuseiMstMapper;
    @Autowired
    private WorkPriorityOrderMstMapper workPriorityOrderMstMapper;
    @Autowired
    private HttpSession session;
    @Autowired
    private PriorityOrderMstAttrSortMapper attrSortMapper;
    @Autowired
    private WorkPriorityAllResultDataMapper workPriorityAllResultDataMapper;
    @Autowired
    private PriorityOrderShelfDataServiceImpl priorityOrderShelfDataService;
    @Autowired
    private BasicPatternRestrictResultMapper basicPatternRestrictResultMapper;
    @Autowired
    private ZokuseiMapper zokuseiMapper;
    private final Logger logger = LoggerFactory.getLogger(PriorityAllPtsServiceImpl.class);

    @Override
    public void saveWorkPtsData(String companyCd, String authorCd, Integer priorityAllCd, Integer patternCd) {
        //採用された商品をすべて検索し、棚順に並べ替え、棚上の商品の位置をマークする
        //新しいptsにデータがあるptsCd
        ShelfPtsData shelfPtsData = priorityAllPtsMapper.selectWorkPtsCdByAuthorCd(companyCd, authorCd, priorityAllCd, patternCd);
        //テンポラリ・テーブルのptscd
        Integer ptsCd = shelfPtsDataMapper.getPtsCd(patternCd);

        PriorityAllPtsDataDto priorityOrderPtsDataDto = new PriorityAllPtsDataDto();
        priorityOrderPtsDataDto.setPriorityAllCd(priorityAllCd);
        priorityOrderPtsDataDto.setOldPtsCd(ptsCd);
        priorityOrderPtsDataDto.setCompanyCd(companyCd);
        priorityOrderPtsDataDto.setAuthorCd(authorCd);

        if(Optional.ofNullable(shelfPtsData).isPresent()){
            Integer oldPtsCd = shelfPtsData.getId();
            priorityAllPtsMapper.deletePtsData(oldPtsCd);
            priorityAllPtsMapper.deletePtsTaimst(oldPtsCd);
            priorityAllPtsMapper.deletePtsTanamst(oldPtsCd);
            priorityAllPtsMapper.deletePtsVersion(oldPtsCd);
            priorityAllPtsMapper.deletePtsDataJandata(oldPtsCd);
        }

        ShelfPtsDataVersion shelfPtsDataVersion = shelfPtsDataVersionMapper.selectByPrimaryKey(companyCd, ptsCd);
        String modeName = shelfPtsDataVersion.getModename();
        //modeNameはptsをダウンロードするファイル名として
        priorityOrderPtsDataDto.setFileName(modeName+"_"+patternCd+"_new.csv");
        //既存のptsからデータをクエリーする
        priorityAllPtsMapper.insertPtsData(priorityOrderPtsDataDto);
        Integer id = priorityOrderPtsDataDto.getId();
        priorityAllPtsMapper.insertPtsTaimst(ptsCd, id, authorCd, priorityAllCd, patternCd);
        priorityAllPtsMapper.insertPtsTanamst(ptsCd, id, authorCd, priorityAllCd, patternCd);
        priorityAllPtsMapper.insertPtsVersion(ptsCd, id, authorCd, priorityAllCd, patternCd);
    }

    @Override
    public void saveWorkPtsJanData(String companyCd, String authorCd, Integer priorityAllCd, Integer patternCd,
                                   List<PriorityOrderResultDataDto> priorityOrderResultData, int isReOrder) {
        //採用された商品をすべて検索し、棚順に並べ替え、棚上の商品の位置をマークする
        List<PriorityOrderResultDataDto> positionResultData = priorityOrderResultData;
        ShelfPtsData shelfPtsData = priorityAllPtsMapper.selectWorkPtsCdByAuthorCd(companyCd, authorCd, priorityAllCd, patternCd);

        //テンポラリ・テーブルのptscd
        if(Optional.ofNullable(shelfPtsData).isPresent()){
            Integer oldPtsCd = shelfPtsData.getId();
            shelfPtsDataMapper.deletePtsDataJandata(oldPtsCd);
            if (!positionResultData.isEmpty()) {
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

                if (!positionResultData.isEmpty()) {
                    priorityAllPtsMapper.insertPtsDataJandata(positionResultData, oldPtsCd, companyCd, authorCd, priorityAllCd, patternCd);
                }
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
    @Override
    public Map<String, Object> getPtsDetailData(Integer patternCd, String companyCd, Integer priorityAllCd) {
        String authorCd = session.getAttribute("aud").toString();
        Integer priorityOrderCd = priorityAllMstMapper.getWorkPriorityOrderCd( priorityAllCd, companyCd);
        PriorityOrderMst priorityOrderMst = priorityOrderMstMapper.selectOrderMstByPriorityOrderCd(priorityOrderCd);
        GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(priorityOrderMst.getCommonPartsData(),companyCd);
        List<Map<String,Object>> attrList = priorityOrderMstAttrSortMapper.getAttrCol(companyCd, priorityOrderCd,commonTableName.getProdIsCore(),commonTableName.getProdMstClass());
        List<Map<String, Object>> attrCol = attrSortMapper.getAttrColForName(companyCd, priorityOrderCd, commonTableName.getProdIsCore(),commonTableName.getProdMstClass());
        List<Map<String, Object>> janSizeCol = zokuseiMstMapper.getJanSizeCol(commonTableName.getProAttrTable());

        PtsDetailDataVo ptsDetailData = priorityAllPtsMapper.getPtsDetailData(companyCd, authorCd, priorityAllCd, patternCd);

        if(ptsDetailData != null){
            Integer id = ptsDetailData.getId();
            ptsDetailData.setSkuNum(priorityAllPtsMapper.getNewSkuNum(ptsDetailData.getId()));
            ptsDetailData.setFaceNum(priorityAllPtsMapper.getNewFaceNum(ptsDetailData.getId()));
            ptsDetailData.setTaiNum(priorityAllPtsMapper.getNewTaiNum(ptsDetailData.getId()));
            ptsDetailData.setTanaNum(priorityAllPtsMapper.getNewTanaNum(ptsDetailData.getId()));
            String zokuseiNm = Joiner.on(",").join(attrList.stream().map(map -> MapUtils.getString(map, "zokusei_nm")).collect(Collectors.toList()));
            String janHeader = ptsDetailData.getJanHeader()+","+"備考"+",商品名,"+zokuseiNm+",幅,高,奥行";
            ptsDetailData.setJanHeader(janHeader);
            StringBuilder s = new StringBuilder("taiCd,tanaCd,tanapositionCd,jan,faceCount,faceMen,faceKaiten,tumiagesu,zaikosu");
            if ("V3.0".equals(ptsDetailData.getVersioninfo())){
                s.append(",faceDisplayflg,facePosition,depthDisplayNum,remarks");
            }else {
                s.append(",remarks");
            }
            s.append(",janName");
            for (Map<String, Object> map : attrCol) {
                s.append(",").append(map.get(MagicString.ZOUKUSEI_COLCD));
            }
            s.append(",plano_width,plano_height,plano_depth");
            ptsDetailData.setJanColumns(s.toString());
            ptsDetailData.setTanaHeader(ptsDetailData.getTanaHeader()+","+"備考");

            String proTableName = MessageFormat.format(MagicString.PROD_JAN_INFO, MagicString.SPECIAL_SCHEMA_CD, MagicString.FIRST_CLASS_CD);

            List<PtsTaiVo> newTaiData = priorityAllPtsMapper.getTaiData(id);
            List<PtsTanaVo> newTanaData = priorityAllPtsMapper.getTanaData(id);
            List<LinkedHashMap<String,Object>> newJanData = priorityAllPtsMapper.getJanData(id,attrCol,commonTableName.getProInfoTable(),janSizeCol, proTableName);


            //既存台、棚、商品データ
            List<PtsTaiVo> taiData = shelfPtsDataMapper.getTaiData(patternCd);
            List<PtsTanaVo> tanaData = shelfPtsDataMapper.getTanaData(patternCd);
            List<LinkedHashMap<String,Object>> janData = shelfPtsDataMapper.getJanData(patternCd,attrCol,commonTableName.getProInfoTable(),janSizeCol, proTableName);
            //棚、商品の変更チェック
            //棚変更：高さ変更
            logger.info("start,{}",System.currentTimeMillis());
            newTanaData.stream()
                    .filter(map -> tanaData.stream().anyMatch(map1 -> map1.getTaiCd().equals(map.getTaiCd())
                            && map1.getTanaCd().equals(map.getTanaCd())
                    ))
                    .forEach(map ->
                            shelfPtsService.getNewPtsTanaHeightChangeRemarks(newTanaData, map, newTaiData, tanaData, taiData)
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
                    .forEach(map -> map.put(MagicString.REMARKS,MagicString.MSG_NEW_JAN));
            //商品変更：位置変更
            newJanData.stream()
                    .filter(map -> janData.stream().anyMatch(map1 -> MapUtils.getString(map1,MagicString.JAN).equals(MapUtils.getString(map,MagicString.JAN))
                            && (!MapUtils.getInteger(map1,MagicString.TAI_CD).equals(MapUtils.getInteger(map,MagicString.TAI_CD))
                            || !MapUtils.getInteger(map1,MagicString.TANA_CD).equals(MapUtils.getInteger(map,MagicString.TANA_CD))
                            || !MapUtils.getInteger(map1,MagicString.TANAPOSITIONCD).equals(MapUtils.getInteger(map,MagicString.TANAPOSITION_CD)))
                    ))
                    .forEach(map -> {
                        LinkedHashMap<String,Object> oldPtsJanDataVo = janData.stream().filter(map1 -> MapUtils.getString(map1,MagicString.JAN).equals(MapUtils.getString(map,MagicString.JAN))).findFirst().get();
                        map.put(MagicString.REMARKS,MagicString.MSG_TANA_POSITION_CHANGE.replace("{tai}", String.valueOf(MapUtils.getInteger(oldPtsJanDataVo,MagicString.TAI_CD)))
                                .replace("{tana}", String.valueOf(MapUtils.getInteger(oldPtsJanDataVo,MagicString.TANA_CD)))
                                .replace("{position}", String.valueOf(MapUtils.getInteger(oldPtsJanDataVo,MagicString.TANAPOSITION_CD))));
                    });
            //商品変更：フェース変更
            newJanData.stream()
                    .filter(map -> janData.stream().anyMatch(map1 -> MapUtils.getString(map1,MagicString.JAN).equals(MapUtils.getString(map,MagicString.JAN))
                            && !MapUtils.getInteger(map1,MagicString.FACE_COUNT).equals(MapUtils.getInteger(map,MagicString.FACE_COUNT))
                    ))
                    .forEach(map -> map.put(MagicString.REMARKS,(StringUtils.hasLength(map.get(MagicString.REMARKS).toString()) ? map.get(MagicString.REMARKS).toString() + "," : "")
                            + MagicString.MSG_FACE_CHANGE
                            + janData.stream().filter(map1 -> MapUtils.getString(map1,MagicString.JAN).equals(MapUtils.getString(map,MagicString.JAN))).findFirst().get().get("faceCount")));
            logger.info("end,{}", System.currentTimeMillis());
            ptsDetailData.setPtsTaiList(newTaiData);
            ptsDetailData.setPtsTanaVoList(newTanaData);
            ptsDetailData.setPtsJanDataList(newJanData);
        }

        return ResultMaps.result(ResultEnum.SUCCESS,ptsDetailData);
    }

    @Override
    public void batchDownloadPtsData(PriorityAllVO priorityAllVO, HttpServletResponse response) throws IOException {
        String authorCd = session.getAttribute("aud").toString();
        Integer priorityAllCd = priorityAllVO.getPriorityAllCd();
        String companyCd = priorityAllVO.getCompanyCd();
        String zipFileName = null;

        List<ShelfPtsData> shelfPtsDataList = priorityAllPtsMapper.selectByPriorityAllCd(companyCd, authorCd, priorityAllCd);
        long currentTimeMillis = System.currentTimeMillis();

        String path = this.getClass().getClassLoader().getResource("").getPath();
        logger.info("parent path: {}", path);

        String fileParentPath = Joiner.on(File.separator).join(Lists.newArrayList(path, currentTimeMillis));
        File file = new File(fileParentPath);
        if (!file.exists()) {
            boolean isMkdir = file.mkdirs();
            logger.info("mkdir:{}",isMkdir);
        }

        zipFileName = MessageFormat.format("全パターン{0}.zip", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        String format = MessageFormat.format("attachment;filename={0};",  UriUtils.encode(zipFileName, "utf-8"));
        response.setHeader("Content-Disposition", format);
        response.setHeader(HttpHeaders.CONTENT_TYPE, "application/zip");

        ServletOutputStream outputStream = response.getOutputStream();

        try(ZipOutputStream zos = new ZipOutputStream(outputStream)) {
            for (ShelfPtsData ptsData : shelfPtsDataList) {
                Integer ptsCd = ptsData.getId();
                String fileName = ptsData.getFileName();

                ShelfPtsDataVersion shelfPtsDataVersion = priorityAllPtsMapper.selectAllVersionByPtsCd(companyCd, ptsCd);
                List<ShelfPtsDataTaimst> shelfPtsDataTaimst = priorityAllPtsMapper.selectAllTaimstByPtsCd(companyCd, ptsCd);
                List<ShelfPtsDataTanamst> shelfPtsDataTanamst = priorityAllPtsMapper.selectAllTanamstByPtsCd(companyCd, ptsCd);
                List<ShelfPtsDataJandata> shelfPtsDataJandata = priorityAllPtsMapper.selectAllJandataByPtsCd(companyCd, ptsCd);

                String filePath = this.generateCsv2File(shelfPtsDataVersion, shelfPtsDataTaimst, shelfPtsDataTanamst, shelfPtsDataJandata, fileParentPath, fileName);
                this.writeZip(filePath, zos, fileName);
            }

            zos.flush();
        } catch (IOException e) {
            logger.error("", e);
        } finally {
            outputStream.close();
            this.deleteDir(new File(fileParentPath));
        }
    }

    @Override
    public Map<String, Object> getBasicAllPlatformShedJans(BasicAllPts basicAllPts) {
        Integer priorityAllCd = basicAllPts.getPriorityAllCd();
        String companyCd = basicAllPts.getCompanyCd();
        Integer patternCd = basicAllPts.getPatternCd();
        Integer priorityOrderCd = priorityAllMstMapper.getWorkPriorityOrderCd(priorityAllCd,companyCd);
        WorkPriorityOrderMstEditVo priorityOrderMst = workPriorityOrderMstMapper.getPriorityOrderMst(companyCd, priorityOrderCd);
        String commonPartsData = priorityOrderMst.getCommonPartsData();
        GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(commonPartsData, companyCd);
        List<Map<String, Object>> ptsGroup = this.getBasicAllPtsGroup(companyCd, priorityOrderCd,priorityAllCd,patternCd);
        ptsGroup= ptsGroup.stream().filter(map->map.get(MagicString.TAI_CD).toString().equals(basicAllPts.getTaiCd()+"")&&
                        map.get(MagicString.TANA_CD).toString().equals(basicAllPts.getTanaCd()+""))
                .sorted(Comparator.comparing(map -> MapUtils.getInteger(map,MagicString.RANK,0)))
                .collect(Collectors.toList());
        List<Map<String, Object>> attrCol = attrSortMapper.getAttrColForNameForFinal(companyCd, priorityOrderCd, commonTableName.getProdIsCore(),commonTableName.getProdMstClass());
        Map<String,Object> mapHeader = new HashMap<>();
        StringBuilder groupColumns = new StringBuilder("janCd,janName");
        StringBuilder groupHeader = new StringBuilder("JAN,商品名");
        for (Map<String, Object> map : attrCol) {
            groupColumns.append(",").append(map.get(MagicString.ZOUKUSEI_COLCD));
            groupHeader.append(",").append(map.get("zokusei_nm"));
        }
        groupColumns.append(",plano_width,plano_height,plano_depth,rank,faceNum");
        groupHeader.append(",幅,高,奥行,RANK,フェース数");
        mapHeader.put("groupColumns", groupColumns.toString());
        mapHeader.put("groupHeader", groupHeader.toString());
        for (Map<String, Object> map : ptsGroup) {
            for (Map.Entry<String, Object> stringObjectEntry : map.entrySet()) {
                for (Map<String, Object> objectMap : attrCol) {
                    if (objectMap.get("zokusei_colname").equals(stringObjectEntry.getKey())){
                        map.put(objectMap.get("zokusei_colcd").toString(),stringObjectEntry.getValue());
                    }
                }
            }
        }
        mapHeader.put("newData",ptsGroup);
        Integer ptsCd = shelfPtsDataMapper.getPtsCd(patternCd);
        List<Map<String, Object>> ptsOldGroup = this.getBasicAllOldPtsGroup(companyCd, priorityOrderCd,ptsCd);
        ptsOldGroup = this.oldGroupHandle(ptsOldGroup, basicAllPts, attrCol);
        mapHeader.put("oldData",ptsOldGroup);
        return ResultMaps.result(ResultEnum.SUCCESS,mapHeader);
    }

    public List<Map<String, Object>>  oldGroupHandle(List<Map<String, Object>>  ptsOldGroup,BasicAllPts basicAllPts,List<Map<String, Object>> attrCol){
        ptsOldGroup= ptsOldGroup.stream().filter(map->map.get(MagicString.TAI_CD).toString().equals(basicAllPts.getTaiCd()+"")&&
                        map.get(MagicString.TANA_CD).toString().equals(basicAllPts.getTanaCd()+""))
                .sorted(Comparator.comparing(map -> MapUtils.getInteger(map,MagicString.RANK,0)))
                .collect(Collectors.toList());
        for (Map<String, Object> map : ptsOldGroup) {
            for (Map.Entry<String, Object> stringObjectEntry : map.entrySet()) {
                for (Map<String, Object> objectMap : attrCol) {
                    if (objectMap.get("zokusei_colname").equals(stringObjectEntry.getKey())){
                        map.put(objectMap.get("zokusei_colcd").toString(),stringObjectEntry.getValue());
                    }
                }
            }
        }
        return ptsOldGroup;
    }



    private List<Map<String, Object>> getBasicAllPtsGroup(String companyCd, Integer priorityOrderCd,Integer priorityAllCd,Integer patternCd) {
        List<PriorityOrderMstAttrSort> mstAttrSorts = attrSortMapper.selectByPrimaryKeyForFinal(companyCd, priorityOrderCd);
        List<Integer> attrList = mstAttrSorts.stream().map(vo->Integer.parseInt(vo.getValue())).collect(Collectors.toList());
        WorkPriorityOrderMstEditVo priorityOrderMst = workPriorityOrderMstMapper.getPriorityOrderMst(companyCd, priorityOrderCd);
        String commonPartsData = priorityOrderMst.getCommonPartsData();
        GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(commonPartsData, companyCd);
        List<ZokuseiMst> zokuseiMsts = zokuseiMapper.selectZokusei(commonTableName.getProdIsCore(), commonTableName.getProdMstClass(), Joiner.on(",").join(attrList));
        List<Integer> allCdList = zokuseiMapper.selectCdHeader(commonTableName.getProKaisouTable());
        Integer id = priorityAllPtsMapper.getNewId(companyCd, priorityAllCd,patternCd);
        List<Map<String, Object>> attrCol = attrSortMapper.getAttrColForNameForFinal(companyCd, priorityOrderCd, commonTableName.getProdIsCore(),commonTableName.getProdMstClass());
        List<Map<String, Object>> janSizeCol = zokuseiMstMapper.getJanSizeCol(commonTableName.getProAttrTable());
        return priorityAllPtsMapper.selectNewJanZokusei(priorityOrderCd, id, zokuseiMsts, allCdList, commonTableName.getProInfoTable(),attrCol,janSizeCol);
    }

    private List<Map<String, Object>> getBasicAllOldPtsGroup(String companyCd, Integer priorityOrderCd,Integer ptsCd) {
        List<PriorityOrderMstAttrSort> mstAttrSorts = attrSortMapper.selectByPrimaryKeyForFinal(companyCd, priorityOrderCd);
        List<Integer> attrList = mstAttrSorts.stream().map(vo->Integer.parseInt(vo.getValue())).collect(Collectors.toList());
        WorkPriorityOrderMstEditVo priorityOrderMst = workPriorityOrderMstMapper.getPriorityOrderMst(companyCd, priorityOrderCd);
        String commonPartsData = priorityOrderMst.getCommonPartsData();
        GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(commonPartsData, companyCd);
        List<ZokuseiMst> zokuseiMsts = zokuseiMapper.selectZokusei(commonTableName.getProdIsCore(), commonTableName.getProdMstClass(), Joiner.on(",").join(attrList));
        List<Integer> allCdList = zokuseiMapper.selectCdHeader(commonTableName.getProKaisouTable());
        List<Map<String, Object>> attrCol = attrSortMapper.getAttrColForNameForFinal(companyCd, priorityOrderCd, commonTableName.getProdIsCore(),commonTableName.getProdMstClass());
        List<Map<String, Object>> janSizeCol = zokuseiMstMapper.getJanSizeCol(commonTableName.getProAttrTable());
        return basicPatternRestrictResultMapper.selectOldJanZokuseiForFinal(priorityOrderCd,ptsCd , zokuseiMsts, allCdList,
                commonTableName.getProInfoTable(),attrCol,janSizeCol,priorityOrderMst.getProductPowerCd());
    }
    private void writeZip(String filePath, ZipOutputStream zos, String fileName){
        try(FileInputStream fis = new FileInputStream(filePath)) {
            zos.putNextEntry(new ZipEntry(fileName));
            byte[] bom = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
            zos.write(bom);

            byte[] bytes = new byte[1024];
            int len;
            while ((len = fis.read(bytes))!=-1){
                zos.write(bytes, 0, len);
            }
            zos.closeEntry();
        } catch (IOException e) {
            logger.error("写zip文件失敗", e);
        }
    }

    private void deleteDir(File dir) {
        try {
            if (dir.isDirectory()) {
                String[] children = dir.list();
                //ディレクトリ内のサブディレクトリを再帰的に削除
                for (int i=0; i<children.length; i++) {
                    Files.deleteIfExists(new File(dir, children[i]).getAbsoluteFile().toPath());
                }
            }
            // ディレクトリが空です，削除可能
            Files.deleteIfExists(dir.getAbsoluteFile().toPath());
        } catch (IOException e) {
            logger.error("", e);
        }
    }

    /**
     * ptsをファイルに生成
     * @param shelfPtsDataVersion
     * @param shelfPtsDataTaimst
     * @param shelfPtsDataTanamst
     * @param shelfPtsDataJandata
     * @return csvファイルのパスを返す
     */
    public String generateCsv2File(ShelfPtsDataVersion shelfPtsDataVersion, List<ShelfPtsDataTaimst> shelfPtsDataTaimst,
                            List<ShelfPtsDataTanamst> shelfPtsDataTanamst, List<ShelfPtsDataJandata> shelfPtsDataJandata,
                                   String fileParentPath, String fileName) {
        String filePath = Joiner.on(File.separator).join(Lists.newArrayList(fileParentPath, fileName));
        logger.info("file path: {}", fileParentPath);

        try(FileWriter fileWriter = new FileWriter(filePath);
            CsvWriter csvWriter = CsvWriter.builder().build(fileWriter)){
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
            for (ShelfPtsDataJandata ptsDataJandata : shelfPtsDataJandata) {
                List<String> janData = Lists.newArrayList(ptsDataJandata.getTaiCd() + "",
                        ptsDataJandata.getTanaCd() + "", ptsDataJandata.getTanapositionCd() + "", ptsDataJandata.getJan() + "",
                        ptsDataJandata.getFaceCount() + "", ptsDataJandata.getFaceMen() + "", ptsDataJandata.getFaceKaiten() + "",
                        ptsDataJandata.getTumiagesu() + "",
                        Optional.ofNullable(ptsDataJandata.getZaikosu()).orElse(0) + "", Optional.ofNullable(ptsDataJandata.getFaceDisplayflg()).orElse(0) + "",
                        Optional.ofNullable(ptsDataJandata.getFacePosition()).orElse(0) + "", Optional.ofNullable(ptsDataJandata.getDepthDisplayNum()).orElse(0) + "");
                csvWriter.writeRow(janData.subList(0, janHeaders.length));
            }

        }catch (IOException e) {
            logger.error("csv writer 閉じる異常", e);
        }

        return filePath;
    }
}
