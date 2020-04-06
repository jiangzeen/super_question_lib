package com.jxust.qq.superquestionlib.dao.mapper.admin;
import com.jxust.qq.superquestionlib.dto.LibTag;
import com.jxust.qq.superquestionlib.dto.admin.EsLibTag;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface EsLibTagMapper {

    String selectTagName(long id);

    EsLibTag selectTagByTagId(long tagId);

    int createLibTag(@Param("libTag") EsLibTag libTag);

    int updateLibTag(@Param("libTag") EsLibTag libTag);

    int deleteLibTag(long tagId);

    List<EsLibTag> selectTagsByParentTagId(long parentTagId);

    List<EsLibTag> selectParentTags();
}
