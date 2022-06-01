package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.vo.PriorityAllVO;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public interface PriorityAllPtsService {

    void saveWorkPtsData(String companyCd, String authorCd, Integer priorityAllCd, Integer patternCd);

    Map<String, Object> getPtsDetailData(Integer patternCd, String companyCd, Integer priorityAllCd);

    void batchDownloadPtsData(PriorityAllVO priorityAllVO, HttpServletResponse response) throws IOException;
}
