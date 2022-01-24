
package com.ssp.script.service;

import com.google.gson.Gson;
import com.ssp.core.jpa.entity.MediaEntity;
import com.ssp.domain.constans.SSPConstans;
import com.ssp.domain.constans.SSPTopicEnum;
import com.ssp.domain.vo.MixDspSspHhStats;
import com.ssp.domain.vo.MixSspHhStats;
import com.ssp.kafka.service.Sender;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Log4j2
@Service
@RequiredArgsConstructor
public class TopicService {
  final Sender sender;

  private static final DateTimeFormatter hh = DateTimeFormatter.ofPattern("HH");
  private static final DateTimeFormatter yyyyMMdd = DateTimeFormatter.ofPattern("yyyyMMdd");

  public void mixSspTopic(Integer sspNo, MediaEntity media, String display) {
    //요청수 TopicLocalDate
    MixSspHhStats mixSspHhStats = new MixSspHhStats();
		mixSspHhStats.setStatsDttm(Integer.parseInt(LocalDateTime.now().format(yyyyMMdd)));
    mixSspHhStats.setStatsHh(LocalDateTime.now().format(hh));
		mixSspHhStats.setSspNo(sspNo);
		mixSspHhStats.setPltfomTpCode(media.getPltfomTpCode());
		mixSspHhStats.setUserId(media.getUserId());

    if(SSPConstans.TOPIC_PASSBACK.equals(display)){
      mixSspHhStats.setPassback(1);       // 패스백 올릴 경우
    }else if(SSPConstans.TOPIC_UNEXPOSED.equals(display)){
      mixSspHhStats.setReqCnt(1);         // 요청 수
      mixSspHhStats.setUnexposed(1);      // 패스백 값이 없을 경우
    }else{
      mixSspHhStats.setReqCnt(1);         // 요청 수
    }

    sender.send(SSPTopicEnum.MIX_SSP_TOPIC.getTopic(), new Gson().toJson(mixSspHhStats));
  }

  public void mixDspSspTopic(Integer sspNo,
                             String pltfomTpCode,
                             String userId,
                             double krwPoint,
                             double point,
                             String dspTpCode,
                             String topicCntStr) {
    //노출 Topic
    MixDspSspHhStats mixDspSspHhStats = new MixDspSspHhStats();
    mixDspSspHhStats.setStatsDttm(Integer.parseInt(LocalDateTime.now().format(yyyyMMdd)));
    mixDspSspHhStats.setStatsHh(LocalDateTime.now().format(hh));
    mixDspSspHhStats.setSspNo(sspNo);
    mixDspSspHhStats.setPltfomTpCode(pltfomTpCode);
    mixDspSspHhStats.setUserId(userId);
    mixDspSspHhStats.setDspTpCode(dspTpCode);    // 낙찰한 dcope

    if(SSPConstans.TOPIC_PAR_EPRS_CNT.equals(topicCntStr)){
      mixDspSspHhStats.setDspStlmntAmt(krwPoint);     // DSP 결제 금액
      mixDspSspHhStats.setParEprsCnt(1);           // 광고 노출 시 +1
    }else if(SSPConstans.TOPIC_REL_PAR_EPRS_CNT.equals(topicCntStr)){
      mixDspSspHhStats.setRelDspStlmntAmt(krwPoint);  // 실제광고 노출에 대한 DSP 결제 금액
      mixDspSspHhStats.setRelDspCurStlmntAmt(point);  // 실제광고 노출에 대한 DSP 결제 금액(DSP 통화 기준)
      mixDspSspHhStats.setRelParEprsCnt(1);        // 실제 광고 노출 시 +1
    }else if(SSPConstans.TOPIC_CLICK_CNT.equals(topicCntStr)){
      mixDspSspHhStats.setDspStlmntAmt(krwPoint);     // DSP 결제 금액
      mixDspSspHhStats.setClickCnt(1);             // 클릭 시
    }

    sender.send(SSPTopicEnum.MIX_DSP_SSP_TOPIC.getTopic(), new Gson().toJson(mixDspSspHhStats)); // 낙찰 토픽
  }

}