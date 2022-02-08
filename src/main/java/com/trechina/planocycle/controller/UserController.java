package com.trechina.planocycle.controller;

import com.trechina.planocycle.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
    @RequestMapping("/planoCycle/Users")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 获取用户信息（目前只有usercd）
     * @return
     */
    @GetMapping("/getUserInfo")
    public Map<String,Object> getUserInfo(){
        return userService.getUserCd();
    }

    /**
     * 退出 清空session
     * @return
     */
    @GetMapping("/logOut")
    public Map<String,Object> logOut(){
        return userService.logOut();
    }
}
