package com.jxust.qq.superquestionlib.dto.admin;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName = "sq_school_info",type = "school_info",createIndex = true,refreshInterval = "-1")
public class EsSchoolInfo {

    @Id
    private int schoolId;
    @Field(index = true,type = FieldType.Text,analyzer ="ik_max_word",searchAnalyzer = "ik_max_word")
    private String schoolName;
    private int schoolParentId;
}
