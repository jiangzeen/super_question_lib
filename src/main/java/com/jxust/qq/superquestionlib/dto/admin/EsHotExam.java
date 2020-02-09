package com.jxust.qq.superquestionlib.dto.admin;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.time.LocalTime;

@Data
@Document(indexName = "sq_hot_exam",type = "hot_exam",createIndex = false,refreshInterval = "-1")
public class EsHotExam implements Serializable {
    @Id
    private int id;
    @Field(type = FieldType.Text,analyzer ="ik_max_word",searchAnalyzer = "ik_max_word")
    private String examName;
    private int examTimeLevel;
    private LocalTime examStartTime;
    private LocalTime examToStartTime;
    // 根据分隔符,分割出多个id
    private String tagIds;
    private String flag;
}
