package SeleniumSess;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;



public class SeleniumBasic {

	public static void main(String[] args) {
		
		System.setProperty("Webdriver.chrome.driver","C:\\Users\\Arpit\\Downloads\\chromedriver-win64\\chromedriver-win64.exe");
		
		
		// ChromeDriver driver = new ChromeDriver();// open browser
		
		// SearchContext driver = new ChromeDriver();
		
		// RemoteWebDriver Driver = new ChromeDriver();
		
		// WebDriver Driver=  new ChromeDriver();
		
		// WebDriver Driver=  new FirefoxDriver();
		
		String browser = "chrome";
		WebDriver driver = null;
		if(browser.equals("Edge")) {
			driver= new EdgeDriver();
		}
		else if(browser.equals("chrome")) {
			driver= new ChromeDriver();
		}
		else if(browser.equals("Firefox")) {
			driver= new FirefoxDriver() ;
		}
			else {
				System.out.println("Please pass the right browser");
			}
	
		
		// get URL
		driver.get("https://www.google.com");
		
		String Atitle = driver.getTitle();
		System.out.println("Page Title :" +Atitle);// get title
		
		// validation point
		if (Atitle.equals("Google")) {
			System.out.println("Pass");
		}
		else {
			System.out.println("FAil");
		
		}
		
		driver.quit();
		// driver.close();
		
		
		
		

	}


}

