package com.jxust.qq.superquestionlib.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jxust.qq.superquestionlib.dao.mapper.LibTagMapper;
import com.jxust.qq.superquestionlib.po.LibTag;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LibTagService {
    private final LibTagMapper libTagMapper;

    public LibTagService(LibTagMapper libTagMapper) {
        this.libTagMapper = libTagMapper;
    }

    public List<LibTag> getAllParentTag() {
        return libTagMapper.selectParentTags();
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
