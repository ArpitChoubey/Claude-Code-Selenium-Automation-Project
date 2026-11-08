package SeleniumSess;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class GoogleLang {

	public static void main(String[] args) {
		
		WebDriver driver=  new ChromeDriver();
		driver.get("https://www.google.co.in/");
		
		List <WebElement> LangLink = driver.findElements(By.xpath("//div[@id=\"SIvCob\"]/a"));
		
		System.out.println(LangLink.size());
        
        for(WebElement e : LangLink) {
       	String Text = e.getText();
       	System.out.println(Text);
       	if(Text.equals("ਪੰਜਾਬੀ")) {
       		e.click();
       		break;
       	}
        }
		
		

	}

}
