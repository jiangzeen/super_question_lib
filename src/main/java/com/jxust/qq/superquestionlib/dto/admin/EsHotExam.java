package com.jxust.qq.superquestionlib.dto.admin;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Setting(settingPath = "user.json")
@Document(indexName = "sq_hot_exam",type = "hot_exam",createIndex = true,refreshInterval = "-1")
public class EsHotExam  implements Serializable{
    @JsonProperty("id")
    @Id
    private int id;
    @JsonProperty("examName")
    @Field(type = FieldType.Text,analyzer ="ik_max_word",searchAnalyzer = "ik_max_word")
    private String examName;
    @Field(index = false,ignoreFields = {"examTagInfo"},type = FieldType.Text)
    private String examTagInfo;
    @JsonProperty("examStartTime")
    @Field(type = FieldType.Date)
    private Date examStartTime;
    @JsonProperty("examToStartTime")
    private int examToStartTime;
    @JsonProperty("examTimeLevel")
    private int examTimeLevel;
    // 根据分隔符,分割出多个id
    @JsonProperty("tagIds")
    @Field(index = true,type = FieldType.Text,analyzer = "comma",searchAnalyzer = "comma")
    private String tagIds;
    public EsHotExam()
    {
    }
}
