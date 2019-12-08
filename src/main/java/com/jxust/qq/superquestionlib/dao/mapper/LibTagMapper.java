package com.jxust.qq.superquestionlib.dao.mapper;

import com.jxust.qq.superquestionlib.po.LibTag;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LibTagMapper {

    LibTag selectTagByTagId(int TagId);

    List<LibTag> selectTagsByParentTagId(long parentTagId);

    List<LibTag> selectParentTags();
}
