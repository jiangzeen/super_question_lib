package com.jxust.qq.superquestionlib.service.realm;

import com.jxust.qq.superquestionlib.dao.mapper.UserMapper;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

public class CustomRealm extends AuthorizingRealm {

    private UserMapper userMapper;

    @Autowired
    private void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }


    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {

        return null;
    }

    /**
     * 获取用户认证信息
     * @param authenticationToken 包含用户信息,需要进行验证
     * @return AuthorizationInfo 包含用户认证信息以及所拥有的权限
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        String username = token.getUsername();
        String password = userMapper.selectPasswordByUsername(username);
        if (password == null) {
            throw new IncorrectCredentialsException("用户名或者密码错误!");
        }
        // 传入四个值, 分别是:username,从数据库中取出来的密文,加密所用的salt,以及realm的name
        return new SimpleAuthenticationInfo(username, password, ByteSource.Util.bytes(username),
                getName());
    }
}
