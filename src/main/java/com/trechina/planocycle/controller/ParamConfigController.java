package com.trechina.planocycle.controller;

import com.trechina.planocycle.entity.vo.AllParamConfigVO;
import com.trechina.planocycle.entity.vo.GroupCompanyVO;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.service.GroupCompanyService;
import com.trechina.planocycle.service.ParamConfigService;
import com.trechina.planocycle.utils.ResultMaps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/planoCycleApi/ParamConfig")
public class ParamConfigController {
    @Autowired
    private ParamConfigService paramConfigService;
    @Autowired
    private GroupCompanyService groupCompanyService;

    @GetMapping("getParamConfigList")
    public Map<String,Object> getParamConfigList(@RequestParam(required = false) Integer showItemCheck)  {

        return paramConfigService.getParamConfigList(showItemCheck);
    }


    @PostMapping("getCompanyConfig")
    public Map<String,Object> getCompanyConfig(@RequestBody Map<String,Object> map)  {

        return paramConfigService.getCompanyConfig(map);
    }

    @GetMapping("getCompanyParam")
    public Map<String,Object> getCompanyParam(String companyCd)  {

        return paramConfigService.getCompanyParam(companyCd);
    }

    @PostMapping("setCompanyConfig")
    public Map<String,Object> setCompanyConfig(@RequestBody Map<String,Object> map)  {

        return paramConfigService.setCompanyConfig(map);
    }

    /**
     * エンタープライズ管理ページエンタープライズリストの取得
     *
     * @return
     */
    @GetMapping("getCompanyList")
    public Map<String,Object> getCompanyList()  {
        return paramConfigService.getCompanyList();
    }

    @GetMapping("/getAllParamConfig")
    public Map<String, Object> getAllParamConfig(){
        AllParamConfigVO allParamConfig = paramConfigService.getAllParamConfig();
        return ResultMaps.result(ResultEnum.SUCCESS, allParamConfig);
    }

    @PostMapping("/updateParamConfig")
    public Map<String, Object> updateParamConfig(@RequestBody AllParamConfigVO allParamConfigVO){
        return paramConfigService.updateParamConfig(allParamConfigVO);
    }

    @PostMapping("/saveGroupCompany")
    public Map<String, Object> saveGroupCompany(@RequestBody GroupCompanyVO groupCompanyVO){
        return groupCompanyService.saveGroupCompany(groupCompanyVO);
    }
}
