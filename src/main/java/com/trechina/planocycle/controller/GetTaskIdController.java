package com.trechina.planocycle.controller;

import com.trechina.planocycle.service.GetTaskIdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(("/planoCycle/GetTaskId"))
public class GetTaskIdController {
    @Autowired GetTaskIdService getTaskIdService;
    @PostMapping("/getTaskId")
    public Map<String,Object> getTaskId(@RequestBody Map<String,Object> para){
        return getTaskIdService.getTaskId(para);
    }
}

