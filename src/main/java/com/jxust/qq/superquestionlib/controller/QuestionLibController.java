package com.jxust.qq.superquestionlib.controller;

import com.alibaba.fastjson.JSONObject;
import com.jxust.qq.superquestionlib.dto.Question;
import com.jxust.qq.superquestionlib.dto.Result;
import com.jxust.qq.superquestionlib.dto.User;
import com.jxust.qq.superquestionlib.service.*;
import com.jxust.qq.superquestionlib.vo.LibSimpleInfoVO;
import com.jxust.qq.superquestionlib.vo.QuestionLibVO;
import com.jxust.qq.superquestionlib.vo.QuestionVO;
import com.jxust.qq.superquestionlib.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * question-lib 对用户上传题库进行分类,打散取出题目,题库文件保存等
 * @author jiangzeen
 * @data   2019/10/2 20:05
 */
@Slf4j
@RestController
public class QuestionLibController {

    private final QuestionLibService questionLibService;
    private final QuestionService questionService;
    private final UserQuestionLibService userQuestionLibService;
    private final LibTagService tagService;
    private final UserService userService;
    // 后面加的一个ThreadLocal属性,用于存放当前线程用户名(不用参数传递了,需要改好几个地方,比较麻烦)
    public static final ThreadLocal<String> userLocal = new ThreadLocal<>();
    private final UserQuestionService userQuestionService;

    public QuestionLibController(QuestionLibService questionLibService, LibTagService tagService,
                                 QuestionService questionService, UserQuestionLibService userQuestionLibService,
                                 UserService userService, UserQuestionService userQuestionService) {
        this.questionLibService = questionLibService;
        this.tagService = tagService;
        this.questionService = questionService;
        this.userQuestionLibService = userQuestionLibService;
        this.userService = userService;
        this.userQuestionService = userQuestionService;
    }

    @SuppressWarnings("unchecked")
    @PostMapping("/user/question_lib/create/{username}")
    public Result createLib(@PathVariable("username") String username, @RequestParam("question_file") MultipartFile file,
                            @RequestParam("tag_id") String tagId, @RequestParam("mark") String mark,
                            @RequestParam("hasAnswer") boolean hasAnswer, @RequestParam("hasPrivate") String hasPrivate,
                            @RequestParam("privateName") String privateName) {
        String user = (String) SecurityUtils.getSubject().getPrincipal();
        if (!user.equals(username)) {
            return Result.PERMISSIONERROR();
        }
        String originName = file.getOriginalFilename();
        userLocal.set(username);
        int libId, userLibId;
        String saveUrl;
        try {
            saveUrl = questionLibService.saveOriginLibFile(file, username);
            log.info("用户{}上传题库文件:{}", username, originName);
            libId = questionLibService.createQuestionLibByUser(username, originName, Integer.parseInt(tagId), saveUrl, mark,
                    Integer.parseInt(hasPrivate));
            userLibId = userQuestionLibService.saveUserLibRecord(username, libId, privateName, mark);
            // 获取到原始上传文件在服务器中的位置 -- example: english.docx
            String fileUrl = questionLibService.getFileUrl(saveUrl);
            try {
                JSONObject res = questionLibService.createQuestionByLibFile(fileUrl, libId, hasAnswer);
                List<Question> questions = (List<Question>) res.get("data");
                if (questions.size() == 0) {
                    return Result.PARAMS_ERROR(res);
                }
                userQuestionService.addUserQuestion(questions);
                return Result.SUCCESS(res);
            } catch (IOException e) {
                // 删除记录
                deleteFaildRecord(libId, userLibId);
                // 删除文件
                questionLibService.deleteLibFile(saveUrl);
                return Result.FAILD("解析文件失败");
            }
        } catch (NullPointerException e) {
            Result res = Result.FAILD(null);
            res.setMessage("操作失败,上传文件不能为null");
            return res;
        } catch (IllegalArgumentException e) {
            Result res = Result.FAILD(null);
            res.setMessage("操作失败,上传文件必须为doc,docx或pdf文件");
            return res;
        }
    }

    private void deleteFaildRecord(int libId, int userLibId) {
        if (questionLibService.deleteLibById(libId)) {
            userQuestionLibService.deleteUserLibById(userLibId);
        }
    }



    @PostMapping("/user/lib/modify_info")
    public Result changeLibName(@RequestBody LibSimpleInfoVO libVo) {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        try {
            userQuestionLibService.modifyLibInfo(libVo);
            return Result.SUCCESS();
        } catch (IllegalArgumentException e) {
            return Result.PARAMS_ERROR();
        }
    }

    @GetMapping("/question_lib/tags")
    public Result getTags() {
        JSONObject data = tagService.getAllTagsWithChildrenTag();
        return Result.SUCCESS(data);
    }

    /**
     * 补充题库的内容,适用于以下情形:
     * 对于解析的题库回显后,用户重新修改未解析正确的内容或添加未解析出来的内容
     * @return res
     */
    @PostMapping("/user/question_lib/completion")
    public Result completionLib(@RequestBody QuestionLibVO libVO) {
        Subject subject = SecurityUtils.getSubject();
        String username = (String) subject.getPrincipal();
        int libId = libVO.getLibId();
        boolean userlib = userQuestionLibService.findByUsernameAndLibId(libId, username);
        if (userlib) {
            List<QuestionVO> questionVOList = libVO.getQuestions();
            questionVOList.forEach(questionVO ->
                    questionService.updateContent(questionVO.getId(), questionVO.getContent()));
            return Result.SUCCESS(null);
        }
        Result res = Result.FAILD(null);
        res.setMessage("请检查该题库id是否正确");
        return res;
    }

    /**
     * 删除上传的题库记录与用户题库记录
     * @param id 题库Id
     */
    @PostMapping("/user/question_lib/delete")
    public Result deleteLibId(@RequestParam("id") int id) {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        questionLibService.deleteLibById(id);
        int userId = userQuestionLibService.findIdByUsernameAndLibId(id, username);
        userQuestionLibService.deleteUserLibById(userId);
        return Result.SUCCESS();
    }

    /**
     * 获取首页公共题库的信息,可以传入分类Id,获取专门的题库
     * @param page 页码 从1开始
     * @return List<QuestionLibVO>
     */
    @GetMapping("/question_lib/public/{page}")
    public Result getPublicLib(@PathVariable("page") int page, @RequestParam("limit") int limit,
                               @RequestParam(value = "typeId", required = false) Integer typeId) {
        List<QuestionLibVO> data = questionLibService.findPublicQuestionLibVOSPages(page, limit, typeId);
        log.info("public lib info:{}", data);
        return Result.SUCCESS(data);
    }

    /**
     * 获取题库作者信息
     * @param username 用户名
     * @return UserVO
     */
    @GetMapping("/question_lib/public/author_info/{username}")
    public Result getAuthorInfo(@PathVariable String username) {
        User user = userService.findUser(username);
        UserVO userVO = new UserVO(username, user.getUserNick());
        userVO.setAvatar(user.getUserAvatar());
        userVO.setLastLoginTime(user.getUserLastLoginTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm")));
        return Result.SUCCESS(userVO);
    }
}
