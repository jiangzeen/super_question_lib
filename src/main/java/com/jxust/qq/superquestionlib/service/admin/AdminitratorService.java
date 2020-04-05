package com.jxust.qq.superquestionlib.service.admin;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.jxust.qq.superquestionlib.dao.mapper.admin.AdminitratorMapper;
import com.jxust.qq.superquestionlib.dto.admin.Adminitrator;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class AdminitratorService
{
    private final AdminitratorMapper mapper;

    public AdminitratorService(AdminitratorMapper mapper) {
        this.mapper = mapper;
    }
    //获取Token
    public String getToken(Adminitrator adminitrator,int minutes)
    {
        Date start=new Date();
        long currentTime=System.currentTimeMillis()+60*60*1000*minutes;   //一小时有效时间
        Date end=new Date(currentTime);
        String token="";
        token= JWT.create().withAudience(String.valueOf(adminitrator.getAdminId())).withIssuedAt(start).withExpiresAt(end)
        .sign(Algorithm.HMAC256(adminitrator.getAdminPassword()));
        return token;
    }

    //获取全部管理员
    public List<Adminitrator> findAdminitrators() {return mapper.findAdminitrators();}
    //创建管理员账号
    public int creatAdminitrator(String username, String password)
    {
        assert username != null;
        assert password != null;
        Adminitrator adminitrator=new Adminitrator();
        adminitrator.setAdminName(username);
        adminitrator.setAdminPassword(encrypt(username,password));
        adminitrator.setLastLoginTime(new Date());
        return mapper.insertAdmin(adminitrator);
    }
    //查找管理员根据名字
    public Adminitrator findAdminitrator(String adminName)
    {
        assert adminName != null;
        return mapper.selectAdminByAdminName(adminName);
    }
    //查找管理员根据Id
    public Adminitrator findAdminitratorById(int id)
    {
        System.out.println("thankyou for everyone");
        return mapper.findAdminitratorById(id);
    }
    //根据用户名查密码
    public String findAdminPassword(String adminName) {
        assert adminName != null;
        return findAdminitrator(adminName).getAdminPassword();
    }
    //修改密码
    public void modifyPassword(String adminName, String newPassword) {
        mapper.updatePassword(adminName, encrypt(adminName, newPassword));
    }
    //修改登陆时间
    public void modifyLoginTime(String adminName) {
        System.out.println(adminName);
        LocalDateTime time = LocalDateTime.now();
        mapper.updateLoginTime(adminName,time);
    }
    //删除用户
    public int deleteByAdminName(String adminName)
    {
        return mapper.deleteByAdminName(adminName);
    }
    //删除用户
    public int deleteById(int id)
    {
        return mapper.deleteById(id);
    }
    //密码转MDD5加密
    public String encrypt(String adminName, String password) {
        Object salt = ByteSource.Util.bytes(adminName);
        SimpleHash simpleHash = new SimpleHash("MD5", password, salt,3);
        return simpleHash.toString();
    }
}
