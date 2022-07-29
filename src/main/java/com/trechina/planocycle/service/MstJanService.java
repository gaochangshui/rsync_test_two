package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.dto.EnterpriseAxisDto;
import com.trechina.planocycle.entity.vo.JanInfoVO;
import com.trechina.planocycle.entity.vo.JanParamVO;
import com.trechina.planocycle.entity.vo.CheckVO;
import com.trechina.planocycle.entity.vo.DownFlagVO;
import com.trechina.planocycle.entity.vo.JanPresetAttribute;
import com.trechina.planocycle.entity.po.JanInfoList;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public interface MstJanService {

    /**
     * janデータのチェック
     * @param janParamVO 検索条件
     * @return
     */
    CheckVO getJanListCheck(JanParamVO janParamVO);

    /**
     * janデータの取得
     * @param downFlagVO
     * @return
     */
    JanInfoVO getJanList(DownFlagVO downFlagVO, HttpServletResponse response);

    Map<String, Object> getJanListInfo(JanInfoList janInfoList);

    /**
     * 表示項目設定の取得
     *
     * @param enterpriseAxisDto
     * @return
     */
    Map<String, Object> getAttrName(EnterpriseAxisDto enterpriseAxisDto);

    /**
     * 表示項目設定のプリセット
     *
     * @param janPresetAttribute
     * @return
     */
    Map<String, Object> setPresetAttribute(JanPresetAttribute janPresetAttribute);

    Map<String, Object> setJanListInfo(Map<String, Object> map);

    /**
     * データ一括取込
     * @param file
     * @param fileName
     * @param classCd
     * @return
     */
    Map<String, Object> uploadJanData(MultipartFile file, String fileName, String classCd,
                                      String commonPartsData, String companyCd);
}
