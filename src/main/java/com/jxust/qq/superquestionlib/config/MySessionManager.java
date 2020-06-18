package com.jxust.qq.superquestionlib.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.util.StringUtils;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.Serializable;

@Slf4j
public class MySessionManager extends DefaultWebSessionManager {

    private static final String TOKEN_FIELD = "authToken";
    private static final String REFERENCED_SESSION_ID_SOURCE = "Stateless request";


    @Override
    protected Serializable getSessionId(ServletRequest request, ServletResponse response) {
        String token = WebUtils.toHttp(request).getHeader(TOKEN_FIELD);
        if (token == null || token.equals("")) {
            return super.getSessionId(request, response);
        }
        // 如果存在的话
        request.setAttribute(ShiroHttpServletRequest.
                REFERENCED_SESSION_ID_SOURCE, REFERENCED_SESSION_ID_SOURCE);
        //sessionId
        request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, token);
        request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);
        return token;
    }
}