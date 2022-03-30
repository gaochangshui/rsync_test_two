package com.trechina.planocycle.service.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.trechina.planocycle.entity.dto.*;
import com.trechina.planocycle.entity.po.*;
import com.trechina.planocycle.entity.vo.*;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.exception.BussinessException;
import com.trechina.planocycle.mapper.*;
import com.trechina.planocycle.service.*;
import com.trechina.planocycle.utils.ResultMaps;
import com.trechina.planocycle.utils.cgiUtils;
import com.trechina.planocycle.utils.sshFtpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PriorityOrderMstServiceImpl implements PriorityOrderMstService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Value("${skuPerPattan}")
    Long skuCountPerPattan;
    @Autowired
    private HttpSession session;
    @Autowired
    private PriorityOrderMstMapper priorityOrderMstMapper;
    @Autowired
    private PriorityOrderPatternMapper priorityOrderPatternMapper;
    @Autowired
    private CommodityScoreMasterService commodityScoreMasterService;
    @Autowired
    private CommonMstService commonMstService;
    @Autowired
    private ShelfPatternService shelfPatternService;
    @Autowired
    private PriorityOrderMstAttrSortService priorityOrderMstAttrSortService;
    @Autowired
    private PriorityOrderJanReplaceService priorityOrderJanReplaceService;
    @Autowired
    private PriorityOrderJanNewService priorityOrderJanNewService;
    @Autowired
    private PriorityOrderJanCardService priorityOrderJanCardService;
    @Autowired
    private PriorityOrderCatePakService priorityOrderCatePakService;
    @Autowired
    private PriorityOrderJanProposalService priorityOrderJanProposalService;
    @Autowired
    private PriorityOrderBranchNumService priorityOrderBranchNumService;
    @Autowired
    private PriorityOrderJanAttributeMapper priorityOrderJanAttributeMapper;
    @Autowired
    private ShelfPtsDataTanamstMapper shelfPtsDataTanamstMapper;
    @Autowired
    private WorkPriorityOrderRestrictSetMapper workPriorityOrderRestrictSetMapper;
    @Autowired
    private WorkPriorityOrderRestrictResultMapper workPriorityOrderRestrictResultMapper;
    @Autowired
    private WorkPriorityOrderRestrictRelationMapper workPriorityOrderRestrictRelationMapper;
    @Autowired
    private ProductPowerMstMapper productPowerMstMapper;
    @Autowired
    private WorkPriorityOrderResultDataMapper workPriorityOrderResultDataMapper;
    @Autowired
    private PriorityOrderMstAttrSortMapper priorityOrderMstAttrSortMapper;
    @Autowired
    private WorkPriorityOrderSpaceMapper workPriorityOrderSpaceMapper;
    @Autowired
    private WorkPriorityOrderSortMapper workPriorityOrderSortMapper;
    @Autowired
    private WorkPriorityOrderSortRankMapper workPriorityOrderSortRankMapper;
    @Autowired
    private WorkPriorityOrderMstMapper workPriorityOrderMstMapper;
    @Autowired
    private PriorityOrderJanNewMapper priorityOrderJanNewMapper;
    @Autowired
    private PriorityOrderJanReplaceMapper priorityOrderJanReplaceMapper;
    @Autowired
    private PriorityOrderJanCardMapper priorityOrderJanCardMapper;

    @Autowired
    private cgiUtils cgiUtil;
    @Autowired
    private ShelfPtsDataMapper shelfPtsDataMapper;
    @Autowired
    private PriorityOrderRestrictRelationMapper priorityOrderRestrictRelationMapper;
    @Autowired
    private PriorityOrderRestrictResultMapper priorityOrderRestrictResultMapper;
    @Autowired
    private PriorityOrderRestrictSetMapper priorityOrderRestrictSetMapper;
    @Autowired
    private PriorityOrderResultDataMapper priorityOrderResultDataMapper;
    @Autowired
    private PriorityOrderSpaceMapper priorityOrderSpaceMapper;
    @Autowired
    private PriorityOrderSortRankMapper priorityOrderSortRankMapper;
    @Autowired
    private PriorityOrderSortMapper priorityOrderSortMapper;
    @Autowired
    private PriorityOrderShelfDataMapper priorityOrderShelfDataMapper;
    @Autowired
    private ShelfPtsService shelfPtsService;
    @Autowired
    private PriorityOrderShelfDataService priorityOrderShelfDataService;

    /**
     * 获取优先顺位表list
     *
     * @param companyCd
     * @return
     */
    @Override
    public Map<String, Object> getPriorityOrderList(String companyCd) {
        logger.info("获取优先顺位表参数：{}", companyCd);
        List<PriorityOrderMst> priorityOrderMstList = priorityOrderMstMapper.selectByPrimaryKey(companyCd);
        logger.info("获取优先顺位表返回值：{}", priorityOrderMstList);
        return ResultMaps.result(ResultEnum.SUCCESS, priorityOrderMstList);
    }

    /**
     * 保存优先顺位表参数
     *
     * @param priorityOrderMstDto
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> setPriorityOrderMst(PriorityOrderMstDto priorityOrderMstDto) {
        logger.info("保存优先顺位表参数{}", priorityOrderMstDto);
        // check优先顺位表名称
        Integer count = priorityOrderPatternMapper.selectByPriorityOrderName(priorityOrderMstDto.getCompanyCd(),
                priorityOrderMstDto.getPriorityOrderName(),
                priorityOrderMstDto.getPriorityOrderCd());
        if (count > 0) {
            return ResultMaps.result(ResultEnum.NAMEISEXISTS);
        }
        // 把参数处理成两个表的的数据，insert
        try {
            logger.info("保存优先顺位表参数：{}", priorityOrderMstDto);
            PriorityOrderMst priorityOrderMst = new PriorityOrderMst();
            priorityOrderMst.setCompanyCd(priorityOrderMstDto.getCompanyCd());
            priorityOrderMst.setPriorityOrderCd(priorityOrderMstDto.getPriorityOrderCd());
            priorityOrderMst.setPriorityOrderName(priorityOrderMstDto.getPriorityOrderName());
            priorityOrderMst.setProductPowerCd(priorityOrderMstDto.getProductPowerCd());
            logger.info("保存优先顺位表mst表要保存的数据：{}", priorityOrderMst);
            priorityOrderMstMapper.deleteforid(priorityOrderMstDto.getPriorityOrderCd());
            priorityOrderMstMapper.insert(priorityOrderMst);
            List<PriorityOrderPattern> priorityOrderPatternList = new ArrayList<>();
            String[] shelfPatternList = priorityOrderMstDto.getShelfPatternCd().split(",");
            for (int i = 0; i < shelfPatternList.length; i++) {
                PriorityOrderPattern priorityOrderPattern = new PriorityOrderPattern();
                priorityOrderPattern.setCompanyCd(priorityOrderMstDto.getCompanyCd());
                priorityOrderPattern.setPriorityOrderCd(priorityOrderMstDto.getPriorityOrderCd());
                priorityOrderPattern.setShelfPatternCd(Integer.valueOf(shelfPatternList[i]));
                priorityOrderPatternList.add(priorityOrderPattern);
            }
            logger.info("保存优先顺位表pattert表要保存的数据：{}", priorityOrderPatternList);
            priorityOrderPatternMapper.deleteforid(priorityOrderMstDto.getPriorityOrderCd());
            priorityOrderPatternMapper.insert(priorityOrderPatternList);
            // 处理属性保存
            List<Map<String, Object>> array = (List<Map<String, Object>>) JSONArray.parse(priorityOrderMstDto.getRankAttributeList());
            attrSave(priorityOrderMstDto, array);
            String attrInfo = "";
            for (int i = 1; i <= array.size(); i++) {
                if (i < array.size()) {
                    attrInfo += array.get(i - 1).get("cd") + ",";
                } else {
                    attrInfo += "13,";
                }
            }
            String attrFinalInfo = attrInfo.substring(0, attrInfo.length() - 1);
            priorityOrderMstDto.setAttributeCd(attrFinalInfo);
            // 调用cgi保存数据
            cgiSave(priorityOrderMstDto);
            return ResultMaps.result(ResultEnum.SUCCESS);
        } catch (Exception e) {
            logger.info("报错:{}", e.getMessage());
            logger.error("保存优先顺位表报错：{}", e.getMessage());
            throw new BussinessException("保存优先顺位表报错");
        }
    }

    // 调用cgi保存数据
    private void cgiSave(PriorityOrderMstDto priorityOrderMstDto) {
        JSONArray jsonArray = (JSONArray) JSONArray.parse(String.valueOf(priorityOrderMstDto.getPriorityData()).replace(" ", ""));
        String[] res = new String[jsonArray.size()];

        for (int i = 0; i < jsonArray.size(); i++) {
            String rowStr = "";
            Map<String, Object> rowMaps = (Map<String, Object>) jsonArray.get(i);
            rowStr += ((Map) jsonArray.get(i)).get("jan_old").toString().trim().isEmpty() ? "_" : ((Map) jsonArray.get(i)).get("jan_old") + " ";
            rowStr += ((Map) jsonArray.get(i)).get("jan_new").toString().trim().isEmpty() ? "_" : ((Map) jsonArray.get(i)).get("jan_new") + " ";

            Object[] listKey = rowMaps.keySet().stream().sorted().toArray();
            for (int z = 0; z < listKey.length; z++) {
                if (listKey[z].toString().indexOf("attr") > -1) {
                    rowStr += ((Map) jsonArray.get(i)).get(listKey[z]) + " ";
                }
            }
            rowStr += ((Map) jsonArray.get(i)).get("pos_amount").toString().trim().isEmpty() ? "_" : ((Map) jsonArray.get(i)).get("pos_amount") + " ";
            rowStr += ((Map) jsonArray.get(i)).get("pos_before_rate").toString().trim().isEmpty() ? "_" : ((Map) jsonArray.get(i)).get("pos_before_rate") + " ";
            rowStr += ((Map) jsonArray.get(i)).get("branch_amount").toString().trim().isEmpty() ? "_" : ((Map) jsonArray.get(i)).get("branch_amount") + " ";
            rowStr += ((Map) jsonArray.get(i)).get("unit_price").toString().trim().isEmpty() ? "_" : ((Map) jsonArray.get(i)).get("unit_price") + " ";
            rowStr += ((Map) jsonArray.get(i)).get("unit_before_diff").toString().trim().isEmpty() ? "_" : ((Map) jsonArray.get(i)).get("unit_before_diff") + " ";
            rowStr += ((Map) jsonArray.get(i)).get("rank").toString().trim().isEmpty() ? "_" : ((Map) jsonArray.get(i)).get("rank") + " ";
            rowStr += ((Map) jsonArray.get(i)).get("branch_num").toString().trim().isEmpty() ? "_" : ((Map) jsonArray.get(i)).get("branch_num") + " ";
            rowStr += ((Map) jsonArray.get(i)).get("rank_prop").toString().trim().isEmpty() ? "_" : ((Map) jsonArray.get(i)).get("rank_prop") + " ";
            rowStr += ((Map) jsonArray.get(i)).get("rank_upd").toString().trim().isEmpty() ? "_" : ((Map) jsonArray.get(i)).get("rank_upd") + " ";
            rowStr += ((Map) jsonArray.get(i)).get("branch_num_upd").toString().trim().isEmpty() ? "_" : ((Map) jsonArray.get(i)).get("branch_num_upd") + " ";
            rowStr += ((Map) jsonArray.get(i)).get("pos_amount_upd").toString().trim().isEmpty() ? "_" : ((Map) jsonArray.get(i)).get("pos_amount_upd") + " ";
            rowStr += ((Map) jsonArray.get(i)).get("difference").toString().trim().isEmpty() ? "_" : ((Map) jsonArray.get(i)).get("difference") + " ";
            rowStr += ((Map) jsonArray.get(i)).get("sale_forecast").toString().trim().isEmpty() ? "_" : ((Map) jsonArray.get(i)).get("sale_forecast");
            res[i] = rowStr;
        }

        String wirteReadFlag = "write";
        Map<String, Object> results = priorityDataWRFlag(priorityOrderMstDto, res, wirteReadFlag);
    }

    // 处理属性保存
    private void attrSave(PriorityOrderMstDto priorityOrderMstDto, List<Map<String, Object>> array) {

        logger.info("获取rankAttributeCdList{}", array);
        List<PriorityOrderMstAttrSort> priorityOrderMstAttrSortList = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            PriorityOrderMstAttrSort priorityOrderMstAttrSort = new PriorityOrderMstAttrSort();
            priorityOrderMstAttrSort.setCompanyCd(priorityOrderMstDto.getCompanyCd());
            priorityOrderMstAttrSort.setPriorityOrderCd(priorityOrderMstDto.getPriorityOrderCd());
            if (array.get(i).get("value").toString().equals("mulit_attr")) {
                priorityOrderMstAttrSort.setValue(array.size());
                priorityOrderMstAttrSort.setCd(13);
            } else {
                priorityOrderMstAttrSort.setValue(Integer.valueOf(array.get(i).get("value").toString()));
                priorityOrderMstAttrSort.setCd(Integer.valueOf(array.get(i).get("cd").toString()));
            }
            if (array.get(i).get("sort").toString().equals("")) {
                priorityOrderMstAttrSort.setSort(0);
            } else {
                priorityOrderMstAttrSort.setSort(Integer.valueOf(array.get(i).get("sort").toString()));
            }
            priorityOrderMstAttrSortList.add(priorityOrderMstAttrSort);
        }
        priorityOrderMstAttrSortService.setPriorityAttrSort(priorityOrderMstAttrSortList);
    }

    /**
     * 读写priorityorderData
     *
     * @param priorityOrderMstDto
     * @param res
     * @param wirteReadFlag
     * @return
     */
    @Override
    public Map<String, Object> priorityDataWRFlag(PriorityOrderMstDto priorityOrderMstDto, String[] res, String wirteReadFlag) {
        PriorityOrderDataForCgiDto priorityOrderDataForCgiDto = new PriorityOrderDataForCgiDto();
        priorityOrderDataForCgiDto.setCompany(priorityOrderMstDto.getCompanyCd());
        String uuid = UUID.randomUUID().toString();
        priorityOrderDataForCgiDto.setGuid(uuid);
        priorityOrderDataForCgiDto.setMode("priority_data");
        priorityOrderDataForCgiDto.setPriorityNO(priorityOrderMstDto.getPriorityOrderCd());
        priorityOrderDataForCgiDto.setWriteReadFlag(wirteReadFlag);
        priorityOrderDataForCgiDto.setAttributeCd(priorityOrderMstDto.getAttributeCd());
        if (wirteReadFlag.equals("write")) {
            priorityOrderDataForCgiDto.setDataArray(res);
        }
        logger.info("保存优先顺位表给cgi的参数{}", priorityOrderDataForCgiDto);
        ResourceBundle resourceBundle = ResourceBundle.getBundle("pathConfig");
        String path = resourceBundle.getString("PriorityOrderData");
        String tokenInfo = (String) session.getAttribute("MSPACEDGOURDLP");
        try {
            Map<String, Object> resultCgi = new HashMap<>();
            //递归调用cgi，首先去taskid
            String result = cgiUtil.postCgi(path, priorityOrderDataForCgiDto, tokenInfo);
            logger.info("taskId返回：{}", result);
            String queryPath = resourceBundle.getString("TaskQuery");
            //带着taskId，再次请求cgi获取运行状态/数据
            resultCgi = cgiUtil.postCgiLoop(queryPath, result, tokenInfo);
            logger.info("保存优先顺位表结果：{}", resultCgi);
            return resultCgi;

        } catch (IOException e) {
            return null;
        }
    }


    /**
     * 获取登录这所在企业是否有优先顺位表
     *
     * @return
     */
    @Override
    public Map<String, Object> getPriorityOrderExistsFlg(String companyCd) {
        //List<String> companyCd = Arrays.asList(session.getAttribute("inCharge").toString().split(","));

        int result = priorityOrderMstMapper.selectPriorityOrderCount(companyCd);
        return ResultMaps.result(ResultEnum.SUCCESS, result);
    }

    /**
     * 优先顺位表获取rank属性的动态列
     *
     * @param companyCd
     * @param productPowerCd
     * @return
     */
    @Override
    public Map<String, Object> getRankAttr(String companyCd, Integer productPowerCd) {
        logger.info("优先顺位表获取rank属性的动态列：{},{}", companyCd, productPowerCd);
        Map<String, Object> result = new HashMap<>();
        commodityScoreMasterService.productPowerParamAttrName(companyCd, productPowerCd, result);
        return ResultMaps.result(ResultEnum.SUCCESS, result);
    }

    /**
     * 获取pts文件下载
     *
     * @param priorityOrderPtsDownDto
     * @param response
     * @return
     */
    @Override
    public Map<String, Object> getPtsFileDownLoad(PriorityOrderPtsDownDto priorityOrderPtsDownDto, HttpServletResponse response, String ptsDownPath) {
        logger.info("获取pts出力参数:{}", priorityOrderPtsDownDto);
        // 从cgi获取数据
        String uuid = UUID.randomUUID().toString();
        ResourceBundle resourceBundle = ResourceBundle.getBundle("pathConfig");
        String path = resourceBundle.getString("PriorityOrderData");
        priorityOrderPtsDownDto.setGuid(uuid);
        // rankAttributeCd
        List<Map<String, Object>> array = (List<Map<String, Object>>) JSONArray.parse(priorityOrderPtsDownDto.getRankAttributeList());
        String rankInfo = "";
        String attrInfo = "";
        StringBuilder rankInfoBuilder = new StringBuilder();
        StringBuilder attrInfoBuilder = new StringBuilder();
        String sortStr = "";
        String sort = "";
        for (int i = 1; i <= array.size(); i++) {
            for (int j = 0; j < array.size(); j++) {
                sortStr = String.valueOf(array.get(j).get("sort"));
                if (sortStr.equals("")) {
                    sort = "0";
                } else {
                    sort = sortStr;
                }
                if (String.valueOf(array.get(j).get("value")).equals("mulit_attr") && i == array.size()) {
                    rankInfoBuilder.append(array.get(j).get("cd") + "_" + i + "_" + sort + ",");
                    attrInfoBuilder.append("13,");
                } else {
                    if (!String.valueOf(array.get(j).get("value")).equals("mulit_attr") && i == Integer.valueOf(String.valueOf(array.get(j).get("value")))) {
                        rankInfo += array.get(j).get("cd") + "_" + i + "_" + sort + ",";
                        attrInfo += array.get(j).get("cd") + ",";
                    }
                }
            }
        }
        rankInfo = rankInfo + rankInfoBuilder.toString();
        attrInfo = attrInfo + attrInfoBuilder.toString();
        String rankFinalInfo = rankInfo.substring(0, rankInfo.length() - 1);
        String attrFinalInfo = attrInfo.substring(0, attrInfo.length() - 1);
        logger.info("处理完的rankAttributeCd{}", rankFinalInfo);
        priorityOrderPtsDownDto.setAttributeCd(attrFinalInfo);
        priorityOrderPtsDownDto.setRankAttributeCd(rankFinalInfo);
        // shelfPatternNoNm
        String resultShelf = shelfPatternService.getShePatternNoNm(priorityOrderPtsDownDto.getShelfPatternNo());
        logger.info("抽出完的shelfPatternNoNm{}", resultShelf);
        priorityOrderPtsDownDto.setShelfPatternNoNm(resultShelf.replace(" ", "*"));
        String tokenInfo = (String) session.getAttribute("MSPACEDGOURDLP");
        Map<String, Object> ptsPath = new HashMap<>();
        try {
            //递归调用cgi，首先去taskid
            String result = cgiUtil.postCgi(path, priorityOrderPtsDownDto, tokenInfo);
            logger.info("taskId返回：{}", result);
            String queryPath = resourceBundle.getString("TaskQuery");
            //带着taskId，再次请求cgi获取运行状态/数据
            ptsPath = cgiUtil.postCgiLoop(queryPath, result, tokenInfo);
            logger.info("pts路径返回数据：{}", ptsPath);

        } catch (IOException e) {
            logger.info("报错:",e);
        }
        String filePath = ptsPath.get("data").toString();
        if (filePath.length() > 1) {
            String[] fileName = filePath.split("/");

            sshFtpUtils sshFtp = new sshFtpUtils();
            try {
                logger.info("pts全路径输出：{},{}", ptsDownPath, ptsPath.get("data"));
                byte[] files = sshFtp.downLoafCgi(ptsDownPath + ptsPath.get("data").toString(), tokenInfo);
                logger.info("files byte:{}", files);
                response.setContentType("application/Octet-stream");
                logger.info("finename:{}", fileName[fileName.length - 1]);
                response.setHeader("Content-Disposition", "attachment;filename=" + java.net.URLEncoder.encode(fileName[fileName.length - 1], "UTF-8"));
                OutputStream outputStream = response.getOutputStream();
                outputStream.write(files);
                outputStream.close();
            } catch (IOException e) {
                logger.info("获取pts文件下载报错{}", e.getMessage());
            }
        }
        logger.info("下载成功");
        return null;
    }

    /**
     * 根据优先顺位表cd获取商品力点数表cd
     *
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Map<String, Object> getProductPowerCdForPriority(Integer priorityOrderCd) {
        logger.info("根据优先顺位表cd获取商品力点数表cd的参数{}", priorityOrderCd);
        Map<String, Object> productPowerCd = priorityOrderMstMapper.selectProductPowerCd(priorityOrderCd);
        logger.info("根据优先顺位表cd获取商品力点数表cd的返回值{}", priorityOrderCd);
        return ResultMaps.result(ResultEnum.SUCCESS, productPowerCd);
    }

    /**
     * 删除所有优先顺位表信息
     *
     * @param primaryKeyVO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> delPriorityOrderAllInfo(PriorityOrderPrimaryKeyVO primaryKeyVO) {
        String companyCd = primaryKeyVO.getCompanyCd();
        Integer priorityOrderCd = primaryKeyVO.getPriorityOrderCd();
        // 删除主表
        delPriorityOrderMst(primaryKeyVO);
        // 删除jan变list
        priorityOrderJanReplaceService.delJanReplaceInfo(companyCd, priorityOrderCd);
        // 删除新规商品list
        priorityOrderJanNewService.delriorityOrderJanNewInfo(companyCd, priorityOrderCd);
        // 删除card商品list
        priorityOrderJanCardService.delPriorityOrderJanCardInfo(companyCd, priorityOrderCd);
        // 删除catepak扩缩
        priorityOrderCatePakService.delPriorityOrderCatePakInfo(companyCd, priorityOrderCd);
        priorityOrderCatePakService.delPriorityOrderCatePakAttrInfo(companyCd, priorityOrderCd);
        // 删除jan变提案list
        priorityOrderJanProposalService.delPriorityOrderJanProposalInfo(companyCd, priorityOrderCd);
        // 删除必须和不可和中间表
        priorityOrderBranchNumService.delPriorityOrderCommodityMustInfo(companyCd, priorityOrderCd);
        priorityOrderBranchNumService.delPriorityOrderCommodityNotInfo(companyCd, priorityOrderCd);
        priorityOrderBranchNumService.delPriorityOrderBranchNumInfo(companyCd, priorityOrderCd);
        // 删除数据的排序
        priorityOrderMstAttrSortService.delPriorityAttrSortInfo(companyCd, priorityOrderCd);
        // 删除jannew的属性列
        priorityOrderJanAttributeMapper.deleteByPrimaryKey(companyCd, priorityOrderCd);
        // 删除棚pattern关联信息
        priorityOrderPatternMapper.deleteforid(priorityOrderCd);
        // 删除smart数据
        ResourceBundle resourceBundle = ResourceBundle.getBundle("pathConfig");
        String path = resourceBundle.getString("PriorityOrderData");
        String queryPath = resourceBundle.getString("TaskQuery");
        PriorityOrderDataForCgiDto priorityOrderDataForCgiDto = new PriorityOrderDataForCgiDto();
        // 调用cgi拿jan变提案list的数据
        String uuids = UUID.randomUUID().toString();
        priorityOrderDataForCgiDto.setMode("priority_delete");
        priorityOrderDataForCgiDto.setGuid(uuids);
        priorityOrderDataForCgiDto.setCompany(companyCd);
        priorityOrderDataForCgiDto.setPriorityNO(priorityOrderCd);
        String tokenInfo = (String) session.getAttribute("MSPACEDGOURDLP");
        //递归调用cgi，首先去taskid
        String resultJan = null;
        try {
            resultJan = cgiUtil.postCgi(path, priorityOrderDataForCgiDto, tokenInfo);
            logger.info("taskId返回：{}", resultJan);
            //带着taskId，再次请求cgi获取运行状态/数据
            Map<String, Object> result = cgiUtil.postCgiLoop(queryPath, resultJan, tokenInfo);
            logger.info("删除smart优先顺位表信息：{}", result);
        } catch (IOException e) {
            logger.info("报错:{}", e.getMessage());
            throw new BussinessException("报错");
        }
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    /**
     * 根据productpowercd查询关联的优先顺位表cd
     *
     * @param companyCd
     * @param productPowerCd
     * @return
     */
    @Override
    public String selPriorityOrderCdForProdCd(String companyCd, Integer productPowerCd) {
        return priorityOrderMstMapper.selectPriorityOrderCdForProdCd(companyCd, productPowerCd);
    }

    /**
     * 优先顺位表主表信息删除
     *
     * @param primaryKeyVO
     * @return
     */
    private Integer delPriorityOrderMst(PriorityOrderPrimaryKeyVO primaryKeyVO) {
        return priorityOrderMstMapper.deleteByPrimaryKey(primaryKeyVO.getCompanyCd(), primaryKeyVO.getPriorityOrderCd());
    }

    @Override
    public Map<String, Object> preCalculation(String companyCd, Long patternCd, Integer priorityOrderCd) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        int isUnset = 0;
        String authorCd = (String) session.getAttribute("aud");
        String getZokusei = "getZokusei";
        String setZokusei = "setZokusei";

        // 清空work表
        workPriorityOrderRestrictResultMapper.deleteByAuthorCd(companyCd, authorCd, priorityOrderCd);
        workPriorityOrderRestrictRelationMapper.deleteByAuthorCd(companyCd, authorCd, priorityOrderCd);
        // 1.通过patternCd查找pts的台段详情
        List<ShelfPtsDataTanamst> tanamstList = shelfPtsDataTanamstMapper.selectByPatternCd(patternCd);

        // 2.获取制约条件
        List<WorkPriorityOrderRestrictSet> workRestrictSetList = workPriorityOrderRestrictSetMapper.selectByAuthorCd(companyCd, authorCd, priorityOrderCd);

        // 3.将整台、段制约条件放到台段上
        List<WorkPriorityOrderRestrictSet> resultList = new ArrayList<>();
        WorkPriorityOrderRestrictSet restrictSet = null;
        WorkPriorityOrderRestrictSet halfRestrictSet1 = null;
        WorkPriorityOrderRestrictSet halfRestrictSet2 = null;
        // 整台制约
        Optional<WorkPriorityOrderRestrictSet> fullTaiSetOptional;
        WorkPriorityOrderRestrictSet fullTaiSet = null;
        // 整段制约
        Optional<WorkPriorityOrderRestrictSet> fullTanaSetOptional;
        WorkPriorityOrderRestrictSet fullTanaSet = null;
        // 半段制约
        String zokusei = null;
        // 3.1 查出半段设定的台段
        List<WorkPriorityOrderRestrictSet> halfRestrictSetList = workRestrictSetList.stream()
                .filter(obj -> obj.getTanaType() == 1 || obj.getTanaType() == 2).collect(Collectors.toList());
        // 3.2 循环台段数据
        Class<?> clazz = WorkPriorityOrderRestrictSet.class;
        for (ShelfPtsDataTanamst tanamst : tanamstList) {
            restrictSet = new WorkPriorityOrderRestrictSet();
            restrictSet.setCompanyCd(companyCd);
            restrictSet.setAuthorCd(authorCd);
            restrictSet.setTaiCd(tanamst.getTaiCd());
            restrictSet.setTanaCd(tanamst.getTanaCd());
            restrictSet.setTanaType((short) 0);
            // 3.2.1 整台制约
            fullTaiSetOptional = workRestrictSetList.stream()
                    .filter(obj -> obj.getTaiCd().equals(tanamst.getTaiCd()) && obj.getTanaCd().equals(0)).findFirst();
            if (fullTaiSetOptional.isPresent()) {
                fullTaiSet = fullTaiSetOptional.get();
                // [1,10]
                for (int i = 1; i <= 10; i++) {
                    zokusei = (String) clazz.getMethod(getZokusei + i).invoke(fullTaiSet);
                    // 属性不为空就覆盖上去
                    if (zokusei != null) {
                        clazz.getMethod(setZokusei + i, String.class).invoke(restrictSet, zokusei);
                    }
                }
            }
            // 3.2.2 整段制约
            fullTanaSetOptional = workRestrictSetList.stream()
                    .filter(obj -> obj.getTaiCd().equals(tanamst.getTaiCd())
                            && obj.getTanaCd().equals(tanamst.getTanaCd())
                            && obj.getTanaType() == 0)
                    .findFirst();
            if (fullTanaSetOptional.isPresent()) {
                fullTanaSet = fullTanaSetOptional.get();
                // [1,10]
                for (int i = 1; i <= 10; i++) {
                    zokusei = (String) clazz.getMethod(getZokusei + i).invoke(fullTanaSet);
                    // 属性不为空就覆盖上去
                    if (zokusei != null) {
                        clazz.getMethod(setZokusei + i, String.class).invoke(restrictSet, zokusei);
                    }
                }
            }
            // 3.2.3 有半段制约

            if (halfRestrictSetList.isEmpty()) {
                resultList.add(restrictSet);
            } else {
                List<WorkPriorityOrderRestrictSet> halfSetList = halfRestrictSetList.stream()
                        .filter(obj -> obj.getTaiCd().equals(tanamst.getTaiCd()) && obj.getTanaCd().equals(tanamst.getTanaCd()))
                        .collect(Collectors.toList());
                if (halfSetList.isEmpty()) {
                    resultList.add(restrictSet);
                } else {
                    // 不通过循环创建 两个半段是因为有可能只设置了一个半段。
                    halfRestrictSet1 = new WorkPriorityOrderRestrictSet();
                    halfRestrictSet2 = new WorkPriorityOrderRestrictSet();
                    BeanUtils.copyProperties(restrictSet, halfRestrictSet1);
                    BeanUtils.copyProperties(restrictSet, halfRestrictSet2);
                    halfRestrictSet1.setTanaType((short) 1);
                    halfRestrictSet2.setTanaType((short) 2);
                    //
                    for (int i = 0; i < halfSetList.size(); i++) {
                        int tanaType = halfSetList.get(i).getTanaType();
                        for (int j = 1; j <= 10; j++) {
                            zokusei = (String) clazz.getMethod(getZokusei + j).invoke(halfSetList.get(i));
                            // 属性不为空就覆盖上去
                            if (zokusei != null) {
                                if (tanaType == 1) {
                                    clazz.getMethod(setZokusei + j, String.class).invoke(halfRestrictSet1, zokusei);
                                } else {
                                    clazz.getMethod(setZokusei + j, String.class).invoke(halfRestrictSet2, zokusei);
                                }
                            }
                        }

                    }
                    resultList.add(halfRestrictSet1);
                    resultList.add(halfRestrictSet2);
                }
            }
        }
        // 判断台段是否有没有设定条件的
        List<WorkPriorityOrderRestrictSet> unsetList = resultList.stream()
                .filter(obj -> Boolean.TRUE.equals(obj.checkCondition()))
                .collect(Collectors.toList());
        if (!unsetList.isEmpty()) {
            logger.error("{}台段没有设置制约条件", unsetList.size());
            isUnset = 1;
        }

        // 去重获取唯一条件
        List<WorkPriorityOrderRestrictSet> distinctList = resultList.stream().filter(distinctByKey(obj -> Stream.of(obj.getZokusei1(), obj.getZokusei2(), obj.getZokusei3(), obj.getZokusei4()
                , obj.getZokusei5(), obj.getZokusei6(), obj.getZokusei7(), obj.getZokusei8(), obj.getZokusei9(), obj.getZokusei10()).toArray())).collect(Collectors.toList());
        if (!distinctList.isEmpty()) {
            List<WorkPriorityOrderRestrictResult> orderResultDataList = new ArrayList<>();
            WorkPriorityOrderRestrictResult orderResult = null;
            WorkPriorityOrderRestrictSet orderRestrictSet = null;
            for (int i = 0; i < distinctList.size(); i++) {
                orderRestrictSet = distinctList.get(i);
                orderResult = new WorkPriorityOrderRestrictResult();
                orderResult.setCompanyCd(companyCd);
                orderResult.setAuthorCd(authorCd);
                orderResult.setPriorityOrderCd(priorityOrderCd);
                orderResult.setRestrictCd((long) i + 1);
                orderResult.setZokusei1(orderRestrictSet.getZokusei1());
                orderResult.setZokusei2(orderRestrictSet.getZokusei2());
                orderResult.setZokusei3(orderRestrictSet.getZokusei3());
                orderResult.setZokusei4(orderRestrictSet.getZokusei4());
                orderResult.setZokusei5(orderRestrictSet.getZokusei5());
                orderResult.setZokusei6(orderRestrictSet.getZokusei6());
                orderResult.setZokusei7(orderRestrictSet.getZokusei7());
                orderResult.setZokusei8(orderRestrictSet.getZokusei8());
                orderResult.setZokusei9(orderRestrictSet.getZokusei9());
                orderResult.setZokusei10(orderRestrictSet.getZokusei10());
                orderResult.setTanaCnt((long) 0);
                orderResultDataList.add(orderResult);
            }


            // 将台段和条件进行关联
            List<WorkPriorityOrderRestrictRelation> orderRestrictRelationList = new ArrayList<>();
            WorkPriorityOrderRestrictRelation orderRestrictRelation = null;
            Optional<WorkPriorityOrderRestrictResult> resultOptional;
            WorkPriorityOrderRestrictResult restrictResult = null;
            for (WorkPriorityOrderRestrictSet set : resultList) {
                orderRestrictRelation = new WorkPriorityOrderRestrictRelation();
                orderRestrictRelation.setPriorityOrderCd(priorityOrderCd);
                orderRestrictRelation.setCompanyCd(companyCd);
                orderRestrictRelation.setAuthorCd(authorCd);
                orderRestrictRelation.setTaiCd(set.getTaiCd());
                orderRestrictRelation.setTanaCd(set.getTanaCd());
                orderRestrictRelation.setTanaType(set.getTanaType());
                // 从制约条件列表中检索相符的制约
                String condition = set.getCondition();
                resultOptional = orderResultDataList.stream().filter(obj -> condition.equals(obj.getCondition())).findFirst();
                if (resultOptional.isPresent()) {
                    restrictResult = resultOptional.get();
                    orderRestrictRelation.setRestrictCd(restrictResult.getRestrictCd());
                    // 台棚数量累加
                    long tanaCnt = restrictResult.getTanaCnt() + 1;
                    restrictResult.setTanaCnt(tanaCnt);
                    //每个棚固定13个品类, 扩大三倍取商品，全パターン直接用这些商品
                    restrictResult.setSkuCnt(tanaCnt * skuCountPerPattan * 3);
                }

                orderRestrictRelationList.add(orderRestrictRelation);
            }

            workPriorityOrderRestrictResultMapper.insertAll(orderResultDataList);
            workPriorityOrderRestrictRelationMapper.insertAll(orderRestrictRelationList);
        }
        return ResultMaps.result(ResultEnum.SUCCESS, isUnset);
    }

    /**
     * 用于对象去重
     *
     * @param keyExtractor 需要去重的属性
     * @param <T>
     * @return
     */
    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        //记录已有对象或者属性
        ConcurrentSkipListMap<Object, Boolean> skipListMap = new ConcurrentSkipListMap<>();
        //获取对象的属性值,且使用putIfAbsent判断存在则不添加到map而且返回数值不存在则添加返回null,value恒定为true
        //JSONObject.toJSONString(keyExtractor.apply(t)) 是为了解决null参数和对象比较的问题
        //在Stream distinct()中使用了支持null为key的hashSet来进行处理 java/util/stream/DistinctOps.java:90  但是没有解决对象比较的问题
        //所以虽然序列化消耗性能但是也没有更好的办法
        return t -> skipListMap.putIfAbsent(JSON.toJSONString(keyExtractor.apply(t)), Boolean.TRUE) == null;
    }

    /**
     * 自动计算
     *
     * @return
     */
    @Override
    public Map<String, Object> autoCalculation(String companyCd, Integer priorityOrderCd) {
        // TODO: 2200866



        String authorCd = session.getAttribute("aud").toString();


        //获取商品力点数表cd
        Integer productPowerCd = productPowerMstMapper.getProductPowerCd(companyCd, authorCd, priorityOrderCd);
        Integer patternCd = productPowerMstMapper.getpatternCd(companyCd, authorCd, priorityOrderCd);
        Integer minFaceNum = 1;

        //先按照社员号删掉work表的数据
        workPriorityOrderResultDataMapper.delResultData(companyCd, authorCd, priorityOrderCd);
        //获取制约条件
        List<WorkPriorityOrderRestrictResult> resultList = workPriorityOrderRestrictResultMapper.getResultList(companyCd, authorCd, priorityOrderCd);
        // 1.通过制约条件查找符合条件的商品
        for (WorkPriorityOrderRestrictResult workPriorityOrderRestrictResult : resultList) {
            List<ProductPowerDataDto> newList = new ArrayList<>();
            workPriorityOrderRestrictResult.setPriorityOrderCd(priorityOrderCd);
            List<ProductPowerDataDto> productPowerData = workPriorityOrderRestrictResultMapper.getProductPowerData(workPriorityOrderRestrictResult, companyCd, productPowerCd, authorCd);

            for (ProductPowerDataDto productPowerDatum : productPowerData) {

                if (productPowerDatum.getJanNew() != null) {
                    productPowerDatum.setJan(productPowerDatum.getJanNew());
                }
                if (productPowerDatum.getRankNewResult()!=null){
                    productPowerDatum.setRankResult(productPowerDatum.getRankNewResult());
                }
            }
            for (ProductPowerDataDto productPowerDatum : productPowerData) {
                newList.add(productPowerDatum);
                if (newList.size() % 1000 == 0 && !newList.isEmpty()) {
                    workPriorityOrderResultDataMapper.setResultDataList(newList, workPriorityOrderRestrictResult.getRestrictCd(), companyCd, authorCd, priorityOrderCd);
                    newList.clear();

                }
            }
            if (!newList.isEmpty()) {

                workPriorityOrderResultDataMapper.setResultDataList(newList, workPriorityOrderRestrictResult.getRestrictCd(), companyCd, authorCd, priorityOrderCd);
            }

        }

        String resultDataList = workPriorityOrderResultDataMapper.getResultDataList(companyCd, authorCd, priorityOrderCd);
        if (resultDataList == null) {
            return ResultMaps.result(ResultEnum.JANCDINEXISTENCE);
        }
        String[] array = resultDataList.split(",");
        //调用cgi
        Map<String, Object> Data = getFaceKeisanForCgi(array, companyCd, patternCd, authorCd);
        if (Data.get("data") != null && Data.get("data") != "") {
            String[] strResult = Data.get("data").toString().split("@");
            String[] strSplit = null;
            List<WorkPriorityOrderResultData> list = new ArrayList<>();
            WorkPriorityOrderResultData orderResultData = null;
            for (int i = 0; i < strResult.length; i++) {
                orderResultData = new WorkPriorityOrderResultData();
                strSplit = strResult[i].split(" ");
                orderResultData.setSalesCnt(Double.valueOf(strSplit[1]));
                orderResultData.setJanCd(strSplit[0]);


                list.add(orderResultData);
            }
            workPriorityOrderResultDataMapper.update(list, companyCd, authorCd, priorityOrderCd);


            //获取旧pts的平均值，最大值最小值
            FaceNumDataDto faceNum = productPowerMstMapper.getFaceNum(patternCd);
            minFaceNum = faceNum.getFaceMinNum();
            DecimalFormat df = new DecimalFormat("#.00");
            //获取salesCntAvg并保留两位小数
            Double salesCntAvg = productPowerMstMapper.getSalesCntAvg(companyCd, authorCd, priorityOrderCd);
            String format = df.format(salesCntAvg);
            salesCntAvg = Double.valueOf(format);

            List<WorkPriorityOrderResultData> resultDatas = workPriorityOrderResultDataMapper.getResultDatas(companyCd, authorCd, priorityOrderCd);

            Double d = null;
            for (WorkPriorityOrderResultData resultData : resultDatas) {
                resultData.setPriorityOrderCd(priorityOrderCd);
                if (resultData.getSalesCnt() != null) {
                    d = resultData.getSalesCnt() * faceNum.getFaceAvgNum() / salesCntAvg;

                    if (d > faceNum.getFaceMaxNum()) {
                        resultData.setFace(Long.valueOf(faceNum.getFaceMaxNum()));
                    } else if (d < faceNum.getFaceMinNum()) {
                        resultData.setFace(Long.valueOf(faceNum.getFaceMinNum()));
                    } else {
                        resultData.setFace(d.longValue());
                    }

                } else {
                    resultData.setFace(Long.valueOf(faceNum.getFaceMinNum()));
                }

            }
            workPriorityOrderResultDataMapper.updateFace(resultDatas, companyCd, authorCd);
        } else {
            return Data;
        }
        //按照属性重新排序
        this.getReorder(companyCd, priorityOrderCd,productPowerCd);
        //摆放商品
        this.setJan(companyCd, authorCd, priorityOrderCd, minFaceNum);

        //保存pts到临时表里
        shelfPtsService.saveWorkPtsData(companyCd, authorCd, priorityOrderCd);


        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    /**
     * 重新计算rank排序
     *
     * @param companyCd
     * @return
     */
    @Override
    public Map<String, Object> getReorder(String companyCd, Integer priorityOrderCd,Integer productPowerCd) {

        String aud = session.getAttribute("aud").toString();
        String colNmforMst = priorityOrderMstAttrSortMapper.getColNmforMst(companyCd, aud, priorityOrderCd);
        if (colNmforMst == null || colNmforMst.equals("")) {
            return ResultMaps.result(ResultEnum.SUCCESS);
        }
        String[] split = colNmforMst.split(",");
        List<PriorityOrderJanNewDto> reorder = null;
        List<WorkPriorityOrderResultData> reorder1 = null;
        if (split.length == 1) {
            //workPriorityOrderResultDataMapper.getReorder(companyCd,aud,priorityOrderCd,split[0],null)
          //  reorder = workPriorityOrderResultDataMapper.getAttrRank(companyCd, aud, priorityOrderCd, split[0], null);
            reorder1 = workPriorityOrderResultDataMapper.getReorder(companyCd, aud,productPowerCd, priorityOrderCd, split[0], null);

        } else {
           // reorder = workPriorityOrderResultDataMapper.getAttrRank(companyCd, aud, priorityOrderCd, split[0], split[1]);
            reorder1 = workPriorityOrderResultDataMapper.getReorder(companyCd, aud,productPowerCd, priorityOrderCd, split[0], split[1]);


        }
        //int i = 1;
        //for (PriorityOrderJanNewDto priorityOrderJanNewDto : reorder) {
        //    priorityOrderJanNewDto.setRank(i++);
        //}
        int j = 1;
        for (WorkPriorityOrderResultData workPriorityOrderResultData : reorder1) {
            workPriorityOrderResultData.setResultRank(j++);

        }

        workPriorityOrderResultDataMapper.setSortRank(reorder1, companyCd, aud, priorityOrderCd);
       // workPriorityOrderSortRankMapper.delete(companyCd, aud, priorityOrderCd);
     //   workPriorityOrderSortRankMapper.insert(companyCd, reorder, aud, priorityOrderCd);
        return ResultMaps.result(ResultEnum.SUCCESS, reorder);
    }

    /**
     * 新规时清空对应临时表所有信息
     *
     * @param companyCd
     * @return
     */
    @Override
    public void deleteWorkTable(String companyCd, Integer priorityOrderCd) {
        String authorCd = session.getAttribute("aud").toString();

        workPriorityOrderMstMapper.deleteByAuthorCd(companyCd, authorCd, priorityOrderCd);
        //清空RestrictRelation表
        workPriorityOrderRestrictRelationMapper.deleteByAuthorCd(companyCd, authorCd, priorityOrderCd);
        //清空RestrictResult表
        workPriorityOrderRestrictResultMapper.deleteByAuthorCd(companyCd, authorCd, priorityOrderCd);
        //清空RestrictSet表
        workPriorityOrderRestrictSetMapper.deleteByAuthorCd(companyCd, authorCd, priorityOrderCd);
        //清空ResultData表
        workPriorityOrderResultDataMapper.delResultData(companyCd, authorCd, priorityOrderCd);
        //清空Space表
        workPriorityOrderSpaceMapper.deleteByAuthorCd(companyCd, authorCd, priorityOrderCd);
        //清空Sort表
        workPriorityOrderSortMapper.delete(companyCd, authorCd, priorityOrderCd);
        //清空SortRank表
        workPriorityOrderSortRankMapper.delete(companyCd, authorCd, priorityOrderCd);
        //清空janNew表
        priorityOrderJanNewMapper.workDelete(companyCd, authorCd, priorityOrderCd);
        //清空jan_replace
        priorityOrderJanReplaceMapper.workDelete(companyCd, authorCd, priorityOrderCd);
        //清空work_priority_order_cut表
        priorityOrderJanCardMapper.workDelete(companyCd, priorityOrderCd, authorCd);
        //获取ptsCd
        Integer id = shelfPtsDataMapper.getId(companyCd, priorityOrderCd);
        //清空work_priority_order_pts_data
        shelfPtsDataMapper.deletePtsData(id);
        //清空work_priority_order_pts_data_taimst
        shelfPtsDataMapper.deletePtsTaimst(id);
        //清空work_priority_order_pts_data_tanamst
        shelfPtsDataMapper.deletePtsTanamst(id);
        //清空work_priority_order_pts_data_version
        shelfPtsDataMapper.deletePtsVersion(id);
        //清空work_priority_order_pts_data_jandata
        shelfPtsDataMapper.deletePtsDataJandata(id);



    }

    @Override
    public Map<String, Object> getFaceKeisanForCgi(String[] array, String companyCd, Integer shelfPatternNo, String authorCd) {
        PriorityOrderJanCgiDto priorityOrderJanCgiDto = new PriorityOrderJanCgiDto();
        priorityOrderJanCgiDto.setDataArray(array);
        String uuid = UUID.randomUUID().toString();
        priorityOrderJanCgiDto.setGuid(uuid);
        priorityOrderJanCgiDto.setMode("idposaverage_data");
        priorityOrderJanCgiDto.setCompany(companyCd);
        priorityOrderJanCgiDto.setShelfPatternNo(shelfPatternNo);
        priorityOrderJanCgiDto.setUsercd(authorCd);
        logger.info("计算给FaceKeisancgi的参数{}", priorityOrderJanCgiDto);
        ResourceBundle resourceBundle = ResourceBundle.getBundle("pathConfig");
        String path = resourceBundle.getString("PriorityOrderData");
        String tokenInfo = (String) session.getAttribute("MSPACEDGOURDLP");
        try {
            Map<String, Object> resultCgi = new HashMap<>();
            //递归调用cgi，首先去taskid
            String result = cgiUtil.postCgi(path, priorityOrderJanCgiDto, tokenInfo);
            logger.info("taskId返回：{}", result);
            String queryPath = resourceBundle.getString("TaskQuery");
            //带着taskId，再次请求cgi获取运行状态/数据
            resultCgi = cgiUtil.postCgiLoop(queryPath, result, tokenInfo);
            logger.info("保存优先顺位表结果：{}", resultCgi);
            return resultCgi;

        } catch (IOException e) {
            logger.error("", e);
        }
        return null;
    }


    /**
     * 保存所有work_priority_order_xxxx到表到实际表中
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> saveAllWorkPriorityOrder(PriorityOrderMstVO primaryKeyVO) {
        String authorCd = session.getAttribute("aud").toString();
        String companyCd = primaryKeyVO.getCompanyCd();
        Integer priorityOrderCd = primaryKeyVO.getPriorityOrderCd();
        String priorityOrderName = primaryKeyVO.getPriorityOrderName();

        try {
            //保存OrderMst，删除原数据
            priorityOrderMstMapper.deleteByAuthorCd(companyCd, authorCd, priorityOrderCd);
            priorityOrderMstMapper.insertBySelect(companyCd, authorCd, priorityOrderCd, priorityOrderName);

            //保存OrderRestrictRelation，删除原数据
            priorityOrderRestrictRelationMapper.deleteByAuthorCd(companyCd, authorCd, priorityOrderCd);
            priorityOrderRestrictRelationMapper.insertBySelect(companyCd, authorCd, priorityOrderCd);

            //保存OrderRestrictResult，删除原数据
            priorityOrderRestrictResultMapper.deleteByAuthorCd(companyCd, authorCd, priorityOrderCd);
            priorityOrderRestrictResultMapper.insertBySelect(companyCd, authorCd, priorityOrderCd);

            //保存OrderRestrictSet，删除原数据
            priorityOrderRestrictSetMapper.deleteByAuthorCd(companyCd, authorCd, priorityOrderCd);
            priorityOrderRestrictSetMapper.insertBySelect(companyCd, authorCd, priorityOrderCd);

            //保存ResultData，删除原数据
            priorityOrderResultDataMapper.deleteByAuthorCd(companyCd, authorCd, priorityOrderCd);
            priorityOrderResultDataMapper.insertBySelect(companyCd, authorCd, priorityOrderCd);

            //保存Space，删除原数据
            priorityOrderSpaceMapper.deleteByAuthorCd(companyCd, authorCd, priorityOrderCd);
            priorityOrderSpaceMapper.insertBySelect(companyCd, authorCd, priorityOrderCd);

            //保存cut，删除原数据
            priorityOrderJanCardMapper.deleteByAuthorCd(companyCd, priorityOrderCd, authorCd);
            priorityOrderJanCardMapper.insertBySelect(companyCd, priorityOrderCd, authorCd);

            //保存jan_new，删除原数据
            priorityOrderJanNewMapper.deleteByAuthorCd(companyCd, priorityOrderCd, authorCd);
            priorityOrderJanNewMapper.insertBySelect(companyCd, priorityOrderCd, authorCd);

            //保存jan_replace，删除原数据
            priorityOrderJanReplaceMapper.deleteByAuthorCd(companyCd, authorCd, priorityOrderCd);
            priorityOrderJanReplaceMapper.insertBySelect(companyCd, authorCd, priorityOrderCd);

            //保存sort，删除原数据
            priorityOrderSortMapper.deleteByAuthorCd(companyCd, authorCd, priorityOrderCd);
            priorityOrderSortMapper.insertBySelect(companyCd, authorCd, priorityOrderCd);

            //保存sort_rank，删除原数据
            priorityOrderSortRankMapper.deleteByAuthorCd(companyCd, authorCd, priorityOrderCd);
            priorityOrderSortRankMapper.insertBySelect(companyCd, authorCd, priorityOrderCd);

            //保存pts数据
            shelfPtsService.saveFinalPtsData(companyCd, authorCd, priorityOrderCd);


            //删除临时表中的数据
//            workPriorityOrderMstMapper.deleteByAuthorCd(companyCd, authorCd, priorityOrderCd);
//            workPriorityOrderRestrictRelationMapper.deleteByAuthorCd(companyCd, authorCd, priorityOrderCd);
//            workPriorityOrderRestrictResultMapper.deleteByAuthorCd(companyCd, authorCd, priorityOrderCd);
//            workPriorityOrderRestrictSetMapper.deleteByAuthorCd(companyCd, authorCd, priorityOrderCd);
//            workPriorityOrderResultDataMapper.delResultData(companyCd, authorCd, priorityOrderCd);
//            workPriorityOrderSpaceMapper.deleteByAuthorCd(companyCd, authorCd, priorityOrderCd);
//            workPriorityOrderSortMapper.delete(companyCd, authorCd, priorityOrderCd);
//            workPriorityOrderSortRankMapper.delete(companyCd, authorCd, priorityOrderCd);
//            priorityOrderJanNewMapper.workDelete(companyCd, authorCd, priorityOrderCd);
//            priorityOrderJanReplaceMapper.workDelete(companyCd, authorCd, priorityOrderCd);
//            priorityOrderJanCardMapper.workDelete(companyCd, priorityOrderCd, authorCd);
        } catch (Exception exception) {
            logger.error("保存临时表数据到实际表报错", exception);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultMaps.result(ResultEnum.FAILURE);
        }

        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    /**
     * 放置商品
     */
    @Override
    public boolean setJan(String companyCd, String authorCd, Integer priorityOrderCd, Integer minFace) {
        WorkPriorityOrderMst priorityOrderMst = workPriorityOrderMstMapper.selectByAuthorCd(companyCd, authorCd, priorityOrderCd);
        Long shelfPatternCd = priorityOrderMst.getShelfPatternCd();

        if (shelfPatternCd == null) {
            logger.info("shelfPatternCd:{}不存在", shelfPatternCd);
            return false;
        }

        List<WorkPriorityOrderRestrictRelation> workPriorityOrderRestrictRelations = workPriorityOrderRestrictRelationMapper.selectByAuthorCd(companyCd, authorCd, priorityOrderCd);
        List<PriorityOrderResultDataDto> workPriorityOrderResultData = workPriorityOrderResultDataMapper.getResultJans(companyCd, authorCd, priorityOrderCd);
        List<PtsTaiVo> taiData = shelfPtsDataMapper.getTaiData(shelfPatternCd.intValue());

        Short partitionFlag = Optional.ofNullable(priorityOrderMst.getPartitionFlag()).orElse((short) 0);
        Short partitionVal = Optional.ofNullable(priorityOrderMst.getPartitionVal()).orElse((short) 2);

        Map<String, List<PriorityOrderResultDataDto>> finalSetJanResultData =
                commonMstService.commSetJan(partitionFlag, partitionVal, taiData,
                        workPriorityOrderResultData, workPriorityOrderRestrictRelations, minFace);

        for (Map.Entry<String, List<PriorityOrderResultDataDto>> entry : finalSetJanResultData.entrySet()) {
            String taiTanaKey = entry.getKey();
            List<PriorityOrderResultDataDto> resultDataDtos = finalSetJanResultData.get(taiTanaKey);
            workPriorityOrderResultDataMapper.updateTaiTanaBatch(companyCd, priorityOrderCd, authorCd, resultDataDtos);
        }

        return true;
    }

    //TODO:10215814
    @Override
    public Map<String, Object> getPriorityOrderAll(String companyCd, Integer priorityOrderCd) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Integer id = shelfPtsDataMapper.getNewId(companyCd, priorityOrderCd);
        String aud = session.getAttribute("aud").toString();
        this.deleteWorkTable(companyCd, priorityOrderCd);
        priorityOrderJanCardMapper.setWorkForFinal(companyCd, priorityOrderCd, aud);
        priorityOrderJanReplaceMapper.setWorkForFinal(companyCd, priorityOrderCd, aud);
        priorityOrderJanNewMapper.setWorkForFinal(companyCd, priorityOrderCd, aud);
        workPriorityOrderMstMapper.setWorkForFinal(companyCd, priorityOrderCd, aud);
        workPriorityOrderRestrictRelationMapper.setWorkForFinal(companyCd, priorityOrderCd, aud);
        workPriorityOrderRestrictResultMapper.setWorkForFinal(companyCd, priorityOrderCd, aud);
        workPriorityOrderRestrictSetMapper.setWorkForFinal(companyCd, priorityOrderCd, aud);
        workPriorityOrderResultDataMapper.setWorkForFinal(companyCd, priorityOrderCd, aud);
        workPriorityOrderSortMapper.setWorkForFinal(companyCd, priorityOrderCd, aud);
        workPriorityOrderSortRankMapper.setWorkForFinal(companyCd, priorityOrderCd, aud);
        workPriorityOrderSpaceMapper.setWorkForFinal(companyCd, priorityOrderCd, aud);

        //获取ptsId

        shelfPtsDataMapper.insertWorkPtsData(companyCd, aud, priorityOrderCd);
        shelfPtsDataMapper.insertWorkPtsTaiData(companyCd, aud, id);
        shelfPtsDataMapper.insertWorkPtsTanaData(companyCd, aud, id);
        shelfPtsDataMapper.insertWorkPtsVersionData(companyCd, aud, id);
        shelfPtsDataMapper.insertWorkPtsJanData(companyCd, aud, id);
        Map<String, Object> map = new HashMap<>();
        //主表信息
        WorkPriorityOrderMstEditVo workPriorityOrderMst = workPriorityOrderMstMapper.getWorkPriorityOrderMst(companyCd, priorityOrderCd, aud);
        Integer shelfCd = workPriorityOrderMstMapper.getShelfName(workPriorityOrderMst.getShelfPatternCd().intValue());
        workPriorityOrderMst.setShelfCd(shelfCd);


        //space信息
        List<PriorityOrderAttrVO> workPriorityOrderSpace = priorityOrderSpaceMapper.workPriorityOrderSpace(companyCd, aud, priorityOrderCd);
        //set表信息
        List<PriorityOrderRestrictSet> workPriorityOrderRestrictSet = priorityOrderRestrictSetMapper.getPriorityOrderRestrict(companyCd, aud, priorityOrderCd);
        List<PriorityOrderAttrValueDto> attrValues = priorityOrderRestrictSetMapper.getAttrValues();
        Class clazz = PriorityOrderRestrictSet.class;
        for (int i = 1; i <= 10; i++) {
            Method getMethod = clazz.getMethod("get" + "Zokusei" + i);
            Method setMethod = clazz.getMethod("set" + "ZokuseiName" + i, String.class);
            for (PriorityOrderRestrictSet priorityOrderRestrictSet : workPriorityOrderRestrictSet) {
                for (PriorityOrderAttrValueDto attrValue : attrValues) {
                    if (getMethod.invoke(priorityOrderRestrictSet) != null && getMethod.invoke(priorityOrderRestrictSet).equals(attrValue.getVal()) && attrValue.getZokuseiId() == i) {
                        setMethod.invoke(priorityOrderRestrictSet, attrValue.getNm());
                    }
                }
            }
        }

        //商品力点数表信息
        Map<String, Object> taiNumTanaNum = shelfPtsService.getTaiNumTanaNum(workPriorityOrderMst.getShelfPatternCd().intValue(), priorityOrderCd);
        //获取陈列顺信息
        List<WorkPriorityOrderSortVo> workPriorityOrderSort = shelfPtsDataMapper.getDisplays(companyCd, aud, priorityOrderCd);
        //获取基本台棚别信息
        List<PriorityOrderPlatformShedDto> platformShedData = priorityOrderShelfDataMapper.getPlatformShedData(companyCd, aud, priorityOrderCd);
        //获取基本制约别信息
        Map<String, Object> restrictData = priorityOrderShelfDataService.getRestrictData(companyCd, priorityOrderCd);
        //获取pts详细数据信息
        Map<String, Object> ptsDetailData = shelfPtsService.getPtsDetailData(workPriorityOrderMst.getShelfPatternCd().intValue(), companyCd, priorityOrderCd);
        //商品力信息
        ProductPowerMstVo productPowerInfo = productPowerMstMapper.getProductPowerInfo(companyCd, workPriorityOrderMst.getProductPowerCd());
        Integer skuNum = productPowerMstMapper.getSkuNum(companyCd, workPriorityOrderMst.getProductPowerCd());
        productPowerInfo.setSku(skuNum);
        //获取janNew信息
        Map<String, Object> priorityOrderJanNew = priorityOrderJanNewService.getPriorityOrderJanNew(companyCd, priorityOrderCd, workPriorityOrderMst.getProductPowerCd());
        ////获取janCut信息
        List<PriorityOrderJanCardVO> priorityOrderJanCut = priorityOrderJanCardMapper.selectJanCard(companyCd,priorityOrderCd);
        //获取jan变信息
        List<PriorityOrderJanReplaceVO> priorityOrderJanReplace = priorityOrderJanReplaceMapper.selectJanInfo(companyCd,priorityOrderCd);
        //获取商品详细信息
        List<JanMstPlanocycleVo> janNewInfo = priorityOrderJanNewMapper.getJanNewInfo(companyCd);
        map.put("workPriorityOrderMst",workPriorityOrderMst);
        map.put("workPriorityOrderSpace",workPriorityOrderSpace);
        map.put("workPriorityOrderRestrictSet",workPriorityOrderRestrictSet);
        map.put("taiNumTanaNum",taiNumTanaNum.get("data"));
        map.put("workPriorityOrderSort",workPriorityOrderSort);
        map.put("platformShedData",platformShedData);
        map.put("restrictData",restrictData.get("data"));
        map.put("ptsDetailData",ptsDetailData.get("data"));
        map.put("productPowerInfo",productPowerInfo);
        map.put("priorityOrderJanNew",priorityOrderJanNew.get("data"));
        map.put("priorityOrderJanCut",priorityOrderJanCut);
        map.put("priorityOrderJanReplace",priorityOrderJanReplace);
        map.put("janNewInfo",janNewInfo);
        return ResultMaps.result(ResultEnum.SUCCESS,map);
    }

    @Override
    public Map<String, Object> checkOrderName(PriorityOrderMstVO priorityOrderMstVO) {
        String priorityOrderName = priorityOrderMstVO.getPriorityOrderName();
        Integer priorityOrderCd = priorityOrderMstVO.getPriorityOrderCd();

        Integer orderNameCount = priorityOrderMstMapper.selectByOrderName(priorityOrderName);
        int isEdit = priorityOrderMstMapper.selectByPriorityOrderCd(priorityOrderCd);

        if (isEdit > 0) {
            //编辑-更新名字
            priorityOrderMstMapper.updateOrderName(priorityOrderCd, priorityOrderName);
            return ResultMaps.result(ResultEnum.SUCCESS);
        }

        //新规
        if(orderNameCount>0){
            return ResultMaps.result(ResultEnum.NAMEISEXISTS);
        }

        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    @Override
    public Map<String, Object> deletePriorityOrderAll(PriorityOrderMstVO priorityOrderMstVO) {
        String aud = session.getAttribute("aud").toString();
        String companyCd = priorityOrderMstVO.getCompanyCd();
        Integer priorityOrderCd = priorityOrderMstVO.getPriorityOrderCd();
        //删除mst表
        priorityOrderMstMapper.logicDeleteByPriorityOrderCd(companyCd, aud, priorityOrderCd);
        //删除relation
        priorityOrderRestrictRelationMapper.logicDeleteByPriorityOrderCd(companyCd, aud, priorityOrderCd);
        //删除result表
        priorityOrderRestrictResultMapper.logicDeleteByPriorityOrderCd(companyCd, aud, priorityOrderCd);
        //删除set表
        priorityOrderRestrictSetMapper.logicDeleteByPriorityOrderCd(companyCd, aud, priorityOrderCd);
        //删除data表
        priorityOrderResultDataMapper.logicDeleteByPriorityOrderCd(companyCd, aud, priorityOrderCd);
        //删除sort表
        priorityOrderSortMapper.logicDeleteByPriorityOrderCd(companyCd, aud, priorityOrderCd);
        //删除rank表
        priorityOrderSortRankMapper.logicDeleteByPriorityOrderCd(companyCd, aud, priorityOrderCd);
        //删除space表
        priorityOrderSpaceMapper.logicDeleteByPriorityOrderCd(companyCd, aud, priorityOrderCd);
        return ResultMaps.result(ResultEnum.SUCCESS);
    }


}
