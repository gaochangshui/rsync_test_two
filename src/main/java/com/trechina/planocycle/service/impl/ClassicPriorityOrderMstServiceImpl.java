package com.trechina.planocycle.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.trechina.planocycle.aspect.LogAspect;
import com.trechina.planocycle.constant.MagicString;
import com.trechina.planocycle.entity.dto.*;
import com.trechina.planocycle.entity.po.*;
import com.trechina.planocycle.entity.vo.PriorityOrderCatePakVO;
import com.trechina.planocycle.entity.vo.PriorityOrderPrimaryKeyVO;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.*;
import com.trechina.planocycle.service.*;
import com.trechina.planocycle.utils.*;
import de.siegmar.fastcsv.writer.CsvWriter;
import org.apache.commons.collections4.MapUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ClassicPriorityOrderMstServiceImpl implements ClassicPriorityOrderMstService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private HttpSession session;
    @Autowired
    private ClassicPriorityOrderMstMapper priorityOrderMstMapper;
    @Autowired
    private ClassicPriorityOrderPatternMapper priorityOrderPatternMapper;
    @Autowired
    private CommodityScoreMasterService commodityScoreMasterService;
    @Autowired
    private ShelfPatternService shelfPatternService;
    @Autowired
    private ClassicPriorityOrderMstAttrSortService priorityOrderMstAttrSortService;
    @Autowired
    private ClassicPriorityOrderJanReplaceService priorityOrderJanReplaceService;
    @Autowired
    private ClassicPriorityOrderJanNewService priorityOrderJanNewService;
    @Autowired
    private ClassicPriorityOrderJanCardService priorityOrderJanCardService;
    @Autowired
    private ClassicPriorityOrderCatePakService priorityOrderCatePakService;
    @Autowired
    private ClassicPriorityOrderJanProposalService priorityOrderJanProposalService;
    @Autowired
    private ClassicPriorityOrderBranchNumService priorityOrderBranchNumService;
    @Autowired
    private ClassicPriorityOrderJanAttributeMapper priorityOrderJanAttributeMapper;
    @Autowired
    private ClassicPriorityOrderJanNewMapper priorityOrderJanNewMapper;
    @Autowired
    private ClassicPriorityOrderJanCardMapper priorityOrderJanCardMapper;
    @Autowired
    private ClassicPriorityOrderJanProposalMapper priorityOrderJanProposalMapper;
    @Autowired
    private PriorityOrderCatepakMapper priorityOrderCatepakMapper;
    @Autowired
    private ClassicPriorityOrderCatepakAttributeMapper priorityOrderCatepakAttributeMapper;
    @Autowired
    private ClassicPriorityOrderCommodityMustMapper priorityOrderCommodityMustMapper;
    @Autowired
    private ClassicPriorityOrderCommodityNotMapper priorityOrderCommodityNotMapper;
    @Autowired
    private ClassicPriorityOrderDataMapper priorityOrderDataMapper;
    @Autowired
    private ClassicPriorityOrderResultDataMapper priorityOrderResultDataMapper;
    @Autowired
    private ClassicPriorityOrderMstService priorityOrderMstService;
    @Autowired
    private ClaasicPriorityOrderAttributeClassifyMapper priorityOrderAttributeClassifyMapper;
    @Autowired
    private PriorityOrderMstAttrSortMapper priorityOrderMstAttrSortMapper;
    @Autowired
    private ClassicPriorityOrderMstAttrSortMapper classicPriorityOrderMstAttrSortMapper;
    @Autowired
    private cgiUtils cgiUtil;
    @Autowired
    private CacheUtil cacheUtil;
    @Autowired
    private ClassicPriorityOrderPatternMapper patternMapper;
    @Autowired
    private ShelfPatternBranchMapper shelfPatternBranchMapper;
    @Autowired
    private PriorityOrderJanReplaceMapper janReplaceMapper;
    @Autowired
    private ClassicPriorityOrderMstAttrSortMapper mstAttrSortMapper;
    @Autowired
    private ShelfPtsDataMapper shelfPtsDataMapper;
    @Autowired
    private WorkPriorityOrderPtsClassifyMapper priorityOrderPtsClassifyMapper;
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;
    @Autowired
    private ShelfPatternMstMapper patternMstMapper;
//    @Autowired
//    private priority_order_pts_pattern_name
    @Autowired
    private BasicPatternMstService basicPatternMstService;
    @Autowired
    private WorkPriorityOrderPtsClassifyMapper workPriorityOrderPtsClassify;
    @Autowired
    private JansMapper jansMapper;
    @Autowired
    private StarReadingTableMapper starReadingTableMapper;
    @Autowired
    private ProductPowerMstMapper productPowerMstMapper;
    @Autowired
    private LogAspect logAspect;
    @Autowired
    private PriorityOrderPtsBackupJanMapper ptsBackupJanMapper;
    @Autowired
    private PriorityOrderPtsPatternNameMapper ptsPatternNameMapper;

    /**
     * 優先順位テーブルlistの取得
     *
     * @param companyCd
     * @return
     */
    @Override
    public Map<String, Object> getPriorityOrderList(String companyCd) {
        logger.info("優先順位テーブルパラメータの取得：{}",companyCd);
        List<PriorityOrderMst> priorityOrderMstList = priorityOrderMstMapper.selectByPrimaryKey(companyCd);
        logger.info("優先順位テーブル戻り値の取得：{}",priorityOrderMstList);
        return ResultMaps.result(ResultEnum.SUCCESS,priorityOrderMstList);
    }

    /**
     * 優先順位テーブルパラメータの保存
     *
     * @param priorityOrderMstDto
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> setPriorityOrderMst(PriorityOrderMstDto priorityOrderMstDto) {
        logger.info("優先順位テーブルパラメータの保存{}",priorityOrderMstDto);
        String authorCd = session.getAttribute("aud").toString();
        // チェック優先順位テーブル名
        Integer count = priorityOrderPatternMapper.selectByPriorityOrderName(priorityOrderMstDto.getCompanyCd(),
                                                            priorityOrderMstDto.getPriorityOrderName(),
                                                            priorityOrderMstDto.getPriorityOrderCd());
        if (count >0) {
            return ResultMaps.result(ResultEnum.NAMEISEXISTS);
        }
        Integer priorityOrderCd = priorityOrderMstDto.getPriorityOrderCd();
        String companyCd = priorityOrderMstDto.getCompanyCd();
        //パラメータを2つのテーブルのデータに処理するinsert
        priorityOrderMstDto.setSetSpecialFlag(1);
        priorityOrderMstService.setWorkPriorityOrderMst(priorityOrderMstDto);
            logger.info("優先順位テーブルパラメータの保存：{}",priorityOrderMstDto);
            priorityOrderMstMapper.deleteforid(priorityOrderMstDto.getPriorityOrderCd());
            priorityOrderMstMapper.insert(priorityOrderMstDto,authorCd);
            //jannew最終テーブルデータの保存
            priorityOrderJanNewMapper.deleteFinal(priorityOrderMstDto.getCompanyCd(),priorityOrderMstDto.getPriorityOrderCd());
            priorityOrderJanNewMapper.setFinalForWork(priorityOrderMstDto.getCompanyCd(),priorityOrderMstDto.getPriorityOrderCd());
            //janAttribute最終テーブルデータの保存
            priorityOrderJanAttributeMapper.deleteFinal(priorityOrderMstDto.getCompanyCd(),priorityOrderMstDto.getPriorityOrderCd());
            priorityOrderJanAttributeMapper.setFinalForWork(priorityOrderMstDto.getCompanyCd(),priorityOrderMstDto.getPriorityOrderCd());
            //jancut 最終テーブルデータの保存
            priorityOrderJanCardMapper.deleteFinal(priorityOrderMstDto.getCompanyCd(),priorityOrderMstDto.getPriorityOrderCd());
            priorityOrderJanCardMapper.setFinalForWork(priorityOrderMstDto.getCompanyCd(),priorityOrderMstDto.getPriorityOrderCd());
            //janProposal 最終テーブルデータの保存
            priorityOrderJanProposalMapper.deleteFinalByPrimaryKey(priorityOrderMstDto.getCompanyCd(),priorityOrderMstDto.getPriorityOrderCd());
            priorityOrderJanProposalMapper.insertFinalData(priorityOrderMstDto.getCompanyCd(),priorityOrderMstDto.getPriorityOrderCd());
            //CatepakAttribute 最終テーブルデータの保存
            priorityOrderCatepakAttributeMapper.deleteFinalByPrimaryKey(priorityOrderMstDto.getCompanyCd(),priorityOrderMstDto.getPriorityOrderCd());
            priorityOrderCatepakAttributeMapper.insertFinalData(priorityOrderMstDto.getCompanyCd(),priorityOrderMstDto.getPriorityOrderCd());
            //Catepak 最終テーブルデータの保存
            priorityOrderCatepakMapper.deleteFinalByPrimaryKey(priorityOrderMstDto.getCompanyCd(),priorityOrderMstDto.getPriorityOrderCd());
            priorityOrderCatepakMapper.insertFinalData(priorityOrderMstDto.getCompanyCd(),priorityOrderMstDto.getPriorityOrderCd());

            //must 最終テーブルデータの保存
            priorityOrderCommodityMustMapper.deleteFinal(priorityOrderMstDto.getCompanyCd(),priorityOrderMstDto.getPriorityOrderCd());
            priorityOrderCommodityMustMapper.setFinalForWork(priorityOrderMstDto.getCompanyCd(),priorityOrderMstDto.getPriorityOrderCd());
            //not 最終テーブルデータの保存
            priorityOrderCommodityNotMapper.deleteFinal(priorityOrderMstDto.getCompanyCd(),priorityOrderMstDto.getPriorityOrderCd());
            priorityOrderCommodityNotMapper.setFinalForWork(priorityOrderMstDto.getCompanyCd(),priorityOrderMstDto.getPriorityOrderCd());
            //classify 最終テーブルデータの保存
            priorityOrderAttributeClassifyMapper.deleteFinal(priorityOrderMstDto.getPriorityOrderCd());
            priorityOrderAttributeClassifyMapper.insertFinal(priorityOrderMstDto.getCompanyCd(),priorityOrderMstDto.getPriorityOrderCd());

            //save resultData
            priorityOrderResultDataMapper.deleteFinal(priorityOrderMstDto.getCompanyCd(),priorityOrderMstDto.getPriorityOrderCd());
            priorityOrderResultDataMapper.setFinalForWork(priorityOrderMstDto.getCompanyCd(),priorityOrderMstDto.getPriorityOrderCd(),authorCd);
            //save 屬性attr
            priorityOrderMstAttrSortMapper.deleteAttrFinal(priorityOrderMstDto.getCompanyCd(),priorityOrderMstDto.getPriorityOrderCd());
            priorityOrderMstAttrSortMapper.insertAttrFinal(priorityOrderMstDto.getCompanyCd(),priorityOrderMstDto.getPriorityOrderCd());
            // save sort
            priorityOrderMstAttrSortMapper.deleteAttrSortFinal(priorityOrderMstDto.getCompanyCd(),priorityOrderMstDto.getPriorityOrderCd());
            priorityOrderMstAttrSortMapper.insertAttrSortFinal(priorityOrderMstDto.getCompanyCd(),priorityOrderMstDto.getPriorityOrderCd());
            // save group
            priorityOrderPtsClassifyMapper.deleteFinal(companyCd,priorityOrderCd);
            priorityOrderPtsClassifyMapper.setFinalForWork(companyCd,priorityOrderCd);
            //save 星取表 branch
            starReadingTableMapper.deleteFinalByBranch(companyCd,priorityOrderCd);
            starReadingTableMapper.setFinalForWorkByBranch(companyCd,priorityOrderCd);
            //save 星取表 pattern
            starReadingTableMapper.deleteFinalByPattern(companyCd,priorityOrderCd);
            starReadingTableMapper.setFinalForWorkByPattern(companyCd,priorityOrderCd);

            priorityOrderPatternMapper.deleteforid(priorityOrderMstDto.getPriorityOrderCd());
            priorityOrderPatternMapper.insertForFinal(priorityOrderCd,companyCd);
            return ResultMaps.result(ResultEnum.SUCCESS);
    }




    /**
     * この企業に優先順位テーブルがあるかどうかのログインを取得します。
     *
     * @return
     */
    @Override
    public Map<String, Object> getPriorityOrderExistsFlg() {
        List<String> companyCd  = Arrays.asList(session.getAttribute("inCharge").toString().split(","));
        int result = priorityOrderMstMapper.selectPriorityOrderCount(companyCd);
        return ResultMaps.result(ResultEnum.SUCCESS,result);
    }

    /**
     * 優先順位テーブルrank属性の動的列の取得
     *
     * @param companyCd
     * @param productPowerCd
     * @return
     */
    @Override
    public Map<String, Object> getRankAttr(String companyCd, Integer productPowerCd) {
        logger.info("優先順位テーブルrank属性の動的列の取得：{},{}",companyCd,productPowerCd);
        Map<String,Object> result = new HashMap<>();
        commodityScoreMasterService.productPowerParamAttrName(companyCd, productPowerCd, result);
        return ResultMaps.result(ResultEnum.SUCCESS,result);
    }


    /**
     * 優先順位テーブルcdに基づく商品力点数テーブルcdの取得
     *
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Map<String, Object> getProductPowerCdForPriority(Integer priorityOrderCd) {
        logger.info("優先順位テーブルcdに基づいて商品力点数テーブルcdのパラメータを取得する,{}",priorityOrderCd);
        Map<String,Object> productPowerCd = priorityOrderMstMapper.selectProductPowerCd(priorityOrderCd);
        logger.info("優先順位テーブルcdから商品力点数テーブルcdの戻り値を取得する,{}",priorityOrderCd);
        return ResultMaps.result(ResultEnum.SUCCESS,productPowerCd);
    }

    /**
     * すべての優先順位テーブル情報を削除
     *
     * @param primaryKeyVO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> delPriorityOrderAllInfo(PriorityOrderPrimaryKeyVO primaryKeyVO) {
        String companyCd= primaryKeyVO.getCompanyCd();
        Integer priorityOrderCd = primaryKeyVO.getPriorityOrderCd();
        // マスターテーブルの削除
        delPriorityOrderMst(primaryKeyVO);
        priorityOrderResultDataMapper.deleteFinal(companyCd,priorityOrderCd);
        priorityOrderJanReplaceService.delJanReplaceInfo(companyCd,priorityOrderCd);
        priorityOrderJanNewService.delriorityOrderJanNewInfo(companyCd,priorityOrderCd);
        priorityOrderJanCardService.delPriorityOrderJanCardInfo(companyCd,priorityOrderCd);
        // 削除catepakかくだい
        priorityOrderCatePakService.delPriorityOrderCatePakInfo(companyCd,priorityOrderCd);
        priorityOrderCatePakService.delPriorityOrderCatePakAttrInfo(companyCd,priorityOrderCd);
        // jan変提案listを削除する
        priorityOrderJanProposalService.delPriorityOrderJanProposalInfo(companyCd,priorityOrderCd);
        // 必須テーブルと不可テーブルの削除
        priorityOrderBranchNumService.delPriorityOrderCommodityMustInfo(companyCd,priorityOrderCd);
        priorityOrderBranchNumService.delPriorityOrderCommodityNotInfo(companyCd,priorityOrderCd);
        priorityOrderBranchNumService.delPriorityOrderBranchNumInfo(companyCd,priorityOrderCd);
        // 削除データのソート
        priorityOrderMstAttrSortService.delPriorityAttrSortInfo(companyCd,priorityOrderCd);
        //attrを削除
        priorityOrderMstAttrSortMapper.deleteAttrSortFinal(companyCd,priorityOrderCd);
        // jannewの属性列を削除
        priorityOrderJanAttributeMapper.deleteByPrimaryKey(companyCd,priorityOrderCd);
        // 棚pattern関連情報の削除
        priorityOrderPatternMapper.deleteforid(priorityOrderCd);

        return ResultMaps.result(ResultEnum.SUCCESS);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> setWorkPriorityOrderMst(PriorityOrderMstDto priorityOrderMstDto) {

        String authorCd = session.getAttribute("aud").toString();
        String companyCd = priorityOrderMstDto.getCompanyCd();
        Integer priorityOrderCd = priorityOrderMstDto.getPriorityOrderCd();
        String priorityData = priorityOrderMstDto.getPriorityData();
        List<GoodsRankDto> goodsRank = priorityOrderDataMapper.getGoodsRank(companyCd,priorityOrderCd);
        JSONArray datas = JSON.parseArray(priorityData);
        List<Map<String, String>> keyNameList = new ArrayList<>();
        colNameList(datas, keyNameList);
        List<Map> delJanList = datas.toJavaList(Map.class).stream().filter(item -> item.get(MagicString.RANK_UPD).equals(99999999)).collect(Collectors.toList());
        priorityOrderJanCardMapper.deleteByPrimaryKey(companyCd, priorityOrderCd);
        if (!delJanList.isEmpty()) {
            priorityOrderJanCardMapper.setDelJanList(delJanList, companyCd, priorityOrderCd);
        }

        priorityOrderDataMapper.deleteWorkData(companyCd,priorityOrderCd);
        List<Map<String, Object>> linkedHashMaps = new Gson().fromJson(datas.toString(), new TypeToken<List<LinkedHashMap<String, Object>>>() {
        }.getType());
        for (Map<String, Object> linkedHashMap : linkedHashMaps) {
            linkedHashMap.remove("priority_order_cd");
            linkedHashMap.remove("company_cd");
            linkedHashMap.remove("author_cd");
            linkedHashMap.remove("repeatFlg");
        }
        priorityOrderDataMapper.insertWorkData(companyCd,priorityOrderCd,linkedHashMaps,authorCd);
        if (!goodsRank.isEmpty()) {
            priorityOrderDataMapper.updateGoodsRank(goodsRank, companyCd, priorityOrderCd);
        }
        if (priorityOrderMstDto.getSetSpecialFlag() != null) {
            List<Map<String, Object>> attrSpecialList = classicPriorityOrderMstAttrSortMapper.getAttrSpecialList(companyCd, priorityOrderCd);
            if (!attrSpecialList.isEmpty()){
                HashMap<String, Object> objectObjectHashMap = new HashMap<>();
                objectObjectHashMap.put("value","1");
                objectObjectHashMap.put("sort",MagicString.JAN_NEW);
                attrSpecialList.add(objectObjectHashMap);
                priorityOrderDataMapper.setSpecialName(linkedHashMaps,attrSpecialList);

            }
        }
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    public void colNameList(JSONArray datas, List<Map<String, String>> keyNameList) {
        boolean isGoodsExist = false;
        for (int i = 0; i < ((JSONObject) datas.get(0)).keySet().toArray().length; i++) {
            Map<String, String> maps = new HashMap<>();
            maps.put("name", (String) ((JSONObject) datas.get(0)).keySet().toArray()[i]);
            String col =(String) ((JSONObject) datas.get(0)).keySet().toArray()[i];
            if (col.equals("goods_rank")){
                isGoodsExist = true;
            }
            keyNameList.add(maps);
        }
        if (!isGoodsExist){
            Map<String, String> maps = new HashMap<>();
            maps.put("name","goods_rank");
            keyNameList.add(maps);
        }
    }


    /**
     * 優先順位テーブルマスタテーブル情報削除
     * @param primaryKeyVO
     * @return
     */
    private Integer delPriorityOrderMst(PriorityOrderPrimaryKeyVO primaryKeyVO){
        return priorityOrderMstMapper.deleteByPrimaryKey(primaryKeyVO.getCompanyCd(),primaryKeyVO.getPriorityOrderCd());
    }

    @Override
    public Map<String, Object> downloadPts(String taskId, String companyCd, Integer priorityOrderCd, Integer newCutFlg, Integer ptsVersion, HttpServletResponse response) throws IOException {
        String path = this.getClass().getClassLoader().getResource("").getPath();
        logger.info("parent path: {}", path);

        cacheUtil.put(taskId, "1");

        Integer modeCheck = priorityOrderMstMapper.selectModeCheck(priorityOrderCd);

        long currentTimeMillis = System.currentTimeMillis();
        String fileParentPath = Joiner.on(File.separator).join(Lists.newArrayList(path, currentTimeMillis));
        File file = new File(fileParentPath);
        if (!file.exists()) {
            boolean isMkdir = file.mkdirs();
            logger.info("mkdir:{}",isMkdir);
        }

        String zipFileName = "";
        if(ptsVersion==1){
            zipFileName = MessageFormat.format("提案棚割PTS_{0}.zip", LocalDateTime.now().format(DateTimeFormatter.ofPattern(MagicString.DATE_FORMATER)));
        }else{
            zipFileName = MessageFormat.format("確認用棚割PTS_{0}.zip", LocalDateTime.now().format(DateTimeFormatter.ofPattern(MagicString.DATE_FORMATER)));
        }

        try {
            ptsBackupJanMapper.deleteBackupJanByCd(priorityOrderCd);
            ptsPatternNameMapper.deletePtsPatternNameByCd(priorityOrderCd);

            List<Map<String, Object>> patternCommonPartsData = patternMstMapper.selectPatternCommonPartsData(priorityOrderCd);

            //pts foreach
            List<ShelfPtsDataDto> patternList = patternMapper.selectPattern(companyCd, priorityOrderCd);
            List<Map<String, Object>> branchs = new ArrayList<>();
            List<Map<String, String>> janReplace = janReplaceMapper.selectJanReplace(companyCd, priorityOrderCd);
            Map<String, String> janReplaceMap = janReplace.stream().collect(Collectors.toMap(map->MapUtils.getString(map, MagicString.JAN_OLD), map->MapUtils.getString(map, MagicString.JAN_NEW)));
            List<Map<String, Object>> allNewJanList = new ArrayList<>();
            List<Map<String, Object>> allDeleteJanList = new ArrayList<>();

            List<PriorityOrderMstAttrSort> rankAttr = mstAttrSortMapper.selectByPrimaryKey(companyCd, priorityOrderCd);
            rankAttr.sort(Comparator.comparing(PriorityOrderMstAttrSort::getValue));
            List<String> rankAttrList = rankAttr.stream().map(PriorityOrderMstAttrSort::getValue).collect(Collectors.toList());

            Map<String, String> tenTableName = null;

            List<Integer> transferRankAttr = rankAttr.stream().map(rank->Integer.parseInt(rank.getValue().replace("attr",""))).collect(Collectors.toList());
            List<PriorityOrderCatePakVO> catePakList = priorityOrderCatepakAttributeMapper.selectFinalByPrimaryKey(transferRankAttr, companyCd, priorityOrderCd);

            for (ShelfPtsDataDto pattern : patternList) {
                boolean branchMustNot = true;
                tenTableName = new HashMap<>();

                for (Map<String, Object> data : patternCommonPartsData) {
                    String commonPartsData = MapUtils.getString(data, "common_parts_data");
                    GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(commonPartsData, companyCd);
                    tenTableName.put(commonTableName.getStoreInfoTable(), commonTableName.getStoreIsCore());
                }

                Integer shelfPatternCd = pattern.getShelfPatternCd();
                List<Map<String, Object>> patternBranches = shelfPatternBranchMapper.selectAllPatternBranch(priorityOrderCd, companyCd, tenTableName, shelfPatternCd);
                branchs.addAll(patternBranches);

                List<String> patternBranchCd = patternBranches.stream().map(map->map.get(MagicString.BRANCH).toString()).collect(Collectors.toList());
                List<Map<String, Object>> commodityMustJans = null;
                List<Map<String, Object>> commodityNotJans = null;
                List<Map<String, Object>> janMustNot = null;

                if(Objects.equals(modeCheck, 1)){
                    //branch
                    janMustNot = starReadingTableMapper.selectJanForBranch(companyCd, priorityOrderCd, Joiner.on(",").join(patternBranchCd));
                }else{
                    //pattern
                    janMustNot = starReadingTableMapper.selectJanForPattern(companyCd, priorityOrderCd, shelfPatternCd);
                }

                commodityMustJans = janMustNot.stream().filter(map->MapUtils.getInteger(map,"exist_flag").equals(MagicString.START_READING_STATUS_MUST)).collect(Collectors.toList());
                commodityNotJans = janMustNot.stream().filter(map->MapUtils.getInteger(map,"exist_flag").equals(MagicString.START_READING_STATUS_NOT)).collect(Collectors.toList());

                if(commodityMustJans.isEmpty() && commodityNotJans.isEmpty()){
                    branchMustNot = false;
                    logger.info("patternCd:{} no exist commodity must and commodity not", shelfPatternCd);
                }

                Integer ptsCd = pattern.getId();
                ShelfPtsHeaderDto shelfPtsHeaderDto = shelfPtsDataMapper.selectShelfPts(shelfPatternCd);

                List<Map<String, Object>> ptsSkuNum = priorityOrderPtsClassifyMapper.getPtsSkuNum(companyCd, priorityOrderCd, ptsCd, rankAttrList);
                List<Map<String, Object>> ptsJanDtoList = shelfPtsDataMapper.selectClassifyPtsData(rankAttrList, shelfPatternCd, priorityOrderCd);
                Map<String, List<Map<String, Object>>> ptsJanDtoListByGroup = ptsJanDtoList.stream()
                        .collect(Collectors.groupingBy(s -> s.get(MagicString.ATTR_LIST).toString(), LinkedHashMap::new, Collectors.toList()));
                List<Map<String, Object>> resultDataList = priorityOrderResultDataMapper.selectFinalDataByAttr(priorityOrderCd, companyCd, rankAttrList);
                List<CompletableFuture<Map<String, List<Map<String, Object>>>>> tasks = new ArrayList<>();

                List<String> allBranchList = shelfPatternBranchMapper.selectByPrimaryKey(shelfPatternCd)
                        .stream().map(shelfPattern->shelfPattern.getBranch().contains("_")?shelfPattern.getBranch().split("_")[1]:shelfPattern.getBranch()).collect(Collectors.toList());
                boolean isAllForMustNot = true;
                Set<String> mustNotBranch = new HashSet<>();

                if(Objects.equals(modeCheck, 1)){
                    //if all branch
                    List<String> branchList = starReadingTableMapper.selectBranchMustNotJan(companyCd, priorityOrderCd);
                    long count = Sets.intersection(Sets.newHashSet(allBranchList), Sets.newHashSet(branchList)).stream().count();
                    isAllForMustNot = Objects.equals(count,branchList.size());

                    List<String> mustBranch = commodityMustJans.stream().map(map->map.get(MagicString.BRANCH).toString()).collect(Collectors.toList());
                    List<String> notBranch = commodityNotJans.stream().map(map->map.get(MagicString.BRANCH).toString()).collect(Collectors.toList());

                    mustNotBranch.addAll(mustBranch);
                    mustNotBranch.addAll(notBranch);
                }

                //must and not only one branch, download a pts csv
                if((mustNotBranch.size()!=1 && !isAllForMustNot) || !branchMustNot){
                    String json = new Gson().toJson(ptsJanDtoListByGroup);
                    Map<String, List<Map<String, Object>>> finalPtsJanDtoListByGroup = new Gson().fromJson(json,
                            new TypeToken<Map<String, List<Map<String, Object>>>>(){}.getType());
                    //common compare
                    CompletableFuture<Map<String, List<Map<String, Object>>>> commonFuture = CompletableFuture.supplyAsync(() -> doNewOldPtsCompare(finalPtsJanDtoListByGroup, resultDataList, ptsSkuNum, pattern,
                            shelfPtsHeaderDto, ptsVersion, catePakList, companyCd, fileParentPath,
                            null, null, priorityOrderCd, null, janReplaceMap, ptsJanDtoList, patternBranchCd));
                    tasks.add(commonFuture);
                }

                if(branchMustNot || Objects.equals(0, modeCheck)){
                    //commodity_must+commodity_not
                    boolean finalIsAllForMustNot = isAllForMustNot;
                    List<Map<String, Object>> finalCommodityMustJans = commodityMustJans;
                    List<Map<String, Object>> finalCommodityNotJans = commodityNotJans;

                    CompletableFuture<Map<String, List<Map<String, Object>>>> mustNotFuture = CompletableFuture.supplyAsync(() -> {
                        Map<String, List<Map<String, Object>>> resultMap = new ConcurrentHashMap<>();
                        if(finalIsAllForMustNot){
                            String json = new Gson().toJson(ptsJanDtoListByGroup);
                            Map<String, List<Map<String, Object>>> finalPtsJanDtoListByGroup = new Gson().fromJson(json,
                                    new TypeToken<Map<String, List<Map<String, Object>>>>(){}.getType());
                            Map<String, List<Map<String, Object>>> tmpResultMap = doNewOldPtsCompare(finalPtsJanDtoListByGroup, resultDataList, ptsSkuNum, pattern,
                                    shelfPtsHeaderDto, ptsVersion, catePakList, companyCd, fileParentPath,
                                    Maps.newHashMap(), finalCommodityMustJans, priorityOrderCd, finalCommodityNotJans, janReplaceMap, ptsJanDtoList,patternBranchCd);

                            resultMap.put(MagicString.DELETE_LIST, tmpResultMap.getOrDefault(MagicString.DELETE_LIST, Lists.newArrayList()));
                            resultMap.put(MagicString.NEW_LIST, tmpResultMap.getOrDefault(MagicString.NEW_LIST, Lists.newArrayList()));
                        }else{
                            //must not != all branch
                            for (Map<String, Object> branch : patternBranches) {
                                String json = new Gson().toJson(ptsJanDtoListByGroup);
                                Map<String, List<Map<String, Object>>> finalPtsJanDtoListByGroup = new Gson().fromJson(json,
                                        new TypeToken<Map<String, List<Map<String, Object>>>>(){}.getType());
                                String branchCd = branch.get(MagicString.BRANCH).toString();
                                List<Map<String, Object>> commodityMustBranchJans = finalCommodityMustJans.stream().filter(m -> m.get(MagicString.BRANCH).toString().equals(branchCd)).collect(Collectors.toList());
                                List<Map<String, Object>> commodityNotBranchJans = finalCommodityNotJans.stream().filter(m -> m.get(MagicString.BRANCH).toString().equals(branchCd)).collect(Collectors.toList());

                                if(commodityMustBranchJans.isEmpty() && commodityNotBranchJans.isEmpty()){
                                    logger.info("patternCd: {},branchCd:{} no commodityMust and commodityNot", pattern.getId(), branchCd);
                                    continue;
                                }

                                Map<String, List<Map<String, Object>>> tmpResultMap = doNewOldPtsCompare(finalPtsJanDtoListByGroup, resultDataList, ptsSkuNum, pattern,
                                        shelfPtsHeaderDto, ptsVersion, catePakList, companyCd, fileParentPath,
                                        branch, commodityMustBranchJans, priorityOrderCd, commodityNotBranchJans, janReplaceMap, ptsJanDtoList, patternBranchCd);

                                resultMap.put(MagicString.DELETE_LIST, tmpResultMap.getOrDefault(MagicString.DELETE_LIST, Lists.newArrayList()));
                                resultMap.put(MagicString.NEW_LIST, tmpResultMap.getOrDefault(MagicString.NEW_LIST, Lists.newArrayList()));
                            }
                        }
                        return resultMap;
                    });

                    tasks.add(mustNotFuture);
                }

                CompletableFuture futures = CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).whenComplete((unused, throwable) -> tasks.forEach(future-> {
                    try {
                        Map<String, List<Map<String, Object>>> tmpResult = future.get();
                        allNewJanList.addAll(tmpResult.get(MagicString.NEW_LIST));
                        allDeleteJanList.addAll(tmpResult.get(MagicString.DELETE_LIST));
                    } catch (InterruptedException e) {
                        logger.error("",e);
                        cacheUtil.put(taskId, "-1");
                        Thread.currentThread().interrupt();
                    } catch (ExecutionException e){
                        logger.error("",e);
                        cacheUtil.put(taskId, "-1");
                    }
                }));
                futures.join();
            }

            if(newCutFlg==1){
                this.writeNewCutExcel(fileParentPath, allNewJanList, allDeleteJanList,
                        priorityOrderCd, companyCd, branchs);
            }
        } catch (Exception e) {
            logger.error("", e);
            cacheUtil.put(taskId, "-1");
        }
        JSONObject json = new JSONObject();
        json.put("path", fileParentPath);
        json.put("fileName", zipFileName);
        cacheUtil.put(taskId, json.toJSONString());
        return ImmutableMap.of();
    }

    @Override
    public Map<String, Object> downloadPtsTask(String taskId, String companyCd, Integer priorityOrderCd, Integer newCutFlg,
                                               Integer ptsVersion, HttpServletResponse response) {
        if(Strings.isNullOrEmpty(taskId)){
            taskId = priorityOrderCd+"_"+System.currentTimeMillis();
        }

        if (cacheUtil.get(taskId)!=null) {
            String s = cacheUtil.get(taskId).toString();

            if("-1".equals(s)){
                return ResultMaps.result(ResultEnum.FAILURE);
            }else if("1".equals(s)){
                JSONObject json = new JSONObject();
                json.put("status", "9");
                json.put("taskId", taskId);
                return ResultMaps.result(ResultEnum.SUCCESS, json.toJSONString());
            }else{
                JSONObject json = new JSONObject();
                json.put("taskId", taskId);
                return ResultMaps.result(ResultEnum.SUCCESS, json.toJSONString());
            }

        }

        String finalTaskId = taskId;
        Future<?> submit = taskExecutor.submit(() -> {
            try {
                this.downloadPts(finalTaskId, companyCd, priorityOrderCd, newCutFlg, ptsVersion, response);
            } catch (IOException e) {
                cacheUtil.put(finalTaskId, "-1");
                throw new RuntimeException(e);
            }
        });

        try {
            submit.get(10, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            JSONObject json = new JSONObject();
            json.put("status", "9");
            json.put("taskId", taskId);
            return ResultMaps.result(ResultEnum.SUCCESS, json.toJSONString());
        } catch(InterruptedException e ){
            Thread.currentThread().interrupt();
            logger.error("", e);
            return ResultMaps.result(ResultEnum.FAILURE);
        } catch (ExecutionException e){
            logger.error("", e);
            return ResultMaps.result(ResultEnum.FAILURE);
        }

        JSONObject json = new JSONObject();
        json.put("taskId", taskId);
        return ResultMaps.result(ResultEnum.SUCCESS, json.toJSONString());
    }

    @Override
    public Map<String, Object> getAttrInfo(String companyCd, Integer priorityOrderCd) {
        List<Map<String, Object>> attrInfo = workPriorityOrderPtsClassify.getAttrInfo(companyCd, priorityOrderCd);
        return ResultMaps.result(ResultEnum.SUCCESS,attrInfo);
    }

    @Override
    public void priorityOrderDataForExcel(PriorityOrderMstDto priorityOrderMstDto,HttpServletResponse response) {
        String companyCd = priorityOrderMstDto.getCompanyCd();
        Integer priorityOrderCd = priorityOrderMstDto.getPriorityOrderCd();
        List<Map<String, Object>> priorityData = new Gson().fromJson(priorityOrderMstDto.getPriorityData(), new com.google.common.reflect.TypeToken<List<Map<String, Object>>>(){}.getType());
        List<LinkedHashMap<String, Object>> attrForName = priorityOrderDataMapper.getAttrForName(companyCd, priorityOrderCd);
        Map<String,Object> paramData = new HashMap<>();
        String productName = priorityOrderMstMapper.getProductName(companyCd, priorityOrderCd);

        paramData.put("productName",productName);
        List<String> shelfPatternName = priorityOrderMstMapper.getShelfPatternName(companyCd, priorityOrderCd);
        paramData.put("shelfPatternName",shelfPatternName);
        List<String> attrName = attrForName.stream().map(map -> map.get("name").toString()).collect(Collectors.toList());
        paramData.put("attrName",attrName);
        String company = productPowerMstMapper.getCompanyName(companyCd);
        paramData.put("company",company);
        LinkedHashMap<String,Object> mapColHeader = new LinkedHashMap<>();
        mapColHeader.put("jan_old","旧JAN");
        mapColHeader.put(MagicString.JAN_NEW,"新JAN");
        mapColHeader.put("sku","SKU");
        mapColHeader.put(MagicString.BRANCH_AMOUNT_UPD,"店@金額(円)");
        mapColHeader.put("pos_amount","POS金額(円)");
        mapColHeader.put("unit_price","単価");
        mapColHeader.put("branch_amount","店@金額(円)");
        mapColHeader.put("branch_num","定番 店舗数");
        mapColHeader.put(MagicString.BRANCH_NUM_UPD,"定番 店舗数");
        mapColHeader.put("difference","配荷差");
        mapColHeader.put("sale_forecast","売上増減 予測(千円)");
        mapColHeader.put("rank","Rank");
        mapColHeader.put("rank_prop","Rank");
        mapColHeader.put(MagicString.RANK_UPD,"Rank");
        String colSort = "jan_old,jan_new,sku";
        for (LinkedHashMap<String, Object> linkedHashMap : attrForName) {
            mapColHeader.put(linkedHashMap.get("sort").toString(),linkedHashMap.get("name"));
            colSort +=","+linkedHashMap.get("sort");
        }
        priorityData.add(0,mapColHeader);
        colSort+= ",pos_amount,branch_amount,unit_price,rank,branch_num,rank_prop,rank_upd,branch_num_upd,branch_amount_upd,difference,sale_forecast";
        ServletOutputStream outputStream = null;
        String date = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        try {
            String fileName = String.format("%s.xlsx", "優先順位表"+date);
            String format = MessageFormat.format("attachment;filename={0};",  UriUtils.encode(fileName, "utf-8"));
            response.setHeader(HttpHeaders.CONTENT_TYPE, "application/octet-stream");
            response.setHeader("Content-Disposition", format);
            outputStream = response.getOutputStream();
            ExcelUtils.priorityOrderExcel(colSort,priorityData,outputStream,attrForName.size(),paramData);
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            if(Objects.nonNull(outputStream)){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    logger.error("io閉じる異常", e);
                    logAspect.setTryErrorLog(e,new Object[]{companyCd,priorityOrderCd});
                }
            }
        }


    }


    @Override
    public void packagePtsZip(String taskId, HttpServletResponse response) throws IOException {
        String s = cacheUtil.get(taskId).toString();
        JSONObject jsonObject = JSON.parseObject(s);
        String zipFileName = jsonObject.getString("fileName");
        String fileParentPath = jsonObject.getString("path");

        String format = MessageFormat.format("attachment;filename={0};",  UriUtils.encode(zipFileName, "utf-8"));

        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, format);

        ServletOutputStream outputStream = response.getOutputStream();
        try(ZipOutputStream zos = new ZipOutputStream(outputStream)) {
            writeZip(fileParentPath, zos);
        } catch (IOException e) {
            logger.error("", e);
        } finally {
            outputStream.close();
            this.deleteDir(new File(fileParentPath));
            cacheUtil.remove(taskId);
        }
    }

    private Map<String, List<Map<String, Object>>> doNewOldPtsCompare(Map<String, List<Map<String, Object>>> ptsJanDtoListByGroup,
                                                                      List<Map<String, Object>> resultDataList, List<Map<String, Object>> ptsSkuNum,
                                                                      ShelfPtsDataDto pattern, ShelfPtsHeaderDto shelfPtsHeaderDto,
          Integer ptsVersion, List<PriorityOrderCatePakVO> catePakList, String companyCd, String fileParentPath,
          Map<String, Object> branch, List<Map<String, Object>> commodityMustJans,Integer priorityOrderCd,
          List<Map<String, Object>> commodityNotJans, Map<String, String> janReplaceMap, List<Map<String, Object>> ptsJanDtoList,List<String> patternBranchCd){
        Map<String, Map<String, String>> catePakMap = new HashMap<>();
        List<Map<String, Object>> newJanList = new ArrayList<>();
        List<Map<String, Object>> deleteJanList = new ArrayList<>();
        Map<String, List<Map<String, Object>>> newPtsJanMap = new HashMap<>();
        Map<String, List<Map<String, Object>>> resultMap = new HashMap<>(2);

        String branchName = "";
        String branchCd = "";
        if(branch!=null && !branch.isEmpty()){
            branchName = branch.get("branch_name").toString();
            branchCd = branch.get(MagicString.BRANCH).toString();
        }

        String branchNames = String.join("_", Lists.newArrayList(branchCd,branchName));
        String fileName = shelfPtsHeaderDto.getFileName().replace(".csv", "")+ (Strings.isNullOrEmpty(branchNames)?"":"_"+branchNames)+".csv";

        //old pts have repeat jan
        Map<String, String> repeatOldJan = new HashMap<>();
        Map<String, List<Map<String, Object>>> notInPtsJanListByGroup = new HashMap<>();

        for (Map.Entry<String, List<Map<String, Object>>> entry : ptsJanDtoListByGroup.entrySet()) {
            //jan new: 777和jan delete: 888
            String group = entry.getKey();
            logger.info("group:{}",group);

            int skuNumInit = 0;
            List<Map<String, Object>> attrList = ptsSkuNum.stream().filter(map -> map.get(MagicString.ATTR_LIST).toString().equals(group)).collect(Collectors.toList());
            if (!attrList.isEmpty()) {
                Map<String, Object> attrMap = attrList.get(0);
                skuNumInit = Integer.parseInt(attrMap.get("sku_num_init").toString());
            }

            List<Map<String, Object>> ptsJanList = entry.getValue();
            List<String> ptsJanCdList = ptsJanList.stream().map(map -> map.get(MagicString.JAN).toString()).collect(Collectors.toList());
            AtomicInteger maxSkuNum = new AtomicInteger(skuNumInit);
            List<String> commodityNotJansCd = new ArrayList<>();
            if(branch!=null){
                commodityNotJansCd = commodityNotJans.stream().map(map->map.get(MagicString.JAN_NEW).toString()).collect(Collectors.toList());
            }

            List<String> finalCommodityNotJansCd = commodityNotJansCd;
            List<Map<String, Object>> resultDataByAttr = resultDataList.stream()
                    .filter(s -> s.get(MagicString.ATTR_LIST).toString().equals(group)).collect(Collectors.toList());

            if(branch!=null){
                //must jan first
                List<String> commodityMustJansCd = commodityMustJans.stream().map(map->map.get(MagicString.JAN_NEW).toString()).collect(Collectors.toList());
                List<String> finalCommodityNotJansCdList = commodityNotJansCd;
                resultDataByAttr = resultDataByAttr.stream().map(map->{
                    String janNew = map.get(MagicString.JAN).toString();
                    int rank = Integer.parseInt(map.get(MagicString.RANK_UPD).toString());
                    if (commodityMustJansCd.contains(janNew) && rank>maxSkuNum.get()) {
                        maxSkuNum.getAndDecrement();
                        map.put("rank_upd_original", MapUtils.getString(map, MagicString.RANK_UPD));
                        map.put(MagicString.RANK_UPD, "0");
                    }

                    if (finalCommodityNotJansCdList.contains(janNew) && rank<=maxSkuNum.get()) {
                        maxSkuNum.getAndIncrement();
                    }

                    return map;
                }).collect(Collectors.toList());
            }

            int finalSkuNum = maxSkuNum.get();
            List<Map<String, Object>> resultData = resultDataByAttr.stream()
                    .filter(s->Integer.parseInt(s.get(MagicString.RANK_UPD).toString()) <= finalSkuNum
                            && !finalCommodityNotJansCd.contains(s.get(MagicString.JAN).toString()))
                    .sorted(Comparator.comparing(map->Integer.parseInt(map.get(MagicString.RANK_UPD).toString()))).collect(Collectors.toList());
            List<Map<String, Object>> bkResultData = resultDataByAttr.stream()
                    .filter(s->!finalCommodityNotJansCd.contains(s.get(MagicString.JAN).toString())
                    && !janReplaceMap.containsValue(s.get(MagicString.JAN).toString()))
                    .sorted(Comparator.comparing(map->Integer.parseInt(map.get(MagicString.RANK_UPD).toString()))).collect(Collectors.toList());
            List<Map<String, Object>> notInPtsJanList = bkResultData.stream()
                    .filter(map -> Integer.parseInt(map.get(MagicString.RANK_UPD).toString()) > finalSkuNum
                            || (Integer.parseInt(map.get(MagicString.RANK_UPD).toString()) <= finalSkuNum
                            && !ptsJanCdList.contains(MapUtils.getString(map, MagicString.JAN))))
                    .sorted(Comparator.comparing(map->Integer.parseInt(map.get(MagicString.RANK_UPD)
                            .toString()))).collect(Collectors.toList());
            notInPtsJanListByGroup.put(group, notInPtsJanList);
            if(!notInPtsJanList.isEmpty()){
                ptsBackupJanMapper.insertBackupJan(priorityOrderCd, pattern.getShelfPatternCd(), group, branchCd, notInPtsJanList);
            }

            int newJanIndex = 0;
            List<Map<String, Object>> adoptPtsJanList = new ArrayList<>(ptsJanList);

            for (int i = 0; i < adoptPtsJanList.size(); i++) {
                Map<String, Object> ptsJan = adoptPtsJanList.get(i);
                String jan = ptsJan.get(MagicString.JAN).toString();
                String janOld = ptsJan.get(MagicString.JAN_OLD).toString();
                String rankUpd = MapUtils.getInteger(ptsJan, MagicString.RANK_UPD)+"";
                String curAttrList = ptsJan.get(MagicString.ATTR_LIST).toString();

                //mulit_attr-->attr?
                String attrKey = String.join(",", curAttrList.split(","));

                if(resultData.stream().noneMatch(map->jan.equals(map.get(MagicString.JAN).toString()))){
                    //replace
                    if(deleteJanList.stream().noneMatch(map->jan.equals(map.get(MagicString.JAN_OLD).toString()))){
                        Map<String, Object> oldJanMap = new HashMap<>(ptsJan);
                        oldJanMap.put(MagicString.PATTERN_NAME, pattern.getShelfPatternName());
                        oldJanMap.put(MagicString.PTS_NAME, fileName);
                        newJanList.removeIf(map->Objects.equals(MapUtils.getString(map, MagicString.JAN), jan));
                        deleteJanList.add(oldJanMap);
                    }else {
                        if(janReplaceMap.containsKey(janOld)) {
                            String newJan = MapUtils.getString(janReplaceMap, janOld);
                            List<String> newRankJan = resultData.stream().filter(data -> MapUtils.getString(data, MagicString.JAN).equals(repeatOldJan.get(jan))).map(data -> MapUtils.getString(data, MagicString.RANK_UPD)).collect(Collectors.toList());

                            //old pts not exists and jan replace
                            if (ptsJanDtoList.stream().noneMatch(map->MapUtils.getString(map, MagicString.JAN).equals(newJan))&&
                                    newJanList.stream().noneMatch(map->newJan.equals(map.get(MagicString.JAN).toString()))) {

                                Optional<Map<String, Object>> oldJanMap = resultData.stream().filter(map -> MapUtils.getString(map, MagicString.JAN).equals(newJan)).findAny();

                                if (oldJanMap.isPresent()) {
                                    Map<String, Object> oldJan = oldJanMap.get();
                                    Map<String, Object> newCopyJanMap = new HashMap<>(oldJan);
                                    newCopyJanMap.put(MagicString.BRANCH_NUM_UPD, oldJan.get(MagicString.BRANCH_NUM_UPD));
                                    newCopyJanMap.put(MagicString.BRANCH_AMOUNT_UPD, oldJan.get(MagicString.BRANCH_AMOUNT_UPD));
                                    newCopyJanMap.put(MagicString.PATTERN_NAME, pattern.getShelfPatternName());
                                    newCopyJanMap.put(MagicString.PTS_NAME, fileName);
                                    newCopyJanMap.put(MagicString.RANK_UPD, MapUtils.getInteger(oldJan, MagicString.RANK_UPD));
                                    deleteJanList.removeIf(map->Objects.equals(newJan, MapUtils.getString(map,MagicString.JAN)));
                                    //priority_order_data exist jan
                                    newJanList.add(newCopyJanMap);
                                }

                            }
                            repeatOldJan.put(jan,newJan);
                            if(ptsVersion==1){
                                if(!newRankJan.isEmpty()){
                                    ptsJan.put(MagicString.RANK_UPD, newRankJan.get(0));
                                }
                                ptsJan.put(MagicString.JAN, newJan);
                            }
                        }
                    }

                    logger.info("patternCd:{}, repeatOldJan:{}", pattern.getId(), repeatOldJan);
                    if(repeatOldJan.containsKey(jan)){
                        List<String> newRankJan = resultData.stream().filter(data -> MapUtils.getString(data, MagicString.JAN).equals(repeatOldJan.get(jan))).map(data -> MapUtils.getString(data, MagicString.RANK_UPD)).collect(Collectors.toList());
                        if(!newRankJan.isEmpty()){
                            ptsJan.put(MagicString.RANK_UPD, newRankJan.get(0));
                        }

                        if(ptsVersion == 1){
                            ptsJan.put(MagicString.JAN, repeatOldJan.get(jan));
                        }else{
                            ptsJan.put(MagicString.JAN, jansMapper.selectDummyJan(companyCd,jan));
                        }
                    }else if(newJanIndex < notInPtsJanList.size()){
                        String newJan = notInPtsJanList.get(newJanIndex).get(MagicString.JAN).toString();
                        Integer newJanRank = MapUtils.getInteger(notInPtsJanList.get(newJanIndex), MagicString.RANK_UPD);

                        if(janReplaceMap.containsKey(janOld)){
                            newJan = MapUtils.getString(janReplaceMap, janOld);
                            //old pts not exists and jan replace
                            String finalNewJan = newJan;
                            if (ptsJanDtoList.stream().noneMatch(map->MapUtils.getString(map, MagicString.JAN).equals(finalNewJan))&&
                                    newJanList.stream().noneMatch(map-> finalNewJan.equals(map.get(MagicString.JAN).toString()))) {

                                Optional<Map<String, Object>> oldJanMap = resultData.stream().filter(map -> MapUtils.getString(map, MagicString.JAN).equals(finalNewJan)).findAny();

                                if (oldJanMap.isPresent()) {
                                    Map<String, Object> oldJan = oldJanMap.get();
                                    Map<String, Object> newCopyJanMap = new HashMap<>(oldJan);
                                    newCopyJanMap.put(MagicString.BRANCH_NUM_UPD, oldJan.get(MagicString.BRANCH_NUM_UPD));
                                    newCopyJanMap.put(MagicString.BRANCH_AMOUNT_UPD, oldJan.get(MagicString.BRANCH_AMOUNT_UPD));
                                    newCopyJanMap.put(MagicString.PATTERN_NAME, pattern.getShelfPatternName());
                                    newCopyJanMap.put(MagicString.PTS_NAME, fileName);
                                    newCopyJanMap.put(MagicString.RANK_UPD, MapUtils.getInteger(oldJan, MagicString.RANK_UPD));
                                    //priority_order_data exist jan
                                    deleteJanList.removeIf(map->finalNewJan.equals(MapUtils.getString(map,MagicString.JAN)));
                                    newJanList.add(newCopyJanMap);
                                }

                            }
                        }else{
                            String finalNewJan = newJan;
                            if(!notInPtsJanList.isEmpty() && newJanList.stream()
                                    .noneMatch(map-> finalNewJan.equals(map.get(MagicString.JAN).toString()))){
                                Map<String, Object> newJanMap = notInPtsJanList.get(newJanIndex);
                                Map<String, Object> newCopyJanMap = new HashMap<>(newJanMap);
                                newCopyJanMap.put(MagicString.BRANCH_NUM_UPD, newJanMap.get(MagicString.BRANCH_NUM_UPD));
                                newCopyJanMap.put(MagicString.BRANCH_AMOUNT_UPD, newJanMap.get(MagicString.BRANCH_AMOUNT_UPD));
                                newCopyJanMap.put(MagicString.PATTERN_NAME, pattern.getShelfPatternName());
                                newCopyJanMap.put(MagicString.PTS_NAME, fileName);
                                newCopyJanMap.put(MagicString.RANK_UPD, MapUtils.getInteger(newJanMap, MagicString.RANK_UPD));
                                deleteJanList.removeIf(map->finalNewJan.equals(MapUtils.getString(map,MagicString.JAN)));
                                //priority_order_data exist jan
                                newJanList.add(newCopyJanMap);

                                newJanIndex++;
                            }
                        }

                        ptsJan.put(MagicString.RANK_UPD, newJanRank);
                        repeatOldJan.put(jan,newJan);
                        if(ptsVersion==1){
                            ptsJan.put(MagicString.JAN, newJan);
                        }
                    }else{
                        if(ptsVersion==1){
                            ptsJan.put(MagicString.DEL_FLAG, "1");
                        }else if(ptsVersion==2){
                            //not exist jan replace old jan,record flag
                            ptsJan.put(MagicString.DEL_FLAG, "0");
                        }
                    }

                    if(ptsVersion == 2){
                        //確認用棚割
                        ptsJan.put(MagicString.JAN, jansMapper.selectDummyJan(companyCd, jan));
                        ptsJan.put(MagicString.DUMMY_JAN, "1");
                    }

                    adoptPtsJanList.set(i, ptsJan);
                }

                int finalSkuNumInit = skuNumInit;
                List<PriorityOrderCatePakVO> catePakVOS = catePakList.stream().filter(catePak -> catePak.getSmalls().equals(attrKey)
                        && Integer.parseInt(catePak.getRank().toString()) <= finalSkuNumInit).collect(Collectors.toList());
                for (PriorityOrderCatePakVO catePakVO : catePakVOS) {
                    Map<String, String> catePakItemMap = catePakMap.getOrDefault(catePakVO.getBigs()+"@"+catePakVO.getRank(), Maps.newHashMap());
                    catePakItemMap.put(MagicString.SMALLS, catePakVO.getSmalls());
                    catePakMap.put(catePakVO.getBigs()+"@"+catePakVO.getRank(), catePakItemMap);
                }
            }


            String transferGroup = group;
            //拡 find 縮, record last newJanIndex
            int finalNewJanIndex = newJanIndex;
            List<PriorityOrderCatePakVO> bigList = catePakList.stream().filter(catePak -> catePak.getBigs().equals(transferGroup)).collect(Collectors.toList());
            for (PriorityOrderCatePakVO priorityOrderCatePakVO : bigList) {
                Integer rank = priorityOrderCatePakVO.getRank();
                Map<String, String> catePakItemMap = catePakMap.getOrDefault(priorityOrderCatePakVO.getBigs()+"@"+rank, Maps.newHashMap());
                catePakItemMap.put(MagicString.BIG_LAST_INDEX, finalNewJanIndex+"");
                catePakMap.put(priorityOrderCatePakVO.getBigs()+"@"+rank, catePakItemMap);
                finalNewJanIndex++;
            }

            if(skuNumInit>0){
                newPtsJanMap.put(group, adoptPtsJanList);
            }
        }

        //拡縮
        for (Map.Entry<String, Map<String, String>> entry : catePakMap.entrySet()) {
            String bigs = entry.getKey().split("@")[0];
            Map<String, String> catePakItemMap = entry.getValue();

            if(!catePakItemMap.containsKey(MagicString.SMALLS)){
                logger.warn("catePakItemMap：{}, It can't shrink", catePakItemMap);
                continue;
            }

            String smalls = catePakItemMap.get(MagicString.SMALLS);
            int bigLastIndex = Integer.parseInt(catePakItemMap.getOrDefault(MagicString.BIG_LAST_INDEX, "0"));

            String attrBigs = bigs;
            String attrSmalls = smalls;

            List<Map<String, Object>> bigList = null;
            List<Map<String, Object>> smallList = null;
            if(newPtsJanMap.containsKey(attrBigs)){
                bigList = newPtsJanMap.get(attrBigs).stream().filter(map->
                        !"1".equals(MapUtils.getString(map, MagicString.DEL_FLAG)) && !"0".equals(MapUtils.getString(map, MagicString.DEL_FLAG))).collect(Collectors.toList());
            }

            if(newPtsJanMap.containsKey(attrSmalls)){
                smallList = newPtsJanMap.get(attrSmalls).stream().filter(map->
                        !"1".equals(MapUtils.getString(map, MagicString.DEL_FLAG)) && !"0".equals(MapUtils.getString(map, MagicString.DEL_FLAG))).collect(Collectors.toList());
            }else{
                continue;
            }

            List<Map<String, Object>> smallListSortByRank = smallList.stream().sorted(Comparator.comparing(map -> MapUtils.getInteger(map, MagicString.RANK_UPD))).collect(Collectors.toList());
            Map<String, Object> compressJan = smallListSortByRank.get(smallList.size() - 1);
            //縮attr's last rank index
            int smallsIndex = 0;
            String smallJan = MapUtils.getString(compressJan, MagicString.JAN);
            for (int i = 0; i < smallList.size(); i++) {
                if (MapUtils.getString(smallList.get(i), MagicString.JAN).equals(smallJan)){
                    smallsIndex = i;
                }
            }

            List<Map<String, Object>> notInPtsJanList = new ArrayList<>();
            if(bigList != null && !bigList.isEmpty()) {
                notInPtsJanList = notInPtsJanListByGroup.getOrDefault(attrBigs, ImmutableList.of());
            }

            Map<String, Object> ptsJanMap = smallList.get(smallsIndex);

            if(deleteJanList.stream().noneMatch(map->smallJan.equals(map.get(MagicString.JAN_OLD).toString())) && !repeatOldJan.containsKey(smallJan)){
                Map<String, Object> oldJanMap = new HashMap<>(ptsJanMap);
                oldJanMap.put(MagicString.PATTERN_NAME, pattern.getShelfPatternName());
                oldJanMap.put(MagicString.PTS_NAME, fileName);
                newJanList.removeIf(map->Objects.equals(MapUtils.getString(map, MagicString.JAN), smallJan));
                deleteJanList.add(oldJanMap);
            }

            if(notInPtsJanList.isEmpty() || bigLastIndex>= notInPtsJanList.size()){
                //no jan can 拡
                if(ptsVersion == 2){
                    smallList = smallList.stream().map(map-> {
                        if (MapUtils.getString(map, MagicString.JAN).equals(smallJan) && !"1".equals(MapUtils.getString(map, MagicString.DUMMY_JAN))) {
                            map.put(MagicString.JAN, jansMapper.selectDummyJan(companyCd,smallJan));
                        }
                        return map;
                    }).collect(Collectors.toList());
                }else{
                    smallList = smallList.stream().map(map-> {
                        if (MapUtils.getString(map, MagicString.JAN).equals(smallJan)) {
                            map.put(MagicString.DEL_FLAG, "1");
                        }
                        return map;
                    }).collect(Collectors.toList());
                }
            }else{
                Map<String, Object> bigMap = notInPtsJanList.get(bigLastIndex);

                if(ptsVersion == 2){
                    smallList = smallList.stream().map(map-> {
                        if(MapUtils.getString(map, MagicString.JAN).equals(smallJan) && !"1".equals(MapUtils.getString(map, MagicString.DUMMY_JAN))){
                            map.put(MagicString.JAN, jansMapper.selectDummyJan(companyCd,smallJan));
                        }

                        return map;
                    }).collect(Collectors.toList());
                }else{
                    smallList = smallList.stream().map(map->{
                        if(MapUtils.getString(map, MagicString.JAN).equals(smallJan)){
                            map.put(MagicString.JAN, MapUtils.getString(bigMap,MagicString.JAN));
                        }
                        return map;
                    }).collect(Collectors.toList());
                }

                if (newJanList.stream().noneMatch(map->map.get(MagicString.JAN).toString().equals(bigMap.get(MagicString.JAN)))) {
                    Map<String, Object> newCopyJanMap = new HashMap<>(bigMap);
                    newCopyJanMap.put(MagicString.BRANCH_NUM, bigMap.get(MagicString.BRANCH_NUM));
                    newCopyJanMap.put(MagicString.BRANCH_AMOUNT, bigMap.get(MagicString.BRANCH_AMOUNT));
                    newCopyJanMap.put(MagicString.PATTERN_NAME, pattern.getShelfPatternName());
                    newCopyJanMap.put(MagicString.PTS_NAME, fileName);
                    deleteJanList.removeIf(map->Objects.equals(bigMap.get(MagicString.JAN), MapUtils.getString(map,MagicString.JAN)));
                    //priority_order_data exist jan
                    newJanList.add(newCopyJanMap);
                    repeatOldJan.put(smallJan, MapUtils.getString(bigMap, MagicString.JAN));
                }
            }

            newPtsJanMap.put(attrSmalls, smallList);
        }

        this.pluralJan(newPtsJanMap, priorityOrderCd,pattern, branchCd,
                ptsVersion, companyCd, deleteJanList, newJanList, fileName, patternBranchCd);

        resultMap.put(MagicString.DELETE_LIST, deleteJanList);
        resultMap.put(MagicString.NEW_LIST, newJanList);

        List<Map<String, Object>> newPtsJanList = this.reOrderByTaiTana(newPtsJanMap);
        List<ShelfPtsDataTaimst> shelfPtsDataTaimst = shelfPtsDataMapper.selectShelfPtsTaiMst(pattern.getId());
        List<ShelfPtsDataTanamst> shelfPtsDataTanamst = shelfPtsDataMapper.selectShelfPtsTanaMst(pattern.getId());

        this.saveJanShelfNameCd(newPtsJanList, pattern.getShelfNameCd(), priorityOrderCd, pattern.getShelfPatternCd(), patternBranchCd);
        this.generateCsv2File(newJanList.stream().map(map->map.get(MagicString.JAN).toString()).collect(Collectors.toList()),
                deleteJanList.stream().map(map->map.get(MagicString.JAN).toString()).collect(Collectors.toList()),
                fileParentPath,newPtsJanList, shelfPtsHeaderDto,
                shelfPtsDataTaimst, shelfPtsDataTanamst, fileName);

        return resultMap;
    }

    /**
     * 優先順位表で複数棚名称を選択した時、出力されるパターンでJANが重複しないように
     */
    private void pluralJan(Map<String, List<Map<String, Object>>> newPtsJanMap, Integer priorityOrderCd, ShelfPtsDataDto shelfPtsDataDto,
                           String branchCd, Integer ptsVersion, String companyCd, List<Map<String, Object>> deleteList,
                           List<Map<String, Object>> newList, String fileName, List<String> branchList){
        Integer shelfNameCd = shelfPtsDataDto.getShelfNameCd();

        List<Map<String, Object>> backupJanList = ptsBackupJanMapper.selectBackupJan(priorityOrderCd, shelfPtsDataDto.getShelfPatternCd(), shelfNameCd, branchCd);
        Map<String, List<Map<String, Object>>> backupJanListByGroup = backupJanList.stream().collect(Collectors.groupingBy(map -> MapUtils.getString(map, MagicString.ATTR_LIST)));
        Map<String, String> repeatOldJan = new HashMap<>();

        for (Map.Entry<String, List<Map<String, Object>>> entry : newPtsJanMap.entrySet()) {
            final int[] usedIndex = {0};
            List<Map<String, Object>> newPtsJanList = entry.getValue();
            List<String> newPtsJanCdList = newPtsJanList.stream().map(map -> MapUtils.getString(map, MagicString.JAN)).collect(Collectors.toList());
            List<Map<String, Object>> backupJan = backupJanListByGroup.getOrDefault(entry.getKey(), ImmutableList.of());

            List<String> existOtherPatternJan = ptsPatternNameMapper.selectExistPatternJan(priorityOrderCd, shelfNameCd,
                    newPtsJanCdList, "??| '{"+Joiner.on(",").join(branchList)+"}'");
            List<Map<String, Object>> removeBackupJan = new ArrayList<>();
            BeanUtils.copyProperties(backupJan, removeBackupJan);
            removeBackupJan.removeIf(janMap->newPtsJanCdList.contains(MapUtils.getString(janMap, MagicString.JAN)));

            if(!existOtherPatternJan.isEmpty()){
                newPtsJanList.forEach(janMap->{
                    String janOld = MapUtils.getString(janMap, MagicString.JAN_OLD);
                    String janCd = MapUtils.getString(janMap, MagicString.JAN);
                    if(existOtherPatternJan.contains(MapUtils.getString(janMap, MagicString.JAN))){
                        if (janOld.equals(janCd)) {
                            janMap.put(MagicString.PTS_NAME, fileName);
                            janMap.put(MagicString.PATTERN_NAME, shelfPtsDataDto.getShelfPatternName());
                            deleteList.removeIf(map-> Objects.equals(MapUtils.getString(map, MagicString.JAN), janOld));
                            deleteList.add(janMap);
                        }else{
                            newList.removeIf(map->map.get(MagicString.JAN).equals(janCd));
                        }

                        if(removeBackupJan.isEmpty() || usedIndex[0]>=removeBackupJan.size()){
                            if(ptsVersion == 2){
                                janMap.put(MagicString.JAN, jansMapper.selectDummyJan(companyCd,MapUtils.getString(janMap, MagicString.JAN_OLD)));
                            }else{
                                janMap.put(MagicString.DEL_FLAG, "1");
                            }
                        }else{
                            if(ptsVersion == 1){
                                if(repeatOldJan.containsKey(janCd)){
                                    janMap.put(MagicString.JAN, repeatOldJan.get(janOld));
                                }else{
                                    Map<String, Object> janNewMap = removeBackupJan.get(usedIndex[0]++);

                                    if (newList.stream().noneMatch(map-> MapUtils.getString(map, MagicString.JAN).equals(janOld))) {
                                        janNewMap.put(MagicString.PTS_NAME, fileName);
                                        janNewMap.put(MagicString.PATTERN_NAME, shelfPtsDataDto.getShelfPatternName());

                                        janMap.put(MagicString.JAN, janNewMap.get(MagicString.JAN));
                                        newList.add(janNewMap);
                                    }

                                    repeatOldJan.put(janCd, MapUtils.getString(janNewMap, MagicString.JAN));
                                }
                            }else{
                                janMap.put(MagicString.JAN, jansMapper.selectDummyJan(companyCd,MapUtils.getString(janMap, MagicString.JAN_OLD)));
                            }
                        }
                    }
                });

                newPtsJanMap.put(entry.getKey(), newPtsJanList);
            }
        }
    }

    private List<Map<String, Object>> reOrderByTaiTana(Map<String, List<Map<String, Object>>> newPtsJanMap){
        List<Map<String, Object>> newPtsJanList = new ArrayList<>();
        for (List<Map<String, Object>> value : newPtsJanMap.values()) {
            newPtsJanList.addAll(value);
        }
        //order by tai_cd,tana_cd, tanaposition_cd
        newPtsJanList = newPtsJanList.stream().filter(map->!"1".equals(map.getOrDefault(MagicString.DEL_FLAG,"").toString()))
                .sorted(Comparator.comparing(map->MapUtils.getInteger(map, MagicString.TANA_POSITION_CD)))
                .sorted(Comparator.comparing(map->MapUtils.getInteger(map, MagicString.TANA_CD)))
                .sorted(Comparator.comparing(map->MapUtils.getInteger(map, MagicString.TAI_CD)))
                .collect(Collectors.toList());
        Map<String, Integer> taiTanaMap = new HashMap<>();
        newPtsJanList.forEach(map->{
            String taiTanaKey = MapUtils.getInteger(map, MagicString.TAI_CD)+"_"+MapUtils.getInteger(map, MagicString.TANA_CD);
            Integer order = taiTanaMap.getOrDefault(taiTanaKey, 0)+1;
            map.put(MagicString.TANA_POSITION_CD, order);
            taiTanaMap.put(taiTanaKey, order);
        });

        return newPtsJanList;
    }

    /**
     * save jan and shelf_name_cd, in order to process [[優先順位表で複数棚名称を選択した時、出力されるパターンでJANが重複しないように]]
     * @return
     */
    public void saveJanShelfNameCd(List<Map<String, Object>> newJanList, Integer shelfNameCd, Integer priorityOrderCd,
                Integer patternCd, List<String> branchList){
        ptsPatternNameMapper.insertPtsPatternName(priorityOrderCd, shelfNameCd, newJanList, patternCd, JSON.toJSONString(branchList));
    }
    public String generateCsv2File(List<String> newJanList, List<String> deleteJanList, String fileParentPath,
                                   List<Map<String, Object>> newPtsJanList, ShelfPtsHeaderDto shelfPtsHeaderDto,
                                   List<ShelfPtsDataTaimst> shelfPtsDataTaimst, List<ShelfPtsDataTanamst> shelfPtsDataTanamst, String fileName) {
        String filePath = Joiner.on(File.separator).join(Lists.newArrayList(fileParentPath, fileName));
        logger.info("file path: {}", fileParentPath);

        try(OutputStreamWriter fileWriter = new OutputStreamWriter(new FileOutputStream(filePath), "Shift_JIS");
            CsvWriter csvWriter = CsvWriter.builder().build(fileWriter)){
            csvWriter.writeRow(Lists.newArrayList(shelfPtsHeaderDto.getCommonInfo(),
                    shelfPtsHeaderDto.getVersionInfo(), shelfPtsHeaderDto.getOutFlg()));
            csvWriter.writeRow(shelfPtsHeaderDto.getModeName());
            csvWriter.writeRow(shelfPtsHeaderDto.getTaiHeader().split(","));

            for (ShelfPtsDataTaimst ptsDataTaimst : shelfPtsDataTaimst) {
                csvWriter.writeRow(Lists.newArrayList(ptsDataTaimst.getTaiCd()+"",
                        ptsDataTaimst.getTaiHeight()+"", ptsDataTaimst.getTaiWidth()+"", ptsDataTaimst.getTaiDepth()+"",
                        Optional.ofNullable(ptsDataTaimst.getTaiName()).orElse("")));
            }

            csvWriter.writeRow(shelfPtsHeaderDto.getTanaHeader().split(","));
            for (ShelfPtsDataTanamst ptsDataTanamst : shelfPtsDataTanamst) {
                csvWriter.writeRow(Lists.newArrayList(ptsDataTanamst.getTaiCd()+"",
                        ptsDataTanamst.getTanaCd()+"", ptsDataTanamst.getTanaHeight()+"", ptsDataTanamst.getTanaWidth()+"",
                        ptsDataTanamst.getTanaDepth()+"",
                        Optional.ofNullable(ptsDataTanamst.getTanaThickness()).orElse(0)+"",
                        Optional.ofNullable(ptsDataTanamst.getTanaType()).orElse(0)+""));
            }

            String[] janHeaders = shelfPtsHeaderDto.getJanHeader().split(",");
            csvWriter.writeRow(janHeaders);

            csvWriter.writeRow("0","0","0", "1000000000777", "1","1","0","1", "", "0","", "");
            for (String newJan : newJanList) {
                csvWriter.writeRow("0","0","0", newJan, "1","1","0","1", "0", "","", "");
            }
            csvWriter.writeRow("0","0","0", "1000000000888", "1","1","0","1", "", "0","", "");
            for (String deleteJan : deleteJanList) {
                csvWriter.writeRow("0","0","0", deleteJan, "1","1","0","1", "0", "","", "");
            }

            for (Map<String, Object> ptsDataJandata : newPtsJanList) {
                List<String> janData = Lists.newArrayList(MapUtils.getInteger(ptsDataJandata, MagicString.TAI_CD) + "",
                        MapUtils.getInteger(ptsDataJandata,MagicString.TANA_CD) + "", MapUtils.getInteger(ptsDataJandata,MagicString.TANA_POSITION_CD) + "", ptsDataJandata.get("jan") + "",
                        MapUtils.getInteger(ptsDataJandata,"face_count",1)+ "",
                        MapUtils.getInteger(ptsDataJandata,"face_men", 1)+ "",
                        MapUtils.getInteger(ptsDataJandata,"face_kaiten", 0) + "",
                        MapUtils.getInteger(ptsDataJandata,"tumiagesu", 1)+ "",
                        MapUtils.getInteger(ptsDataJandata,"zaikosu") + "",
                        Strings.isNullOrEmpty(MapUtils.getString(ptsDataJandata,"face_displayflg"))?"":MapUtils.getInteger(ptsDataJandata,"face_displayflg")+"",
                        Strings.isNullOrEmpty(MapUtils.getString(ptsDataJandata,"face_position"))?"":MapUtils.getInteger(ptsDataJandata,"face_position")+"",
                        Strings.isNullOrEmpty(MapUtils.getString(ptsDataJandata,"depth_display_num"))?"":MapUtils.getInteger(ptsDataJandata,"depth_display_num")+"");
                csvWriter.writeRow(janData.subList(0, janHeaders.length));
            }

        }catch (IOException e) {
            logger.error("csv writer 閉じる異常", e);
        }

        return filePath;
    }

    private void writeNewCutExcel(String fileParentPath, List<Map<String, Object>> allNewList,
                                  List<Map<String, Object>> allDeleteList, Integer priorityOrderCd, String companyCd,
                                  List<Map<String, Object>> branchs){
        List<ClassicPriorityOrderMstAttrSortDto> priorityOrderMstAttrSortDtos = mstAttrSortMapper.selectAttrName(companyCd, priorityOrderCd);
        Map<String, String> mstAttrSortDtoMap = priorityOrderMstAttrSortDtos.stream()
                .collect(Collectors.toMap(ClassicPriorityOrderMstAttrSortDto::getValue, ClassicPriorityOrderMstAttrSortDto::getAttrName, (k,v)->k, LinkedHashMap::new));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        LocalDateTime now = LocalDateTime.now();
        String dateTime = now.format(formatter);
        String fileName = "新規&カット商品_"+dateTime+".xlsx";
        String filePath = Joiner.on(File.separator).join(Lists.newArrayList(fileParentPath,fileName));

        try(XSSFWorkbook workbook = new XSSFWorkbook();
            FileOutputStream fos = new FileOutputStream(filePath);){
            //新規
            XSSFSheet newJanSheet = workbook.createSheet("新規");
            List<String> newJanHeader = Lists.newArrayList(MagicString.PATTERN_NAME_TEXT, "PTS名", "JAN", "商品名");
            for (Map.Entry<String, String> entry : mstAttrSortDtoMap.entrySet()) {
                newJanHeader.add(entry.getValue());
            }
            newJanHeader.addAll(ImmutableList.of("RANK", "配荷店舗数", "想定店＠金額"));

            XSSFRow newJanRowHeader = newJanSheet.createRow(0);
            for (int i = 0; i < newJanHeader.size(); i++) {
                XSSFCell cell = newJanRowHeader.createCell(i);
                cell.setCellValue(newJanHeader.get(i));
                cell.setCellType(CellType.STRING);
            }

            int rowIndex = 1;
            for (Map<String, Object> newJan : allNewList) {
                XSSFRow row = newJanSheet.createRow(rowIndex);
                row.createCell(0).setCellValue(newJan.get(MagicString.PATTERN_NAME).toString());
                row.createCell(1).setCellValue(newJan.get(MagicString.PTS_NAME).toString());
                XSSFCell cell = row.createCell(2);
                cell.setCellType(CellType.STRING);
                cell.setCellValue(newJan.get("jan").toString());
                row.createCell(3).setCellValue(newJan.get("sku").toString());

                int colIndex = 4;
                String attrList = newJan.get(MagicString.ATTR_LIST).toString();
                String[] attrArr = attrList.split(",");
                Map<String, String> attrArrMap = Arrays.stream(attrArr)
                        .collect(Collectors.toMap(attr -> attr.split(":")[0], attr -> attr.split(":")[1]));

                for (Map.Entry<String, String> entry : mstAttrSortDtoMap.entrySet()) {
                    row.createCell(colIndex).setCellValue(attrArrMap.get(entry.getKey()));
                    colIndex++;
                }

                row.createCell(colIndex++).setCellValue(newJan.get(MagicString.RANK_UPD).toString());
                row.createCell(colIndex++).setCellValue(newJan.get(MagicString.BRANCH_NUM_UPD).toString());
                row.createCell(colIndex++).setCellValue(newJan.get(MagicString.BRANCH_AMOUNT_UPD).toString());
                rowIndex++;
            }

            //カット
            XSSFSheet cutJanSheet = workbook.createSheet("カット");
            List<String> cutJanHeader = Lists.newArrayList("棚パターン名", "PTS名", "JAN", "商品名");
            XSSFRow cutJanRowHeader = cutJanSheet.createRow(0);
            for (int i = 0; i < cutJanHeader.size(); i++) {
                XSSFCell cell = cutJanRowHeader.createCell(i);
                cell.setCellValue(cutJanHeader.get(i));
            }

            rowIndex = 1;
            for (Map<String, Object> deleteJan : allDeleteList) {
                XSSFRow row = cutJanSheet.createRow(rowIndex);
                row.createCell(0).setCellValue(deleteJan.get(MagicString.PATTERN_NAME).toString());
                row.createCell(1).setCellValue(deleteJan.get(MagicString.PTS_NAME).toString());
                row.createCell(2).setCellValue(deleteJan.get(MagicString.JAN).toString());
                row.createCell(3).setCellValue(deleteJan.get("sku").toString());
                rowIndex++;
            }

            //棚パターン店舗対応関係
            XSSFSheet relationSheet = workbook.createSheet("棚パターン店舗対応関係");
            List<String> relationJanHeader = Lists.newArrayList("棚パターン名", "店舗CD", "店舗名");
            XSSFRow relationRowHeader = relationSheet.createRow(0);
            for (int i = 0; i < relationJanHeader.size(); i++) {
                XSSFCell cell = relationRowHeader.createCell(i);
                cell.setCellValue(relationJanHeader.get(i));
            }

            rowIndex = 1;
            for (Map<String, Object> branch : branchs) {
                XSSFRow row = relationSheet.createRow(rowIndex);
                row.createCell(0).setCellValue(branch.get("shelf_pattern_name").toString());
                row.createCell(1).setCellValue(Integer.parseInt(branch.get(MagicString.BRANCH).toString()));
                row.createCell(2).setCellValue(branch.get("branch_name").toString());
                rowIndex++;
            }

            workbook.write(fos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeZip(String parentFilePath, ZipOutputStream zos){
        File file = new File(parentFilePath);
        if(file.isDirectory()){
            for (File f : Objects.requireNonNull(file.listFiles())) {
                try(FileInputStream fis = new FileInputStream(f)) {
                    zos.putNextEntry(new ZipEntry(f.getName()));

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
        }

    }

    private void deleteDir(File dir) {
        try {
            if (dir.isDirectory()) {
                String[] children = dir.list();
                //ディレクトリ内のサブディレクトリを再帰的に削除
                for (int i = 0; i < children.length; i++) {
                    Files.deleteIfExists(new File(dir, children[i]).getAbsoluteFile().toPath());
                }
            }
            // ディレクトリが空です，削除可能
            Files.deleteIfExists(dir.getAbsoluteFile().toPath());
        } catch (IOException e) {
            logger.error("", e);
        }
    }
}
