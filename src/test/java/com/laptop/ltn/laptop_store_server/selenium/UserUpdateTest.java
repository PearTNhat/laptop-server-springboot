package com.laptop.ltn.laptop_store_server.selenium;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class UserUpdateTest {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        System.out.println("Before each test");
    }

    private void login() {
        driver.get("https://laptop-ltn105-store.vercel.app/login");

        WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
        emailInput.sendKeys("batho09082003@gmail.com");

        WebElement passwordInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("password")));
        passwordInput.sendKeys("123456");
        passwordInput.sendKeys(Keys.RETURN);

        wait.until(ExpectedConditions.urlToBe("https://laptop-ltn105-store.vercel.app/"));
        String currentUrl = driver.getCurrentUrl();
        Assertions.assertTrue(
                currentUrl.equals("https://laptop-ltn105-store.vercel.app/") || currentUrl.equals("https://laptop-ltn105-store.vercel.app"),
                "URL sau khi đăng nhập không đúng: " + currentUrl
        );
    }

    @Test
    void testUpdateUserInfo() throws InterruptedException {
        login();

        driver.get("https://laptop-ltn105-store.vercel.app/user/profile");
        Thread.sleep(3000);

        driver.navigate().refresh();

        WebElement firstNameField = driver.findElement(By.id("firstName"));
        WebElement lastNameField = driver.findElement(By.id("lastName"));
        WebElement phoneField = driver.findElement(By.id("phone"));
        WebElement emailField = driver.findElement(By.id("email"));
        WebElement addressField = driver.findElement(By.id("address"));
        WebElement saveButton = driver.findElement(By.xpath("/html/body/div[1]/div/div/div[2]/div/div/div[2]/form/div[5]/button"));

        firstNameField.clear();
        firstNameField.sendKeys("Tran");

        lastNameField.clear();
        lastNameField.sendKeys("Tho");

        emailField.clear();
        emailField.sendKeys("batho09082003@gmail.com");

        phoneField.clear();
        phoneField.sendKeys("0365365365");

        addressField.clear();
        addressField.sendKeys("GiaLai, Vietnam");

        saveButton.click();

        WebElement updateFirstName = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/div/div[2]/div/div/div[2]/form/div[2]/div[1]/input")
        ));
        assertTrue(updateFirstName.isDisplayed(), "Họ người dùng chưa được cập nhật!");

        WebElement updateLastName = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/div/div[2]/div/div/div[2]/form/div[2]/div[2]/input")
        ));
        assertTrue(updateLastName.isDisplayed(), "Tên người dùng chưa được cập nhật!");

        WebElement updatePhone = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/div/div[2]/div/div/div[2]/form/div[3]/div[2]/input")
        ));
        assertTrue(updatePhone.isDisplayed(), "Tên người dùng chưa được cập nhật!");

        WebElement updatedAddress = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/div/div[2]/div/div/div[2]/form/div[4]/input")
        ));
        assertTrue(updatedAddress.isDisplayed(), "Địa chỉ chưa được cập nhật!");
    }

    @Test
    void testUpdateUserInfoWithEmptyFields() throws InterruptedException {
        login();

        driver.get("https://laptop-ltn105-store.vercel.app/user/profile");
        Thread.sleep(3000);

        driver.navigate().refresh();

        WebElement firstNameField = driver.findElement(By.id("firstName"));
        firstNameField.sendKeys(" ");
        firstNameField.clear();

        WebElement lastNameField = driver.findElement(By.id("lastName"));
        lastNameField.clear();
        lastNameField.sendKeys("");

        WebElement phoneField = driver.findElement(By.id("phone"));
        phoneField.clear();
        phoneField.sendKeys("");

        WebElement emailField = driver.findElement(By.id("email"));
        emailField.clear();
        emailField.sendKeys("batho09082003@gmail.com");

        WebElement addressField = driver.findElement(By.id("address"));
        addressField.clear();
        addressField.sendKeys("");

        WebElement saveButton = driver.findElement(
                By.xpath("/html/body/div[1]/div/div/div[2]/div/div/div[2]/form/div[5]/button"));
        saveButton.click();

        WebElement firstNameError = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div/div/div/div[2]/div/div/div[2]/form/div[2]/div[1]/div/small")
        ));
    }

    @AfterEach
    void tearDown() {
        driver.quit();
        System.out.println("After all tests");
    }
}
