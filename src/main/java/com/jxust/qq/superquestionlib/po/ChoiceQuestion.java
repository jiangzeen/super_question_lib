package com.jxust.qq.superquestionlib.po;


import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public class ChoiceQuestion extends Question {
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;

}
