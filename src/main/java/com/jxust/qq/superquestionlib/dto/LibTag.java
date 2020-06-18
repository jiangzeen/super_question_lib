package com.jxust.qq.superquestionlib.dto;

import lombok.Data;

@Data
public class LibTag {

    private long tagId;
    private String tagName;
    private long parentTagId;
    private String tagImgUrl;
}
