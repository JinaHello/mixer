package com.ssp.script.service;

import com.ssp.script.config.LastMomentConfig;
import com.ssp.script.constants.LastMomentHtmlConstants;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RefreshScope
public class LastMomentService {

	private final boolean lastMoment;
	private final LastMomentConfig lastMomentConfig;

	public LastMomentService(@Value("${lastmoment}") boolean lastMoment, LastMomentConfig lastMomentConfig) {
		this.lastMoment = lastMoment;
		this.lastMomentConfig = lastMomentConfig;
	}

	public boolean isLastMoment() {
		return (lastMoment || (lastMomentConfig != null && lastMomentConfig.isCheck())) ;
	}

	public String makeLastMomentHtml(String width, String height, boolean isApp) {
		log.error("last moment {} or {}", lastMoment, lastMomentConfig);

		if (isApp) {
			return LastMomentHtmlConstants.APP_MOBONSDK_FAILCALLBACK_SCRIPT;
		}

		String html = LastMomentHtmlConstants.WEB_OTJALNAM_HTML;
		html = html.replace("{{w}}", width);
		html = html.replace("{{h}}", height);
		return html;
	}
}
