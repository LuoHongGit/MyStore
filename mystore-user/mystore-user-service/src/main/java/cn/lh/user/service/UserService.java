package cn.lh.user.service;

import cn.lh.user.mapper.UserMapper;
import cn.lh.user.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户业务层
 */
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    /**
     * 校验用户参数是否有效
     * @param data
     * @param type
     * @return
     */
    public Boolean checkUserData(String data, Integer type) {
        User user = new User();
        if (type == 1){
            user.setUsername(data);
        } else if (type == 2){
            user.setPhone(data);
        }else{
            return null;
        }
        return userMapper.selectCount(user) == 0;
    }
}
