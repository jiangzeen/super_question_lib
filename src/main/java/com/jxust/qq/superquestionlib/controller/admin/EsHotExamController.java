package com.jxust.qq.superquestionlib.controller.admin;

import com.alibaba.fastjson.JSONObject;
import com.jxust.qq.superquestionlib.dto.Result;
import com.jxust.qq.superquestionlib.dto.admin.EsHotExam;
import com.jxust.qq.superquestionlib.service.admin.EsHotExamService;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.PostMapping;

public class EsHotExamController {
    private final EsHotExamService esHotExamService;

    public EsHotExamController(EsHotExamService esHotExamService) {
        this.esHotExamService = esHotExamService;
    }


    @PostMapping("/admin/hotExam/findById")
    public Result findById(@Param("id")int id)
    {
        EsHotExam esHotExam=esHotExamService.termHotExam(id);
        JSONObject data=new JSONObject();
        data.put("eshotExam",esHotExam);
        if(esHotExam!=null)
        {
            return Result.SUCCESS(data);
        }
        else
            return Result.FAILD(data);
    }

}
