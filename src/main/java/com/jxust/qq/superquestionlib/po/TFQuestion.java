package com.jxust.qq.superquestionlib.po;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class TFQuestion extends Question{

    private String OptionA;
    private String OptionB;
}
