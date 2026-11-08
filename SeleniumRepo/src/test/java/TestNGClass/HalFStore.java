package TestNGClass;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class HalFStore {
	
	WebDriver driver;
	@BeforeMethod
	public void setup() {
		driver = DriverFactory.createDriver();
		driver.get("https://naveenautomationlabs.com/opencart/index.php?route=account/register");
		driver.manage().deleteAllCookies();
		driver.manage().window().maximize();
		
	}
	
	@DataProvider
	public Object[][] getTestdata() {
		return new Object[][] {

			{"Aman","Singh"},
			{"Amanl","Singuh"},
			{"Amany","Singht"},
			{"Amanh","Singhi"},
			
			
			
			
		};
	}
	
	@Test(dataProvider = "getTestdata")
	public void RegPageTest(String Firstname,String lastName) {
		driver.findElement(By.id("input-firstname")).sendKeys(Firstname);
		driver.findElement(By.id("input-lastname")).sendKeys(lastName);
		
		
	}
	
	@AfterMethod
	public void Teardown() {
		driver.quit();
		
	}
	

}
