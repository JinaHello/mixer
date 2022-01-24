package com.ssp.script.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ssp.core.jpa.entity.CodeEntity;
import com.ssp.core.jpa.entity.MediaEntity;
import com.ssp.core.jpa.entity.MediaGoogleEntity;
import com.ssp.domain.constans.CodeConstans;
import com.ssp.domain.constans.SSPConstans;
import com.ssp.domain.cookie.CookieSameSite;
import com.ssp.domain.cookie.DSPCookie;
import com.ssp.domain.dto.DSPResult;
import com.ssp.domain.dto.SSPToDspDTO;
import com.ssp.domain.util.CookieAdd;
import com.ssp.domain.vo.DspVO;
import com.ssp.domain.vo.UserAgentVO;
import com.ssp.kafka.service.Sender;
import com.ssp.script.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Log4j2
@RestController
@RequiredArgsConstructor
public class ScriptController {

	final EhcacheManagerService mediaSearch;
	final DSPMatchingService dspMatching;
	final HtmlMakeService htmlMake;
	final SSPToDSPConnectionService sspToDSPConnection;
	final MacroService macroService;
	final DSPRedisService dSPRedis;
	final Sender sender;
	final TopicService topicService;
	private final GoogleMakeHtmlService googleMakeHtmlService;
	private final UserAgentService userAgentService;
	private final LastMomentService lastMomentService;
	private final WinDspService winDspService;

	@ResponseStatus(value = HttpStatus.OK)
	@ResponseBody
	@GetMapping(value = "/script")
	public String scriptCall(
		HttpServletResponse response, // si cookie write 를 위한 response
		HttpServletRequest request,
		@CookieValue(value = SSPConstans.DSP_ID, defaultValue = SSPConstans.EMPTY) String dspIdCookie,
		@CookieValue(value = SSPConstans.SSP_ID, defaultValue = SSPConstans.EMPTY) String sspIdCookie,
		@RequestHeader(value = SSPConstans.USER_AGENT, defaultValue = SSPConstans.EMPTY) String userAgent,
		@RequestParam(name = "sspNo") Integer sspNo,
		@RequestParam(name = "page", defaultValue = SSPConstans.EMPTY) String page,
		@RequestParam(name = "w", defaultValue = SSPConstans.DEFAULT_SIZE) String w,
		@RequestParam(name = "h", defaultValue = SSPConstans.DEFAULT_SIZE) String h,
		@RequestParam(name = "ver", defaultValue = SSPConstans.EMPTY) String ver,
		@RequestParam(name = "carrier", defaultValue = SSPConstans.EMPTY) String carrier,
		@RequestParam(name = "ifa", defaultValue = SSPConstans.EMPTY) String ifa,
		@RequestParam(name = "sspPassback", defaultValue = SSPConstans.EMPTY) String sspPassback) {

		// 최후의 순간을 대비하는 html
		if (lastMomentService.isLastMoment()) {
			boolean isApp = !SSPConstans.EMPTY.equals(ifa); // ifa에 값이 있으면 APP에서 호출한 것으로 판단
			return lastMomentService.makeLastMomentHtml(w, h, isApp);
		}

		String ip = request.getHeader("X-Forwarded-For");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}

		if (SSPConstans.EMPTY.equals(userAgent)) {
			userAgent = request.getHeader("User-Agent");
		}
		UserAgentVO userAgentVO = userAgentService.userAgentMake(userAgent.toLowerCase());
		response.setContentType("text/html; charset=utf-8");

		String sspUUID;
		if (SSPConstans.EMPTY.equals(page)) {
			page = request.getHeader("referer");
			if (StringUtils.isEmpty(page)) {
				page = SSPConstans.EMPTY;
			}
		}

		// si 체크
		if (SSPConstans.EMPTY.equals(sspIdCookie)) {
			sspUUID = UUID.randomUUID().toString(); // si 가 없을시 si 를 새로 생성합니다.
			CookieSameSite cookie = CookieAdd.makeCookieSameSite(SSPConstans.SSP_ID, sspUUID, 30 * 24 * 60 * 60 * 12);
			if (response == null || cookie == null) return null;

			response.addHeader("Set-Cookie", cookie.addCookie());
		} else {
			sspUUID = sspIdCookie;
		}

		MediaEntity media = mediaSearch.findBySspNo(sspNo, CodeConstans.SSP_STATE);
		if (media == null) {
			response.setStatus(HttpStatus.NO_CONTENT.value());
			return null; // 204 NO_CONTENT 처리
		}

		Gson gson = new Gson();
		// 쿠키 값
		List<DSPCookie> dspIdCookieList = null;
		if (!SSPConstans.EMPTY.equals(dspIdCookie)) {
			dspIdCookieList = gson.fromJson(dspIdCookie, new TypeToken<ArrayList<DSPCookie>>() {
			}.getType());
		}

		// sspNo에 맵핑되어있는 DSP LIST
		List<DspVO> dspVOList = dspMatching.dspMappingList(dspIdCookieList, mediaSearch.findByDspList(sspNo), media.getPltfomTpCode());

		MediaGoogleEntity mediaGoogle = null;
		// PASSBACK 일 시에, 굳이 DBCall을 하지 않는다.
		if (SSPConstans.EMPTY.equals(sspPassback)) {
			mediaGoogle = mediaSearch.findByGoogleSspNo(sspNo);
		}

		if (!dspVOList.isEmpty()) {
			SSPToDspDTO sspToDspDTO = getSspToDspDTO(userAgent, page, ver, carrier, ifa, ip, userAgentVO, sspUUID, media, dspVOList);

			DSPResult result;
			if (mediaGoogle == null) { // passback 시에는 구글 체크하지 않는다.
				result = dSPRedis.returnRedisSspToDSP(SSPConstans.SSP_NO+SSPConstans.REDIS_DEPT+sspNo+SSPConstans.REDIS_DEPT+sspUUID);
			} else {
				result = sspToDSPConnection.sspToDSPConnectionMakeBody(sspToDspDTO);
			}
			if (result != null && result.getDspTpCode() != null) {
				StringBuilder body = winDspService.winDspHtmlBody(result,media,mediaGoogle,sspNo,sspUUID);
				body.append(dspMatching.dotImageMake(sspUUID));
				body.append(dspMatching.dotImageMake(dspVOList, sspUUID));
				return htmlMake.fullHtmlBody(body.toString(),media.getPltfomTpCode(), media.getAdSizeWMin());
			}
		}

		StringBuilder body = new StringBuilder();
		// 광고가 나갈 게 없을 시 구글을 태웁니다.
		if (mediaGoogle != null && mediaGoogle.getGoogleId() != null) { //passback 아닐경우 && 구글 id가 등록이 된 경우
			CodeEntity mediaFormatScript = mediaSearch.findBycodeTpIdAndCodeId(SSPConstans.GOOGLE_TP_ID, mediaGoogle.getGoogleZoneTpCode());

			if (mediaFormatScript != null && !StringUtils.isEmpty(mediaFormatScript.getCodeDesc())) {
				String googleBody = googleMakeHtmlService.replaceMacro(mediaFormatScript.getCodeDesc(),
									 mediaGoogle.getGoogleId(),
									 mediaGoogle.getGoogleSlotId(),
									 media.getDomain(),
									 media.getAdSizeWMin(),
									 media.getAdSizeHMin(),
									 sspNo);
				body.append(googleBody);
			} else {
				log.error("googleBody is not found {}", mediaGoogle);
			}
		}

		// passback 인 경우 || 구글은 무조건 있어야하는 데, 혹시 몰라, 없을 경우에 대한 에러 대응 작업.
		if (StringUtils.isEmpty(body) || body.length() == 0) {
			if (StringUtils.isEmpty(media.getSspRstVal())) {
				if (mediaGoogle != null) {			//passback 이 아닌경우만 광고 요청수 +1
					topicService.mixSspTopic(sspNo,media,SSPConstans.TOPIC_UNEXPOSED);	// 패스백 값이 없을 경우
				}
				response.setStatus(HttpStatus.NO_CONTENT.value());
				return null; // 204 NO_CONTENT 처리
			} else {  //passback 인경우
				if ("01".equals(media.getSspRstChk())) {        // 01 : URL, 02 : 스크립트
					body.append(htmlMake.dotImageSrcMake2(media.getSspRstVal()));
				} else {
					response.setContentType("text/html; charset=utf-8");
					body.append(media.getSspRstVal());            // 대체 배너(패스백, 하우스 배너) 광고 셋팅
				}
				topicService.mixSspTopic(sspNo,media,SSPConstans.TOPIC_PASSBACK);		// 패스백 올릴 경우
			}
		}

		//passback 이 아닌경우만 광고 요청수 +1
		if (mediaGoogle != null) {
			topicService.mixSspTopic(sspNo,media,SSPConstans.TOPIC_EMPTY);
		}

		body.append(dspMatching.dotImageMake(sspUUID)); // TODO 우선 모비온 DSP 하드코딩.
		body.append(dspMatching.dotImageMake(dspVOList, sspUUID));

		return htmlMake.fullHtmlBody(body.toString(),media.getPltfomTpCode(),media.getAdSizeWMin());
	}

	private SSPToDspDTO getSspToDspDTO(String userAgent, String page, String ver, String carrier, String ifa, String ip,
																		 UserAgentVO userAgentVO, String sspUUID, MediaEntity media, List<DspVO> dspVOList) {
		return SSPToDspDTO.builder()
			.dspList(dspVOList)
			.sspId(media.getSspNo())
			.sspUUID(sspUUID)
			.page(page)
			.userAgent(userAgent)
			.ip(ip)
			.deviceType(userAgentVO.getDeviceType())
			.os(userAgentVO.getOs())
			.osv(userAgentVO.getOsv())
			.model(userAgentVO.getOs())
			.ver(ver)
			.carrier(carrier)
			.ifa(ifa)
			.publisherId(media.getSeller() != null ? media.getSeller().getSellerId() : "")
			.publisherName(media.getSeller() != null ? media.getSeller().getName() : "")
			.pltfomTpCode(media.getPltfomTpCode()).build();
	}
}
