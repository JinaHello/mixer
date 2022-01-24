package com.ssp.script.controller;

import com.ssp.core.jpa.repository.MediaDSPRepository;
import com.ssp.script.config.CacheConfig;
import com.ssp.script.config.LastMomentConfig;
import com.ssp.script.config.RedisConfig;
import com.ssp.script.service.RedisManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@Log4j2
@RestController
@RequiredArgsConstructor
public class HealthController {

	private final MediaDSPRepository mediaDSPRepository;
	private final LastMomentConfig lastMomentConfig;
	final RedisManagerService redisCacheManager;

	@Autowired
	private CacheConfig cacheConfig;

	@Autowired
	private RedisConfig redisConfig;

	@Value("${lastmoment}")
	private boolean lastmoment;

	@ResponseBody
	@GetMapping(value = "/health")
	public boolean scriptHealthCheck(HttpServletResponse response) {
		if (lastmoment || (lastMomentConfig != null && lastMomentConfig.isCheck())) {
			// 최후의 순간 Check
			response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
		} else if (mediaDSPRepository.findBySspNo(14).size() == 0) {
			// DB Check
			response.setStatus(HttpStatus.NO_CONTENT.value());
		}

		return lastmoment;
	}
}
