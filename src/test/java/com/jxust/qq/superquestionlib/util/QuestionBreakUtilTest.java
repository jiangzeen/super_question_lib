package com.jxust.qq.superquestionlib.util;

import com.jxust.qq.superquestionlib.exception.QuestionException;
import com.jxust.qq.superquestionlib.exception.QuestionParseExceptionEnum;
import com.jxust.qq.superquestionlib.service.DefaultBreakQuestion;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class QuestionBreakUtilTest {



    @Test
    public void readQuestionFile() throws IOException {
        System.out.println(QuestionBreakUtil.init("english.docx"));
    }

    @Test
    public void readTitle() throws IOException {
        DefaultBreakQuestion breakQuestion = new DefaultBreakQuestion("english.docx");
        try {
            breakQuestion.breakQuestion();
        }catch (QuestionException e) {
            System.out.println(e);
        }

    }

    @Test
    public void testList() {
        List<StringBuffer> array = new ArrayList<>();
        array.add(new StringBuffer("132"));
        array.add(new StringBuffer("456"));
        array.forEach(it->{
            System.out.println(it.toString());
        });
        StringBuffer a = array.get(0);
        a.append("789");
        array.forEach(it->{
            System.out.println(it.toString());
        });
    }
}