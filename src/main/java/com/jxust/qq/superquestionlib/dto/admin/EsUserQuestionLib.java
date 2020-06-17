package com.jxust.qq.superquestionlib.dto.admin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(indexName = "sq_user_question_lib",type = "user_question_lib",createIndex = true,refreshInterval = "-1")
public class EsUserQuestionLib
{
    @Id
    @JsonProperty("id")
    private int id;

    public EsUserQuestionLib(int id, String userName, int questionLibId, String questionLibName, String privateName, int questionLibImportance) {
        this.id = id;
        this.userName = userName;
        this.questionLibId = questionLibId;
        this.questionLibName = questionLibName;
        this.privateName = privateName;
        this.questionLibImportance = questionLibImportance;
    }

    public EsUserQuestionLib() {
    }

    @JsonProperty("userName")
    @Field(type = FieldType.Text,analyzer ="ik_max_word",searchAnalyzer = "ik_max_word")
    private String userName;
    @JsonProperty("questionLibId")
    private int questionLibId;
    @Field(type = FieldType.Text,analyzer ="ik_max_word",searchAnalyzer = "ik_max_word")
    private String questionLibName;
    @JsonProperty("privateName")
    @Field(type = FieldType.Text,analyzer ="ik_max_word",searchAnalyzer = "ik_max_word")
    private String privateName;
    @JsonProperty("questionLibImportance")
    private int questionLibImportance;
}
