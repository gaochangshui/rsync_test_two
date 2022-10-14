package com.trechina.planocycle.interceptor;

import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.utils.ResultMaps;
import com.trechina.planocycle.utils.jwtUtils;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class LoginInterceptor implements HandlerInterceptor {
    private static final Logger logger =  LoggerFactory.getLogger(LoginInterceptor.class);
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse response, Object o) throws Exception{
        Cookie[] cookies = httpServletRequest.getCookies();
        String token = "";
        //tokenの取得
        if (cookies == null) {
            returnJson(response,ResultMaps.result(ResultEnum.NOTFOUNTCOOKIE));
            return false;
        } else {
            for (Cookie cookie:cookies){
                if (cookie.getName().equals("MSPACEDGOURDLP")) {
                    token = cookie.getValue();
                }
            }
        }
        if (token == null || token.equals("")) {
            returnJson(response,ResultMaps.result(ResultEnum.NOTFOUNTCOOKIE));
            return false;
        }
        // 解析token
        jwtUtils util= new jwtUtils();
        JSONObject jwtInfo=util.getJwtInfo(token);
        // tokenの有効期限の判断
        int time = (int) (System.currentTimeMillis() / 1000);
        int tokenTime = (int) jwtInfo.get("exp");
        if(time>tokenTime){
            returnJson(response,ResultMaps.result(ResultEnum.TIMEOUT));
            return false;
        }

        // レコードセッション
        HttpSession httpSession = httpServletRequest.getSession();
        httpSession.setAttribute("aud",jwtInfo.get("aud"));
        httpSession.setAttribute("inCharge",jwtInfo.get("incharge"));
        httpSession.setAttribute("MSPACEDGOURDLP",token);
        return true;

    }

    private void returnJson(HttpServletResponse response, Map<String,Object> result) {
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(200);
        try (PrintWriter writer = response.getWriter()) {
            writer.print(new JSONObject(result));
        } catch (IOException e) {
            logger.info("error:",e);
        }

    }
}
