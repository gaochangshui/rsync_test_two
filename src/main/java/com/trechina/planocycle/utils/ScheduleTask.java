package com.trechina.planocycle.utils;

import com.trechina.planocycle.service.TableTransferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduleTask {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private TableTransferService tableTransferService;
    @Scheduled(cron = "0 0 6 * * ?")
    public void MasterInfoSync(){
        logger.info("定時調度任務--attr表同期開始");
        tableTransferService.getAttrTransfer();
        logger.info("定時調度任務--area表同期開始");
        tableTransferService.getAreasTransfer();
        logger.info("定時調度任務--branch表同期開始");
        tableTransferService.getBranchsTransfer();
        logger.info("定時調度任務--jan表同期開始");
        tableTransferService.getJansTransfer();
        //logger.info("定時調度任務--janInfo表同期開始");
        //tableTransferService.getJanInfoTransfer();
    }

    public void syncZokuseiMst(){
        tableTransferService.syncZokuseiMst();
    }
}
