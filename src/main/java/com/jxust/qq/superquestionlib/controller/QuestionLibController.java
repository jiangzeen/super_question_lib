package com.jxust.qq.superquestionlib.controller;

import com.alibaba.fastjson.JSONObject;
import com.jxust.qq.superquestionlib.dto.Result;
import com.jxust.qq.superquestionlib.dto.User;
import com.jxust.qq.superquestionlib.service.*;
import com.jxust.qq.superquestionlib.vo.QuestionLibVO;
import com.jxust.qq.superquestionlib.vo.QuestionVO;
import com.jxust.qq.superquestionlib.vo.UserVO;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * question-lib 对用户上传题库进行分类,打散取出题目,题库文件保存等
 * @author jiangzeen
 * @data   2019/10/2 20:05
 */
@RestController
public class QuestionLibController {

    private final QuestionLibService questionLibService;
    private final QuestionService questionService;
    private final UserQuestionLibService userQuestionLibService;
    private final LibTagService tagService;
    private final UserService userService;

    public QuestionLibController(QuestionLibService questionLibService, LibTagService tagService,
                                 QuestionService questionService, UserQuestionLibService userQuestionLibService,
                                 UserService userService) {
        this.questionLibService = questionLibService;
        this.tagService = tagService;
        this.questionService = questionService;
        this.userQuestionLibService = userQuestionLibService;
        this.userService = userService;
    }

    @PostMapping("/user/question_lib/create/{username}")
    public Result createLib(@PathVariable("username") String username, @RequestParam("question_file") MultipartFile file,
                            @RequestParam("tag_id") String tagId, @RequestParam("mark") String mark,
                            @RequestParam("hasPrivate") String hasPrivate, @RequestParam("privateName") String privateName) {
        String originName = file.getOriginalFilename();
        try {
            String saveUrl = questionLibService.saveOriginLibFile(file, username);
            // TODO 开启事务处理,防止如果解析失败等情况出现
            int libId = questionLibService.createQuestionLibByUser(username, originName, Integer.parseInt(tagId), saveUrl, mark, Integer.parseInt(hasPrivate));
            System.out.println("libId:" + libId);
            userQuestionLibService.saveUserLibRecord(username, libId, privateName);
            // 获取到原始上传文件在服务器中的位置 -- example: english.docx
            String fileUrl = questionLibService.getFileUrl(saveUrl);
            try {
                JSONObject res = questionLibService.createQuestionByLibFile(fileUrl, libId);
                return Result.SUCCESS(res);
            } catch (IOException e) {
                Result res = Result.FAILD("");
                res.setMessage("解析文件失败");
                return res;
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
            questionVOList.forEach(questionVO -> {
                questionService.updateContent(questionVO.getId(), questionVO.getContent());
            });
            return Result.SUCCESS(null);
        }
        Result res = Result.FAILD(null);
        res.setMessage("请检查该题库id是否正确");
        return res;
    }

    /**
     * 获取首页公共题库的信息,可以传入分类Id,获取专门的题库
     * @param page 页码 从1开始
     * @param map  分页参数以及分类参数
     * @return List<QuestionLibVO>
     */
    @GetMapping("/question_lib/public/{page}")
    public Result getPublicLib(@PathVariable("page") int page, @RequestBody Map<String, String> map) {
        int limit = Integer.parseInt(map.get("limit"));
        Integer typeId = null;
        if (map.containsKey("typeId")) {
            typeId = Integer.parseInt(map.get("typeId"));
        }
        List<QuestionLibVO> data = questionLibService.findPublicQuestionLibVOSPages(page, limit, typeId);
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
