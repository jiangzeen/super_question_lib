package com.jxust.qq.superquestionlib.service.admin;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jxust.qq.superquestionlib.dao.mapper.LibTagMapper;
import com.jxust.qq.superquestionlib.dao.mapper.admin.repository.EsLibTagRepository;
import com.jxust.qq.superquestionlib.dto.LibTag;
import com.jxust.qq.superquestionlib.dto.admin.EsLibTag;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FuzzyQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class EsLibTagService
{
    public static int pagesum;
    @Autowired
    EsLibTagRepository eslibTagRepository;
    @Autowired
    LibTagMapper libTagMapper;
    public List<LibTag> getAllParentTag() {
        return libTagMapper.selectParentTags();
    }
    //进行模糊匹配
    public Iterable<EsLibTag> boolQuery(String queryString,int parentTagId) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        //级别
        TermQueryBuilder parentQuery = QueryBuilders.termQuery("parentTagId", parentTagId);
        boolQuery.filter(parentQuery);
        if(!queryString.isEmpty()) {
            FuzzyQueryBuilder fuzzyQueryBuilder = QueryBuilders.fuzzyQuery("tagName", queryString);
            boolQuery.must(fuzzyQueryBuilder);
        }
        PageRequest pageRequest = PageRequest.of(0, 20);
        Iterable<EsLibTag> libTags =eslibTagRepository.search(boolQuery, pageRequest);
        pagesum= (int) eslibTagRepository.search(boolQuery, pageRequest).getTotalElements()+1;
        return libTags;
    }
    public JSONObject getAllTagsWithChildrenTag() {
        List<LibTag> parentTags = getAllParentTag();
        JSONObject data = new JSONObject();
        JSONArray parentArray = new JSONArray();
        parentTags.forEach(parentTag -> {
            List<LibTag> childTags = libTagMapper.selectTagsByParentTagId(parentTag.getTagId());
            JSONObject parent = new JSONObject();
            JSONArray childArray = new JSONArray();
            parent.put("tagId", parentTag.getTagId());
            parent.put("tagName", parentTag.getTagName());
            childTags.forEach(childTag -> {
                JSONObject child = new JSONObject();
                child.put("tagId", childTag.getTagId());
                child.put("tagName", childTag.getTagName());
                childArray.add(child);
            });
            parent.put("childTag", childArray);
            parentArray.add(parent);
        });
        data.put("tags", parentArray);
        return data;
    }
}
@Data
class childTag
{
    int tagId;
    String tagName;
}

