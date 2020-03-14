package com.jxust.qq.superquestionlib.dao.mapper;


import com.jxust.qq.superquestionlib.dto.UserTasks;
import com.jxust.qq.superquestionlib.vo.TaskDetailInfoVO;
import com.jxust.qq.superquestionlib.vo.TaskSimpleInfo;
import com.jxust.qq.superquestionlib.vo.UserTaskVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Map;

@Repository
public interface UserTaskMapper {

    void insertUserTask(@Param("task")UserTasks task);

    int selectExpiredById(int id);

    void updateUserTaskExpired(@Param("id") int id);

    String selectTaskUsername(@Param("id") int id);

    List<UserTaskVO> selectTasksByUsername(@Param("username") String username);
    
    List<TaskDetailInfoVO> selectTasksByUsernameAndExpired(@Param("username")String username);

    List<TaskDetailInfoVO> selectUnfinishedTask(String username);

    int selectExpiredTaskCount(String username);

    int selectUnfinishedCount(String username);

    int selectFinishedCount(String username);

    String selectQuestionList(int taskId);

    Map<String, Object> selectTaskLibId(int taskId);

    UserTasks selectTaskById(int taskId);
}
