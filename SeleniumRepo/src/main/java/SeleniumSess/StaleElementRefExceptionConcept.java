package SeleniumSess;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class StaleElementRefExceptionConcept {

	public static void main(String[] args) {
		
		// refresh,back,forward and click-StaleElementRefException
		
		
		WebDriver driver=  new ChromeDriver();
		driver.get("https://naveenautomationlabs.com/opencart/index.php?route=account/login");
		
		WebElement fn= driver.findElement(By.id("input-email"));
		fn.sendKeys("choubeyarpit963@gmail.com");
		
		driver.navigate().refresh();
		
		fn= driver.findElement(By.id("input-email"));// solution- create the web element after refresh the page 
		
		fn.sendKeys("choarpit963@gmail.com");
		
		

	}

}
