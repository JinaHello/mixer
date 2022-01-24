package com.ssp.script.config;

import com.ssp.script.domain.DSP;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
@RefreshScope
@ConfigurationProperties(prefix="dsp")
public class DspListConfig {
	List<DSP> list;
}
