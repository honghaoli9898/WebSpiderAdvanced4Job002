package com.tl.job002.utils;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.apache.log4j.Logger;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Platform;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.tl.job002.schedule.TaskScheduleManager;

public class WebDriverUtil {
	public static ChromeDriverService service;
	public static Logger logger = Logger.getLogger(WebDriverUtil.class);

	/**
	 * 创建phantomjs
	 * 
	 * @param path
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	public static WebDriver createPhantomjsWebDriver(String path)
			throws Exception {
		DesiredCapabilities capabilities = DesiredCapabilities.phantomjs();
		if (path == null || "".equals(path)) {
			throw new Exception("配置错误，没有配置：phantomjs.binary.path");
		}
		capabilities.setCapability("phantomjs.binary.path", path);
		capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		capabilities.setJavascriptEnabled(true);
		capabilities.setCapability("takesScreenshot", true);
		capabilities
				.setBrowserName("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3679.0 Safari/537.36");
		capabilities.setPlatform(Platform.WIN10);
		WebDriver webDriver = null;
		try {
			webDriver = new PhantomJSDriver(capabilities);
		} catch (RuntimeException e) {
			throw new Exception("创建webdriver失败");
		}
		if (webDriver == null) {
			throw new Exception("创建webdriver失败");
		}
		webDriver.manage().timeouts().pageLoadTimeout(5000, TimeUnit.SECONDS);
		webDriver.manage().window().setSize(new Dimension(1920, 1080));
		return webDriver;
	}

	/**
	 * 创建Chrome
	 * 
	 * @throws Exception
	 */

	public static int locationNum = 0;

	public static WebDriver createChromeWebDriver(String path,
			boolean isUseProxy) throws Exception {
		if (path == null || "".equals(path)) {
			throw new Exception("配置错误，没有配置：chrome path");
		}
		service = new ChromeDriverService.Builder()
				.usingDriverExecutable(new File(path)).usingAnyFreePort()
				.build();
		try {
			service.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		DesiredCapabilities cap = new DesiredCapabilities();
		if (SystemConfigParas.is_setting_proxy.equals("true")
				&& isUseProxy == true) {
			if (TaskScheduleManager.isNull4ProxyIpPoolList()
					|| TaskScheduleManager.getProxyIpPoolListSize() == SystemConfigParas.proxy_ip_pool
							.size()) {
				locationNum = 0;
				TaskScheduleManager.cleanProxyIpPoolList();
			}
			String proxy = SystemConfigParas.proxy_ip_pool.get(locationNum);
			logger.info("正在切换代理ip:" + proxy);
			setProxy(cap, proxy);
			TaskScheduleManager.addProxyIpPoolList(proxy);
			locationNum++;
		}
		setLog(cap);
		ChromeOptions options = new ChromeOptions();
		setOptionsArguments(SystemConfigParas.options_arguments, options);
		// options.addArguments("--proxy-server=http://116.209.56.169:9999");
		cap.setCapability(ChromeOptions.CAPABILITY, options);

		System.getProperties().setProperty("webdriver.chrome.driver", path);
		WebDriver webDriver = new ChromeDriver(cap);
		webDriver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		webDriver.manage().timeouts().pageLoadTimeout(1200, TimeUnit.SECONDS);
		webDriver.manage().window().setSize(new Dimension(1920, 1080));
		
		return webDriver;
	}

	public static String getPagStatus(WebDriver webDriver) {
		String currentURL = webDriver.getCurrentUrl();
		LogEntries logs = webDriver.manage().logs().get("performance");
		int status = -1;
		for (Iterator<LogEntry> it = logs.iterator(); it.hasNext();) {
			LogEntry entry = it.next();
			try {
				JSONObject json = JSON.parseObject(entry.getMessage());
				JSONObject message = json.getJSONObject("message");
				String method = message.getString("method");
				if (method != null && "Network.responseReceived".equals(method)) {
					JSONObject params = message.getJSONObject("params");
					JSONObject response = params.getJSONObject("response");
					String messageUrl = response.getString("url");
					if (currentURL.equals(messageUrl)) {
						status = response.getInteger("status");
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return String.valueOf(status);
	}

	/**
	 * 创建Firefox
	 */
	public static WebDriver createFirefoxWebDriver(String path)
			throws Exception {
		if (path == null || "".equals(path)) {
			throw new Exception("配置错误, 没有配置 gecko path");
		}
		System.setProperty("webdriver.gecko.driver", path);
		WebDriver webDriver = new FirefoxDriver();
		return webDriver;
	}

	/**
	 * 创建chromedriver实例
	 */
	public static WebDriver createWebDriver(boolean isUseProxy) {
		WebDriver webDriver = null;
		try {
			webDriver = WebDriverUtil.createChromeWebDriver(
					SystemConfigParas.chrome_driver_path, isUseProxy);
		} catch (Exception e) {
			webDriver.quit();
			e.printStackTrace();
		}
		return webDriver;
	}

	public static void setOptionsArguments(List<String> argsList,
			ChromeOptions options) {
		for (String argument : argsList) {
			options.addArguments(argument);
		}
	}

	// 设置代理
	public static void setProxy(DesiredCapabilities cap, String proxyIpAndPort) {
		Proxy proxy = new Proxy();
		proxy.setHttpProxy(proxyIpAndPort).setFtpProxy(proxyIpAndPort)
				.setSslProxy(proxyIpAndPort);
		cap.setCapability(CapabilityType.ForSeleniumServer.AVOIDING_PROXY, true);
		cap.setCapability(
				CapabilityType.ForSeleniumServer.ONLY_PROXYING_SELENIUM_TRAFFIC,
				true);
		System.setProperty("http.nonProxyHosts", "localhost");
		cap.setCapability(CapabilityType.PROXY, proxy);
	}

	// 设置日志服务
	public static void setLog(DesiredCapabilities cap) {
		LoggingPreferences logPrefs = new LoggingPreferences();
		logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
		cap.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
	}

	// 设置socket代理服务器
	public static void setSocketProxy() {
		System.setProperty("http.proxySet", "true");
		System.getProperties().setProperty("http.proxyHost", "127.0.0.1");
		System.getProperties().setProperty("http.proxyPort", "80");
	}
}
