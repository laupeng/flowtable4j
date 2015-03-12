package com.ctrip.flowtable.core.common;


import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author weiyu
 * @date 2015年3月12日
 */
public class CommonUtils {
	@Autowired
	private Properties appProperties;

	public String getAppId() {
		return appProperties.getProperty("APPID");
	}

	/*
	 * http://localhost:8080/userprofile-webservice
	 */
	public String getUserProfileRestfulWS() {
		return appProperties.getProperty("USERPROFILERESTFUL");
	}

	public String getCRedisServiceUrl() {
		return appProperties.getProperty("CRedisServiceUrl");
	}

	public String getLoggingServerIP() {
		return appProperties.getProperty("LoggingServerIP");
	}

	public String getLoggingServerPort() {
		return appProperties.getProperty("LoggingServerPort");
	}

	public String getRabbitmqHost() {
		return appProperties.getProperty("RabbitmqHost");
	}

	public String getRabbitmqVirtualHost() {
		return appProperties.getProperty("RabbitmqVirtualHost");
	}

	static String eSBUrl = null;

	public String getESBUrl() {
		if (eSBUrl == null || eSBUrl.isEmpty()) {
			eSBUrl = appProperties.getProperty("ESBUrl");
		}
		return eSBUrl;
	}

	static String hostIP = null;

	public String getHostIP() {
		if (hostIP == null || hostIP.isEmpty()) {
			hostIP = getLocalIP();
		}
		return hostIP;
	}

	public String getProxyHost() {
		String proxyHost = "";
		try {
			proxyHost = appProperties.getProperty("ProxyHost");
		} catch (Exception e) {

		}

		return proxyHost;
	}

	public int getProxyPort() {
		int ProxyPort = 8080;
		try {
			String t = appProperties.getProperty("ProxyPort");
			ProxyPort = org.apache.commons.lang.math.NumberUtils.toInt(t, 8080);
		} catch (Exception e) {

		}

		return ProxyPort;
	}

	/**
	 * 
	 * 判斷當前操作是否Windows.
	 * 
	 * 
	 * 
	 * @return true---是Windows操作系統
	 */

	public static boolean isWindowsOS() {

		boolean isWindowsOS = false;
		String osName = System.getProperty("os.name");
		if (osName.toLowerCase().indexOf("windows") > -1) {
			isWindowsOS = true;
		}
		return isWindowsOS;
	}

	/**
	 * 
	 * 獲取本機IP地址，並自動區分Windows還是Linux操作系統
	 * 
	 * 
	 * 
	 * @return String
	 */

	private static String getLocalIP() {
		String sIP = "";
		InetAddress ip = null;
		try {
			// 如果是Windows操作系統
			if (isWindowsOS()) {
				ip = InetAddress.getLocalHost();
			}
			// 如果是Linux操作系統
			else {
				boolean bFindIP = false;
				Enumeration<NetworkInterface> netInterfaces = (Enumeration<NetworkInterface>) NetworkInterface.getNetworkInterfaces();

				while (netInterfaces.hasMoreElements()) {
					if (bFindIP) {
						break;
					}

					NetworkInterface ni = (NetworkInterface) netInterfaces.nextElement();

					// ----------特定情況，可以考慮用ni.getName判斷
					// 遍歷所有ip
					Enumeration<InetAddress> ips = ni.getInetAddresses();
					while (ips.hasMoreElements()) {
						ip = (InetAddress) ips.nextElement();
						if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress() // 127.開頭的都是lookback地址
								&& ip.getHostAddress().indexOf(":") == -1) {
							bFindIP = true;

							break;

						}

					}

				}

			}

		} catch (Exception e) {

			e.printStackTrace();

		}

		if (null != ip) {

			sIP = ip.getHostAddress();

		}

		return sIP;

	}

}
