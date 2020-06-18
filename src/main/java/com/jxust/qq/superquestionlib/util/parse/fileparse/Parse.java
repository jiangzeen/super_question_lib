package com.jxust.qq.superquestionlib.util.parse.fileparse;

import java.io.IOException;

@FunctionalInterface
public interface Parse {
    String readQuestionFile(String file) throws IOException;
}
