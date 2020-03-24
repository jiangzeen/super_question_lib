package com.jxust.qq.superquestionlib.dto.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.util.Date;

@Data
@Setting(settingPath = "user.json")
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
    Date userCreateTime;
    int userSex;
    int userSchoolId;
    @Field(index = true,type = FieldType.Text,analyzer ="comma",searchAnalyzer = "comma")
    String parentIds;
    String userSchoolInfo;
}
