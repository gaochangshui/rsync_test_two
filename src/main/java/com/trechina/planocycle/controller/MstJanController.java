package com.trechina.planocycle.controller;

import com.trechina.planocycle.entity.po.JanInfoList;
import com.trechina.planocycle.entity.vo.*;
import com.trechina.planocycle.mapper.MstJanMapper;
import com.trechina.planocycle.mapper.SysConfigMapper;
import com.trechina.planocycle.service.JanAttrService;
import com.trechina.planocycle.service.MstJanService;
import org.apache.tomcat.util.security.PrivilegedGetTccl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/planoCycleApi/MstJan")
public class MstJanController {

    @Autowired
    private MstJanService mstJanService;

    @Autowired
    private JanAttrService janAttrService;
    @Autowired
    private MstJanMapper mstJanMapper;
    @Autowired
    private SysConfigMapper sysConfigMapper;

    /**
     * janデータのチェック
     * @param janParamVO 検索条件
     * @return
     */
    @PostMapping("/getJanListCheck")
    public CheckVO getJanListCheck(@RequestBody JanParamVO janParamVO){
        return mstJanService.getJanListCheck(janParamVO);
    }

    /**
     * janデータの取得
     * @param downFlagVO
     * @return
     */
    @PostMapping("/getJanList")
    public Map<String, Object> getJanList(@RequestBody DownFlagVO downFlagVO) throws Exception {
        return mstJanService.getJanList(downFlagVO);
    }

    @PostMapping("/getJanListResult")
    public JanInfoVO getJanListResult(@RequestBody DownFlagVO downFlagVO, HttpServletResponse response) throws IOException {
        return mstJanService.getJanListResult(downFlagVO, response);
    }

    /**
     * janデータの取得
     * @param janInfoList
     * @return
     */
    @PostMapping("/getJanListInfo")
    public Map<String,Object> getJanListInfo(@RequestBody JanInfoList janInfoList){
        return mstJanService.getJanListInfo(janInfoList);
    }
    /**
     * janデータの保存
     * @param map 検索条件
     * @return
     */
    @PostMapping("/setJanListInfo")
    public Map<String,Object> setJanListInfo(@RequestBody Map<String,Object> map){
        return mstJanService.setJanListInfo(map);
    }

    /**
     * 属性の追加
     * @param productItemVO
     * @return
     */
    @PostMapping("/saveAttrInfo")
    public Map<String,Object> saveAttrInfo(@RequestBody ProductItemVO productItemVO){

        return "".equals(productItemVO.getValue())?
                janAttrService.saveProductItem(productItemVO) :janAttrService.updateAttrInfo(productItemVO);
    }

    /**
     * 属性の削除
     * @param productItemVO
     * @return
     */
    @DeleteMapping("/delAttrInfo")
    public Map<String,Object> delAttrInfo(@RequestBody ProductItemVO productItemVO){
        return janAttrService.delProductItem(productItemVO);
    }


    /**
     * 表示項目設定の取得
     *
     * @param janPresetAttribute
     * @return
     */
    @PostMapping("/getPresetAttribute")
    public Map<String, Object> getAttrName(@RequestBody JanPresetAttribute janPresetAttribute) {
        return mstJanService.getAttrName(janPresetAttribute);
    }

    /**
     * 表示項目設定のプリセット
     *
     * @param janPresetAttribute
     * @return
     */
    @PostMapping("/setPresetAttribute")
    public Map<String, Object> setPresetParam(@RequestBody JanPresetAttribute janPresetAttribute){
        return mstJanService.setPresetAttribute(janPresetAttribute);
    }

    /**
     * データ一括取込
     *
     * @param file
     * @param fileName
     * @return
     */
    @PostMapping("/uploadJanData")
    public Map<String, Object> uploadJanData(MultipartFile file,
                                             String fileName,
                                             String classCd,
                                             String commonPartsData,
                                             String companyCd){
        return mstJanService.uploadJanData(file, fileName, classCd, commonPartsData, companyCd);
    }

    @GetMapping("/getUploadJanDataResult")
    public Map<String, Object> getUploadJanDataResult(String taskId){
        return mstJanService.getUploadJanDataResult(taskId);
    }

    /**
     * JANデータ同期
     *
     * @return
     */
    @GetMapping("/syncJanData")
    public Map<String, Object> syncJanData(){
        String env = sysConfigMapper.selectSycConfig("env");
        return mstJanService.syncJanData(env);
    }
}
