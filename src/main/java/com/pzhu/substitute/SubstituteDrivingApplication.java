package com.pzhu.substitute;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.pzhu.substitute.mapper")
public class SubstituteDrivingApplication {

    public static void main(String[] args) {
        SpringApplication.run(SubstituteDrivingApplication.class, args);
    }

}
