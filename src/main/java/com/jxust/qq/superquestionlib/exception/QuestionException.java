package com.jxust.qq.superquestionlib.exception;

import lombok.Getter;

public class QuestionException extends RuntimeException {

    @Getter
    private ExceptionCode exceptionCode;


    public QuestionException(String msg) {
        super(msg);
        this.exceptionCode = QuestionParseExceptionEnum.DEFAULT_EXCEPTION;
    }

    public QuestionException(ExceptionCode exceptionCode) {
        super(exceptionCode.getDesc());
        this.exceptionCode = exceptionCode;
    }

    public QuestionException(ExceptionCode exceptionCode, String detailDesc) {
        super(detailDesc);
        this.exceptionCode = exceptionCode;
    }


}
