package CBT.NGWebDriverCBT;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.paulhammant.ngwebdriver.ByAngular;
import com.paulhammant.ngwebdriver.NgWebDriver;

public class ToDoAppTest {
	private RemoteWebDriver driver;
	private CBTHelper cbt;
	private NgWebDriver ngDriver;
	private String username = "chase%40crossbrowsertesting.com", authkey = "YOURAUTHKEY", score = "fail";
	private String hubString = "http://" + username + ":" + authkey + "@hub.crossbrowsertesting.com:80/wd/hub";
	private URL hubUrl = null;
	
	@Before
	public void setup() {
		boolean useSecureTunnel = true;
		DesiredCapabilities caps = new DesiredCapabilities();
        cbt = new CBTHelper(username, authkey, useSecureTunnel);
        caps.setCapability("name", "Todo App Example");
        caps.setCapability("build", "1.0");
        caps.setCapability("browserName", "Chrome");
        caps.setCapability("version", "60");
        caps.setCapability("platform", "Windows 10");
        caps.setCapability("screenResolution", "1366x768");
        caps.setCapability("record_video", "true");

        try {
        	hubUrl = new URL(hubString);
        } catch (MalformedURLException ex) {
        	System.out.println(ex.getStackTrace());
        }
        
        driver = new RemoteWebDriver(hubUrl, caps);
        cbt.setSessionId(driver.getSessionId().toString());
        ngDriver = new NgWebDriver(driver);
	}
	
	@Test
	public void TestTodo() {
		driver.get("http://crossbrowsertesting.github.io/todo-app.html");
		driver.manage().timeouts().setScriptTimeout(10, TimeUnit.SECONDS);
		List<WebElement> todos = driver.findElements(ByAngular.model("todo.done"));
		
		System.out.println("Clicking Todo's");
		todos.get(3).click();
		todos.get(4).click();
		
		System.out.println("Entering Text");
		WebElement textbox = driver.findElement(ByAngular.model("todoList.todoText"));
		textbox.sendKeys("Run your first Selenium test");
		
		System.out.println("Adding new todo's");
		driver.findElement(By.id("addbutton")).click();
		
		System.out.println("Archiving old todo's");
		driver.findElement(By.linkText("archive")).click();
		
		System.out.println("Grabbing snapshot of finished result");
		cbt.takeSnapshot();
		score = "pass";
	}
	
	@After
	public void AfterEach() {
		if (score.equals("fail")) 
			cbt.setDescription("Test failed!");
		
		cbt.setScore(score);
		if (driver != null) 
			driver.quit();
	}
}
