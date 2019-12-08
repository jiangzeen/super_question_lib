package com.jxust.qq.superquestionlib.util;


import lombok.Getter;
import lombok.Setter;

/**
 * 字段{@code prefix}代表题型前面的标识符
 * 分别取0,1,2,3,4
 * 0:代表无前缀,例如:选择题
 * 1:代表前缀为数字+顿号      例如:1、选择题
 * 2:代表前缀为数字+点        例如:1.选择题
 * 3:代表前缀为中文数字+顿号   例如:一、选择题
 * 4:代表前缀为中文数字+点     例如:一.选择题
 * 字段{@code mark}表示题型
 * 默认为
 * <p>选择题</p>
 * <p>填空题</p>
 * <p>判断题</p>
 * <p>解答题</p>
 * 可自行添加定义，如增加题型需在{@code DefaultBreakQuestion}实现其分解
 */
public enum QuestionMark {
    CQMARKONE("选择题"),
    CQMARKTWO("填空题"),
    CQMARKTHREE("判断题"),
    CQMARKFOUR("解答题");
    @Getter
    private String mark;
    @Setter
    @Getter
    private int prefix;
    QuestionMark(String mark) {
        this.mark = mark;
    }
}
