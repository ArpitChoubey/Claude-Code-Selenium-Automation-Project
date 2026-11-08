package SeleniumSess;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class GetTextForText {

	public static void main(String[] args) {
		
		
		WebDriver driver=  new ChromeDriver();
		driver.get("https://naveenautomationlabs.com/opencart/index.php?route=account/login");
		
		WebElement fn= driver.findElement(By.id("input-email"));
		fn.sendKeys("ArpitChoubey");
		
		String name = fn.getAttribute("value");
		System.out.println(name);

	}

}
