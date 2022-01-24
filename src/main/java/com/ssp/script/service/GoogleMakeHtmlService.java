package com.ssp.script.service;

import com.ssp.domain.constans.SSPConstans;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class GoogleMakeHtmlService {

  public String replaceMacro(String googleBody,
                             String googleId,
                             String slotId,
                             String domain,
                             String width,
                             String height,
                             Integer sspNo) {

    googleBody = googleBody.replace("{{ZONE_ID}}", googleId);
    if(!StringUtils.isEmpty(slotId)){
      googleBody = googleBody.replace("{{SLOT_ID}}", slotId);
    }else{
      googleBody = googleBody.replace("{{SLOT_ID}}", "");
    }
    googleBody = googleBody.replace("{{SITE_URL}}", domain);
    googleBody = googleBody.replace("{{WIDTH}}", width);
    googleBody = googleBody.replace("{{HEIGHT}}", height);
    googleBody = googleBody.replace("{{PB_URL}}", SSPConstans.HTTPS + SSPConstans.DOMAIN + "/script?sspNo=" + sspNo + "&sspPassback=y");
    return googleBody;

  }
}
