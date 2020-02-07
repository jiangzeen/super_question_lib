package com.jxust.qq.superquestionlib.util;

import com.jxust.qq.superquestionlib.exception.QuestionException;
import com.jxust.qq.superquestionlib.service.DefaultBreakQuestion;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    public void test_time() {
        LocalDateTime time = LocalDateTime.now();
        System.out.println(time.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
    }
}