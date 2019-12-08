package com.jxust.qq.superquestionlib.service;

import com.jxust.qq.superquestionlib.po.ChoiceQuestion;
import com.jxust.qq.superquestionlib.po.ExplainQuestion;
import com.jxust.qq.superquestionlib.po.FillQuestion;
import com.jxust.qq.superquestionlib.po.TFQuestion;
import com.jxust.qq.superquestionlib.util.QuestionMark;


import java.io.FileNotFoundException;
import java.util.List;

public interface BreakQuestion {

    List<ChoiceQuestion> breakCQuestion(QuestionMark mark) throws FileNotFoundException;

    List<FillQuestion> breakFQuestion(QuestionMark mark) throws FileNotFoundException;

    List<ExplainQuestion> breakEQuestion(QuestionMark mark) throws FileNotFoundException;

    List<TFQuestion> breakTFQuestion(QuestionMark mark) throws FileNotFoundException;
}
