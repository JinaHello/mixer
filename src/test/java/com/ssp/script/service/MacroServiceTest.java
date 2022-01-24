package com.ssp.script.service;

import com.ssp.domain.constans.CodeConstans;
import com.ssp.domain.dto.DSPResult;
import com.ssp.domain.util.AES256Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.function.Function;

public class MacroServiceTest {

  /**
   * 낙찰가 인코딩 테스트 시작
   *
   * 할일 : adm, nurl의 인코딩 종류 및 여부에 관한 정보는 MacroService가 아닌 Enum등에서 얻어야 한다. (새로운 DSP 정책이 있으면 수정 고려)
   */
  private final MacroService service = new MacroService();
  private final Base64.Encoder encoder = Base64.getEncoder();
  private final Function<Float, String> base64EncoderFn = (point) -> encoder.encodeToString(String.valueOf(point).getBytes());
  private final Function<Float, String> toStringFn = String::valueOf;
  private final Function<Float, String> aes256EncryptFn = (point) -> {
    try {
      return URLEncoder.encode(AES256Util.encrypt(String.valueOf(point)), StandardCharsets.UTF_8);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  };

  @Test
  public void 낙찰가가_모비온은_ADM은_BASE64로_NURL은_BASE64로_보여줘야한다() {
    testAuctionPriceAdmNurl(CodeConstans.DSP_CODE_TP_ID_MOBON, 1000, base64EncoderFn, base64EncoderFn);
    testAuctionPriceAdmNurl(CodeConstans.DSP_CODE_TP_ID_MOBON, 2000, base64EncoderFn, base64EncoderFn);
  }

  @Test
  public void 낙찰가가_기타99는_ADM은_AES256으로_NURL은_AES256으로_보여줘야한다() {
    testAuctionPriceAdmNurl(CodeConstans.DSP_CODE_TP_ID_ETC, 1000, aes256EncryptFn, aes256EncryptFn);
    testAuctionPriceAdmNurl(CodeConstans.DSP_CODE_TP_ID_ETC, 2000, aes256EncryptFn, aes256EncryptFn);
  }

  @Test
  public void 낙찰가가_나머지는_ADM은_그대로_NURL은_그대로_보여줘야한다() {
    testAuctionPriceAdmNurl(CodeConstans.DSP_CODE_TP_ID_WIDER, 1000, toStringFn, toStringFn);
    testAuctionPriceAdmNurl(CodeConstans.DSP_CODE_TP_ID_NASMEDIA, 1000, toStringFn, toStringFn);
    testAuctionPriceAdmNurl(CodeConstans.DSP_CODE_TP_ID_NASMEDIA, 2000, toStringFn, toStringFn);
    testAuctionPriceAdmNurl(CodeConstans.DSP_CODE_TP_ID_BIDENCE, 1000, toStringFn, toStringFn);
    testAuctionPriceAdmNurl(CodeConstans.DSP_CODE_TP_ID_BIDENCE, 2000, toStringFn, toStringFn);
  }

  private void testAuctionPriceAdmNurl(String dspCode, float point, Function<Float, String> admFn, Function<Float, String> nurlFn) {
    DSPResult dspResult = getDSPResult(dspCode, point);
    service.dspMacro(14, dspResult, "01", "");

    Assertions.assertEquals(admFn.apply(point), dspResult.getHtml());
    Assertions.assertEquals(nurlFn.apply(point), dspResult.getNurl());
  }

  private DSPResult getDSPResult(String dspTpCode, float point) {
    return DSPResult.builder()
      .dspTpCode(dspTpCode)
      .html("${AUCTION_PRICE}")
      .nurl("${AUCTION_PRICE}")
      .requestid("").bidid("").impid("").seat("").adid("").cur("")
      .point(point)
      .build();
  }
}
