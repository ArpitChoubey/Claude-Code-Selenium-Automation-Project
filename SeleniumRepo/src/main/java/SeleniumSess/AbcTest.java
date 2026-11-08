package SeleniumSess;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.WebDriver;

public class AbcTest {

	public static void main(String[] args) {
		
		String browserName = "chrome";
		
		SeleniumUtil Abc= new SeleniumUtil();
		
		Abc.initDriver(browserName);
		
		Abc.LaunchURL("https://www.amazon.com");
		
		String Bd = Abc.getPageTitle();
		if(Bd.contains("amazon")) {
			System.out.println("title--Pass");
		}
		else {
			System.out.println("title-----Fail");
		}
           
		Abc.CloseBrowser();
		
		WebDriver driver=  new ChromeDriver();
		driver.get("https://www.amazon.com");
		
		String Bc = Abc.getPageUrl();
		if(Bc.contains("amazon")) {
			System.out.println("URL--Pass");
		}
		else {
			System.out.println("URL-----Fail");
		}
		
		Abc.QuitBrowser();
	}
	         
                  
	
	
}

                  