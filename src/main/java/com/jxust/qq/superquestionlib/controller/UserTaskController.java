package com.jxust.qq.superquestionlib.controller;

import com.jxust.qq.superquestionlib.dto.Result;
import com.jxust.qq.superquestionlib.dto.UserTasks;
import com.jxust.qq.superquestionlib.service.QuestionService;
import com.jxust.qq.superquestionlib.service.task.ScheduleTaskService;
import com.jxust.qq.superquestionlib.service.UserQuestionLibService;
import com.jxust.qq.superquestionlib.service.UserTaskService;
import com.jxust.qq.superquestionlib.vo.QuestionVO;
import com.jxust.qq.superquestionlib.vo.TaskSimpleInfo;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(value = "/user/task")
public class UserTaskController {

    private final UserTaskService taskService;
    private final UserQuestionLibService libService;
    private final ScheduleTaskService scheduleTaskService;
    private final QuestionService questionService;


    public UserTaskController(UserTaskService taskService, UserQuestionLibService libService,
                              ScheduleTaskService scheduleTaskService, QuestionService questionService) {
        this.taskService = taskService;
        this.libService = libService;
        this.scheduleTaskService = scheduleTaskService;
        this.questionService = questionService;
    }




    @PostMapping("/add")
    public Result addTask(@RequestBody TaskSimpleInfo info) {
        int libId = info.getLibId();
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        // 要验证libId是否属于用户
        boolean access = libService.findByUsernameAndLibId(libId, username);
        if (access) {
            UserTasks task = taskService.addTask(username, info);
            System.out.println(task);
            int min = task.getScheduleTime().getMinute() - task.getCreateTime().getMinute();
            // 开启定时任务
            int state = scheduleTaskService.executorTask(task.getId(), task.getMark(),
                    username, min, TimeUnit.MINUTES);
            if (state == 1) {
                return Result.SUCCESS(null);
            }else {
                return Result.FAILD("任务已存在,启动失败");
            }
        }else {
            return Result.FAILD("题库无效,请重新选择");
        }
    }

    @PostMapping("/start/{taskId}")
    public Result startTask(@PathVariable int taskId) {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        if (!taskService.verifyTask(taskId, username)) {
            return Result.FAILD("该任务不存在");
        }
        if (taskService.hasExpired(taskId))  {
            return Result.PERMISSIONERROR();
        }
        List<Integer> questionIdList =  taskService.taskQuestions(taskId);
        List<QuestionVO> questionVOList = questionService.findQuestionListByIds(questionIdList);
        return Result.SUCCESS(questionVOList);
    }


    @GetMapping("/counts")
    public Result getTaskCounts() {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        return Result.SUCCESS(taskService.findTaskCounts(username));
    }

    @GetMapping("/expired/details")
    public Result getTaskDetails() {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        return Result.SUCCESS(taskService.findExpiredTasksByUsername(username));
    }

}
