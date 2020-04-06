package com.jxust.qq.superquestionlib.controller.admin;

import com.alibaba.fastjson.JSONObject;
import com.jxust.qq.superquestionlib.dao.mapper.admin.interfaces.AdminLoginToken;
import com.jxust.qq.superquestionlib.dto.LibTag;
import com.jxust.qq.superquestionlib.dto.Result;
import com.jxust.qq.superquestionlib.dto.admin.EsLibTag;
import com.jxust.qq.superquestionlib.dto.admin.EsQuestion;
import com.jxust.qq.superquestionlib.service.admin.EsLibTagService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;

@Slf4j
@RestController
public class EsLibTagController
{
    private final EsLibTagService libTagService;

    public EsLibTagController(EsLibTagService libTagService) {
        this.libTagService = libTagService;
    }
    @AdminLoginToken
    @PostMapping("admin/libTag/fuzzyQuery")
    public Result fuzzyQuery(@RequestParam("queryString") String queryString, @RequestParam("parentTagId")int parentTagId)
    {
        Iterable<EsLibTag> esLibTags=libTagService.boolQuery(queryString,parentTagId);
        ArrayList<TagInfo> infos=new ArrayList<>();
        infos.add(new TagInfo(1000,"其他"));
        for (EsLibTag esLibTag : esLibTags)
        {
            infos.add(new TagInfo(esLibTag.getTagId(),esLibTag.getTagName()));
        }
        JSONObject data=new JSONObject();
        data.put("esTagInfos",infos);
        data.put("pagesum",EsLibTagService.pagesum);
        return Result.SUCCESS(data);
    }
    @AdminLoginToken
    @GetMapping("admin/libTag/deleteById/{id}")
    public Result deleteById(@PathVariable("id")long id)
    {
        JSONObject data = libTagService.deleteLibTags(id);
        return Result.SUCCESS(data);
    }
    @AdminLoginToken
    @PostMapping("admin/libTag/create")
    public Result create(@RequestBody EsLibTag libTag)
    {
        int status=libTagService.creatLibTags(libTag);
        JSONObject data=new JSONObject();
        if (status <= 0) {
            return Result.SERVERERROR();
        }
        else{
            return Result.SUCCESS(data);
        }
    }
    //修改
    @AdminLoginToken
    @PostMapping("admin/libTag/updateById")
    public Result updateById(@RequestBody EsLibTag libTag)
    {
        int status=libTagService.updateLibTags(libTag);
        JSONObject data=new JSONObject();
        if (status <= 0) {
            return Result.SERVERERROR();
        }
        else{
            return Result.SUCCESS(data);
        }
    }
}
@Data
class TagInfo
{
    long id;
    String libTagName;
    public TagInfo(long id, String libTagName) {
        this.id = id;
        this.libTagName = libTagName;
    }
}
