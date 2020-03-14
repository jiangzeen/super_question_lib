package com.jxust.qq.superquestionlib.vo;

import lombok.Data;

import java.util.List;

@Data
public class PaperAndPracticeVO {

    private int libId;
    private List<Integer> typeList;
    private int targetNumber;

}
