package com.felix.mymusicserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})  // 暂时禁用默认的安全配置
public class MyMusicServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyMusicServerApplication.class, args);
    }
}