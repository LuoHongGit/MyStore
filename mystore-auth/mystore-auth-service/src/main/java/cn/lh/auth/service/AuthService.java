package cn.lh.auth.service;

import cn.lh.auth.client.UserClient;
import cn.lh.auth.config.JwtProperties;
import cn.lh.auth.pojo.UserInfo;
import cn.lh.auth.utils.JwtUtils;
import cn.lh.user.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

@Service
@EnableConfigurationProperties(JwtProperties.class)
public class AuthService {
    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private UserClient userClient;

    /**
     * 用户登录
     *
     * @param username
     * @param password
     * @return
     */
    public String accredit(String username, String password) {
        try {
            //调用用户微服务查询用户信息
            User user = userClient.queryUser(username, password);

            if (user == null) {
                return null;
            }

            //转换为UserInfo
            UserInfo userInfo = new UserInfo();
            userInfo.setId(user.getId());
            userInfo.setUsername(username);

            String token = null;

            //生成token

            token = JwtUtils.generateToken(userInfo, jwtProperties.getPrivateKey(), jwtProperties.getExpire());

            return token;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
