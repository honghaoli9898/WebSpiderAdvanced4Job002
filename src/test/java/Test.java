import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.WebDriver.Navigation;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;

public class Test {

	public static void main(String[] args) throws InterruptedException,
			AWTException {

		String URL = "https://www.autoitscript.com";
		// avoid Chrome warnning message like
		// "unsupported command-line flag --ignore-certificate-errors. "
		ChromeOptions options = new ChromeOptions();

		options.addArguments("--test-type");

		System.setProperty("webdriver.chrome.driver",
				"plugins/chromedriver/chromedriver.exe");
		WebDriver driver = new ChromeDriver(options);
		// WebDriver driver = new FirefoxDriver();

		driver.manage().window().maximize();// 这是页面最大化

		driver.get(URL);
		driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS); // 这是设置的网页超时时间
		WebElement editor = driver.findElement(By
				.xpath("//*[@id='menu-item-207']"));
		Actions actions = new Actions(driver);
		actions.moveToElement(editor).perform();
		// locate download link
		WebElement d = driver.findElement(By
				.xpath("//*[@id='menu-item-209']/a"));
		d.click();

		Thread.sleep(1000);
		// right click the download link

		// locate download link

		// right click the download
		// link//*[@id="post-77"]/div/table[2]/tbody/tr[1]/td[2]/a/img
		WebElement download = driver
				.findElement(By
						.xpath("//*[@id=\"post-77\"]/div/table[2]/tbody/tr[1]/td[2]/a/img"));// *[@id="content-area"]/div/table/tbody/tr[1]/td[2]/p/a/img
		JavascriptExecutor js = (JavascriptExecutor) driver;
		// roll down and keep the element to the center of browser
		js.executeScript("arguments[0].scrollIntoView(true);", download);
		actions.moveToElement(download).contextClick().build().perform();
		Robot robot = new Robot();

		// This will bring the selection down one by one

		robot.keyPress(KeyEvent.VK_DOWN);

		Thread.sleep(300);

		robot.keyPress(KeyEvent.VK_DOWN);

		Thread.sleep(300);

		robot.keyPress(KeyEvent.VK_DOWN);

		Thread.sleep(300);

		robot.keyPress(KeyEvent.VK_DOWN);

		robot.keyPress(KeyEvent.VK_DOWN);

		Thread.sleep(300);
		robot.keyPress(KeyEvent.VK_DOWN);

		Thread.sleep(300);
		robot.keyPress(KeyEvent.VK_DOWN);

		Thread.sleep(300);
		robot.keyRelease(KeyEvent.VK_DOWN);

		Thread.sleep(300); // 一共是7个这是7个下
		robot.keyRelease(KeyEvent.VK_DOWN);

		Thread.sleep(1000); // 一共是7个这是7个下

		robot.keyPress(KeyEvent.VK_ENTER);// 按下确定 然后去执行那个exe文件
		Thread.sleep(1000);

		// call autoIt to save the file
		try {
			Runtime.getRuntime().exec("plugins\\test.exe");
		} catch (IOException e) {
			e.printStackTrace();
		}

		Thread.sleep(15000);
		driver.quit();

	}

}