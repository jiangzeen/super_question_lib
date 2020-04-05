package com.jxust.qq.superquestionlib.controller.admin;

import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.jxust.qq.superquestionlib.dao.mapper.admin.interfaces.AdminLoginToken;
import com.jxust.qq.superquestionlib.dao.mapper.admin.interfaces.AdminPassToken;
import com.jxust.qq.superquestionlib.dto.Result;
import com.jxust.qq.superquestionlib.dto.admin.EsPassword;
import com.jxust.qq.superquestionlib.service.admin.AdminitratorService;
import com.jxust.qq.superquestionlib.dto.admin.Adminitrator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

@RestController
@Slf4j
public class AdminController
{
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    private final AdminitratorService adminitratorService;
    public AdminController(AdminitratorService adminitratorService) {
        this.adminitratorService = adminitratorService;
    }
    //查找所有
    @AdminLoginToken
    @GetMapping("/admin/findAll")
    public  Result find_allAdmin()
    {
       JSONObject data=new JSONObject();
        List<Adminitrator> adminitrators=adminitratorService.findAdminitrators();
        data.put("adminitrators",adminitrators);
        if (adminitrators.isEmpty())
        {
            return Result.FAILD(data);
        }
        else
        {
            return Result.SUCCESS("查找成功",data);
        }
    }
    //注册
    @AdminLoginToken
    @PostMapping("/admin/register")
    public Result getRegister(@RequestBody Adminitrator adminitrator) {
            int status = adminitratorService.creatAdminitrator(adminitrator.getAdminName(),adminitrator.getAdminPassword());
            if (status < 0) {
                return Result.SERVERERROR();
            }
            JSONObject data = new JSONObject();
            data.put("adminName",adminitrator.getAdminName());
            return Result.SUCCESS("注册成功", data);
    }
    //登陆测试
    @AdminPassToken
    @PostMapping("/admin/login")
    public Result login(@RequestBody Adminitrator adminitrator1) {
        Adminitrator adminitrator2 = adminitratorService.findAdminitrator(adminitrator1.getAdminName());
        //成功查找到
        JSONObject data = new JSONObject();
        data.put("adminName",adminitrator1.getAdminName());
        if(adminitrator2!=null) {
            if(adminitrator2.getAdminPassword().equals(adminitratorService.encrypt(adminitrator1.getAdminName()
                    ,adminitrator1.getAdminPassword()))) {
                adminitratorService.modifyLoginTime(adminitrator1.getAdminName());
                String token=adminitratorService.getToken(adminitrator2,60);
                redisTemplate.opsForValue().set(String.valueOf(adminitrator2.getAdminId()),token);
                data.put("token",token);
                data.put("lastLoginTime",new Date());
                return Result.SUCCESS(data);
            }
            else
                return Result.FAILD(data);
        }
        else
        {
            return Result.FAILD(data);
        }
    }
    //注销
    @AdminLoginToken
    @GetMapping("/admin/logout")
    public Result logOut(HttpServletRequest request)
    {
        String token=request.getHeader("token");
        String adminId = JWT.decode(token).getAudience().get(0);
        Adminitrator adminitrator=adminitratorService.findAdminitratorById(Integer.parseInt(adminId));
        System.out.println(adminId);
        String new_token=adminitratorService.getToken(adminitrator,0);   //获取新令牌,旧的就销毁掉了
        redisTemplate.opsForValue().set(adminId,new_token);
        return Result.SUCCESS("注销成功");
    }
    //修改密码
    @AdminLoginToken
    @PostMapping("/admin/modify_password")
    public Result changPassword(@RequestBody EsPassword password, HttpServletRequest request)
    {
        String token=request.getHeader("token");
        String adminId = JWT.decode(token).getAudience().get(0);
        Adminitrator adminitrator=adminitratorService.findAdminitratorById(Integer.parseInt(adminId));
        String adminName=adminitrator.getAdminName();
        JSONObject data = new JSONObject();
        data.put("adminName",adminName);
       if(adminitratorService.findAdminitrator(adminName).getAdminPassword().equals(adminitratorService.encrypt(adminName,password.getOldPassword())))
        {
            adminitratorService.modifyPassword(adminName,password.getNewPassword());
            return Result.SUCCESS("修改成功",data);
        }
        else
        {
            return Result.FAILD(data);
        }
    }
    //删除管理员账号
    @AdminLoginToken
    @GetMapping("/admin/deleteByAdminName/{adminName}")
    public Result deleteAdmin(@PathVariable String adminName)
    {
        JSONObject data = new JSONObject();
        data.put("adminName",adminName);
        int status=adminitratorService.deleteByAdminName(adminName);
         if(status<0) return Result.SERVERERROR();
         else return  Result.SUCCESS("成功",data);
    }
    //删除管理员账号
    @AdminLoginToken
    @GetMapping("/admin/deleteById/{id}")
    public Result deleteAdmin(@PathVariable int id)
    {
        JSONObject data = new JSONObject();
        Adminitrator adminitrator=adminitratorService.findAdminitratorById(id);
        if(adminitrator!=null)
        data.put("adminName",adminitrator.getAdminName());
        int status=adminitratorService.deleteById(id);
        if(status<0) return Result.SERVERERROR();
        else return  Result.SUCCESS("成功",data);
    }
}

