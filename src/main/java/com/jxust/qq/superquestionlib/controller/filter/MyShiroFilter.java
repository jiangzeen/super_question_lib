package com.jxust.qq.superquestionlib.controller.filter;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


@Slf4j
public class MyShiroFilter extends FormAuthenticationFilter {


    /**
     * 直接过滤可以访问的请求类型,OPTIONS请求直接放行
     */
    private static final String REQUEST_TYPE = "OPTIONS";


    /**
     * 根据subject来判断,判断权限
     * 也可以进行拓展,例如改成jwt token的方式进行验证
     * @param request
     * @param response
     * @param mappedValue
     * @return
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        if (((HttpServletRequest)request).getMethod().toUpperCase().equals(REQUEST_TYPE)) {
            return true;
        }
        return super.isAccessAllowed(request,response, mappedValue);
    }

    /**
     * 权限不足,采取的处理方法
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletResponse httpResp = (HttpServletResponse) response;
        httpResp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        httpResp.setHeader("Access-Control-Allow-Origin", "*");
        httpResp.setHeader("Content-type", "text/html;charset=UTF-8");
        httpResp.setStatus(HttpServletResponse.SC_OK);
        Map<String, Object> res = new HashMap<>();
        res.put("code", "201");
        res.put("msg", "无权限");
        httpResp.getWriter().println(new JSONObject(res));
        return false;
    }
}
