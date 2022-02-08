package com.trechina.planocycle.service.Impl;

import com.trechina.planocycle.entity.po.PriorityOrderCommodityMust;
import com.trechina.planocycle.service.PriorityOrderBranchNumService;
import com.trechina.planocycle.utils.dataConverUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PriorityOrderBranchNumServiceImplTest {

    @Test
    void setPriorityOrderCommodityMust() {
        PriorityOrderBranchNumServiceImpl priorityOrderBranchNumService = new PriorityOrderBranchNumServiceImpl();
        List<PriorityOrderCommodityMust> priorityOrderCommodityMustList = new ArrayList<>();
        PriorityOrderCommodityMust priorityOrderCommodityMust = new PriorityOrderCommodityMust();
        priorityOrderCommodityMust.setCompanyCd("1");
        priorityOrderCommodityMust.setPriorityOrderCd(1);
        priorityOrderCommodityMust.setJan("1");
        priorityOrderCommodityMust.setBranch("1");
        priorityOrderCommodityMustList.add(priorityOrderCommodityMust);
        PriorityOrderCommodityMust priorityOrderCommodityMust1 = new PriorityOrderCommodityMust();
        priorityOrderCommodityMust1.setJan("2");
        priorityOrderCommodityMust1.setBranch("2");
        priorityOrderCommodityMustList.add(priorityOrderCommodityMust1);
        dataConverUtils dataConverUtils = new dataConverUtils();
        dataConverUtils.priorityOrderCommonMstInsertMethod(PriorityOrderCommodityMust.class,priorityOrderCommodityMustList,"12",1);
        assertNotNull(dataConverUtils.priorityOrderCommonMstInsertMethod(PriorityOrderCommodityMust.class,priorityOrderCommodityMustList,"12",1));
    }

}
