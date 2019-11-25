package cn.lh.auth.controller;

import cn.lh.auth.config.JwtProperties;
import cn.lh.auth.pojo.UserInfo;
import cn.lh.auth.service.AuthService;
import cn.lh.auth.utils.JwtUtils;
import cn.lh.common.utils.CookieUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@EnableConfigurationProperties(JwtProperties.class)
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 用户登录
     * @param username
     * @param password
     * @return
     */
    @PostMapping("/accredit")
    public ResponseEntity<Void> accredit(
            @RequestParam("username")String username,
            @RequestParam("password")String password,
            HttpServletRequest request,
            HttpServletResponse response){
        //通过用户名和密码获取token
        String token = authService.accredit(username,password);

        //判空
        if(StringUtils.isBlank(token)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        //将token存入cookie写回浏览器
        CookieUtils.setCookie(request,response, jwtProperties.getCookieName(),token,jwtProperties.getExpire() * 60);

        return ResponseEntity.ok().build();
    }

    /**
     * 获取用户信息并更新过期时间
     */
    @GetMapping("/verify")
    public ResponseEntity<UserInfo> verify(
            @CookieValue("MYSTORE_TOKEN")String token,
            HttpServletRequest request,
            HttpServletResponse response){
        //判空
        if(StringUtils.isBlank(token)){
            return ResponseEntity.notFound().build();
        }

        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());

            if(userInfo == null){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            //重新生成Token
            token = JwtUtils.generateToken(userInfo, jwtProperties.getPrivateKey(), jwtProperties.getExpire());

            //将token存入cookie写回浏览器
            CookieUtils.setCookie(request,response, jwtProperties.getCookieName(),token,jwtProperties.getExpire() * 60);

            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
