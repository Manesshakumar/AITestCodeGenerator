package com.example.tests;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.io.FileHandler;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

public class OrangeHRMLoginTest {

    private WebDriver driver;
    private static ExtentReports extent;
    private ExtentTest test;

    private final String BASE_URL = "https://opensource-demo.orangehrmlive.com/";
    private final String VALID_USERNAME = "Admin";
    private final String VALID_PASSWORD = "admin123";

    @BeforeSuite
    public void setupExtentReports() {
        // Define report path
        String reportPath = System.getProperty("user.dir") + "/reports/ExtentReport_" +
                            new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".html";
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);

        // Configure Spark Reporter
        sparkReporter.config().setDocumentTitle("OrangeHRM Login Test Automation Report");
        sparkReporter.config().setReportName("OrangeHRM Login Test Results");
        sparkReporter.config().setTheme(Theme.STANDARD);

        // Initialize ExtentReports
        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);

        // Add System information to reports
        extent.setSystemInfo("OS", System.getProperty("os.name"));
        extent.setSystemInfo("Browser", "Chrome");
        extent.setSystemInfo("Environment", "QA");
        extent.setSystemInfo("Tester", "Test Automation Engineer");
    }

    @BeforeClass
    public void setupDriver() {
        // Set up ChromeDriver path. Adjust this path if ChromeDriver is not in your system's PATH or project root.
        System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver"); // Or the path to your chromedriver executable

        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @BeforeMethod
    public void createExtentTest(java.lang.reflect.Method method) {
        test = extent.createTest(method.getName());
    }

    @Test(description = "Verify successful login with valid credentials")
    public void testSuccessfulLogin() {
        test.info("Navigating to OrangeHRM login page: " + BASE_URL);
        driver.get(BASE_URL);

        // Verify Page Title
        String expectedLoginPageTitle = "OrangeHRM";
        String actualLoginPageTitle = driver.getTitle();
        test.info("Verifying login page title. Expected: '" + expectedLoginPageTitle + "', Actual: '" + actualLoginPageTitle + "'");
        Assert.assertEquals(actualLoginPageTitle, expectedLoginPageTitle, "Login Page Title Mismatch!");
        test.pass("Successfully verified login page title: '" + actualLoginPageTitle + "'");

        // Enter valid username and password
        test.info("Locating username and password fields.");
        WebElement usernameField = driver.findElement(By.name("username"));
        WebElement passwordField = driver.findElement(By.name("password"));
        WebElement loginButton = driver.findElement(By.xpath("//button[@type='submit']"));

        test.info("Entering username: '" + VALID_USERNAME + "'");
        usernameField.sendKeys(VALID_USERNAME);
        test.info("Entering password: '*****'"); // Avoid logging sensitive data
        passwordField.sendKeys(VALID_PASSWORD);

        test.info("Clicking the Login button.");
        loginButton.click();

        // Verify successful login by checking dashboard visibility or Page Title
        String expectedDashboardPageTitleContains = "Dashboard";
        String actualDashboardPageTitle = driver.getTitle();
        test.info("Verifying successful login by checking dashboard page title. Expected to contain: '" + expectedDashboardPageTitleContains + "', Actual: '" + actualDashboardPageTitle + "'");
        Assert.assertTrue(actualDashboardPageTitle.contains(expectedDashboardPageTitleContains),
                "Dashboard Page Title Mismatch! Expected to contain '" + expectedDashboardPageTitleContains + "', but got '" + actualDashboardPageTitle + "'");
        test.pass("Successfully logged in. Dashboard page title contains: '" + actualDashboardPageTitle + "'");

        // Further verification: Check for a specific element on the dashboard
        test.info("Verifying visibility of 'Dashboard' header on the dashboard page.");
        WebElement dashboardHeader = driver.findElement(By.xpath("//h6[text()='Dashboard']"));
        Assert.assertTrue(dashboardHeader.isDisplayed(), "Dashboard header element is not displayed!");
        test.pass("Successfully verified the 'Dashboard' header is displayed.");
    }

    @AfterMethod
    public void tearDownExtentTest(ITestResult result) throws IOException {
        if (result.getStatus() == ITestResult.FAILURE) {
            test.fail("Test Failed: " + result.getThrowable());
            String screenshotPath = captureScreenshot(driver, result.getName());
            test.fail("Screenshot on failure:", MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath, "Failure Screenshot").build());
        } else if (result.getStatus() == ITestResult.SKIP) {
            test.skip("Test Skipped: " + result.getThrowable());
        } else {
            test.pass("Test Passed");
        }
    }

    @AfterClass
    public void tearDownDriver() {
        if (driver != null) {
            driver.quit();
        }
    }

    @AfterSuite
    public void flushExtentReports() {
        if (extent != null) {
            extent.flush();
        }
    }

    /**
     * Captures a screenshot and saves it to the reports/screenshots directory.
     *
     * @param driver     The WebDriver instance.
     * @param methodName The name of the test method that failed.
     * @return The absolute path to the saved screenshot file.
     * @throws IOException If an error occurs during file operations.
     */
    private String captureScreenshot(WebDriver driver, String methodName) throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        String screenshotName = methodName + "_" + timestamp + ".png";
        String screenshotDirectory = System.getProperty("user.dir") + "/reports/screenshots/";
        File targetFile = new File(screenshotDirectory + screenshotName);

        // Create the directory if it doesn't exist
        if (!new File(screenshotDirectory).exists()) {
            new File(screenshotDirectory).mkdirs();
        }

        FileHandler.copy(screenshotFile, targetFile);
        return targetFile.getAbsolutePath();
    }
}