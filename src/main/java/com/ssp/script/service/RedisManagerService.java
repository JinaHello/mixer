package com.ssp.script.service;

import com.ssp.core.jpa.entity.*;
import com.ssp.core.jpa.repository.CodeRepository;
import com.ssp.core.jpa.repository.MediaDSPRepository;
import com.ssp.core.jpa.repository.MediaGoogleRepository;
import com.ssp.core.jpa.repository.MediaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hibernate.annotations.Proxy;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Log4j2
@Proxy(lazy = false)
@Service
@RequiredArgsConstructor
public class RedisManagerService {

  private final MediaRepository mediaRepository;
  private final MediaDSPRepository mediaDSPRepository;
  private final CodeRepository codeRepository;
  private final MediaGoogleRepository mediaGoogleRepository;
  private static final DateTimeFormatter yyyyMMdd = DateTimeFormatter.ofPattern("yyyyMMdd");

  // sspNo 로 검색해서, onlyone return;
  @Cacheable(cacheManager = "redisCacheManager", value = "findBySspNo", key = "#sspNo", cacheNames = "findBySspNo")
  public MediaEntity findBySspNo(Integer sspNo,Character sspState) {
    log.debug("redis not found findBySspNo db search");
    MediaEntity mediaEntity = mediaRepository.findBySspNoAndSspState(sspNo, sspState).orElse(new MediaEntity());
    Collection<MediaBidSetupEntity> bidSetup = mediaEntity.getBidSetup();

    // PersistentBag 래퍼클래스 -> ArrayList 변경 (JPA 영속성 문제로 인하여 변경)
    List<MediaBidSetupEntity> bidSetupEntityList = new ArrayList<>();
    bidSetupEntityList.addAll(bidSetup);
    mediaEntity.setBidSetup(bidSetupEntityList);
    return mediaEntity;
  }

  // sspNo 와 맵핑되어잇는 DSP List
  @Cacheable(cacheManager = "redisCacheManager", value = "findByDspList", key = "#sspNo", cacheNames = "findByDspList")
  public List<MediaDspEntity> findByDspList(Integer sspNo) {
    log.debug("redis not found findByDspList db search");
    return mediaDSPRepository.findBySspNo(sspNo);
  }

  //ssp script 검색
  @Cacheable(cacheManager = "redisCacheManager", value = "findBycodeTpIdAndCodeId", key = "#scriptCodeId", cacheNames = "findBycodeTpIdAndCodeId")
  public CodeEntity findBycodeTpIdAndCodeId(String scriptTpId, String scriptCodeId) {
    log.debug("redis not found findBycodeTpIdAndCodeId db search");
    CodeEntity codeEntity = codeRepository.findBycodeTpIdAndCodeId(scriptTpId,scriptCodeId).orElse(new CodeEntity());
    log.debug("redis not found findBycodeTpIdAndCodeId db search {}, {}, {}", scriptTpId, scriptCodeId, codeEntity);
    return codeEntity;
  }

  //Google Price 검색
  @Cacheable(cacheManager = "redisCacheManager", value = "findByGoogleSspNo", key = "#sspNo", cacheNames = "findByGoogleSspNo")
  public MediaGoogleEntity findByGoogleSspNo(Integer sspNo) {
    // 비어있는 경우가 많으므로 null이 아닌 빈객체를 생성하여 return 시킵니다.
    MediaGoogleEntity mediaGoogleEntity =
      mediaGoogleRepository.findBySspNo(sspNo, Integer.valueOf(LocalDateTime.now().format(yyyyMMdd)))
        .orElse(new MediaGoogleEntity());
    log.debug("redis not found findByGoogleSspNo db search {}, {}", sspNo, mediaGoogleEntity);
    return mediaGoogleEntity;
  }

}
