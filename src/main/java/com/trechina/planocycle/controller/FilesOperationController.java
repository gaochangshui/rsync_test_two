package com.trechina.planocycle.controller;

import com.trechina.planocycle.service.FilesOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.ResourceBundle;

@RestController
@RequestMapping("/planoCycle/Files")
public class FilesOperationController {
    @Autowired
    private FilesOperationService filesOperationService;
    @Autowired
    private HttpSession session;
    @Value("${projectIds}")
    String projectIds;
    @Value("${bucketNames}")
    String bucketNames;
    @Value("${productDownPath}")
    String productDownPath;

    /**
     * 上传csv文件
     * @param multipartFile
     * @return
     * @throws IOException
     */
    @PostMapping("/CsvUpload")
    public Map<String,Object> CsvUpload(@RequestParam("file")MultipartFile multipartFile,
            @RequestParam("item")String filename,@RequestParam("companyCd") String companyCd) throws IOException {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("pathConfig");
        String path = resourceBundle.getString("csvPathCommodityPower")+session.getAttribute("aud").toString()+"/";

        return filesOperationService.CsvUpload(multipartFile,path,companyCd,filename,projectIds,bucketNames);
    }

    /**
     * 上传多个csv文件
     * @param multipartFileList
     * @return
     */
    @PostMapping("/csvUploadMulti")
    public Map<String,Object> CsvUploadShelf(@RequestParam("file") MultipartFile[] multipartFileList,
                                             @RequestParam("companyCd") String companyCd) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("pathConfig");
        String path = resourceBundle.getString("csvPathShelf")+session.getAttribute("aud").toString()+"/";
        return filesOperationService.CsvUploadMulti(multipartFileList,path,companyCd,projectIds,bucketNames);
    }

    /**
     *
     * @param multipartFile
     * @param response
     * @return
     */
    @PostMapping("/csvConvertExcelDowlLoad")
    public Map<String,Object> csvConvertExcelDowlLoad(@RequestParam("file")MultipartFile multipartFile, HttpServletResponse response){
        return filesOperationService.csvConvertExcelDowlLoad(multipartFile,productDownPath+session
                .getAttribute("aud").toString()+ File.separator,response);
    }

}
