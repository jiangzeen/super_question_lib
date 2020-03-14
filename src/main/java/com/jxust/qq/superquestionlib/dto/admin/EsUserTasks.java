package com.jxust.qq.superquestionlib.dto.admin;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalTime;
import java.util.Date;

@Data
@Document(indexName = "sq_user_tasks",type = "user_tasks",createIndex = true,refreshInterval = "-1")
public class EsUserTasks
{
    /**
     * id int(10) auto_increment
     *         primary key,
     *     user_name varchar(20) not null,
     *     mark varchar(100) default '' not null, 备忘录
     *     question_lib_id int not null comment '该任务对应的题库(从该题库抽取题目)',
     *     question_numbers tinyint default 0 not null comment '规定的任务量',
     *     create_time timestamp default CURRENT_TIMESTAMP not null,
     *     end_time timestamp null comment '用户完成日期(例如用户提前完成任务)',
     *     schedule_time timestamp null comment '用户规定的任务截止日期',
     *     expired tinyint default 0 null comment '任务是否过期,0代表没过期,1代表过期',
     *     has_complete_numbers tinyint default 0 null comment '用户已经完成的题目数量'
     */
    @Id
    int id;
    @Field(index = true,type = FieldType.Text,analyzer ="ik_max_word",searchAnalyzer = "ik_max_word")
    String userName;
    @Field(index = true,type = FieldType.Text,analyzer ="ik_max_word",searchAnalyzer = "ik_max_word")
    String mark;
    int question_lib_id;
    @Field(index = true,type = FieldType.Text,analyzer ="ik_max_word",searchAnalyzer = "ik_max_word")
    String questionLibName;
    int questionNumbers;
    @Field(type = FieldType.Date)
    Date creatTime;
    @Field(type = FieldType.Date)
    Date endTime;
    @Field(type = FieldType.Date)
    Date scheduleTime;
    int expired;
    int hasCompleteNumbers;
}
