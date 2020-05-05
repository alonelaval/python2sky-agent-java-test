package com.project.b;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@SpringBootApplication
public class BApplication {

    public static void main(String[] args) {
        SpringApplication.run(BApplication.class, args);
    }


    @Controller
    @RequestMapping("project")
    class JobController{

        @GetMapping("b")
        public Object run() throws Exception{
            return ResponseEntity.ok().body("project b");
        }

    }
}
