package com.trechina.planocycle.controller;

import com.trechina.planocycle.service.PriorityAllMstService;
import com.trechina.planocycle.service.PriorityOrderMstService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/planoCycle/PriorityAllMst")
public class PriorityAllMstController {
    @Autowired
    private HttpSession session;

    @Autowired
    private PriorityAllMstService priorityAllMstService;

    /**
     * 自動計算Main
     * @param companyCd
     * @return
     */
    @GetMapping("/getPriorityAllList")
    public Map<String,Object> autoCalculation(String companyCd){
        return null;
    }

}
