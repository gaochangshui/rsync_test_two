package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.po.CommoditySyncSet;
import com.trechina.planocycle.entity.vo.JanInfoVO;
import com.trechina.planocycle.entity.vo.JanParamVO;
import com.trechina.planocycle.entity.vo.CheckVO;
import com.trechina.planocycle.entity.vo.DownFlagVO;
import com.trechina.planocycle.entity.vo.JanPresetAttribute;
import com.trechina.planocycle.entity.po.JanInfoList;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

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
    Map<String, Object> getJanList(DownFlagVO downFlagVO) throws Exception;

    Map<String, Object> getJanListInfo(JanInfoList janInfoList);

    /**
     * 表示項目設定の取得
     *
     * @param janPresetAttribute
     * @return
     */
    Map<String, Object> getAttrName(JanPresetAttribute janPresetAttribute);

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

    /**
     * JANデータ同期
     *
     * @return
     */
    Map<String, Object> syncJanData(String env);

    @Transactional(rollbackFor = Exception.class)
    Map<String, Object> perSyncJanData(String companyCd, CommoditySyncSet commoditySyncSet, String existTable);

    JanInfoVO getJanListResult(DownFlagVO downFlagVO, HttpServletResponse response) throws IOException;

    Map<String, Object> getUploadJanDataResult(String taskId);
}
