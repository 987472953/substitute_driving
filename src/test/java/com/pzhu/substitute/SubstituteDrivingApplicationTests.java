package com.pzhu.substitute;

import com.pzhu.substitute.common.CommonConstants;
import com.pzhu.substitute.mapper.OrderMapper;
import com.pzhu.substitute.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Set;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SubstituteDrivingApplicationTests {

    @Test
    void contextLoads() {
    }

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private OrderMapper orderMapper;

    @Test
    public void testPassEncoding() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        System.out.println(bCryptPasswordEncoder.encode("1"));
    }

    @Test
    public void redisTest() {
        Boolean qqq = redisTemplate.delete("qqq");
        System.out.println(qqq);
    }

    @Test
    public void JwtTest() {
        String dengyiqing = JwtUtil.createJWT("dengyiqing", 1000, 1L, "user");
        System.out.println(dengyiqing);
        Claims claims = JwtUtil.parseJWT("dengyiqing", dengyiqing);
        System.out.println(claims.getSubject());
    }

    @Test
    public void createOrder() {
        Set<String> range = redisTemplate.opsForZSet().range(CommonConstants.ORDER_LIST, 1515967609284124700L, 1515967609284124701L);
        for (String s : range) {
            System.out.println(s);
        }
    }


}
