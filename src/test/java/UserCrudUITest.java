import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

public class UserCrudUITest {
    WebDriver driver;
    String baseUrl = "http://localhost:8080";

    String testUsername = "uiTestUser";
    String testPassword = "uiTestPass";
    String testEmail = "uiuser@example.com";

    String updatedUsername = "uiUpdatedUser";
    String updatedEmail = "uiupdated@example.com";

    Long createdUserId;

    @BeforeClass
    public void setup() {
        System.setProperty("webdriver.chrome.driver", "E:\\chromedriver-win64\\chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Test(priority = 1)
    public void testCreateUser() throws InterruptedException {
        driver.get(baseUrl + "/register.html");

        WebElement username = driver.findElement(By.id("username"));
        WebElement password = driver.findElement(By.id("password"));
        WebElement email = driver.findElement(By.id("email"));
        WebElement submit = driver.findElement(By.id("registerBtn"));

        username.sendKeys(testUsername);
        password.sendKeys(testPassword);
        email.sendKeys(testEmail);
        submit.click();

        // Wait for confirmation
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.visibilityOfElementLocated(By.id("successMsg")));


        WebElement successMessage = driver.findElement(By.id("successMsg"));
        Assert.assertTrue(successMessage.isDisplayed(), "Registration should be successful");
    }

    @Test(priority = 2)
    public void testReadAllUsers() throws InterruptedException {
        driver.get(baseUrl + "/user-list.html");

        Thread.sleep(2000); // Wait for table to load

        List<WebElement> rows = driver.findElements(By.cssSelector("table tr"));
        boolean found = false;

        for (WebElement row : rows) {
            if (row.getText().contains(testUsername)) {
                found = true;
                // Extract user ID (assuming it's in first column)
                createdUserId = Long.parseLong(row.findElement(By.cssSelector("td:nth-child(1)")).getText());
                break;
            }
        }

        Assert.assertTrue(found, "User should be listed in the user table");
    }

    @Test(priority = 3, dependsOnMethods = "testReadAllUsers")
    public void testUpdateUser() throws InterruptedException {
        driver.get(baseUrl + "/edit-user.html?id=" + createdUserId);

        Thread.sleep(1000);

        WebElement username = driver.findElement(By.id("username"));
        WebElement email = driver.findElement(By.id("email"));
        WebElement updateBtn = driver.findElement(By.id("updateBtn"));

        username.clear();
        username.sendKeys(updatedUsername);
        email.clear();
        email.sendKeys(updatedEmail);

        updateBtn.click();

        Thread.sleep(2000);
        WebElement successMsg = driver.findElement(By.id("updateSuccess"));
        Assert.assertTrue(successMsg.isDisplayed(), "Update should be successful");
    }

    @Test(priority = 4, dependsOnMethods = "testUpdateUser")
    public void testDeleteUser() throws InterruptedException {
        driver.get(baseUrl + "/delete-user.html?id=" + createdUserId);

        WebElement deleteBtn = driver.findElement(By.id("deleteBtn"));
        deleteBtn.click();

        Thread.sleep(2000);

        WebElement successMsg = driver.findElement(By.id("deleteSuccess"));
        Assert.assertTrue(successMsg.isDisplayed(), "User should be deleted");
    }

    @AfterClass
    public void tearDown() {
        if (driver != null)
            driver.quit();
    }
}
