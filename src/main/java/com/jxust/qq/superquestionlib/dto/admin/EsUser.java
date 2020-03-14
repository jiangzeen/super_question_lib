package com.jxust.qq.superquestionlib.dto.admin;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

@Data
@Document(indexName = "sq_user",type = "user",createIndex = true,refreshInterval = "-1")
public class EsUser
{
    @Id
    int userId;
    @Field(type = FieldType.Text,analyzer ="ik_max_word",searchAnalyzer = "ik_max_word")
    String userName;
    String userPassword;
    String userAvatar;
    @Field(type = FieldType.Text,analyzer ="ik_max_word",searchAnalyzer = "ik_max_word")
    String userNick;
    @Field(type = FieldType.Date)
    Date userLastLoginTime;
    @Field(type = FieldType.Date)
    Date userCreatTime;
    int userSex;
    int userSchoolId;
    String parentIds;
    String userSchoolInfo;
}
