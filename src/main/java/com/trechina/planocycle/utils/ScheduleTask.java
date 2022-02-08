package com.trechina.planocycle.utils;

import com.trechina.planocycle.service.TableTransferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduleTask {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private TableTransferService tableTransferService;
    @Scheduled(cron = "0 0 6 * * ?")
    public void MasterInfoSync(){
        logger.info("定时调度任务--attr表同步开始");
        tableTransferService.getAttrTransfer();
        logger.info("定时调度任务--area表同步开始");
        tableTransferService.getAreasTransfer();
        logger.info("定时调度任务--branch表同步开始");
        tableTransferService.getBranchsTransfer();
        logger.info("定时调度任务--jan表同步开始");
        tableTransferService.getJansTransfer();
    }
}
