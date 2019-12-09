package com.jxust.qq.superquestionlib.service;

import com.jxust.qq.superquestionlib.po.Question;
import com.jxust.qq.superquestionlib.util.QuestionMark;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class DefaultBreakQuestion {
    @Getter
    @Setter
    private String filepath;
    public DefaultBreakQuestion(String filepath) {
        this.filepath = filepath;
    }

    /**
     * TODO 分解题目实现类
     * @param mark 标记
     * @return List<Question>
     */
    public List<Question> breakQuestion(QuestionMark mark) {
        return null;
    }
}
