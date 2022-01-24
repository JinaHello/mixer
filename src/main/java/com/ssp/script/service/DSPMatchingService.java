package com.ssp.script.service;

import com.ssp.core.jpa.entity.MediaDspEntity;
import com.ssp.domain.constans.CodeConstans;
import com.ssp.domain.constans.SSPConstans;
import com.ssp.domain.cookie.DSPCookie;
import com.ssp.domain.cookie.DSPExceptCookie;
import com.ssp.domain.table.DSPConnectionUrlGroupEnum;
import com.ssp.domain.util.DateUtils;
import com.ssp.domain.vo.DspVO;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Log4j2
@Service
public class DSPMatchingService {

  final HtmlMakeService htmlMake;

  public DSPMatchingService(HtmlMakeService htmlMake) {
    this.htmlMake = htmlMake;
  }

  public String dspFindUUID(String dspTpCode, List<DSPCookie> dspIdCookieList) {
    if (dspIdCookieList != null) {
      for (DSPCookie dspCookie: dspIdCookieList) {
        if (dspCookie.getDc().equals(dspTpCode)) {
          return dspCookie.getDi();
        }
      }
    }
    return null;
  }

  public List<DspVO> dspMappingList(final List<DSPCookie> dspCookieIdList, List<MediaDspEntity> dspList, String pltfomTpCode) {
    List<DspVO> dspVOList = new ArrayList<>();

    dspList.forEach(dsps -> {
      //MIX_MEDIA_DSP 테이블에 DSP가 있을경우
      DSPConnectionUrlGroupEnum dspGroup = DSPConnectionUrlGroupEnum.findByConnectionUrlCode(dsps.getDspTpCode());
      if(dspGroup != null && dsps.getDspTpCode().equals(dspGroup.getDspTpCode())){
        String dspUUID = dspFindUUID(dspGroup.getDspTpCode(), dspCookieIdList);
        if(!SSPConstans.DSP_ID_NONE.equals(dspUUID) && !"".equals(dspGroup.getDspUrl())){
          if (CodeConstans.PLTFOM_APP_TP_CODE.equals(pltfomTpCode) || (!dspGroup.isBuyerIdCheck() || !StringUtils.isEmpty(dspUUID))) {
            DspVO dsp = DspVO.builder()
              .dspUUID(dspUUID)
              .dspTpCode(dspGroup.getDspTpCode())
              .dspUrl(dspGroup.getDspUrl())
              .userId(dspGroup.getUserId())
              .test(dspGroup.getTest())
              .gzip(dspGroup.isGzip())
              .secure(dspGroup.getSecure())
              .build();
            if (dsp != null) dspVOList.add(dsp);
          }
        }
      }
    });
    return dspVOList;
  }

  // Completely excluded
  public boolean completelyExcluded(List<DSPExceptCookie> dspExcepts, String dspCode) {
    for (DSPExceptCookie dspExcept : dspExcepts) {
      if (dspExcept.isEx() && dspCode.equals(dspExcept.getDc())) {
        return true;
      }
    }
    return false;
  }

  // 불완전제외...
  public boolean inCompletelyExcluded(List<DSPExceptCookie> dspExcepts, String dspCode) {
    for (DSPExceptCookie dspExcept : dspExcepts) {
      if (!dspExcept.isEx() && dspCode.equals(dspExcept.getDc())) {
        return true;
      }
    }
    return false;
  }

  public boolean dspConnectionAdd(List<MediaDspEntity> medias, String dspCode) {
    for (MediaDspEntity mediaDspVo : medias) {
      if (dspCode.equals(mediaDspVo.getDspTpCode())) {
        return true;
      }
    }
    return false;
  }

  // 제외 해야할 dsp List
  public List<DspVO> excludedDspList(List<MediaDspEntity> dspList, List<DSPCookie> finalDspCookieIdList){
    List<DSPConnectionUrlGroupEnum> dspGroup = DSPConnectionUrlGroupEnum.findByConnectionUrlCode();
    List<DspVO> dspVOList = new ArrayList<>();

    dspGroup.forEach(GroupEnumList -> {
      Iterator<MediaDspEntity> it = dspList.iterator();
      boolean addDsp = true;

      while (it.hasNext()) {
        MediaDspEntity dspListData = it.next();
        //MIX_MEDIA_DSP 테이블에 제외 DSP가 있을경우 제외하는 로직
        if (GroupEnumList.getDspTpCode().equals(dspListData.getDspTpCode())) {
          it.remove();
          addDsp = false;
          break;
        }
      }

      if (addDsp) {
        String dspUUID = dspFindUUID(GroupEnumList.getDspTpCode(), finalDspCookieIdList);
        DspVO dsp = DspVO.builder()
          .dspUUID(dspUUID)
          .dspTpCode(GroupEnumList.getDspTpCode())
          .dspUrl(GroupEnumList.getDspUrl())
          .userId(GroupEnumList.getUserId())
          .build();
        if (dsp != null) dspVOList.add(dsp);
      }
    });

    return dspVOList;
  }

  public String dotImageMake(String sspUUID){
    return htmlMake.dotImageSrcMake(DSPConnectionUrlGroupEnum.findByConnectionUrlCode(CodeConstans.DSP_CODE_TP_ID_MOBON).getCookieMatchingUrl() + sspUUID);
  }

  public String dotImageMake(List<DspVO> medias, String sspUUID){
    return medias.stream()
            .map(mediaDspVo -> DSPConnectionUrlGroupEnum.findByConnectionUrlCode(mediaDspVo.getDspTpCode()))
            .filter(dspGroup -> dspGroup != null && dspGroup.getCookieMatchingUrl() != null)
            .map(dspGroup -> htmlMake.dotImageSrcMake(dspGroup.getCookieMatchingUrl() + sspUUID))
            .collect(Collectors.joining());
  }

  // 제외 목록 쿠키
  public List<DSPExceptCookie> excluded(List<DSPCookie> dspCookies, List<DSPExceptCookie> dspExcepts, double point) {
    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyyMMddHHmmss", Locale.KOREA);
    String mTime = LocalDateTime.now().format(dateFormat);

    List<DSPExceptCookie> finalDspExcepts = new ArrayList<>();
    /*
      String dc;     // dspCode : DSP_TP_CODE
      boolean ex;    //
      String st;     // 날짜 업데이트
     */
    for (DSPCookie dspCookie : dspCookies) {
      boolean duplicateDc = true;
      for (DSPExceptCookie dspExcept : dspExcepts) {
        if (dspCookie.getDc().equals(dspExcept.getDc())) {
          if (dspExcept.isEx()) {
            LocalDateTime reqDateTime = LocalDateTime.parse(dspExcept.getSt(),dateFormat);
            long diffSec = DateUtils.getDiff(LocalDateTime.now(), reqDateTime, "HOURS");
            // 1시간 지났는지 확인 후 제외조건을 초기화 시킴.
            if(diffSec > 1) duplicateDc = false;
          }
          break;
        }
      }
      if (duplicateDc) {
        try {
          if ((dspCookie.getWn() / dspCookie.getBq()) * 100 > 1) {
            if (dspCookie.getBq() > 300 && dspCookie.getWn() == 0) {
              finalDspExcepts.add(DSPExceptCookie.builder().dc(dspCookie.getDc()).ex(true).st(mTime).build());
            } else {
              finalDspExcepts.add(DSPExceptCookie.builder().dc(dspCookie.getDc()).ex(false).st(mTime).build());
            }
          } else if (point > dspCookie.getP() && dspCookie.getP() > 0) {
            finalDspExcepts.add(DSPExceptCookie.builder().dc(dspCookie.getDc()).ex(false).st(mTime).build()); // 그냥 제거
          }
        } catch (Exception e) {
          log.error("cookie is not bid request, bid response {}", dspCookie);
        }
      }
    }
    return finalDspExcepts;
  }

}
