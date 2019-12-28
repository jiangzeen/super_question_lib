package com.jxust.qq.superquestionlib.exception;

public interface ExceptionCode {

    /**
     * 获取异常代码
     * @return code
     */
    int getCode();

    /**
     * 获取异常描述
     * @return 描述
     */
    String getDesc();
}
