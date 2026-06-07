package com.project.base;

import com.project.utils.ConfigReader;
import com.project.utils.ScreenshotUtils;
import com.project.utils.S3Uploader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.time.Duration;

/**
 * BaseTest is extended by every test class.
 * Handles: driver setup, teardown, and screenshot on failure.
 */
public class BaseTest {

    protected static final Logger log = LogManager.getLogger(BaseTest.class);

    @BeforeMethod
    public void setUp() {
        String browser = ConfigReader.get("browser");
        String baseUrl  = ConfigReader.get("base.url");

        log.info("Starting browser: {}", browser);
        DriverFactory.initDriver(browser);

        WebDriver driver = DriverFactory.getDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();
        driver.get(baseUrl);
        log.info("Navigated to: {}", baseUrl);
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            log.error("Test FAILED: {}", result.getName());

            // Capture screenshot on failure
            String screenshotPath = ScreenshotUtils.capture(result.getName());

            // Upload screenshot to AWS S3 (simulated when AWS not configured)
            S3Uploader.upload(screenshotPath, "screenshots/" + result.getName() + ".png");
        }

        log.info("Closing browser after: {}", result.getName());
        DriverFactory.quitDriver();
    }

    // Convenience method for subclasses
    protected WebDriver getDriver() {
        return DriverFactory.getDriver();
    }
}
