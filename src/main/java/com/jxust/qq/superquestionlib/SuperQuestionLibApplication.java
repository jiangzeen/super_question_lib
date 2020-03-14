package com.jxust.qq.superquestionlib;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@MapperScan("com.jxust.qq.superquestionlib.dao.mapper")
public class SuperQuestionLibApplication {

    public static void main(String[] args) {
        //保证ES配置与启动配置相一致,well done,非常好用
        System.setProperty("es.set.netty.runtime.available.processors", "false");
        SpringApplication.run(SuperQuestionLibApplication.class, args);
    }

}
