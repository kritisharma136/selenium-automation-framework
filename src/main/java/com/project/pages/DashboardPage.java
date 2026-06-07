package com.project.pages;

import com.project.utils.WaitUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

/**
 * DashboardPage — Page Object for the secure area after login.
 * Demo site: https://the-internet.herokuapp.com/secure
 */
public class DashboardPage {

    private static final Logger log = LogManager.getLogger(DashboardPage.class);
    private WebDriver driver;

    @FindBy(css = "h2")
    private WebElement pageHeading;

    @FindBy(css = ".flash.success")
    private WebElement successBanner;

    @FindBy(css = "a[href='/logout']")
    private WebElement logoutLink;

    public DashboardPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public boolean isLoaded() {
        try {
            WaitUtils.waitForVisible(driver, pageHeading);
            boolean loaded = pageHeading.getText().contains("Secure Area");
            log.info("Dashboard loaded: {}", loaded);
            return loaded;
        } catch (Exception e) {
            log.error("Dashboard did not load", e);
            return false;
        }
    }

    public String getPageTitle() {
        return driver.getTitle();
    }

    public String getHeadingText() {
        WaitUtils.waitForVisible(driver, pageHeading);
        return pageHeading.getText();
    }

    public String getSuccessBannerText() {
        WaitUtils.waitForVisible(driver, successBanner);
        return successBanner.getText();
    }

    public void logout() {
        logoutLink.click();
        log.info("Clicked logout");
    }
}
