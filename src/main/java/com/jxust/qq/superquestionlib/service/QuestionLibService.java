package com.jxust.qq.superquestionlib.service;

import com.alibaba.fastjson.JSONObject;
import com.jxust.qq.superquestionlib.dao.mapper.QuestionLibMapper;
import com.jxust.qq.superquestionlib.dto.Question;
import com.jxust.qq.superquestionlib.dto.QuestionLib;
import com.jxust.qq.superquestionlib.vo.QuestionLibVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
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
        // todo 添加userQuestionLib
        QuestionLib lib = new QuestionLib();
        lib.setQuestionLibLevel(0);
        lib.setQuestionLibCreateTime(LocalDateTime.now());
        lib.setQuestionLibTagId(tagId);
        lib.setQuestionLibUrl(libUrl);
        lib.setQuestionLibMark(mark);
        lib.setQuestionLibName(libName);
        libMapper.insertQuestionLib(lib);
        return (int) lib.getQuestionLibId();
    }

    /**
     *
     * @return
     */
    public QuestionLib findQuestionLibById(int id) {
        return libMapper.selectQuestionLibById(id);
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
        String saveName = username + "-lib-" + produceTime() + "." + suffix[suffix.length - 1];
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


    public JSONObject createQuestionByLibFile(String filename, int libId) throws IOException {
        DefaultBreakQuestion bqUtil = new DefaultBreakQuestion(filename);
        List<Question> cqList = bqUtil.breakQuestion();
        assert cqList != null;
        QuestionLibVO libVO = new QuestionLibVO();
        cqList.forEach(cq->{
            cq.setKeyword("");
            cq.setRightTime(0);
            cq.setRightTime(0);
            cq.setQuestionLibId(libId);
            cq.setQuestionLevel(0);
            cq.setCreateTime(LocalDateTime.now());
            cq.setLastModify(LocalDateTime.now());
            int qId = questionService.insert(cq);
        });
        JSONObject data = new JSONObject();
        data.put("isComplete", bqUtil.isComplete());
        data.put("data", cqList);
        return data;
    }

    private String produceTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }


    public List<QuestionLibVO> findPublicQuestionLibVOSPages(int page, int limit, Integer typeId) {
        assert page > 0;
        int total = (page - 1) * limit;
        return libMapper.selectPublicLibByPage(total, page, typeId);
    }

    public int findPublicQuestionLibSize() {

        return 0;
    }
}
