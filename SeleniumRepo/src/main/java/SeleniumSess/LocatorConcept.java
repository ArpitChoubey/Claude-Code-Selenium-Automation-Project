package SeleniumSess;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
public class LocatorConcept {

	public static void main(String[] args) {
		
		WebDriver Driver=  new ChromeDriver();
		Driver.get("https://naveenautomationlabs.com/opencart/index.php?route=account/login");
		
	//	Driver.findElement(By.id("input-email")).sendKeys("naveen@gmail.com");
	//	Driver.findElement(By.id("input-password")).sendKeys("naveen@123");
		
		WebElement emailID = Driver.findElement(By.id("input-email"));
		WebElement pwd = Driver.findElement(By.id("input-password"));
		
		
		emailID.sendKeys("naveen@gmail.com");
		pwd.sendKeys("naveen@123");
		
		
		
		

	}

}
