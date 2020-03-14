package com.jxust.qq.superquestionlib.util.algo;


import com.jxust.qq.superquestionlib.dto.UserQuestion;

import java.util.List;

public interface QuestionProcess {

    UserQuestion process(UserQuestion question, QuestionQuality quality);

}
