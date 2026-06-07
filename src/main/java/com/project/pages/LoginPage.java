package com.project.pages;

import com.project.base.DriverFactory;
import com.project.utils.WaitUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

/**
 * LoginPage — Page Object Model for the login screen.
 * All locators and actions for this page live here.
 * Tests call methods, never touch locators directly.
 *
 * Demo site used: https://the-internet.herokuapp.com/login
 */
public class LoginPage {

    private static final Logger log = LogManager.getLogger(LoginPage.class);
    private WebDriver driver;

    // PageFactory @FindBy annotation — alternative to By.id()
    @FindBy(id = "username")
    private WebElement usernameField;

    @FindBy(id = "password")
    private WebElement passwordField;

    @FindBy(css = "button[type='submit']")
    private WebElement loginButton;

    @FindBy(css = ".flash.error")
    private WebElement errorMessage;

    @FindBy(css = ".flash.success")
    private WebElement successMessage;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this); // Initialises @FindBy fields
    }

    public void enterUsername(String username) {
        WaitUtils.waitForVisible(driver, usernameField);
        usernameField.clear();
        usernameField.sendKeys(username);
        log.info("Entered username: {}", username);
    }

    public void enterPassword(String password) {
        passwordField.clear();
        passwordField.sendKeys(password);
        log.info("Entered password");
    }

    public void clickLogin() {
        loginButton.click();
        log.info("Clicked login button");
    }

    // Fluent method — returns DashboardPage after successful login
    public DashboardPage loginAs(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
        return new DashboardPage(driver);
    }

    public String getErrorMessage() {
        WaitUtils.waitForVisible(driver, errorMessage);
        return errorMessage.getText();
    }

    public boolean isErrorDisplayed() {
        try {
            return errorMessage.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
