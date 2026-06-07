package com.project.pages;

import com.project.utils.WaitUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

/**
 * PatientPage — example domain page representing a healthcare module.
 * Demonstrates: dropdowns, dynamic tables, and form interactions.
 * Maps to the "Digital Health Platform" project on the resume.
 */
public class PatientPage {

    private static final Logger log = LogManager.getLogger(PatientPage.class);
    private WebDriver driver;

    @FindBy(id = "patient-name")
    private WebElement patientNameField;

    @FindBy(id = "patient-id")
    private WebElement patientIdField;

    @FindBy(id = "department-dropdown")
    private WebElement departmentDropdown;

    @FindBy(css = "#patient-table tbody tr")
    private java.util.List<WebElement> patientRows;

    @FindBy(css = "button#save-patient")
    private WebElement saveButton;

    public PatientPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void enterPatientName(String name) {
        WaitUtils.waitForVisible(driver, patientNameField);
        patientNameField.clear();
        patientNameField.sendKeys(name);
        log.info("Entered patient name: {}", name);
    }

    public void selectDepartment(String department) {
        Select select = new Select(departmentDropdown);
        select.selectByVisibleText(department);
        log.info("Selected department: {}", department);
    }

    public int getPatientTableRowCount() {
        return patientRows.size();
    }

    public void savePatient() {
        WaitUtils.waitForClickable(driver, saveButton);
        saveButton.click();
        log.info("Saved patient record");
    }
}
