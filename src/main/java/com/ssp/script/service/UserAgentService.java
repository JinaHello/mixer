
package com.ssp.script.service;

import com.ssp.domain.vo.UserAgentVO;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class UserAgentService {

	public UserAgentVO userAgentMake(String userAgent) {
		return UserAgentVO.builder().deviceType(userAgentDevicetype(userAgent))
			.os(userAgentOs(userAgent))
			.osv(userAgentOsv(userAgent)).build();
	}

	public String userAgentOs(String userAgent) {
		if (userAgent.contains("windows nt 10.0")) return "Windows10";
		else if (userAgent.contains("windows nt 6.1")) return "Windows7";
		else if (userAgent.contains("windows nt 6.2") || userAgent.contains("windows nt 6.3")) return "Windows8";
		else if (userAgent.contains("windows nt 6.0")) return "WindowsVista";
		else if (userAgent.contains("windows nt 5.1")) return "WindowsXP";
		else if (userAgent.contains("windows nt 5.0")) return "Windows2000";
		else if (userAgent.contains("windows nt 4.0")) return "WindowsNT";
		else if (userAgent.contains("windows 98")) return "Windows98";
		else if (userAgent.contains("windows 95")) return "Windows95";
		else if (userAgent.contains("iphone")) return "ios";
		else if (userAgent.contains("ipad")) return "ios";
		else if (userAgent.contains("android")) return "android";
		else if (userAgent.contains("mac")) return "ios";
		else if (userAgent.contains("linux")) return "Linux";
		return "etc";
	}

	public String userAgentOsv(String userAgent) {
		if (userAgent.contains("windows")) {
			if (userAgent.contains("windows nt 10.0")) return "10.0";
			else if (userAgent.contains("windows nt 6.1")) return "6.1";
			else if (userAgent.contains("windows nt 6.2")) return "6.2";
			else if (userAgent.contains("windows nt 6.3")) return "6.3";
			else if (userAgent.contains("windows nt 6.0")) return "6.0";
			else if (userAgent.contains("windows nt 5.1")) return "5.1";
			else if (userAgent.contains("windows nt 5.0")) return "5.0";
			else if (userAgent.contains("windows nt 4.0")) return "4.0";
			else if (userAgent.contains("wow64")) return "WOW64";
			else if (userAgent.contains("win64;x64;")) return "Win64 on x64";
			else if (userAgent.contains("win16")) return "16-bit";
			else return "etc";
		} else if (userAgent.contains("linux")) {
			if (userAgent.contains("x86_64")) return "x86_64";
			else if (userAgent.contains("i686")) return "i686";
			else if (userAgent.contains("i686 on x86_64")) return "i686 running on x86_64";
			else if (userAgent.contains("armv7l")) return "Nokia N900 Linux mobile, on the Fennec browser";
			else if (userAgent.contains("ia-32")) return "32-bit";
			else return "etc";
		} else if (userAgent.contains("mac")) {
			if (userAgent.contains("intel")) return "Intel x86 or x86_64";
			else if (userAgent.contains("ppc")) return "PowerPC";
			else return "etc";
		}
		return "";
	}

	public String userAgentDevicetype(String agent) {
		if (agent.contains("windows")) {
			if (agent.contains("mobile")) {
				return "1";
			} else {
				return "2";
			}
		}
		else if (agent.contains("iphone") || agent.contains("android")) return "4";
		else if (agent.contains("ipad")) return "1";
		else if (agent.contains("mac") || agent.contains("linux")) return "2";
		else return "2";
	}
}