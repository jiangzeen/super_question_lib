package com.jxust.qq.superquestionlib.service;

import com.alibaba.fastjson.JSONObject;
import com.jxust.qq.superquestionlib.dao.mapper.ExamMapper;
import com.jxust.qq.superquestionlib.dto.UserPaper;
import com.jxust.qq.superquestionlib.util.DateFormat;
import com.jxust.qq.superquestionlib.util.QuestionTypeEnum;
import com.jxust.qq.superquestionlib.vo.PaperDetailInfoVO;
import com.jxust.qq.superquestionlib.vo.PaperQuestionTypeSimpleInfo;
import com.jxust.qq.superquestionlib.vo.PaperVO;
import com.jxust.qq.superquestionlib.vo.QuestionVO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class UserExamService {

    private final ExamMapper examMapper;
    private final QuestionService questionService;

    public UserExamService(ExamMapper examMapper, QuestionService questionService) {
        this.examMapper = examMapper;
        this.questionService = questionService;
    }

    public int addNewExam(PaperVO paperVO) {
        UserPaper userPaper = new UserPaper();
        JSONObject result = new JSONObject(paperVO.getUserResult());
        userPaper.setUsername(paperVO.getUsername());
        userPaper.setQuestionLibId(paperVO.getId());
        userPaper.setResult(null);
        userPaper.setStartTime(LocalDateTime.parse(paperVO.getStartTime(),
                DateFormat.SECOND_FORMATTER.getFormatter()));
        userPaper.setLimitTime(paperVO.getLimitTime());
        examMapper.insertExam(userPaper);
        return userPaper.getId();
    }

    public PaperDetailInfoVO modifyPaperResult(int id, LocalDateTime time, JSONObject result, String username) {
        // 计算结果
        int libId = examMapper.findUserPaperById(id);
        PaperDetailInfoVO data = papersSimpleInfo(result);
        examMapper.updateExamPaper(id, time, result.toJSONString());
        return data;
    }

    /**
     * 获取到用户某个题库的多个历史试卷信息
     * @param libId 题库Id
     */
    public List<PaperDetailInfoVO> findUserPapersByLibId(int libId, String username) {
        List<UserPaper> paperVOS = examMapper.selectPapersByIdAndName(username, libId);
        return paperVOS.stream().map(paperVO -> {
            PaperDetailInfoVO paperInfo = papersSimpleInfo(JSONObject.parseObject(paperVO.getResult()));
            paperInfo.setTime(paperVO.getStartTime(), paperVO.getEndTime(), paperVO.getLimitTime());
            return paperInfo;
        }).collect(Collectors.toList());
    }


    /**
     * 对一张提交过来的试卷进行解析,并且封装成一个VO返回
     * @param paperResult 用户提交的结果
     * @return paperDetailVO
     */
    private PaperDetailInfoVO papersSimpleInfo(JSONObject paperResult) {
        Set<Map.Entry<String ,Object>> set = paperResult.entrySet();
        PaperDetailInfoVO detailInfoVO = new PaperDetailInfoVO();
        Map<QuestionTypeEnum, PaperQuestionTypeSimpleInfo> questionInfo =
                detailInfoVO.getQuestionDetailInfo();
        int unknown = 0;
        for (Map.Entry<String, Object> entry : set) {
            String qId = entry.getKey();
            QuestionVO qVo = questionService.findQuestionById(Integer.parseInt(qId));
            // 如果题目不存在的话, 则抛出异常
            if (qVo == null) {
                throw new IllegalArgumentException();
            }
            QuestionTypeEnum typeEnum =  questionService.findQuestionType(qVo);
            String standardResult = questionService.findAnswerByQuestionId(qVo.getId());
            if (standardResult == null || standardResult.trim().equals("")) {
                unknown++;
                continue;
            }
            // 对比答案, 对结果进行更新
            String userAnswer = (String) entry.getValue();
            updatePaperQuestionInfo(questionInfo, Integer.parseInt(qId), userAnswer, standardResult, typeEnum);
        }
        return detailInfoVO;
    }

    private void updatePaperQuestionInfo(Map<QuestionTypeEnum, PaperQuestionTypeSimpleInfo> paperInfo, int qId,
                                         String userAnswer, String standardAnswer, QuestionTypeEnum type) {
        paperInfo.compute(type, (typeKey, value) -> {
            if (value == null) {
                value = new PaperQuestionTypeSimpleInfo();
            }
            value.addQuestionId(qId);
            // 若为选择题与判断题, 并且答案正确, 则添加到正确答案List中.
            if (type == QuestionTypeEnum.TYPEONE || type == QuestionTypeEnum.TYPETHREE
                && userAnswer.equals(standardAnswer)) {
                value.addRightId(qId);
            } else {
                // 如果用户提交的答案是错误的或者是主观题,那么则将正确的答案返回做对比
                value.getUserAnswer().computeIfAbsent(qId, key -> standardAnswer);
            }
            return value;
        });
    }
}
