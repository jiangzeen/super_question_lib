package com.jxust.qq.superquestionlib.dto.admin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.util.Date;
@Data
@Setting(settingPath = "user.json")
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(indexName = "sq_question_lib",type = "question_lib",createIndex=true,refreshInterval = "-1")
public class EsQuestionLib
{
    @Id
    @JsonProperty("questionLibId")
    private long questionLibId;
    @JsonProperty("questionLibLevel")
    private int questionLibLevel;
    @JsonProperty("questionLibName")
    @Field(type = FieldType.Text,analyzer ="ik_max_word",searchAnalyzer = "ik_max_word")
    private String questionLibName;
    @JsonProperty("questionLibUrl")
    private String questionLibUrl;
    //1代表为私有 0代码并非私有
    @JsonProperty("hasPrivate")
    private int hasPrivate;
    @Field(type = FieldType.Date)
    @JsonProperty("questionLibCreateTime")
    private Date questionLibCreateTime;
    @JsonProperty("questionLibTagId")
    private int questionLibTagId;
    private String LibTagInfo;
    @Field(index = true,type = FieldType.Text,analyzer = "comma",searchAnalyzer = "comma")
    private String parentIds;
    @JsonProperty("questionLibMark")
    @Field(type = FieldType.Text,analyzer ="ik_max_word",searchAnalyzer = "ik_max_word")
    private String questionLibMark;
}
