package com.jxust.qq.superquestionlib.util;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import lombok.Data;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SerializationTest {

    String html = "<div style=\"padding: 20px 0;min-width: 500px;text-align: center\">\n" +
            "  <div style=\"height: 40px;line-height: 40px;vertical-align: middle\">\n" +
            "    <img src=\"{{logo的url}}\" width=\"35px\" height=\"35px\" style=\"margin-right: 10px;border-radius: 50%;vertical-align: middle\"/>\n" +
            "    <a href=\"{{网站的url}}\" target=\"_blank\" style=\"vertical-align: middle;text-decoration: none\">super-question-lib</a>\n" +
            "  </div>\n" +
            "  <div>\n" +
            "    <h4>――·❀―时光邮箱―❀·――</h4>\n" +
            "  </div>\n" +
            "  <div style=\"width: 60%;margin: 0 auto;line-height: 18px;font-size: 12px;color: orange;text-align: left\">\n" +
            "    <p>亲爱的用户 ，<br>\n" +
            "      您在super-question-lib上设置的定时任务已到达，小邮我登上时光旅行的小船，不惧风雨，穿梭于时空的缝隙，只为不负所托，将这封邮件准时送达到您的手中。<br>\n" +
            "      韶华易逝,劝君珍惜,努力完成 (&gt;ω&lt;)。</p>\n" +
            "  </div>\n" +
            "  <div style=\"width: 60%;margin: 20px auto;padding: 1px 10px 5px;outline: 2px solid #B3C0D1\">\n" +
            "    <h3>任务备注</h3>\n" +
            "    <hr>\n" +
            "    <p style=\"text-align: left;text-indent:2em;font-size: 15px;color: black\">\n" +
            "\t\tmessage\n" +
            "    </p>\n" +
            "    <p>请登录<a href=\"/\" style=\"color:#00a2ca\">super-question-lib</a>查看并完成</p>\t\n" +
            "    <p style=\"text-align: right;\">super-question-lib网站</p>\n" +
            "  </div>\n" +
            "</div>";
    @Test
    public void serialization() {
        User user = new User();
        user.setId(10);
        user.setSex("male");
        user.setUsername("123456");
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter();
        filter.getExcludes().add("sex");
        System.out.println(JSONObject.toJSONString(user, filter));
    }


    @Data
    private static class User{
        int id;
        String username;
        String sex;
    }

    @Test
    public void testTime() {
        LocalDateTime a = LocalDateTime.parse("2020-10-19 15:22:10",DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss") );
        System.out.println(a.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    @Test
    public void test_html() {
        System.out.println(html.replace("message", "new message"));
    }
}
