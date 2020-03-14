package com.jxust.qq.superquestionlib.service.admin;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.jxust.qq.superquestionlib.dao.mapper.admin.SchoolInfoMapper;
import com.jxust.qq.superquestionlib.dao.mapper.admin.repository.EsSchoolInfoRepository;
import com.jxust.qq.superquestionlib.dao.mapper.admin.repository.EsUserRepository;
import com.jxust.qq.superquestionlib.dto.SchoolInfo;
import com.jxust.qq.superquestionlib.dto.admin.EsUser;
import com.jxust.qq.superquestionlib.util.admin.DateFormat;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.*;
import org.elasticsearch.index.similarity.ScriptedSimilarity;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.avg.AvgAggregationBuilder;
import org.elasticsearch.search.aggregations.pipeline.bucketmetrics.max.MaxBucketPipelineAggregationBuilder;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.context.request.WebRequest;

import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
public class EsUserService
{
    @Autowired
    EsUserRepository User;
    @Autowired
    EsSchoolInfoRepository schoolInfo;
    private final SchoolInfoMapper schoolInfoMapper;
    public static long pagesum;

    public EsUserService(SchoolInfoMapper schoolInfoMapper) {
        this.schoolInfoMapper = schoolInfoMapper;
    }
    private String getName(int init_id)
    {
         String s1="";
         int id=init_id;
         while(id!=0)
         {
             SchoolInfo schoolInfo=schoolInfoMapper.selectSchoolInfoById(id);
             id=schoolInfo.getSchoolParentId();
             s1=schoolInfo.getSchoolName().concat(s1);
         }
         return s1;
    }

    //聚合按时间查找
    public JSONObject aggregationBuilder()
    {
        Map<String,Date> maps= DateFormat.getTime("week");
        DateHistogramAggregationBuilder histogramAggregationBuilder= AggregationBuilders.dateHistogram("day")
                .dateHistogramInterval(DateHistogramInterval.DAY).format("yyyy-MM-dd").field("userLastLoginTime")
                .minDocCount(0).timeZone(DateTimeZone.forTimeZone(TimeZone.getTimeZone("GMT+:08:00")));
        QueryBuilder bool=QueryBuilders.boolQuery().filter(QueryBuilders.rangeQuery("userLastLoginTime")
                .gte(maps.get("startTime").getTime()).lte(maps.get("endTime").getTime()));
        SearchQuery searchQuery=new NativeSearchQueryBuilder().withQuery(bool).addAggregation(histogramAggregationBuilder)
                .build();
        pagesum= (int) User.search(searchQuery).getTotalElements();
        AggregatedPage<EsUser> search=(AggregatedPage)User.search(searchQuery);
        Terms terms=(Terms) search.getAggregations().get("day");
        JSONObject data=new JSONObject();
        ArrayList<userCount> userCounts=new ArrayList<>();
        for (Terms.Bucket bucket : terms.getBuckets())
        {
               userCounts.add(new userCount(bucket.getDocCount(),bucket.getKeyAsString()));
        }
        data.put("userCounts",userCounts);
        data.put("pagesum",pagesum);
        return data;
    }
    //进行模糊匹配查找
    public List<EsUser> matchUser(String queryString, int pagenum, int pagesize)
    {
        MultiMatchQueryBuilder matchQuery= QueryBuilders.multiMatchQuery(queryString,"userName"
                ,"userNick");
        PageRequest pageRequest=PageRequest.of(pagenum-1,pagesize);
        Iterable<EsUser> users=User.search(matchQuery,pageRequest);
        pagesum=User.search(matchQuery,pageRequest).getTotalElements();
        List<EsUser> hotExamList=new ArrayList<>();
        for (EsUser user : users)
        {
            user.setUserSchoolInfo(getName(user.getUserSchoolId()));
            hotExamList.add(user);
        }
        return hotExamList;
    }
    //根据id完全匹配查找
    public EsUser termUser(long id)
    {
        EsUser user=null;
        if(User.findById(id).isPresent())
        {
            user=User.findById(id).get();
            user.setUserSchoolInfo(getName(user.getUserSchoolId()));
            pagesum=1;
        }
        else pagesum=0;
        return user;
    }
    //true 降序 false 升序
    public ArrayList<EsUser> boolHotUser(String queryStrings, boolean dir, int pagenum, int pagesize, int schoolInfoId, int sex, Date createTimeBegin, Date createTimeEnd)
    {
        BoolQueryBuilder boolQuery=QueryBuilders.boolQuery();
        //按字符查询

        MultiMatchQueryBuilder matchQuery=QueryBuilders.multiMatchQuery(queryStrings,"userName","userNick");
        boolQuery.must(matchQuery);
        //过滤条件
        RangeQueryBuilder startTimeQuery=QueryBuilders.rangeQuery("userCreateTime").from(createTimeBegin.getTime()-3600*8*1000).to(createTimeEnd.getTime()-3600*8*1000);
        RangeQueryBuilder sexQuery=QueryBuilders.rangeQuery("sex").lte(sex).gte(sex);

        boolQuery.filter(startTimeQuery);
        boolQuery.filter(sexQuery);
        //校信息查询
        FuzzyQueryBuilder schoolInfoQuery = QueryBuilders.fuzzyQuery("parentIds", String.valueOf(schoolInfoId));
        boolQuery.filter(schoolInfoQuery);
        //分页
        Sort sort;
        if(dir)
            sort=Sort.by(Sort.Direction.DESC,"userCreatTime");
        else
            sort=Sort.by(Sort.Direction.ASC,"userCreatTime");
        PageRequest pageRequest=PageRequest.of(pagenum-1,pagesize,sort);
        pagesum=User.search(boolQuery,pageRequest).getTotalElements();
        Iterable<EsUser> iterables=User.search(boolQuery,pageRequest);
        Iterator<EsUser> iterator=iterables.iterator();
        ArrayList<EsUser> users=new ArrayList<>();
        while(iterator.hasNext())
        {
            EsUser user=iterator.next();
            users.add(user);
        }
        return users;
    }
    @InitBinder
    public void initBinder(WebDataBinder binder, WebRequest request) {
        //转换日期 注意这里的转化要和传进来的字符串的格式一直 如2015-9-9 就应该为yyyy-MM-dd
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));// CustomDateEditor为自定义日期编辑器
    }
}
@Data
class userCount
{
    long count;
    String date;

    public userCount(long count, String date) {
        this.count = count;
        this.date = date;
    }
}