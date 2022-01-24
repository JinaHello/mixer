package com.ssp.script.service;

import com.ssp.script.config.LastMomentConfig;
import com.ssp.script.constants.LastMomentHtmlConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LastMomentServiceTest {

  @Test
  public void 최후의순간_WEB에서는_옷잘남_광고가_나간다() {
    String width = "300";
    String height = "250";

    String expected = LastMomentHtmlConstants.WEB_OTJALNAM_HTML;
    expected = expected.replace("{{w}}", width);
    expected = expected.replace("{{h}}", height);

    testMakeLastMomentHtml(width, height, false, expected);
  }

  @Test
  public void 최후의순간_APP에서는_FAILCALLBACK_스크립트를_실행한다() {
    String width = "300";
    String height = "250";
    String expected = LastMomentHtmlConstants.APP_MOBONSDK_FAILCALLBACK_SCRIPT;
    testMakeLastMomentHtml(width, height, true, expected);
  }

  private void testMakeLastMomentHtml(String width, String height, boolean isApp, String expected) {
    LastMomentService lastMomentService = new LastMomentService(false, new LastMomentConfig());
    String result = lastMomentService.makeLastMomentHtml(width, height, isApp);
    Assertions.assertEquals(expected, result);
  }
}
