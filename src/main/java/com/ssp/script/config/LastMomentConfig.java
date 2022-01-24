package com.ssp.script.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@RefreshScope
@ConfigurationProperties(prefix="lastmoment")
public class LastMomentConfig {
	boolean check;
}