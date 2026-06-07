package com.project.regression;

import com.project.base.BaseTest;
import com.project.listeners.RetryAnalyzer;
import com.project.pages.DashboardPage;
import com.project.pages.LoginPage;
import com.project.utils.ExcelUtils;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * LoginRegressionTest — full regression suite for login module.
 * Part of testng-regression.xml — runs nightly on Jenkins.
 * Demonstrates: data-driven testing with @DataProvider + ExcelUtils.
 */
public class LoginRegressionTest extends BaseTest {

    /**
     * @DataProvider feeds multiple credential sets to the test below.
     * In production, data comes from Excel — here we use inline for simplicity.
     * Replace with ExcelUtils.readData(...) for real data-driven runs.
     */
    @DataProvider(name = "loginData")
    public Object[][] loginDataProvider() {
        // Format: {username, password, expectedResult}
        return new Object[][] {
            {"tomsmith",  "SuperSecretPassword!", "success"},
            {"wronguser", "wrongpass",            "failure"},
            {"tomsmith",  "wrongpass",            "failure"},
            {"",          "",                     "failure"}
        };
    }

    @Test(dataProvider = "loginData",
          description = "Data-driven login test with multiple credential sets")
    public void testLoginWithMultipleCredentials(String username, String password, String expected) {
        log.info("Testing login — user: {}, expected: {}", username, expected);

        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.enterUsername(username);
        loginPage.enterPassword(password);
        loginPage.clickLogin();

        if ("success".equals(expected)) {
            DashboardPage dashboard = new DashboardPage(getDriver());
            Assert.assertTrue(dashboard.isLoaded(),
                    "Dashboard should load for valid credentials");
        } else {
            Assert.assertTrue(loginPage.isErrorDisplayed(),
                    "Error message should appear for invalid credentials: " + username);
        }
    }

    @Test(description = "Verify logout redirects back to login page",
          retryAnalyzer = RetryAnalyzer.class)
    public void testLogoutFlow() {
        log.info("Testing logout flow");

        LoginPage loginPage = new LoginPage(getDriver());
        DashboardPage dashboard = loginPage.loginAs("tomsmith", "SuperSecretPassword!");

        Assert.assertTrue(dashboard.isLoaded(), "Must be on dashboard before logout");

        dashboard.logout();

        // After logout, URL should return to login page
        Assert.assertTrue(getDriver().getCurrentUrl().contains("login"),
                "Should redirect to login page after logout");
    }

    @Test(description = "Verify page title on login page")
    public void testLoginPageTitle() {
        Assert.assertEquals(getDriver().getTitle(), "The Internet",
                "Login page title mismatch");
    }
}
