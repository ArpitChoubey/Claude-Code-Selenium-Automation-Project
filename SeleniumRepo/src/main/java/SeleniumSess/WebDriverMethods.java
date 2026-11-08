package SeleniumSess;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
public class WebDriverMethods {

	public static void main(String[] args) {
		
		System.setProperty("Webdriver.chrome.driver","C:\\Users\\Arpit\\Downloads\\chromedriver-win64\\chromedriver-win64.exe");
		
		
		WebDriver driver=  new ChromeDriver();
		driver.get("https://www.amazon.com");
		
		driver.manage().window().maximize();
		// driver.manage().window().fullscreen();
		driver.manage().deleteAllCookies();
		String url = driver.getCurrentUrl();
		System.out.println(url);
		if(url.contains("amazon.com")) {
			System.out.println("PASS");
		}
		
		String Pg= driver.getPageSource();
		System.out.println(Pg);
		
		driver.quit();
		
		
		

	}

}
