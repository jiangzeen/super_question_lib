package com.jxust.qq.superquestionlib.service;

import com.jxust.qq.superquestionlib.dao.mapper.QuestionNoteMapper;
import com.jxust.qq.superquestionlib.dto.QuestionNote;
import com.jxust.qq.superquestionlib.vo.QuestionNoteVO;
import org.apache.shiro.crypto.hash.Hash;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QuestionNoteService {
    private final QuestionNoteMapper noteMapper;

    public QuestionNoteService(QuestionNoteMapper noteMapper) {
        this.noteMapper = noteMapper;
    }

    public int addQuestionNote(QuestionNote questionNote) {
        noteMapper.insertNote(questionNote);
        return questionNote.getNoteId();
    }

    public List<QuestionNoteVO> findNotesByQuestionId(int questionId) {
        return noteMapper.selectNoteByQuestionId(questionId);
    }

    public List<QuestionNoteVO> findNoteByOtherUsername(String username, int questionId) {
        // todo 要对两道题目做相似度检测
        return noteMapper.selectNoteByOtherUsernameAndId(username, questionId);
    }

    /**
     * 查找一个用户所有分类下的所有笔记
     * 1.查找用户题库的分类
     * 2.根据分类找到相应的题目
     * 3.找到题目的笔记
     * @param username 用户名
     * @return 分类
     */
    public Map<Integer, List<HashMap<String, Object>>> findAllNoteByType(String username) {
        List<HashMap<String, Object>> dataMap = noteMapper.selectNotesByType(username);
        Map<Integer, List<HashMap<String, Object>>> res = new HashMap<>();
        dataMap.forEach(map -> {
            int typeId = Integer.parseInt(map.get("tagId").toString());
            if (!res.containsKey(typeId)) {
                List<HashMap<String, Object>> hashMapList = new ArrayList<>();
                hashMapList.add(map);
                res.put(typeId, hashMapList);
            }else {
                res.get(typeId).add(map);
            }
        });
        return res;
    }

    public void updateNoteById(QuestionNote note) throws IllegalArgumentException{
        QuestionNote daoNote = noteMapper.selectNoteById(note.getNoteId());
        if (daoNote == null) {
            throw new IllegalArgumentException();
        }
        noteMapper.updateById(note.getCreateTime(), note.getNoteId(),
                note.getContent());
    }
}
