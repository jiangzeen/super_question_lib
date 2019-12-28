package com.jxust.qq.superquestionlib.util;


import lombok.Getter;

/**
 * 字段{@code mark}表示题型
 * 默认为
 * <p>选择题</p>
 * <p>填空题</p>
 * <p>判断题</p>
 * <p>解答题</p>
 * 可自行添加定义，如增加题型需在{@code DefaultBreakQuestion}实现其分解
 */
public enum QuestionTypeEnum {
    TYPEONE(1,"选择题"),
    TYPETWO(2,"填空题"),
    TYPETHREE(3,"判断题"),
    TYPEFOUR(4,"解答题");
    @Getter
    private int id;
    @Getter
    private String mark;
    QuestionTypeEnum(int id ,String mark) {
        this.mark = mark;
    }
}
