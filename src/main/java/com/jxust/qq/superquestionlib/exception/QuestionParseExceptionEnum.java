package com.jxust.qq.superquestionlib.exception;

public enum QuestionParseExceptionEnum implements ExceptionCode {

    DEFAULT_EXCEPTION(400,"默认异常"),
    CONTENT_NOT_MATCH_OPTIONS(401, "存在题干未匹配到对应的选项"),
    CONTENT_NOT_MATCH_ANSWER(402, "存在题干未匹配到对应的答案");

    private final int code;
    private final String desc;

    QuestionParseExceptionEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    /**
     * 根据code获取到具体的异常信息，如没有找到则返回默认的异常
     * @param code 异常代号
     * @return 具体的异常desc
     */
    public static QuestionParseExceptionEnum getEnumByCode(int code) {
        for (QuestionParseExceptionEnum e : QuestionParseExceptionEnum.values()) {
            if (e.code == code) {
                return e;
            }
        }
        return DEFAULT_EXCEPTION;
    }
}
