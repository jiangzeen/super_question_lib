package com.jxust.qq.superquestionlib.dto.admin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(indexName = "sq_question",type = "question",createIndex = true,refreshInterval = "-1")
public class EsQuestion {

    @Id
    @JsonProperty("questionId")
    private long questionId;
    @JsonProperty("questionLevel")
    private int  questionLevel;
    @JsonProperty("questionLibId")
    private long questionLibId;
    @Field(type = FieldType.Text,analyzer ="ik_max_word",searchAnalyzer = "ik_max_word")
    private String questionLibName;
    @JsonProperty("questionTypeId")
    private long questionTypeId;
    @Field(type = FieldType.Text,analyzer ="ik_max_word",searchAnalyzer = "ik_max_word")
    private String questionContent;
    @JsonProperty("rightTime")
    private long rightTime;
    @JsonProperty("wrongTime")
    private long wrongTime;
    @JsonProperty("keyword")
    @Field(type = FieldType.Text,analyzer ="ik_max_word",searchAnalyzer = "ik_max_word")
    private String keyword;
    @JsonProperty("creatTime")
    @Field(type = FieldType.Date)
    private Date createTime;
    @Field(type = FieldType.Date)
    private Date lastModify;
}
