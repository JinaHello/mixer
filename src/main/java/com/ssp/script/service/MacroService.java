
package com.ssp.script.service;

import com.ssp.domain.constans.CodeConstans;
import com.ssp.domain.constans.SSPConstans;
import com.ssp.domain.dto.DSPResult;
import com.ssp.domain.util.AES256Util;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Log4j2
@Service
public class MacroService {
	@Value("${ssp.server}")
	String server;

	public void dspMacro(Integer sspNo, DSPResult result, String pltfomTpCode, String userId) {

		String nurl = result.getNurl();
		String adm = result.getHtml();
		if (nurl != null) {
			nurl = replaceMacro(sspNo, result, nurl, pltfomTpCode, userId, CodeConstans.MACRO_TYPE_NURL);
			result.setNurl(nurl);
		}
		if (adm != null) {
			adm = replaceMacro(sspNo, result, adm, pltfomTpCode, userId,CodeConstans.MACRO_TYPE_ADM);
			result.setHtml(adm);
		}
	}

	private String replaceMacro(Integer sspNo, DSPResult result, String macro, String pltfomTpCode, String userId, String macroType) {
		String point = "0";			// 입찰 금액
		String krwPoint = "0";	// 입찰 원화 금액

		try {

			switch (result.getDspTpCode()) {
				case "04":
					Base64.Encoder encoder = Base64.getEncoder();
					point = encoder.encodeToString(String.valueOf(result.getPoint()).getBytes());
					break;
				case "99" :
					point = URLEncoder.encode(AES256Util.encrypt(String.valueOf(result.getPoint())), StandardCharsets.UTF_8);
					break;
				default:
					point = String.valueOf(result.getPoint());
			}

			krwPoint = URLEncoder.encode(AES256Util.encrypt(String.valueOf(result.getKrwPoint())), StandardCharsets.UTF_8);

		} catch (Exception e) {
			log.error("encode error {}", result.getPoint());
		}

		macro = macro.replace("${AUCTION_ID}", result.getRequestid());
		macro = macro.replace("${AUCTION_BID_ID}", result.getBidid());
		macro = macro.replace("${AUCTION_IMP_ID}", result.getImpid());
		macro = macro.replace("${AUCTION_SEAT_ID}", result.getSeat());
		macro = macro.replace("${AUCTION_AD_ID}", result.getAdid());
		macro = macro.replace("${AUCTION_PRICE}", point);
		macro = macro.replace("${AUCTION_PRICE:B64}", point);
		macro = macro.replace("${AUCTION_CURRENCY}", result.getCur());

		if (!"null".equals(result.getActionLoss()) && result.getActionLoss() != null) {
			macro = macro.replace("${AUCTION_LOSS}", result.getActionLoss());
		}

		macro = macro.replace("${TTX_USER_ID}", "");

		String clickUrl = SSPConstans.HTTPS + server +
			"/click?sspNo=" + sspNo +
			"&dspTpCode=" + result.getDspTpCode() +
			"&point=" + krwPoint +
			"&pltfomTpCode=" + pltfomTpCode +
			"&userId=" + userId + "&ad_url=";

		macro = macro.replace("${TTX_CLICK_URL}", clickUrl);


		if (macro.contains("${TTX_CLICK_URL:url")) {
			int idx = macro.indexOf("${TTX_CLICK_URL:url");
			int urlNo = Integer.parseInt(macro.substring(idx + 19, idx + 20));

			for (int i = 1; i <= urlNo; i++) {
				macro = macro.replace("${TTX_CLICK_URL:url" + urlNo + "}", URLEncoder.encode(clickUrl, StandardCharsets.UTF_8));
			}
		}

		if (macro.contains("${SSP_CLICK_URL:2")) {
			String clickUrl2 = URLEncoder.encode(clickUrl, StandardCharsets.UTF_8);
			macro = macro.replace("${SSP_CLICK_URL:2}", URLEncoder.encode(clickUrl2, StandardCharsets.UTF_8));
		}

		macro = macro.replace("%%CLICK_URL_ESC%%", URLEncoder.encode(clickUrl, StandardCharsets.UTF_8));
		macro = macro.replace("${CLICK_TRACKER}", clickUrl);
		macro = macro.replace("${CLICK_TRACK_URL}", clickUrl);
		macro = macro.replace("%%CLICK_URL_UNESC%%", clickUrl);
		macro = macro.replace("%%TTD_CLK%%", clickUrl);
		macro = macro.replace("${SSP_CLICK_URL}", clickUrl);
		macro = macro.replace("${CLICK_TRACKING_URL}", clickUrl);
		macro = macro.replace("${CLICK_TRACKING_URL_ENCODE}", URLEncoder.encode(clickUrl, StandardCharsets.UTF_8));
		macro = macro.replace("${CLICK_TRACKING_URL_ENCODE_ENCODE}",
			URLEncoder.encode(URLEncoder.encode(clickUrl, StandardCharsets.UTF_8),StandardCharsets.UTF_8));

		if(pltfomTpCode.equals(CodeConstans.PLTFOM_APP_TP_CODE) && macroType.equals(CodeConstans.MACRO_TYPE_ADM)){
			String sdkClick = SSPConstans.HTTPS + server +
					"/click?sspNo=" + sspNo +
					"&dspTpCode=" + result.getDspTpCode() +
					"&pltfomTpCode=" + CodeConstans.PLTFOM_SDK_APP_TP_CODE +
					"&userId=" + userId;

			macro += "<script type=\"text/javascript\">\n" +
					"\tlet sdkMixerClick ={\"chk\" : \"sdkMixerClick\",\n" +
					"\t\t                  \"clickSdkUrl\" : \" "+sdkClick+"\"};\n" +
					"  window.parent.parent.postMessage(sdkMixerClick, '*');\n" +
					" </script>";
		}

		return macro;
	}
}