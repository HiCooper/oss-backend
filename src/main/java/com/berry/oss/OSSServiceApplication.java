package com.berry.oss;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;

/**
 * @author xueancao
 */
@SpringCloudApplication
public class OSSServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OSSServiceApplication.class, args);
    }
}
