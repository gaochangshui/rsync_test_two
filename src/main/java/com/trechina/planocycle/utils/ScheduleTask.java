package com.trechina.planocycle.utils;

import com.trechina.planocycle.mapper.LogMapper;
import com.trechina.planocycle.mapper.MstBranchMapper;
import com.trechina.planocycle.mapper.ProductPowerDataMapper;
import com.trechina.planocycle.service.MstBranchService;
import com.trechina.planocycle.service.MstJanService;
import com.trechina.planocycle.service.TableTransferService;
import com.trechina.planocycle.service.impl.MstBranchServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScheduleTask {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private TableTransferService tableTransferService;
    @Autowired
    private LogMapper logMapper;
    @Autowired
    private MstJanService mstJanService;
    @Autowired
    private MstBranchService mstBranchService;
    @Autowired
    private ProductPowerDataMapper productPowerDataMapper;

    @Scheduled(cron = "0 0 7 * * ?")
    public void MasterInfoSync(){
//        logger.info("定時調度任務--attr表同期開始");
//        tableTransferService.getAttrTransfer();
//        logger.info("定時調度任務--area表同期開始");
//        tableTransferService.getAreasTransfer();
//        logger.info("定時調度任務--branch表同期開始");
//        tableTransferService.getBranchsTransfer();
//        logger.info("定時調度任務--jan表同期開始");
//        tableTransferService.getJansTransfer();
        //logger.info("定時調度任務--janInfo表同期開始");
        //tableTransferService.getJanInfoTransfer();
        mstJanService.syncJanData();
        mstBranchService.syncTenData();
        tableTransferService.syncZokuseiMst();
    }

    @Scheduled(cron = "0 5 0 * * ?")
    public void doDelLog(){
        logMapper.deleteLog();
        List<String> workTableName = productPowerDataMapper.getWorkTableName();
        for (String s : workTableName) {
            productPowerDataMapper.delWork(s);
        }

    }
}
