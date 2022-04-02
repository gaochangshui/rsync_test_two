package com.trechina.planocycle.service;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
    Map<String, Object> csvUpload(MultipartFile multipartFile, String path, String companyCd, String filename,
                                  String projectIds, String bucketNames) throws IOException;

    Map<String, Object> csvUploadMulti(MultipartFile[] multipartFileList, String path, String companyCd,
                                       String projectIds, String bucketNames);

    /**
     * csvはexcelを回転して、そしてダウンロードします
     *
     * @param multipartFile
     * @return
     */
    Map<String, Object> csvConvertExcelDowlLoad(MultipartFile multipartFile, String productDownPath, HttpServletResponse response);

    List<String[]> uploadCsvToList(MultipartFile multipartFile);
}
