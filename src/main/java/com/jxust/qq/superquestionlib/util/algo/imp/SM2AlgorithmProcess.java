package com.jxust.qq.superquestionlib.util.algo.imp;

import com.jxust.qq.superquestionlib.dto.UserQuestion;
import com.jxust.qq.superquestionlib.util.algo.QuestionProcess;
import com.jxust.qq.superquestionlib.util.algo.QuestionQuality;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class SM2AlgorithmProcess implements QuestionProcess {

    private int repetitions;
    private float easiness;
    private int interval;


    public SM2AlgorithmProcess() {
        easiness = 2.5f;
        interval = 0;
        repetitions = 0;
    }

    @Override
    public UserQuestion process(UserQuestion question, QuestionQuality quality) throws IllegalArgumentException {
        repetitions = question.getRepetitions();
        easiness = question.getEasiness();
        interval = question.getInterval();
        if (quality == null) {
            throw new IllegalArgumentException("quality must not be null");
        }
        // 计算一道题目的难易程度 [1.3, 2.5]
        easiness = (float) Math.max(1.3, easiness + 0.1 -
                (5.0 - quality.getId()) * (0.08 + (5.0 - quality.getId()) * 0.02));
        /*
         给定已经学习了的标准: 当quality(熟悉程度)大于3即已经学习了
         */
        if (quality.getId() < 3) {
            repetitions = 0;
        } else {
            repetitions += 1;
        }
        // 判断已经重复学习的次数, 给定下次学习的间隔
        if (repetitions <= 1) {
            interval = 1;
        } else if (repetitions == 2){
            interval = 6;
        } else {
            interval = Math.round(easiness * interval);
        }

        // 计算下一次学习的时间
        question.setInterval(interval);
        question.setEasiness(easiness);
        question.setRepetitions(repetitions);

        return question;
    }
}
