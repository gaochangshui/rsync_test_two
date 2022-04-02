package com.trechina.planocycle.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.trechina.planocycle.entity.dto.PriorityOrderDataForCgiDto;
import com.trechina.planocycle.entity.dto.PriorityOrderMstDto;
import com.trechina.planocycle.entity.vo.ProductOrderAttrAndItemVO;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.PriorityOrderDataMapper;
import com.trechina.planocycle.mapper.ProductPowerParamMstMapper;
import com.trechina.planocycle.service.CommodityScoreMasterService;
import com.trechina.planocycle.service.PriorityOrderDataService;
import com.trechina.planocycle.service.PriorityOrderJanProposalService;
import com.trechina.planocycle.service.PriorityOrderMstService;
import com.trechina.planocycle.utils.ResultMaps;
import com.trechina.planocycle.utils.cgiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.*;

@Service
public class PriorityOrderDataServiceImpl implements PriorityOrderDataService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private HttpSession session;
    @Autowired
    private PriorityOrderDataMapper priorityOrderDataMapper;
    @Autowired
    private PriorityOrderJanProposalService priorityOrderJanProposalService;
    @Autowired
    private CommodityScoreMasterService commodityScoreMasterService;
    @Autowired
    private PriorityOrderMstService priorityOrderMstService;
    @Autowired
    private ProductPowerParamMstMapper productPowerParamMstMapper;
    @Autowired
    private cgiUtils cgiUtil;
    /**
     * 優先順位表初期設定データ
     *
     * @param priorityOrderDataForCgiDto
     * @return
     */
    @Override
    public Map<String, Object> getPriorityOrderData(PriorityOrderDataForCgiDto priorityOrderDataForCgiDto) {
        // cgiからデータを取得する
        logger.info("优先顺位表初期设定数据参数{}",priorityOrderDataForCgiDto);
        Map<String,Object> Data = new HashMap<>();
        //没次の2つのパラメータは直接ジャンプ画面を表しておらず、既存のデータがあります。
        if (priorityOrderDataForCgiDto.getMode().equals("priority_data")){
            PriorityOrderMstDto priorityOrderMstDto = new PriorityOrderMstDto();
            priorityOrderMstDto.setCompanyCd(priorityOrderDataForCgiDto.getCompany());
            priorityOrderMstDto.setPriorityOrderCd(priorityOrderDataForCgiDto.getPriorityNO());
            // 抽出attrbutecd
            String attrcd = priorityOrderDataMapper.selectPriorityAttrCd(priorityOrderDataForCgiDto.getCompany(),
                    priorityOrderDataForCgiDto.getPriorityNO());
            String attrValue = priorityOrderDataMapper.selectPriorityAttrValue(priorityOrderDataForCgiDto.getCompany(),
                    priorityOrderDataForCgiDto.getPriorityNO());

            logger.info("抽出的优先顺位表属性cd"+attrcd);
            priorityOrderMstDto.setAttributeCd(attrValue);
            Data = priorityOrderMstService.priorityDataWRFlag(priorityOrderMstDto,new String[0],"read");
            logger.info("优先顺位表既存cgi返回数据："+Data);
        } else {
            // イニシャルデータ
            String uuid = UUID.randomUUID().toString();
            ResourceBundle resourceBundle = ResourceBundle.getBundle("pathConfig");
            String path = resourceBundle.getString("PriorityOrderData");
            priorityOrderDataForCgiDto.setGuid(uuid);
            ProductOrderAttrAndItemVO productOrderAttrAndItemVO = productPowerParamMstMapper.selectAttrAndValue(
                    priorityOrderDataForCgiDto.getCompany(),priorityOrderDataForCgiDto.getProductPowerNo());
            priorityOrderDataForCgiDto.setAttributeCd(productOrderAttrAndItemVO.getAttrStr());
            priorityOrderDataForCgiDto.setItemFlg(productOrderAttrAndItemVO.getItemFlg());
            priorityOrderDataForCgiDto.setProductNmFlag(productOrderAttrAndItemVO.getItemFlg());
            priorityOrderDataForCgiDto.setWriteReadFlag("read");
            priorityOrderDataForCgiDto.setMode("priority_shoki");
            String tokenInfo = (String) session.getAttribute("MSPACEDGOURDLP");
            logger.info("调用cgi获取优先顺位表的参数：{}",priorityOrderDataForCgiDto);
            //再帰的にcgiを呼び出して、まずtaskidに行きます
            String result = cgiUtil.postCgi(path,priorityOrderDataForCgiDto,tokenInfo);
            logger.info("taskId返回：{}",result);
            String queryPath = resourceBundle.getString("TaskQuery");
            //taskIdを持って、再度cgiに運転状態/データの取得を要求する
            Data =cgiUtil.postCgiLoop(queryPath,result,tokenInfo);
            logger.info("优先顺位表cgi返回数据：{}",Data);

        }

        if (Data.get("data") !=null) {
            JSONArray datas = priorityOrderData((JSONArray) JSONArray.parse(Data.get("data").toString()));
            //検索
            List<Map<String,Object>> results = priorityOrderDataMapper.selectTempData(priorityOrderDataForCgiDto.getOrderCol(),
                    "public.priorityorder"+session.getAttribute("aud").toString());
            results.forEach(item->{
                if (item.get("rank_prop").toString().equals("99999999")){
                    item.put("rank_prop","カット");
                }
                if (item.get("rank_prop").toString().equals("99999998")){
                    item.put("rank_prop","_");
                }
                if (item.get("rank_upd").toString().equals("99999999")){
                    item.put("rank_upd","カット");
                }
                if (item.get("rank_upd").toString().equals("99999998")){
                    item.put("rank_upd","_");
                }
                if (item.get("rank").toString().equals("-1")){
                    item.put("rank","新規");
                }
                if (item.get("rank").toString().equals("99999998")){
                    item.put("rank","_");
                }
            });
            Map<String,Object> colMap = new HashMap<>();
            colMap.put("col",((JSONObject) datas.get(0)).keySet().stream().sorted());
            results.add(0, colMap);
            return ResultMaps.result(ResultEnum.SUCCESS,results);
        } else {
            return Data;
        }
    }
    // クエリー属性名
    @Override
    public List<Map<String,Object>> getAttrName(Integer productPowerCd){
        return priorityOrderDataMapper.selectPriorityAttrName(productPowerCd);
    }



    private JSONArray priorityOrderData(JSONArray datas) {
        List<Map<String,String>> keyNameList = new ArrayList<>();
        //時計のねじをとる
        colNameList(datas, keyNameList);
        logger.info("打印创建临时表前的表头{}", keyNameList);
        logger.info(keyNameList.toString());

        //データを一時保存するエンティティ・テーブル priorityorder+社員番号
        String tablename = "public.priorityorder"+session.getAttribute("aud").toString();
        logger.info("创建的表名{}",tablename);
        //初期化テーブル
        priorityOrderDataMapper.dropTempData(tablename);
        priorityOrderDataMapper.updateTempData(keyNameList,tablename);
        //データの書き込み
        priorityOrderDataMapper.insert(datas,keyNameList,tablename);
        return datas;
    }



    /**
     * 優先順位表反応ボタン抽出データ
     * @param colNameList
     * @return
     */
    @Override
    public Map<String, Object> getPriorityOrderDataUpd(List<String> colNameList,Integer priorityOrderCd) {
        logger.info("优先顺位表反应按钮抽出数据参数{}",colNameList);
        // すべてのカラム名を取得
        List<String> colName = priorityOrderDataMapper.selectTempColName("priorityorder"+session.getAttribute("aud").toString());

        logger.info("优先顺位表临时表抽出表头{}",colName);
        List<Map<String, Object>> result;
        if (!colNameList.isEmpty()) {
            result = priorityOrderDataMapper.selectTempDataAndMst(colNameList, colName,priorityOrderCd,
                    "public.priorityorder"+session.getAttribute("aud").toString());
            result.forEach(item ->{
                if (item.get("rank").toString().equals("-1") ){
                    item.put("rank", "新規");
                }
                if (item.get("rank_prop").toString().equals("99999999") ){
                    item.put("rank_prop", "カット");
                    item.put("rank_upd", "カット");
                }
            });
        } else {
            result = priorityOrderDataMapper.selectTempDataCol(null,priorityOrderCd,
                    "public.priorityorder"+session.getAttribute("aud").toString());
        }
        logger.info("优先顺位表临时表抽出数据{}",result);
        Map<String,Object> colMap = new HashMap<>();
        colMap.put("col", Arrays.stream(colName.stream().toArray()).sorted());
        result.add(0,colMap);
        return ResultMaps.result(ResultEnum.SUCCESS,result);
    }



    public void colNameList(JSONArray datas, List<Map<String, String>> keyNameList) {
        for (int i = 0; i < ((JSONObject) datas.get(0)).keySet().toArray().length; i++) {
            Map<String,String> maps = new HashMap<>();
            maps.put("name", (String) ((JSONObject) datas.get(0)).keySet().toArray()[i]);
            keyNameList.add(maps);
        }
    }
}
