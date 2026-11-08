package SeleniumSess;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.safari.SafariDriver;

public class SeleniumUtil {
	
	private WebDriver driver;
	/**
	 * This method is used to initailize the driver on the basic of given browser Name
	 * @param browserName
	 * @return This returns the specific browser
	 */
	public WebDriver initDriver(String browserName) {
		System.out.println("Browser Name is :"  + browserName);
		switch(browserName.toLowerCase().trim()) {
		case "chrome":
			driver=  new ChromeDriver();
			break;
		case "firefox":
			driver=  new FirefoxDriver();
			break;
		case "safari":
			driver=  new SafariDriver();
			break;
		case "edge":
			driver=  new EdgeDriver();
			break;
			default:
				System.out.println("Please pass the right browser:" + browserName);
			break;
		}
		return driver;
	}
		
		// 
                   	public void LaunchURL (String Url) {
			if (Url==null) {
				System.out.println("Url can not be null");
				return;
			}
             
                   	
	      	if(Url.indexOf("http")==0) {
				driver.get(Url);
			}
                   	}
			
			public String getPageTitle() {
				String title = driver.getTitle();
				System.out.println("page Title :" +title);
				return title;
			}
		
			public  String getPageUrl() {
				String Url = driver.getCurrentUrl();
				System.out.println("page Current Url:" + Url);
				return Url;
			}
		
	
	
	
	public void CloseBrowser() {
		 if(driver!=null) {
			 driver.close();
		 }
		 }
		 
	 public void QuitBrowser() {
		 if(driver!=null) {
			 driver.quit();
			 }
	 }
	 
	}
	
	

