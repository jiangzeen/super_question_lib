package com.jxust.qq.superquestionlib.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.Serializable;

@Slf4j
public class MySessionManager extends DefaultWebSessionManager {

    @Override
    protected Serializable getSessionId(ServletRequest request, ServletResponse response) {
        log.info("使用默认的session进行管理");
        return super.getSessionId(request, response);
    }
}