package com.jxust.qq.superquestionlib.service;

import com.jxust.qq.superquestionlib.dao.mapper.QuestionLibMapper;
import com.jxust.qq.superquestionlib.po.Question;
import com.jxust.qq.superquestionlib.po.QuestionLib;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * @author jiangzeen
 * @data 2019/10/2 20:00
 */
@Service
@Slf4j
public class QuestionLibService {

    private final static String FILE_DIR = "/home/jiangzeen/project_log/upload_question_lib/";
    private final QuestionService questionService;
    private final QuestionLibMapper libMapper;
    private final UserService userService;

    public QuestionLibService(QuestionLibMapper libMapper, UserService userService, QuestionService service) {
        this.libMapper = libMapper;
        this.userService = userService;
        this.questionService = service;
    }


    public int createQuestionLibByUser (String username, String libName,
                                         int tagId, String libUrl, String mark, int hasPrivate) {
        long userId = userService.findUser(username).getUserId();
        QuestionLib lib = new QuestionLib();
        lib.setQuestionLibLevel(0);
        lib.setQuestionLibCreateTime(LocalDateTime.now());
        lib.setQuestionLibTagId(tagId);
        lib.setQuestionLibUrl(libUrl);
        lib.setQuestionLibMark(mark);
        lib.setQuestionLibName(libName);
        return libMapper.insertQuestionLib(lib);
    }
    /**
     * 保存题库文件到服务器
     * 题库文件必须以 .doc || .docx || .pdf 结尾
     * 题库文件命名:userame+"-" + lib + UUID + 后缀名
     * @param questionLibFile 题库文件
     * @param username 上传题库用户名
     */
    public String saveOriginLibFile(MultipartFile questionLibFile, String username) throws IllegalArgumentException, NullPointerException{
        if (questionLibFile == null || questionLibFile.getOriginalFilename() == null) {
            throw new NullPointerException();
        }
        log.info("获取到上传的题库文件:{}", questionLibFile.getOriginalFilename());
        String originname = questionLibFile.getOriginalFilename();
        Pattern rex = Pattern.compile(".*(.doc)$|.*(.docx)$|.*(.pdf)$");
        if (!rex.matcher(originname).find()) {
            throw new IllegalArgumentException();
        }
        String[] suffix = originname.split("//.");
        String saveName = username + "-lib-" + UUID.randomUUID() + "." + suffix[suffix.length - 1];
        try {
            InputStream in = questionLibFile.getInputStream();
            FileOutputStream out = new FileOutputStream(FILE_DIR + saveName);
            byte[] bb = new byte[1024];
            while (in.read(bb) != -1) {
                out.write(bb);
            }
            return saveName;
        } catch (IOException e) {
            return null;
        }
    }

    public String getFileUrl(String filename) {
        return FILE_DIR + filename;
    }


    public void createQuestionByLibFile(String filename, int libId) throws IOException {
        DefaultBreakQuestion bqUtil = new DefaultBreakQuestion(filename);
        List<Question> cqList = bqUtil.breakQuestion();
        assert cqList != null;
        cqList.forEach(cq->{
            // TODO 添加题目
            cq.setKeyword("");
            cq.setRightTime(0);
            cq.setRightTime(0);
            cq.setQuestionLibId(libId);
            cq.setQuestionLevel(0);
            cq.setCreateTime(LocalDateTime.now());
            cq.setLastModify(LocalDateTime.now());
            questionService.insert(cq);
        });
    }

}
