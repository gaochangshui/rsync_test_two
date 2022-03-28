package com.trechina.planocycle.service.Impl;

import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.service.UserService;
import com.trechina.planocycle.utils.ResultMaps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    private Logger logger= LoggerFactory.getLogger(this.getClass());
    @Autowired
    private HttpSession session;
    @Override
    public Map<String, Object> getUserCd() {
        logger.info("登录的社员号：{}",session.getAttribute("aud"));
        return ResultMaps.result(ResultEnum.SUCCESS,session.getAttribute("aud"));
    }

    @Override
    public Map<String, Object> logOut() {
        Enumeration em = session.getAttributeNames();
        while (em.hasMoreElements()) {
            session.removeAttribute(em.nextElement().toString());
        }
        return ResultMaps.result(ResultEnum.SUCCESS);
    }
}
