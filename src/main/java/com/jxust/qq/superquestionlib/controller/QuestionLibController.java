package com.jxust.qq.superquestionlib.controller;

import com.alibaba.fastjson.JSONObject;
import com.jxust.qq.superquestionlib.po.Result;
import com.jxust.qq.superquestionlib.service.LibTagService;
import com.jxust.qq.superquestionlib.service.QuestionLibService;
import com.jxust.qq.superquestionlib.util.QuestionMark;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * question-lib 对用户上传题库进行分类,打散取出题目,题库文件保存等
 * @author jiangzeen
 * @data   2019/10/2 20:05
 */
@RestController
public class QuestionLibController {

    private final QuestionLibService questionLibService;
    private final LibTagService tagService;

    public QuestionLibController(QuestionLibService questionLibService, LibTagService tagService) {
        this.questionLibService = questionLibService;
        this.tagService = tagService;
    }

    @PostMapping("/user/question_lib/create/{username}")
    public Result createLib(@PathVariable("username") String username, @RequestParam("question_file") MultipartFile file,
                            @RequestParam("tag_id") String tagId, @RequestParam("mark") String mark,
                            @RequestParam("request_mark")List<QuestionMark> questionMarkList) {
        String originName = file.getOriginalFilename();
        try {
            String saveUrl = questionLibService.saveOriginLibFile(file, username);
            int libId = questionLibService.createQuestionLibByUser(username, originName, Integer.valueOf(tagId), saveUrl, mark);
            String fileUrl = questionLibService.getFileUrl(saveUrl);
            //TODO 修改该方法的参数--需要获取到QuestionMark参数进行分解Question
            try {
                questionLibService.createQuestionByLibFile(fileUrl, null, libId, Integer.valueOf(tagId));
            } catch (FileNotFoundException e) {
                Result res = Result.FAILD(null);
                res.setMessage("服务器内部错误!");
                return res;
            }
            return Result.SUCCESS(null);
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

}
