package TestNGClass;

import org.testng.annotations.Test;

public class PriorityTesT {
	
	@Test(priority=-1)
	public void A_Text() {
		System.out.println("A Test");
	}
	
	@Test(priority=2)
	public void ABC_Text() {
		System.out.println("ABC Test");
	}
	
	@Test(priority=0)
	public void a_Text() {
		System.out.println("a Test");
	}
	
	@Test
	public void ab_Text() {
		System.out.println("ab Test");
	}
	
	@Test
	public void c_Text() {
		System.out.println("c Test");
	}

}
