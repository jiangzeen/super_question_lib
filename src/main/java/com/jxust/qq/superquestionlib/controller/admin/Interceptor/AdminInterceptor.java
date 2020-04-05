package com.jxust.qq.superquestionlib.controller.admin.Interceptor;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.jxust.qq.superquestionlib.dao.mapper.admin.interfaces.AdminLoginToken;
import com.jxust.qq.superquestionlib.dao.mapper.admin.interfaces.AdminPassToken;
import com.jxust.qq.superquestionlib.dto.admin.Adminitrator;
import com.jxust.qq.superquestionlib.service.admin.AdminitratorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
@Slf4j
public class AdminInterceptor implements HandlerInterceptor
{
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Autowired
    private AdminitratorService adminitratorService;
    /**
     * 请求处理之前调用
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
    {
        String token=request.getHeader("token");
        if(!(handler instanceof HandlerMethod))
        {
            return true;
        }
        HandlerMethod handlerMethod=(HandlerMethod)handler;
        Method method=handlerMethod.getMethod();
        //检查是否有passToken注释
        if(method.isAnnotationPresent(AdminPassToken.class))
        {
            AdminPassToken adminPassToken=method.getAnnotation(AdminPassToken.class);
            if(adminPassToken.required())
            {
                return true;
            }
        }
        //检查有没有需要用户权限的注释
        if(method.isAnnotationPresent(AdminLoginToken.class))
        {
            AdminLoginToken adminLoginToken=method.getAnnotation(AdminLoginToken.class);
            if(adminLoginToken.required())
            {
                if(token==null)
                {
                    throw new RuntimeException("无token请重新登陆");
                }
            }
            //获取token中的adminId
            String adminId;
            try {
                adminId = JWT.decode(token).getAudience().get(0);
            }
            catch (JWTDecodeException j)
            {
                throw new RuntimeException("401,无权限访问");
            }
            Adminitrator adminitrator=adminitratorService.findAdminitratorById(Integer.parseInt(adminId));
            if(adminitrator==null) {throw new RuntimeException("用户不存在,请重新登陆");}
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(adminitrator.getAdminPassword())).build();
            try {
                jwtVerifier.verify(token);
            }
            catch (JWTVerificationException j) {
                throw new RuntimeException("token已经超时");
            }
                String token1=redisTemplate.opsForValue().get(adminId);
                if(token1==null||!token1.equals(token))
                {
                   throw new RuntimeException("token已被销毁");
                }

        }
        return true;
    }
}
