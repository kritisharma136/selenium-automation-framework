package com.project.utils;

import com.project.base.DriverFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ScreenshotUtils captures a screenshot and saves it to reports/screenshots/.
 * Called by TestListener on test failure. The path is then passed to S3Uploader.
 */
public class ScreenshotUtils {

    private static final Logger log = LogManager.getLogger(ScreenshotUtils.class);
    private static final String SCREENSHOT_DIR = "reports/screenshots/";

    /**
     * Captures a screenshot for the current test and saves it locally.
     * @param testName name of the failed test
     * @return full path to the saved screenshot file
     */
    public static String capture(String testName) {
        WebDriver driver = DriverFactory.getDriver();

        if (driver == null) {
            log.warn("Driver is null — cannot capture screenshot");
            return null;
        }

        try {
            // Create directory if not exists
            Files.createDirectories(Paths.get(SCREENSHOT_DIR));

            // Generate timestamped filename
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = testName + "_" + timestamp + ".png";
            String fullPath = SCREENSHOT_DIR + fileName;

            // Cast driver to TakesScreenshot and save
            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(srcFile.toPath(), Paths.get(fullPath));

            log.info("Screenshot saved: {}", fullPath);
            return fullPath;

        } catch (IOException e) {
            log.error("Failed to capture screenshot for: {}", testName, e);
            return null;
        }
    }
}
