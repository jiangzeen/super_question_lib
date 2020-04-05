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
@Document(indexName = "sq_user_paper",type = "user_paper",createIndex = true,refreshInterval = "-1")
public class EsUserPaper
{
    @JsonProperty("id")
    @Id
    private int id;
    @JsonProperty("userName")
    @Field(index = true,type = FieldType.Text,analyzer ="ik_max_word",searchAnalyzer = "ik_max_word")
    private String userName;
    @JsonProperty("questionLibId")
    private int questionLibId;
    @Field(index = true,type = FieldType.Text,analyzer ="ik_max_word",searchAnalyzer = "ik_max_word")
    private String questionLibName;
    @JsonProperty("startTime")
    @Field(type = FieldType.Date)
    private Date startTime;
    private int limitTime;
    @JsonProperty("endTime")
    @Field(type = FieldType.Date)
    private Date endTime;
    @JsonProperty("paperResult")
    @Field(index = true,type = FieldType.Text,analyzer ="ik_max_word",searchAnalyzer = "ik_max_word")
    private String paperResult;
}
