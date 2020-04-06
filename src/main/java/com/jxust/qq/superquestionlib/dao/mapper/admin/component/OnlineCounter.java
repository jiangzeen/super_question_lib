package com.jxust.qq.superquestionlib.dao.mapper.admin.component;
import com.auth0.jwt.JWT;
import org.springframework.stereotype.Component;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 心跳模式 统计60秒内活跃的管理员在线人数
 */
@Component
public class OnlineCounter
{
    private static Map countMap=new ConcurrentHashMap<String,Object>();
    public void insertToken(String token){
        //获得当前时间(毫秒)
        long currentTime = System.currentTimeMillis();
        //解析token，获得ID
        String code= JWT.decode(token).getAudience().get(0);
        System.out.println(code);
        //以签发时间为key。当前时间+60s为value存入countMap中
        countMap.put(code,currentTime+1*60*1000);
    }

    /**
     * 获取用户数量
     * @return
     */
    public Integer getOnlineCount(){
        int onlineCount = 0;
        //获取countMap的迭代器
        Iterator iterator = countMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String,Object>  entry = (Map.Entry<String, Object>) iterator.next();
            Long value = (Long) entry.getValue();
            if (value > System.currentTimeMillis()) {
                //过期时间大于当前时间则没有过期
                onlineCount++;

            }
            else countMap.remove(entry.getKey());
        }
        return onlineCount;
    }
}
