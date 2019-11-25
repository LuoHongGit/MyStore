package cn.lh.user.api;

import cn.lh.user.pojo.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 用户微服务远程调用接口
 */
public interface UserApi {
    @GetMapping("/query")
    public User queryUser(@RequestParam("username")String username, @RequestParam("password")String password);
}
