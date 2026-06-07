package com.project.smoke;

import com.project.base.BaseTest;
import com.project.pages.DashboardPage;
import com.project.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * LoginSmokeTest — fast sanity check that login works.
 * Part of testng-smoke.xml — runs on every Jenkins build commit.
 *
 * Demo site: https://the-internet.herokuapp.com/login
 * Valid credentials: tomsmith / SuperSecretPassword!
 */
public class LoginSmokeTest extends BaseTest {

    @Test(description = "Verify successful login with valid credentials")
    public void testSuccessfulLogin() {
        log.info("Running smoke test: successful login");

        LoginPage loginPage = new LoginPage(getDriver());
        DashboardPage dashboard = loginPage.loginAs("tomsmith", "SuperSecretPassword!");

        Assert.assertTrue(dashboard.isLoaded(),
                "Dashboard should load after successful login");

        Assert.assertTrue(dashboard.getSuccessBannerText().contains("You logged into a secure area"),
                "Success banner message mismatch");

        log.info("Smoke test PASSED: login successful");
    }

    @Test(description = "Verify error message on invalid login")
    public void testInvalidLogin() {
        log.info("Running smoke test: invalid login");

        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.enterUsername("wronguser");
        loginPage.enterPassword("wrongpass");
        loginPage.clickLogin();

        Assert.assertTrue(loginPage.isErrorDisplayed(),
                "Error message should be displayed for invalid credentials");

        Assert.assertTrue(loginPage.getErrorMessage().contains("Your username is invalid"),
                "Error message text mismatch");

        log.info("Smoke test PASSED: error message shown for invalid credentials");
    }
}
