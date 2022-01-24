
package com.ssp.script.controller;

import com.ssp.domain.constans.SSPConstans;
import com.ssp.kafka.service.Sender;
import com.ssp.script.service.TopicService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
public class ViewController {
  final Sender sender;
  final TopicService topicService;

  public ViewController(Sender sender, TopicService topicService) {
    this.sender = sender;
    this.topicService = topicService;
  }

  @ResponseBody
  @ResponseStatus(value = HttpStatus.OK)
  @GetMapping(value = "/view")
  public void view(@RequestParam("sspNo") int sspNo,
                   @RequestParam("dspTpCode") String dspTpCode,
                   @RequestParam("pltfomTpCode") String pltfomTpCode,
                   @RequestParam("userId") String userId,
                   @RequestParam("krwPoint") double krwPoint,
                   @RequestParam("point") double point)  {

    //낙찰 Topic
    topicService.mixDspSspTopic(sspNo,pltfomTpCode,userId,krwPoint,point,dspTpCode,SSPConstans.TOPIC_REL_PAR_EPRS_CNT);
  }
}