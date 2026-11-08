package SeleniumSess;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class TotalLinks {

	public static void main(String[] args) {
		
		
		WebDriver driver=  new ChromeDriver();
		driver.get("https://www.flipkart.com/");
		
		 List<WebElement> LinkList = driver.findElements(By.tagName("a"));
		
		 int linksCount = LinkList.size();
		 System.out.println("Total links is :" +linksCount);
		
		 for(int i=0; i<linksCount; i++) {
		String text = LinkList.get(i).getText();
		 System.out.println(text);
			
		if(text.length()>0) {
			System.out.println(i +":" +text);
			 }
		}
		 // For each loops-------
		
	      int count =0;
		 for(WebElement e : LinkList) {
			 String text = e.getText();
				if(text.length()>0) {
					System.out.println(count +":" +text);
			 
		 }
				count++;
		
		List<WebElement> LinkList1 = driver.findElements(By.tagName("img"));
		System.out.println(LinkList1.size());
	}

}
}
