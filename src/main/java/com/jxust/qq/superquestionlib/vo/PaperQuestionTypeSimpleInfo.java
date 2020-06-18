package com.jxust.qq.superquestionlib.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class PaperQuestionTypeSimpleInfo {

    private int totalNum;
    private int rightNum;
    private List<Integer> rightsId;
    private List<Integer> questionIds;
    private Map<Integer, String> userAnswer;


    public PaperQuestionTypeSimpleInfo() {
        this.totalNum = 0;
        this.rightNum = 0;
        this.rightsId = new ArrayList<>();
        this.questionIds = new ArrayList<>();
        this.userAnswer = new HashMap<>();
    }

    public void addQuestionId(int qId) {
        questionIds.add(qId);
        totalNum++;
    }

    public void addRightId(int qId) {
        rightsId.add(qId);
        rightNum++;
    }
}
