package cn.lh.user.service;

import cn.lh.common.utils.NumberUtils;
import cn.lh.user.mapper.UserMapper;
import cn.lh.user.pojo.User;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * 用户业务层
 */
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private AmqpTemplate amqpTemplate;

    private static final String KEY_PREFIX = "user:verify:";

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

    /**
     * 发送验证码
     * @return
     */
    public Boolean sendVerifyCode(String phone) {
        //判空
        if(StringUtils.isBlank(phone)){
            return null;
        }

        //生成6位随机验证码
        String code = NumberUtils.generateCode(6);

        try {
            //创建Map集合
            HashMap<String, String> msg = new HashMap<>();
            msg.put("phone",phone);
            msg.put("code",code);

            //发送信息到消息队列
            amqpTemplate.convertAndSend("mystore.sms.exchange","sms.verify.code",msg);

            //将验证码存储到Redis
            redisTemplate.opsForValue().set(KEY_PREFIX + phone, code, 5, TimeUnit.MINUTES);

            return true;
        } catch (AmqpException e) {
            e.printStackTrace();
            return false;
        }
    }


}
