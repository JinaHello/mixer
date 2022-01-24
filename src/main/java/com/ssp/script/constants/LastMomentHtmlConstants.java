package com.ssp.script.constants;

public class LastMomentHtmlConstants {
  public static final String WEB_OTJALNAM_HTML = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n" +
    "<html lang=\"en\" xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
    "<head>\n" +
    "\t<meta http-equiv=\"content-type\" content=\"text/html;charset=UTF-8\">\n" +
    "\t<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
    "\t<meta name=\"viewport\" content=\"width=device-width, user-scalable=no, initial-scale=1, maximum-scale=1\">\n" +
    "\t<title>mobon guide</title>\n" +
    "\t<link rel=\"stylesheet\" type=\"text/css\" href=\"https://img.mobon.net/Frtb/common/css/mobon_reset.css?201706292\">\n" +
    "\t<style type=\"text/css\">\n" +
    "\t\thtml,body{height:100%;overflow:hidden;}\n" +
    "\t\t*,body{box-sizing:border-box;margin:0;padding:0;}\n" +
    "\t\t#app{position:relative;background:#fff;width:100%;overflow:hidden;margin:0 auto;}\n" +
    "        .cell{position:relative;float:left;background:#fff;display:block;overflow:hidden;cursor:pointer;padding:2px;}\n" +
    "\t\t.shop_logo{background-image:url(\"https://www.mediacategory.com/mediaCategory/ad/mobon_user_branch1.html\");background-size:contain;background-repeat:no-repeat;background-position:center center;width:100%;}\n" +
    "\t\t.sale_box p{position:absolute;left:50%;width:100px;margin-left:-50px;}\n" +
    "\t\t.dc_box div{position:absolute;top:5px;left:5px;background-color:rgba(255,0,10,.5);width:35px;height:35px;line-height:35px;text-align:center;color:#fff;font-size:12px;z-index:10;font-weight:600;border-radius:50%;}\n" +
    "        .inner-box{position:absolute;width:100%;left:50%;top:50%;transform:translate(-50%,-50%);display:inline-block;border:5px solid #fff;font-size:1.5rem;color:#fff;padding:10px;font-weight: bold;text-align: center;word-break:keep-all;font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;}\n" +
    "\t\t.inner-box small{display:block;word-break:keep-all;padding: 5px 0;font-weight:normal}\n" +
    "\t</style>\n" +
    "</head>\n" +
    "<body>\n" +
    "\t<div id=\"app\">\n" +
    "\t\t<div id=\"mobonLogo\" onclick=\"window.open('https://img.mobon.net/ad/linfo.php')\"></div>\n" +
    "\t\t<div class=\"shop_logo\"></div>\n" +
    "\t\t<div id=\"slider\">\n" +
    "\t\t\t<ul class=\"slide-ul\"></ul>\n" +
    "\t\t</div>\n" +
    "\t</div>\n" +
    "</body>\n" +
    "<script type=\"text/javascript\" src=\"https://img.mobon.net/Frtb/common/script/jquery.js\"></script>\n" +
    "<script type=\"text/javascript\">\n" +
    "\tvar noimage = false;\n" +
    "\t$(document).ready(function(){\n" +
    "\t\timgFail({{w}},{{h}});\n" +
    "\t});\n" +
    "\tfunction imgFail(framewidth,frameheight) {\n" +
    "\t\t$('.slide-ul').empty();\n" +
    "\t\tif (frameheight <= 150 && framewidth < 500) {\n" +
    "            $('.shop_logo').css({ 'float': 'left', 'width': '30%', 'height': frameheight + 'px', 'margin': '0 auto',\"background-image\":\"url('https://img.mobon.net/servlet/image/otjalnam/otjalnam_logo.png')\",\"background-size\":\"90%\",\"background-repeat\":\"no-repeat\",\"background-position\":\"center center\"});\n" +
    "            $('#slider').css({'float': 'left','width': '70%'});\n" +
    "            $('#app').css({\"background-image\":\"url('https://img.mobon.net/servlet/image/otjalnam/otjalnam_02.png')\",\"background-size\":\"cover\",\"background-repeat\":\"no-repeat\",\"background-position\":\"center center\"});\n" +
    "            $('.slide-ul').append(\"<div class='cell' style='width:80%;height:\" + frameheight + \"px;background:transparent' onclick='openOtjalnam()'>\"+\n" +
    "            \"<div class='inner-box' style='font-size:1rem;padding:5px 0'><small style='font-size:.8rem;padding:0'>어떤옷을 사야할까</small>새옷살때 고민되면 <span style='font-size:14px;'>맞춤추천 쇼핑앱</span></div>\"+\n" +
    "            \"</div>\");\n" +
    "        } else if (frameheight < 150 && framewidth > 500) {\n" +
    "            $('.shop_logo').css({ 'float': 'left', 'width': (framewidth/4) + (framewidth !== 100 ? 'px' : '%'), 'height': frameheight + 'px', 'margin': '0 auto',\"background-image\":\"url('https://img.mobon.net/servlet/image/otjalnam/otjalnam_logo.png')\",\"background-size\":\"30%\",\"background-repeat\":\"no-repeat\",\"background-position\":\"center center\"});\n" +
    "            $('#slider').css({'float': 'left','width': ((framewidth/4)*3)+(framewidth !== 100 ? 'px' : '%')});\n" +
    "            $('#app').css({\"background-image\":\"url('https://img.mobon.net/servlet/image/otjalnam/otjalnam_02.png')\",\"background-size\":\"cover\",\"background-repeat\":\"no-repeat\",\"background-position\":\"center center\"});\n" +
    "            $('.slide-ul').append(\"<div class='cell' style='width:80%;height:\"+frameheight+\"px;background:transparent' onclick='openOtjalnam()'>\"+\n" +
    "            \"<div class='inner-box'><small style='font-size:1rem;padding: 0'>어떤옷을 사야할까</small>새옷살때 고민되면 <span>맞춤추천 쇼핑앱</span></div>\"+\n" +
    "            \"</div>\");\n" +
    "        } else if (frameheight < 250 && framewidth < 200) {\n" +
    "            $('.shop_logo').css({ 'height':'100px',\"background-image\":\"url('https://img.mobon.net/servlet/image/otjalnam/otjalnam_logo.png')\",\"background-size\":\"50%\",\"background-repeat\":\"no-repeat\",\"background-position\":\"center center\"});\n" +
    "            $('#app').css({\"background-image\":\"url('https://img.mobon.net/servlet/image/otjalnam/otjalnam_01.png')\",\"background-size\":\"cover\",\"background-repeat\":\"no-repeat\",\"background-position\":\"center center\"});\n" +
    "            $('.slide-ul').append(\"<div class='cell' style='width:100%;height:\"+(frameheight-100)+\"px;background:transparent' onclick='openOtjalnam()'>\"+\n" +
    "            \"<div class='inner-box' style='width:80%;margin:0 auto;font-size:.8rem'><small style='font-size:.5rem'>어떤옷을 사야할까</small>새옷살때 고민되면 <span style='display:block;padding:0;font-size:.5rem;'>맞춤추천 쇼핑앱</span></div>\"+\n" +
    "            \"</div>\");\n" +
    "        } else {\n" +
    "            $('.shop_logo').css({ 'height':'100px',\"background-image\":\"url('https://img.mobon.net/servlet/image/otjalnam/otjalnam_logo.png')\",\"background-size\":\"50%\",\"background-repeat\":\"no-repeat\",\"background-position\":\"center center\"});\n" +
    "            $('#app').css({\"background-image\":\"url('https://img.mobon.net/servlet/image/otjalnam/otjalnam_01.png')\",\"background-size\":\"cover\",\"background-repeat\":\"no-repeat\",\"background-position\":\"center center\"});\n" +
    "            $('.slide-ul').append(\"<div class='cell' style='width:100%;height:\"+(frameheight-100)+\"px;background:transparent' onclick='openOtjalnam()'>\"+\n" +
    "            \"<div class='inner-box' style='width:80%;margin:0 auto;'><small style='font-size:.8rem'>어떤옷을 사야할까</small>새옷살때 고민되면 <span style='display:block;padding:5px 0;font-size:1rem;'>맞춤추천 쇼핑앱</span></div>\"+\n" +
    "            \"</div>\");\n" +
    "        }\n" +
    "\t}\n" +
    "\tfunction openOtjalnam() {\n" +
    "\t\twindow.open('https://ref.ad-brix.com/v1/referrallink?ak=576324275&ck=1535237');\n" +
    "\t}\n" +
    "</script>\n" +
    "</html>\n";

  public static String APP_MOBONSDK_FAILCALLBACK_SCRIPT = "<!DOCTYPE html><html><body><script>window.mobonSDK.FailCallback();</script></body></html>";
}
