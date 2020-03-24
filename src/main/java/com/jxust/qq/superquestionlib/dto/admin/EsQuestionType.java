package com.jxust.qq.superquestionlib.dto.admin;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName = "sq_question_type",type = "question_type",createIndex = true,refreshInterval = "-1")
public class EsQuestionType {
    @Id
    private int questionTypeId;
    @Field(type = FieldType.Text,analyzer ="ik_max_word",searchAnalyzer = "ik_max_word")
    private String questionTypeName;
}