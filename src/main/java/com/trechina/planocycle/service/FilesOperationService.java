package com.trechina.planocycle.service;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public interface FilesOperationService {
    /**
     * csv上传接口
     * @param multipartFile
     * @return
     * @throws IOException
     */
    Map<String,Object> CsvUpload(MultipartFile multipartFile,String path,String companyCd,String filename,
                                 String projectIds,String bucketNames) throws IOException;

    Map<String,Object> CsvUploadMulti(MultipartFile[] multipartFileList, String path,String companyCd,
                                      String projectIds,String bucketNames);

    /**
     * csv转excel，并下载
     * @param multipartFile
     * @return
     */
    Map<String, Object> csvConvertExcelDowlLoad(MultipartFile multipartFile, String productDownPath, HttpServletResponse response);
}
