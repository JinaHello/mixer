
package com.ssp.script.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Log4j2
@Service
@RequiredArgsConstructor
public class NurlService {
  private final WebClient webClient;
  final HtmlMakeService htmlMake;

  public void nurlWebClient(String dspTpCode, String nurl, StringBuilder htmlBody){

    switch (dspTpCode) {
      case "04" :
      case "07" :
      case "99" :
        htmlBody.append(htmlMake.dotImageSrcMake(nurl));
        break;
      default :
        try {
          ResponseEntity<Void> resp = this.webClient.get()
            .uri(nurl)
            .header("Cache-Control","no-cache")
            .accept(MediaType.IMAGE_JPEG)
            .retrieve()
            .toBodilessEntity()
            .doOnError(throwable -> log.error("dspTpCode : {}, nurl Exception : {}",dspTpCode,throwable.getMessage()))
            .log()
            .block();
          if (resp == null) {
            log.error("dspTpCode : {}, resp is null {}",dspTpCode, nurl);
          } else if (!resp.getStatusCode().is2xxSuccessful()) {
            log.error("dspTpCode : {}, status {}, nurl {}",dspTpCode, resp.getStatusCode(), nurl);
          }
        } catch (Exception e) {
          log.error("dspTpCode : {}, nurl : {}",dspTpCode, nurl, e);
        }
    }
  }
}