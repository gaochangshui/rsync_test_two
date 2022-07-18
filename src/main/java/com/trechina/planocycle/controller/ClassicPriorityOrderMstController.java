package com.trechina.planocycle.controller;

import com.trechina.planocycle.entity.dto.PriorityOrderMstDto;
import com.trechina.planocycle.entity.dto.PriorityOrderPtsDownDto;
import com.trechina.planocycle.entity.vo.PriorityOrderPrimaryKeyVO;
import com.trechina.planocycle.service.ClassicPriorityOrderMstService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/planoCycle/priority/PriorityOrderMst")
public class ClassicPriorityOrderMstController {
    @Autowired
    private HttpSession session;
    @Autowired
    private ClassicPriorityOrderMstService priorityOrderMstService;
    @Value("${ptsDownPath}")
    String ptsDownPath;

    /**
     * 優先順位テーブルlistの取得
     * @param companyCd
     * @return
     */
    @GetMapping("/getPriorityOrderList")
    public Map<String,Object> getPriorityOrderList(String companyCd){
        return priorityOrderMstService.getPriorityOrderList(companyCd);
    }
    /**
     * 優先順位テーブルパラメータの保存
     * @param priorityOrderMstDto
     * @return
     */
    @PostMapping("/setPriorityOrderMst")
    public Map<String,Object> setPriorityOrderMst(@RequestBody PriorityOrderMstDto priorityOrderMstDto){
        return priorityOrderMstService.setPriorityOrderMst(priorityOrderMstDto);
    }

    /**
     * 優先順位テーブルパラメータの保存
     * @param priorityOrderMstDto
     * @return
     */
    @PostMapping("/setWorkPriorityOrderMst")
    public Map<String,Object> setWorkPriorityOrderMst(@RequestBody PriorityOrderMstDto priorityOrderMstDto){
        return priorityOrderMstService.setWorkPriorityOrderMst(priorityOrderMstDto);
    }

    /**
     * この企業に優先順位テーブルがあるかどうかのログインを取得します
     * @return
     */
    @GetMapping("/getPriorityOrderExistsFlg")
    public Map<String,Object> getPriorityOrderExistsFlg(){
        return priorityOrderMstService.getPriorityOrderExistsFlg();
    }

    /**
     * 優先順位テーブルrank属性の動的列の取得
     * @param companyCd
     * @param productPowerCd
     * @return
     */
    @GetMapping("/getRankAttr")
    public Map<String,Object> getRankAttr(String companyCd,Integer productPowerCd){
        return priorityOrderMstService.getRankAttr(companyCd,productPowerCd);
    }

    /**
     * 優先順位テーブルcdに基づく商品力点数テーブルcdの取得
     * @param priorityOrderCd
     * @return
     */
    @GetMapping("/getProductPowerCdForPriority")
    public Map<String,Object> getProductPowerCdForPriority(Integer priorityOrderCd){
        return priorityOrderMstService.getProductPowerCdForPriority(priorityOrderCd);
    }

    /**
     * ptsファイルのダウンロードを取得する
     * @param priorityOrderPtsDownDto
     * @param response
     * @return
     */
    @PostMapping("/getPtsFileDownLoad")
    public Map<String,Object> getPtsFileDownLoad(@RequestBody PriorityOrderPtsDownDto priorityOrderPtsDownDto, HttpServletResponse response) {
        return priorityOrderMstService.downloadPtsTask(priorityOrderPtsDownDto.getTaskId(), priorityOrderPtsDownDto.getCompany(),
                priorityOrderPtsDownDto.getPriorityNo(),
                priorityOrderPtsDownDto.getNewCutFlg(), priorityOrderPtsDownDto.getPtsVerison(), response);
//        return priorityOrderMstService.getPtsFileDownLoad(priorityOrderPtsDownDto,response,ptsDownPath);
    }

    @GetMapping("/getPtsFileDownLoadByTask")
    public void getPtsFileZipDownLoad(String taskId, HttpServletResponse response) throws IOException {
        priorityOrderMstService.packagePtsZip(taskId, response);
    }

    /**
     * すべての優先順位テーブル情報を削除
     * @param primaryKeyVO
     * @return
     */
    @DeleteMapping("/delPriorityOrderAllInfo")
    public Map<String,Object> delPriorityOrderAllInfo(@RequestBody PriorityOrderPrimaryKeyVO primaryKeyVO){
        return priorityOrderMstService.delPriorityOrderAllInfo(primaryKeyVO);
    }

}
