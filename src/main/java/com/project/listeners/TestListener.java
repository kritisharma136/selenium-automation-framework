package com.project.listeners;

import com.project.utils.ScreenshotUtils;
import com.project.utils.S3Uploader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * TestListener hooks into TestNG lifecycle events.
 * On failure: captures screenshot and uploads to S3.
 * Registered in testng.xml — no annotation needed in test classes.
 *
 * Interview Q: "How do you take screenshots on failure?"
 * Answer: "I implement ITestListener and override onTestFailure —
 * cast the driver to TakesScreenshot and save the file."
 */
public class TestListener implements ITestListener {

    private static final Logger log = LogManager.getLogger(TestListener.class);

    @Override
    public void onTestStart(ITestResult result) {
        log.info("=== TEST STARTED: {} ===", result.getName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        log.info("=== TEST PASSED: {} ===", result.getName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        log.error("=== TEST FAILED: {} ===", result.getName());
        log.error("Failure reason: {}", result.getThrowable().getMessage());

        // Step 1: capture screenshot locally
        String screenshotPath = ScreenshotUtils.capture(result.getName());

        // Step 2: upload to S3 (skipped gracefully if not configured)
        if (screenshotPath != null) {
            S3Uploader.upload(screenshotPath, "failures/" + result.getName() + ".png");
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        log.warn("=== TEST SKIPPED: {} ===", result.getName());
    }
}
