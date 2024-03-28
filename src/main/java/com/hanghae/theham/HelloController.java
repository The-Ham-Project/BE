package com.hanghae.theham;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
public class HelloController {

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        log.error("한국어 테스트");
        return ResponseEntity.ok().body("pong");
    }
}
