package com.pzhu.substitute;

import com.pzhu.substitute.config.WebSocketServer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@MapperScan("com.pzhu.substitute.mapper")
public class SubstituteDrivingApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(SubstituteDrivingApplication.class, args);
        WebSocketServer.setApplicationContext(applicationContext);
    }

}
