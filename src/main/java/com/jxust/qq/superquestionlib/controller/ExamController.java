package com.jxust.qq.superquestionlib.controller;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.jxust.qq.superquestionlib.dto.Result;
import com.jxust.qq.superquestionlib.service.QuestionLibService;
import com.jxust.qq.superquestionlib.service.QuestionService;
import com.jxust.qq.superquestionlib.service.RedisService;
import com.jxust.qq.superquestionlib.service.UserExamService;
import com.jxust.qq.superquestionlib.util.DateFormat;
import com.jxust.qq.superquestionlib.vo.PaperDetailInfoVO;
import com.jxust.qq.superquestionlib.vo.PaperVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/user")
public class ExamController {

    private final QuestionService questionService;
    private final QuestionLibService libService;
    private final UserExamService examService;
    private final RedisService redisService;

    public ExamController(QuestionService questionService, QuestionLibService libService,
                          UserExamService examService, RedisService redisService) {
        this.questionService = questionService;
        this.libService = libService;
        this.examService = examService;
        this.redisService = redisService;
    }


    /**
     * 添加(不是提交结果)一个新的考试记录
     * @param paperVO 考试信息 考试时间, 限制时间等
     * @return 考试记录id与考试实体ids
     */
    @PostMapping("/exam/start_by_libId")
    public Result addPaperRecord(@RequestBody PaperVO paperVO) {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        String key = "exam:" + username;
        if (redisService.getValue(key) != null) {
            return Result.PARAMS_ERROR("正在考试中,请勿重复创建考试");
        }
        paperVO.setUsername(username);
        if (!libService.verifyLibId(paperVO.getId(), username)) {
            return Result.PARAMS_ERROR("libId is not exist");
        }
        List<JSONObject> questionsData = questionService.findQuestionByLibId(paperVO.getId(), username);
        int paperId = examService.addNewExam(paperVO);
        JSONObject data = new JSONObject();
        data.put("paperId", paperId);
        data.put("questionList", questionsData);
        // 保存用户的考试信息
        redisService.setKeyValue(key, String.valueOf(paperId));
        redisService.setKeyExpire(key, paperVO.getLimitTime() * 60);
        return Result.SUCCESS(data);
    }

    @PostMapping("/exam/submit")
    public Result submitPaper(@RequestBody PaperVO resultVo) {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        String result = JSONObject.toJSONString(resultVo, SerializerFeature.PrettyFormat);
        log.warn("提交的result:{}", result);
        int id = resultVo.getId();
        LocalDateTime endTime = LocalDateTime.parse(resultVo.getEndTime(),
                DateFormat.SECOND_FORMATTER.getFormatter());
        JSONObject data = new JSONObject(resultVo.getUserResult());
        try {
            PaperDetailInfoVO res = examService.modifyPaperResult(id, endTime, data, username);
            return Result.SUCCESS(res);
        } catch (IllegalArgumentException e) {
            return Result.PARAMS_ERROR("题目Id存在错误");
        }
    }

    @GetMapping("/exam/{libId}/totals")
    public Result LibPaperRecords(@PathVariable int libId) {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        List<PaperDetailInfoVO> data = examService.findUserPapersByLibId(libId, username);
        return Result.SUCCESS(data);
    }

    @GetMapping("/exam/{examId}/details")
    public Result paperDetail(@PathVariable int examId) {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        return null;
    }
}
