package com.jxust.qq.superquestionlib.util;

import org.junit.Test;

import java.io.FileNotFoundException;

import static org.junit.Assert.*;

public class QuestionBreakUtilTest {

    @Test
    public void breakCQ() {
        try {
            QuestionBreakUtil.breakCQ("/home/jiangzeen/project_log/", null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void readQuestionFile() {

    }
}