package com.jxust.qq.superquestionlib.util.parse.imp;

import com.alibaba.fastjson.JSONObject;
import com.jxust.qq.superquestionlib.dto.Question;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

public class DefaultQuestionParseTest {

    DefaultQuestionParse parse;
    DefaultQuestionParse parse2;

    {
        try {
            parse = new DefaultQuestionParse("english_test2.docx", false);
            parse2 = new DefaultQuestionParse("test_english.doc", false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void showParts() {
        List<DefaultQuestionParse.QuestionPart> parts = parse.showParts();
        assertEquals(1, parts.size());
    }

    @Test
    public void showParts2() {
        List<DefaultQuestionParse.QuestionPart> parts = parse2.showParts();
        assertEquals(4, parts.size());
        assertEquals(15, parse2.showDividePart(parts.get(0)).size());
        assertEquals(15, parse2.showDividePart(parts.get(1)).size());
        assertEquals(15, parse2.showDividePart(parts.get(2)).size());
        assertEquals(15, parse2.showDividePart(parts.get(3)).size());
    }

    @Test
    public void parseQuestions() {
        List<Question> questionList = parse.parseQuestions();
        assertEquals(5, questionList.size());
        for (Question question : questionList) {
            System.out.println(question.getQuestionContent());
        }
    }

    @Test
    public void parseDocFile() {
        List<Question> questionList = parse2.parseQuestions();
    }

    @Test
    public void testDocFile() {
        System.out.println(parse.showContent());
        List<Question> questionList = parse.parseQuestions();
        assertEquals(60, questionList.size());
    }

    @Test
    public void testJSONObject() {
        JSONObject json = new JSONObject(null);
        json.put("name", null);
        assertNull(json.toJSONString());
    }
}