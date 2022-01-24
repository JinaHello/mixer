package com.ssp.script.controller;

import com.ssp.domain.constans.SSPConstans;
import com.ssp.kafka.service.Sender;
import com.ssp.script.service.TopicService;
import io.netty.util.internal.StringUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;

@Log4j2
@RestController
public class ClickController {
  final Sender sender;
  final TopicService topicService;

  public ClickController(Sender sender, TopicService topicService) {
    this.sender = sender;
    this.topicService = topicService;
  }

  @Value("${lastmoment}")
  private boolean lastmoment;

  @ResponseBody
  @ResponseStatus(value = HttpStatus.OK)
  @GetMapping(value = "/click")
  public void click(@RequestParam("sspNo") int sspNo,
                    @RequestParam("dspTpCode") String dspTpCode,
                    @RequestParam("pltfomTpCode") String pltfomTpCode,
                    @RequestParam("userId") String userId,
                    @RequestParam(required = false, name = "ad_url") String ad_url,
                    HttpServletResponse response)  {


    if(lastmoment) {
      if(!StringUtil.isNullOrEmpty(ad_url)){
        try {
          response.sendRedirect(URLDecoder.decode(ad_url,"UTF-8"));
        } catch (IOException e) {
          log.error("ad_url is validation {}", ad_url);
        }
      }
    } else {
      //낙찰 Topic
      topicService.mixDspSspTopic(sspNo,pltfomTpCode, userId, 0 , 0 ,dspTpCode, SSPConstans.TOPIC_CLICK_CNT);

      if(!StringUtil.isNullOrEmpty(ad_url)){
        try {
          response.sendRedirect(ad_url);
        } catch (IOException e) {
          log.error("ad_url is validation {}", ad_url);
        }
      }
    }
  }
}
