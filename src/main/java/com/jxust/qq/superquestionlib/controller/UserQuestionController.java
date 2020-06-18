package com.jxust.qq.superquestionlib.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jxust.qq.superquestionlib.dto.QuestionNote;
import com.jxust.qq.superquestionlib.dto.Result;
import com.jxust.qq.superquestionlib.service.QuestionNoteService;
import com.jxust.qq.superquestionlib.service.QuestionService;
import com.jxust.qq.superquestionlib.service.UserQuestionHistoryService;
import com.jxust.qq.superquestionlib.service.UserQuestionLibService;
import com.jxust.qq.superquestionlib.vo.QuestionLibVO;
import com.jxust.qq.superquestionlib.vo.QuestionNoteVO;
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
    private final QuestionNoteService questionNoteService;

    public UserQuestionController(UserQuestionLibService userQuestionLibService,
                                  QuestionService questionService,
                                  UserQuestionHistoryService userQuestionHistoryService, QuestionNoteService questionNoteService) {
        this.userQuestionLibService = userQuestionLibService;
        this.questionService = questionService;
        this.userQuestionHistoryService = userQuestionHistoryService;
        this.questionNoteService = questionNoteService;
    }


    /**
     * 获取用户空间数据
     */
    @GetMapping("/user/user_lib/{username}/1")
    public Result geUserQuestionLib(@PathVariable("username") String username,@RequestParam("pageNum") int page,
                                    @RequestParam(value = "typeId", required = false) String type,
                                    @RequestParam("limit") int limit) {
        if (page < 1) {
            return Result.FAILD("page必须大于0");
        }
        Integer typeId = null;
        if (type != null) {
            typeId = Integer.valueOf(type);
        }
        //
        List<QuestionLibVO> libVOS = userQuestionLibService.findUserLibByUsername(username, typeId, page, limit);
        // 获取总数
        int total = userQuestionLibService.findUserLibTotals(username);
        JSONObject data = new JSONObject();
        data.put("total", total);
        data.put("libList", libVOS);
        return Result.SUCCESS(data);
    }

    /**
     * 获取到某一个题库的全部题目信息
     * @param type 指定题目的类型如 选择题->1 填空题->2等
     * @return questionLibVO
     */
    @GetMapping("/user/user_lib/details/{username}/{libId}")
    public Result getUserLibQuestions(@PathVariable String username, @PathVariable int libId,
                                      @RequestParam(value = "typeId", required = false) String type) {
        Integer typeId = null;
        if (type != null) {
            typeId = Integer.valueOf(type);
        }
        QuestionLibVO libVO = userQuestionLibService.findQuestionsByUsernameAndId(username, libId, typeId);
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
            return Result.HASAUTH(null);
        }
        int id = Integer.parseInt(map.get("questionId"));
        String result = map.get("result");
        userQuestionHistoryService.addQuestionHistory(username, id, result);
        return Result.SUCCESS("提交成功");
    }

    @GetMapping("/user/user_question/submit_list")
    public Result submitQuestionList(@RequestParam("libId") String libId) {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        int lId = Integer.parseInt(libId);
        List<UserQuestionHistoryVO> data = userQuestionHistoryService.
                findHistoriesByLibIdAndUsername(username, lId);
        return Result.SUCCESS(data);
    }


    /**
     * 获取用户一个题库最近的做题信息
     * @param limit nullable
     * @param libId 题库Id
     * @return result
     */
    @GetMapping("/user/user_question/recent_result")
    public Result recentQuestionResult(@RequestParam(required = false) Integer limit, @RequestParam("libId") int libId) {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        JSONArray data;
        if (limit == null) {
            data = userQuestionHistoryService.findRecentQuestionByDefault(libId, username);
        } else {
            data = userQuestionHistoryService.findRecentQuestionWithLimit(libId, username, limit);
        }
        return Result.SUCCESS(data);
    }

    @PostMapping("/user/user_question/add_note")
    public Result addQuestionNote(@RequestBody QuestionNote note) {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        note.setUsername(username);
        int id = questionNoteService.addQuestionNote(note);
        JSONObject data = new JSONObject();
        data.put("noteId", id);
        return Result.SUCCESS(data);
    }

    /**
     * 获取一道题目的笔记, 包括自己的和别人的
     * @param questionId id
     * @return List
     */
    @GetMapping("/user/user_question/notes")
    public Result getQuestionNotes(@RequestParam("questionId") int questionId) {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        List<QuestionNoteVO> voList = questionNoteService.findNotesByQuestionId(questionId);
        List<QuestionNoteVO> otherNote = questionNoteService.findNoteByOtherUsername(username, questionId);
        JSONObject data = new JSONObject();
        data.put("selfNote", voList);
        data.put("otherNote", otherNote);
        return Result.SUCCESS(data);
    }

    /**
     * 根据分类获取笔记
     * @return 返回已经分好类的note
     */
    @GetMapping("/user/user_question/notes/by_type")
    public Result getTypeNotes() {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        return Result.SUCCESS(questionNoteService.findAllNoteByType(username));
    }


    @PostMapping("/user/user_question/note_update")
    public Result modifyNote(@RequestBody QuestionNote note) {
        try {
            questionNoteService.updateNoteById(note);
            return Result.SUCCESS("更新笔记成功");
        }catch (IllegalArgumentException e) {
            return Result.FAILD("该noteId不存在,请检查后重试");
        }
    }
}
