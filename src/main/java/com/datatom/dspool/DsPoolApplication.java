package com.datatom.dspool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author 78737
 * 开启全局事务
 */
@SpringBootApplication
@EnableTransactionManagement
@ComponentScan(basePackages = {"com.datatom.dspool.interceptor","com.datatom.dspool.config","com.datatom.dspool.datasource","com.datatom.dspool.mapper", "com.datatom.dspool.service.impl", "com.datatom.dspool.controller"})
public class DsPoolApplication {

    public static void main(String[] args) {
        SpringApplication.run(DsPoolApplication.class, args);
    }

}
