package com.ssp.script.config;

import io.netty.channel.ChannelOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Slf4j
@Configuration
public class WebClientConfig {

	@Bean
	public WebClient webClient() {
		HttpClient httpClient = HttpClient.create()
			.tcpConfiguration(client -> client.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 600))
			.doOnError((request, error) -> log.error(error.getLocalizedMessage()), (response, error) -> log.error(error.getLocalizedMessage()));
		return WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient)).build();
	}

}