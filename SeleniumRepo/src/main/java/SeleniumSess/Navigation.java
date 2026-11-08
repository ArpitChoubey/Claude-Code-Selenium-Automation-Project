package SeleniumSess;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class Navigation {

	public static void main(String[] args) {
	
		
		WebDriver driver=  new ChromeDriver();
		driver.get("https://www.google.co.in/");
		
		System.out.println(driver.getTitle());
		
		driver.navigate().to("https://www.flipkart.com/");
		
		System.out.println(driver.getTitle());
		
		driver.navigate().back();
		
		System.out.println(driver.getTitle());
		
		driver.manage().window().maximize();
		
		
		driver.navigate().forward();
		
		System.out.println(driver.getTitle());
		
		driver.navigate().refresh();
		
		driver.quit();
		

		// WebDriver driver1=  new ChromeDriver();
		// driver1.get("https://www.amazon.in/");
		
		// driver1.findElement(By.xpath("//a[contains(text(),'Amazon')]"));
		
		
		
		
		

	}

}
