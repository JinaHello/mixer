package com.ssp.script.service;

import com.ssp.domain.dto.DSPResult;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Log4j2
@Service
public class DSPRedisService {

  @Resource(name="redisMapTemplate")
  private ValueOperations<String, Object> valueOperations;

  public void setRedisSspToDSP(String key, DSPResult result) {
    valueOperations.set(key,result,1L, TimeUnit.MINUTES);
  }

  public DSPResult returnRedisSspToDSP(String key) {
    Map<String, DSPResult> redisInfo = (Map<String, DSPResult>) valueOperations.get(key);

    DSPResult build = null;

    if(redisInfo != null){
      build = DSPResult.builder().point(Double.parseDouble(String.valueOf(redisInfo.get("point"))))
        .krwPoint(Double.parseDouble(String.valueOf(redisInfo.get("krwPoint"))))
        .html(String.valueOf(redisInfo.get("html")))
        .dspTpCode(String.valueOf(redisInfo.get("dspTpCode")))
        .nurl(String.valueOf(redisInfo.get("nurl")))
        .seat(String.valueOf(redisInfo.get("seat")))
        .impid(String.valueOf(redisInfo.get("impid")))
        .cur(String.valueOf(redisInfo.get("cur")))
        .requestid(String.valueOf(redisInfo.get("dspid")))
        .impid(String.valueOf(redisInfo.get("impid")))
        .bidid(String.valueOf(redisInfo.get("bidid")))
        .actionLoss(String.valueOf(redisInfo.get("actionLoss")))
        .adid(String.valueOf(redisInfo.get("adid")))
        .build();
    }

    return build;
  }

}