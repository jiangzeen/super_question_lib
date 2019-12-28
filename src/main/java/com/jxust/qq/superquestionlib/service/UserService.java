package com.jxust.qq.superquestionlib.service;

import com.jxust.qq.superquestionlib.dao.mapper.UserMapper;
import com.jxust.qq.superquestionlib.po.User;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

@Service
@Slf4j
public class UserService {

    private final UserMapper mapper;
    private final static String avatar_dir_path = "/home/jiangzeen/project_log/";

    public UserService(UserMapper mapper) {
        this.mapper = mapper;
    }

    public List<User> findUsers() {
        return mapper.findAllUsers();
    }

    public int createUser(String username, String password, String nickname) {
        assert username != null;
        assert password != null;
        assert nickname != null;
        User user = new User();
        user.setUserName(username);
        user.setUserNick(nickname);
        user.setUserPassword(encrypt(username, password));
        user.setUserCreateTime(LocalDateTime.now());
        user.setUserLastLoginTime(LocalDateTime.now());
        user.setUserAvatar("");
        user.setUserSex(0);
        user.setUserSchoolId(0);
        return mapper.insertUser(user);
    }

    public User findUser(String username) {
        assert username != null;
        return mapper.selectUserByUserName(username);
    }

    public String findUserPassword(String username) {
        assert username != null;
        return findUser(username).getUserPassword();
    }

    public void modifyPassword(String username, String newPassword) {
        mapper.updatePassword(username, encrypt(username, newPassword));
    }

    public String encrypt(String username, String password) {
        Object salt = ByteSource.Util.bytes(username);
        SimpleHash simpleHash = new SimpleHash("MD5", password, salt,3);
        return simpleHash.toString();
    }

    public void modifyLoginTime(String username) {
        LocalDateTime time = LocalDateTime.now();
        mapper.updateLoginTime(username, time);
    }

    public void completeInfo(String username, String avatarUrl, String majorId) throws IllegalArgumentException {
        if (findUser(username) == null) {
            throw new IllegalArgumentException();
        }
        mapper.updateAvatarAndMajor(avatarUrl, Integer.valueOf(majorId), username);
    }

    public String processAvatar(String username, MultipartFile avatar) {
        String originName = avatar.getOriginalFilename();
        if (originName == null) {
            log.info("上传图片为空!");
            return null;
        }
        log.info("获得上传文件{}", originName);
        int random = new Random().nextInt(1000000);
        String filename = username + "_" + random + ".";
        Pattern imgReg = Pattern.compile(".*(.jpg)$|.*(.png)$|.*(.jpeg)$");
        if (!imgReg.matcher(originName).find()) {
            log.info("上传图片文件不符合特定格式[jpg,png,jpeg]");
            return null;
        }
        String[] suffix = originName.split("\\.");
        filename += suffix[suffix.length - 1];
        try {
            InputStream inStream = avatar.getInputStream();
            FileOutputStream outStream = new FileOutputStream(avatar_dir_path + filename);
            byte[] bb = new byte[1024];
            while (inStream.read(bb) != -1) {
                outStream.write(bb);
            }

        } catch (IOException e) {
            log.info("保存图片文件失败 cause by:" + e);
            return null;
        }
        return filename;
    }
}
