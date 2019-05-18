package com.tl.job002.utils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.tl.job002.parse.JDGoodsParser4JsoupImpl;
import com.tl.job002.pojos.entity.JDGoodsCommentsEntriy;
import com.tl.job002.pojos.entity.JDGoodsEntriy;
import com.tl.job002.schedule.TaskScheduleManager;

public class WebPageDownloadUtil4ChromeDriver {
	private WebDriver webDriver;
	public static Logger logger = Logger
			.getLogger(WebPageDownloadUtil4ChromeDriver.class);
	public int temp = 1;

	public String download(String url) {
		String html = null;
		webDriver.get(url);
		String status = WebDriverUtil.getPagStatus(webDriver);
		if (status.equals("200")) {
			try {
				slideDown(webDriver);
				Thread.sleep(RandomNumberUtil.getRandomNumber());
				html = webDriver.getPageSource();
				logger.info(url + "采集完毕");
				return html;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			// 关闭上一个浏览器
			webDriver.quit();
			WebDriverUtil.service.stop();
			if (SystemConfigParas.is_setting_proxy.equals("true")) {
				while (temp == 1) {
					webDriver = WebDriverUtil.createWebDriver(true, true);
					logger.info(url + "切换模式1成功，这是第"
							+ TaskScheduleManager.getProxyIpPoolListSize()
							+ "次切换。");
					webDriver.get(url);
					status = WebDriverUtil.getPagStatus(webDriver);
					if (status.equals("200")) {
						try {
							slideDown(webDriver);
							Thread.sleep(RandomNumberUtil.getRandomNumber());
							html = webDriver.getPageSource();
							logger.info(url + "使用模式1采集完毕");
							break;
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						if (TaskScheduleManager.getProxyIpPoolListSize() == SystemConfigParas.proxy_ip_pool
								.size()) {
							logger.info(url + "使用模式1完毕，即将切换模式2");
							temp = 2;
							TaskScheduleManager.cleanProxyIpPoolList();
						}
					}
				}
			} else {
				temp = 2;
			}
			while (temp == 2) {
				webDriver = WebDriverUtil.createWebDriver(false, false);
				logger.info(url + "切换模式2成功，这是第"
						+ TaskScheduleManager.getUserNameListSize() + "次切换。");
				status = jdSimulationLogin();
				if (status.equals("200")) {
					webDriver.get(url);
					status = WebDriverUtil.getPagStatus(webDriver);
					if (status.equals("200)")) {
						try {
							slideDown(webDriver);
							Thread.sleep(RandomNumberUtil.getRandomNumber());
							html = webDriver.getPageSource();
							logger.info(url + "使用模式2采集完毕");
							break;
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						if (TaskScheduleManager.getUserNameListSize() == SystemConfigParas.login_user_name
								.size()) {
							logger.info(url + "使用模式2完毕，即将切换模式3");
							temp = 3;
							TaskScheduleManager.cleanUserNameList();
						}
					}
				} else {
					if (TaskScheduleManager.getUserNameListSize() == SystemConfigParas.login_user_name
							.size()) {
						logger.info(url + "所有登陆均不成功,即将切换模式3");
						temp = 3;
						TaskScheduleManager.cleanUserNameList();
					}
				}
			}
			if (SystemConfigParas.is_setting_proxy.equals("true")) {
				while (temp == 3) {
					isChangeUserName = false;
					if (TaskScheduleManager.getProxyIpPoolListSize() == SystemConfigParas.proxy_ip_pool
							.size()) {
						isChangeUserName = true;
						isFirstChange = false;
					}
					webDriver = WebDriverUtil.createWebDriver(true, false);
					logger.info(url + "切换模式3成功");
					status = jdSimulationLogin();
					if (status.equals("200")) {
						webDriver.get(url);
						status = WebDriverUtil.getPagStatus(webDriver);
						if (status.equals("200)")) {
							try {
								slideDown(webDriver);
								Thread.sleep(RandomNumberUtil.getRandomNumber());
								html = webDriver.getPageSource();
								logger.info(url + "使用模式3采集完毕");
								break;
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						} else {
							if (TaskScheduleManager.getUserNameListSize() == SystemConfigParas.login_user_name
									.size()
									&& TaskScheduleManager
											.getProxyIpPoolListSize() == SystemConfigParas.proxy_ip_pool
													.size()) {
								temp = 1;
								isChangeUserName = true;
								isFirstChange = true;
								webDriver = WebDriverUtil.createWebDriver(
										false, true);
								
								TaskScheduleManager.cleanUserNameList();
								TaskScheduleManager.cleanProxyIpPoolList();
							}
						}
					} else {
						logger.info(url + "模式3登录页状态值非200即将切换ip");
						if (TaskScheduleManager.getProxyIpPoolListSize() == SystemConfigParas.proxy_ip_pool
								.size()&&TaskScheduleManager.getUserNameListSize() == SystemConfigParas.login_user_name
										.size()) {
							logger.info(url + "在模式3中登录时切换了所有ip,即将退出下载");
							temp = 1;
							isChangeUserName = true;
							isFirstChange = true;
							webDriver = WebDriverUtil.createWebDriver(false,
									true);
							TaskScheduleManager.cleanProxyIpPoolList();
						}
					}
				}
			} else {
				logger.info("未设置代理模式");
				temp = 1;
			}
		}
		return html;
	}

	public static void slideDown(WebDriver webDriver) {

		try {
			Thread.sleep(5000);
			((JavascriptExecutor) webDriver)
					.executeScript("window.scrollTo(0,document.body.scrollHeight/2)");
			Thread.sleep(5000);
			((JavascriptExecutor) webDriver)
					.executeScript("window.scrollTo(document.body.scrollHeight/2,document.body.scrollHeight)");
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static int locationNum = 0;

	// 延时输入
	public static synchronized void delayInput(WebElement webElement, int flag,
			boolean isChangeUserName, boolean isFirstChange) {
		if (TaskScheduleManager.isNull4UserNameList()
				|| TaskScheduleManager.getUserNameListSize() == SystemConfigParas.login_user_name
						.size()) {
			locationNum = 0;
			TaskScheduleManager.cleanUserNameList();
		}
		String sendKey = null;
		switch (flag) {
		case 0:
			if (isChangeUserName) {
				sendKey = SystemConfigParas.login_user_name.get(locationNum);
				if (isFirstChange) {
					locationNum++;
					TaskScheduleManager.addUserNameList(sendKey);
				}
			} else {
				sendKey = SystemConfigParas.login_user_name.get(locationNum);
				if (TaskScheduleManager.getProxyIpPoolListSize() == SystemConfigParas.proxy_ip_pool
						.size()) {
					TaskScheduleManager.addUserNameList(sendKey);
					locationNum++;
				}
			}
			break;
		case 1:
			sendKey = SystemConfigParas.login_password.get(locationNum);
		default:
			break;
		}

		for (int i = 0; i < sendKey.length(); i++) {
			try {
				webElement.sendKeys("" + sendKey.charAt(i));
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean isChangeUserName = true;
	public boolean isFirstChange = true;

	// 登录
	public String jdSimulationLogin() {
		List<BufferedImage> localImageList = null;
		try {
			localImageList = SiderUtil.getLocalALLImage("pugins/images");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		webDriver.get(SystemConfigParas.jd_login_url);
		String status = WebDriverUtil.getPagStatus(webDriver);
		if (status.equals("200")) {
			webDriver
					.findElement(
							By.xpath("//*[@id=\"content\"]/div[2]/div[1]/div/div[3]/a"))
					.click();
			try {
				logger.info("选中账号登录----------即将休息1秒钟");
				Thread.sleep(1000);
				logger.info("休息结束-----------即将选中账号密码输入框");
				WebElement username = webDriver.findElement(By
						.xpath("//*[@id=\"loginname\"]"));
				WebElement password = webDriver.findElement(By
						.xpath("//*[@id=\"nloginpwd\"]"));
				WebElement submit = webDriver.findElement(By
						.xpath("//*[@id=\"loginsubmit\"]"));
				username.clear();
				delayInput(username, 0, isChangeUserName, isFirstChange);
				delayInput(password, 1, isChangeUserName, isFirstChange);
				submit.click();
				Thread.sleep(2000);
				if (webDriver.getTitle().equals(
						SystemConfigParas.jd_search_title)) {
					logger.info("账号"
							+ TaskScheduleManager.getLastListForUserNameList()
							+ "登陆成功");
				} else {
					// 选中滑块按钮
					By moveBtn = By
							.cssSelector(".JDJRV-slide-inner.JDJRV-slide-btn");// 滑动验证按钮
					SiderUtil.waitForLoad(webDriver, moveBtn);
					WebElement moveElemet = webDriver.findElement(moveBtn);
					int i = 0;
					while (i < 5) {
						try {
							// 得到带补全的缺口图片 京东的图片是base64编码的 所以有个base64的解码工具类还原图片
							BufferedImage gapImage = SiderUtil
									.getGapImage(webDriver);
							// 将图片与本地图片对比找到相似度最大的图片即为该图片的完整图片

							BufferedImage similarImage = SiderUtil
									.getSimilarImage(gapImage, localImageList);

							// 得到移动距离
							int distance = SiderUtil.getSliceOffset(gapImage,
									similarImage);
							// 由于下载的图片比网页图片格式要大 根据推算 图片的拉伸长度为0.7222倍
							distance = (int) (distance * 0.77222);
							System.out.println("得到移动距离" + distance * 0.77222);
							// 按照加速度移动图片
							SiderUtil.move(webDriver, moveElemet, distance);
							Thread.sleep(4000);
							if (webDriver.getTitle().equals(
									SystemConfigParas.jd_search_title)) {
								logger.info("账号"
										+ TaskScheduleManager
												.getLastListForUserNameList()
										+ "登陆成功");
								break;
							}
							if (i == 4) {
								status = "-1";
								logger.info("账号"
										+ TaskScheduleManager
												.getLastListForUserNameList()
										+ "登陆失败");
							}
							i++;
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				int sleepTime = RandomNumberUtil.getRandomNumber();
				logger.info("即将休息" + sleepTime / 1000 + "秒-----------");
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return status;

	}

	public static void main(String[] args) {
		WebPageDownloadUtil4ChromeDriver webPageDownloadUtil4ChromeDriver = new WebPageDownloadUtil4ChromeDriver();
		try {
			long start = System.currentTimeMillis();

			JDGoodsParser4JsoupImpl jd = new JDGoodsParser4JsoupImpl();
			// "https://search.jd.com/Search?keyword=手机&enc=utf-8&page=1";
			String url = "https://search.jd.com/Search?keyword=手机&enc=utf-8&qrst=1&rt=1&stop=1&vt=2&cid2=653&cid3=655&page=1&s=1&click=0";
			String commentUrl = "https://sclub.jd.com/comment/productPageComments.action?&productId=()&score=0&sortType=5&page=0&pageSize=10";
			webPageDownloadUtil4ChromeDriver.setWebDriver(WebDriverUtil
					.createWebDriver(false, true));
			webPageDownloadUtil4ChromeDriver.jdSimulationLogin();
			String htmlSource = webPageDownloadUtil4ChromeDriver.download(url);
			if (htmlSource == null) {

			}
			Map<String, JDGoodsEntriy> jdGoodsEntriyMap = jd
					.parserHtmlSource(htmlSource);
			int i = 0;
			for (Entry<String, JDGoodsEntriy> entry : jdGoodsEntriyMap
					.entrySet()) {
				htmlSource = webPageDownloadUtil4ChromeDriver
						.download(commentUrl.replace("()", entry.getValue()
								.getGoodsSKU()));
				htmlSource = JsoupUtil
						.getElementsBySelector(htmlSource, "body").text();
				List<JDGoodsCommentsEntriy> jdGoodsCommentsEntriyList = jd
						.parserHtmlSource(htmlSource, entry.getValue());
				IOUtil.writeFile(entry.getValue().toString(), "goods.txt", true);
				logger.info("写入商品信息成功");
				IOUtil.writeFile(jdGoodsCommentsEntriyList.toString(),
						"comment.txt", true);
				System.out.println("写入评论信息成功");
				int time = RandomNumberUtil.getRandomNumber();
				i++;
				logger.info("第" + i + "商品的评论也爬取完毕,即将休息" + time / 1000 + "秒");
				Thread.sleep(time);

			}
			long stop = System.currentTimeMillis();
			System.out.println("总共运行" + (stop - start) / 1000 + "秒");
		} catch (Exception e) {
			e.printStackTrace();
			webPageDownloadUtil4ChromeDriver.webDriver.quit();
		} finally {
			if (webPageDownloadUtil4ChromeDriver.webDriver != null) {
				webPageDownloadUtil4ChromeDriver.webDriver.quit();
			}
		}
	}

	public WebDriver getWebDriver() {
		return webDriver;
	}

	public void setWebDriver(WebDriver webDriver) {
		this.webDriver = webDriver;
	}
}
