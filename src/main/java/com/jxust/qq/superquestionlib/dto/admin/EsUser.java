package com.jxust.qq.superquestionlib.dto.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Setting(settingPath = "user.json")
@Document(indexName = "sq_user",type = "user",createIndex = true,refreshInterval = "-1")
public class EsUser
{
    @Id
    @JsonProperty("userId")
    int userId;
    @Field(type = FieldType.Text,analyzer ="ik_max_word",searchAnalyzer = "ik_max_word")
    @JsonProperty("userName")
    String userName;
    @JsonProperty("userPassword")
    String userPassword;
    @JsonProperty("userAvatar")
    String userAvatar;
    @JsonProperty("userNick")
    @Field(type = FieldType.Text,analyzer ="ik_max_word",searchAnalyzer = "ik_max_word")
    String userNick;
    @JsonProperty("userLastLoginTime")
    @Field(type = FieldType.Date)
    Date userLastLoginTime;
    @JsonProperty("userCreateTime")
    @Field(type = FieldType.Date)
    Date userCreateTime;
    @JsonProperty("userSex")
    int userSex;
    @JsonProperty("userSchoolId")
    int userSchoolId;
    @Field(index = true,type = FieldType.Text,analyzer ="comma",searchAnalyzer = "comma")
    String parentIds;
    String userSchoolInfo;
}
