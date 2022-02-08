package com.trechina.planocycle.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ResourceBundle;

@RestController
@RequestMapping("/planoCycle/Redirect")
public class RedirectController {

    @Value("${SystemPath}")
    private String SystemPaths;
    /**
     * 用于cclink 重定向
     * @param pathName
     * @return
     */
    @PostMapping ("/getRedirectUrl")
    public void getRedirectUrl(String pathName,  HttpServletResponse response) throws IOException {
        String[] path = pathName.split("\\?");
        response.sendRedirect(SystemPaths+path[0]);
    }
}
