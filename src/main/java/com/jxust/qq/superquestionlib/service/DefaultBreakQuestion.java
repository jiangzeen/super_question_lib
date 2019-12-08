package com.jxust.qq.superquestionlib.service;

import com.jxust.qq.superquestionlib.po.ChoiceQuestion;
import com.jxust.qq.superquestionlib.po.ExplainQuestion;
import com.jxust.qq.superquestionlib.po.FillQuestion;
import com.jxust.qq.superquestionlib.po.TFQuestion;
import com.jxust.qq.superquestionlib.util.QuestionBreakUtil;
import com.jxust.qq.superquestionlib.util.QuestionMark;
import lombok.Getter;
import lombok.Setter;

import java.io.FileNotFoundException;
import java.util.List;

public class DefaultBreakQuestion implements BreakQuestion {
    @Getter
    @Setter
    private String filepath;
    public DefaultBreakQuestion(String filepath) {
        this.filepath = filepath;
    }


    @Override
    public List<ChoiceQuestion> breakCQuestion(QuestionMark mark) throws FileNotFoundException {
        return QuestionBreakUtil.breakCQ(filepath, mark);
    }

    @Override
    public List<FillQuestion> breakFQuestion(QuestionMark mark) throws FileNotFoundException {
        return null;
    }

    @Override
    public List<ExplainQuestion> breakEQuestion(QuestionMark mark) throws FileNotFoundException {
        return null;
    }

    @Override
    public List<TFQuestion> breakTFQuestion(QuestionMark mark) throws FileNotFoundException {
        return null;
    }
}
