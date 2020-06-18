package com.jxust.qq.superquestionlib.util.parse;

import com.jxust.qq.superquestionlib.dto.Question;

import java.io.IOException;
import java.util.List;

public interface QuestionParse {

    String getParseText(String filepath) throws IOException;

    List<Question> parseQuestions();
}
