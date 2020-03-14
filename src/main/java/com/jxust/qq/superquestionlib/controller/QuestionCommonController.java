package com.jxust.qq.superquestionlib.controller;

import com.alibaba.fastjson.JSONObject;
import com.jxust.qq.superquestionlib.dto.Result;
import com.jxust.qq.superquestionlib.dto.UserQuestion;
import com.jxust.qq.superquestionlib.service.QuestionLibService;
import com.jxust.qq.superquestionlib.service.QuestionService;
import com.jxust.qq.superquestionlib.service.UserQuestionService;
import com.jxust.qq.superquestionlib.util.DateFormat;
import com.jxust.qq.superquestionlib.util.algo.QuestionQuality;
import com.jxust.qq.superquestionlib.util.algo.imp.SM2AlgorithmProcess;
import com.jxust.qq.superquestionlib.vo.PaperAndPracticeVO;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 包含题目练习与考试接口
 * 记录保存
 */
@RestController
public class QuestionCommonController {

    private final QuestionLibService libService;
    private final QuestionService questionService;
    private final UserQuestionService userQuestionService;
    private final SM2AlgorithmProcess sm2AlgorithmProcess;


    public QuestionCommonController(QuestionLibService libService, QuestionService questionService,
                                    UserQuestionService userQuestionService, SM2AlgorithmProcess sm2AlgorithmProcess) {
        this.libService = libService;
        this.questionService = questionService;
        this.userQuestionService = userQuestionService;
        this.sm2AlgorithmProcess = sm2AlgorithmProcess;
    }

    /**
     * 获取到一个题库的所包含的类型
     */
    @GetMapping("/question/types/{id}")
    public List<Integer> questionTypes(@PathVariable int id) {
        return libService.findQuestionType(id);
    }


    /**
     * 选择算法:
     * 根据interval选取最近需要练习的题, 若数目不够则根据easiness选取其他的题目
     * @param vo
     * @return
     */
    @PostMapping("/user/user_question/practice")
    public Result generatePractice(@RequestBody PaperAndPracticeVO vo) {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        if (!libService.isUserLib(vo.getLibId(), username)) {
            return Result.URINOTFOUND();
        }
        // 算法选择出需要得题库
        return null;
    }

    /**
     * 获取当日需要练习的题目
     * [
     *      {
     *           "content" : "",
     *           "questionId": "",
     *           "lastPracticeTime" : ""
     *      },
     *      {
     *          ....
     *      }
     * ]
     * @param libId 所选择的题库Id
     * @return List<JSONObject>
     */
    @GetMapping("/user/user_question/recent_to_practice")
    public Result generateRecentQuestions(@RequestParam("libId") int libId) {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        if (!libService.isUserLib(libId, username)){
            return Result.URINOTFOUND();
        }
        List<JSONObject> data = userQuestionService.findQuestionByRecent(username, libId)
                .stream().map(question -> {
                    JSONObject json = new JSONObject();
                    json.put("content", question.getContent());
                    json.put("questionId", question.getQuestionId());
                    json.put("lastPracticeTime",
                            question.getLastPracticeTime().format(DateFormat.MINUTE_FORMATTER.getFormatter()));
                    return json;
                }).collect(Collectors.toList());
        return Result.SUCCESS(data);
    }

    /**
     *
     * params:
     * {
     *  "quality": xxx,   [1-5]之间
     *  "questionId": xxx
     * }
     * @param params 请求体参数
     * @return 更新结果
     */
    @PostMapping("/user/user_question/practice/calculate")
    public Result calculateQuestionEasiness(@RequestBody Map<String, String> params) {
        if (params.get("questionId") == null || params.get("quality") == null) {
            return Result.PARAMS_MISSING();
        }
        int questionId = Integer.parseInt(params.get("questionId"));
        int quality = Integer.parseInt(params.get("quality"));
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        UserQuestion question = userQuestionService.findQuestion(username, questionId);
        if (question == null) {
            return Result.PARAMS_ERROR();
        }
        // 更新最新的练习时间
        question.setLastPracticeTime(LocalDateTime.now());
        try {
            UserQuestion updatedQuestion =
                    sm2AlgorithmProcess.process(question, QuestionQuality.qualityById(quality));
            userQuestionService.updateQuestionAfterProcess(updatedQuestion);
            return Result.SUCCESS(null);
        }catch (IllegalArgumentException e) {
            return Result.PARAMS_ERROR("quality must be in 0-5");
        }
    }

}
