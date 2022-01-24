
package com.ssp.script.service;

import com.ssp.core.jpa.entity.CodeEntity;
import com.ssp.core.jpa.entity.MediaBidSetupEntity;
import com.ssp.core.jpa.entity.MediaEntity;
import com.ssp.core.jpa.entity.MediaGoogleEntity;
import com.ssp.domain.constans.SSPConstans;
import com.ssp.domain.dto.DSPResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Log4j2
@Service
@RequiredArgsConstructor
public class WinDspService {

  final EhcacheManagerService mediaSearch;
  final DSPRedisService dSPRedis;
  final MacroService macroService;
  final TopicService topicService;
  private final GoogleMakeHtmlService googleMakeHtmlService;
  private final HtmlMakeService htmlMake;
  private final NurlService nurlService;

  public StringBuilder winDspHtmlBody(DSPResult result,
                                      MediaEntity media,
                                      MediaGoogleEntity mediaGoogle,
                                      Integer sspNo,
                                      String sspUUID ) {

    double krwPoint = result.getKrwPoint();  // 마지막에 낙찰한 금액으로 업데이트
    double point = result.getPoint();

    StringBuilder body = new StringBuilder();
    // passback 시에는 구글 체크하지않음 (mediaGoogle != null)
    // googleId가 없는 경우 (DB에 구글 아이디가 X)
    // dsp 보다 구글입찰가가 높은경우
    // 최저입잘가 (구글 최저입찰가)
    if (mediaGoogle != null
      && mediaGoogle.getGoogleId() != null
      && krwPoint < Double.parseDouble(mediaGoogle.getGooglePrice())
      && media.getBidSetup().stream().findFirst().map(MediaBidSetupEntity::getBidFormVal).orElse(0) <= Double.parseDouble(mediaGoogle.getGooglePrice())) {

      CodeEntity mediaFormatScript = mediaSearch.findBycodeTpIdAndCodeId(SSPConstans.GOOGLE_TP_ID, mediaGoogle.getGoogleZoneTpCode());

      if (mediaFormatScript != null) {
        String googleBody = googleMakeHtmlService.replaceMacro( mediaFormatScript.getCodeDesc(),
          mediaGoogle.getGoogleId(),
          mediaGoogle.getGoogleSlotId(),
          media.getDomain(),
          media.getAdSizeWMin(),
          media.getAdSizeHMin(),
          sspNo);
        body.append(googleBody);

        log.info("Final Google Dsp Win Info : SspNo - {}, SspNm - {}, GoogleZoneTpCode - {}, GoogleId - {}, point - {}",
                sspNo,media.getSspScriptNm(),mediaGoogle.getGoogleZoneTpCode(),mediaGoogle.getGoogleId(),mediaGoogle.getGooglePrice());

        //Dsp Redis 추가
        dSPRedis.setRedisSspToDSP(SSPConstans.SSP_NO + SSPConstans.REDIS_DEPT + sspNo + SSPConstans.REDIS_DEPT + sspUUID, result);
      }
    }

    // passback인경우 && point금액이 구글금액보다 큰 경우
    // SSPConstans.EMPTY.equals(passback) 면 body는 비어있지 않아야함. 그러므로 passback됨.
    if (body.length() == 0) {
      // win Noti
      macroService.dspMacro(sspNo, result, media.getPltfomTpCode(), media.getUserId());
      body.append(result.getHtml());

      // TODO TTD status 302 FOUND Error로 잠시 주석처리
      /*
      if (!"null".equals(result.getNurl()) && !StringUtils.isEmpty(result.getNurl())) {
        nurlService.nurlWebClient(result.getDspTpCode(), result.getNurl(), body);
      }
      */

      log.info("Final Dsp Win Info : SspNo - {},SspNm - {}, DspTpCode - {},  krwAmt - {}, point - {}",
              sspNo,media.getSspScriptNm(),result.getDspTpCode(),result.getKrwPoint(),result.getPoint());

      //실제요청수
      body.append(htmlMake.viewImageSrcMake(sspNo, result, krwPoint, point, media.getPltfomTpCode(), media.getUserId()));
      //낙찰 Topic
      topicService.mixDspSspTopic(sspNo,media.getPltfomTpCode(), media.getUserId(),krwPoint,point,result.getDspTpCode(),
        SSPConstans.TOPIC_PAR_EPRS_CNT);
    }

    //passback 이 아닌경우만 광고 요청수 +1
    if (mediaGoogle != null) {
      topicService.mixSspTopic(sspNo,media,SSPConstans.TOPIC_EMPTY);
    }
    //body.append(dspMatching.dotImageMake(dspVOList, sspUUID)); TODO 우선은 모비온 하드코딩
    return body;
  }
}
