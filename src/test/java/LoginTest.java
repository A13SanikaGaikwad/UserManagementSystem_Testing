import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.*;

import java.sql.*;

public class LoginTest {
    WebDriver driver;
    String dbUrl = "jdbc:mysql://localhost:3306/usermanagementdb";
    String dbUser = "root";
    String dbPassword = "Gaikwad@123";
    String usernameFromDB;
    String passwordFromDB;

    @BeforeClass
    public void setup() {
        System.setProperty("webdriver.chrome.driver", "E:\\chromedriver-win64\\chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();

        // Load user from DB
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT username, password FROM user WHERE id = 1")) {

            if (rs.next()) {
                usernameFromDB = rs.getString("username");
                passwordFromDB = rs.getString("password");
            } else {
                throw new RuntimeException("No user found in the database with id = 1");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testLoginPage() {
        driver.get("http://localhost:8080/login.html");

        WebElement username = driver.findElement(By.id("username"));
        WebElement password = driver.findElement(By.id("password"));
        WebElement loginBtn = driver.findElement(By.id("loginBtn"));

        username.clear();
        username.sendKeys(usernameFromDB);

        password.clear();
        password.sendKeys(passwordFromDB);

        loginBtn.click();

        // Add a short wait if needed to allow JS execution
        try {
            Thread.sleep(2000);  // Replace with WebDriverWait for production tests
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        WebElement welcomeMsg = driver.findElement(By.id("welcome"));
        Assert.assertTrue(welcomeMsg.isDisplayed(), "Welcome message should be visible.");
        Assert.assertTrue(welcomeMsg.getText().contains("Welcome"), "Welcome message should contain 'Welcome'.");
    }

    @AfterClass
    public void tearDown() {
        if (driver != null)
            driver.quit();
    }
}
