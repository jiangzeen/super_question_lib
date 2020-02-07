package com.jxust.qq.superquestionlib.controller;

import com.alibaba.fastjson.JSONObject;
import com.jxust.qq.superquestionlib.dto.Result;
import com.jxust.qq.superquestionlib.dto.User;
import com.jxust.qq.superquestionlib.service.MailService;
import com.jxust.qq.superquestionlib.service.RedisService;
import com.jxust.qq.superquestionlib.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@Slf4j
public class UserController {

    private final UserService userService;
    private final RedisService redisService;
    private final MailService mailService;

    public UserController(UserService userService, RedisService redisService,
                          MailService mailService) {
        this.userService = userService;
        this.redisService = redisService;
        this.mailService = mailService;
    }
    // TODO 待会删掉
    @PostMapping("/toRegister")
    public Result getRegisterCode(@RequestParam("username") String username,
                                  @RequestParam("password") String password,
                                  @RequestParam("nickname") String nickname) {
            int status = userService.createUser(username, password, nickname);
            if (status < 0) {
                return Result.SERVERERROR();
            }
            JSONObject data = new JSONObject();
            data.put("username", username);
            return Result.SUCCESS("注册成功", data);
    }


    @PostMapping("/register")
    public Result getRegisterCode(@RequestParam("username") String username,
                                  @RequestParam("password") String password,
                                  @RequestParam("nickname") String nickname,
                                  @RequestParam("code") String code) {
        String value = redisService.getValue(username);
        if (value != null && value.equals(code)) {
            int status = userService.createUser(username, password, nickname);
            if (status < 0) {
                return Result.SERVERERROR();
            }
            JSONObject data = new JSONObject();
            data.put("username", username);
            return Result.SUCCESS("注册成功", data);
        }
        Result result = new Result();
        result.setCode(203);
        result.setMessage("验证码错误或验证码已过期");
        result.setData(username);
        return result;
    }

    @PostMapping("/register/verify/{username}")
    public Result verifyCode(@PathVariable String username, @RequestParam("code")String code) {
        String value = redisService.getValue(username);
        if (value != null && value.equals(code)) {
            return Result.SUCCESS(null);
        }else {
            return Result.FAILD(null);
        }
    }

    @PostMapping("/register/send_code/{username}")
    public Result code(@PathVariable("username") String username) {
        User user = userService.findUser(username);
        if (user != null) {
            return Result.FAILD(null);
        }
        int code = mailService.senderSimpleMail(username);
        redisService.setKeyValue(username, String.valueOf(code));
        return Result.SUCCESS(null);
    }


    @PostMapping("/login")
    public Result login(@RequestParam("username") String username, @RequestParam("password")
                        String password) {
        UsernamePasswordToken token = new UsernamePasswordToken();
        token.setUsername(username);
        token.setPassword(password.toCharArray());
        Result result;
        User user = userService.findUser(username);
        JSONObject data = new JSONObject();
        data.put("nickname", user.getUserNick());
        data.put("username", username);
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            return Result.HASAUTH(data);
        }
        try {
            subject.login(token);
        } catch (IncorrectCredentialsException e) {
            result = Result.FAILD(username);
            return result;
        }
        result = Result.SUCCESS(data);
        return result;
    }

    @PostMapping("/user/modify_password")
    public Result changePassword(@RequestParam("old_password")String oldPassword,
                                 @RequestParam("new_password")String newPassword) {
        Subject user = SecurityUtils.getSubject();
        String username = (String) user.getPrincipal();
        String password = userService.findUserPassword(username);
        JSONObject data = new JSONObject();
        data.put("username", username);
        if (!userService.encrypt(username, oldPassword).equals(password)) {
            return Result.FAILD(data);
        }
        userService.modifyPassword(username, newPassword);
        user.logout();
        return Result.SUCCESS(data);
    }


    @PostMapping("/user/complete_info/{username}")
    public Result completeInfo(@RequestParam("majorId") String majorId,@PathVariable("username")String username,
                               @RequestParam(value = "avatar", required = false) MultipartFile avatarFile) {
        User user = userService.findUser(username);
        JSONObject data = new JSONObject();
        data.put("username", username);
        if (user == null) {
           Result res =  Result.FAILD(data);
           res.setMessage("失败,用户不存在,请检查username");
           return res;
        }
        // 保存图片
        String imgurl = userService.processAvatar(username, avatarFile);
        if (imgurl == null) {
            Result res =  Result.FAILD(data);
            res.setMessage("失败,用户头像文件格式错误");
            return res;
        }
        log.info("上传文件[{}]成功", imgurl);
        user.setUserAvatar(imgurl);
        userService.completeInfo(username, imgurl, majorId);
        return Result.SUCCESS(data);
    }

    @PostMapping("/user/logout")
    public Result logout(){
        Subject user = SecurityUtils.getSubject();
        String username = (String) user.getPrincipal();
        user.logout();
        userService.modifyLoginTime(username);
        return Result.SUCCESS(username);
    }
}
