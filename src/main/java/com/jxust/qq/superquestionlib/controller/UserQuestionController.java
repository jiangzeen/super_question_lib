package com.jxust.qq.superquestionlib.controller;


import com.jxust.qq.superquestionlib.dto.Result;
import com.jxust.qq.superquestionlib.dto.UserQuestionHistory;
import com.jxust.qq.superquestionlib.service.QuestionService;
import com.jxust.qq.superquestionlib.service.UserQuestionHistoryService;
import com.jxust.qq.superquestionlib.service.UserQuestionLibService;
import com.jxust.qq.superquestionlib.vo.QuestionLibVO;
import com.jxust.qq.superquestionlib.vo.UserQuestionHistoryVO;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
public class UserQuestionController {

    private final UserQuestionLibService userQuestionLibService;
    private final QuestionService questionService;
    private final UserQuestionHistoryService userQuestionHistoryService;

    public UserQuestionController(UserQuestionLibService userQuestionLibService, QuestionService questionService, UserQuestionHistoryService userQuestionHistoryService) {
        this.userQuestionLibService = userQuestionLibService;
        this.questionService = questionService;
        this.userQuestionHistoryService = userQuestionHistoryService;
    }


    /**
     * 获取用户空间数据
     * @param username
     * @param map
     * @return
     */
    @GetMapping("/user/user_lib/{username}/{page}")
    public Result geUserQuestionLib(@PathVariable("username") String username, @RequestBody  Map<String, String> map,
                                @PathVariable("page") int page) {
        if (page < 1) {
            return Result.FAILD("page必须大于0");
        }
        String type = map.get("typeId");
        Integer typeId = null;
        if (type != null) {
            typeId = Integer.valueOf(type);
        }
        int limit = Integer.parseInt(map.get("limit"));
        List<QuestionLibVO> libVOS = userQuestionLibService.findUserLibByUsername(username, typeId, page, limit);
        return Result.SUCCESS(libVOS);
    }

    /**
     * 获取到某一个题库的全部题目信息
     * @param map 指定题目的类型如 选择题->1 填空题->2等
     * @return questionLibVO
     */
    @GetMapping("/user/user_lib/details/{username}/{libId}")
    public Result getUserLibQuestions(@PathVariable String username, @PathVariable int libId, @RequestBody Map<String, String> map) {
        Integer type = null;
        if (map.get("typeId") != null) {
            type = Integer.valueOf(map.get("typeId"));
        }
        QuestionLibVO libVO = userQuestionLibService.findQuestionsByUsernameAndId(username, libId, type);
        return Result.SUCCESS(libVO);
    }

    @GetMapping("/user/user_question/answer/{question_id}")
    public Result getQuestionAnswer(@PathVariable int question_id) {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        boolean userAccess =  questionService.findUsernameByQId(username, question_id);
        if (userAccess) {
            String answer = questionService.findAnswerByQuestionId(question_id);
            return Result.SUCCESS(answer);
        }else {
            return Result.FAILD("无权限查看此题答案");
        }
    }

    @PostMapping("/user/user_question/submit")
    public Result submitQuestion(@RequestBody Map<String, String> map) {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        if (!username.equals(map.get("username"))) {
            return Result.FAILD("用户名不一致, 无权限提交");
        }
        int id = Integer.parseInt(map.get("questionId"));
        String result = map.get("result");
        userQuestionHistoryService.addQuestionHistory(username, id, result);
        return Result.SUCCESS("提交成功");
    }

    @GetMapping("/user/user_question/submit_list")
    public Result submitQuestionList(@RequestBody Map<String, String> map) {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        int lId = Integer.parseInt(map.get("libId"));
        List<UserQuestionHistoryVO> data = userQuestionHistoryService.
                findHistoriesByLibIdAndUsername(username, lId);
        return Result.SUCCESS(data);
    }
}
