# Getting Started with NGWebDriver and CBT

## Connecting to CrossBrowserTesting's Hub

When working with CBT's cloud environment, you'll need to ensure you're using a RemoteWebDriver object pointed at our hub. Make sure to use your CBT username and authorization key which can be found from the [manage account](https://app.crossbrowsertesting.com/account) section of our app. You'll also need to use a DesiredCapabilities object containg the relevant values for the browser you'd like to test. You can find those capabilities in [our API](https://crossbrowsertesting.com/apidocs/v3/selenium.html#!/default/get_selenium_browsers), as well as in the configurator on [our Selenium dashboard](https://app.crossbrowsertesting.com/selenium/run). 

```

DesiredCapabilities caps = new DesiredCapabilities();
caps.setCapability("name", "Todo App Example");
caps.setCapability("build", "1.0");
caps.setCapability("browserName", "Chrome");
caps.setCapability("version", "60");
caps.setCapability("platform", "Windows 10");
caps.setCapability("screenResolution", "1366x768");
caps.setCapability("record_video", "true");

try {
    hubUrl = new URL("http://" + username + ":" + authkey + "@hub.crossbrowsertesting.com:80/wd/hub");
} catch (MalformedURLException ex) {
    System.out.println(ex.getStackTrace());
}

driver = new RemoteWebDriver(hubUrl, caps);
```

## Executing Angular Tests

The great thing about NGWebDriver is its usage of the underlying logic designed by the Protractor team. This means you can make use of Angular attributes to test your application right alongside Selenium WebDriver:

```

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

```

## Using the Local Connection Feature

If you're using the example in this repo, you should have access to CBTHelper which includes methods for using [cbt-tunnels](https://github.com/crossbrowsertesting/cbt-tunnel-nodejs/releases). Just set useSecureTunnel to true when creating the CBTHelper object, and ensure that cbt-tunnels can be found in a file called SecureTunnel in the root directory of your project. 

## Using CBT's API

CBTHelper also comes with some useful functions from CBT's API. For instance, at different points throughout your test, you can use CBTHelper to take screenshots of your page:

```
cbt.takeSnapshot();
```

Set the score at the end of your test:

```
cbt.setScore("pass");
cbt.setScore("fail");
```

Or even set the description of your test dynamically so you can search for it later:

```
cbt.setDescription("Failed login test");
```

## Support

Getting started is easy with CBT, but if at any point you have trouble, don't hesitate to [reach out to us](mailto:support@crossbrowsertesting.com). We're always happy to help!