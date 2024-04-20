package com.ratelimiter.solution.Rate_Limiter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author gjangid
 *
 */
@SpringBootApplication
@EnableAsync
public class RateLimiterApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(RateLimiterApiApplication.class, args);
	}

}

