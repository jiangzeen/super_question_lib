package com.jxust.qq.superquestionlib.controller.admin;

import com.alibaba.fastjson.JSONObject;
import com.jxust.qq.superquestionlib.dto.Result;
import com.jxust.qq.superquestionlib.dto.admin.EsSchoolInfo;
import com.jxust.qq.superquestionlib.service.admin.EsSchoolInfoService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@Slf4j
public class EsSchoolInfoController
{
    private final EsSchoolInfoService schoolInfoService;

    public EsSchoolInfoController(EsSchoolInfoService schoolInfoService) {
        this.schoolInfoService = schoolInfoService;
    }
    @PostMapping("admin/schoolInfo/fuzzyQuery")
    public Result fuzzyQuery(@Param("queryString") String queryString,@Param("schoolParentId")int schoolParentId)
    {
        Iterable<EsSchoolInfo> esSchoolInfos=schoolInfoService.boolQuery(queryString,schoolParentId);
        ArrayList<info> infos=new ArrayList<>();
        infos.add(new info(1000,"其他"));
        for (EsSchoolInfo esSchoolInfo : esSchoolInfos)
        {
            System.out.println(new info(esSchoolInfo.getSchoolId(),esSchoolInfo.getSchoolName()));
            infos.add(new info(esSchoolInfo.getSchoolId(),esSchoolInfo.getSchoolName()));
        }
        JSONObject data=new JSONObject();
        data.put("esSchoolInfos",infos);
        data.put("pagesum",schoolInfoService.pagesum);
        return Result.SUCCESS(data);
    }
}
@Data
class info
{
    int id;
    String schoolName;
    public info(int id, String schoolName) {
        this.id = id;
        this.schoolName = schoolName;
    }
}
