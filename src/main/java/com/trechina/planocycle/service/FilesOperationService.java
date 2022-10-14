package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.po.ShelfPtsDataVersion;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface FilesOperationService {
    /**
     * csvアップロードインタフェース
     *
     * @param multipartFile
     * @return
     * @throws IOException
     */
    //Map<String, Object> csvUpload(MultipartFile multipartFile, String path, String companyCd, String filename,
    //                              String projectIds, String bucketNames) throws IOException;

    Map<String, Object> csvUploadMulti(MultipartFile[] multipartFileList, String path, String companyCd,
                                       String projectIds, String bucketNames);

    @Transactional(rollbackFor = Exception.class)
    void savePtsData(ShelfPtsDataVersion ptsDataVersion, List<String[]> arrList1,
                     List<String[]> arrList2, List<String[]> arrList3, Integer ptsId, String companyCd, String authorCd, Date now);

    /**
     * csvはexcelを回転して、そしてダウンロードします
     *
     * @param multipartFile
     * @return
     */

    List<String[]> uploadCsvToList(MultipartFile multipartFile);
}
