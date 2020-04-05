package com.jxust.qq.superquestionlib.dao.mapper.admin;
import org.springframework.stereotype.Repository;


@Repository
public interface EsLibTagMapper {

    String selectTagName(int id);

}
