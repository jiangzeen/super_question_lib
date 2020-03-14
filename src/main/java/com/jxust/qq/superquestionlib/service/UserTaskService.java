package com.jxust.qq.superquestionlib.service;

import com.alibaba.fastjson.JSONObject;
import com.jxust.qq.superquestionlib.dao.mapper.UserTaskMapper;
import com.jxust.qq.superquestionlib.dto.UserTasks;
import com.jxust.qq.superquestionlib.vo.TaskDetailInfoVO;
import com.jxust.qq.superquestionlib.vo.TaskSimpleInfo;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.jxust.qq.superquestionlib.util.DateFormat.SECOND_FORMATTER;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

@Service
public class UserTaskService {

    private final UserTaskMapper taskMapper;
    private final UserQuestionHistoryService historyService;
    private final QuestionService questionService;

    public UserTaskService(UserTaskMapper taskMapper, UserQuestionHistoryService historyService,
                           QuestionService questionService) {
        this.taskMapper = taskMapper;
        this.historyService = historyService;
        this.questionService = questionService;
    }


    /**
     * 返回用户已经过期的任务详细信息
     * 过期任务的定义:
     * 1.expired值为1
     * 2.endTime字段为null
     * @param username 用户名
     * @return list
     */
    public List<TaskDetailInfoVO> findExpiredTasksByUsername(String username) {
        List<TaskDetailInfoVO> taskDetailInfoVOS = taskMapper.
                selectTasksByUsernameAndExpired(username);
        LocalDateTime now = LocalDateTime.now();
        taskDetailInfoVOS.forEach(it -> {
            LocalDateTime scheduleTime =  it.getScheduleTime();
            long expired = Duration.between(scheduleTime, now).toDays();
            it.setExpiredDayNum((int)expired);
        });
        return taskDetailInfoVOS;
    }


    public List<TaskDetailInfoVO> findUnCompleteTasksByUsername(String username) {
        return taskMapper.selectUnfinishedTask(username);
    }

    /**
     * 返回用户三类task的数量
     * 1.开始了但是截止日期未完成的task
     * 2.已经完成了的task
     * 3.未开始并且已过期的task
     * @return json,包含三类task的数量
     */
    public JSONObject findTaskCounts(String username) {
        JSONObject data = new JSONObject();
        data.put("expiredCount", taskMapper.selectExpiredTaskCount(username));
        data.put("unfinishedCount", taskMapper.selectUnfinishedCount(username));
        data.put("finishedCount", taskMapper.selectFinishedCount(username));
        return data;
    }

    public UserTasks addTask(String username, TaskSimpleInfo simpleInfo) {
        UserTasks task = new UserTasks();
        boolean customize = simpleInfo.isCustomize();
        StringBuilder builder = new StringBuilder();
        if (customize) {
            simpleInfo.getQuestionList().forEach(id->
                    builder.append(id).append(","));
            task.setCustomizeList(builder.toString());
        }
        task.setMark(simpleInfo.getMark());
        task.setExpired(0);
        task.setUsername(username);
        task.setQuestionLibId(simpleInfo.getLibId());
        task.setCreateTime(LocalDateTime.parse(
                simpleInfo.getCreateTime(), SECOND_FORMATTER.getFormatter()));
        task.setScheduleTime(LocalDateTime.parse(
                simpleInfo.getScheduleTime(), SECOND_FORMATTER.getFormatter()));
        task.setEndTime(LocalDateTime.parse(
                simpleInfo.getEndTime(), SECOND_FORMATTER.getFormatter()));
        task.setQuestionNumbers(simpleInfo.getQuestionNumber());
        taskMapper.insertUserTask(task);
        // 开启定时任务
        return task;
    }

    /**
     * 获取任务题库列表
     * @param taskId
     * @return
     */
    public List<Integer> taskQuestions(int taskId) {
        String questionIdString = taskMapper.selectQuestionList(taskId);
        int targetNumber = 0;
        int libId = 0;
        if (questionIdString == null) {
            /*
             *如何随机抽取题目?
             * 1.搜索历史做题记录,找出最近做错的n道题目
             * 2.若做题记录不足n道,再从题库抽取出不同类型n到题目
             */
            Map<String, Object> taskInfo = taskMapper.selectTaskLibId(taskId);
            targetNumber = (int) taskInfo.get("questionNumber");
            libId = (int) taskInfo.get("libId");
            // 获取到题目id列表类似于(1,2,3,4,...)
            questionIdString = historyService.findErrorQuestionFromHistory(
                   libId, (String) taskInfo.get("username"), targetNumber);
        }
        List<Integer> questionIdList;
        // 判断是否有现存列表
        if (questionIdString.equals("")) {
            questionIdList = new ArrayList<>();
        }else {
            questionIdList =
                    Arrays.stream(questionIdString.split(",")).
                            map(Integer::parseInt).collect(toList());
        }
        List<Integer> otherIdList;
        // 如果解析出来的history的题目数量不够的
        if (questionIdList.size() < targetNumber) {
            otherIdList = questionService.findOtherQuestionId(libId, questionIdList, targetNumber -
                    questionIdList.size());
            return Stream.of(questionIdList, otherIdList).flatMap(Collection::stream)
                    .collect(Collectors.toList());
        }
        return questionIdList;
    }


    public int findTaskLastingTimeById(int taskId) {
        UserTasks info = taskMapper.selectTaskById(taskId);
        if (info != null) {
            LocalDateTime start = info.getScheduleTime();
            LocalDateTime end = info.getEndTime();
            return (int) Duration.between(start, end).toMinutes();
        }else {
            throw new IllegalArgumentException("taskId不存在");
        }
    }

    public void modifyTaskState(int taskId) {
        taskMapper.updateUserTaskExpired(taskId);
    }

    public boolean verifyTask(int taskId, String username) {
        UserTasks tasks = taskMapper.selectTaskById(taskId);
        return tasks != null && tasks.getUsername().equals(username);
    }

    public boolean hasExpired(int taskId) {
        return taskMapper.selectExpiredById(taskId) == 1;
    }
}
