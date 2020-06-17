package com.jxust.qq.superquestionlib.service.admin;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jxust.qq.superquestionlib.dao.mapper.admin.EsUserMapper;
import com.jxust.qq.superquestionlib.dao.mapper.admin.UtilMapper;
import com.jxust.qq.superquestionlib.dao.mapper.admin.repository.EsSchoolInfoRepository;
import com.jxust.qq.superquestionlib.dao.mapper.admin.repository.EsUserRepository;
import com.jxust.qq.superquestionlib.dto.admin.EsUser;
import com.jxust.qq.superquestionlib.util.admin.DateFormat;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.ExtendedBounds;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
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
    private final UtilMapper utilMapper;
    private final EsUserMapper userMapper;
    public static long pagesum;

    public EsUserService(UtilMapper utilMapper, EsUserMapper userMapper) {
        this.utilMapper = utilMapper;
        this.userMapper = userMapper;
    }
    //聚合按时间查找
    public JSONObject aggregationBuilder(int period)
    {
        Map<String,String> maps= DateFormat.getTime(period-1);
        DateHistogramAggregationBuilder histogramAggregationBuilder= AggregationBuilders.dateHistogram("day")
                .dateHistogramInterval(DateHistogramInterval.DAY).format("yyyy-MM-dd").field("userLastLoginTime")
                .minDocCount(0).timeZone(DateTimeZone.forTimeZone(TimeZone.getTimeZone("GMT+:08:00")))
                .extendedBounds(new ExtendedBounds(maps.get("startTime"),maps.get("endTime")));
        QueryBuilder bool=QueryBuilders.boolQuery().filter(QueryBuilders.rangeQuery("userLastLoginTime")
                .timeZone("UTC")
                .gte(new Date().getTime()-period*24*3600*1000-8*3600*1000)
                .lte(new Date().getTime()));
        SearchQuery searchQuery=new NativeSearchQueryBuilder().withQuery(bool).addAggregation(histogramAggregationBuilder)
                .build();
        pagesum= (int) User.search(searchQuery).getTotalElements();
        AggregatedPage<EsUser> search=(AggregatedPage<EsUser>)User.search(searchQuery);
        JSONObject data=new JSONObject();
        ArrayList<userCount> userCounts=new ArrayList<>();
        Histogram terms=(Histogram)search.getAggregations().getAsMap().get("day");
        for(Histogram.Bucket bucket : terms.getBuckets())
        {
               userCounts.add(new userCount(bucket.getDocCount(),bucket.getKeyAsString()));
        }
        data.put("userCounts",userCounts);
        data.put("pagesum",pagesum);
        return data;
    }
    //聚合按时间查找
    public JSONObject increaseAggregationBuilder(int period)
    {
        Map<String,String> maps= DateFormat.getTime(period-1);
        DateHistogramAggregationBuilder histogramAggregationBuilder= AggregationBuilders.dateHistogram("day")
                .dateHistogramInterval(DateHistogramInterval.DAY).field("userCreateTime").format("yyyy-MM-dd")
                .minDocCount(0).timeZone(DateTimeZone.forTimeZone(TimeZone.getTimeZone("GMT+:08:00")))
                .extendedBounds(new ExtendedBounds(maps.get("startTime"),maps.get("endTime")));
        QueryBuilder bool=QueryBuilders.boolQuery().filter(QueryBuilders.rangeQuery("userCreateTime")
                .timeZone("UTC")
                .gte(new Date().getTime()-period*24*3600*1000-8*3600*1000)
                .lte(new Date().getTime()));
        SearchQuery searchQuery=new NativeSearchQueryBuilder().withQuery(bool).addAggregation(histogramAggregationBuilder)
                .build();
        pagesum= (int) User.search(searchQuery).getTotalElements();
        AggregatedPage<EsUser> search=(AggregatedPage<EsUser>)User.search(searchQuery);
        JSONObject data=new JSONObject();
        ArrayList<userCount> userCounts=new ArrayList<>();
        Histogram terms=(Histogram)search.getAggregations().getAsMap().get("day");
        for(Histogram.Bucket bucket : terms.getBuckets())
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
        BoolQueryBuilder boolQueryBuilder=QueryBuilders.boolQuery();
        if(!queryString.isEmpty()) {
            MultiMatchQueryBuilder matchQuery = QueryBuilders.multiMatchQuery(queryString, "userName"
                    , "userNick");
            boolQueryBuilder.filter(matchQuery);
        }
        PageRequest pageRequest=PageRequest.of(pagenum-1,pagesize);
        Iterable<EsUser> users=User.search(boolQueryBuilder,pageRequest);
        pagesum=User.search(boolQueryBuilder,pageRequest).getTotalElements();
        List<EsUser> hotExamList=new ArrayList<>();
        for (EsUser user : users)
        {
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
            pagesum=1;
        }
        else pagesum=0;
        return user;
    }
    //true 降序 false 升序
    public ArrayList<EsUser> boolHotUser(String queryStrings, boolean dir, int pagenum, int pagesize, int schoolInfoId, int sex, Date createTimeBegin, Date createTimeEnd) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        //按字符查询
        if (!queryStrings.isEmpty()) {
            MultiMatchQueryBuilder matchQuery = QueryBuilders.multiMatchQuery(queryStrings, "userName", "userNick");
            boolQuery.must(matchQuery);
        }
        //过滤条件
        if(createTimeBegin!=null&&createTimeEnd!=null) {
            RangeQueryBuilder startTimeQuery = QueryBuilders.rangeQuery("userCreateTime")
                    .timeZone("UTC").gte(createTimeBegin.getTime())
                    .lte(createTimeEnd.getTime());
            boolQuery.filter(startTimeQuery);
        }
        if(sex==1||sex==0)
        {
        MatchQueryBuilder sexQuery = QueryBuilders.matchQuery("userSex", sex);
        boolQuery.filter(sexQuery);
       }
        //校信息查询
        if(schoolInfoId>=0) {
            FuzzyQueryBuilder schoolInfoQuery = QueryBuilders.fuzzyQuery("parentIds", schoolInfoId);
            boolQuery.filter(schoolInfoQuery);
        }
        //分页
        Sort sort;
        if(dir)
            sort=Sort.by(Sort.Direction.DESC,"userLastLoginTime");
        else
            sort=Sort.by(Sort.Direction.ASC,"userLastLoginTime");
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

    //删
    public int deleteById(long id)
    {
        if(User.existsById(id))
            User.deleteById(id);
        return userMapper.deleteById(id);
    }
    //修改
    public int updateById(EsUser user)
    {
        int status=userMapper.updateUser(user);
        if(status>0)
        {
            if(schoolInfo.findById(user.getUserSchoolId()).isPresent())
            {
                user.setUserSchoolInfo(schoolInfo.findById(user.getUserSchoolId()).get().getSchoolName());
            }
            user.setParentIds(utilMapper.getSchoolParent(user.getUserSchoolId()));
            User.save(user);
        }
        return status;
    }
    //增加
    public int createUser(EsUser user)
    {
        int status;
        status=userMapper.createUser(user);
        if(status>0)
        {
            if(schoolInfo.findById(user.getUserSchoolId()).isPresent())
            {
                user.setUserSchoolInfo(schoolInfo.findById(user.getUserSchoolId()).get().getSchoolName());
            }
            user.setParentIds(utilMapper.getSchoolParent(user.getUserSchoolId()));
            User.save(user);
        }
        return status;
    }
    public long count()
    {
        return User.count();
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