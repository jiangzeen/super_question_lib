package com.jxust.qq.superquestionlib.controller.admin;

import com.alibaba.fastjson.JSONObject;
import com.jxust.qq.superquestionlib.dto.Result;
import com.jxust.qq.superquestionlib.dto.admin.EsQuestion;
import com.jxust.qq.superquestionlib.dto.admin.EsUser;
import com.jxust.qq.superquestionlib.service.UserService;
import com.jxust.qq.superquestionlib.service.admin.EsHotExamService;
import com.jxust.qq.superquestionlib.service.admin.EsUserService;
import com.jxust.qq.superquestionlib.util.admin.DateFormat;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@Slf4j
public class EsUserController
{
     private final EsUserService userService;
     private final UserService userService2;

    public EsUserController(EsUserService userService, UserService userService2) {
        this.userService = userService;
        this.userService2 = userService2;
    }
    @PostMapping("admin/user/fuzzyQuery")
    public Result fuzzyQuery(@Param("queryString") String queryString, @Param("pagenum") int pagenum, @Param("pagesize") int pagesize)
    {
        List<EsUser> esUserList=userService.matchUser(queryString,pagenum,pagesize);
        JSONObject data=new JSONObject();
        data.put("esUsers",esUserList);
        data.put("pagesum",userService.pagesum);
        if(!esUserList.isEmpty())
        {
            return Result.SUCCESS(data);
        }
        else
            return Result.FAILD(data);
    }
    @PostMapping("admin/user/findById")
    public Result findById(@Param("id")long id)
    {
        EsUser esUser=null;
        esUser=userService.termUser(id);
        JSONObject data=new JSONObject();
        data.put("esUser",esUser);
        data.put("pagesum",userService.pagesum);
        if(esUser!=null)
        {
            return Result.SUCCESS(data);
        }
        else
            return Result.FAILD(data);
    }
    //条件查找
    @PostMapping("admin/user/conditionQuery")
    public Result conditionQuery(@Param("queryString") String queryString, @Param("dir") boolean dir,
                                 @Param("pagenum") int pagenum, @Param("pagesize") int pagesize,
                                 @Param("schoolInfoId") int schoolInfoId, @Param("sex") int sex,
                                 @Param("creatTimeBegin") String createTimeBegin,@Param("creatTimeEnd") String createTimeEnd)
    {
        List<EsUser> esUserList=userService.boolHotUser(queryString,dir,pagenum,
                pagesize,
                schoolInfoId,sex,
                DateFormat.DateFormatParse(createTimeBegin),DateFormat.DateFormatParse(createTimeEnd));
        JSONObject data=new JSONObject();
        data.put("esUsers",esUserList);
        data.put("pagesum",EsHotExamService.pagesum);
        if(!esUserList.isEmpty())
        {
            return Result.SUCCESS(data);
        }
        else
            return Result.FAILD(data);
    }
    //用户登陆统计图
    @PostMapping("admin/user/chart")
    public Result chart(@Param("period")int period)
    {
        JSONObject data=userService.aggregationBuilder(period);
        return Result.SUCCESS(data);
    }


    @PostMapping("admin/user/deleteById")
    public Result deleteById(@Param("userId")long userId)
    {
        int status=userService.deleteById(userId);
        JSONObject data = new JSONObject();
        if(status<=0) return Result.SERVERERROR();
        else return Result.SUCCESS(data);
    }
    @PostMapping("admin/user/create")
    public Result create(@Param("userName")String userName,
                         @Param("userPassword")String userPassword,
                         @Param("userAvatar")String userAvatar,
                         @Param("userNick")String userNick,
                         @Param("userLastLoginTime")String userLastLoginTime,
                         @Param("userCreateTime")String userCreateTime,
                         @Param("userSex")String userSex,
                         @Param("userSchoolId")String userSchoolId)
    {
        EsUser user=new EsUser();
        if(userName==null) userName="";
        user.setUserName(userName);
        if(userPassword==null) userPassword="0";
        user.setUserPassword(userPassword);
        user.setUserPassword(userService2.encrypt(user.getUserName(),user.getUserPassword()));
        if(userAvatar==null) userAvatar="";
        user.setUserAvatar(userAvatar);
        if(userNick==null) userNick="";
        user.setUserNick(userNick);
        if(userLastLoginTime==null) user.setUserLastLoginTime(new Date());
        else user.setUserLastLoginTime(DateFormat.DateFormatParse(userLastLoginTime));
        if(userCreateTime==null) user.setUserCreateTime(new Date());
        else user.setUserLastLoginTime(DateFormat.DateFormatParse(userCreateTime));
        if(userSex==null) userSex="0";
        user.setUserSex(Integer.parseInt(userSex));
        if(userSchoolId==null) userSchoolId="0";
        user.setUserSchoolId(Integer.parseInt(userSchoolId));
        int status=userService.createUser(user);
        JSONObject data=new JSONObject();
        if (status <= 0) {
            return Result.SERVERERROR();
        }
        else{
            return Result.SUCCESS(data);
        }
    }
    //修改
    @PostMapping("admin/user/updateById")
    public Result updateById(@Param("userId")String userId,
                             @Param("userName")String userName,
                             @Param("userPassword")String userPassword,
                             @Param("userAvatar")String userAvatar,
                             @Param("userNick")String userNick,
                             @Param("userLastLoginTime")String userLastLoginTime,
                             @Param("userCreateTime")String userCreateTime,
                             @Param("userSex")String userSex,
                             @Param("userSchoolId")String userSchoolId)
    {
        EsUser user=new EsUser();
        user.setUserId(Integer.parseInt(userId));
        if(userName==null) userName="";
        user.setUserName(userName);
        if(userNick==null) userNick="";
        user.setUserNick(userNick);
        if(userPassword==null) userPassword="0";
        user.setUserPassword(userPassword);
        user.setUserPassword(userService2.encrypt(user.getUserName(),user.getUserPassword()));
        if(userAvatar==null) userAvatar="";
        user.setUserAvatar(userAvatar);
        if(userLastLoginTime==null) user.setUserLastLoginTime(new Date());
        else user.setUserLastLoginTime(DateFormat.DateFormatParse(userLastLoginTime));
        if(userCreateTime==null) user.setUserCreateTime(new Date());
        else user.setUserLastLoginTime(DateFormat.DateFormatParse(userCreateTime));
        if(userSex==null) userSex="0";
        user.setUserSex(Integer.parseInt(userSex));
        if(userSchoolId==null) userSchoolId="0";
        user.setUserSchoolId(Integer.parseInt(userSchoolId));
        int status=userService.updateById(user);
        JSONObject data=new JSONObject();
        if (status <= 0) {
            return Result.SERVERERROR();
        }
        else{
            return Result.SUCCESS(data);
        }
    }
}
