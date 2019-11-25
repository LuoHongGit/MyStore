package cn.lh.user.controller;

import cn.lh.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 用户控制层
 */
@Controller
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 校验用户参数是否有效
     * @param data
     * @param type
     * @return
     */
    @GetMapping("/check/{data}/{type}")
    public ResponseEntity<Boolean> checkUserData(@PathVariable("data")String data,@PathVariable("type")Integer type){
        Boolean result = userService.checkUserData(data,type);

        if(result == null){
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(result);
    }

    /**
     * 发送验证码
     * @return
     */
    @PostMapping("/code")
    public ResponseEntity<Void> sendVerifyCode(@RequestParam("phone")String phone){
        Boolean result = userService.sendVerifyCode(phone);

        if(result == null || !result){
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
