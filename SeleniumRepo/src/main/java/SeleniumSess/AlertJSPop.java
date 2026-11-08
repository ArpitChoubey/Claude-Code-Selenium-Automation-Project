package SeleniumSess;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class AlertJSPop {

	public static void main(String[] args) throws InterruptedException {
		
		
		WebDriver driver=  new ChromeDriver();
		driver.get("https://the-internet.herokuapp.com/javascript_alerts");
		
		// driver.findElement(By.xpath("//button[text()='Click for JS Alert']")).click();
		
		// driver.findElement(By.xpath("//button[text()='Click for JS Confirm']")).click();
		
		driver.findElement(By.xpath("//button[text()='Click for JS Prompt']")).click();
		
		Thread.sleep(4000);
		
		Alert alert = driver.switchTo().alert();
		
		//String text = alert.getText();
		//System.out.println(text);
		
		alert.sendKeys("Jio Dhan Dhan");
		
		Thread.sleep(4000);
		
		alert.accept();
		
		// alert.dismiss();
		
		

	}

}
