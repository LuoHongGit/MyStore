package cn.lh.auth.test;

import cn.lh.auth.pojo.UserInfo;
import cn.lh.auth.utils.JwtUtils;
import cn.lh.auth.utils.RsaUtils;
import org.junit.Before;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;

public class JwtTest {

    private static final String pubKeyPath = "E:\\MyTest\\rsa\\rsa.pub";

    private static final String priKeyPath = "E:\\MyTest\\rsa\\rsa.pri";

    private PublicKey publicKey;

    private PrivateKey privateKey;

    @Test
    public void testRsa() throws Exception {
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "234");
    }

    @Before
    public void testGetRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    @Test
    public void testGenerateToken() throws Exception {
        // 生成token
        String token = JwtUtils.generateToken(new UserInfo(20L, "jack"), privateKey, 5);
        System.out.println("token = " + token);
    }

    @Test
    public void testParseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MjAsInVzZXJuYW1lIjoiamFjayIsImV4cCI6MTU3NDY4NDk2M30.WNvnXCeMGrJZInyivFLnr9kUa9TAPpx7qW3AKGylDwVx52fSUAxMdZ9NcVdUf2TD5MF1OYkEdBzrgQoepwioLKeeX51GtZ9QF9GaLYhcfzwNbOeOFtO8LHUcFnC-cInly8uc1kXCYA1PPpsuzrKw4uYMEstYRZ0vvMsOSZ7U8Pc";

        // 解析token
        UserInfo user = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + user.getId());
        System.out.println("userName: " + user.getUsername());
    }
}