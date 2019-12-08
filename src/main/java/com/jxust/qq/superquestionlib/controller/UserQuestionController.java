package com.jxust.qq.superquestionlib.controller;


import com.jxust.qq.superquestionlib.po.Result;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class UserQuestionController {

    @GetMapping("/user/question_lib/{username}")
    public Result getMyQuestion(@PathVariable("username") String username) {
        Subject su = SecurityUtils.getSubject();
        if (!su.isAuthenticated()) {
            return Result.FAILD(null);
        }
        return Result.SUCCESS(null);
    }
}
