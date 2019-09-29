package com.jxust.qq.superquestionlib;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@MapperScan("com.jxust.qq.superquestionlib.dao.mapper")
public class SuperQuestionLibApplication {

    public static void main(String[] args) {
        SpringApplication.run(SuperQuestionLibApplication.class, args);
    }

}
