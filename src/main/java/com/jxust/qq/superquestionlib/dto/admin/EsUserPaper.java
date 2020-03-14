package com.jxust.qq.superquestionlib.dto.admin;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import java.util.Date;

@Data
@Document(indexName = "sq_user_paper",type = "user_paper",createIndex = true,refreshInterval = "-1")
public class EsUserPaper
{
    @Id
    private int id;
    @Field(index = true,type = FieldType.Text,analyzer ="ik_max_word",searchAnalyzer = "ik_max_word")
    private String username;
    private int questionLibId;
    @Field(index = true,type = FieldType.Text,analyzer ="ik_max_word",searchAnalyzer = "ik_max_word")
    private String questionLibName;
    @Field(type = FieldType.Date)
    private Date startTime;
    private int limitTime;
    @Field(type = FieldType.Date)
    private Date endTime;
    @Field(index = true,type = FieldType.Text,analyzer ="ik_max_word",searchAnalyzer = "ik_max_word")
    private String result;
}
