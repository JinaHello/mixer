package com.ssp.script.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class CacheConfig {

  @Bean(name = "redisCacheManager")
  public RedisCacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
    return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(defaultConf())
            .withInitialCacheConfigurations(confMap())
            .build();
  }

  @Bean(name = "ehCacheManager")
  @Primary
  public CacheManager cacheManager() {
    return new EhCacheCacheManager(ehCacheCacheManager().getObject());
  }

  @Bean
  public EhCacheManagerFactoryBean ehCacheCacheManager() {
    EhCacheManagerFactoryBean cmfb = new EhCacheManagerFactoryBean();
    cmfb.setConfigLocation(new ClassPathResource("/config/ehcache.xml"));
    cmfb.setShared(true);
    return cmfb;
  }

  private RedisCacheConfiguration defaultConf() {
    return RedisCacheConfiguration.defaultCacheConfig()
//            .serializeKeysWith(fromSerializer(new StringRedisSerializer()))
//            .serializeValuesWith(fromSerializer(new GenericJackson2JsonRedisSerializer()))
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
            .entryTtl(Duration.ofMinutes(1));
  }

  private Map<String, RedisCacheConfiguration> confMap() {
    Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
    cacheConfigurations.put("findBySspNo", defaultConf().entryTtl(Duration.ofMinutes(30L)));
    cacheConfigurations.put("findByDspList", defaultConf().entryTtl(Duration.ofMinutes(30L)));
    cacheConfigurations.put("findBycodeTpIdAndCodeId", defaultConf().entryTtl(Duration.ofDays(31)));
    cacheConfigurations.put("findByGoogleSspNo", defaultConf().entryTtl(Duration.ofMinutes(30L)));
    return cacheConfigurations;
  }

}