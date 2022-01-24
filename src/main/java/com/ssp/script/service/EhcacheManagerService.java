package com.ssp.script.service;

import com.ssp.core.jpa.entity.CodeEntity;
import com.ssp.core.jpa.entity.MediaDspEntity;
import com.ssp.core.jpa.entity.MediaEntity;
import com.ssp.core.jpa.entity.MediaGoogleEntity;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
public class EhcacheManagerService {

  final RedisManagerService redisCacheManager;

  public EhcacheManagerService(RedisManagerService redisCacheManager) {
    this.redisCacheManager = redisCacheManager;
  }

  @Cacheable(cacheManager = "ehCacheManager", value = "findBySspNo", key = "#sspNo", cacheNames = "findBySspNo")
  public MediaEntity findBySspNo(Integer sspNo,Character sspState) {
    log.debug("ehcache not found findBySspNo redis search {},{}",sspNo,sspState);
    return redisCacheManager.findBySspNo(sspNo, sspState);

  }

  @Cacheable(cacheManager = "ehCacheManager", value = "findByDspList", key = "#sspNo", cacheNames = "findByDspList")
  public List<MediaDspEntity> findByDspList(Integer sspNo) {
    log.debug("ehcache not found findByDspList redis search {}",sspNo);
    return redisCacheManager.findByDspList(sspNo);
  }

  @Cacheable(cacheManager = "ehCacheManager", value= "findBycodeTpIdAndCodeId", key = "#scriptCodeId", cacheNames = "findBycodeTpIdAndCodeId")
  public CodeEntity findBycodeTpIdAndCodeId(String scriptTpId, String scriptCodeId) {
    log.debug("ehcache not found findBycodeTpIdAndCodeId redis search {},{}",scriptTpId,scriptCodeId);
    return redisCacheManager.findBycodeTpIdAndCodeId(scriptTpId,scriptCodeId);
  }

  @Cacheable(cacheManager = "ehCacheManager", value = "findByGoogleSspNo", key = "#sspNo", cacheNames = "findByGoogleSspNo" )
  public MediaGoogleEntity findByGoogleSspNo(Integer sspNo) {
    log.debug("ehcache not found findByGoogleSspNo redis search {}", sspNo);
    return redisCacheManager.findByGoogleSspNo(sspNo);
  }

}
