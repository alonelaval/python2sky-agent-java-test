package com.project.a;

import com.google.common.collect.Maps;
import com.project.common.OkHttpUtil;
import okhttp3.Response;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class AApplication {

    public static void main(String[] args) {
        SpringApplication.run(AApplication.class, args);
    }


    @TraceCrossThread
    public static class MyCallable<String> implements Callable<String> {
        @Override
        public String call() throws Exception {
            try {
                Response response = OkHttpUtil.get("http://localhost:8088/project/b", Maps.newHashMap());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Controller
    @RequestMapping("project")
    class JobController{

        @GetMapping("a")
        public Object run() throws Exception{
            ExecutorService executorService = Executors.newFixedThreadPool(1);
            executorService.submit(new MyCallable());
            return ResponseEntity.ok().body("{a}");
        }

        @GetMapping("cross_process")
        public Object cross_process() throws Exception{

            Response response = OkHttpUtil.get("http://localhost:5000/flask/cross_process", Maps.newHashMap());
            System.out.println(response.body());
            return ResponseEntity.ok().body("{cross_process}");
        }


    }

}
