package com.jxust.qq.superquestionlib.controller;

import com.alibaba.fastjson.JSONObject;
import com.jxust.qq.superquestionlib.dto.Result;
import com.jxust.qq.superquestionlib.dto.SchoolInfo;
import com.jxust.qq.superquestionlib.dto.User;
import com.jxust.qq.superquestionlib.service.MailService;
import com.jxust.qq.superquestionlib.service.RedisService;
import com.jxust.qq.superquestionlib.service.UserService;
import com.jxust.qq.superquestionlib.vo.UserVO;
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
            data.put("defaultAvatar", UserService.default_avatar_url);
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
        // todo 应该检查redis服务是否可用
        User user = userService.findUser(username);
        if (user != null) {
            return Result.FAILD(null);
        }
        int code = mailService.senderSimpleMail(username);
        redisService.setKeyValue(username, String.valueOf(code));
        redisService.setKeyExpire(username, 300L);
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
        if (user == null) {
            return Result.HASAUTH(null);
        }
        JSONObject data = new JSONObject();
        data.put("nickname", user.getUserNick());
        data.put("username", username);
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            return Result.HASAUTH(data);
        }
        String authToken;
        try {
            subject.login(token);
            authToken = (String) subject.getSession().getId();
        } catch (IncorrectCredentialsException e) {
            result = Result.FAILD(username);
            return result;
        }
        userService.modifyLoginTime(username);
        data.put("authToken", authToken);
        redisService.setKeyValue(authToken, username);
        redisService.setKeyExpire(authToken, 15*60);
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

    @GetMapping("/user/info/{username}")
    public Result getUserInfo(@PathVariable String username) {
        // todo 修改查找用户的方法,会出现NPE的异常
        UserVO vo = userService.findUserByUsername(username);
        if (vo != null) {
            return Result.SUCCESS(vo);
        }else {
            return Result.FAILD("用户不存在,检查用户名后重试");
        }
    }


    @GetMapping("/info/schools")
    public Result schoolInfo() {
        return Result.SUCCESS(userService.schoolInfos());
    }

    @GetMapping("/info/findSchool")
    public Result findSchool(@RequestParam("username") String username) {
        SchoolInfo info = userService.findSchool(username);
        if (info == null) {
            return Result.PARAMS_ERROR();
        }
        return Result.SUCCESS(info);
    }

    @PostMapping("/user/complete_info")
    public Result completeInfo(@RequestBody UserVO userVO) {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        userVO.setUsername(username);
        if (userVO.getSchoolInfo() != null) {

        }
        userService.completeInfo(username, userVO.getNickname(),userVO.getSex(), userVO.getSchoolId());
        return Result.SUCCESS(null);
    }

    @PostMapping("/user/upload/avatar")
    public Result completeAvatar(@RequestParam("avatar") MultipartFile file) {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        String avatarUrl = userService.processAvatar(username, file);
        userService.modifyUserAvatar(avatarUrl, username);
        return Result.SUCCESS(avatarUrl);
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
