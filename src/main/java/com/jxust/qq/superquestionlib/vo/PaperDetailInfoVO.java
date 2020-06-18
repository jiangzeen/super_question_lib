package com.jxust.qq.superquestionlib.vo;

import com.jxust.qq.superquestionlib.util.DateFormat;
import com.jxust.qq.superquestionlib.util.QuestionTypeEnum;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Data
public class PaperDetailInfoVO {

    private String startTime;
    private String endTime;
    private int limitTime;
    private Map<QuestionTypeEnum, PaperQuestionTypeSimpleInfo>
            questionDetailInfo;

    public PaperDetailInfoVO() {
        this.questionDetailInfo = new HashMap<>();
    }

    public void setTime(LocalDateTime startTime, LocalDateTime endTime, int limitTime) {
        // 对时间进行格式化(精确到秒)
        DateTimeFormatter timeFormatter =  DateFormat.SECOND_FORMATTER.getFormatter();
        this.startTime = startTime.format(timeFormatter);
        this.endTime = endTime.format(timeFormatter);
        this.limitTime = limitTime;
    }
}
