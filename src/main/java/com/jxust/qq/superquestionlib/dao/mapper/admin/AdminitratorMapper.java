package com.jxust.qq.superquestionlib.dao.mapper.admin;
import com.jxust.qq.superquestionlib.dto.admin.Adminitrator;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AdminitratorMapper
{
    List<Adminitrator> findAdminitrators();

    int insertAdmin(@Param("adminitrator") Adminitrator adminitrator);

    Adminitrator selectAdminByAdminName(@Param("adminName") String adminName);

    Adminitrator selectAdminByNameAndPassword(String username, String password);

    String selectPasswordByAdmin(String username);

    int updatePassword(@Param("adminName") String adminName, @Param("password") String password);

    void updateAdmin(Adminitrator adminitrator);

    void updateLoginTime(@Param("adminName") String adminName, @Param("time") LocalDateTime time);

    int deleteByAdminName(@Param("adminName") String adminName);
}
