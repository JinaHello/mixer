package com.ssp.script;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
//@ComponentScan("com.ssp.core.jpa.vo")
@EntityScan("com.ssp.core.jpa.entity")
@EnableJpaRepositories("com.ssp.core.jpa.repository")
@ComponentScan({"com.ssp.kafka","com.ssp.script"})
@EnableScheduling
public class ScriptApplication {
  public static void main(String[] args) {
    SpringApplication.run(ScriptApplication.class, args);
  }
}
