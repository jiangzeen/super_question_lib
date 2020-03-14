package com.jxust.qq.superquestionlib.controller.admin;

import com.alibaba.fastjson.JSONObject;
import com.jxust.qq.superquestionlib.dto.Result;
import com.jxust.qq.superquestionlib.service.admin.AdminitratorService;
import com.jxust.qq.superquestionlib.dto.admin.Adminitrator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@Slf4j
public class AdminController
{
    private final AdminitratorService adminitratorService;
    public AdminController(AdminitratorService adminitratorService) {
        this.adminitratorService = adminitratorService;
    }
    //查找所有
    @PostMapping("/admin/findAll")
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
    @PostMapping("/admin/register")
    public Result getRegister(@RequestParam("adminName") String adminName,
                                  @RequestParam("password") String password) {
            int status = adminitratorService.creatAdminitrator(adminName, password);
            if (status < 0) {
                return Result.SERVERERROR();
            }
            JSONObject data = new JSONObject();
            data.put("adminName", adminName);
            return Result.SUCCESS("注册成功", data);
    }
    //登陆测试
    @PostMapping("/admin/login")
    public Result login(HttpServletRequest request,@RequestParam("adminName") String adminName, @RequestParam("password")
            String password) {
        Adminitrator adminitrator = adminitratorService.findAdminitrator(adminName);
        //成功查找到
        JSONObject data = new JSONObject();
        data.put("adminName", adminName);
        if(adminitrator!=null) {
            if(adminitrator.getAdminPassword().equals(adminitratorService.encrypt(adminName,password))) {
                adminitratorService.modifyLoginTime(adminName);
                request.getSession().setAttribute("adminName",adminName);
                data.put("lastLoginTime", adminitrator.getLastLoginTime());
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
    @PostMapping("/admin/logout")
    public Result logOut(HttpServletRequest request)
    {
        HttpSession session=request.getSession();
        System.out.println("hello");
        session.getAttribute("adminName");
        JSONObject data = new JSONObject();
        data.put("adminName",(String)session.getAttribute("adminName"));
        session.invalidate();
        return Result.SUCCESS("注销成功",data);
    }
    //修改密码
    @PostMapping("/admin/modify_password")
    public Result changPassword(@RequestParam("oldPassword")String oldPassword,
                                @RequestParam("newPassword")String newPassword,HttpServletRequest request)
    {
       HttpSession session=request.getSession();
       String adminName=(String) session.getAttribute("adminName");
        JSONObject data = new JSONObject();
        data.put("adminName",adminName);
       if(adminitratorService.findAdminitrator(adminName).getAdminPassword().equals(adminitratorService.encrypt(adminName,oldPassword)))
        {
            adminitratorService.modifyPassword(adminName,newPassword);
            return Result.SUCCESS("修改成功",data);
        }
        else
        {
            return Result.FAILD(data);
        }
    }
    //删除管理员账号
    @PostMapping("/admin/delete")
    public Result deleteAdmin(@RequestParam("adminName")String adminName)
    {
        JSONObject data = new JSONObject();
        data.put("adminName",adminName);
        int status=adminitratorService.deleteByAdminName(adminName);
         if(status<0) return Result.SERVERERROR();
         else return  Result.SUCCESS("成功",data);
    }
}

