package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.dto.PriorityOrderResultDataDto;
import com.trechina.planocycle.entity.po.BasicAllPts;
import com.trechina.planocycle.entity.po.PriorityAllFaceVo;
import com.trechina.planocycle.entity.vo.PriorityAllVO;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface PriorityAllPtsService {

    void saveWorkPtsData(String companyCd, String authorCd, Integer priorityAllCd, Integer patternCd);

    void saveWorkPtsJanData(String companyCd, String authorCd, Integer priorityAllCd, Integer patternCd,
                            List<PriorityOrderResultDataDto> priorityOrderResultData, int isReOrder);

    Map<String, Object> getPtsDetailData(Integer patternCd, String companyCd, Integer priorityAllCd);

    void batchDownloadPtsData(PriorityAllVO priorityAllVO, HttpServletResponse response) throws IOException;


    Map<String, Object> getBasicAllPlatformShedJans(BasicAllPts basicAllPts);

    Map<String, Object> setFaceNumForPriorityAll(List<PriorityAllFaceVo> priorityAllFaceVo);
}
