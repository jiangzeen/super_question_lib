package com.jxust.qq.superquestionlib.controller.admin;

import com.alibaba.fastjson.JSONObject;
import com.jxust.qq.superquestionlib.dto.Result;
import com.jxust.qq.superquestionlib.dto.admin.EsLibTag;
import com.jxust.qq.superquestionlib.dto.admin.EsSchoolInfo;
import com.jxust.qq.superquestionlib.service.admin.EsLibTagService;
import lombok.Data;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;

public class EsLibTagController
{
    private final EsLibTagService libTagService;

    public EsLibTagController(EsLibTagService libTagService) {
        this.libTagService = libTagService;
    }
    @PostMapping("/admin/libTag/fuzzyQuery")
    public Result fuzzyQuery(@Param("queryString") String queryString, @Param("parentTagId")int parentTagId)
    {
        Iterable<EsLibTag> esLibTags=libTagService.boolQuery(queryString,parentTagId);
        ArrayList<TagInfo> infos=new ArrayList<>();
        infos.add(new TagInfo(100,"其他"));
        for (EsLibTag esLibTag : esLibTags)
        {
            infos.add(new TagInfo(esLibTag.getTagId(),esLibTag.getTagName()));
        }
        JSONObject data=new JSONObject();
        data.put("esTagInfos",infos);
        return Result.SUCCESS(data);
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
