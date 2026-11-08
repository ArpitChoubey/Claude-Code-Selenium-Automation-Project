package SeleniumSess;

import java.util.List;

import javax.lang.model.element.Element;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

public class DropDown {

	public static void main(String[] args) throws InterruptedException {
		
		WebDriver driver=  new ChromeDriver();
		driver.get("https://www.globalsqa.com/demo-site/select-dropdown-menu/");
		
		WebElement country_Ele= driver.findElement(By.xpath("//*[@id=\"post-2646\"]/div[2]/div/div/div/p/select"));
		Select select = new Select(country_Ele);
		select.selectByIndex(6);
		Thread.sleep(2000);
	      select.selectByValue("GMB");
	      Thread.sleep(3000);
		 select.selectByVisibleText("Mexico");
		 
		 
		 
	         List<WebElement>	countryL =  select.getOptions();
	         System.out.println(countryL.size());
	         
	         for(WebElement e : countryL ) {
	        	String Text = e.getText();
	        	System.out.println(Text);
	        	if(Text.equals("Zimbabwe")) {
	        		e.click();
	        		break;
	        	}
	         }
		        
	         driver.close();
		
		
		

	}

}
