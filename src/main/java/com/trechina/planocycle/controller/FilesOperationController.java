package com.trechina.planocycle.controller;

import com.trechina.planocycle.service.FilesOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.ResourceBundle;

@RestController
@RequestMapping("/planoCycleApi/Files")
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
     * csvファイルのアップロード
     * @param multipartFile
     * @return
     * @throws IOException
     */
    //@PostMapping("/CsvUpload")
    //public Map<String,Object> csvUpload(@RequestParam("file")MultipartFile multipartFile,
    //        @RequestParam("item")String filename,@RequestParam("companyCd") String companyCd) throws IOException {
    //    ResourceBundle resourceBundle = ResourceBundle.getBundle("pathConfig");
    //    String path = resourceBundle.getString("csvPathCommodityPower")+session.getAttribute("aud").toString()+File.separator;
    //
    //    return filesOperationService.csvUpload(multipartFile,path,companyCd,filename,projectIds,bucketNames);
    //}

    /**
     * 複数のcsvファイルをアップロード
     * @param multipartFileList
     * @return
     */
    @PostMapping("/csvUploadMulti")
    public Map<String,Object> csvUploadShelf(@RequestParam("file") MultipartFile[] multipartFileList,
                                             @RequestParam("companyCd") String companyCd) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("pathConfig");
        String path = resourceBundle.getString("csvPathShelf")+session.getAttribute("aud").toString()+ File.separator;
        return filesOperationService.csvUploadMulti(multipartFileList,path,companyCd,projectIds,bucketNames);
    }



}
