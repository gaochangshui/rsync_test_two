package com.trechina.planocycle.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/planoCycle/Redirect")
public class RedirectController {

    @Value("${SystemPath}")
    private String systemPaths;
    /**
     * cclinkリダイレクト用
     * @param pathName
     * @return
     */
    @PostMapping ("/getRedirectUrl")
    public void getRedirectUrl(String pathName,  HttpServletResponse response) throws IOException {
        String[] path = pathName.split("\\?");
        response.sendRedirect(systemPaths +path[0]);
    }
}
