package com.laptop.ltn.laptop_store_server.selenium;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginTest {
    WebDriver driver;
    WebDriverWait wait;

    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
        driver.get("https://laptop-ltn105-store.vercel.app/login");
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        System.out.println("Before each test");
    }

    @Test
    void login_sucess() {
        // Tìm các field đăng nhập
        WebElement emailField = driver.findElement(By.id("email"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.cssSelector("#root > div > div > div.relative > div.flex.justify-center.items-center.my-16 > form > div.mt-4.mb-2 > button"));
        // Nhập thông tin đăng nhập
        emailField.sendKeys("letuannhat105@gmail.com");
        passwordField.sendKeys("123456");

        // Nhấn nút đăng nhập
        loginButton.click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement welcome = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("#root > div > div > div.bg-main > div > span:nth-child(2)")
        ));

        // Kiểm tra xem phần tử có hiển thị không và có chứa chữ "Thoát" không
        Assertions.assertTrue(welcome.getText().toLowerCase().contains("welcome"), "Đăng nhập thất bại");
    }

    @Test
    void login_failed_password() {
        // Tìm các field đăng nhập
        WebElement emailField = driver.findElement(By.id("email"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.cssSelector("#root > div > div > div.relative > div.flex.justify-center.items-center.my-16 > form > div.mt-4.mb-2 > button"));
        // Nhập thông tin đăng nhập
        emailField.sendKeys("letuannhat105@gmail.com");
        passwordField.sendKeys("1234567");

        // Nhấn nút đăng nhập
        loginButton.click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement swal2 = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("#swal2-html-container")
        ));
        // Kiểm tra xem phần tử có hiển thị không và có chứa chữ "Thoát" không
        Assertions.assertTrue(swal2.getText().equalsIgnoreCase("Email or password are not exist"));
    }

    @Test
    void login_failed_email() {
        // Tìm các field đăng nhập
        WebElement emailField = driver.findElement(By.id("email"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.cssSelector("#root > div > div > div.relative > div.flex.justify-center.items-center.my-16 > form > div.mt-4.mb-2 > button"));
        // Nhập thông tin đăng nhập
        emailField.sendKeys("letuannhat105x@gmail.com");
        passwordField.sendKeys("123456");
        // Nhấn nút đăng nhập
        loginButton.click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement swal2 = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("#swal2-html-container")
        ));

        // Kiểm tra xem phần tử có hiển thị không và có chứa chữ "Thoát" không
        Assertions.assertTrue(swal2.getText().equalsIgnoreCase("Email or password are not exist"));
    }

    @Test
    void login_email_and_password_null() {
        // Tìm các field đăng nhập
        WebElement emailField = driver.findElement(By.id("email"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.cssSelector("#root > div > div > div.relative > div.flex.justify-center.items-center.my-16 > form > div.mt-4.mb-2 > button"));
        // Nhập thông tin đăng nhập
        emailField.sendKeys("");
        passwordField.sendKeys("");

        // Nhấn nút đăng nhập
        loginButton.click();

        WebElement emailError = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("#root > div > div > div.relative > div.flex.justify-center.items-center.my-16 > form > div:nth-child(2) > small")
        ));
        WebElement passwordError = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("#root > div > div > div.relative > div.flex.justify-center.items-center.my-16 > form > div:nth-child(3) > small")
        ));

        // Kiểm tra xem phần tử có hiển thị không và có chứa chữ "Thoát" không
        Assertions.assertTrue(emailError.getText().equalsIgnoreCase("Email is required"));
        Assertions.assertTrue(passwordError.getText().equalsIgnoreCase("Password is required"));

    }

    @AfterEach
    void tearDown() {
        driver.quit();
        System.out.println("After all tests");
    }

}
