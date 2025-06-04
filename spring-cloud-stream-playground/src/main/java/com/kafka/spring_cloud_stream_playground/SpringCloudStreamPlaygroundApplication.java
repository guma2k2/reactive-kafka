package com.kafka.spring_cloud_stream_playground;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.kafka.spring_cloud_stream_playground.${sec}")
public class SpringCloudStreamPlaygroundApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudStreamPlaygroundApplication.class, args);
	}

}
