package com.pzhu.substitute;

import com.pzhu.substitute.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class SecurityApplicationTests {

    @Test
    void contextLoads() {
    }

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Test
    public void testPassEncoding(){
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        System.out.println(bCryptPasswordEncoder.encode("1"));
    }
    @Test
    public void redisTest(){
        Boolean qqq = redisTemplate.delete("qqq");
        System.out.println(qqq);
    }

    @Test
    public void JwtTest(){
        String dengyiqing = JwtUtil.createJWT("11", 123333,"dengyiqing");
        System.out.println(dengyiqing);
        Claims claims = JwtUtil.parseJWT("11", dengyiqing);
        System.out.println(claims.getSubject());
    }

}
