package com.jxust.qq.superquestionlib.dto.admin;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import java.util.Date;
@Data
@Document(indexName = "sq_question_lib",type = "question_lib",createIndex=true,refreshInterval = "-1")
public class EsQuestionLib
{
    @Id
    private long questionLibId;
    private int questionLibLevel;
    @Field(type = FieldType.Text,analyzer ="ik_max_word",searchAnalyzer = "ik_max_word")
    private String questionLibName;
    private String questionLibUrl;
    //1代表为私有 0代码并非私有
    private int hasPrivate;
    @Field(type = FieldType.Date)
    private Date questionLibCreateTime;
    private int questionLibTagId;
    private String LibTagInfo;
    private String parentIds;
    @Field(type = FieldType.Text,analyzer ="ik_max_word",searchAnalyzer = "ik_max_word")
    private String questionLibMark;
}
