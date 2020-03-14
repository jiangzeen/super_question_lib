package com.jxust.qq.superquestionlib.redis;

import lombok.Data;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


@SpringBootTest
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.yml")
public class RedisTransaction {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    @Test
    public void getValue() {
        System.out.println(stringRedisTemplate.opsForValue().get("jiangzeen"));
    }

    @Test
    public void stringStore() {
        stringRedisTemplate.opsForValue().set("srt", "中文字符");
    }


    @Test
    public void storeUser() {
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("name", "Frank");
        map1.put("money", "47");
        HashMap<String, String> map2 = new HashMap<>();
        map2.put("name", "Bill");
        map2.put("money", "125");
        redisTemplate.opsForHash().putAll("user:1", map1);
        redisTemplate.opsForHash().putAll("user:2", map2);
    }


    @Test
    public void storeBag() {
        redisTemplate.opsForSet().add("bag:1", "ItemL", "ItemM", "ItemN");
        redisTemplate.opsForSet().add("bag:2", "ItemO", "ItemP", "ItemQ");
    }

    @Test
    public void storeMarket() {
        redisTemplate.opsForZSet().add("market", "ItemA.4", 35);
        redisTemplate.opsForZSet().add("market", "ItemC.7", 48);
        redisTemplate.opsForZSet().add("market", "ItemE.2", 60);
        redisTemplate.opsForZSet().add("market", "ItemG.3", 73);
    }

    public void listItem(String itemId, int sellerId, double price) {
        String bagKey = "bag:" + sellerId;
        String item = String.format("%s.%s", itemId, sellerId);
        List<Object> res = redisTemplate.executePipelined(new SessionCallback<Object>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                redisTemplate.watch(bagKey);
//                if (!redisTemplate.opsForSet().isMember(bagKey, itemId)) {
//                    redisTemplate.unwatch();
//                    return null;
//                }
                redisTemplate.multi();
                redisTemplate.opsForZSet().add("market", item, price);
                redisTemplate.opsForSet().remove(bagKey, itemId);
                redisTemplate.exec();
                return null;
            }
        });
        System.out.println(res);
    }


    public void purchaseItem(int buyerId, String itemId, int sellerId) {
        // 测试出售商品
        String seller = "user:" + sellerId;
        String buyer = "user:" + buyerId;
        String item = String.format("%s.%s", itemId, sellerId);
        double sellPrice = redisTemplate.opsForZSet().score("market", item);
        double buyerMoney = Double.parseDouble(Objects.requireNonNull(redisTemplate.opsForHash().get(buyer, "money")).toString());
        double sellerMoney = Double.parseDouble(Objects.requireNonNull(redisTemplate.opsForHash().get(seller, "money")).toString());
        List<Object> res = redisTemplate.executePipelined(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                redisTemplate.watch("market");
                redisTemplate.watch(buyer);
                if (sellPrice > buyerMoney) {
                    System.out.println("买家资产不足,交易取消");
                    redisTemplate.unwatch();
                    return null;
                }
                redisTemplate.multi();
                redisTemplate.opsForHash().put(seller, "money", Double.toString(buyerMoney + sellerMoney));
                redisTemplate.opsForHash().put(buyer, "money", Double.toString(sellerMoney - sellPrice));
                redisTemplate.opsForSet().add("bag:" + buyerId, itemId);
                redisTemplate.opsForZSet().remove("market", item);
                redisTemplate.exec();
                System.out.println("交易成功");
                return null;
            }
        });
        System.out.println(res);
    }



    @Test
    public void testAdd() {
        listItem("ItemL", 1, 50);
    }

    @Test
    public void testSell() {
        String s = redisTemplate.opsForValue().get("mailSender:task:model");
        System.out.println(s);
    }

    @Data
    class User implements Serializable {
        private String name;
        private double money;

        public User(String name, double money) {
            this.name = name;
            this.money = money;
        }
    }
}
