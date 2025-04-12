package com.laptop.ltn.laptop_store_server.selenium;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class DemoSele {
    WebDriver driver;

    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
        driver.get("https://nhathuocviet.vn/dang-nhap.html");
        System.out.println("Before each test");
    }

    @Test
    void login_sucess() {
        // Tìm các field đăng nhập
        WebElement emailField = driver.findElement(By.id("vnCusLogin1_txtUserEmail"));
        WebElement passwordField = driver.findElement(By.id("vnCusLogin1_txtPassword"));
        WebElement loginButton = driver.findElement(By.id("vnCusLogin1_btnLogin"));
        // Nhập thông tin đăng nhập
        emailField.sendKeys("letuannhat105@gmail.com");
        passwordField.sendKeys("123456");

        // Nhấn nút đăng nhập
        loginButton.click();

        WebElement logoutElement = driver.findElement(By.cssSelector("#vnTopHeader1_tblLoginAC > ul > li:nth-child(2) > a"));

        // Kiểm tra xem phần tử có hiển thị không và có chứa chữ "Thoát" không
        Assertions.assertTrue(logoutElement.getText().equalsIgnoreCase("thoát") , "Đăng nhập thất bại");
    }
    @Test
    void login_failed_wrong_password() {
        // Tìm các field đăng nhập
        WebElement emailField = driver.findElement(By.id("vnCusLogin1_txtUserEmail"));
        WebElement passwordField = driver.findElement(By.id("vnCusLogin1_txtPassword"));
        WebElement loginButton = driver.findElement(By.id("vnCusLogin1_btnLogin"));

        // Nhập thông tin đăng nhập sai
        emailField.sendKeys("letuannhat105@gmail.com");
        passwordField.sendKeys("wrongpassword"); // Sai mật khẩu

        // Nhấn nút đăng nhập
        loginButton.click();

        // Chờ và xử lý alert khi sai mật khẩu
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent()); // Chờ alert xuất hiện
            String alertText = alert.getText(); // Lấy nội dung alert
            Assertions.assertTrue(alertText.contains("Email hoặc mật khẩu nhập không đúng"), "Thông báo lỗi không đúng");

            alert.accept(); // Bấm "OK" để đóng alert
        } catch (TimeoutException e) {
            Assertions.fail("Không xuất hiện alert khi nhập sai mật khẩu");
        }

        // Kiểm tra nút "Thoát" không xuất hiện (đảm bảo chưa đăng nhập)
        Assertions.assertThrows(NoSuchElementException.class, () -> {
            driver.findElement(By.cssSelector("#vnTopHeader1_tblLoginAC > ul > li:nth-child(2) > a"));
        }, "Không nên thấy nút 'Thoát' khi nhập sai mật khẩu");
    }
    @Test
    void login_failed_wrong_email() {
        // Tìm các field đăng nhập
        WebElement emailField = driver.findElement(By.id("vnCusLogin1_txtUserEmail"));
        WebElement passwordField = driver.findElement(By.id("vnCusLogin1_txtPassword"));
        WebElement loginButton = driver.findElement(By.id("vnCusLogin1_btnLogin"));

        // Nhập thông tin đăng nhập sai
        emailField.sendKeys("letuannhat105aa@gmail.com");
        passwordField.sendKeys("123456"); // Sai mật khẩu

        // Nhấn nút đăng nhập
        loginButton.click();

        // Chờ và xử lý alert khi sai mật khẩu
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent()); // Chờ alert xuất hiện
            String alertText = alert.getText(); // Lấy nội dung alert
            Assertions.assertTrue(alertText.contains("Email hoặc mật khẩu nhập không đúng"), "Thông báo lỗi không đúng");

            alert.accept(); // Bấm "OK" để đóng alert
        } catch (TimeoutException e) {
            Assertions.fail("Không xuất hiện alert khi nhập sai mật khẩu");
        }

        // Kiểm tra nút "Thoát" không xuất hiện (đảm bảo chưa đăng nhập)
        Assertions.assertThrows(NoSuchElementException.class, () -> {
            driver.findElement(By.cssSelector("#vnTopHeader1_tblLoginAC > ul > li:nth-child(2) > a"));
        }, "Không nên thấy nút 'Thoát' khi nhập sai mật khẩu");
    }
    @AfterEach
    void tearDown() {
        driver.quit();
        System.out.println("After all tests");
    }
}
