package com.kh.lifeFit.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {

    @Bean
    public RedissonClient redissonClient(RedisProperties redisProperties) {
        // TLS 여부에 따라 redis:// vs rediss:// 선택
        boolean sslEnabled = false;
        if (redisProperties.getSsl() != null) {
            sslEnabled = redisProperties.getSsl().isEnabled();
        }

        String scheme = sslEnabled ? "rediss://" : "redis://";
        String address = scheme + redisProperties.getHost() + ":" + redisProperties.getPort();

        Config config = new Config();
        config.useSingleServer()
                .setAddress(address)
                .setPassword(redisProperties.getPassword());

        return Redisson.create(config);
    }

}
