package com.ssp.script.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ssp.domain.constans.SSPConstans;
import com.ssp.domain.dto.DSPResult;
import com.ssp.domain.dto.SSPToDspDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.channel.ChannelOperations;

import java.time.Duration;

@Log4j2
@Service
@RequiredArgsConstructor
public class SSPToDSPConnectionService {

	@Value("${dsp.server}")
	private String sever;

	private final WebClient webClient;

	public DSPResult sspToDSPConnectionMakeBody(SSPToDspDTO sspToDsp) {

		Gson gson = new Gson();
		String bidSample = gson.toJson(sspToDsp);
		try {
			String result = this.webClient.post()
				.uri(SSPConstans.HTTP + sever + "/dsp")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.bodyValue(bidSample)
				.retrieve()
				.bodyToMono(String.class)
				.timeout(Duration.ofMillis(600))
				.block(); // 비동기 처리를 막기 위해선 block 처리해야함.

			return gson.fromJson(result, new TypeToken<DSPResult>() {
			}.getType());
		} catch (Exception e) {
			log.error("body error >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> {}", e.getMessage());
		}
		return null;
	}
}
