package com.jxust.qq.superquestionlib.controller.filter;

import com.jxust.qq.superquestionlib.po.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import java.nio.charset.StandardCharsets;


@Slf4j
public class MyShiroFilter extends FormAuthenticationFilter {

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        Subject su = SecurityUtils.getSubject();
        log.info("subject中的sessionId:{}", su.getSession().getId());
        return su.isAuthenticated();
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        log.info("进入权限拒绝处理");
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        Result res = Result.FAILD(null);
        response.getWriter().println(res);
        return false;
    }
}
