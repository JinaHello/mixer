package com.ssp.script.service;

import com.ssp.domain.constans.SSPConstans;
import com.ssp.domain.dto.DSPResult;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class HtmlMakeService {
	@Value("${ssp.server}")
	String server;

	public String fullHtmlBody(String bodyString, String pltfomTpCode, String w) {
		StringBuilder html = new StringBuilder();
		html.append("<html>\n");
		html.append("<header>\n");
		html.append("<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">");
		html.append("<title>openRtb HTML</title>\n");
		html.append("</header>\n");

		if(pltfomTpCode.equals("03")){				// app 가운데 정렬
			html.append("<body style='margin:0 auto;width:").append(w).append("px;'>\n");
			html.append("<script src='mraid.js'></script>");
		}else{																// WEB, MOBILE
			html.append("<body style=margin:0px;>\n");
		}

		html.append(bodyString).append("</body>\n").append("</html>");

		return html.toString();
	}

	public String dotImageSrcMake(String dspCookieMatchingUrl) {
		return "<img src='" + dspCookieMatchingUrl + "' style='display:none;'>";
	}

	public String dotImageSrcMake2(String dspCookieMatchingUrl) {
		return "<img src='" + dspCookieMatchingUrl + "'>";
	}

	/* 실제 광고 노출 */
	public String viewImageSrcMake(Integer sspNo,
								   DSPResult result,
								   double krwPoint,
								   double point,
								   String pltfomTpCode,
								   String userId) {
		StringBuilder html = new StringBuilder();

		html.append("<img src='" + SSPConstans.HTTPS + server + "/view?sspNo=" + sspNo + "&dspTpCode=" + result.getDspTpCode() +
				"&krwPoint=" + krwPoint + "&point=" + point + "&pltfomTpCode=" + pltfomTpCode + "&userId=" + userId +
				"' style='display:none;'>");

		if(pltfomTpCode.equals("03")){
			html.append("<script>console.log('AdapterSuccessCallback_"+sspNo+"');</script>");
		}

		return html.toString();
	}
}
