// 현재처럼 com.cardgame.cardserver 패키지에 두되:
package com.cardgame.cardserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Map;

@SpringBootApplication(scanBasePackages = "com.cardgame") // ★추가
public class CardgameApplication {
    public static void main(String[] args) {
        SpringApplication.run(CardgameApplication.class, args);

    }
    public Map<String,Object> ping(){ return Map.of("ok", true); }
}


