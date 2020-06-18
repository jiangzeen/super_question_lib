package com.jxust.qq.superquestionlib.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jxust.qq.superquestionlib.dao.mapper.UserQuestionLibMapper;
import com.jxust.qq.superquestionlib.dto.UserQuestionLib;
import com.jxust.qq.superquestionlib.vo.LibSimpleInfoVO;
import com.jxust.qq.superquestionlib.vo.QuestionLibVO;
import com.jxust.qq.superquestionlib.vo.QuestionVO;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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

    /**
     * 根据题库Id查找user-lib-id
     * @param libId 题库Id
     * @param username 用户名
     * @return userLibId || -1
     */
    public int findIdByUsernameAndLibId(int libId, String username) {
        UserQuestionLib userLib =
                userQuestionLibMapper.selectByLibIdAndUsername(username, libId);
        if (userLib != null) {
            return userLib.getId();
        }
        return -1;
    }

    public int saveUserLibRecord(String username, int libId, String privateName,
                                  String privateMark) {
        UserQuestionLib userLib = new UserQuestionLib();
        userLib.setUsername(username);
        userLib.setPrivateName(privateName);
        userLib.setQuestionLibId(libId);
        userLib.setQuestionLibImportance(0);
        userLib.setPrivateMark(privateMark);
        userQuestionLibMapper.insertUserLib(userLib);
        return userLib.getId();
    }

    public List<QuestionLibVO> findUserLibByUsername(String username, Integer typeId,
                                                     int page, int limit) {
        assert username != null;
        assert page > 1;
        int total = (page - 1) * limit;
        List<QuestionLibVO> libVOS = userQuestionLibMapper.selectLibByUsername(username, typeId, limit, total);
        System.out.println(JSONObject.toJSONString(libVOS, true));
        libVOS.forEach(vo-> {
            if (vo.getMark() == null) {
                vo.setMark("");
            }
            if (vo.getPrivateName() == null) {
                vo.setPrivateName("");
            }
        });
        System.out.println(JSONObject.toJSONString(libVOS, true));
        return libVOS;
    }

    public QuestionLibVO findQuestionsByUsernameAndId(String username, int libId, Integer typeId) {
        QuestionLibVO libVO = userQuestionLibMapper.selectQuestionsByIdAndUsername(username, libId, typeId);
        if (typeId == null) {
            return libVO;
        }
        List<QuestionVO> typeQuestionVo = libVO.getQuestions().stream().filter(questionVO -> {
            JSONObject question = JSON.parseObject(questionVO.getContent());
            return question.get("type") != null && question.get("type") == typeId;
        }).collect(Collectors.toList());
        libVO.setQuestions(typeQuestionVo);
        return libVO;
    }

    public void modifyLibInfo(LibSimpleInfoVO vo) {
        String username = String.valueOf(SecurityUtils.getSubject().
                getPrincipal());
        if (!findByUsernameAndLibId(vo.getId(), username)
                || (vo.getName() == null && vo.getMark() == null)) {
            throw new IllegalArgumentException();
        }
        userQuestionLibMapper.updateQuestionLib(username, vo);
    }

    public int findUserLibTotals(String username) {
        return userQuestionLibMapper.selectUserLibsNumbers(username);
    }

    public void deleteUserLibById(int userLibId) {
        userQuestionLibMapper.deleteById(userLibId);
    }
}
