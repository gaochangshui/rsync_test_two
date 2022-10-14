package com.trechina.planocycle.service.impl;

import com.trechina.planocycle.entity.vo.JanParamVO;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.MstJanParamMapper;
import com.trechina.planocycle.service.MstJanParamService;
import com.trechina.planocycle.utils.ResultMaps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class MstJanParamServiceImpl implements MstJanParamService {
@Autowired
private  MstJanParamMapper mstJanParamMapper;

    public static List<Map<String,Object>> listToTree(List<Map<String,Object>> arr, String id, String pid, String child){
        List<Map<String,Object>> r = new ArrayList<>();
        Map<String,Object> hash = new HashMap<>();
        //配列をObject形式に変換し、keyは配列中のid
        for(int i=0;i<arr.size();i++){
            Map<String,Object> json =  arr.get(i);
            hash.put((String) json.get(id), json);
        }
        //結果セットのトラバース
        for(int j=0;j<arr.size();j++){
            //シングルレコード
            Map<String,Object> aVal =  arr.get(j);
            //1つのレコードにおけるpidの値であるkeyをhashから取り出す
            Map<String,Object> hashVP = (Map<String,Object>) hash.get(aVal.get(pid).toString());
            //記録されたpidが存在する場合は、親ノードがあり、子ノードのコレクションに追加されます
            if(hashVP!=null){
                //child属性があるかどうかをチェック
                if(hashVP.get(child)!=null){
                    List<Map<String,Object>> ch = (List) hashVP.get(child);
                    ch.add(aVal);
                    hashVP.put(child, ch);
                }else{
                    List<Map<String,Object>> ch = new ArrayList<>();
                    ch.add(aVal);
                    hashVP.put(child, ch);
                }
            }else{
                r.add(aVal);
            }
        }
        return r;
    }

    @Override
    public Map<String, Object> getAttributeTree(JanParamVO janParamVO) {
        log.info("jan分類のパラメータを取得するには:{}",janParamVO);
        String companyCd ="";
        String classCd = janParamVO.getCommonPartsData().getProdMstClass();
        if (janParamVO.getCommonPartsData().getProdIsCore().equals("1")) {
            companyCd= "1000";
        }else{
            companyCd= janParamVO.getCompanyCd();
        }
        List<Map<String,Object>> goodsAttrTree = mstJanParamMapper.getAttributeTree(companyCd,classCd);
        List<Map<String,Object>> jsonArray = listToTree(goodsAttrTree, "attrCd", "pid", "children");
        log.info("jan分類リターン",jsonArray);
        return ResultMaps.result(ResultEnum.SUCCESS,jsonArray);
    }

}
