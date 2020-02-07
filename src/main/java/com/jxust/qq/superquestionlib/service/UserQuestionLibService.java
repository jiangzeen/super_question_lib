package com.jxust.qq.superquestionlib.service;

import com.jxust.qq.superquestionlib.dao.mapper.UserQuestionLibMapper;
import com.jxust.qq.superquestionlib.dto.UserQuestionLib;
import com.jxust.qq.superquestionlib.vo.QuestionLibVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserQuestionLibService {
    private final UserQuestionLibMapper userQuestionLibMapper;

    public UserQuestionLibService(UserQuestionLibMapper userQuestionLibMapper) {
        this.userQuestionLibMapper = userQuestionLibMapper;
    }

    public boolean findByUsernameAndLibId(int id, String username){
        assert username != null;
        return userQuestionLibMapper.selectByLibIdAndUsername(username, id) != null;
    }

    public void saveUserLibRecord(String username, int libId, String privateName) {
        UserQuestionLib userLib = new UserQuestionLib();
        userLib.setUsername(username);
        userLib.setPrivateName(privateName);
        userLib.setQuestionLibId(libId);
        userLib.setQuestionLibImportance(0);
        userQuestionLibMapper.insertUserLib(userLib);
    }

    public List<QuestionLibVO> findUserLibByUsername(String username, Integer typeId,
                                                     int page, int limit) {
        assert username != null;
        assert page > 1;
        int total = (page - 1) * limit;
        return userQuestionLibMapper.selectLibByUsername(username, typeId, limit, total);
    }

    public QuestionLibVO findQuestionsByUsernameAndId(String username, int libId, Integer typeId) {
        return userQuestionLibMapper.selectQuestionsByIdAndUsername(username, libId, typeId);
    }
}
