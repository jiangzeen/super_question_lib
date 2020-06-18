package com.jxust.qq.superquestionlib.util.parse.imp;


import com.jxust.qq.superquestionlib.util.parse.QuestionParse;
import com.jxust.qq.superquestionlib.util.parse.fileparse.Parse;
import lombok.Getter;

import java.io.IOException;

public abstract class AbstractQuestionParse implements QuestionParse {

    protected String filepath;
    @Getter
    protected String content;
    protected Parse parse;
    protected boolean hasAnswer;

    public void init() throws IOException {
        content = parse.readQuestionFile(filepath);
    }

    @Override
    public String getParseText(String filepath) throws IOException {
        return content;
    }



}
