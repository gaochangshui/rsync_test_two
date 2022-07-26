package com.trechina.planocycle.controller;

import com.trechina.planocycle.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
    @RequestMapping("/planoCycleApi/Users")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * ユーザー情報の取得(現在usercdのみ)
     * @return
     */
    @GetMapping("/getUserInfo")
    public Map<String,Object> getUserInfo(){
        return userService.getUserCd();
    }

    /**
     * クリアセッションを終了
     * @return
     */
    @GetMapping("/logOut")
    public Map<String,Object> logOut(){
        return userService.logOut();
    }
}
